package com.sumugu.liubo.lc;

import android.content.ClipData;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sumugu.liubo.lc.contract.ItemContract;

import java.text.DateFormat;
import java.util.Date;

public class ItemDetailActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = ItemDetailActivity.class.getSimpleName();
    private TextView textContent;
    private TextView textCreatedAt;
    private TextView textAlarmClock;
    private Button button1minute;
    private Button button5minutes;
    private Button button15minutes;
    private Button buttonFinish;
    private long mItemId=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        //find all views
        textAlarmClock = (TextView)findViewById(R.id.text_item_alarmclock_detail);
        textContent = (TextView)findViewById(R.id.text_item_content_detail);
        textCreatedAt = (TextView)findViewById(R.id.text_item_created_at_detail);
        button15minutes = (Button)findViewById(R.id.btn_15minutes_detail);
        button1minute = (Button)findViewById(R.id.btn_1minute_detail);
        button5minutes = (Button)findViewById(R.id.btn_5minutes_detail);
        buttonFinish = (Button)findViewById(R.id.btn_item_finish_detail);
        //
        buttonFinish.setOnClickListener(this);
        button15minutes.setOnClickListener(this);
        button5minutes.setOnClickListener(this);
        button1minute.setOnClickListener(this);
        //
        Intent intent = getIntent();
        mItemId = intent.getLongExtra(ItemContract.Column.ITEM_ID, -1);
        updateView(mItemId);
        //
    }

    private void updateView(long id)
    {
        if(id==-1) {
            textContent.setText("什么都没有！");
            return;
        }
        Uri uri = ContentUris.withAppendedId(ItemContract.CONTENT_URI,id);
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if(!cursor.moveToFirst())
            return;

        String content = cursor.getString(cursor.getColumnIndex(ItemContract.Column.ITEM_CONTENT));
        long createdAt = cursor.getLong(cursor.getColumnIndex(ItemContract.Column.ITEM_CREATED_AT));
        long alarmclock = cursor.getLong(cursor.getColumnIndex(ItemContract.Column.ITEM_ALARM_CLOCK));
        long hasClock = cursor.getLong(cursor.getColumnIndex(ItemContract.Column.ITEM_HAS_CLOCK));
        long isFinished = cursor.getLong(cursor.getColumnIndex(ItemContract.Column.ITEM_IS_FINISHED));

        textContent.setText(content);
        textCreatedAt.setText(DateUtils.getRelativeTimeSpanString(createdAt));
        textAlarmClock.setText(android.text.format.DateFormat.format("yyyy-MM-dd kk:mm:ss",alarmclock));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        long interval = 0;  //秒
        switch (v.getId())
        {
            case R.id.btn_1minute_detail:
                interval=60;
                return;
            case R.id.btn_5minutes_detail:
                interval=300;
                return;
            case R.id.btn_15minutes_detail:
                interval=900;
                return;
            case R.id.btn_item_finish_detail:
                interval=-1;
                return;
            default:
                interval=0;
        }
        //设置闹钟
    }
}
