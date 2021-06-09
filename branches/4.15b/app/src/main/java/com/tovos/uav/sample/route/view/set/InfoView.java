package com.tovos.uav.sample.route.view.set;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.commonlib.utils.CustomPackageUtils;
import com.tovos.uav.sample.R;
import com.tovos.uav.sample.route.view.listener.ActivityListener;
import com.tovos.uav.sample.route.view.CustomClickUrlSpan;

import androidx.annotation.Nullable;

public class InfoView extends LinearLayout {

    public TextView  info_sure, info_tw;
    RelativeLayout info_back_rl;
    private ActivityListener listener;

    public void setActivityListener(ActivityListener listener){
        this.listener = listener;
    }

    public InfoView(Context context) {
        super(context);
        initInfoView(context);
    }

    public InfoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initInfoView(context);
    }

    public InfoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initInfoView(context);
    }

    public InfoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initInfoView(context);
    }


    /**
     * 初始化版本信息
     */
    public void initInfoView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.info_set_layout, this);
        info_back_rl = (RelativeLayout) findViewById(R.id.info_back_rl);
        info_sure = (TextView) findViewById(R.id.info_sure);
        info_tw = (TextView) findViewById(R.id.info_tw);
        info_back_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.backToMenuView(InfoView.this);
            }
        });

        info_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        String info = "软件名称: " + CustomPackageUtils.getAppName(getContext())
                + "\n" + getResources().getString(R.string.bb_info) + " " + CustomPackageUtils.getVersionName(getContext()) +
                "\n公司名称: 北京拓维思科技有限公司"
                + "\n" + getResources().getString(R.string.web_url) + " " + Html.fromHtml("http://www.tovos.net/")
                + "\n\n版权归北京拓维思科技有限公司所有" +
                "\n，并保留所有权利";
        info_tw.setText(info);
        info_tw.setMovementMethod(LinkMovementMethod.getInstance());
        CharSequence text = info_tw.getText();
        if (text instanceof Spannable) {
            int end = text.length();
            Spannable sp = (Spannable) info_tw.getText();
            URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
            SpannableStringBuilder style = new SpannableStringBuilder(text);
            style.clearSpans();
            for (final URLSpan url : urls) {
                //最主要的一点
                CustomClickUrlSpan myURLSpan = new CustomClickUrlSpan(url.getURL(), new CustomClickUrlSpan.OnLinkClickListener() {
                    @Override
                    public void onLinkClick(View view) {
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");

                        Uri content_url = Uri.parse(url.getURL());
                        intent.setData(content_url);
                        //startActivity(intent);
                    }
                });
                style.setSpan(myURLSpan, sp.getSpanStart(url), sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            info_tw.setText(style);
        }

    }
}
