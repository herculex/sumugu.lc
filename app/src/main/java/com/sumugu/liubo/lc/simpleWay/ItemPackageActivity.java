package com.sumugu.liubo.lc.simpleWay;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.sumugu.liubo.lc.R;
import com.sumugu.liubo.lc.alarmclock.AlarmUntils;
import com.sumugu.liubo.lc.cloud.Upgrade;
import com.sumugu.liubo.lc.cloud.UserActivities;
import com.sumugu.liubo.lc.contract.ItemContract;
import com.sumugu.liubo.lc.simpleWay.fragments.ItemLineFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class ItemPackageActivity extends AppCompatActivity implements ItemLineFragment.OnItemActionCallback {

    final static int REQUEST_CODE_ALARM = 0;
    View popupView;
    PopupWindow mPopupWindow;
    TextInputEditText editText;
    TextView alarmText;
    long mId = 0;
    long mOldAlarmClock = 0;
    long mNewAlarmClock = 0;
    boolean mIsFinished = false;
    AlarmUntils alarmUntils = new AlarmUntils();
    ArrayList<View> viewArrayList;
    ArrayList<Fragment> fragmentArrayList;
    TabLayout tabLayout;


    @Override
    protected void onResume() {
        super.onResume();
//        UserActivities.trackActivity(this,"onResume");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_package_md);

        //MARK: check upgrade and track
        Upgrade.checkUpgradeInfoFromCloud(this, new Upgrade.OnCheckedListener() {

            @Override
            public void finished(int vercode, final String url) {
                Snackbar.make(findViewById(R.id.main_body), "发现新版本", Snackbar.LENGTH_LONG).setAction("查看", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    }
                }).setActionTextColor(Color.WHITE).setDuration(5500).show();
            }
        });
        UserActivities.trackActivity(this, "onCreate");
        //end.

        //PopupWindow setup
        popupView = getLayoutInflater().inflate(R.layout.popup_window_dialog, null);
        mPopupWindow = new PopupWindow(popupView, CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT, true);
//        mPopupWindow.setFocusable(true);
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
                editText.clearFocus();
                alarmText.setText(R.string.label_setting_alarm);
                mOldAlarmClock = 0;
                mNewAlarmClock = 0;
                mIsFinished = false;
                //rest window alpha to 1f
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
        //
        Button btnSave = (Button) popupView.findViewById(R.id.btn_press_save);
        editText = (TextInputEditText) popupView.findViewById(R.id.edit_content);
        alarmText = (TextView) popupView.findViewById(R.id.text_alarm);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (savingItem(mId, editText.getText().toString()) > 0) {
//                    Toast.makeText(ItemPackageActivity.this, "saved ok.", Toast.LENGTH_SHORT).show();
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

        ////PopupWindow setup end.

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.inflateMenu(R.menu.menu_item);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_additem) {
                    popupEditWindow();
                }
                return true;
            }
        });

        ItemLineFragment frag1 = new ItemLineFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putString(ItemLineFragment.TITLE, getString(R.string.fragment_title_history));
        bundle1.putInt(ItemLineFragment.WHAT_TYPE, ItemLineFragment.TYPE_HISTORY);
        frag1.setArguments(bundle1);

        ItemLineFragment frag2 = new ItemLineFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putString(ItemLineFragment.TITLE, getString(R.string.fragment_title_plan));
        bundle2.putInt(ItemLineFragment.WHAT_TYPE, ItemLineFragment.TYPE_PLAN);
        frag2.setArguments(bundle2);

        ItemLineFragment frag3 = new ItemLineFragment();
        Bundle bundle3 = new Bundle();
        bundle3.putString(ItemLineFragment.TITLE, getString(R.string.fragment_title_reminder));
        bundle3.putInt(ItemLineFragment.WHAT_TYPE, ItemLineFragment.TYPE_REMINDER);
        frag3.setArguments(bundle3);


        fragmentArrayList = new ArrayList<>();
        fragmentArrayList.add(frag1);
        fragmentArrayList.add(frag2);
        fragmentArrayList.add(frag3);

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), fragmentArrayList);
        ViewPager viewpager = (ViewPager) findViewById(R.id.viewpager);
        viewpager.setAdapter(adapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewpager);
        viewpager.setCurrentItem(1);

        viewArrayList = new ArrayList<>();
//
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            View tabView = getLayoutInflater().inflate(R.layout.tab_custom_view, null);
            tabLayout.getTabAt(i).setCustomView(tabView);
            viewArrayList.add(tabView);
        }

        ItemLineFragment.OnItemLoaderFinishedCallback listener = new ItemLineFragment.OnItemLoaderFinishedCallback() {
            @Override
            public void action(int count, int lineType, String title) {
                switch (lineType) {
                    case ItemLineFragment.TYPE_REMINDER:
                        View customView = viewArrayList.get(2);
                        TextView countText = (TextView) customView.findViewById(R.id.text_count);
                        TextView titleText = (TextView) customView.findViewById(R.id.text_title);
                        countText.setText(String.valueOf(count));
                        titleText.setText(title);
                        break;
                    case ItemLineFragment.TYPE_PLAN:
                        customView = viewArrayList.get(1);
                        countText = (TextView) customView.findViewById(R.id.text_count);
                        countText.setText(String.valueOf(count));
                        titleText = (TextView) customView.findViewById(R.id.text_title);
                        titleText.setText(title);
                        break;
                    case ItemLineFragment.TYPE_HISTORY:
                        customView = viewArrayList.get(0);
                        countText = (TextView) customView.findViewById(R.id.text_count);
                        countText.setText(String.valueOf(count));
                        titleText = (TextView) customView.findViewById(R.id.text_title);
                        titleText.setText(title);

                        break;
                }
//                Toast.makeText(ItemPackageActivity.this, "here is echo from type:" + lineType + "=" + count, Toast.LENGTH_SHORT).show();
            }
        };
        frag1.setOnItemLoaderFinishedCallback(listener);
        frag2.setOnItemLoaderFinishedCallback(listener);
        frag3.setOnItemLoaderFinishedCallback(listener);

    }

    void turnSoftInputOn(boolean on, EditText edit) {
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (on)
            //打开软键盘
            mgr.showSoftInput(edit, InputMethodManager.SHOW_IMPLICIT);
        else
            //隐藏软键盘
            mgr.hideSoftInputFromWindow(edit.getWindowToken(), 0);
    }

    void popupEditWindow() {
        View vi = findViewById(R.id.main_body);
        int[] location = new int[2];
        vi.getLocationInWindow(location);
        mPopupWindow.showAtLocation(vi, Gravity.TOP, 0, location[1]);

        editText.setFocusable(true);
        editText.requestFocus();
        editText.setSelection(editText.length());

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);
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

            //test notification //
//            Intent noti = new Intent(ItemPackageActivity.this, NotifyIntentService.class);
//            noti.putExtra(ItemContract.Column.ITEM_ID, id);
//            startService(noti);
            //test end.

        } else {

            //create (insert)
            values.put(ItemContract.Column.ITEM_TITLE, R.string.app_name);
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
//                Toast.makeText(this, "created ok." + uri.getLastPathSegment(), Toast.LENGTH_SHORT).show();
            }

        }
        return result;
    }

    @Deprecated
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
//            Toast.makeText(this, "it's ItemContent'detail come back.", Toast.LENGTH_SHORT).show();
        }
        if (requestCode == 2) {
//            Toast.makeText(this, "it's ItemContent'create come back", Toast.LENGTH_SHORT).show();
        }
        if (requestCode == REQUEST_CODE_ALARM) {
            TextView textAlarm = (TextView) popupView.findViewById(R.id.text_alarm);

            if (null == textAlarm) {
//                Toast.makeText(this, data.getLongExtra("alarmclock", 0) + "no more text_alarm", Toast.LENGTH_SHORT).show();
                return;
            }

            if (resultCode == RESULT_OK) {
                long alarm = data.getLongExtra("alarmclock", 0);
                if (alarm == 0) {
//                    Toast.makeText(this, "cancel alarm done.", Toast.LENGTH_SHORT).show();
                    mNewAlarmClock = alarm;
                    textAlarm.setText(R.string.label_setting_alarm);
                } else {
                    mNewAlarmClock = alarm;
                    textAlarm.setText(DateFormat.format("yyyy-MM-dd HH:mm 提醒", alarm));
                }

            } else if (resultCode == RESULT_CANCELED) {

//                Toast.makeText(this, "没想好？", Toast.LENGTH_SHORT).show();

            } else {
//                Toast.makeText(this, "what the fuck happend?!", Toast.LENGTH_SHORT).show();
                textAlarm.setText("what's * happend?!");
            }
        }
    }

    @Override
    public void edit(long id) {
        if (id > 0) {
            initalingItemDetail(id);
            mId = id;
            popupEditWindow();
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

        int result = getContentResolver().update(ItemContract.CONTENT_URI, values, where, args);

    }

    @Override
    public void delete(long id) {
        cancelAlarmClock(id);

        String where = ItemContract.Column.ITEM_ID + "=?";
        String[] args = new String[]{String.valueOf(id)};
        int result = getContentResolver().delete(ItemContract.CONTENT_URI, where, args);
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
            return ((ItemLineFragment) mFragments.get(position)).getTitle();
        }
    }


}
