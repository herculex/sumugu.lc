package com.sumugu.liubo.lc.alarmclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.sumugu.liubo.lc.contract.ItemContract;
import com.sumugu.liubo.lc.notification.NotifyService;

public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Toast.makeText(context, "闹钟时间到:" + intent.getAction(), Toast.LENGTH_LONG).show();
        //
        Intent notifyService = new Intent(context,NotifyService.class);
        notifyService.putExtra("mItemId",intent.getAction()); //传送ItemID到通知的服务实例
        notifyService.putExtra("title","快消除ta");
        notifyService.putExtra("text",intent.getAction());
        notifyService.putExtra("ticker","LC阿尔法提醒你！");
        context.startService(notifyService);
    }
}
