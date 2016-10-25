package com.sumugu.liubo.lc.xdemo;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Adapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.sumugu.liubo.lc.R;
import com.sumugu.liubo.lc.utils.DisplayUtil;
import com.sumugu.liubo.lc.xdemo.data.MyCursorAdapter;
import com.sumugu.liubo.lc.xdemo.data.MyLoaderCallback;

public class Xdemo extends Activity {

    MyScrollView myScrollView;
    MyCustomItem myCustomItem;
    MyListView myListView;
    int maxCustomItemHeight;
    RelativeLayout myCover;

    View container01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xdemo02);

        maxCustomItemHeight = DisplayUtil.dip2px(this, 45f);
        myCustomItem = (MyCustomItem) findViewById(R.id.customItem);
        myCustomItem.setPreparingText("下拉创建新的信条", "松开开始键入内容");

        myScrollView = (MyScrollView) findViewById(R.id.customScroller);
        myScrollView.setOnScrollListener(new ScrollListener());
        myScrollView.setOnInterceptTouchListner(new ScrollerIntercepterListner());

        myCover = (RelativeLayout) findViewById(R.id.layer_cover);
        myCover.setOnClickListener(coverOnClickListener);


        findViewById(R.id.buttonTest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Xdemo.this, "button test", Toast.LENGTH_SHORT).show();
            }
        });


        int loaderId = 5;
        final MyCursorAdapter myCursorAdapter = new MyCursorAdapter(this, null, 0);
        MyLoaderCallback myLoaderCallback = new MyLoaderCallback(this, myCursorAdapter, loaderId);

        myListView = (MyListView) findViewById(R.id.myListView);
        myListView.setAdapter(myCursorAdapter);

        getLoaderManager().initLoader(loaderId, null, myLoaderCallback);

        container01 = findViewById(R.id.container02);
        myListView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.d("ViewTree", "onGlobalLayout.myListView.height=" + myListView.getHeight() + ",measuredHeight=" + myListView.getMeasuredHeight());
                //set listview's parent height requestlayout
                Log.d("ViewTree", "onGlobalLayout.container01.height=" + container01.getHeight() + ",measuredHeight=" + container01.getMeasuredHeight());
                container01.getLayoutParams().height = myListView.getHeight();

                Log.d("ViewTree", "onGlobalLayout.myscrollview.height=" + myScrollView.getHeight() + ",measuredHeight=" + myScrollView.getMeasuredHeight());
//                container01.requestLayout();

            }
        });
//        myListView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            @Override
//            public boolean onPreDraw() {
//                Log.d("ViewTree","onPreDraw.myListView.height="+myListView.getHeight()+",measuredHeight="+myListView.getMeasuredHeight());
//                return true;
//            }
//        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        myListView.post(new Runnable() {
            @Override
            public void run() {
                Log.d("onStart", "myListView,height=" + myListView.getMeasuredHeight() + ",width=" + myListView.getMeasuredWidth() + ",count=" + myListView.getCount());
                int totalHeight = 0;
  /*              Adapter adapter = myListView.getAdapter();
                if(adapter.getCount()>0)
                {
                    for(int i=0;i<adapter.getCount();i++) {
                        View item = adapter.getView(i, null, myListView);
                        item.measure(0,0);
                        Log.d("onStart","myListView,item "+i+" height="+item.getMeasuredHeight());
                        totalHeight+=item.getMeasuredHeight();
                    }
                }
                Log.d("onStart","myListView,all items height="+totalHeight);

                myListView.getLayoutParams().height=totalHeight+myListView.getDividerHeight()*(myListView.getCount()-1);*/
//                myListView.requestLayout();

/*                int th=0;
                Log.d("onStart","myListView,all children ="+myListView.getChildCount());
                for(int i=0;i<myListView.getChildCount();i++){
                    View item = myListView.getChildAt(i);
                    th+=item.getHeight();
                    Log.d("onStart","myListView,child "+i+" height"+item.getMeasuredHeight());
                }
                Log.d("onStart","myListView,childs "+th);
                myListView.getLayoutParams().height=th+myListView.getDividerHeight()*(myListView.getCount()-1);
                myListView.requestLayout();*/
                Log.d("onStart", "final myListView,height=" + myListView.getHeight() + ",width=" + myListView.getWidth() + ",count=" + myListView.getCount());
                Log.d("onStart", "final myScrollerView,height=" + myScrollView.getHeight() + "," + myScrollView.getMeasuredHeight());
            }
        });
    }

    private View.OnClickListener coverOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String content = myCustomItem.getText().toString();
            if (content.isEmpty()) {
                myCustomItem.setText("");
                myCustomItem.readyPreparing();

                customItemCloseup(myCustomItem, maxCustomItemHeight);
                myCover.animate().alpha(0).setDuration(500).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        myCover.setVisibility(View.GONE);
                        myCover.setAlpha(1);
                    }
                });
            } else {
                myCustomItem.setText(content);
            }
        }
    };

    boolean callback = false;
    int height = 0;


    private class ScrollListener implements MyScrollView.OnScrollListener {
        @Override
        public void onStart(int index, int start, int downY) {
            Log.d("ScrollListener", "onStart:" + index + "," + start + "," + downY);
        }

        @Override
        public boolean onScroll(int index, int dy, int scrollY) {
            Log.d("ScrollListener", "onScroll:" + index + "," + dy + "," + scrollY);
            if (index != 1)
                return true;

            if (!callback) {
                callback = true;
                height = 0;
            }

            //2016.10.25 AM improved
            boolean canRoll;

            if (dy <= 0 && scrollY >= 0) {
                height += -dy;
                if (height > maxCustomItemHeight) {
                    height = maxCustomItemHeight;
                    canRoll = true;
                } else
                    canRoll = false;
            } else if (dy >= 0 && scrollY <= 0) {
                height += -dy;
                if (height < 0) {
                    height = 0;
                    canRoll = true;
                } else
                    canRoll = false;
            } else {
                canRoll = true;
            }

            if (myCustomItem.getVisibility() == View.GONE)
                myCustomItem.setVisibility(View.VISIBLE);
            myCustomItem.getLayoutParams().height = height;
            myCustomItem.requestLayout();

            return canRoll;
        }

        @Override
        public void onStop(int index, int scrollY, boolean goNext) {
            Log.d("ScrollListener", "onScroll:" + index + "," + scrollY + "," + myCustomItem.getLayoutParams().height);

            int lastHeight = myCustomItem.getLayoutParams().height;
            if (lastHeight > 0 && lastHeight < maxCustomItemHeight) {
                customItemCloseup(myCustomItem, lastHeight);
            } else {
                if (goNext) {
                    myCustomItem.getLayoutParams().height = 0;
                    myCustomItem.requestLayout();
                    myCover.setVisibility(View.GONE);
                } else {
                    if (myCustomItem.getLayoutParams().height == maxCustomItemHeight) {
                        myCustomItem.edit();
                        myCover.setVisibility(View.VISIBLE);
                    }
                }

            }

            height = 0;   //rest to 0
            callback = false;
        }

    }

    private void customItemCloseup(final View custom, final int startHeight) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, startHeight);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = startHeight - (int) valueAnimator.getAnimatedValue();
                custom.getLayoutParams().height = val;
                custom.requestLayout();
            }
        });
        float fraction = (float) startHeight / maxCustomItemHeight;
        valueAnimator.setDuration((int) (fraction * 500));
        valueAnimator.start();
    }

    private class ScrollerIntercepterListner implements MyScrollView.OnInterceptTouchListner {

        @Override
        public boolean intercept(MotionEvent event, int pageIndex, int deltaY) {

            Log.d("ScrollerIntercepter", "listview'height= " + myListView.getHeight() + ",last=" + myListView.getLastVisiblePosition() + "," + myListView.getChildAt(myListView.getChildCount() - 1).getTop());

            if (deltaY > 0) {
                //swiping down
                if (myListView.getFirstVisiblePosition() == 0) {
                    //达到最顶部
                    View view = myListView.getChildAt(0);
                    if (view != null && view.getTop() == 0) {
                        Log.d("MyListView.scrollchange", "at the top of list");
                        //要求Parent进行事件拦截
                        return true;
                    }
                }
            } else {
                //swiping up
                if (myListView.getLastVisiblePosition() == myListView.getCount() - 1) {
                    //达到最底部
                    View view = myListView.getChildAt(myListView.getChildCount() - 1);
                    if (view != null && (view.getTop() + view.getHeight() == myListView.getHeight())) {
                        Log.d("ScrollerIntercepter", "at the botom of list" + view.getTop() + "," + view.getHeight());
                        //要求Parent进行事件拦截
                        return true;
                    }
                }
            }

            return false;
        }

    }

}
