package com.sumugu.liubo.lc.xdemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import com.sumugu.liubo.lc.R;

public class Xdemo extends Activity {

    MyScrollView myScrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xdemo02);

        myScrollView=(MyScrollView)findViewById(R.id.customScroller);
    }

    int mLastX,mLastY;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int x=(int)ev.getX();
        int y=(int)ev.getY();
        switch (ev.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                int delatX = x-mLastX;
                int delatY=y-mLastY;
                if(Math.abs(delatY)<Math.abs(delatX)){
                     return onTouchEvent(ev);
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        mLastX=x;
        mLastY=y;
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x=(int)event.getX();
        int y=(int)event.getY();
        switch (event.getActionMasked()){

            case MotionEvent.ACTION_MOVE:
                int deltaX = x-mLastX;
                myScrollView.setTranslationX(deltaX);
                Log.d("xdemo","action_move:"+deltaX);
                break;
            default:
                break;
        }
        return true;
    }
}
