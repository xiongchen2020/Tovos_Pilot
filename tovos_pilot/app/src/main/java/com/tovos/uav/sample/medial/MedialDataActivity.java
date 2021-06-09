package com.tovos.uav.sample.medial;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import com.example.commonlib.utils.CustomTimeUtils;
import com.example.commonlib.utils.DialogManager;

import com.example.djilib.dji.component.camera.CustomCameraManager;

import com.tovos.uav.sample.MApplication;
import com.tovos.uav.sample.R;

import com.tovos.uav.sample.databean.TaskBean;
import com.tovos.uav.sample.route.util.XMLUtil;
import com.tovos.uav.sample.route.recylerviewabout.GridSpacingItemDecoration;

import com.example.commonlib.utils.DialogUtils;
import com.example.commonlib.utils.FileManager;

import com.example.commonlib.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dji.common.camera.SettingsDefinitions;
import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.sdk.camera.Camera;
import dji.sdk.media.DownloadListener;
import dji.sdk.media.MediaFile;


public class MedialDataActivity extends BaseMedialActivity implements OnMedialItemClickListener {

    private RecyclerView recyclerView;
    private GridAdapter gridAdapter;
    private ActionBar mActionBar;
    private List<Camera> cameraList = new ArrayList<>();
    private List<String> name = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_layout);
        EventBus.getDefault().register(this);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);

        if (getIntent().getExtras().get("data")!=null){
            taskBean = (TaskBean)getIntent().getExtras().get("data");
        }

        createFlie();
        customCameraManager =new CustomCameraManager(this,this);
        cameraList = customCameraManager.getCamera();
        if (cameraList!=null&&cameraList.size()>0){
            for (int i = 0; i < cameraList.size(); i++) {
                name.add(cameraList.get(i).getDisplayName());
            }
        }

        recyclerView.setLayoutManager(new GridLayoutManager(MedialDataActivity.this, 3));
        int spanCount = 3; // 3 columns
        int spacing = 25; // 50px
        boolean includeEdge = false;
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));

        gridAdapter = new GridAdapter(MedialDataActivity.this);
        recyclerView.setAdapter(gridAdapter);
        customCameraManager.getCameraInstance(0);

        initActionBar();
        init();

    }

    public void initActionBar() {
        mActionBar = this.getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); //Enable自定义的View
            mActionBar.setCustomView(R.layout.actionbar_medialdata);//设置自定义的布局：actionbar_custom
            Spinner spinner = findViewById(R.id.spinner);

            if (name!=null&&name.size()>0){
                spinner.setVisibility(View.VISIBLE);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,name);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (cameraList != null && cameraList.size() > 0 && cameraList.size() > position) {
                            customCameraManager.getCameraInstance(position);
                                try {
                                    customCameraManager.getMediaPath(CustomTimeUtils.longToString(taskBean.getStatrtime(), timeformat), CustomTimeUtils.longToString(taskBean.getEndtime(), timeformat));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }else{
                spinner.setVisibility(View.INVISIBLE);
            }

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (customCameraManager !=null){
            customCameraManager.setCameraModel(SettingsDefinitions.CameraMode.SHOOT_PHOTO);
        }
    }

    @Override
    public void getPicturesInfoSuccess() {
        if (customCameraManager.getMediaFiles().size()>0){
            ToastUtils.setResultToToast("获取多媒体信息成功，多媒体数量:"+ customCameraManager.getMediaFiles().size());

                runOnUiThread(new Runnable() {
                    public void run() {
                        gridAdapter.setData(customCameraManager.getMediaFiles());
                        gridAdapter.notifyDataSetChanged();
                    }
                });
            File file = new File(path+"/"+taskBean.getName()+".txt");
            if (!file.exists()) {
                List<String> djilist = new ArrayList<>();
                for (int i = 0; i < customCameraManager.getMediaFiles().size(); i++) {
                    djilist.add(customCameraManager.getMediaFiles().get(i).getFileName());
                }
                taskBean.setDjiPicList(djilist);
                XMLUtil.TaskToXML(taskBean);
                createTxt();
            }

        }else {
            ToastUtils.setResultToToast("多媒体数量为0");
        }
    }

    @Override
    public void getPicturesInfoFaild() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gridAdapter.notifyDataSetChanged();
            }
        });

    }

    public void createTxt(){
        for (int i=0;i<taskBean.getDjiPicList().size();i++){
            if (taskBean.getWaypointPicList().size()>i){
                FileManager.writerTxtFile(path,taskBean.getName(),taskBean.getDjiPicList().get(i),taskBean.getWaypointPicList().get(i));
            }
        }
    }
    @Override
    public void onMedialItemClick(int Position) {

        Intent intent = new Intent(MedialDataActivity.this,ImgGalleryActivity.class);
        intent.putExtra("data",taskBean);
        intent.putExtra("pos",Position);
        startActivity(intent);
    }



    class GridAdapter extends RecyclerView.Adapter<GridAdapter.Holder> {

        private List<MediaFile> list = new ArrayList<>();
        private OnMedialItemClickListener listener;
        public GridAdapter(OnMedialItemClickListener listener){
            this.listener = listener;
        }
        public GridAdapter(List<MediaFile> list,OnMedialItemClickListener listener){
            this.list = list;
            this.listener = listener;
        }
        public void setData(List<MediaFile> list){
            this.list = list;
        }

        @Override
        public GridAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new GridAdapter.Holder(LayoutInflater.from(MedialDataActivity.this).inflate(R.layout.image_item_layout, parent, false));
        }

        @Override
        public void onBindViewHolder(final GridAdapter.Holder holder, final int position) {

            final MediaFile item = list.get(position);
            holder.date.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
            holder.name.setText(item.getFileName());
            holder.date.setTag(position);
            String type = item.getMediaType().getDisplayName();
            FileManager myFlieManager = new FileManager();
            Log.i("MedialDataActivity","多媒体文件是否存在:"+myFlieManager.fileIsExists(cachePath+item.getFileName()));
            if (!myFlieManager.fileIsExists(cachePath+item.getFileName())) {
                item.fetchPreview(new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError==null){
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    if ((int)holder.date.getTag()==position){
                                        holder.date.setImageBitmap(item.getPreview());
                                        Log.i("MedialDataActivity"," 下载多媒体:"+position+"/总媒体数："+taskBean.getWaypointPicList().size());
                                        FileManager fileManager = new FileManager();
                                        String itempath = cachePath+item.getFileName();
                                        fileManager.saveBitmap(item.getPreview(), itempath);
                                    }
                                }
                            });

                        }else {
                            ToastUtils.setResultToToast("多媒体缓存失败:"+djiError.getDescription());
                        }
                    }
                });

            }else {

                Glide.with(MedialDataActivity.this)
                        .load(cachePath+item.getFileName())
                        //.placeholder(R.drawable.loading)
                        .into(new SimpleTarget<GlideDrawable>() {
                            @Override
                            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                                holder.date.setImageDrawable(resource);
                            }
                        });

            }


            holder.download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!customCameraManager.isSupportDowaload) {
                        ToastUtils.setResultToToast("本设备不支持下载媒体文件");
                        return;
                    }
                    FileManager fileManager = new FileManager();
                    if (fileManager.fileIsExists(medialPath+item.getFileName())) {
                        ToastUtils.setResultToToast("该文件已下载");


                    }else {
                        ProgressDialog dialog = DialogManager.showProgressDialog(MedialDataActivity.this, "下载", false, "取消", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               item.stopFetchingFileData(null);
                               dialog.dismiss();
                           }
                       });
                        String s=item.getFileName();
                        String[] strings = s.split("\\.");

                        item.fetchFileData(new File(medialPath), strings[0], new DownloadListener<String>() {

                            @Override
                            public void onStart() {

                                Log.i("MedialDataActivity","onStart");
                            }

                            @Override
                            public void onRateUpdate(long l, long l1, long l2) {
                              //  Log.i("MedialDataActivity","onRateUpdate:"+l+"/"+l1+"/"+l2);

                            }


                            @Override
                            public void onRealtimeDataUpdate(byte[] bytes, long l, boolean b) {

                            }

                            @Override
                            public void onProgress(long l, long l1) {
                                Log.i("MedialDataActivity","onProgress:"+l+"/"+l1+"/");
                                if (l!=0){
                                    int pregress = (int) (l1/Double.valueOf(l)*100);
                                    dialog.setProgress(pregress);
                                    if (l==l1){
                                        dialog.dismiss();
                                    }
                                }

                            }

                            @Override
                            public void onSuccess(String s) {
                                Log.i("MedialDataActivity","onSuccess");

                            }


                            @Override
                            public void onFailure(DJIError djiError) {
                                Log.i("MedialDataActivity","onFailure:"+djiError.getDescription());
                                if (djiError.getDescription().equals("Not supported")){
                                    ToastUtils.setResultToToast("该设备不支持下载照片");
                                }else if (djiError.getDescription().equals("Result of media downloading: the client aborted the download")){
                                    ToastUtils.setResultToToast("停止下载");
                                }
                                else {
                                    ToastUtils.setResultToToast("下载失败："+djiError.getDescription());
                                }
                                DialogUtils.dismissWaitingDialog();
                            }
                        });
                    }

                }
            });

            holder.upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!customCameraManager.isSupportDowaload) {
                        ToastUtils.setResultToToast("本设备不支持下载，无法上传图片");
                        return;
                    }

                    FileManager fileManager = new FileManager();
                    if (fileManager.fileIsExists(medialPath+item.getFileName())) {
                        DialogUtils.showWaitingDialog(MedialDataActivity.this,"媒体文件上传中。。。。。");
                        uploadMedial(item);
                    }else {
                        ToastUtils.setResultToToast("请先下载文件");

                    }
                }
            });
            holder.rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onMedialItemClick(position);
                }
            });

        }

        private void uploadMedial(MediaFile item){
            String type = item.getMediaType().getDisplayName();
            String uploadType = "image/jpeg";
            String medialType = "flightPic";
            String message = "uploadImage";

            if( MediaFile.MediaType.MP4.getDisplayName().equals(type)|| MediaFile.MediaType.MOV.getDisplayName().equals(type)){
                if (MediaFile.MediaType.MP4.getDisplayName().equals(type)){
                    uploadType = "video/mp4";
                }else if(MediaFile.MediaType.MOV.getDisplayName().equals(type)){
                    uploadType = "video/quicktime";
                }
                medialType = "flightVideo";
                message = "uploadVideo";
            }
            TreeMap<String,Object> treeMap = new TreeMap<>();
            treeMap.put("medailtype",medialType);
            treeMap.put("message",message);
            treeMap.put("type",uploadType);
            treeMap.put("path",medialPath+item.getFileName());
            treeMap.put("name",item.getFileName());
            EventBus.getDefault().postSticky(treeMap);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class Holder extends RecyclerView.ViewHolder {

            private LinearLayout rl;
            private ImageView date;
            private TextView name;

            private ImageView upload;
            private ImageView download;
            public Holder(View itemView) {
                super(itemView);
                rl = (LinearLayout) itemView.findViewById(R.id.rl);
                date = (ImageView)itemView.findViewById(R.id.pic);
                name = (TextView)itemView.findViewById(R.id.name);
                upload = itemView.findViewById(R.id.bt_upload);
                download = itemView.findViewById(R.id.bt_download);


            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dismissDialog(String  event) {
        /* Do something */
        if ("".equals(event)) {
           DialogUtils.dismissWaitingDialog();
        }
    }
}
