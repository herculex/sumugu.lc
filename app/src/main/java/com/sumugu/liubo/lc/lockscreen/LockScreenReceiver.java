package com.sumugu.liubo.lc.lockscreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.sumugu.liubo.lc.MainActivity;

public class LockScreenReceiver extends BroadcastReceiver {
    public LockScreenReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)
                || intent.getAction().equals(Intent.ACTION_SCREEN_ON)
                || intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
        {
            start_lockscreen(context);
        }
    }

    private void start_lockscreen(Context context) {
//        Intent intent = new Intent(context, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent);
        Toast.makeText(context, "alpha5-ScreenReceived.", Toast.LENGTH_SHORT).show();
    }
}
