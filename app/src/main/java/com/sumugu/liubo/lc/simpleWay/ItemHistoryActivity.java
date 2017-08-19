package com.sumugu.liubo.lc.simpleWay;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.sumugu.liubo.lc.R;
import com.sumugu.liubo.lc.contract.ItemContract;

import static android.os.Build.VERSION.SDK;
import static android.os.Build.VERSION.SDK_INT;

public class ItemHistoryActivity extends Activity {

    ListView mListView;
    TextView mTextClear, mTextBack;

    private String[] FROM = new String[]{
            ItemContract.Column.ITEM_CONTENT,
            ItemContract.Column.ITEM_ALARM_CLOCK,
            ItemContract.Column.ITEM_CREATED_AT,
            ItemContract.Column.ITEM_ID,
            ItemContract.Column.ITEM_CONTENT,
            ItemContract.Column.ITEM_CREATED_AT_DAY
    };
    private int[] TO = new int[]{
            R.id.text_content,
            R.id.text_alarm,
            R.id.text_created_at,
            R.id.text_flag,
            R.id.text_content,
            R.id.text_created_at_day
    };

    private SimpleCursorAdapter.ViewBinder VIEW_BINDER = new SimpleCursorAdapter.ViewBinder() {

        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
            String at_day;
            long item_id;
            switch (view.getId()) {
                case R.id.text_alarm:
                    long alarm = cursor.getLong(columnIndex);
                    if (alarm == 0) {
                        view.setVisibility(View.GONE);
                    } else {
                        String alarmString = DateFormat.format("yyyy-MM-dd HH:mm 提醒", alarm).toString();
                        ((TextView) view).setText(alarmString);
                        view.setVisibility(View.VISIBLE);
                    }
                    return true;
                case R.id.text_created_at:
                    long created = cursor.getLong(columnIndex);
//                    if (created == 0) {
//                        view.setVisibility(View.GONE);
//                    } else {
                    String createdString = DateFormat.format("yyyy-MM-dd HH:mm 创建", created).toString();
                    createdString = DateUtils.getRelativeTimeSpanString(created).toString();
                    ((TextView) view).setText(createdString);
//                    }
                    return true;
                case R.id.text_content:
                    TextView textContent = (TextView) view;
                    textContent.setText(cursor.getString(columnIndex));

                    if (cursor.getInt(cursor.getColumnIndex(ItemContract.Column.ITEM_IS_FINISHED)) == 1) {
                        //增加删除线
                        textContent.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    } else {
                        //取消Flag;
                        textContent.setPaintFlags(0);
                    }
                    return true;
                case R.id.text_flag:

                    TextView textFlag = (TextView) view;
                    String text = "已完成";
                    textFlag.setText(text);

                    return true;

                case R.id.text_created_at_day:
                    TextView textCreatedAtDay = (TextView) view;
                    textCreatedAtDay.setVisibility(View.GONE);

                    return true;
                default:
                    return false;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_history_md);

        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        toolbarLayout.setTitle("这个是CollapsingToolbar");
 /*       //
        mTextBack = (TextView) findViewById(R.id.tv_back);
        mTextClear = (TextView) findViewById(R.id.tv_clear);
        //
        */
        mListView = (ListView) findViewById(R.id.listView);
        if (SDK_INT >= 21)
            mListView.setNestedScrollingEnabled(true);
        //set adapter
        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.itempackage_listview_item, null, FROM, TO, 0);
        simpleCursorAdapter.setViewBinder(VIEW_BINDER);
        mListView.setAdapter(simpleCursorAdapter);
        getLoaderManager().initLoader(0, null, new HistoryLoader(this, simpleCursorAdapter));

        //
/*
        mTextBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
                overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
            }
        });

        mTextClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ItemHistoryActivity.this);
                builder.setTitle("准备");
                builder.setMessage("清除所有历史？");
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setPositiveButton("绝对", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int result = doClearHistory();
                        Toast.makeText(ItemHistoryActivity.this, "吓坏人了," + result, Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("再考虑下", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(ItemHistoryActivity.this, "幸好，留住一些美好和回忆", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });*/
    }

    int doClearHistory() {
        String selection = ItemContract.Column.ITEM_IS_FINISHED + "=1";
        return getContentResolver().delete(ItemContract.CONTENT_URI,
                selection,
                null
        );

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    class HistoryLoader implements LoaderManager.LoaderCallbacks<Cursor> {

        Context mContext;
        SimpleCursorAdapter mSimpleCursorAdapter;

        public HistoryLoader(Context c, SimpleCursorAdapter adapter) {
            mContext = c;
            mSimpleCursorAdapter = adapter;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
            String selection = ItemContract.Column.ITEM_IS_FINISHED + "=1";
            return new CursorLoader(mContext, ItemContract.CONTENT_URI, null, selection, null, ItemContract.Column.ITEM_FINISHED_AT + " DESC");
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            mSimpleCursorAdapter.swapCursor(cursor);

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mSimpleCursorAdapter.swapCursor(null);

        }
    }
}
