package com.sumugu.liubo.lc.alarmclock;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;

import com.sumugu.liubo.lc.contract.ItemContract;

import java.util.Calendar;

/**
 * Created by liubo on 15/9/14.
 */
public class AlarmUntils {

    public String SetAlarmClock(Activity activity,Calendar calendar,boolean repeat,long interval,long actionId)
    {
        int year,month,day,hour,minute,second;
        // 获取设置日期的年，月，日，时，分，秒
        year = calendar.get(Calendar.YEAR);
        month=calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DATE);
        hour=calendar.get(Calendar.HOUR);
        minute=calendar.get(Calendar.MINUTE);
        second=calendar.get(Calendar.SECOND);


        // 设置闹铃的时间(年,月,日,时,分,秒)
//        calendar.set(year, month, day, hour, minute, 0);
        Intent intent = new Intent(activity, AlarmReceiver.class);
        // 设置intent的动作,识别当前设置的是哪一个闹铃,有利于管理闹铃的关闭。同时有利于AlarmReceiver获得后intent的action，可以获得itemid，从而获得item的信息。
        intent.setAction(String.valueOf(actionId));
        intent.putExtra(ItemContract.Column.ITEM_ID,actionId);

        // 用广播管理闹铃
        PendingIntent pi = PendingIntent.getBroadcast(activity, 0, intent, 0);
        // 获取闹铃管理
        AlarmManager am = (AlarmManager) activity.getSystemService(Activity.ALARM_SERVICE);
        // 设置闹钟
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
        // 设置闹钟重复时间
        if(repeat && interval>0) {
            am.setRepeating(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), interval, pi);
        }
        // 获取到的月份是0~11,所以要加1
        int newMonth = month + 1;

        return ("您选择的闹铃为:" + year + "年" + newMonth + "月" + day
                + "日" + hour + "时" + minute + "分" + second + "秒.");
    }

    public String CancelAlarmClock(Activity activity,Calendar calendar,long actionId)
    {
        int year,month,day,hour,minute,second;
        // 获取设置日期的年，月，日，时，分
        year = calendar.get(Calendar.YEAR);
        month=calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DATE);
        hour=calendar.get(Calendar.HOUR);
        minute=calendar.get(Calendar.MINUTE);
        second=calendar.get(Calendar.SECOND);


        Intent intent = new Intent(activity, AlarmReceiver.class);

        // 找出当前控件选择的闹铃时间,并关闭当前选择的闹铃
        intent.setAction(String.valueOf(actionId));
        intent.putExtra(ItemContract.Column.ITEM_ID,actionId);

        PendingIntent pi = PendingIntent.getBroadcast(activity, 0, intent, 0);
        AlarmManager am = (AlarmManager) activity.getSystemService(Activity.ALARM_SERVICE);
        am.cancel(pi);
        int newMonth = month + 1;
        return ("您取消了" + year + "年" + newMonth + "月" + day + "日"
                + hour + "时" + minute + "分"  + second + "秒的闹铃.");
    }
}
