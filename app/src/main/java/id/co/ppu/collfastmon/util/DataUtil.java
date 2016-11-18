package id.co.ppu.collfastmon.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;
import java.util.Date;
import java.util.Set;

import id.co.ppu.collfastmon.pojo.master.MstDelqReasons;
import id.co.ppu.collfastmon.pojo.master.MstLDVClassifications;
import id.co.ppu.collfastmon.pojo.master.MstLDVParameters;
import id.co.ppu.collfastmon.pojo.master.MstLDVStatus;
import id.co.ppu.collfastmon.pojo.master.MstOffices;
import id.co.ppu.collfastmon.pojo.master.MstParam;
import id.co.ppu.collfastmon.pojo.master.MstPotensi;
import id.co.ppu.collfastmon.pojo.master.MstTaskType;
import id.co.ppu.collfastmon.rest.ApiInterface;
import id.co.ppu.collfastmon.rest.ServiceGenerator;
import id.co.ppu.collfastmon.rest.response.ResponseGetMasterData;
import id.co.ppu.collfastmon.rest.response.ResponseGetMasterMonData;
import io.realm.Realm;
import io.realm.RealmModel;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Eric on 23-Sep-16.
 */

public class DataUtil {
    public static final int SYNC_AS_PAYMENT = 1;
    public static final int SYNC_AS_VISIT = 2;
    public static final int SYNC_AS_REPO = 3;

    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static void resetData(Realm realm) {

        if (realm != null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.deleteAll();
                }
            });
        }

    }

    public static void resetData() {
        Realm r = Realm.getDefaultInstance();
        try{
            resetData(r);
        }finally {
            if (r != null) {
                r.close();
            }
        }
    }

    public static boolean isMasterTransactionTable(String tableName) {
        return tableName.equalsIgnoreCase(MstDelqReasons.class.getSimpleName())
                || tableName.equalsIgnoreCase(MstLDVClassifications.class.getSimpleName())
                || tableName.equalsIgnoreCase(MstLDVParameters.class.getSimpleName())
                || tableName.equalsIgnoreCase(MstLDVStatus.class.getSimpleName())
                || tableName.equalsIgnoreCase(MstParam.class.getSimpleName())
                || tableName.equalsIgnoreCase(MstPotensi.class.getSimpleName())
                ;
    }

    public static boolean isMasterDataDownloaded(Context ctx, Realm realm, boolean forceDownload) {

        boolean masterIsEmpty = false;
        Set<Class<? extends RealmModel>> tables = realm.getConfiguration().getRealmObjectClasses();
        for (Class<? extends RealmModel> table : tables) {
            String key = table.getSimpleName();
            if (!key.toLowerCase().startsWith("mst"))
                continue;

            // skip juga yg bukan table master transaksi
            if (!isMasterTransactionTable(key))
                continue;

            long count = realm.where(table).count();

            if (count < 1) {
                masterIsEmpty = true;
                break;
            }
        }

        if (!masterIsEmpty && !forceDownload) {
            return true;
        }

        // is master data complete  ?

        // skip master data
        if (!masterIsEmpty) {
//        if (count > 0) {
            return true;
        }

        if (!NetUtil.isConnected(ctx))
            return false;

        try {
            retrieveMasterFromServerBackground(realm, ctx);

            return true;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    public static void retrieveMasterFromServerBackground(Realm realm, Context ctx) throws Exception {

        ApiInterface fastService =
                ServiceGenerator.createService(ApiInterface.class, Utility.buildUrl(Storage.getPreferenceAsInt(ctx, Storage.KEY_SERVER_ID, 0)));

        Call<ResponseGetMasterMonData> callMasterData = fastService.getMasterMonData();

        // hrs async karena error networkonmainthreadexception
        callMasterData.enqueue(new Callback<ResponseGetMasterMonData>() {
            @Override
            public void onResponse(Call<ResponseGetMasterMonData> call, Response<ResponseGetMasterMonData> response) {
                if (response.isSuccessful()) {
                    final ResponseGetMasterMonData respGetMasterData = response.body();

                    if (respGetMasterData.getError() != null) {

                    } else {
                        // save db here, tp krn async disarankan buat instance baru
                        Realm _realm = Realm.getDefaultInstance();
                        // save db here
                        try {
                            _realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm bgRealm) {
                                    // insert ldp classifications
                                    long count = bgRealm.where(MstLDVClassifications.class).count();
                                    if (count > 0) {
                                        bgRealm.delete(MstLDVClassifications.class);
                                    }
                                    bgRealm.copyToRealmOrUpdate(respGetMasterData.getData().getLdpClassifications());

                                    // insert param
                                    count = bgRealm.where(MstParam.class).count();
                                    if (count > 0) {
                                        bgRealm.delete(MstParam.class);
                                    }
                                    bgRealm.copyToRealmOrUpdate(respGetMasterData.getData().getParams());

                                    // insert ldp status
                                    count = bgRealm.where(MstLDVStatus.class).count();
                                    if (count > 0) {
                                        bgRealm.delete(MstLDVStatus.class);
                                    }
                                    bgRealm.copyToRealmOrUpdate(respGetMasterData.getData().getLdpStatus());

                                    // insert ldp parameter
                                    count = bgRealm.where(MstLDVParameters.class).count();
                                    if (count > 0) {
                                        bgRealm.delete(MstLDVParameters.class);
                                    }
                                    bgRealm.copyToRealmOrUpdate(respGetMasterData.getData().getLdpParameters());

                                    // insert delq reasons
                                    count = bgRealm.where(MstDelqReasons.class).count();
                                    if (count > 0) {
                                        bgRealm.delete(MstDelqReasons.class);
                                    }
                                    bgRealm.copyToRealmOrUpdate(respGetMasterData.getData().getDelqReasons());

                                    // insert office
                                    count = bgRealm.where(MstOffices.class).count();
                                    if (count > 0) {
                                        bgRealm.delete(MstOffices.class);
                                    }
                                    bgRealm.copyToRealmOrUpdate(respGetMasterData.getData().getOffices());

                                    // insert potensi
                                    count = bgRealm.where(MstPotensi.class).count();
                                    if (count > 0) {
                                        bgRealm.delete(MstPotensi.class);
                                    }
                                    bgRealm.copyToRealmOrUpdate(respGetMasterData.getData().getPotensi());

                                    // insert task type
                                    count = bgRealm.where(MstTaskType.class).count();
                                    if (count > 0) {
                                        bgRealm.delete(MstTaskType.class);
                                    }
                                    bgRealm.copyToRealmOrUpdate(respGetMasterData.getData().getTask());
                                }
                            });

                        } finally {
                            if (_realm != null) {
                                _realm.close();
                            }
                        }
                    }
                } else {
                    // handle request errors yourself
                    ResponseBody errorBody = response.errorBody();
                    try {
                        Log.e("eric.unSuccessful", errorBody.string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseGetMasterMonData> call, Throwable t) {
                Log.e("eric.onFailure", t.getMessage(), t);
            }
        });

    }

}
