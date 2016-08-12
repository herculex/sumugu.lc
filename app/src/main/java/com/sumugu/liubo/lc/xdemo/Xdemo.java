package com.sumugu.liubo.lc.xdemo;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.sumugu.liubo.lc.R;
import com.sumugu.liubo.lc.utils.DisplayUtil;

public class Xdemo extends Activity {

    MyScrollView myScrollView;
    MyCustomItem myCustomItem;
    int maxCustomItemHeight;
    RelativeLayout myCover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xdemo02);

        maxCustomItemHeight = DisplayUtil.dip2px(this, 45f);
        myCustomItem = (MyCustomItem) findViewById(R.id.customItem);
        myCustomItem.setPreparingText("下拉创建新的信条", "松开开始键入内容");

        myScrollView = (MyScrollView) findViewById(R.id.customScroller);
        myScrollView.setOnScrollListener(new ScrollListener());

        myCover = (RelativeLayout)findViewById(R.id.layer_cover);
//        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)relativeLayout.getLayoutParams();
////        lp.setMargins(0,100,0,0);
//        relativeLayout.setTranslationY(100);
//        Log.d("Xdemo","relation TOP:"+relativeLayout.getTop());
//        relativeLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(Xdemo.this, "hello", Toast.LENGTH_SHORT).show();
//            }
//        });

    }

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
            if(index!=0)
                return true;

            if (!callback) {
                callback = true;
                height = 0;
            }
            boolean canTrans;

            if (dy <= 0 && scrollY <= 0) {
                height += -dy;
                if (height > maxCustomItemHeight) {
                    height = maxCustomItemHeight;
                    canTrans = true;
                } else
                    canTrans = false;
            } else if (dy >= 0 && scrollY >= 0) {
                height += -dy;
                if (height < 0) {
                    height = 0;
                    canTrans = true;
                } else
                    canTrans = false;
            } else {
                canTrans = true;
            }

            if (myCustomItem.getVisibility() == View.GONE)
                myCustomItem.setVisibility(View.VISIBLE);
            myCustomItem.getLayoutParams().height = height;
            myCustomItem.requestLayout();

            return canTrans;
        }

        @Override
        public void onStop(int index, int scrollY) {
            Log.d("ScrollListener", "onScroll:" + index + "," + scrollY + "," + myCustomItem.getLayoutParams().height);

            int lastHeight = myCustomItem.getLayoutParams().height;
            if (lastHeight > 0 && lastHeight < maxCustomItemHeight) {
                customItemCloseup(myCustomItem, lastHeight);
            }
            else {
                if(myCustomItem.getLayoutParams().height==maxCustomItemHeight) {
                    myCustomItem.edit();
                    myCover.setVisibility(View.VISIBLE);
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

}
