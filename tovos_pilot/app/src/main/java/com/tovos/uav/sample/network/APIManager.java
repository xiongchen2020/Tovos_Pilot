package com.tovos.uav.sample.network;

import android.content.Context;

import com.example.commonlib.utils.LogUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tovos.uav.sample.MApplication;


import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class APIManager {


   // public static String REQUEST_URL_API = "http://39.101.201.211/fhsdk/v3/";
    public static String REQUEST_URL_API = " https://www.pgyer.com/apiv2/app/";

    private static APIManager instance;

    public static final MediaType CONTENT_TYPE = MediaType.parse("application/x-www-form-urlencoded");


    private APIManager() {

    }

    public static APIManager getInstance() {
        if (instance == null) {
            synchronized (APIManager.class) {
                if (instance == null) {
                    instance = new APIManager();
                }
            }
        }
        return instance;
    }

    public Retrofit initRetrofit(){
        Context globalApp = MApplication.getInstance().getApplicationContext();
        File cacheDir = globalApp.getExternalCacheDir();
        // 设置OkHttp请求数据时缓存
        File cacheFile = new File(cacheDir, "cache_network");
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 100);
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

//                long ts = System.currentTimeMillis();
//
//                String token = "";
//                String signkey = "";
//                if (MApplication.customFlightHubManager!=null){
//                    token= MApplication.customFlightHubManager.getToken();
//                    signkey= MApplication.customFlightHubManager.getSignKey();
//                }
//                String sign = FHSignUtil.getSign(token,signkey,ts,body);

                Request request= chain.request().newBuilder()
//                        .header("FH-AppId", token)
//                        .header("FH-ReqId", reqId)
//                        .header("FH-Ts", ts+"")
//                        .addHeader("FH-Sign", sign)
                        .build();



                Response response = chain.proceed(request);


                int maxAge = 0;

                response.newBuilder()
                        .addHeader("Content-Type", "application/json;charset=UTF-8")
                        .addHeader("Cache-Control", "public, max-age=" + maxAge)
                        .removeHeader("Pragma")// 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                        .build();

                return response;
            }
        };

        OkHttpClient client = new OkHttpClient.Builder().cache(cache)
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();
        LogUtil.i("APIManager","hub_url:"+REQUEST_URL_API);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(REQUEST_URL_API)
                //增加返回值为Gson的支持(以实体类返回)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                //增加返回值为Oservable<T>的支持
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }
}
