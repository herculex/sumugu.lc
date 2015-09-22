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
        //Toast.makeText(context, "闹钟时间到:" + intent.getAction(), Toast.LENGTH_LONG).show();
        //
        Intent notifyService = new Intent(context,NotifyService.class);
        //传送ItemID到通知的服务实例
        notifyService.putExtra(ItemContract.Column.ITEM_ID,intent.getLongExtra(ItemContract.Column.ITEM_ID,-1));
        context.startService(notifyService);
    }
}
