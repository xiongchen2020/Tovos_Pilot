package com.tovos.uav.sample.route.custom;

import android.content.Context;
import android.util.AttributeSet;

import com.tovos.uav.sample.R;

import dji.ux.widget.ReturnHomeWidget;

public class CustonReturnHomeWidget extends ReturnHomeWidget {
    public CustonReturnHomeWidget(Context context) {
        super(context);
    }

    public CustonReturnHomeWidget(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public CustonReturnHomeWidget(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void initView(Context var1, AttributeSet var2, int var3) {
        super.initView(var1, var2, var3);
        super.imageBackground.setBackgroundColor(getResources().getColor(R.color.transparent));
    }
}
