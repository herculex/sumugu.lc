package com.sumugu.liubo.lc.xdemo;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
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


    public interface OnScrollListener {
        void onStart(int index, int start, int downY);    //current pager index,start scrollY ,downY point

        boolean onScroll(int index, int dy, int scrollY);   //current pager index,scrollBy-Y,total scrollY

        void onStop(int index, int scrollY,boolean goNext); //current pager index,total scrollY

    }

    public interface OnInterceptTouchListner {
        boolean intercept(MotionEvent event, int pageIndex, int deltaY);    //event,current pager index,scrollBy-Y(scrolling up or down)
    }

    private static final String TAG = MyScrollView.class.getSimpleName();
    private int mScreenHeight;
    private Scroller mScroller;

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    private OnScrollListener onScrollListener;

    public void setOnInterceptTouchListner(OnInterceptTouchListner onInterceptTouchListner) {
        this.onInterceptTouchListner = onInterceptTouchListner;
    }

    private OnInterceptTouchListner onInterceptTouchListner;

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

    private void initView(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        mScreenHeight = displayMetrics.heightPixels;
        mScroller = new Scroller(context);
        Log.d(TAG, "screenHeight:" + mScreenHeight);
    }

    public int getScreenHeight(){
        return mScreenHeight;
    }

    private int[] childBottoms = new int[3];
    private int[] childTops = new int[3];

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        int childCount = getChildCount();

//        MarginLayoutParams mlp = (MarginLayoutParams) getLayoutParams();//Margin留空？
//        mlp.height = mScreenHeight * childCount;
//        setLayoutParams(mlp);

        int last_bottom = 0;
        int last_top;
        //编排每个child的布局layout位置
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            Log.d(TAG, "onLayout called.child " + i + ",before child's height=" + child.getHeight() + ",measureHeight=" + child.getMeasuredHeight() + ",top=" + child.getTop() + ",bottom=" + child.getBottom());

//            if (child.getVisibility() != View.GONE) {
//                child.layout(left, i * mScreenHeight,
//                        right, (i + 1) * mScreenHeight);
//            }

            //improved.2016.10.20
            if (child.getVisibility() != View.GONE) {

                int h = child.getMeasuredHeight();
                if (h < mScreenHeight)
                    h = mScreenHeight;

                last_top = last_bottom;
                last_bottom = h + last_top;

                child.layout(left, last_top,
                        right, last_bottom);

                childBottoms[i] = last_bottom;
                childTops[i] = last_top;
            }
            Log.d(TAG, "ccc.onLayout called.child " + i + ",after child's height=" + child.getHeight() + ",measureHeight=" + child.getMeasuredHeight() + ",top=" + child.getTop() + ",bottom=" + child.getBottom() + ",[" + childBottoms[i] + "]");
        }

        Log.d(TAG, "onLayout called.");
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        Log.d(TAG, "onMeasure called." + ",height=" + getModeName(MeasureSpec.getMode(heightMeasureSpec)) + "," + MeasureSpec.getSize(heightMeasureSpec));
        Log.d(TAG, "onMeasure called." + ",width=" + getModeName(MeasureSpec.getMode(widthMeasureSpec)) + "," + MeasureSpec.getSize(widthMeasureSpec));

        //需要测量每个child，不然child不会被绘制出来！
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);

            Log.d(TAG, "Child onMeasure called." + i + ",height=" + child.getMeasuredHeight() + ",width=" + child.getMeasuredWidth());
        }
    }

    private String getModeName(int measureSpecMode) {
        switch (measureSpecMode) {
            case MeasureSpec.AT_MOST:
                return "AT_MOST";
            case MeasureSpec.EXACTLY:
                return "EXACTLY";
            default:
                return "UNSPECIFED";
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean result = super.dispatchTouchEvent(ev);
        Log.d("dispatchTest", "MyScrollerView dispatchtouchevent:" + result + ",action:" + ev.getActionMasked());
        return result;
    }

    private int findChildBottom(int scrollY) {
        int butt = 0;
        int lastHeight = 0;
        for (int i = 0; i < childBottoms.length; i++) {
            if (scrollY > (lastHeight + childBottoms[i])) {
                lastHeight += childBottoms[i];
            } else {
                butt = childBottoms[i];
                break;
            }
        }
        //
        return butt;
    }

    private int mLastChildIndex = 0;

    private void setLastChildIndex(int scrollY) {
        int index = 0;
        if (scrollY > 0) {

            for (int i = 0; i < childTops.length; i++) {
                index = i;

                if (childTops[i] > scrollY) {
                    if ((childTops[i] - scrollY) > mScreenHeight / 2)
                        index = i - 1;
                    break;
                }
            }
        }
        mLastChildIndex = index;
    }

    int mLastX;
    int mLastY;
    int mStart;
    int mDownY;
    int mEnd;
    ViewDragHelper mViewDragHelpher;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "MyScrollerView,onTouchEvent called:" + event.getActionMasked());

        int y = (int) event.getY();
        int x = (int) event.getX();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                mLastX = x;
                mStart = getScrollY();
                Log.d(TAG, "ACTION_DOWN,mLast=" + mLastY + ";mStart:getScrollY()=" + mStart);
                break;
            case MotionEvent.ACTION_MOVE:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                int dy = mLastY - y;
                int dx = mLastX - x;
                Log.d(TAG, "ACTION_MOVE,getHeight:=" + getHeight() + ";screeNheight:" + mScreenHeight + ";dy=" + dy + ";getScrollY()=" + getScrollY());
                Log.d(TAG, "ACTION_MOVE,dy=" + dy + ";getScrollY()=" + getScrollY());


                // TODO: 16/10/21 need to be improved below.

                if (onScrollListener == null)
                    scrollBy(0, dy);
                else {
                    if (onScrollListener.onScroll(mLastChildIndex, dy, childTops[mLastChildIndex] - getScrollY())) {
                        scrollBy(0, dy);

                        Log.d("onScrollListener", "action.move.childIndex=" + mLastChildIndex
                                + ",child top=" + childTops[mLastChildIndex] + ",getScrollY=" + getScrollY()
                                + ";deltaY=" + (childTops[mLastChildIndex] - getScrollY()));
                    }
                }

/*                if (onScrollListener == null)
                    scrollBy(0, dy);
                else {
                    if (onScrollListener.onScroll(getScrollY() / mScreenHeight, dy,  getScrollY() % mScreenHeight)) {
                        Log.d(TAG, "1MyScrollerView scrollBy:" + dy);
                        scrollBy(0, dy);

                    }
                    Log.d("onScrollListener", "getScrollY=" + getScrollY() + ";top=" + getTop());

                }*/

                mLastY = y;
                mLastX = x;
                break;

            case MotionEvent.ACTION_UP:

                //// // 16/10/20 improved.

                int sh = mScreenHeight;
                int bt = findChildBottom(getScrollY());
                int endY = getScrollY();
                int deltaY = 0;

                if (endY < 0) {
                    //复位
                    mScroller.startScroll(0, endY, 0, -endY);
                } else {

                    deltaY = bt - endY;
                    if (deltaY > sh) {
                        //大于屏幕，不动
                    } else {
                        //大于屏幕，滑动；最终显示占比大的child。
                        if (deltaY > sh / 2) {
                            mScroller.startScroll(0, endY, 0, -(sh - deltaY));
                        } else {
                            mScroller.startScroll(0, endY, 0, deltaY);
                        }
                    }
                }
                setLastChildIndex(getScrollY());

                Log.d(TAG, "xxx.action_up.sh=" + sh + ",bt=" + bt + ",endY=" + endY + ",deltaY=" + deltaY + ",lastChildIndex=" + mLastChildIndex);

/*                int dScrollY = checkAlignment();    //int dScrollY = getScrollY()-mStart;

                Log.d(TAG, "ACTION_UP,dScrollY=" + dScrollY + ";translationY=" + getTranslationY());*/


/*                if (dScrollY > 0) {
                    if (dScrollY < mScreenHeight / 3) {
                        mScroller.startScroll(0, getScrollY(), 0, -dScrollY);
                    } else {
                        if (getScrollY() > mScreenHeight * (getChildCount() - 1))
                            mScroller.startScroll(0, getScrollY(), 0, -dScrollY);
                        else
                            mScroller.startScroll(0, getScrollY(), 0, mScreenHeight - dScrollY);
                    }
                } else {
                    if (-dScrollY < mScreenHeight / 3) {
                        mScroller.startScroll(0, getScrollY(), 0, -dScrollY);
                    } else {
                        mScroller.startScroll(0, getScrollY(), 0, -mScreenHeight - dScrollY);
                    }
                }
                */
                int currentChildMove = childTops[mLastChildIndex] - endY;
                if (onScrollListener != null)
                    onScrollListener.onStop(0,currentChildMove ,deltaY>sh/2);


                break;
        }
        postInvalidate();
        return true;
    }


/*    private int checkAlignment() {
        int mEnd = getScrollY();

        boolean isUP = ((mEnd - mStart) > 0);
        int lastPrev = mEnd % mScreenHeight;
        int lastNext = mScreenHeight - lastPrev;
        Log.d(TAG, "mStart:" + mStart + ";mEnd:" + mEnd + ";isUP:" + isUP + ";lastPrev=" + lastPrev + ";lastNext=" + lastNext);

        if (isUP)
            return lastPrev;
        else
            return -lastNext;
    }*/

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
//        Log.d(TAG, "computeScroll() called.");
    }

    int mLastXIntercept = 0;
    int mLastYIntercept = 0;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

/*        boolean result = super.onInterceptTouchEvent(ev);
        Log.d(TAG, "MyScrollerView onInterceptTouchEvent.super:" + ev.getActionMasked() + ",result=" + result);
        return result;*/

/*        2016.10.25 get remember and improved.*/
        Log.d(TAG, "MyScrollerView intercept start.");
        int intercept = 0;
        int x = (int) ev.getX();
        int y = (int) ev.getY();

        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                    intercept = 1;
                }
                mLastX = x;
                mLastY = y;
                mLastXIntercept = x;
                mLastYIntercept = y;
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = x - mLastXIntercept;
                int deltaY = y - mLastYIntercept;
                Log.d(TAG, "MyScrollerView deltaY=" + deltaY);
                if (Math.abs(deltaY) < Math.abs(deltaX)) {
                    intercept = 0;
                } else if (onInterceptTouchListner != null) {
                    if (onInterceptTouchListner.intercept(ev, mLastChildIndex, deltaY)) {
                        Log.d(TAG, "MyScrollerView intercept action:" + ev.getActionMasked());
                        intercept = 1;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                mLastYIntercept = mLastXIntercept = 0;
                intercept = 0;
                break;
            default:
                intercept = 0;
                break;
        }

        return intercept == 1;
    }

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
