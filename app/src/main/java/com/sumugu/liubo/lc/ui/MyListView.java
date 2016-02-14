package com.sumugu.liubo.lc.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ListView;

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
    @Override
    public boolean onTouchEvent(MotionEvent ev) {

//        Log.d(TAG,"LV_begingSwiped:"+String.valueOf(beingSwiped)+";TranY="+String.valueOf(getTranslationY()));
//        if(beingSwiped && getTranslationY()<0)
//        {
//            beingSwiped=false;
//            return super.onTouchEvent(ev);
//        }
        if(getChildAt(0).getTop()==getFirstVisiblePosition())
        {
            Log.d(TAG,String.valueOf(getTranslationY())+";LV_Abort_Handle,pass to up");
//            beingSwiped=true;
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
