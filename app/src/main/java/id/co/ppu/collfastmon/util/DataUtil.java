package id.co.ppu.collfastmon.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import id.co.ppu.collfastmon.pojo.CollJob;
import id.co.ppu.collfastmon.pojo.chat.TrnChatMsg;
import id.co.ppu.collfastmon.pojo.master.MasterData;
import id.co.ppu.collfastmon.pojo.master.MasterMonData;
import id.co.ppu.collfastmon.pojo.master.MstDelqReasons;
import id.co.ppu.collfastmon.pojo.master.MstLDVClassifications;
import id.co.ppu.collfastmon.pojo.master.MstLDVParameters;
import id.co.ppu.collfastmon.pojo.master.MstLDVStatus;
import id.co.ppu.collfastmon.pojo.master.MstOffices;
import id.co.ppu.collfastmon.pojo.master.MstParam;
import id.co.ppu.collfastmon.pojo.master.MstPotensi;
import id.co.ppu.collfastmon.pojo.master.MstTaskType;
import id.co.ppu.collfastmon.pojo.trn.TrnRVColl;
import id.co.ppu.collfastmon.pojo.trn.TrnRVCollPK;
import id.co.ppu.collfastmon.rest.ApiInterface;
import id.co.ppu.collfastmon.rest.ServiceGenerator;
import id.co.ppu.collfastmon.rest.response.ResponseGetMasterMonData;
import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmQuery;
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

    public static String generateRunningNumber(Date date, String collCode) {
        //yyyyMMdd-runnningnumber2digit
        StringBuilder sb = new StringBuilder();
        sb.append(Utility.convertDateToString(date, "dd"))
                .append(Utility.convertDateToString(date, "MM"))
                .append(Utility.convertDateToString(date, "yyyy"))
//                            .append(Utility.leftPad(runningNumber, 3));
                .append(collCode);

        return sb.toString();
    }

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

        if (!NetUtil.isConnected(ctx)) {
            if (DemoUtil.isDemo(ctx)) {

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        MasterMonData data = DemoUtil.buildMasterData();

                        saveMastersToDB(realm, data);
                    }
                });

                return true;
            }else
                return false;
        }

        try {
            retrieveMasterFromServerBackground(realm, ctx);

            return true;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    public static void saveCollectorsToDB(Realm bgRealm, List<CollJob> data) {

        boolean d = bgRealm.where(CollJob.class).findAll().deleteAllFromRealm();

        // replace taskcode ke string
        // dimodifikasi spy bisa nampilin header di recycler view
        List<CollJob> cj = new ArrayList<>();

        boolean headerCreated = false;

        for (int i = 0; i < data.size(); i++) {

            CollJob obj = data.get(i);

            MstTaskType first = bgRealm.where(MstTaskType.class)
                    .equalTo("taskCode", data.get(i).getLastTask())
                    .findFirst();

            if (first != null) {
                data.get(i).setLastTask(first.getShortDesc());
                obj.setLastTask(first.getShortDesc());
            }

            if (!headerCreated && obj.getCountLKP() < 1) {
                CollJob header = new CollJob();
                header.setCountVisited(100L);

                // hitung brp collector yg countlkp-nya 0
                                            /*
                                            long counter = 0;
                                            for (CollJob _cj : respGetCollJob.getData()) {
                                                if (_cj.getCountLKP() < 1)
                                                    counter += 1;
                                            }
                                            */

                header.setCountLKP(0L);
                header.setCollCode("Z");
                header.setCollName("A");

                cj.add(header);
                headerCreated = true;
            }

            cj.add(obj);
        }

        // ternyata isinya bisa berkurang karena sebelumnya duplicate
        bgRealm.copyToRealmOrUpdate(cj);

        CollJob z = bgRealm.where(CollJob.class).equalTo("collCode", "Z").findFirst();
        if (z != null) {

            long count = bgRealm.where(CollJob.class)
                    .notEqualTo("collCode", "Z")
                    .equalTo("countLKP", 0L)
                    .count();

            z.setCountLKP(count);

            bgRealm.copyToRealmOrUpdate(z);
        }

    }

    // store db without commit/rollback. you need to prepare it first
    public static void saveMastersToDB(Realm bgRealm, MasterMonData data) {
        // insert ldp classifications
        long count = bgRealm.where(MstLDVClassifications.class).count();
        if (count > 0) {
            bgRealm.delete(MstLDVClassifications.class);
        }
        bgRealm.copyToRealmOrUpdate(data.getLdpClassifications());

        // insert param
        count = bgRealm.where(MstParam.class).count();
        if (count > 0) {
            bgRealm.delete(MstParam.class);
        }
        bgRealm.copyToRealmOrUpdate(data.getParams());

        // insert ldp status
        count = bgRealm.where(MstLDVStatus.class).count();
        if (count > 0) {
            bgRealm.delete(MstLDVStatus.class);
        }
        bgRealm.copyToRealmOrUpdate(data.getLdpStatus());

        // insert ldp parameter
        count = bgRealm.where(MstLDVParameters.class).count();
        if (count > 0) {
            bgRealm.delete(MstLDVParameters.class);
        }
        bgRealm.copyToRealmOrUpdate(data.getLdpParameters());

        // insert delq reasons
        count = bgRealm.where(MstDelqReasons.class).count();
        if (count > 0) {
            bgRealm.delete(MstDelqReasons.class);
        }
        bgRealm.copyToRealmOrUpdate(data.getDelqReasons());

        // insert office
        count = bgRealm.where(MstOffices.class).count();
        if (count > 0) {
            bgRealm.delete(MstOffices.class);
        }
        bgRealm.copyToRealmOrUpdate(data.getOffices());

        // insert potensi
        count = bgRealm.where(MstPotensi.class).count();
        if (count > 0) {
            bgRealm.delete(MstPotensi.class);
        }
        bgRealm.copyToRealmOrUpdate(data.getPotensi());

        // insert task type
        count = bgRealm.where(MstTaskType.class).count();
        if (count > 0) {
            bgRealm.delete(MstTaskType.class);
        }
        bgRealm.copyToRealmOrUpdate(data.getTask());

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

                                    saveMastersToDB(bgRealm, respGetMasterData.getData());

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

    public static RealmQuery<TrnChatMsg> queryChatMsg(Realm r, String user1, String user2) {

        RealmQuery<TrnChatMsg> group = r.where(TrnChatMsg.class)
                .beginGroup()
                .equalTo("fromCollCode", user1)
                .equalTo("toCollCode", user2)
                .endGroup()
                .or()
                .beginGroup()
                .equalTo("fromCollCode", user2)
                .equalTo("toCollCode", user1)
                .endGroup();

        return group;

    }

    /**
     *
     * @param data null to create, not null to update
     * @param serverDate
     * @param collCode
     * @param officeCode
     * @param ldvNo
     * @param contractNo
     * @param rvbNo
     * @param paymentFlag
     * @param platform
     * @param danaSosial
     * @param angsuranKe
     * @param denda
     * @param biayaTagih
     * @param receivedAmount
     * @param latitude
     * @param longitude
     * @param notes
     * @return
     */
    public static TrnRVColl createTrnRVColl(TrnRVColl data, Date serverDate, String collCode, String officeCode, String ldvNo, String contractNo, String rvbNo, Long paymentFlag, String platform, Long danaSosial, Long angsuranKe, Long denda, Long biayaTagih, Long receivedAmount, String latitude, String longitude, String notes) {

        if (data == null) {
            TrnRVCollPK pk = new TrnRVCollPK();
            pk.setRbvNo(rvbNo);
            pk.setRvCollNo(DataUtil.generateRunningNumber(serverDate, collCode));

            data = new TrnRVColl();
            data.setPk(pk);
            data.setCreatedBy(Utility.LAST_UPDATE_BY);
            data.setCreatedTimestamp(new Date());
        }

        data.setStatusFlag("NW");
        data.setFlagToEmrafin("N");
        data.setCollId(collCode);
        data.setOfficeCode(officeCode);
        data.setFlagDone("Y");
        data.setTransDate(serverDate);
        data.setProcessDate(serverDate);
        data.setDaysIntrAc(0L);

        data.setPaymentFlag(paymentFlag);

        // payment receive udah pasti denda, maka ambil dana sosial apa adanya dari detil
        long lDanaSosial = danaSosial == null ? 0 : danaSosial.longValue();
        data.setDanaSosial(lDanaSosial);

        data.setPlatform(platform);

        data.setInstNo(angsuranKe);

        data.setPenaltyAc(denda);
        data.setCollFeeAc(biayaTagih);

        data.setLdvNo(ldvNo);
        data.setContractNo(contractNo);

        data.setLatitude(latitude);
        data.setLongitude(longitude);

        data.setReceivedAmount(receivedAmount);
        data.setNotes(notes);

        data.setLastupdateBy(Utility.LAST_UPDATE_BY);
        data.setLastupdateTimestamp(new Date());

        return data;
    }


}
