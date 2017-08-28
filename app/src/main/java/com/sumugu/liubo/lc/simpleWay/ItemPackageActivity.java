package com.sumugu.liubo.lc.simpleWay;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.sumugu.liubo.lc.R;
import com.sumugu.liubo.lc.alarmclock.AlarmUntils;
import com.sumugu.liubo.lc.contract.ItemContract;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ItemPackageActivity extends AppCompatActivity implements ItemHistoryFragment.OnItemActionCallback {

    final static int REQUEST_CODE_ALARM = 0;
    View popupView;
    PopupWindow mPopupWindow;
    EditText editText;
    TextView alarmText;
    long mId = 0;
    long mOldAlarmClock = 0;
    long mNewAlarmClock = 0;
    boolean mIsFinished = false;
    AlarmUntils alarmUntils = new AlarmUntils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_package_md);

        popupView = getLayoutInflater().inflate(R.layout.popup_window_dialog, null);
        mPopupWindow = new PopupWindow(popupView, CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));//API 23 以下必须有才能点击外部或"back"键消失
        mPopupWindow.setAnimationStyle(R.style.PopupAnimation);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //rest everything
                mId = 0;
                editText.setText("");
                alarmText.setText("here set alarm ");
                mOldAlarmClock = 0;
                mNewAlarmClock = 0;
                mIsFinished = false;
            }
        });
        //
        Button btnSave = (Button) popupView.findViewById(R.id.btn_press_save);
        editText = (EditText) popupView.findViewById(R.id.edit_content);
        alarmText = (TextView) popupView.findViewById(R.id.text_alarm);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (savingItem(mId, editText.getText().toString()) > 0) {
                    Toast.makeText(ItemPackageActivity.this, "saved ok.", Toast.LENGTH_SHORT).show();
                    //set alarm clock here when saved was OK.
                    setUpAlarmClock();

                } else {
                    Toast.makeText(ItemPackageActivity.this, "saved not ok", Toast.LENGTH_SHORT).show();
                }

                mPopupWindow.dismiss();
            }
        });


        alarmText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemPackageActivity.this, DatePickerActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ALARM);
            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_item_package);

        ItemHistoryFragment frag1 = new ItemHistoryFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putString(ItemHistoryFragment.TITLE, "history");
        bundle1.putInt(ItemHistoryFragment.WHAT_TYPE, ItemHistoryFragment.TYPE_HISTORY);
        frag1.setArguments(bundle1);

        ItemHistoryFragment frag2 = new ItemHistoryFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putString(ItemHistoryFragment.TITLE, "plan");
        bundle2.putInt(ItemHistoryFragment.WHAT_TYPE, ItemHistoryFragment.TYPE_PLAN);
        frag2.setArguments(bundle2);

        ItemHistoryFragment frag3 = new ItemHistoryFragment();
        Bundle bundle3 = new Bundle();
        bundle3.putString(ItemHistoryFragment.TITLE, "reminder");
        bundle3.putInt(ItemHistoryFragment.WHAT_TYPE, ItemHistoryFragment.TYPE_REMINDER);
        frag3.setArguments(bundle3);

        ArrayList<Fragment> list = new ArrayList<>();
        list.add(frag1);
        list.add(frag2);
        list.add(frag3);

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), list);
        ViewPager viewpager = (ViewPager) findViewById(R.id.viewpager);
        viewpager.setAdapter(adapter);

        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewpager);
        viewpager.setCurrentItem(1);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_lc);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                openCreateContent();
                mPopupWindow.showAtLocation(findViewById(R.id.main_body), Gravity.TOP, 0, 0);

            }
        });

    }

    void setUpAlarmClock() {
        if (mIsFinished)
            return;

        Calendar old = Calendar.getInstance();
        Calendar now = Calendar.getInstance();

        if (mOldAlarmClock > 0)
            old.setTimeInMillis(mOldAlarmClock);
        else
            old = null;

        if (mNewAlarmClock > 0)
            now.setTimeInMillis(mNewAlarmClock);
        else
            now = null;

        alarmUntils.setAlarmClock(ItemPackageActivity.this, now, old, true, 60 * 1000, mId);
    }

    int savingItem(long id, String content) {
        int result = 0;
        ContentValues values = new ContentValues();

        if (id > 0) {
            //update
            values.put(ItemContract.Column.ITEM_CONTENT, content);
            values.put(ItemContract.Column.ITEM_ALARM_CLOCK, mNewAlarmClock);

            Uri uri = Uri.withAppendedPath(ItemContract.CONTENT_URI, String.valueOf(id));
            String where = ItemContract.Column.ITEM_ID + "=?";
            String[] paras = new String[]{String.valueOf(id)};

            result = getContentResolver().update(uri, values, where, paras);
        } else {

            //create (insert)
            values.put(ItemContract.Column.ITEM_TITLE, "a5");
            values.put(ItemContract.Column.ITEM_CONTENT, content);
            values.put(ItemContract.Column.ITEM_CREATED_AT, new Date().getTime());
            values.put(ItemContract.Column.ITEM_CREATED_AT_DAY, DateFormat.format("yyyy-MM-dd", new Date().getTime()).toString());
            values.put(ItemContract.Column.ITEM_IS_FINISHED, false);
            values.put(ItemContract.Column.ITEM_FINISHED_AT, 0);
            values.put(ItemContract.Column.ITEM_HAS_CLOCK, 0);
            values.put(ItemContract.Column.ITEM_ALARM_CLOCK, mNewAlarmClock);

            values.put(ItemContract.Column.ITEM_LIST_ID, 0);//default is 0

            Uri uri = getContentResolver().insert(ItemContract.CONTENT_URI, values);
            mId = Integer.valueOf(uri.getLastPathSegment());

            if (mId > 0) {
                result = 1;
                Toast.makeText(this, "created ok." + uri.getLastPathSegment(), Toast.LENGTH_SHORT).show();
            }

        }
        return result;
    }


    void openCreateContent() {
        Intent itemContent = new Intent(ItemPackageActivity.this, ItemContentActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("id", "0");
        bundle.putString("content", "create new content");
        itemContent.putExtras(bundle);
        startActivityForResult(itemContent, 2);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            Toast.makeText(this, "it's ItemContent'detail come back.", Toast.LENGTH_SHORT).show();
        }
        if (requestCode == 2) {
            Toast.makeText(this, "it's ItemContent'create come back", Toast.LENGTH_SHORT).show();
        }
        if (requestCode == REQUEST_CODE_ALARM) {
            TextView textAlarm = (TextView) popupView.findViewById(R.id.text_alarm);

            if (null == textAlarm) {
                Toast.makeText(this, data.getLongExtra("alarmclock", 0) + "no more text_alarm", Toast.LENGTH_SHORT).show();
                return;
            }

            if (resultCode == RESULT_OK) {
                long alarm = data.getLongExtra("alarmclock", 0);
                if (alarm == 0) {
                    Toast.makeText(this, "cancel alarm done.", Toast.LENGTH_SHORT).show();
                    mNewAlarmClock = alarm;
                    textAlarm.setText("点击 '这里' 设置提醒闹钟");
                } else {
                    mNewAlarmClock = alarm;
                    textAlarm.setText(DateFormat.format("yyyy-MM-dd HH:mm 提醒", alarm));
                }

            } else if (resultCode == RESULT_CANCELED) {

                Toast.makeText(this, "没想好？", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "what the fuck happend?!", Toast.LENGTH_SHORT).show();
                textAlarm.setText("what the fuck happend?!");
            }
        }
    }

    @Override
    public void edit(long id) {
        if (id > 0) {
            initalingItemDetail(id);
            mId = id;
            mPopupWindow.showAtLocation(findViewById(R.id.main_body), Gravity.TOP, 0, 0);
        }
    }

    @Override
    public void finish(long id) {
        cancelAlarmClock(id);

        String where = ItemContract.Column.ITEM_ID + "=?";
        String[] args = new String[]{String.valueOf(id)};
        ContentValues values = new ContentValues();

        values.put(ItemContract.Column.ITEM_IS_FINISHED, true);
        values.put(ItemContract.Column.ITEM_FINISHED_AT, new Date().getTime());

        getContentResolver().update(ItemContract.CONTENT_URI, values, where, args);

    }

    @Override
    public void delete(long id) {
        cancelAlarmClock(id);

        String where = ItemContract.Column.ITEM_ID + "=?";
        String[] args = new String[]{String.valueOf(id)};
        getContentResolver().delete(ItemContract.CONTENT_URI, where, args);
    }


    void initalingItemDetail(long id) {
        Uri uri = Uri.withAppendedPath(ItemContract.CONTENT_URI, String.valueOf(id));
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor == null)
            return;

        if (!cursor.moveToFirst()) {
            cursor.close();
            return;
        }

        String content = cursor.getString(cursor.getColumnIndex(ItemContract.Column.ITEM_CONTENT));
        mIsFinished = cursor.getInt(cursor.getColumnIndex(ItemContract.Column.ITEM_IS_FINISHED)) == 1;
        mOldAlarmClock = cursor.getLong(cursor.getColumnIndex(ItemContract.Column.ITEM_ALARM_CLOCK));
        mNewAlarmClock = mOldAlarmClock;

        if (mOldAlarmClock > 0)
            alarmText.setText(DateFormat.format("yyyy-MM-dd HH:mm 提醒", mOldAlarmClock));

        editText.setText(content);
        editText.setSelection(content.length());

        cursor.close();
    }

    long getAlarmClock(long id) {
        Cursor cursor = getContentResolver().query(Uri.withAppendedPath(ItemContract.CONTENT_URI, String.valueOf(id)), null, null, null, null);
        if (cursor == null)
            return 0;
        else if (cursor.moveToFirst()) {
            long clock = cursor.getLong(cursor.getColumnIndex(ItemContract.Column.ITEM_ALARM_CLOCK));
            cursor.close();
            return clock;
        }
        return 0;
    }

    void cancelAlarmClock(long id) {
        long alarm = getAlarmClock(id);

        if (alarm > 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(alarm);

            alarmUntils.cancelAlarmClock(ItemPackageActivity.this, calendar, id);
        }
    }

    public class PagerAdapter extends FragmentPagerAdapter {

        ArrayList<Fragment> mFragments;

        public PagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
            super(fm);
            mFragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return ((ItemHistoryFragment) mFragments.get(position)).getTitle();
        }
    }


}
