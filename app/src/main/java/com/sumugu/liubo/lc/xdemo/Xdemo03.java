package com.sumugu.liubo.lc.xdemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.sumugu.liubo.lc.R;
import com.sumugu.liubo.lc.xdemo.data.MyCursorAdapter;
import com.sumugu.liubo.lc.xdemo.data.MyLoaderCallback;

public class Xdemo03 extends Activity {

    private MyListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xdemo3);

        int loaderId = 5;
        MyCursorAdapter myCursorAdapter = new MyCursorAdapter(this, null, 0);
        MyLoaderCallback myLoaderCallback = new MyLoaderCallback(this, myCursorAdapter, loaderId);

        listView = (MyListView) findViewById(R.id.listView);
        listView.setAdapter(myCursorAdapter);

        getLoaderManager().initLoader(loaderId, null, myLoaderCallback);

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
    }
}
