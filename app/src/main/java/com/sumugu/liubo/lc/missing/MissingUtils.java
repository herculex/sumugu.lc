package com.sumugu.liubo.lc.missing;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;

/**
 * Created by liubo on 15/9/16.
 */
public class MissingUtils {

    ///Getting unread SMSs simply. 简单方式获取未读短信数量
    public int getNewSmsCount(Context context) {
        int result = 0;
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms"), null,
                "type = 1 and read = 0", null, null);
        if (cursor != null) {
            result = cursor.getCount();
            cursor.close();
        }
        return result;
    }
    public Cursor getNewSms(Context context)
    {
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms"), null,
                "type = 1 and read = 0", null, null);

        return cursor;
    }
    ///Getting unread MSMs simply.简单方式获取未读彩信数量
    public int getNewMmsCount(Context context) {
        int result = 0;
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://mms/inbox"),
                null, "read = 0", null, null);
        if (cursor != null) {
            result = cursor.getCount();
            cursor.close();
        }
        return result;
    }

    ///Getting missing Calls simply.简单方式获取未接电话数量
    public int getMissCallCount(Context context) {
        int result = 0;
        Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, new String[] {
                CallLog.Calls.TYPE
        }, " type=? and new=?", new String[] {
                CallLog.Calls.MISSED_TYPE + "", "1"
        }, "date desc");

        if (cursor != null) {
            result = cursor.getCount();
            cursor.close();
        }
        return result;
    }
    public Cursor getMissCall(Context context)
    {
        Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, new String[] {
                CallLog.Calls.TYPE
        }, " type=? and new=?", new String[] {
                CallLog.Calls.MISSED_TYPE + "", "1"
        }, "date desc");

        return cursor;
    }
}
