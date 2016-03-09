package com.sumugu.liubo.lc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sumugu.liubo.lc.contract.ItemContract;
import com.sumugu.liubo.lc.contract.ListContract;
import com.sumugu.liubo.lc.lockscreen.LockScreenService;
import com.sumugu.liubo.lc.lockscreen.LockScreenUtils;
import com.sumugu.liubo.lc.missing.MissingUtils;

import java.util.Random;


public class MainActivity extends Activity implements LockScreenUtils.OnLockStatusChangedListener,View.OnClickListener{

    private final String TAG=MainActivity.class.getSimpleName();
    private LockScreenUtils mLockscreenUtils;

    // Set appropriate flags to make the screen appear over the keyguard
//    @Override
//    public void onAttachedToWindow() {
//        this.getWindow().setType(
//                WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
//        this.getWindow().addFlags(
//                WindowManager.LayoutParams.FLAG_FULLSCREEN
//                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
//        );
//
//        super.onAttachedToWindow();
//    }

    private LinearLayout mContainerBottom;
    private LinearLayout mContainerUnlock;
    private LinearLayout mContainerContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContainerBottom = (LinearLayout)findViewById(R.id.layer_bottom);
        mContainerUnlock = (LinearLayout)findViewById(R.id.layer_unlock);
        mContainerContent = (LinearLayout)findViewById(R.id.layer_content);

        findViewById(R.id.btn_unlock_screen).setOnClickListener(this);
        findViewById(R.id.btn_new_life).setOnClickListener(this);

        //
        makeFullScreen();
        //
        init();
        // unlock screen in case of app get killed by system
        if (getIntent() != null && getIntent().hasExtra("kill")
                && getIntent().getExtras().getInt("kill") == 1) {
            enableKeyguard();
            unlockHomeButton();
        } else {

            try {
                // disable keyguard
                disableKeyguard();

                // lock home button
                lockHomeButton();

                // start service for observing intents
                startService(new Intent(this, LockScreenService.class));

                // listen the events get fired during the call
                StateListener phoneStateListener = new StateListener();
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                telephonyManager.listen(phoneStateListener,
                        PhoneStateListener.LISTEN_CALL_STATE);

            } catch (Exception e) {
            }
        }

    }
    private void init() {
        mLockscreenUtils = new LockScreenUtils();
    }

    private void makeFullScreen(){

        //makeFullScreen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


//        Toast.makeText(this,"SDK:"+String.valueOf(Build.VERSION.SDK_INT),Toast.LENGTH_LONG).show();

        if(Build.VERSION.SDK_INT <19)
        {
            Log.d(TAG, "sumugu,HIDE NAVIGATION.");
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
        else //View.SYSTEM_UI_FLAG_IMMERSIVE is only on API 19+
        {
            Log.d(TAG,"sumugu,IMMERSIVE.");
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE);
//            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.btn_new_life:
                startActivity(new Intent(this, ItemLineFrameActivity.class));
                return;
            case R.id.btn_unlock_screen:
                unlockHomeButton();
                return;
            default:
                return;
        }
    }

    // Handle events of calls and unlock screen if necessary
    private class StateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    unlockHomeButton();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        return;
    }
    @Override
    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
                || (keyCode == KeyEvent.KEYCODE_POWER)
                || (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
                || (keyCode == KeyEvent.KEYCODE_CAMERA)) {
            return true;
        }
        if ((keyCode == KeyEvent.KEYCODE_HOME)) {

            return true;
        }

        return false;

    }

    // handle the key press events here itself
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP
                || (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN)
                || (event.getKeyCode() == KeyEvent.KEYCODE_POWER)) {
            return false;
        }
        if ((event.getKeyCode() == KeyEvent.KEYCODE_HOME)) {

            return true;
        }
        return false;
    }
    // Lock home button
    public void lockHomeButton() {

        //2015.09.11稍微改动了下，如果有物理菜单键/导航键，则会激活错误的OverlayDialog打开，否则不开（如继续打开会影响隐藏虚拟菜单键/导航键）。备注，使用错误的View，不懂会不会同一个影响？
        if(ViewConfiguration.get(this).hasPermanentMenuKey())
        {
            Log.d(TAG, "sumugu,有物理菜单键");
            mLockscreenUtils.lock(MainActivity.this, true);
        }
        else {
            Log.d(TAG, "sumugu,没有物理菜单键");
            mLockscreenUtils.lock(MainActivity.this, false);
        }

    }

    // Unlock home button and wait for its callback
    public void unlockHomeButton() {
        mLockscreenUtils.unlock();
    }

    // Simply unlock device when home button is successfully unlocked
    @Override
    public void onLockStatusChanged(boolean isLocked) {
        if (!isLocked) {
            unlockDevice();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unlockHomeButton();
    }
    @SuppressWarnings("deprecation")
    private void disableKeyguard() {
        KeyguardManager mKM = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock mKL = mKM.newKeyguardLock("IN");
        mKL.disableKeyguard();
    }

    @SuppressWarnings("deprecation")
    private void enableKeyguard() {
        KeyguardManager mKM = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock mKL = mKM.newKeyguardLock("IN");
        mKL.reenableKeyguard();
    }

    //Simply unlock device by finishing the activity
    private void unlockDevice()
    {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return true;
    }

}
