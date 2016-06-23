package com.sumugu.liubo.lc.xdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sumugu.liubo.lc.R;

public class XdemoActivty extends AppCompatActivity {

    private MyTextView hiddenView;    //// TODO: 16/6/16 扩展这个TextView，自定义MyTextView，观察onSizeChanged和onDraw
    private TextView welcomeView;
    private ViewGroup.LayoutParams hiddenParams,welcomeParams;
    private int maxHiddenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xdemo);

        hiddenView = (MyTextView)findViewById(R.id.hiddenView);
        welcomeView = (TextView)findViewById(R.id.welcomeView);

        hiddenParams = hiddenView.getLayoutParams();
        welcomeParams = welcomeView.getLayoutParams();
        maxHiddenHeight = hiddenParams.height;


//        Toast.makeText(MainActivity.this, "hidden's height:"+hiddenParams.height+";welcome's height:"+welcomeParams.height, Toast.LENGTH_SHORT).show();

        Log.d("MyDemo","hidden's height:"+hiddenParams.height+";welcome's height:"+welcomeParams.height);

    }

    private float downY;
    private boolean pressed=false;
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                if(!pressed) {
                    downY = event.getY();
                    pressed = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float dist = event.getY()- downY;
                if(dist<0)
                    dist=0;
                float absDist = Math.abs(dist);

                if(hiddenView.getVisibility() == View.GONE){
                    hiddenView.setVisibility(View.VISIBLE);
                }
                if(absDist>=maxHiddenHeight)
                    absDist=maxHiddenHeight;

                hiddenParams.height = (int) absDist;
                hiddenView.setLayoutParams(hiddenParams);

                break;
            case MotionEvent.ACTION_CANCEL:
                downY=0;
                pressed=false;
                hiddenParams.height=maxHiddenHeight;
                hiddenView.setLayoutParams(hiddenParams);
                hiddenView.setVisibility(View.GONE);
                break;
            case MotionEvent.ACTION_UP:
                downY=0;
                pressed=false;
                hiddenParams.height=maxHiddenHeight;
                hiddenView.setLayoutParams(hiddenParams);
                hiddenView.setVisibility(View.GONE);
                break;
        }
        return true;
    }
}
