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

        maxCustomItemHeight = DisplayUtil.dip2px(this,45f);
        myCustomItem=(MyCustomItem)findViewById(R.id.customItem);
        myCustomItem.setPreparingText("下拉创建新的信条","松开开始键入内容");

        myScrollView=(MyScrollView)findViewById(R.id.customScroller);
        myScrollView.setOnBeforeScrollListener(new BeforeScrollListener());
        myScrollView.setOnReleaseScrollListener(new ReleaseScrollListener());

    }

    private class BeforeScrollListener implements MyScrollView.OnBeforeScrollListener
    {
        @Override
        public boolean scrollY(int dy) {
            Log.d("BeforeScrollListener","call:"+dy);
            boolean canTrans;
            int transHeight;

            if(dy>maxCustomItemHeight){
                transHeight=maxCustomItemHeight;
                canTrans=false;
            }else{
                if(dy<0)
                    transHeight=0;
                else
                    transHeight=dy;
                canTrans=true;
            }
            myCustomItem.setVisibility(View.VISIBLE);
            myCustomItem.getLayoutParams().height=transHeight;
            myCustomItem.requestLayout();

            return canTrans;
        }
    }

    private class ReleaseScrollListener implements MyScrollView.OnReleaseScrollListener
    {
        @Override
        public boolean stay(int valY) {
            if(valY>maxCustomItemHeight)
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
            if(valY==0)
                valY=maxCustomItemHeight;

           final int maxY= valY;

            ValueAnimator valueAnimator = ValueAnimator.ofInt(0, maxY);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {

                    myCustomItem.getLayoutParams().height= maxY - (int) valueAnimator.getAnimatedValue();;
                    myCustomItem.requestLayout();
                }
            });
            valueAnimator.setDuration(maxY/maxCustomItemHeight*1000);
            valueAnimator.start();
        }
    }

}
