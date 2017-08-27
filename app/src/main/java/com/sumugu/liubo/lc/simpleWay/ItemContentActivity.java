package com.sumugu.liubo.lc.simpleWay;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sumugu.liubo.lc.R;
import com.sumugu.liubo.lc.alarmclock.AlarmUntils;
import com.sumugu.liubo.lc.contract.ItemContract;

import java.util.Calendar;
import java.util.Date;

public class ItemContentActivity extends AppCompatActivity {
    TextView textAlarm, textViewContent, textViewId;
    TextView textDelete, textFinish;
    EditText editContent;
    LinearLayout lineActionZone;

    long mId;
    long mOldAlarmClock = 0;
    long mNewAlarmClock = 0;
    boolean mIsFinished = false;
    AlarmUntils alarmUntils = new AlarmUntils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_content);
//
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_item_content);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.app_bar_save:
                        if (savingItem(mId) > 0) {
                            Toast.makeText(ItemContentActivity.this, "saved ok.", Toast.LENGTH_SHORT).show();
                            //set alarm clock here when saved was OK.
                            setUpAlarmClock();

                        } else {
                            Toast.makeText(ItemContentActivity.this, "saved not ok", Toast.LENGTH_SHORT).show();
                        }

                        setResult(RESULT_OK);
                        finish();
                        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                        return true;
                    default:
                        return false;
                }
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                setResult(RESULT_CANCELED);
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        });

        //
        //
        lineActionZone = (LinearLayout) findViewById(R.id.line_action_zone);
        editContent = (EditText) findViewById(R.id.edit_content);
        textViewId = (TextView) findViewById(R.id.text_id);
        textViewContent = (TextView) findViewById(R.id.text_content);
        textDelete = (TextView) findViewById(R.id.text_delete);
        textFinish = (TextView) findViewById(R.id.text_finish);
        textAlarm = (TextView) findViewById(R.id.text_alarm);


        Bundle bundle = getIntent().getExtras();
        mId = bundle.getLong("id");
        String content = bundle.getString("content");

        if (mId > 0)
            initalingItemDetail(mId);
        else {
            textViewContent.setText("打开键盘写入内容...");
            lineActionZone.setVisibility(View.GONE);
        }


        textViewId.setText(String.valueOf(mId));
//        textViewContent.setText(content);

        textDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deletingItem(mId) > 0) {
                    Toast.makeText(ItemContentActivity.this, "deleted was ok.", Toast.LENGTH_SHORT).show();
                    //cancel alarm clock here when deleted was OK.
                    cancelAlarmClock();
                } else
                    Toast.makeText(ItemContentActivity.this, "deleted nothing.", Toast.LENGTH_SHORT).show();

                setResult(RESULT_OK);
                finish();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        });

        textFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (finishItem(mId) > 0) {
                    Toast.makeText(ItemContentActivity.this, "finish ok. ", Toast.LENGTH_SHORT).show();
                    //cancel alarm clock here when finished was OK.
                    cancelAlarmClock();
                } else
                    Toast.makeText(ItemContentActivity.this, "finish not ok ,again.", Toast.LENGTH_SHORT).show();

                setResult(RESULT_OK);
                finish();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        });

        textAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ItemContentActivity.this, DatePickerActivity.class);
                startActivityForResult(intent, 0);
            }
        });

//        editContent.setInputType(InputType.TYPE_NULL);
        editContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Toast.makeText(ItemContentActivity.this, "you just press ", Toast.LENGTH_SHORT).show();
                }
                return false;
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

        alarmUntils.setAlarmClock(ItemContentActivity.this, now, old, true, 60 * 1000, mId);
    }

    void cancelAlarmClock() {
        if (mOldAlarmClock > 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(mOldAlarmClock);

            alarmUntils.cancelAlarmClock(ItemContentActivity.this, calendar, mId);
        }
    }

    void initalingItemDetail(long id) {
        Uri uri = Uri.withAppendedPath(ItemContract.CONTENT_URI, String.valueOf(id));
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (!cursor.moveToFirst()) {
            cursor.close();
            return;
        }

        String content = cursor.getString(cursor.getColumnIndex(ItemContract.Column.ITEM_CONTENT));
        mIsFinished = cursor.getInt(cursor.getColumnIndex(ItemContract.Column.ITEM_IS_FINISHED)) == 1;
        mOldAlarmClock = cursor.getLong(cursor.getColumnIndex(ItemContract.Column.ITEM_ALARM_CLOCK));
        mNewAlarmClock = mOldAlarmClock;

        if (mOldAlarmClock > 0)
            textAlarm.setText(DateFormat.format("yyyy-MM-dd HH:mm 提醒", mOldAlarmClock));

        editContent.setText(content);
        editContent.setSelection(content.length());

        cursor.close();
    }

    int savingItem(long id) {
        int result = 0;
        ContentValues values = new ContentValues();

        if (id > 0) {
            //update
            values.put(ItemContract.Column.ITEM_CONTENT, editContent.getText().toString());
            values.put(ItemContract.Column.ITEM_ALARM_CLOCK, mNewAlarmClock);

            Uri uri = Uri.withAppendedPath(ItemContract.CONTENT_URI, String.valueOf(id));
            String where = ItemContract.Column.ITEM_ID + "=?";
            String[] paras = new String[]{String.valueOf(id)};

            result = getContentResolver().update(uri, values, where, paras);
        } else {

            //create (insert)
            values.put(ItemContract.Column.ITEM_TITLE, "a5");
            values.put(ItemContract.Column.ITEM_CONTENT, editContent.getText().toString());
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

    int deletingItem(long id) {
        String where = ItemContract.Column.ITEM_ID + "=?";
        String[] args = new String[]{String.valueOf(id)};
        return getContentResolver().delete(ItemContract.CONTENT_URI, where, args);
    }

    int finishItem(long id) {
        String where = ItemContract.Column.ITEM_ID + "=?";
        String[] args = new String[]{String.valueOf(id)};
        ContentValues values = new ContentValues();
        values.put(ItemContract.Column.ITEM_CONTENT, editContent.getText().toString());
        values.put(ItemContract.Column.ITEM_IS_FINISHED, true);
        values.put(ItemContract.Column.ITEM_FINISHED_AT, new Date().getTime());

        return getContentResolver().update(ItemContract.CONTENT_URI, values, where, args);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
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

    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

}
