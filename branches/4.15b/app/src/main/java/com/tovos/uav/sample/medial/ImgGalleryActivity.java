package com.tovos.uav.sample.medial;

import android.content.Context;
import android.os.Bundle;
import android.view.Window;

import com.example.djilib.dji.component.camera.CustomCameraManager;
import com.tovos.uav.sample.R;
import com.tovos.uav.sample.databean.TaskBean;
import com.example.commonlib.utils.ToastUtils;
import com.tovos.uav.sample.medial.view.RotateDownPageTransformer;

import androidx.viewpager.widget.ViewPager;

public class ImgGalleryActivity extends BaseMedialActivity {
    private ViewPager mPager;
    private Context context;
    private int pos;//传过来的 点击图片位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pic_big_layout);
        if (getIntent().getExtras().get("data")!=null){
            customCameraManager =new CustomCameraManager(this,this);
            taskBean = (TaskBean)getIntent().getExtras().get("data");
            pos = getIntent().getExtras().getInt("pos");
            mPager=(ViewPager)findViewById(R.id.pager);
            //设置图片之间的距离
//          mPager.setPageMargin((int) (getResources().getDisplayMetrics().density * 15));
            mPager.setAdapter(pagerAdapter);
            //根据传过来的pos viewpager中显示第pos张图片

            mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            init();
        }

    }

    @Override
    public void getPicturesInfoSuccess() {
        if (customCameraManager.getMediaFiles().size()>0){
            ToastUtils.setResultToToast("获取照片信息成功，照片数量:"+ customCameraManager.getMediaFiles().size());
            runOnUiThread(new Runnable() {
                public void run() {
                    pagerAdapter.notifyDataSetChanged();
                    mPager.setCurrentItem(pos);
                    //viewpager 的切换动画  RotateDownPageTransformer是自定义的
                    mPager.setPageTransformer(true,new RotateDownPageTransformer());
                }
            });
        }else {
            ToastUtils.setResultToToast("照片数量为0");
        }
    }

}
