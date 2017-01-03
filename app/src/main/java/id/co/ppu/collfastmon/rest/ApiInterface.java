package id.co.ppu.collfastmon.rest;

import id.co.ppu.collfastmon.pojo.chat.TrnChatMsg;
import id.co.ppu.collfastmon.rest.request.RequestCollJobByDate;
import id.co.ppu.collfastmon.rest.request.RequestCollJobBySpv;
import id.co.ppu.collfastmon.rest.request.RequestGetGPSHistory;
import id.co.ppu.collfastmon.rest.request.RequestLKPByDate;
import id.co.ppu.collfastmon.rest.request.RequestLogError;
import id.co.ppu.collfastmon.rest.request.RequestLogin;
import id.co.ppu.collfastmon.rest.request.RequestReopenBatch;
import id.co.ppu.collfastmon.rest.request.RequestSyncLocation;
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
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Eric on 19-Aug-16.
 */
public interface ApiInterface {
    @POST("fastmon/login")
    Call<ResponseLogin> login(@Body RequestLogin req);

    @POST("fastmon/get_any_user")
    Call<ResponseUserPwd> getAnyUser();

//    @POST("fastmon/get_colls")
//    Call<ResponseGetCollJob> getCollectorsJob(@Body RequestCollJobBySpv req);

    @POST("fastmon/get_coll_list")
    Call<ResponseGetCollJobList> getCollectorsJob(@Body RequestCollJobBySpv req);

    @POST("fastmon/get_coll_list_ex")
    Call<ResponseGetCollJobList> getCollectorsJobEx(@Body RequestCollJobBySpv req);

    @POST("fastmon/get_lkp")
    Call<ResponseGetLKPMonitoring> getLKPByDate(@Body RequestLKPByDate req);

    @GET("fastmon/get_app_version")
    Call<ResponseBody> getAppVersion(@Query("version") String version);

    @POST("fastmon/get_tasklog")
    Call<ResponseGetTaskLog> getTaskLog(@Body RequestCollJobByDate req);

    @POST("fastmon/masterdata")
    Call<ResponseGetMasterMonData> getMasterMonData();

    @POST("fastmon/reopen_batch")
    Call<ResponseBody> reopenBatch(@Body RequestReopenBatch req);


    ///////////////////////////////  CHAT FUNCTIONS  ////////////////////////////////////////
    @POST("fastchat/send")
    Call<ResponseBody> sendMessage(@Body TrnChatMsg msg);

    @POST("fastchat/send_messages")
    Call<ResponseBody> sendMessages(@Body RequestChatMsg msg);

    @POST("fastchat/status")
    Call<ResponseBody> sendStatus(@Body RequestChatStatus status);

    @GET("fastchat/update_msg_status")
    Call<ResponseBody> updateMessageStatus(@Query("uid") String uid, @Query("status") String status);

    @GET("fastchat/get_msg")
    Call<ResponseGetChatHistory> getMessage(@Query("uid") String uid);

    @GET("fastchat/get_latest_msg")
    Call<ResponseGetChatHistory> getLatestMessage(@Query("user1") String user1, @Query("user2") String user2);

    /**
     * When user open app, the previous messages wont update, so this function will check the selected messages.
     * in return, a message will contain UID and messageStatus only. The remaining fields will be empty to save bandwidth
     * @param req
     * @return
     */
    @POST("fastchat/get_msg_status")
    Call<ResponseGetChatHistory> checkMessageStatus(@Body RequestChatMsgStatus req);

    @POST("fastchat/status_check")
    Call<ResponseBody> checkStatus(@Body RequestChatStatus req);

//    @POST("fastchat/online_contacts")
//    Call<ResponseGetOnlineContacts> getGroupContacts();

    @POST("fastchat/online_contacts_by")
    Call<ResponseGetOnlineContacts> getOnlineContacts(@Body RequestChatStatus req);

    @POST("fastchat/chat_contacts")
    Call<ResponseGetOnlineContacts> getChatContacts(@Body RequestChatContacts req);

    @POST("fastchat/group_contacts_by")
    Call<ResponseGetOnlineContacts> getGroupContacts(@Body RequestChatStatus req);

    @POST("fastchat/chat_hist")
    Call<ResponseGetChatHistory> getChatHistory(@Body RequestGetChatHistory req);


    ///////////////////////////////  BASIC FAST FUNCTIONS  ////////////////////////////////////////
    @POST("fast/server_info")
    Call<ResponseServerInfo> getServerInfo();

//    @POST("fast/masterdata")
//    Call<ResponseGetMasterData> getMasterData();

    @POST("fast/log_error")
    Call<ResponseBody> logError(@Body RequestLogError req);

    @POST("fast/sync_gps")
    Call<ResponseBody> syncLocation(@Body RequestSyncLocation req);

    @POST("fast/get_gps_hist")
    Call<ResponseGetGPSHistory> getGPSHistory(@Body RequestGetGPSHistory req);

}
