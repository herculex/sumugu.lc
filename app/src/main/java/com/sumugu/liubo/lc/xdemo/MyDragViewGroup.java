package com.sumugu.liubo.lc.xdemo;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.Date;

/**
 * Created by liubo on 16/7/27.
 */
public class MyDragViewGroup extends FrameLayout {
    public MyDragViewGroup(Context context) {
        super(context);
        initView();
    }

    public MyDragViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public MyDragViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private ViewDragHelper mViewDragHelpher;
    private View mMenuView,mMainView;
    private int mWidth;



    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mMainView = getChildAt(1);
        mMenuView = getChildAt(0);
////        mMainView.setOnClickListener(new OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                Toast.makeText(getContext(), "hello,world.", Toast.LENGTH_SHORT).show();
////            }
////        });
//        mMainView.setLongClickable(false);
//        mMainView.setOnTouchListener(new OnTouchListener() {
//            long start;
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                switch (motionEvent.getActionMasked()){
//                    case MotionEvent.ACTION_DOWN:
//                        start=new Date().getTime();
//                        return true;
//                    case MotionEvent.ACTION_MOVE:
//                        if((new Date().getTime()-start)<1000) {
//                            requestDisallowInterceptTouchEvent(false);
//                            start=new Date().getTime();
//                            return false;
//                        }
//                        else {
//                            start=new Date().getTime();
//                            return true;
//                        }
//                    case MotionEvent.ACTION_UP:
//                        long end = new Date().getTime()-start;
//                        if(end <1000)
//                            Toast.makeText(getContext(), "hello,world.", Toast.LENGTH_SHORT).show();
//                        return true;
//                }
//                return false;
//            }
//        });

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth=mMenuView.getMeasuredWidth();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mViewDragHelpher.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelpher.processTouchEvent(event);
        return true;
    }

    private void initView(){
        mViewDragHelpher = ViewDragHelper.create(this, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                //如果当前触摸的child是mMainView时候开始检测
                return mMainView==child;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                return 0;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                return left;
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                super.onViewReleased(releasedChild, xvel, yvel);
                Log.d("MyDrapViewGroup","xvel="+xvel+",yvel="+yvel);
                Log.d("MyDrapViewGroup","left="+mMainView.getLeft());
                //
                if(mMainView.getLeft()<300){
                    mViewDragHelpher.smoothSlideViewTo(mMainView,0,0);
                    ViewCompat.postInvalidateOnAnimation(MyDragViewGroup.this);
                }
                else{
                    mViewDragHelpher.smoothSlideViewTo(mMainView,300,0);
                    ViewCompat.postInvalidateOnAnimation(MyDragViewGroup.this);
//                    mMainView.setTranslationX(300);
                }

                Log.d("MyDrapViewGroup","transX="+mMainView.getTranslationX());
            }
        });


    }

    @Override
    public void computeScroll() {
        if(mViewDragHelpher.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }
}
