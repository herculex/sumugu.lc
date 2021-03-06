package com.sumugu.liubo.lc.alarmclock;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.format.DateFormat;
import android.util.Log;

import com.sumugu.liubo.lc.contract.ItemContract;

import java.util.Calendar;
import java.util.Date;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class AlarmIntentService extends IntentService {

    public AlarmIntentService() {
        super("AlarmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        checkAlarms();
    }

    void checkAlarms() {
        long newdate = new Date().getTime();
        Uri uri = ItemContract.CONTENT_URI;
        String selection = ItemContract.Column.ITEM_ALARM_CLOCK + ">?";
        String[] args = new String[]{String.valueOf(newdate)};

        try {
            Cursor cursor = getContentResolver().query(uri, null, selection, args, ItemContract.DEFAULT_SORT);

            if (cursor != null && cursor.getCount() > 0) {
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    long itemId = cursor.getLong(cursor.getColumnIndex(ItemContract.Column.ITEM_ID));
                    long alarmClock = cursor.getLong(cursor.getColumnIndex(ItemContract.Column.ITEM_ALARM_CLOCK));
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(alarmClock);
                    AlarmUntils alarmUntils = new AlarmUntils();
                    alarmUntils.setAlarmClock(getBaseContext(), calendar, true, 60 * 1000, itemId);
                }
                cursor.close();

                Log.d(this.getClass().getName(), DateFormat.format("yyyy-MM-dd HH:mm:ss", newdate) + ",启动alarm数量：" + cursor.getCount() + "。at:" + newdate);
//                doInsert(DateFormat.format("yyyy-MM-dd HH:mm:ss", newdate) + ",启动alarm数量：" + cursor.getCount() + "。at:" + newdate);
            } else {
                Log.d(this.getClass().getName(), DateFormat.format("yyyy-MM-dd HH:mm:ss", newdate) + ",无启动alarm。at:" + newdate);
//                doInsert(DateFormat.format("yyyy-MM-dd HH:mm:ss", newdate) + ",无启动alarm。at:" + newdate);
            }

        } catch (Exception e) {
            Log.d(this.getClass().getName(), e.getMessage());
//            doInsert(e.getMessage());
        }
    }

    void doInsert(String content) {
        ContentValues values = new ContentValues();
        //create (insert)
        values.put(ItemContract.Column.ITEM_TITLE, "a5");
        values.put(ItemContract.Column.ITEM_CONTENT, content);
        values.put(ItemContract.Column.ITEM_CREATED_AT, new Date().getTime());
        values.put(ItemContract.Column.ITEM_CREATED_AT_DAY, DateFormat.format("yyyy-MM-dd", new Date().getTime()).toString());
        values.put(ItemContract.Column.ITEM_IS_FINISHED, false);
        values.put(ItemContract.Column.ITEM_FINISHED_AT, 0);
        values.put(ItemContract.Column.ITEM_HAS_CLOCK, 0);
        values.put(ItemContract.Column.ITEM_ALARM_CLOCK, 0);

        values.put(ItemContract.Column.ITEM_LIST_ID, 0);//default is 0

        Uri uri = getContentResolver().insert(ItemContract.CONTENT_URI, values);
    }

}
