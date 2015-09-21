package com.sumugu.liubo.lc;

import android.content.ClipData;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sumugu.liubo.lc.alarmclock.AlarmUntils;
import com.sumugu.liubo.lc.contract.ItemContract;

import java.text.DateFormat;
import java.util.Calendar;
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
    private Button buttonDelete;
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
        buttonDelete = (Button)findViewById(R.id.btn_item_delete_detail);
        //
        buttonDelete.setOnClickListener(this);
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
        //根据ItemID查询结果
        Uri uri = ContentUris.withAppendedId(ItemContract.CONTENT_URI,id);
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if(!cursor.moveToFirst())
            return;

        //获取查询结果各项值
        String content = cursor.getString(cursor.getColumnIndex(ItemContract.Column.ITEM_CONTENT));
        long createdAt = cursor.getLong(cursor.getColumnIndex(ItemContract.Column.ITEM_CREATED_AT));
        long alarmclock = cursor.getLong(cursor.getColumnIndex(ItemContract.Column.ITEM_ALARM_CLOCK));
        long hasClock = cursor.getLong(cursor.getColumnIndex(ItemContract.Column.ITEM_HAS_CLOCK));
        long isFinished = cursor.getLong(cursor.getColumnIndex(ItemContract.Column.ITEM_IS_FINISHED));

        //设置各项UI内容
        textContent.setText(content);
        textCreatedAt.setText(DateUtils.getRelativeTimeSpanString(createdAt));
        textAlarmClock.setText(android.text.format.DateFormat.format("yyyy-MM-dd kk:mm:ss", alarmclock));
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

        //设置闹钟
        //单位：分钟
        switch (v.getId()) {
            case R.id.btn_1minute_detail:
                setAlarmClock(1);   //1分钟后提醒
                backToItemLine();
                return;
            case R.id.btn_5minutes_detail:
                setAlarmClock(5);   //5分钟后提醒
                backToItemLine();
                return;
            case R.id.btn_15minutes_detail:
                setAlarmClock(15);  //15分钟后提醒
                backToItemLine();
                return;
            case R.id.btn_item_finish_detail:
                finishItem();       //完成
                backToItemLine();
                return;
            case R.id.btn_item_delete_detail:
                deleteItem();       //删除
                backToItemLine();
                return;
            default:
                return;
        }
    }

    private void backToItemLine()
    {
        startActivity(new Intent(this,MainActivity.class));
    }

    private void setAlarmClock(int interval) {

        //定义各种以下用到的变量
        AlarmUntils alarmUntils = new AlarmUntils();
        Calendar calendar = Calendar.getInstance();
        Uri uri = Uri.withAppendedPath(ItemContract.CONTENT_URI, String.valueOf(mItemId));

        //step 1，找出已经设置的闹钟提醒，并取消之。
        cancelAlarmClock();

        //step 2，设置新的闹钟并更新纪录。
        //设置新闹钟
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, interval);

        //重复提醒，间隔1分钟
        String result = alarmUntils.SetAlarmClock(this,calendar,true,60*1000,String.valueOf(mItemId));

        //step 3，更新记录的闹钟信息
        ContentValues values = new ContentValues();
        values.put(ItemContract.Column.ITEM_IS_FINISHED,0);
        values.put(ItemContract.Column.ITEM_HAS_CLOCK,1);
        values.put(ItemContract.Column.ITEM_ALARM_CLOCK,calendar.getTimeInMillis());

        int count = getContentResolver().update(uri,values,null,null);

        Toast.makeText(ItemDetailActivity.this, "已经设置"+interval+"后再见！", Toast.LENGTH_SHORT).show();

    }

    private void cancelAlarmClock()
    {
        //定义各种以下用到的变量
        AlarmUntils alarmUntils = new AlarmUntils();
        Calendar calendar = Calendar.getInstance();
        Uri uri = Uri.withAppendedPath(ItemContract.CONTENT_URI, String.valueOf(mItemId));

        //找出已经设置的闹钟提醒，并取消之。
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if(!cursor.moveToFirst()) {
            return;
        }

        int hasAlarm = cursor.getInt(cursor.getColumnIndex(ItemContract.Column.ITEM_HAS_CLOCK));
        long alarmClock = cursor.getLong(cursor.getColumnIndex(ItemContract.Column.ITEM_ALARM_CLOCK));

        if(hasAlarm>0)
        {
            // /取消闹钟
            calendar.setTimeInMillis(alarmClock);
            String result = alarmUntils.CancelAlarmClock(this,calendar,String.valueOf(mItemId));
            Toast.makeText(ItemDetailActivity.this, result, Toast.LENGTH_SHORT).show();
        }
    }

    private void finishItem() {

//        Uri uri = Uri.withAppendedPath(ItemContract.CONTENT_URI,String.valueOf(mItemId));

        Uri uri = ItemContract.CONTENT_URI;
        String where = ItemContract.Column.ITEM_ID + "=?";
        String[] params = new String[] {String.valueOf(mItemId)};

        //step 1，查处闹钟并取消掉
        cancelAlarmClock();

        //step 2，更新记录的闹钟和标记
        ContentValues values = new ContentValues();
        values.put(ItemContract.Column.ITEM_IS_FINISHED, 1);
        values.put(ItemContract.Column.ITEM_HAS_CLOCK,0);
        values.put(ItemContract.Column.ITEM_ALARM_CLOCK,0);

        int count = getContentResolver().update(uri,values,where,params);
//        int count = getContentResolver().update(Uri.withAppendedPath(ItemContract.CONTENT_URI,String.valueOf(mItemId)),values,null,null);

        if(count>0)
        {
            Toast.makeText(ItemDetailActivity.this, "继续努力！", Toast.LENGTH_SHORT).show();
            Log.d(TAG,"sumugu:finish item"+String.valueOf(mItemId));
        }
    }

    private void deleteItem() {

        //step 1，查处闹钟并取消
        cancelAlarmClock();

        //创建访问内容提供器的URI
        Uri uri = Uri.withAppendedPath(ItemContract.CONTENT_URI,String.valueOf(mItemId));

        //step 2，删除指定ID的纪录
        int count = getContentResolver().delete(uri,null,null);

        //删除成功提示
        if(count>0)
        {
            Toast.makeText(this, "已经删除！", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "sumugu:delete item:" + String.valueOf(mItemId));
        }
    }
}
