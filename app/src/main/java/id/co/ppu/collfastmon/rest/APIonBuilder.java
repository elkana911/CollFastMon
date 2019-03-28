package id.co.ppu.collfastmon.rest;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.google.gson.JsonPrimitive;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.Date;
import java.util.List;
import java.util.Map;

import id.co.ppu.collfastmon.BuildConfig;
import id.co.ppu.collfastmon.pojo.chat.TrnChatMsg;
import id.co.ppu.collfastmon.rest.request.RequestBasic;
import id.co.ppu.collfastmon.rest.request.RequestCollJobByDate;
import id.co.ppu.collfastmon.rest.request.RequestCollJobBySpv;
import id.co.ppu.collfastmon.rest.request.RequestGetGPSHistory;
import id.co.ppu.collfastmon.rest.request.RequestLKPByDate;
import id.co.ppu.collfastmon.rest.request.RequestLogin;
import id.co.ppu.collfastmon.rest.request.RequestReopenBatch;
import id.co.ppu.collfastmon.rest.request.chat.RequestChatContacts;
import id.co.ppu.collfastmon.rest.request.chat.RequestChatMsg;
import id.co.ppu.collfastmon.rest.request.chat.RequestChatMsgStatus;
import id.co.ppu.collfastmon.rest.request.chat.RequestChatStatus;
import id.co.ppu.collfastmon.rest.request.chat.RequestGetChatHistory;
import id.co.ppu.collfastmon.rest.response.ResponseGetCollJobList;
import id.co.ppu.collfastmon.rest.response.ResponseGetGPSHistory;
import id.co.ppu.collfastmon.rest.response.ResponseGetLKPMonitoring;
import id.co.ppu.collfastmon.rest.response.ResponseGetMasterMonData;
import id.co.ppu.collfastmon.rest.response.ResponseGetTaskLog;
import id.co.ppu.collfastmon.rest.response.ResponseLogin;
import id.co.ppu.collfastmon.rest.response.ResponseServerInfo;
import id.co.ppu.collfastmon.rest.response.ResponseUserPwd;
import id.co.ppu.collfastmon.rest.response.chat.ResponseGetChatHistory;
import id.co.ppu.collfastmon.rest.response.chat.ResponseGetOnlineContacts;
import id.co.ppu.collfastmon.util.DataUtil;
import id.co.ppu.collfastmon.util.NetUtil;
import id.co.ppu.collfastmon.util.Storage;
import id.co.ppu.collfastmon.util.Utility;

/**
 * H2U
 APIonBuilder.getChatContacts(ctx, collectorsCode, (e, result) -> {

     if (e != null) {
         if (listener != null)
             listener.onFailure(e);
         return;
     }

    //avoid forceclose
     if (result == null || result.getData() == null) {
         return;
     }

     if (result.getError() != null) {
         if (listener != null) {
             String msg = "Error (" + result.getError().getErrorCode() + ")\n" + result.getError().getErrorDesc();
             listener.onFailure(new RuntimeException(msg));
         }

        return;
     }

     if (listener != null) {
         listener.onSuccess(resp.getData());
     }
 });
 *
 */
public class APIonBuilder {

//    private APIonBuilder(){
//        serverUrl = Storage.getUrlServer();
//    }

//    /**
//     *
//     * @param serverID refer to {@link Utility#SERVERS}
//     * @return
//     */
//    public static APIonBuilder changeServerTo(int serverID) {
//        if (self == null) {
//            self = new APIonBuilder();
//        }
//
//        self.serverUrl = Utility.buildUrlAsString(serverID);
//
//        return self;
//    }

    private static String _constructUrl(int serverID, String subServiceJson) {
        StringBuilder sb = new StringBuilder(Utility.buildUrlAsString(serverID));
        sb.append("/").append(subServiceJson);

        return sb.toString();
    }

    private static <T> void _buildIonGET(Context context, String url, ContentValues cvAsParam, Class<T> clazz, FutureCallback<T> callback) {

//        String url = serverUrl + "/" + subServiceJson;// Storage.getUrlServer(subServiceJson);

        Uri.Builder b = Uri.parse(url).buildUpon();

        if (cvAsParam != null)
            for (Map.Entry<String, Object> entry : cvAsParam.valueSet()) {
                b.appendQueryParameter(entry.getKey(), String.valueOf(entry.getValue()));
            }

        String fullUrl = b.toString();

        Ion.with(context)
                .load(fullUrl)
                .setTimeout(Long.valueOf(Utility.convertMinutesToMillis(Utility.NETWORK_TIMEOUT_MINUTES)).intValue())
                .basicAuthentication(NetUtil.SERVER_USERNAME, NetUtil.SERVER_PWD)
                .as(clazz)
                .setCallback(callback)
        ;
    }

    private static <T> void _buildIonPOST(Context context, String url, ContentValues cvAsParam, Object postRequest, Class<T> clazzResponse, FutureCallback<T> callback) {

//        String url = serverUrl + "/" + subServiceJson; //Storage.getUrlServer(subServiceJson);
        // it's possible to self fix
        if (url == null || !url.startsWith("http")) {
            url = Storage.getUrlServer(url);
        }

        Uri.Builder b = Uri.parse(url).buildUpon();

        if (cvAsParam != null)
            for (Map.Entry<String, Object> entry : cvAsParam.valueSet()) {
                b.appendQueryParameter(entry.getKey(), String.valueOf(entry.getValue()));
            }

        String fullUrl = b.toString();
        if (postRequest == null) {
            Ion.with(context)
                    .load("POST", fullUrl)
                    .setTimeout(Long.valueOf(Utility.convertMinutesToMillis(Utility.NETWORK_TIMEOUT_MINUTES)).intValue())
                    .basicAuthentication(NetUtil.SERVER_USERNAME, NetUtil.SERVER_PWD)
                    .as(clazzResponse)
                    .setCallback(callback)
            ;

        } else {
            Ion.with(context)
                    .load(fullUrl)
                    .setTimeout(Long.valueOf(Utility.convertMinutesToMillis(Utility.NETWORK_TIMEOUT_MINUTES)).intValue())
                    .basicAuthentication(NetUtil.SERVER_USERNAME, NetUtil.SERVER_PWD)
                    .setJsonPojoBody(postRequest)
                    .as(clazzResponse)
                    .setCallback(callback)
            ;
        }
    }

    private static void _fillRequest(Context context, String actionName, RequestBasic req) {
        try {
            double[] gps = id.co.ppu.collfastmon.screen.location.Location.getGPS(context);
            String latitude = String.valueOf(gps[0]);
            String longitude = String.valueOf(gps[1]);
            req.setLatitude(latitude);
            req.setLongitude(longitude);
        } catch (Exception e) {
            e.printStackTrace();

            req.setLatitude("0.0");
            req.setLongitude("0.0");
        }

        req.setActionName(actionName);
        req.setUserId(DataUtil.getCurrentUserId());
        req.setSysInfo(Utility.buildSysInfoAsCsv(context));


    }

    public static void login(Context ctx, int serverID, String username, String password, FutureCallback<ResponseLogin> callback){

        RequestLogin req = new RequestLogin();

        _fillRequest(ctx, Utility.ACTION_LOGIN, req);

        req.setId(username);
        req.setPwd(password);

       _buildIonPOST(ctx, _constructUrl(serverID,"fastmon/login.json"), null, req, ResponseLogin.class, callback);
    }

    public static void getServerInfo(Context ctx, int serverID, FutureCallback<ResponseServerInfo> callback){
        _buildIonPOST(ctx, _constructUrl(serverID, "fast/server_info.json"), null, null, ResponseServerInfo.class, callback);
    }

    public static void getServerInfo(Context ctx, FutureCallback<ResponseServerInfo> callback){
        getServerInfo(ctx, Storage.getPrefAsInt(Storage.KEY_SERVER_ID, 0), callback);
//        _buildIonPOST(ctx, "fast/server_info.json", null, null, ResponseServerInfo.class, callback);
    }

    public static void getLKPByDate(Context ctx, String collCode, Date lkpDate, FutureCallback<ResponseGetLKPMonitoring> callback){

        RequestLKPByDate req = new RequestLKPByDate();
        req.setCollectorCode(collCode);
        req.setYyyyMMdd(Utility.convertDateToString(lkpDate, "yyyyMMdd"));

        _buildIonPOST(ctx, "fastmon/get_lkp.json", null, req, ResponseGetLKPMonitoring.class, callback);
    }

    public static void getAppVersion(Context ctx, int serverID, FutureCallback<JsonPrimitive> callback){
        final String versionName = BuildConfig.VERSION_NAME;

        ContentValues cv = new ContentValues();
        cv.put("version", versionName);

        _buildIonGET(ctx,  _constructUrl(serverID, "fastmon/get_app_version.json"), cv, JsonPrimitive.class, callback);
    }

    public static void getAnyUser(Context ctx, int serverID, FutureCallback<ResponseUserPwd> callback){
        _buildIonPOST(ctx, _constructUrl(serverID,"fastmon/get_any_user.json") , null, null, ResponseUserPwd.class, callback);
    }

    public static void getCollectorsJobEx(Context ctx, Date lkpDate, FutureCallback<ResponseGetCollJobList> callback){

        RequestCollJobBySpv req = new RequestCollJobBySpv();
        _fillRequest(ctx, Utility.ACTION_GET_COLL, req);

        req.setSpvCode(DataUtil.getCurrentUserId());
//        req.setLdvNo(null); // not mandatory for this service
        req.setLkpDate(lkpDate);

        _buildIonPOST(ctx, "fastmon/get_coll_list_ex.json", null, req, ResponseGetCollJobList.class, callback);
    }

    public static void getTaskLog(Context ctx, String collCode, Date lkpDate, FutureCallback<ResponseGetTaskLog> callback){

        RequestCollJobByDate req = new RequestCollJobByDate();

        _fillRequest(ctx, Utility.ACTION_GET_COLL, req);

        req.setCollCode(collCode);
//        req.setLdvNo(this.ldvNo);
        req.setLkpDate(lkpDate);

        _buildIonPOST(ctx, "fastmon/get_tasklog.json", null, req, ResponseGetTaskLog.class, callback);
    }

    public static void reopenBatch(Context ctx, String ldvNo, Date lkpDate, FutureCallback<JsonPrimitive> callback){

        RequestReopenBatch req = new RequestReopenBatch();

        _fillRequest(ctx, Utility.ACTION_REOPEN_BATCH, req);

        req.setLdvNo(ldvNo);
        req.setSpvCode( DataUtil.getCurrentUserId());
        req.setYyyyMMdd(Utility.convertDateToString(lkpDate, "yyyyMMdd"));

        _buildIonPOST(ctx, "fastmon/reopen_batch.json", null, req, JsonPrimitive.class, callback);
    }

    public static void getGPSHistory(Context ctx, String collCode, Date lkpDate, FutureCallback<ResponseGetGPSHistory> callback){

        RequestGetGPSHistory req = new RequestGetGPSHistory();

        _fillRequest(ctx, Utility.ACTION_GET_GPS, req);

        req.setCollectorCode(collCode);
        // ambil semuanya dalam hitungan hari, bukan jam
        req.setFromDate(lkpDate);
        req.setToDate(lkpDate);

        // ambil semuanya saja
        String business = "ALL"; //((String)spAction.getSelectedItem());

        req.setBusiness(business);

        if (req.getToDate().before(req.getFromDate())) {
            Utility.showDialog(ctx, "Error", "Invalid To Date.");
            return;
        }

        _buildIonPOST(ctx, "fast/get_gps_hist.json", null, req, ResponseGetGPSHistory.class, callback);
    }

    public static void getMasterMonData(Context ctx, FutureCallback<ResponseGetMasterMonData> callback){
        _buildIonPOST(ctx, "fastmon/masterdata.json", null, null, ResponseGetMasterMonData.class, callback);
    }


    // chat functions---------------
    public static void sendMessages(Context ctx, List<TrnChatMsg> messages, FutureCallback<JsonPrimitive> callback){
        RequestChatMsg req = new RequestChatMsg();
        req.setMsg(messages);
        _buildIonPOST(ctx, "fastchat/send_messages.json", null, req, JsonPrimitive.class, callback);
    }

    public static void sendStatus(Context ctx, String collCode, String messageStatus, String message, FutureCallback<JsonPrimitive> callback){
        RequestChatStatus req = new RequestChatStatus();
        req.setCollCode(collCode);
        req.setStatus(messageStatus);
        req.setMessage(message);
        req.setAndroidId(Storage.getAndroidToken());

        _buildIonPOST(ctx, "fastchat/status.json", null, req, JsonPrimitive.class, callback);
    }

    public static void getChatContacts(Context ctx, List<String> collectorsCode, FutureCallback<ResponseGetOnlineContacts> callback){
        RequestChatContacts req = new RequestChatContacts();
        req.setCollsCode(collectorsCode);

        _buildIonPOST(ctx, "fastchat/chat_contacts.json", null, req, ResponseGetOnlineContacts.class, callback);
    }

    public static void getGroupContacts(Context ctx, String collCode, String messageStatus, String message, String androidId, FutureCallback<ResponseGetOnlineContacts> callback){
        RequestChatStatus req = new RequestChatStatus();
        req.setCollCode(collCode);
        req.setMessage(message);
        req.setStatus(messageStatus);
        req.setAndroidId(androidId);

        _buildIonPOST(ctx, "fastchat/group_contacts_by.json", null, req, ResponseGetOnlineContacts.class, callback);
    }

    public static void getOnlineContacts(Context ctx, String collCode, String messageStatus, String message, String androidId, FutureCallback<ResponseGetOnlineContacts> callback){
        RequestChatStatus req = new RequestChatStatus();
        req.setCollCode(collCode);
        req.setMessage(message);
        req.setStatus(messageStatus);
        req.setAndroidId(androidId);

        _buildIonPOST(ctx, "fastchat/online_contacts_by.json", null, req, ResponseGetOnlineContacts.class, callback);
    }

    public static void checkMessageStatus(Context ctx, List<String> uid, FutureCallback<ResponseGetChatHistory> callback){
        RequestChatMsgStatus req = new RequestChatMsgStatus();
        req.setUid(uid);

        _buildIonPOST(ctx, "fastchat/get_msg_status.json", null, req, ResponseGetChatHistory.class, callback);
    }

    public static void checkUserStatus(Context ctx, String collCode, FutureCallback<JsonPrimitive> callback){
        RequestChatStatus req = new RequestChatStatus();
        req.setCollCode(collCode);

        _buildIonPOST(ctx, "fastchat/status_check.json", null, req, JsonPrimitive.class, callback);
    }

    public static void getChatHistory(Context ctx, String fromCollCode, String toCollCode, String lastUid, FutureCallback<ResponseGetChatHistory> callback){
        RequestGetChatHistory req = new RequestGetChatHistory();
        req.setFromCollCode(fromCollCode);
        req.setToCollCode(toCollCode);
        req.setYyyyMMdd("");
        req.setLastUid(lastUid);

        _buildIonPOST(ctx, "fastchat/chat_hist.json", null, req, ResponseGetChatHistory.class, callback);
    }

    public static void updateMessageStatus(Context ctx, String uid, String status, FutureCallback<JsonPrimitive> callback){

        ContentValues cv = new ContentValues();
        cv.put("uid", uid);
        cv.put("status", status);

        _buildIonGET(ctx, "fastchat/update_msg_status.json", cv, JsonPrimitive.class, callback);
    }

    public static void getMessage(Context ctx, String uid, FutureCallback<ResponseGetChatHistory> callback){

        ContentValues cv = new ContentValues();
        cv.put("uid", uid);

        _buildIonGET(ctx, "fastchat/get_msg.json", cv, ResponseGetChatHistory.class, callback);
    }

    // end chat functions-----------
}
