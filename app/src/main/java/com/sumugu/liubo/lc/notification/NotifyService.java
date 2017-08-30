package com.sumugu.liubo.lc.notification;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.format.DateUtils;
import android.widget.RemoteViews;

import com.sumugu.liubo.lc.ItemDetailActivity;
import com.sumugu.liubo.lc.MainActivity;
import com.sumugu.liubo.lc.R;
import com.sumugu.liubo.lc.contract.ItemContract;

import java.util.Random;


public class NotifyService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    private static String TAG = NotifyService.class.getSimpleName();
    long mItemId;
    //标题，内容，提示
    private String mTitle, mText, mTicker;
    public NotifyService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        //获取过来的Intent的参数ItemID，来的是Long类型，必须用LongExtra接收，否则null了，就是取默认值。
        mItemId = intent.getLongExtra(ItemContract.Column.ITEM_ID, -1);

        //定义各种以下用到的变量
        Uri uri = Uri.withAppendedPath(ItemContract.CONTENT_URI, String.valueOf(mItemId));

        //找出Item的各项内容
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (!cursor.moveToFirst()) {
            return;
        }

        String text = cursor.getString(cursor.getColumnIndex(ItemContract.Column.ITEM_CONTENT));
        long createdAt = cursor.getLong(cursor.getColumnIndex(ItemContract.Column.ITEM_CREATED_AT));
        long alarmClock = cursor.getLong(cursor.getColumnIndex(ItemContract.Column.ITEM_ALARM_CLOCK));

        mText = text;
        mTitle = DateUtils.getRelativeTimeSpanString(createdAt).toString();
        mTicker = "GET UP!来自lc.alpha.5." + (android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss", alarmClock));

        cursor.close();

        show();

    }

    private void show() {
        Intent intentItemDetail = new Intent(this, ItemDetailActivity.class);
        intentItemDetail.putExtra(ItemContract.Column.ITEM_ID, mItemId);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intentItemDetail, PendingIntent.FLAG_UPDATE_CURRENT);

        //
        //Follow codes mainly from Example of Google Notifications | Android Developers | 2015.09.15
        //truly helpful!
        //
        //Same ID , Only one notify. 如果同一个ID，通知只有一个
        Random random = new Random();
        int mId = random.nextInt(900);

        //API 11 ,android 3.0
        Notification.Builder mBuilder =
                new Notification.Builder(this)
                        .setSmallIcon(R.mipmap.ic_notification)                 //必要条件1/3，小图标 -android.R.drawable.ic_lock_idle_alarm
                        .setContentTitle(mTitle)                                //必要条件2/3，标题 -"My notification"
                        .setContentText(mText)                                  //必要条件3/3，内容 -"Hello World!"
                        .setTicker(mTicker)
//                      .setContent(remoteViews)                                //自定义通知样式
                        .setVibrate(new long[]{350, 0, 100, 350})                 //自定义震动的模式
                        .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND);    //默认的铃声和震动模式

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, ItemDetailActivity.class);
        resultIntent.putExtra(ItemContract.Column.ITEM_ID, mItemId);

        //API 16 , android 4.1.2

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);


//        Notification.InboxStyle InboxStyle = new Notification.InboxStyle();
//        InboxStyle.setBigContentTitle("大视图Inbox内容：");
//        String[] events = new String[] {"event 1 ,go to school","event 2: go to home.","event 3: go to music to learn","event 4: go to guanzhou"};
//        for(String event :events)
//            InboxStyle.addLine(event);
//        mBuilder.setStyle(InboxStyle);

//        Notification.BigTextStyle bigTextStyle = new Notification.BigTextStyle();
//        bigTextStyle.setBigContentTitle("大视图BigText");
//        bigTextStyle.bigText(mText);
//        bigTextStyle.setSummaryText(mTitle);
//
//        mBuilder.setStyle(bigTextStyle);

        //remoteview
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_simpleway);
        remoteViews.setTextViewText(R.id.text_title, mTitle);
        remoteViews.setTextViewText(R.id.text_content, mText);

        BroadcastReceiver onClickReceiver = new BroadcastReceiver() {

            IntentFilter filter = new IntentFilter();
            ;
            Intent buttonIntent = new Intent(STATUS_BAR_COVER_CLICK_ACTION);
        filter.addAction(STATUS_BAR_COVER_CLICK_ACTION);
            PendingIntent pendButtonIntent = PendingIntent.getBroadcast(this, 0, buttonIntent, 0);
            //        if(Build.VERSION.SDK_INT>=20) {
//            PendingIntent pend = PendingIntent.getActivity(getBaseContext(),0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);
//             Notification.Action.Builder builder = new Notification.Action.Builder(R.mipmap.ic_launcher,"测试",pend);
//            mBuilder.addAction(builder.build());
//        }
            Notification notification = mBuilder.build();
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mRemoteViews.setOnClickPendingIntent(R.id.music_status_bar_icon,pendButtonIntent);
            //R.id.trackname为你要监听按钮的id
        mRemoteViews.setOnClickPendingIntent(R.id.trackname,pendButtonIntent);

            registerReceiver(onClickReceiver, filter);

            notification.bigContentView=remoteViews;

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(STATUS_BAR_COVER_CLICK_ACTION)) {
                    //在这里处理点击事件
                }
            }

        // mId allows you to update the notification later on.
        mNotificationManager.notify((int)mItemId,notification);
    }

}
