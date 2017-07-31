package com.sumugu.liubo.lc.simpleWay;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sumugu.liubo.lc.R;
import com.sumugu.liubo.lc.contract.ItemContract;

import java.util.Calendar;
import java.util.Date;

public class ItemContentActivity extends Activity {
    TextView textAlarm, textViewContent, textViewId;
    TextView textDelete, textFinish;
    EditText editContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_content);

        editContent = (EditText) findViewById(R.id.edit_content);
        textViewId = (TextView) findViewById(R.id.text_id);
        textViewContent = (TextView) findViewById(R.id.text_content);
        textDelete = (TextView) findViewById(R.id.text_delete);
        textFinish = (TextView) findViewById(R.id.text_finish);


        Bundle bundle = getIntent().getExtras();
        final long mId = bundle.getLong("id");
        String content = bundle.getString("content");

        if (mId > 0)
            initalingItemDetail(mId);
        else
            textViewContent.setText("打开键盘写入内容...");

        textViewId.setText(String.valueOf(mId));
//        textViewContent.setText(content);

        TextView textBack = (TextView) findViewById(R.id.tv_back);
        TextView textSave = (TextView) findViewById(R.id.tv_save);

        textBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        textSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (savingItem(mId) > 0) {
                    Toast.makeText(ItemContentActivity.this, "saved ok.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ItemContentActivity.this, "saved not ok", Toast.LENGTH_SHORT).show();
                }

                setResult(RESULT_OK);
                finish();
            }
        });

        textDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deletingItem(mId) > 0)
                    Toast.makeText(ItemContentActivity.this, "deleted was ok.", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(ItemContentActivity.this, "deleted nothing.", Toast.LENGTH_SHORT).show();

                setResult(RESULT_OK);
                finish();
            }
        });

        textFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (finishItem(mId) > 0)
                    Toast.makeText(ItemContentActivity.this, "finish ok. ", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(ItemContentActivity.this, "finish not ok ,again.", Toast.LENGTH_SHORT).show();

                setResult(RESULT_OK);
                finish();
            }
        });

        textAlarm = (TextView) findViewById(R.id.text_alarm);
        textAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ItemContentActivity.this, DatePickerActivity.class);
                startActivityForResult(intent, 0);
            }
        });
        Calendar calender = Calendar.getInstance();
        textAlarm.setText(calender.get(Calendar.MONTH) + 1 + "," + calender.get(Calendar.DAY_OF_MONTH) + "," + calender.get(Calendar.HOUR_OF_DAY) + "," + calender.get(Calendar.MINUTE));
    }

    void initalingItemDetail(long id) {
        Uri uri = Uri.withAppendedPath(ItemContract.CONTENT_URI, String.valueOf(id));
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (!cursor.moveToFirst()) {
            cursor.close();
            return;
        }

        String content = cursor.getString(cursor.getColumnIndex(ItemContract.Column.ITEM_CONTENT));

        editContent.setText(content);
        editContent.setSelection(content.length());

        cursor.close();
    }

    int savingItem(long id) {
        int result = 0;
        ContentValues values = new ContentValues();

        if (id > 0) {
            values.put(ItemContract.Column.ITEM_CONTENT, editContent.getText().toString());
            Uri uri = Uri.withAppendedPath(ItemContract.CONTENT_URI, String.valueOf(id));
            String where = ItemContract.Column.ITEM_ID + "=?";
            String[] paras = new String[]{String.valueOf(id)};

            result = getContentResolver().update(uri, values, where, paras);
        } else {

            values.put(ItemContract.Column.ITEM_TITLE, "a5");
            values.put(ItemContract.Column.ITEM_CONTENT, editContent.getText().toString());
            values.put(ItemContract.Column.ITEM_CREATED_AT, new Date().getTime());
            values.put(ItemContract.Column.ITEM_IS_FINISHED, 0);
            values.put(ItemContract.Column.ITEM_HAS_CLOCK, 0);
            values.put(ItemContract.Column.ITEM_ALARM_CLOCK, 0);

            values.put(ItemContract.Column.ITEM_LIST_ID, 0);//default is 0

            Uri uri = getContentResolver().insert(ItemContract.CONTENT_URI, values);

            if (uri != null) {
                result = 1;
                Toast.makeText(this, "created ok." + uri.getLastPathSegment(), Toast.LENGTH_SHORT).show();
            }

        }
        return result;
    }

    int deletingItem(long id) {
        String where = ItemContract.Column.ITEM_ID + "=?";
        String[] args = new String[]{String.valueOf(id)};
        int count = getContentResolver().delete(ItemContract.CONTENT_URI, where, args);
        return count;
    }

    int finishItem(long id) {
        String where = ItemContract.Column.ITEM_ID + "=?";
        String[] args = new String[]{String.valueOf(id)};
        ContentValues values = new ContentValues();
        values.put(ItemContract.Column.ITEM_CONTENT, editContent.getText().toString());
        values.put(ItemContract.Column.ITEM_IS_FINISHED, true);

        int count = getContentResolver().update(ItemContract.CONTENT_URI, values, where, args);
        return count;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {

                long alarm = data.getLongExtra("alarmclock", 0);
                if (alarm == 0) {
                    Toast.makeText(this, "cancel alarm done.", Toast.LENGTH_SHORT).show();
                    textAlarm.setText("无提醒");
                } else {
                    textAlarm.setText(DateFormat.format("yyyy-MM-dd hh:mm:ss", alarm));
                }

            } else if (resultCode == RESULT_CANCELED) {

                Toast.makeText(this, "没选好", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "what the fuck happend?!", Toast.LENGTH_SHORT).show();
                textAlarm.setText("what the fuck happend?!");
            }
        }
    }


}
