package com.sumugu.liubo.lc.xdemo;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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

    int mLastY = 0;
    int mLastX = 0;


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        boolean result= super.dispatchTouchEvent(ev);
//        Log.d("dispatchTest","MyListView dispatchtouchevent:"+",action:"+ev.getActionMasked());
//        return result;

//        int x = (int) ev.getX();
//        int y = (int) ev.getY();
//        switch (ev.getActionMasked()) {
//            case MotionEvent.ACTION_DOWN:
//                mLastX = x;
//                mLastY = y;
//                break;
//            case MotionEvent.ACTION_MOVE:
//                int deltaX = x - mLastX;
//                int deltaY = y - mLastY;
//
//                if (deltaY > 0) {
//                    //向下拉
//                    if (getFirstVisiblePosition() == 0) {
//                        //达到最顶部
//                        View view = getChildAt(0);
//                        if (view != null && view.getTop() == 0) {
//                            Log.d("MyListView.scrollchange", "at the top of list");
//                            return true;
//                        }
//                    }
//                } else if (deltaY < 0) {
//                    //向上
//                    //达到最底部
//                    View view = getChildAt(getChildCount() - 1);
//                    if (view != null && (view.getTop() + view.getHeight() == getHeight())) {
//                        Log.d("MyListView.scrollchange", "at the botom of list" + view.getTop() + "," + view.getHeight());
//                        return true;
//                    }
//                }
//                mLastY=y;
//                mLastX=x;
//                break;
//        }
       /* if (getFirstVisiblePosition() == 0) {
            //达到最顶部
            View view = getChildAt(0);
            if (view != null && view.getTop() == 0) {
                Log.d("MyListView.scrollchange", "at the top of list");
                return false;
            }
        } else if (getLastVisiblePosition() == getCount() - 1) {
            //达到最底部
            View view = getChildAt(getChildCount() - 1);
            if (view != null && (view.getTop() + view.getHeight() == getHeight())) {
                Log.d("MyListView.scrollchange", "at the botom of list" + view.getTop() + "," + view.getHeight());

                getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        }*/

        boolean result = super.dispatchTouchEvent(ev);
        Log.d("dispatchTest","MyListView dispatchtouchevent:super."+"action:"+ev.getActionMasked()+"result:"+result);
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //

//        int x = (int) ev.getX();
//        int y = (int) ev.getY();
//        switch (ev.getActionMasked()) {
//            case MotionEvent.ACTION_DOWN:
//                mLastX = x;
//                mLastY = y;
//                break;
//            case MotionEvent.ACTION_MOVE:
//                int deltaX = x - mLastX;
//                int deltaY = y - mLastY;
//
//                if (deltaY > 0) {
//                    //向下拉
//                    if (getFirstVisiblePosition() == 0) {
//                        //达到最顶部
//                        View view = getChildAt(0);
//                        if (view != null && view.getTop() == 0) {
//                            Log.d("MyListView.scrollchange", "at the top of list");
//                            return false;
//                        }
//                    }
//                } else if (deltaY < 0) {
//                    //向上
//                    //达到最底部
//                    View view = getChildAt(getChildCount() - 1);
//                    if (view != null && (view.getTop() + view.getHeight() == getHeight())) {
//                        Log.d("MyListView.scrollchange", "at the botom of list" + view.getTop() + "," + view.getHeight());
//                        return false;
//                    }
//                }
//                mLastY=y;
//                mLastX=x;
//                break;
//        }
//        //
        boolean result = super.onTouchEvent(ev);
        Log.d("dispatchTest","MyListView ontouchevent:super."+"action:"+ev.getActionMasked()+"result:"+result);
        return result;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        boolean result = super.onInterceptTouchEvent(ev);
        Log.d("dispatchTest","MyListView onInterceptTouchEvent:super."+"action:"+ev.getActionMasked()+"result:"+result);
        return result;
    }

    int mLastInterceptX = 0;
    int mLastInterceptY = 0;

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        /*if (getFirstVisiblePosition() == 0) {
            //达到最顶部
            View view = getChildAt(0);
            if (view != null && view.getTop() == 0) {
                Log.d("MyListView.scrollchange", "at the top of list");

                //要求Parent进行事件拦截
                getParent().requestDisallowInterceptTouchEvent(false);
            }
        } else*/ /*if (getLastVisiblePosition() == getCount() - 1) {
            //达到最底部
            View view = getChildAt(getChildCount() - 1);
            if (view != null && (view.getTop() + view.getHeight() == getHeight())) {
                Log.d("MyListView.scrollchange", "at the botom of list" + view.getTop() + "," + view.getHeight());
                //要求Parent进行事件拦截
                getParent().requestDisallowInterceptTouchEvent(false);
            }
        }*/

    }

    //    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        int intercept = 0;
//        int x = (int) ev.getX();
//        int y = (int) ev.getY();
//        switch (ev.getActionMasked()) {
//            case MotionEvent.ACTION_DOWN:
//                mLastInterceptY=y;
//                mLastInterceptX=x;
//                mLastX=x;
//                mLastY=y;
//                intercept = 0;
//                break;
//            case MotionEvent.ACTION_MOVE:
//                int deltaX = x - mLastInterceptX;
//                int deltaY = y - mLastInterceptY;
//                if (Math.abs(deltaY) < Math.abs(deltaX)) {
//                    intercept = 0;
//                }
//                else {
//                    Log.d("MyListViewInter", "intercept");
//
//                    intercept = 1;
//                }
//                break;
//
//            case MotionEvent.ACTION_UP:
//                mLastInterceptX = 0;
//                mLastInterceptY = 0;
//                intercept = 0;
//                break;
//            default:
//                intercept = 0;
//                break;
//
//        }
//        return intercept == 1;
//    }
    //    int mLastXdis,mLastYdis;
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        int x=(int)ev.getX();
//        int y =(int)ev.getY();
//        switch (ev.getActionMasked()){
//            case MotionEvent.ACTION_DOWN:
//                getParent().requestDisallowInterceptTouchEvent(true);
//                break;
//            case MotionEvent.ACTION_MOVE:
//                int deltaX=x-mLastXdis;
//                int deltaY=y-mLastYdis;
//                if(Math.abs(deltaX)>Math.abs(deltaY)){
//                    getParent().requestDisallowInterceptTouchEvent(false);
//                }
//                break;
//            case MotionEvent.ACTION_UP:
//                break;
//            default:
//                break;
//        }
//        mLastXdis=x;
//        mLastYdis=y;
//        return super.dispatchTouchEvent(ev);
//    }
}