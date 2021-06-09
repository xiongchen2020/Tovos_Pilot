package com.tovos.uav.sample.route.view.set;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tovos.uav.sample.R;
import com.tovos.uav.sample.route.view.listener.ActivityListener;

import androidx.annotation.Nullable;

public class ServerView extends LinearLayout {
    private ActivityListener listener;
    RelativeLayout server_back_rl;
    public EditText server_ip;
    TextView server_sure;

    public void setActivityListener(ActivityListener listener){
        this.listener = listener;
    }

    public ServerView(Context context) {
        super(context);
        initView(context);
    }

    public ServerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ServerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public ServerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context){
        LayoutInflater.from(context).inflate(R.layout.server_set_layout, this);
        server_back_rl = (RelativeLayout) findViewById(R.id.server_back_rl);
        server_sure = (TextView) findViewById(R.id.server_sure);
        server_ip = (EditText) findViewById(R.id.server_ip);

        server_back_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.backToMenuView(ServerView.this);
            }
        });
    }
}
