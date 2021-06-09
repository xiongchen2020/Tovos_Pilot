package com.tovos.uav.sample.route.view.djiview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import dji.thirdparty.io.reactivex.disposables.CompositeDisposable;
import dji.thirdparty.io.reactivex.disposables.Disposable;

public class BaseView extends RelativeLayout {

    private CompositeDisposable reactionDisposables = new CompositeDisposable();
    public BaseView(Context context) {
        super(context);
    }

    public BaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BaseView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void onUnsubscribe() {
        if (reactionDisposables != null && !reactionDisposables.isDisposed()){
            reactionDisposables.dispose();

        }
    }


    public void addSubscription(Disposable subscriber) {
        if (reactionDisposables == null) {
            reactionDisposables = new CompositeDisposable();

        }
        reactionDisposables.add(subscriber);
    }
}
