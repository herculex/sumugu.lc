package com.sumugu.liubo.lc.xdemo;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.sumugu.liubo.lc.R;
import com.sumugu.liubo.lc.utils.DisplayUtil;

public class Xdemo extends Activity {

    MyScrollView myScrollView;
    MyCustomItem myCustomItem;
    int maxCustomItemHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xdemo02);

        maxCustomItemHeight = DisplayUtil.dip2px(this, 45f);
        myCustomItem = (MyCustomItem) findViewById(R.id.customItem);
        myCustomItem.setPreparingText("下拉创建新的信条", "松开开始键入内容");

        myScrollView = (MyScrollView) findViewById(R.id.customScroller);
        myScrollView.setOnBeforeScrollListener(new BeforeScrollListener());
        myScrollView.setOnReleaseScrollListener(new ReleaseScrollListener());

    }

    boolean callback = false;
    int height = 0;


    private class BeforeScrollListener implements MyScrollView.OnBeforeScrollListener {
        @Override
        public boolean scrollY(int index, int dy, int scrollY) {
            Log.d("BeforeScrollListener", "call:" + index + "," + dy + "," + scrollY);
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
    }

    private class ReleaseScrollListener implements MyScrollView.OnReleaseScrollListener {
        @Override
        public boolean stay(int valY) {
            if (valY > maxCustomItemHeight)
                return false;
            else
                customItemCloseup(valY);
            return true;
        }

        @Override
        public boolean leave(int valY) {
            return false;
        }

        private void customItemCloseup(int valY) {
            if (valY == 0)
                valY = maxCustomItemHeight;

            final int maxY = valY;

            ValueAnimator valueAnimator = ValueAnimator.ofInt(0, maxY);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {

                    myCustomItem.getLayoutParams().height = maxY - (int) valueAnimator.getAnimatedValue();
                    ;
                    myCustomItem.requestLayout();
                }
            });
            valueAnimator.setDuration(maxY / maxCustomItemHeight * 1000);
            valueAnimator.start();
        }
    }

}
