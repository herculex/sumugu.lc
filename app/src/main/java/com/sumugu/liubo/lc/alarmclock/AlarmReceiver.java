package com.sumugu.liubo.lc.alarmclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.Toast;

import com.sumugu.liubo.lc.contract.ItemContract;
import com.sumugu.liubo.lc.notification.NotifyService;

import java.util.Calendar;
import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
//            Toast.makeText(context, "Alarm启动", Toast.LENGTH_SHORT).show();

            Uri uri = ItemContract.CONTENT_URI;
            String selection = ItemContract.Column.ITEM_ALARM_CLOCK + ">?";
            String[] args = new String[]{String.valueOf(new Date().getTime())};

            Cursor cursor = context.getContentResolver().query(uri, null, selection, args, ItemContract.DEFAULT_SORT);

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                long itemId = cursor.getLong(cursor.getColumnIndex(ItemContract.Column.ITEM_ID));
                long alarmClock = cursor.getLong(cursor.getColumnIndex(ItemContract.Column.ITEM_ALARM_CLOCK));
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(alarmClock);
                AlarmUntils alarmUntils = new AlarmUntils();
                alarmUntils.setAlarmClock(context, calendar, true, 60 * 1000, itemId);
            }
            cursor.close();

        } else {
            Toast.makeText(context, "闹钟时间到:" + intent.getAction(), Toast.LENGTH_LONG).show();
            //
            Intent notifyService = new Intent(context, NotifyService.class);
            //传送ItemID到通知的服务实例
            notifyService.putExtra(ItemContract.Column.ITEM_ID, intent.getLongExtra(ItemContract.Column.ITEM_ID, -1));
            context.startService(notifyService);
        }

    }
}
