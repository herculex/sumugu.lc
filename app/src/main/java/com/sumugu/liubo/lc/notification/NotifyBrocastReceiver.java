package com.sumugu.liubo.lc.notification;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.sumugu.liubo.lc.alarmclock.AlarmUntils;
import com.sumugu.liubo.lc.contract.ItemContract;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by liubo on 2017/8/31.
 */

public class NotifyBrocastReceiver extends BroadcastReceiver {
    final static String ACTION_FINISH = "com.sumugu.liubo.lc.notification.ACTION_FINISH";
    final static String ACTION_SNOOZE = "com.sumugu.liubo.lc.notification.ACTION_SNOOZE";

    @Override
    public void onReceive(Context context, Intent intent) {
        String actionString = intent.getAction();
        long itemId = intent.getLongExtra(ItemContract.Column.ITEM_ID, 0);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (ACTION_FINISH.equals(actionString)) {
            //
            Log.d("Notify_finish", String.valueOf(itemId));
            finish(context, itemId);

        }
        if (ACTION_SNOOZE.equals(actionString)) {
            //
            Log.d("Notify_snooze", String.valueOf(itemId));
            long oldAlarm = intent.getLongExtra(ItemContract.Column.ITEM_ALARM_CLOCK, 0);

            //增加一个小时
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(new Date().getTime());
            cal.add(Calendar.HOUR, 1);

            update(context, itemId, oldAlarm, cal.getTimeInMillis());
        }

        notificationManager.cancel((int) itemId);
    }

    void update(Context context, long id, long oldAlarm, long newAlarm) {

        ContentValues values = new ContentValues();
        values.put(ItemContract.Column.ITEM_ALARM_CLOCK, newAlarm);

        Uri uri = Uri.withAppendedPath(ItemContract.CONTENT_URI, String.valueOf(id));
        String where = ItemContract.Column.ITEM_ID + "=?";
        String[] paras = new String[]{String.valueOf(id)};

        int result = context.getContentResolver().update(uri, values, where, paras);
        if (result > 0) {
            setUpAlarmClock(context, newAlarm, oldAlarm, id);
        }

    }

    void setUpAlarmClock(Context context, long newAlarm, long oldAlarm, long id) {

        Calendar old = Calendar.getInstance();
        Calendar now = Calendar.getInstance();

        if (oldAlarm > 0)
            old.setTimeInMillis(oldAlarm);
        else
            old = null;

        if (newAlarm > 0)
            now.setTimeInMillis(newAlarm);
        else
            now = null;

        new AlarmUntils().setAlarmClock(context, now, old, true, 60 * 1000, id);
    }

    void finish(Context context, long id) {
        cancelAlarmClock(context, id);

        String where = ItemContract.Column.ITEM_ID + "=?";
        String[] args = new String[]{String.valueOf(id)};
        ContentValues values = new ContentValues();

        values.put(ItemContract.Column.ITEM_IS_FINISHED, true);
        values.put(ItemContract.Column.ITEM_FINISHED_AT, new Date().getTime());

        int result = context.getContentResolver().update(ItemContract.CONTENT_URI, values, where, args);

    }

    long getAlarmClock(Context context, long id) {
        Cursor cursor = context.getContentResolver().query(Uri.withAppendedPath(ItemContract.CONTENT_URI, String.valueOf(id)), null, null, null, null);
        if (cursor == null)
            return 0;
        else if (cursor.moveToFirst()) {
            long clock = cursor.getLong(cursor.getColumnIndex(ItemContract.Column.ITEM_ALARM_CLOCK));
            cursor.close();
            return clock;
        }
        return 0;
    }

    void cancelAlarmClock(Context context, long id) {
        long alarm = getAlarmClock(context, id);

        if (alarm > 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(alarm);

            new AlarmUntils().cancelAlarmClock(context, calendar, id);
        }
    }
}
