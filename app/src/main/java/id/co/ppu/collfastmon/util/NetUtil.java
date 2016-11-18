package id.co.ppu.collfastmon.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import java.util.Date;

import id.co.ppu.collfastmon.pojo.UserData;
import id.co.ppu.collfastmon.pojo.trn.TrnCollPos;
import id.co.ppu.collfastmon.pojo.trn.TrnErrorLog;
import id.co.ppu.collfastmon.rest.ApiInterface;
import id.co.ppu.collfastmon.rest.ServiceGenerator;
import id.co.ppu.collfastmon.rest.request.RequestLogError;
import id.co.ppu.collfastmon.rest.request.RequestSyncLocation;
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

}
