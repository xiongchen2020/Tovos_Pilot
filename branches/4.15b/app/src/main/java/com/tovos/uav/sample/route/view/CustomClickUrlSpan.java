package com.tovos.uav.sample.route.view;

import android.text.style.ClickableSpan;
import android.view.View;

public class CustomClickUrlSpan extends ClickableSpan {
    private String url;
    private OnLinkClickListener mListener;

    public CustomClickUrlSpan(String url, OnLinkClickListener listener) {
        this.url=url;
        this.mListener=listener;
    }

    @Override
    public void onClick(View widget) {
        if (mListener!=null){
            mListener.onLinkClick(widget);
        }
    }

    /**
     * 跳转链接接口
     */
    public interface OnLinkClickListener{
        void onLinkClick(View view);
    }
}