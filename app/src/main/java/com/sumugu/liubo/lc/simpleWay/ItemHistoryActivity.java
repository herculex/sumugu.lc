package com.sumugu.liubo.lc.simpleWay;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.sumugu.liubo.lc.R;
import com.sumugu.liubo.lc.contract.ItemContract;

import java.util.ArrayList;

public class ItemHistoryActivity extends Activity {

    ListView mListView;
    TextView mTextClear, mTextBack;
    ArrayList<String> mArrayList = new ArrayList<>();
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

    public void onClick(View v) {
        Snackbar.make(v, "这是一个snackBar", Snackbar.LENGTH_LONG).show();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_history_md);

        //start testing recyclerview for array string
        String[] datas = new String[]{
                "Item001", "Item002", "ItemBlackSheep003", "Item004", "Item005", "Item006",
                "ItemGoHomeForSleeping011", "Item012", "Item013", "Item014", "Item015", "Item016",
                "Item021", "Item022", "ItemContent023", "Item024", "Item025", "Item026",
                "ItemHelloWorld031", "Item032", "Item033", "Item034", "Item035", "Item036",
                "Item041", "ItemActivity042", "Item043", "Item044", "ItemContent045", "Item046"};
        for (String string : datas
                ) {
            mArrayList.add(string);
        }
//        for(int i=0;i<datas.length;i++){
//            mArrayList.add(datas[i]);
//        }
/*        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        final HistoryRecyclerViewAdapter historyRecyclerViewAdapter = new HistoryRecyclerViewAdapter(this, mArrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.scrollToPosition(0);

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(historyRecyclerViewAdapter);
        //that's all ??


        Button butt = (Button) findViewById(R.id.btn_add);
        butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mArrayList.add(0, "this new add.");

                historyRecyclerViewAdapter.notifyItemInserted(0);
                recyclerView.scrollToPosition(0);
            }
        });
*/

        //
        // Do testing SimpleCursorAdapter ,and delete item through provider,What happend to Recyclerview in

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        final SimpleCursorRecyclerAdapter simpleCursorRecyclerAdapter = new SimpleCursorRecyclerAdapter(R.layout.simpleway_listview_item, null, new String[]{ItemContract.Column.ITEM_CONTENT}, new int[]{R.id.text_content});

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.scrollToPosition(0);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(simpleCursorRecyclerAdapter);
        getLoaderManager().initLoader(0, null, new HistoryLoader(this, simpleCursorRecyclerAdapter));

        Button butt = (Button) findViewById(R.id.btn_add);
        butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Cursor cursor = simpleCursorRecyclerAdapter.getCursor();
                if (cursor.moveToFirst()) {
                    long itemId = cursor.getLong(cursor.getColumnIndex(ItemContract.Column.ITEM_ID));

                    Uri uri = Uri.withAppendedPath(ItemContract.CONTENT_URI, String.valueOf(itemId));
                    int result = getContentResolver().delete(uri, null, null);
                    Snackbar snackbar = Snackbar.make(view, "delete:" + result, Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }
        });
        //
 /*       //
        mTextBack = (TextView) findViewById(R.id.tv_back);
        mTextClear = (TextView) findViewById(R.id.tv_clear);
        //

        mListView = (ListView) findViewById(R.id.listView);
        if (SDK_INT >= 21)
            mListView.setNestedScrollingEnabled(true);
        //set adapter
        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.itempackage_listview_item, null, FROM, TO, 0);
        simpleCursorAdapter.setViewBinder(VIEW_BINDER);
        mListView.setAdapter(simpleCursorAdapter);
        getLoaderManager().initLoader(0, null, new HistoryLoader(this, simpleCursorAdapter));
        */
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
        CursorRecyclerAdapter mSimpleCursorAdapter;

        public HistoryLoader(Context c, CursorRecyclerAdapter adapter) {
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
