package com.tovos.uav.sample.medial;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.commonlib.utils.CustomTimeUtils;
import com.example.djilib.dji.component.camera.CustomCameraManager;
import com.example.djilib.dji.component.camera.MyCameraInterface;
import com.tovos.uav.sample.MApplication;
import com.tovos.uav.sample.R;
import com.tovos.uav.sample.databean.TaskBean;
import com.example.commonlib.utils.DialogManager;
import com.example.commonlib.utils.FileManager;
import com.tovos.uav.sample.medial.view.PhotoView;


import java.text.ParseException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import dji.common.camera.SettingsDefinitions;
import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.sdk.media.MediaFile;

public class BaseMedialActivity extends AppCompatActivity implements MyCameraInterface {

    public TaskBean taskBean;
    public CustomCameraManager customCameraManager;
    public String timeformat = "yyyy-MM-dd HH:mm:ss";
    public String cachePath = "";
    public String medialPath = "";
    public String path = "";

    public void createFlie() {

        try {
            path = MApplication.picPath+taskBean.getName()+"_"
                    + CustomTimeUtils.longToString(taskBean.getStatrtime(), timeformat)+"_"
                    +CustomTimeUtils.longToString(taskBean.getEndtime(), timeformat)+"/";
            cachePath = path+"cache/";
            medialPath = path +"medial/";
            FileManager fileManager = new FileManager();
            fileManager.createDirectory(path);
            fileManager.createDirectory(cachePath);
            fileManager.createDirectory(medialPath);
            //  fileManager.createDirectory(path+"video/");
            Log.i(this.getClass().getSimpleName(),"path:"+path);
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }
    public void init(){
        customCameraManager.isMediaDownloadModeSupported();
        if (customCameraManager.isModuleAvailable()){
            customCameraManager.setCameraModel(SettingsDefinitions.CameraMode.MEDIA_DOWNLOAD);
        } else {
            showMedialDialog();
        }

    }

    @Override
    public void setStartShootState() {

    }

    @Override
    public void setEndShootState(int position) {

    }

    @Override
    public void setCameraMode(DJIError djiError) {
        if (customCameraManager.isModuleAvailable()){
            try {

                customCameraManager.getMediaPath(CustomTimeUtils.longToString(taskBean.getStatrtime(), timeformat), CustomTimeUtils.longToString(taskBean.getEndtime(), timeformat));


            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            showMedialDialog();
        }

    }

    @Override
    public void getPicturesInfoSuccess() {

    }

    @Override
    public void getPicturesInfoFaild() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            //System.out.println("按下了back键   onKeyDown()");
            finish();
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }

    public PagerAdapter pagerAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return customCameraManager.getMediaFiles().size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(final ViewGroup container, final int position) {
            final PhotoView view = new PhotoView(BaseMedialActivity.this);
            view.enable();

            view.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));

            MediaFile item = customCameraManager.getMediaFiles().get(position);
            FileManager myFlieManager = new FileManager();
            Log.i("MedialDataActivity","多媒体文件是否存在:"+myFlieManager.fileIsExists(cachePath+item.getFileName()));
            if (!myFlieManager.fileIsExists(cachePath+item.getFileName())) {

                item.fetchPreview(new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {

                        runOnUiThread(new Runnable() {
                            public void run() {
                                view.setImageBitmap(customCameraManager.getMediaFiles().get(position).getPreview());
                                view.setScaleType(ImageView.ScaleType.FIT_XY);
                            }
                        });


                    }
                });
            }else {
                Glide.with(BaseMedialActivity.this)
                        .load(cachePath+item.getFileName())
                        //.placeholder(R.drawable.loading)
                        .into(new SimpleTarget<GlideDrawable>() {
                            @Override
                            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                                view.setImageDrawable(resource);
                                view.setScaleType(ImageView.ScaleType.FIT_XY);
                            }
                        });
            }
//                    Bitmap bitmap=ImageLoaderUtils.ImageLoader(context, url.get(position), view);
            //  ImageLoaderUtils.ImageLoader(context,imgsList[position], view);
//                    view.setImageResource(imgsId[position]);
            //设置为被选择的图片
//                    mPager.setCurrentItem(pos);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    };

//    private AlertDialog.Builder builder2;

    private void showMedialDialog() {
       DialogManager.showSingleButton(this,R.drawable.logo,"提示","多媒体模块不可用，请检查飞机连接状况!","确定",false,new DialogInterface.OnClickListener(){
           @Override
           public void onClick(DialogInterface dialog, int which) {
               //ToDo: 你想做的事情
               dialog.dismiss();
               finish();

           }
       });
//        builder2 = new AlertDialog.Builder(this).setIcon(R.mipmap.ic_launcher).setTitle("提示")
//                .setMessage("多媒体模块不可用，请检查飞机连接状况!").setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                    }
//                });
//        builder2.create().show();
    }

}
