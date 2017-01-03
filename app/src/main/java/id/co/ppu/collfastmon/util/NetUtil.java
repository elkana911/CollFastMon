package id.co.ppu.collfastmon.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import id.co.ppu.collfastmon.listener.OnGetChatContactListener;
import id.co.ppu.collfastmon.listener.OnSuccessError;
import id.co.ppu.collfastmon.pojo.UserData;
import id.co.ppu.collfastmon.pojo.chat.TrnChatContact;
import id.co.ppu.collfastmon.pojo.chat.TrnChatMsg;
import id.co.ppu.collfastmon.pojo.trn.TrnCollPos;
import id.co.ppu.collfastmon.pojo.trn.TrnErrorLog;
import id.co.ppu.collfastmon.rest.ApiInterface;
import id.co.ppu.collfastmon.rest.ServiceGenerator;
import id.co.ppu.collfastmon.rest.request.RequestLogError;
import id.co.ppu.collfastmon.rest.request.RequestSyncLocation;
import id.co.ppu.collfastmon.rest.request.chat.RequestChatContacts;
import id.co.ppu.collfastmon.rest.request.chat.RequestChatMsg;
import id.co.ppu.collfastmon.rest.request.chat.RequestChatStatus;
import id.co.ppu.collfastmon.rest.response.chat.ResponseGetOnlineContacts;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Eric on 30-Aug-16.
 */
public class NetUtil {
    public static boolean isConnected(Context ctx) {
        ConnectivityManager connec = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        return (connec.getActiveNetworkInfo() != null)
                && (connec.getActiveNetworkInfo().isAvailable())
                && (connec.getActiveNetworkInfo().isConnected());
    }

    /**
     * KOnsepnya kalo ada koneksi langsung kirim, kalo tidak ada, simpan dulu di lokal.
     * Kalo sukses terkirim clear out data
     * @param ctx
     * @param realm
     * @param collectorId
     * @param moduleName
     * @param message1
     * @param message2
     */
    public static void syncLogError(final Context ctx, final Realm realm, final String collectorId, final String moduleName, final String message1, final String message2) {

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                TrnErrorLog trnErrorLog = new TrnErrorLog();
                trnErrorLog.setUid(java.util.UUID.randomUUID().toString());
                trnErrorLog.setCollectorId(collectorId);
                trnErrorLog.setCreatedTimestamp(new Date());
                trnErrorLog.setModule(moduleName);
                trnErrorLog.setMessage1(message1);
                trnErrorLog.setMessage2(message2);
                realm.copyToRealm(trnErrorLog);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                if (isConnected(ctx)) {

                    ApiInterface fastService =
                            ServiceGenerator.createService(ApiInterface.class, Utility.buildUrl(Storage.getPreferenceAsInt(ctx, Storage.KEY_SERVER_ID, 0)));
                    RequestLogError req = new RequestLogError();

                    RealmResults<TrnErrorLog> trnErrorLogs = realm.where(TrnErrorLog.class)
                            .equalTo("collectorId", collectorId)
                            .findAll();

                    req.setLogs(realm.copyFromRealm(trnErrorLogs));

                    Call<ResponseBody> call = fastService.logError(req);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            Realm r = Realm.getDefaultInstance();
                            r.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    boolean b = realm.where(TrnErrorLog.class)
                                            .equalTo("collectorId", collectorId)
                                            .findAll().deleteAllFromRealm();

                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });

                } else {
                }

            }
        });

    }

    /**
     * Make sure this method is run in background or asynctask
     * @param ctx
     */
    public static void syncLocation(final Context ctx, final double[] gps, boolean offline) {
        Realm realm = Realm.getDefaultInstance();
        try {
//            final double[] gps = Location.getGPS(ctx);

            Log.i("eric.gps", "lat=" + String.valueOf(gps[0]) + ",lng=" + String.valueOf(gps[1]));
//            final Date twoDaysAgo = Utility.getTwoDaysAgo(new Date());

            final UserData userData = (UserData) Storage.getObjPreference(ctx, Storage.KEY_USER, UserData.class);

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    long total = realm.where(TrnCollPos.class).count();

                    RealmResults<TrnCollPos> all = realm.where(TrnCollPos.class).findAll();

//                        long totalTwoDaysAgo = realm.where(TrnCollPos.class).lessThanOrEqualTo("lastUpdate", twoDaysAgo).count();


                    TrnCollPos trnCollPos = new TrnCollPos();

                    trnCollPos.setUid(java.util.UUID.randomUUID().toString());

                    trnCollPos.setCollectorId(userData.getUserId());
                    trnCollPos.setLatitude(String.valueOf(gps[0]));
                    trnCollPos.setLongitude(String.valueOf(gps[1]));
                    trnCollPos.setLastupdateTimestamp(new Date());
                    realm.copyToRealmOrUpdate(trnCollPos);

                }
            });

            if (offline)
                return;

            if (!isConnected(ctx)) {
                return;
            }

            ApiInterface fastService =
                    ServiceGenerator.createService(ApiInterface.class, Utility.buildUrl(Storage.getPreferenceAsInt(ctx, Storage.KEY_SERVER_ID, 0)));

            RequestSyncLocation req = new RequestSyncLocation();

            RealmResults<TrnCollPos> trnCollPoses = realm.where(TrnCollPos.class)
                    .equalTo("collectorId", userData.getUserId())
                    .findAll();

            req.setList(realm.copyFromRealm(trnCollPoses));

            Call<ResponseBody> call = fastService.syncLocation(req);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Realm r = Realm.getDefaultInstance();
                        r.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                // delete local data
                                boolean b = realm.where(TrnCollPos.class)
                                        .equalTo("collectorId", userData.getUserId())
                                        .findAll().deleteAllFromRealm();

                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            realm.close();
        }

    }

    public static void chatLogOn(Context ctx, String collCode, final OnSuccessError listener) {
        // send to server that current contact is online
        if (!isConnected(ctx)) {
            return;
        }

        String androidId = Storage.getAndroidToken(ctx);
        String userStatus = ConstChat.FLAG_ONLINE;
        String userMsg = "Available";

        RequestChatStatus req = new RequestChatStatus();
        req.setCollCode(collCode);
        req.setStatus(userStatus);
        req.setMessage(userMsg);
        req.setAndroidId(androidId);

        Call<ResponseBody> call = Storage.getAPIService(ctx).sendStatus(req);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // update display status here
                if (!response.isSuccessful()) {

                    ResponseBody errorBody = response.errorBody();

                    try {
                        if (listener != null) {
                            listener.onFailure(new RuntimeException(errorBody.string()));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return;
                }

                if (listener != null)
                    listener.onSuccess(null);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (listener != null)
                    listener.onFailure(t);
            }
        });

    }

    public static void chatLogOff(Context ctx, String collCode, final OnSuccessError listener) {
        if (!isConnected(ctx)) {
            return;
        }

        String androidId = Storage.getAndroidToken(ctx);
        String userStatus = ConstChat.FLAG_OFFLINE;
        String userMsg = "Offline";

        RequestChatStatus req = new RequestChatStatus();
        req.setCollCode(collCode);
        req.setStatus(userStatus);
        req.setMessage(userMsg);
        req.setAndroidId(androidId);

        Call<ResponseBody> call = Storage.getAPIService(ctx).sendStatus(req);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // update display status here
                if (!response.isSuccessful()) {

                    ResponseBody errorBody = response.errorBody();

                    try {
                        if (listener != null) {
                            listener.onFailure(new RuntimeException(errorBody.string()));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return;
                }

                Realm r = Realm.getDefaultInstance();
                try {
                    r.beginTransaction();
                    r.delete(TrnChatContact.class);
//                    r.delete(TrnChatMsg.class);
                    r.commitTransaction();
                } finally {
                    if (r != null)
                        r.close();
                }

                if (listener != null)
                    listener.onSuccess(null);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (listener != null)
                    listener.onFailure(t);
            }
        });

    }

    public static void chatUpdateContacts(Context ctx, String collCode, final OnGetChatContactListener listener) {

        if (!isConnected(ctx)) {
            return;
        }

        RequestChatStatus req = new RequestChatStatus();
        req.setCollCode(collCode);
        req.setStatus(null);
        req.setMessage(null);

        Call<ResponseGetOnlineContacts> call = Storage.getAPIService(ctx).getOnlineContacts(req);
        call.enqueue(new Callback<ResponseGetOnlineContacts>() {
            @Override
            public void onResponse(Call<ResponseGetOnlineContacts> call, Response<ResponseGetOnlineContacts> response) {

                if (!response.isSuccessful()) {
                    int statusCode = response.code();

                    // handle request errors yourself
                    ResponseBody errorBody = response.errorBody();

                    if (listener != null) {
                        listener.onFailure(null);
                    }

                    return;
                }

                final ResponseGetOnlineContacts resp = response.body();

                if (resp == null || resp.getData() == null) {
//                    Utility.showDialog(MainChatActivity.this, "No Contacts found", "You have empty List.\nPlease try again.");
                    return;
                }

                if (resp.getError() != null) {
                    if (listener != null) {
                        String msg = "Error (" + resp.getError().getErrorCode() + ")\n" + resp.getError().getErrorDesc();
                        listener.onFailure(new RuntimeException(msg));
                    }

                    return;
                }

                /*
                dangerous coud
                Realm r = Realm.getDefaultInstance();

                r.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        // harusnya ga perlu delete, tp mungkin saat ini biar gampang begini aja
                        realm.delete(TrnChatContact.class);

                        realm.copyToRealm(resp.getData());
                    }
                });

                final int size = resp.getData().size();

                r.close();
                 */

                if (listener != null) {
                    listener.onSuccess(resp.getData());
                }
            }

            @Override
            public void onFailure(Call<ResponseGetOnlineContacts> call, Throwable t) {
                if (listener != null)
                    listener.onFailure(t);

            }
        });

    }

    public static void chatGetGroupContacts(Context ctx, String collCode, final OnGetChatContactListener listener) {

        if (!isConnected(ctx)) {
            return;
        }

        RequestChatStatus req = new RequestChatStatus();
        req.setCollCode(collCode);
        req.setStatus(null);
        req.setMessage(null);

        Call<ResponseGetOnlineContacts> call = Storage.getAPIService(ctx).getGroupContacts(req);
        call.enqueue(new Callback<ResponseGetOnlineContacts>() {
            @Override
            public void onResponse(Call<ResponseGetOnlineContacts> call, Response<ResponseGetOnlineContacts> response) {

                if (!response.isSuccessful()) {
                    int statusCode = response.code();

                    // handle request errors yourself
                    ResponseBody errorBody = response.errorBody();

                    if (listener != null) {
                        listener.onFailure(null);
                    }

                    return;
                }

                final ResponseGetOnlineContacts resp = response.body();

                if (resp == null || resp.getData() == null) {
//                    Utility.showDialog(MainChatActivity.this, "No Contacts found", "You have empty List.\nPlease try again.");
                    return;
                }

                if (resp.getError() != null) {
                    if (listener != null) {
                        String msg = "Error (" + resp.getError().getErrorCode() + ")\n" + resp.getError().getErrorDesc();
                        listener.onFailure(new RuntimeException(msg));
                    }

                    return;
                }
/*
dangerous code
                Realm r = Realm.getDefaultInstance();

                r.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.delete(TrnChatContact.class);

                        realm.copyToRealmOrUpdate(resp.getData());
                    }
                });

                final int size = resp.getData().size();

                r.close();
 */

                if (listener != null) {

                    listener.onSuccess(resp.getData());
                }
            }

            @Override
            public void onFailure(Call<ResponseGetOnlineContacts> call, Throwable t) {
                if (listener != null)
                    listener.onFailure(t);

            }
        });

    }

    public static void chatGetContacts(Context ctx, final List<String> collectorsCode, final OnGetChatContactListener listener) {

        if (!isConnected(ctx)) {
            return;
        }

        RequestChatContacts req = new RequestChatContacts();
        req.setCollsCode(collectorsCode);

        Call<ResponseGetOnlineContacts> call = Storage.getAPIService(ctx).getChatContacts(req);
        call.enqueue(new Callback<ResponseGetOnlineContacts>() {
            @Override
            public void onResponse(Call<ResponseGetOnlineContacts> call, Response<ResponseGetOnlineContacts> response) {

                if (!response.isSuccessful()) {
                    int statusCode = response.code();

                    // handle request errors yourself
                    ResponseBody errorBody = response.errorBody();

                    if (listener != null) {
                        listener.onFailure(null);
                    }

                    return;
                }

                final ResponseGetOnlineContacts resp = response.body();

                if (resp == null || resp.getData() == null) {
//                    Utility.showDialog(MainChatActivity.this, "No Contacts found", "You have empty List.\nPlease try again.");
                    return;
                }

                if (resp.getError() != null) {
                    if (listener != null) {
                        String msg = "Error (" + resp.getError().getErrorCode() + ")\n" + resp.getError().getErrorDesc();
                        listener.onFailure(new RuntimeException(msg));
                    }

                    return;
                }
/*
                Realm r = Realm.getDefaultInstance();

                r.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmResults<TrnChatContact> all = realm.where(TrnChatContact.class)
                                .in("collCode", (String[]) collectorsCode.toArray((new String[collectorsCode.size()])))
                                .findAll();

                        all.deleteAllFromRealm();

                        realm.copyToRealmOrUpdate(resp.getData());
                    }
                });

                final int size = resp.getData().size();

                r.close();
*/
                if (listener != null) {
                    listener.onSuccess(resp.getData());
                }
            }

            @Override
            public void onFailure(Call<ResponseGetOnlineContacts> call, Throwable t) {
                if (listener != null)
                    listener.onFailure(t);

            }
        });

    }

    public static void chatSendQueueMessage(final Context ctx) {
        if (Utility.isScreenOff(ctx) || !NetUtil.isConnected(ctx))
            return;

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // Do something here on the main thread
                final Realm r = Realm.getDefaultInstance();
                try{

                    // 1. check transmitting data
                    final RealmResults<TrnChatMsg> anyTransmittingData = r.where(TrnChatMsg.class)
                            .equalTo("messageStatus", ConstChat.MESSAGE_STATUS_TRANSMITTING)
                            .findAll();

                    if (anyTransmittingData.size() > 0) {
//                Log.d(TAG, "There are " + anyTransmittingData.size() + " chats transmitting");
                        // wait until all chats sent to server
                        // if 5 minutes expired will be reset to unopened or firsttime
                        r.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                for (TrnChatMsg _obj : anyTransmittingData) {
                                    long minutesAge = Utility.getMinutesDiff(_obj.getCreatedTimestamp(), new Date());

                                    if (minutesAge > 5) {
                                        _obj.setMessageStatus(ConstChat.MESSAGE_STATUS_UNOPENED_OR_FIRSTTIME);
                                    }
                                }
                                // no need to do this
//                            realm.copyToRealmOrUpdate(anyTransmittingData);
                            }
                        });
                        return;
                    }

                    // 2. check pending message
                    final RealmResults<TrnChatMsg> pendings = r.where(TrnChatMsg.class)
                            .equalTo("messageStatus", ConstChat.MESSAGE_STATUS_UNOPENED_OR_FIRSTTIME)
                            .findAll();

                    if (pendings.size() > 0) {
                        r.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                for (TrnChatMsg _obj : pendings) {
                                    _obj.setMessageStatus(ConstChat.MESSAGE_STATUS_TRANSMITTING);
                                }
                            }
                        });

                        final RequestChatMsg req = new RequestChatMsg();
                        req.setMsg(r.copyFromRealm(pendings));

                        Call<ResponseBody> call = Storage.getAPIService(ctx).sendMessages(req);
                        // ga boleh call.enqueue krn bisa hilang dr memory utk variable2 di atas
                        try {
                            Response<ResponseBody> execute = call.execute();

                            if (!execute.isSuccessful()) {
                                r.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        for (TrnChatMsg _obj : pendings) {
                                            _obj.setMessageStatus(ConstChat.MESSAGE_STATUS_FAILED);
                                        }
                                    }
                                });

                                return;
                            }

                            final ResponseBody resp = execute.body();

                            try {
                                String msgStatus = resp.string();

//                        Log.d(TAG, "msgStatus = " + msgStatus);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            r.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    for (TrnChatMsg msg : pendings) {
                                        msg.setMessageStatus(ConstChat.MESSAGE_STATUS_SERVER_RECEIVED);
                                    }

                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
//                            Utility.throwableHandler(ctx, e, false);
                        }

                    }

                }finally{
                    if (r != null)
                        r.close();
                }

            }
        });
    }

}
