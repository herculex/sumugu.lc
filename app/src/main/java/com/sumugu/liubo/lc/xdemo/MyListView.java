package com.sumugu.liubo.lc.xdemo;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * Created by liubo on 16/8/16.
 */
public class MyListView extends ListView {
    public MyListView(Context context) {
        super(context);
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.d("MyListView", "dispatchTouchEvent:" + ev.getActionMasked());
//        if(ev.getActionMasked()==MotionEvent.ACTION_DOWN) {
//            Log.d("MyListView","super.dispatch down");
//            return super.dispatchTouchEvent(ev);
//        }
//
//        if (ev.getActionMasked() == MotionEvent.ACTION_MOVE)
            if(getFirstVisiblePosition() == 0 && getLastVisiblePosition() == (getChildCount() - 1)){
                Log.d("MyListView","does not dispatch");
                return false;}
            else {
                Log.d("MyListView","super.dispatch move");
                return super.dispatchTouchEvent(ev);
            }
//
//        Log.d("MyListView","super.dispatch");
//        return super.dispatchTouchEvent(ev);
    }
}
