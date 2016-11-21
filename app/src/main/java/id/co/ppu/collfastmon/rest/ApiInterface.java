package id.co.ppu.collfastmon.rest;

import id.co.ppu.collfastmon.rest.request.RequestCollJobByDate;
import id.co.ppu.collfastmon.rest.request.RequestGetGPSHistory;
import id.co.ppu.collfastmon.rest.request.RequestLKPByDate;
import id.co.ppu.collfastmon.rest.request.RequestLogError;
import id.co.ppu.collfastmon.rest.request.RequestLogin;
import id.co.ppu.collfastmon.rest.request.RequestReopenBatch;
import id.co.ppu.collfastmon.rest.request.RequestSyncLocation;
import id.co.ppu.collfastmon.rest.response.ResponseGetGPSHistory;
import id.co.ppu.collfastmon.rest.response.ResponseGetCollJob;
import id.co.ppu.collfastmon.rest.response.ResponseGetLKPMonitoring;
import id.co.ppu.collfastmon.rest.response.ResponseGetMasterMonData;
import id.co.ppu.collfastmon.rest.response.ResponseGetTaskLog;
import id.co.ppu.collfastmon.rest.response.ResponseLogin;
import id.co.ppu.collfastmon.rest.response.ResponseServerInfo;
import id.co.ppu.collfastmon.rest.response.ResponseUserPwd;
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

    @POST("fastmon/get_colls")
    Call<ResponseGetCollJob> getCollectorsJob(@Body RequestCollJobByDate req);

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
