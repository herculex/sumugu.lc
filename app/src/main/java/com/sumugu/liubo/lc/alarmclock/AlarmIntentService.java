package com.sumugu.liubo.lc.alarmclock;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

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
        Uri uri = ItemContract.CONTENT_URI;
        String selection = ItemContract.Column.ITEM_ALARM_CLOCK + ">?";
        String[] args = new String[]{String.valueOf(new Date().getTime())};

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
        }
    }

}
