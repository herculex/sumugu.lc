package com.sumugu.liubo.lc;

import android.app.Fragment;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sumugu.liubo.lc.alarmclock.AlarmUntils;
import com.sumugu.liubo.lc.contract.ItemContract;

import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class ItemDetailFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = ItemDetailFragment.class.getSimpleName();
    private TextView textContent;
    private TextView textCreatedAt;
    private TextView textAlarmClock;
    private TextView button1minute;
    private TextView button5minutes;
    private TextView button15minutes;
    private TextView buttonFinish;
    private TextView buttonDelete;
    private long mItemId = -1;

    public ItemDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_item_detail, container, false);
        //find all views
        textAlarmClock = (TextView) view.findViewById(R.id.text_item_alarmclock_detail);
        textContent = (TextView) view.findViewById(R.id.text_item_content_detail);
        textCreatedAt = (TextView) view.findViewById(R.id.text_item_created_at_detail);
        button15minutes = (TextView) view.findViewById(R.id.btn_15minutes_detail);
        button1minute = (TextView) view.findViewById(R.id.btn_1minute_detail);
        button5minutes = (TextView) view.findViewById(R.id.btn_5minutes_detail);
        buttonFinish = (TextView) view.findViewById(R.id.btn_item_finish_detail);
        buttonDelete = (TextView) view.findViewById(R.id.btn_item_delete_detail);
        //
        buttonDelete.setOnClickListener(this);
        buttonFinish.setOnClickListener(this);
        button15minutes.setOnClickListener(this);
        button5minutes.setOnClickListener(this);
        button1minute.setOnClickListener(this);
        //
        Intent intent = getActivity().getIntent();
        mItemId = intent.getLongExtra(ItemContract.Column.ITEM_ID, -1);
        updateView(mItemId);
        //
        return view;
    }

    private void updateView(long id) {
        if (id == -1) {
            textContent.setText("什么都没有！");
            return;
        }
        //根据ItemID查询结果
        Uri uri = ContentUris.withAppendedId(ItemContract.CONTENT_URI, id);
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        if (!cursor.moveToFirst())
            return;

        //获取查询结果各项值
        String content = cursor.getString(cursor.getColumnIndex(ItemContract.Column.ITEM_CONTENT));
        long createdAt = cursor.getLong(cursor.getColumnIndex(ItemContract.Column.ITEM_CREATED_AT));
        long alarmclock = cursor.getLong(cursor.getColumnIndex(ItemContract.Column.ITEM_ALARM_CLOCK));
        long hasClock = cursor.getLong(cursor.getColumnIndex(ItemContract.Column.ITEM_HAS_CLOCK));
        long isFinished = cursor.getLong(cursor.getColumnIndex(ItemContract.Column.ITEM_IS_FINISHED));

        //设置各项UI内容
        textContent.setText(content);
        textCreatedAt.setText("创建于：" + DateUtils.getRelativeTimeSpanString(createdAt));
        if (alarmclock > 0) {
            textAlarmClock.setText(android.text.format.DateFormat.format("yyyy-MM-dd kk:mm:ss", alarmclock) + "提醒：");
            button15minutes.setVisibility(View.VISIBLE);
            button5minutes.setVisibility(View.VISIBLE);
            button1minute.setVisibility(View.VISIBLE);
        } else {
            button1minute.setVisibility(View.GONE);
            button5minutes.setVisibility(View.GONE);
            button15minutes.setVisibility(View.GONE);
        }
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

    private void backToItemLine() {
//        startActivity(new Intent(getActivity(),ItemLineFrameActivity.class));
        getActivity().finish();
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
        String result = alarmUntils.setAlarmClock(getActivity(), calendar, true, 60 * 1000, mItemId);

        //step 3，更新记录的闹钟信息
        ContentValues values = new ContentValues();
        values.put(ItemContract.Column.ITEM_IS_FINISHED, 0);
        values.put(ItemContract.Column.ITEM_HAS_CLOCK, 1);
        values.put(ItemContract.Column.ITEM_ALARM_CLOCK, calendar.getTimeInMillis());

        int count = getActivity().getContentResolver().update(uri, values, null, null);

        Toast.makeText(getActivity(), "已经设置" + interval + "后再见！", Toast.LENGTH_SHORT).show();

    }

    private void cancelAlarmClock() {
        //定义各种以下用到的变量
        AlarmUntils alarmUntils = new AlarmUntils();
        Calendar calendar = Calendar.getInstance();
        Uri uri = Uri.withAppendedPath(ItemContract.CONTENT_URI, String.valueOf(mItemId));

        //找出已经设置的闹钟提醒，并取消之。
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        if (cursor.moveToFirst()) {

            int hasAlarm = cursor.getInt(cursor.getColumnIndex(ItemContract.Column.ITEM_HAS_CLOCK));
            long alarmClock = cursor.getLong(cursor.getColumnIndex(ItemContract.Column.ITEM_ALARM_CLOCK));

            if (alarmClock > 0) {
                // /取消闹钟
                calendar.setTimeInMillis(alarmClock);
                String result = alarmUntils.cancelAlarmClock(getActivity(), calendar, mItemId);
//            Toast.makeText(ItemDetailActivity.this, result, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "sumugu:" + result);
            }
        }
    }

    private void finishItem() {

//        Uri uri = Uri.withAppendedPath(ItemContract.CONTENT_URI,String.valueOf(mItemId));

        Uri uri = ItemContract.CONTENT_URI;
        String where = ItemContract.Column.ITEM_ID + "=?";
        String[] params = new String[]{String.valueOf(mItemId)};

        //step 1，查处闹钟并取消掉
        cancelAlarmClock();

        //step 2，更新记录的闹钟和标记
        ContentValues values = new ContentValues();
        values.put(ItemContract.Column.ITEM_IS_FINISHED, 1);

        //不必删除闹钟信息
//        values.put(ItemContract.Column.ITEM_HAS_CLOCK,0);
//        values.put(ItemContract.Column.ITEM_ALARM_CLOCK,0);

        int count = getActivity().getContentResolver().update(uri, values, where, params);
//        int count = getContentResolver().update(Uri.withAppendedPath(ItemContract.CONTENT_URI,String.valueOf(mItemId)),values,null,null);

        if (count > 0) {
            Toast.makeText(getActivity(), "继续努力！", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "sumugu:finish item" + String.valueOf(mItemId));
        }
    }

    private void deleteItem() {

        //step 1，查处闹钟并取消
        cancelAlarmClock();

        //创建访问内容提供器的URI
        Uri uri = Uri.withAppendedPath(ItemContract.CONTENT_URI, String.valueOf(mItemId));

        //step 2，删除指定ID的纪录
        int count = getActivity().getContentResolver().delete(uri, null, null);

        //删除成功提示
        if (count > 0) {
            Toast.makeText(getActivity(), "已经删除！", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "sumugu:delete item:" + String.valueOf(mItemId));
        }
    }
}
