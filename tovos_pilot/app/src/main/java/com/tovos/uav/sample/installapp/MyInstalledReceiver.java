package com.tovos.uav.sample.installapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import com.example.commonlib.utils.LogUtil;
import com.tovos.uav.sample.route.BaseActivity;

import java.io.File;

public class MyInstalledReceiver  extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //接收安装广播
        if (intent.getAction().equals("android.intent.action.MY_PACKAGE_REPLACED")) {
            String packageName ="安装了:" + intent.getDataString()+ "包名的程序";
            LogUtil.d("MyInstalledReceiver",packageName);

            LogUtil.d("MyInstalledReceiver",context.getPackageName());
              File  file = new File(Environment.getExternalStorageDirectory()+"/tovos", "tovos.apk");
                if (file.exists()){
                    file.delete();

                Intent serviceIntent = new Intent(context, DownloadAppService.class);
                context.stopService(serviceIntent);
            }
        }


    }
}
