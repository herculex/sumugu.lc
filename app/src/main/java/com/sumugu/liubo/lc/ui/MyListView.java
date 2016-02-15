package com.sumugu.liubo.lc.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ListView;
import android.widget.TextView;

import com.sumugu.liubo.lc.R;

/**
 * Created by liubo on 16/2/1.
 */
public class MyListView extends ListView{
    final static String TAG = "lc_MyListView";

    public MyListView(Context context) {
        super(context);
    }

    public MyListView(Context context,AttributeSet attributeSet)
    {
        super(context,attributeSet);
    }
    public MyListView(Context context,AttributeSet attributeSet,int defAttri)
    {
        super(context,attributeSet,defAttri);
    }

    boolean beingSwiped=false;
    boolean SwipedFix=false;
    public void setBeingSwiped(boolean flag)
    {
        beingSwiped=flag;
    }
    public void setSwipedFix(boolean flag)
    {
        SwipedFix=flag;
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {


        Log.d(TAG,String.valueOf(beingSwiped)+";"+String.valueOf(SwipedFix)+";LV_Request..");
        if(beingSwiped)
        {
            Log.d(TAG,"goin____beingSwiped.");
            return false;
        }

        if(SwipedFix)
        {
            Log.d(TAG,"goin____swipedFix.");
            SwipedFix=false;
            return super.onTouchEvent(ev);
        }

        if(getChildAt(0).getTop()==0 && getFirstVisiblePosition()==0)
        {
            TextView textView = (TextView)getChildAt(0).findViewById(R.id.text_content);
            Log.d(TAG,(textView.getText())+":"+String.valueOf(getChildAt(0).getTop())+":"+String.valueOf(getFirstVisiblePosition())+";LV_Abort_Handle,pass to up");
            Log.d(TAG,"TranY="+String.valueOf(getTranslationY()));
            return false;
        }


        Log.d(TAG, "LV_Super_OnTouchEvent");
        return super.onTouchEvent(ev);

//        switch (ev.getActionMasked())
//        {
//            case MotionEvent.ACTION_DOWN:
//                Log.d(TAG,"LV____TextView__DOWN");
//                break;
//            case MotionEvent.ACTION_MOVE:
//                Log.d(TAG,"LV____TextView__MOVE");
//                return super.onTouchEvent(ev);
////                break;
//            case MotionEvent.ACTION_CANCEL:
//                Log.d(TAG,"LV____TextView__CANCEL");
//                break;
//            case MotionEvent.ACTION_UP:
//                Log.d(TAG,"LV____TextView__UP");
//                break;
//            default:
//                return false;
//        }
//        return true;
    }
}
