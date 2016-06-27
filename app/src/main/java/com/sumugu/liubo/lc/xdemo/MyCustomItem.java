package com.sumugu.liubo.lc.xdemo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.sumugu.liubo.lc.R;

/**
 * Created by liubo on 16/6/27.
 */
public class MyCustomItem extends FrameLayout {

    public MyCustomItem(Context context) {
        super(context);
        loadLayout(context);
    }

    public MyCustomItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadLayout(context);
    }

    public MyCustomItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadLayout(context);
    }
    private void loadLayout(Context context)
    {
        LayoutInflater.from(context).inflate(R.layout.custom_item,this,true);
    }


}
