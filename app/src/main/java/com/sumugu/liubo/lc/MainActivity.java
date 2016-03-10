package com.sumugu.liubo.lc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.LoaderManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Paint;
import android.net.Uri;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sumugu.liubo.lc.contract.ItemContract;
import com.sumugu.liubo.lc.contract.ListContract;
import com.sumugu.liubo.lc.lockscreen.LockScreenService;
import com.sumugu.liubo.lc.lockscreen.LockScreenUtils;
import com.sumugu.liubo.lc.missing.MissingUtils;

import java.util.Date;
import java.util.Random;

////Named Alpha5 -2016.03.09 PM10:27 marked.

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
    private ListView mListView;
    private CursorAdapter mCursorAdapter;
    private MyLoaderCallback mLoaderCallback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContainerBottom = (LinearLayout)findViewById(R.id.layer_bottom);
        mContainerUnlock = (LinearLayout)findViewById(R.id.layer_unlock);
        mContainerContent = (LinearLayout)findViewById(R.id.layer_content);

        mContainerUnlock.setOnTouchListener(mUnlockOnTouchListener);

        mListView = (ListView)findViewById(R.id.lv_lite);
        mCursorAdapter = new MyCursorAdapter(this,null,10);
        mListView.setAdapter(mCursorAdapter);
        mLoaderCallback = new MyLoaderCallback(this,mCursorAdapter,9);
        getLoaderManager().initLoader(9,null,mLoaderCallback);

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

        //
        displayUndoCount();

    }

    // TODO: 16/3/10 ListView Lite的列表，Item滑动 向左划开 出现“删除”和“完成”
    // TODO: 16/3/10 动画未完成，还有一次能划开一行，划其他行，原先打开的行要复位。

    private View mSwipedView;
    private View.OnTouchListener mListItemOnTouchListener = new View.OnTouchListener() {

        private float mDownX;
        private boolean mSwiping;
        private boolean mPressed;
        private boolean mSwiped;
        @Override
        public boolean onTouch(View view, MotionEvent event) {

            final LinearLayout containerItem = (LinearLayout)view.getParent();

            switch (event.getActionMasked()){

                case MotionEvent.ACTION_DOWN:
                    if(!mPressed){
                        mPressed=true;
                        mDownX=event.getX();
                        break;
                    }else
                        return false;

                case MotionEvent.ACTION_CANCEL:
                    mPressed=false;
                    mSwiping=false;
                    mDownX=0;
                    containerItem.setTranslationX(0);
                    break;

                case MotionEvent.ACTION_MOVE:
                    float x = event.getX();
                    float deltaX=x-mDownX;
                    float absDeltaX=Math.abs(deltaX);
                    if(absDeltaX>0){
                        mSwiping=true;
                        mListView.requestDisallowInterceptTouchEvent(true);
                    }
                    if(mSwiping){
                        if(mContainerContent.getTranslationX()>0) {
                            mContainerContent.setTranslationX(deltaX + mContainerContent.getTranslationX());
                        }
                        else {
                            mContainerContent.setTranslationX(0);
                            containerItem.setTranslationX(deltaX + containerItem.getTranslationX());
                        }
                    }
                    if(deltaX>0){
                        float maxX=containerItem.getWidth()/3;
                        if(containerItem.getTranslationX()>=maxX){
                            containerItem.setTranslationX(maxX);
                            //grade than 1/3 containerItem then tranX the containerContent
                            mContainerContent.setTranslationX(deltaX + mContainerContent.getTranslationX());
                        }

                    }else{
                        float maxX=findViewById(R.id.tv_delete_hint).getWidth()+findViewById(R.id.tv_done).getWidth();
                        Log.d(TAG, "maxX=" + String.valueOf(maxX));

                        if(Math.abs(containerItem.getTranslationX())>=maxX)
                            containerItem.setTranslationX(-maxX);
                    }
                    break;
                case MotionEvent.ACTION_UP:

                    //if grade than 1/3 TranX of containerContent,then open it's detail
                    //get itemId from adapter ,build the intent with extra data with it,start it.
                    //else TranX containerContent and item to 0 with animation
                    float canTranX=mContainerContent.getTranslationX();
                    if(canTranX>mContainerContent.getWidth()/3) {
                        long itemId = mCursorAdapter.getItemId(mListView.getPositionForView(view));
                        Intent intentDetail = new Intent(MainActivity.this, ItemDetailActivity.class);
                        intentDetail.putExtra(ItemContract.Column.ITEM_ID, itemId);
                        startActivity(intentDetail);

                        mContainerContent.animate()
                                .alpha(0)
                                .setDuration(150);
                    }
                    else{
                        if(canTranX>0){
                            float a=canTranX/mContainerContent.getWidth();
                            float b=1-a;
                            long stand=250;
                            long canDuration=(int)(b*stand);

                            mContainerContent.animate()
                                    .translationX(0)
                                    .setDuration(canDuration);
                        }
                        float itemTranX=containerItem.getTranslationX();
                        long duration=(int)((1-itemTranX/containerItem.getWidth())*250);
                        if(itemTranX>0) {
                            //itemTranX>0 mean being swiped to right -->

                            containerItem.animate()
                                    .translationX(0)
                                    .setDuration(duration);
                        }
                        else{
                            //itemTranx<0 mean being swiped to left <--

                            float absItemTranX=Math.abs(itemTranX);
                            float leftMaxTrans=findViewById(R.id.tv_done).getWidth()+findViewById(R.id.tv_delete_hint).getWidth();
//                            Log.d(TAG,String.valueOf(leftMaxTrans)+":"+String.valueOf(itemTranX));
                            if(absItemTranX>=leftMaxTrans){
                                mSwiped=true;
                            }
                            else{
                                if(absItemTranX>leftMaxTrans/2){
                                    containerItem.setTranslationX(-leftMaxTrans);// TODO: 16/3/10 需要动画
                                    mSwiped=true;
                                }
                                else{
                                    containerItem.setTranslationX(0);
                                }
                            }

                            if(mSwiped)
                                Log.d(TAG,"containerItem swiped:"+((TextView)view.findViewById(R.id.tv_content)).getText());

                        }
                    }

                    mDownX=0;
                    mSwiping=false;
                    mPressed=false;
                    break;
                default:
                    return false;
            }
            return true;
        }
    };

    // 向右unlock,向左进入itemline.需要完成.unlock 控制mContainerConent的X轴位置
    // 控制的mContainerContent的动画
    // TODO: 16/3/10 完成,需完善.

    private View.OnTouchListener mUnlockOnTouchListener = new View.OnTouchListener() {

        private float mDownX;
        private boolean mSwiping;
        private boolean mPressed;

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            switch (motionEvent.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    //不允许多点
                    if (!mPressed) {
                        mPressed=true;
                        mDownX = motionEvent.getX();
                        break;
                    } else
                        return false;

                case MotionEvent.ACTION_CANCEL:

                    mContainerContent.setTranslationX(0);
                    mPressed=false;
                    mDownX=0;
                    mSwiping=false;

                    break;

                case MotionEvent.ACTION_MOVE:

                    float deltaX=motionEvent.getX()-mDownX;
                    float absDeltaX=Math.abs(deltaX);
                    if(absDeltaX>0)
                        mSwiping=true;

                    if(mSwiping){
                        float x = motionEvent.getX()-mDownX;
                        mContainerContent.setTranslationX(x);
                    }

                    break;

                case MotionEvent.ACTION_UP:

                    float tranX=mContainerContent.getTranslationX();
                    if(Math.abs(tranX)>mContainerContent.getWidth()/2){
                        if(tranX>0){
                            //swiping to right to UNLOCK
                            mContainerContent.animate()
                                    .alpha(0)
                                    .setDuration(250)
                                    .withEndAction(new Runnable() {
                                        @Override
                                        public void run() {
                                            unlockHomeButton();
                                        }
                                    });
                        }
                        else{
                            //swiping to left to enter the ItemLine
                            mContainerContent.animate()
                                    .alpha(0)
                                    .setDuration(250)
                                    .withEndAction(new Runnable() {
                                        @Override
                                        public void run() {
                                            startActivity(new Intent(MainActivity.this, ItemLineFrameActivity.class));
                                        }
                                    });
                        }
                    }
                    else {
                        float a=tranX/mContainerContent.getWidth();
                        float c=1-a;
                        long returnDuration=250;
                        long duration=(int)(returnDuration*c);

                        mContainerContent.animate()
                                .translationX(0)
                                .alpha(1)
                                .setDuration(duration);
                    }
                    mDownX=0;
                    mPressed=false;
                    mSwiping=false;

                    break;
                default:
                    return false;
            }
            return true;
        }
    };

    private void displayUndoCount()
    {
        //// TODO: 16/3/10 选择当天未完成的条数(按提醒闹钟）
        String where = ItemContract.Column.ITEM_IS_FINISHED+"=0";
        Cursor cursor=getContentResolver().query(ItemContract.CONTENT_URI,null,where,null,ItemContract.DEFAULT_SORT);
        if(cursor!=null){
            int count = cursor.getCount();
            if(count>0){
                TextView textView = (TextView)findViewById(R.id.tv_count_hint);
                textView.setText("你今天有 "+String.valueOf(count)+" 项需要清除的！" );
            }
            cursor.close();
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

    public class MyCursorAdapter extends CursorAdapter {

        public MyCursorAdapter(Context context, Cursor cursor, int flag) {
            super(context, cursor, flag);
        }

        public MyCursorAdapter(Context context, Cursor cursor, boolean autoRequst) {
            super(context, cursor, autoRequst);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            //找到布局文件，保存控件到Holder
            MyViewHolder holder = new MyViewHolder();
            View view = getLayoutInflater().inflate(R.layout.item_frame_lite, null);

            holder.textView = (TextView) view.findViewById(R.id.tv_content);
            holder.deleteView=(TextView)view.findViewById(R.id.tv_delete_hint);
            holder.doneView=(TextView)view.findViewById(R.id.tv_done);

            view.setTag(holder);
            //返回view，会自动传给bindView方法。
            return view;

        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            //接收view的holder里的控件对象，然后赋值
            MyViewHolder holder = (MyViewHolder) view.getTag();

            String id = cursor.getString(cursor.getColumnIndex(ItemContract.Column.ITEM_ID));
            String content = cursor.getString(cursor.getColumnIndex(ItemContract.Column.ITEM_CONTENT));
            long reminder = cursor.getLong(cursor.getColumnIndex(ItemContract.Column.ITEM_ALARM_CLOCK));
            int finish=cursor.getInt(cursor.getColumnIndex(ItemContract.Column.ITEM_IS_FINISHED));

            holder.textView.setText(content);

             //抗锯齿
            holder.textView.getPaint().setAntiAlias(true);

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            if (view != convertView) {

                view.findViewById(R.id.tv_content).setOnTouchListener(mListItemOnTouchListener);
                // TODO: 16/3/10 对删除，完成增加click事件的listener
//                view.findViewById(R.id.tv_done).setOnClickListener();
//                view.findViewById(R.id.tv_delete_hint).setOnClickListener();

            }
            return view;
        }

        class MyViewHolder {
            TextView textView;
            TextView deleteView;
            TextView doneView;
        }
    }
    public class MyLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {
        final static String TAG = "lc_myLoaderCallback";

        CursorAdapter mCursorAdapter;
        int mLoaderId;
        Context mContext;

        public MyLoaderCallback(Context context, CursorAdapter adapter, int id) {
            mContext = context;
            mCursorAdapter = adapter;
            mLoaderId = id;
            Log.d(TAG, "init..");
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (id != mLoaderId)
                return null;
            //未完成的前5五项
            // TODO: 16/3/9 当天未完成的前五项（按提醒闹钟）
            String where = ItemContract.Column.ITEM_IS_FINISHED + "=0";
            return new CursorLoader(mContext, ItemContract.CONTENT_URI, null, where, null, ItemContract.DEFAULT_SORT+" LIMIT 5");

        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mCursorAdapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mCursorAdapter.swapCursor(null);
        }
    }
}
