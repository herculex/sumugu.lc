package com.sumugu.liubo.lc.xdemo;

import android.animation.ValueAnimator;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sumugu.liubo.lc.R;
import com.sumugu.liubo.lc.utils.DisplayUtil;

public class XdemoActivty extends AppCompatActivity {

    private final static String TAG = XdemoActivty.class.getSimpleName();
    private MyTextView hiddenView;    //// TODO: 16/6/16 扩展这个TextView，自定义MyTextView，观察onSizeChanged和onDraw
    private TextView welcomeView;
    private ViewGroup.LayoutParams hiddenParams, welcomeParams;
    private int maxHiddenHeight;
    private MyCustomItem customItem;
    private ViewGroup.LayoutParams customItemParams;
    private int maxCustomItemHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xdemo);

        customItem = (MyCustomItem) findViewById(R.id.customItem);
        hiddenView = (MyTextView) findViewById(R.id.hiddenView);
        welcomeView = (TextView) findViewById(R.id.welcomeView);

        hiddenParams = hiddenView.getLayoutParams();
        welcomeParams = welcomeView.getLayoutParams();
        maxHiddenHeight = hiddenParams.height;

        customItemParams = customItem.getLayoutParams();
//        maxCustomItemHeight = customItemParams.height;
        maxCustomItemHeight = DisplayUtil.dip2px(this, 45f);
        customItem.setPreparingText("jerk off", "on face");
        customItem.setOnPreparingListener(new MyCustomItem.OnPreparingListener() {

            @Override
            public void end(int index) {
                Log.d("preparingX", "end callback");
                customItem.animate().translationX(-customItem.getWidth()).setDuration(250).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        customItem.animate().translationX(0).setDuration(150).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                customItem.setText("jerk again");
//                                customItem.edit();
                            }
                        });
                    }
                });
            }
        });
        customItem.setOnFinishListener(new MyCustomItem.OnFinishListener() {
            @Override
            public void end(int index) {
                Toast.makeText(XdemoActivty.this, "well,done!", Toast.LENGTH_SHORT).show();
//                toast.setGravity(Gravity.TOP,0,0);
//                toast.show();
            }
        });
        customItem.setOnDeleteListner(new MyCustomItem.OnDeleteListner() {
            @Override
            public void end(int index) {
                LinearLayout parentView = (LinearLayout) findViewById(R.id.container_item);
                parentView.removeView(customItem);

                Toast.makeText(XdemoActivty.this, "remove customItem!!", Toast.LENGTH_SHORT).show();
//                toast.setGravity(Gravity.TOP,0,0);
//                toast.show();
            }
        });
//        prepareToShowup();
        customItemShowup();

//        Toast.makeText(MainActivity.this, "hidden's height:"+hiddenParams.height+";welcome's height:"+welcomeParams.height, Toast.LENGTH_SHORT).show();

        Log.d("MyDemo", "hidden's height:" + hiddenParams.height + ";welcome's height:" + welcomeParams.height);

    }

    private void customItemShowup() {
        int height = maxCustomItemHeight;
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, height);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (int) valueAnimator.getAnimatedValue();
                customItemParams.height = val;
                customItem.setLayoutParams(customItemParams);
            }
        });
        valueAnimator.setDuration(1500);
        valueAnimator.start();
    }

    private void prepareToShowup() {
        int height = maxHiddenHeight;
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, height);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (int) valueAnimator.getAnimatedValue();
                hiddenParams.height = val;
                hiddenView.setLayoutParams(hiddenParams);
            }
        });
        valueAnimator.setDuration(3000);
        valueAnimator.start();
    }

    private float downY;
    private boolean pressed = false;

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//
//        Log.d(TAG,"xdemo.onTouchEvent go.");
//        switch (event.getActionMasked()) {
//            case MotionEvent.ACTION_DOWN:
//                Log.d(TAG,"xdemo.onTouchEvent down.");
//                if (!pressed) {
//                    downY = event.getY();
//                    pressed = true;
//                }
//                break;
//            case MotionEvent.ACTION_MOVE:
//
//                float dist = event.getY() - downY;
//                if (dist < 0)
//                    dist = 0;
//                float absDist = Math.abs(dist);
//
//                if (hiddenView.getVisibility() == View.GONE) {
//                    hiddenView.setVisibility(View.VISIBLE);
//                }
//                if (absDist >= maxHiddenHeight)
//                    absDist = maxHiddenHeight;
//
//                hiddenParams.height = (int) absDist;
//                hiddenView.setLayoutParams(hiddenParams);
//                Log.d(TAG,"xdemo.onTouchEvent move."+absDist);
//                break;
//            case MotionEvent.ACTION_CANCEL:
//                Log.d(TAG,"xdemo.onTouchEvent cancel.");
//                downY = 0;
//                pressed = false;
//                hiddenParams.height = maxHiddenHeight;
//                hiddenView.setLayoutParams(hiddenParams);
//                hiddenView.setVisibility(View.GONE);
//                break;
//            case MotionEvent.ACTION_UP:
//                Log.d(TAG,"xdemo.onTouchEvent up.");
//                downY = 0;
//                pressed = false;
//                hiddenParams.height = maxHiddenHeight;
//                hiddenView.setLayoutParams(hiddenParams);
//                hiddenView.setVisibility(View.GONE);
//                break;
//        }
//        return true;
//    }
}
