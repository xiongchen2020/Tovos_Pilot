package com.tovos.uav.sample.installapp;


import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.example.commonlib.utils.DialogUtils;
import com.example.commonlib.utils.LogUtil;
import com.example.commonlib.utils.ToastUtils;
import com.tovos.uav.sample.network.AppModel;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class DownloadAppService extends Service {
    AppModel model;
    File file;

    @Override
    public void onCreate() {
        super.onCreate();
        file = new File(Environment.getExternalStorageDirectory()+"/tovos", "tovos.apk");
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        model = new AppModel();
        String downloadUrl = intent.getStringExtra("app_url");
        LogUtil.d("app_url",downloadUrl);
        downLoadAPK(downloadUrl);
        return super.onStartCommand(intent, flags, startId);
    }
    private void downLoadAPK(String downloadURL) {
        // DialogUtils.showWaitingDialog(this, "安装包下载中。。。");
        Observable<ResponseBody> observable  =  model.download(downloadURL);
        model.addSubscription(observable.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())//需要
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody baseResponse) throws Exception {

                       // LogUtil.d("app_url","下载成功");
                        try {
                            InputStream is = baseResponse.byteStream();
                           // file = new File(Environment.getExternalStorageDirectory()+"/tovos", "tovos.apk");
                            FileOutputStream fos = new FileOutputStream(file);
                            BufferedInputStream bis = new BufferedInputStream(is);
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = bis.read(buffer)) != -1) {
                                fos.write(buffer, 0, len);
                                fos.flush();
                            }
                            fos.close();
                            bis.close();
                            is.close();

                            LogUtil.d("app_url","下载成功");
                            showSelectAPK();
                        } catch (IOException e) {
                            e.printStackTrace();
                            if (file!=null){
                                file.delete();
                            }
                            //  DialogUtils.dismissWaitingDialog();
                            ToastUtils.setResultToToast("升级失败，请重新升级");

                        }finally {
                            // DialogUtils.dismissWaitingDialog();
                        }


                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtil.d("app_url","下载失败");
                        ToastUtils.setResultToToast("安装包下载失败");
                        DialogUtils.dismissWaitingDialog();
                    }
                }));

    }


    /***
     * 调起安装APP窗口  安装APP
     */
    private  void showSelectAPK(){
        File file = new File(Environment.getExternalStorageDirectory() +"/tovos/tovos.apk");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        //判读版本是否在7.0以上
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            Uri apkUri = FileProvider.getUriForFile(this, "com.tovos.uav.sample.fileprovider", file);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        }else{
            intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
        }
        startActivity(intent);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        model.onUnsubscribe();
    }
}
