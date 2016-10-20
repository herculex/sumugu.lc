package com.sumugu.liubo.lc.xdemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.sumugu.liubo.lc.R;
import com.sumugu.liubo.lc.xdemo.data.MyCursorAdapter;
import com.sumugu.liubo.lc.xdemo.data.MyLoaderCallback;

public class Xdemo03 extends Activity {

    private MyListView listView;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xdemo3);

        int loaderId = 5;
        MyCursorAdapter myCursorAdapter = new MyCursorAdapter(this, null, 0);
        MyLoaderCallback myLoaderCallback = new MyLoaderCallback(this, myCursorAdapter, loaderId);

        textView = (TextView) findViewById(R.id.textBottom);
        listView = (MyListView) findViewById(R.id.listView);
        listView.setAdapter(myCursorAdapter);

        getLoaderManager().initLoader(loaderId, null, myLoaderCallback);

        listView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
//                listView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                Log.d("xdemo3","onViewTree,listview.height="+listView.getHeight());
            }
        });
        listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            int precallcount=0;
            @Override
            public boolean onPreDraw() {
                listView.getViewTreeObserver().removeOnPreDrawListener(this);
                Log.d("xdemo3",precallcount+",onPreDraw,listview.height="+listView.getHeight());
                precallcount++;
                return true;
            }
        });
//        listView.getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
//            @Override
//            public void onDraw() {
//                Log.d("xdemo3","onDraw,listview.height="+listView.getHeight());
//            }
//        });

        textView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                Log.d("xdemo3","onPreDraw,textview.height="+textView.getHeight());
                return true;
            }
        });
        Log.d("xdemo3","onCreated,textview.height="+textView.getHeight());
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            Log.d("xdemo3","onWindowsFocus,listview.height="+listView.getHeight());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        listView.post(new Runnable() {
            @Override
            public void run() {
                Log.d("xdemo3","listview.height="+listView.getHeight());
                Log.d("xdemo3","listview.childcount="+listView.getCount());
                Log.d("xdemo3","listview.adapter.childcount="+listView.getAdapter().getCount());

//                for(int i=0;i<listView.getCount();i++){
//                    Log.d("xdemo3","listview.child:"+i+",height="+listView.getChildAt(i).getHeight());
//                }
            }
        });

        textView.post(new Runnable() {
            @Override
            public void run() {
                Log.d("xdemo3","onStart,textview.height="+textView.getHeight());
            }
        });
    }
}
