package com.tovos.uav.sample.network;

import com.example.filgthhublibrary.network.bean.ResRegisterUser;
import com.tovos.uav.sample.network.bean.BaseResponse;


import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
import retrofit2.http.Url;


public interface APIService {

    @FormUrlEncoded
    @POST("check")
    Observable<BaseResponse> check(@Field("_api_key") String _api_key,@Field("appKey") String appKey,@Field("buildVersion") String buildVersion);

    @Streaming
    @GET
    Observable<ResponseBody> download(@Url String url);
}
