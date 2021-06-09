package com.tovos.uav.sample.network;

import android.telephony.SubscriptionManager;

import com.example.filgthhublibrary.util.FHSignUtil;
import com.google.gson.Gson;
import com.tovos.uav.sample.BuildConfig;
import com.tovos.uav.sample.network.bean.BaseResponse;


import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

import static com.tovos.uav.sample.network.APIManager.CONTENT_TYPE;


public class AppModel {

    private Retrofit retrofit;
    private CompositeDisposable compositeDisposable;

    private String token = "";
    private String signKey = "";


    public AppModel() {
        retrofit = APIManager.getInstance().initRetrofit();

    }


    public Observable<BaseResponse> check() {

        APIService service = retrofit.create(APIService.class);

        return service.check("5d5193d5dcd80504654fe7c1c80ac96c","62ef023003f9ea69cf7832e9e089d140",BuildConfig.VERSION_NAME);

    }


    public Observable<ResponseBody> download(String url){
        APIService service = retrofit.create(APIService.class);
        return service.download(url);
    }
    public void onUnsubscribe() {
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();

        }
    }


    public void addSubscription(Disposable subscriber) {
        if (compositeDisposable == null) {

            compositeDisposable = new CompositeDisposable();

        }
        compositeDisposable.add(subscriber);
    }


}
