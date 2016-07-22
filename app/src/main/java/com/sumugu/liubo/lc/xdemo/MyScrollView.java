package com.sumugu.liubo.lc.xdemo;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Scroller;

/**
 * Created by liubo on 16/7/22.
 */
public class MyScrollView extends ViewGroup {

    private static final String TAG = MyScrollView.class.getSimpleName();
    private int mScreenHeight;
    private Scroller mScroller;

    public MyScrollView(Context context) {
        super(context);
        initView(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context)
    {
        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        mScreenHeight = displayMetrics.heightPixels;
        mScroller  = new Scroller(context);
        Log.d(TAG,"screenHeight:"+mScreenHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        int childCount = getChildCount();

        MarginLayoutParams mlp = (MarginLayoutParams)getLayoutParams();//Margin留空？
        mlp.height=mScreenHeight*childCount;
        setLayoutParams(mlp);

        //编排每个child的布局layout位置
        for(int i=0;i<childCount;i++){
            View child=getChildAt(i);
            if(child.getVisibility()!=GONE){
                child.layout(left,i*mScreenHeight,
                        right,(i+1)*mScreenHeight);
            }
        }
        Log.d(TAG,"onLayout called.");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();

        //需要测量每个child，不然child不会被绘制出来！
        for(int i=0;i<childCount;i++){
            measureChild(getChildAt(i),widthMeasureSpec,heightMeasureSpec);
        }
        Log.d(TAG,"onMeasure called.");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
