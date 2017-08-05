package com.sumugu.liubo.lc.simpleWay;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.sumugu.liubo.lc.R;
import com.sumugu.liubo.lc.contract.ItemContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemPackageActivity extends Activity {

    ArrayList<Long> mItemIDList = new ArrayList<>();
    //    ArrayList<String> mCreatedAtDayList = new ArrayList<>();
    HashMap<String, Long> mCreatedAtDayHash = new HashMap<>();
    HashMap<String, List<Long>> mIdIndexHash = new HashMap<>();

    private ListView mListView;
    private String[] arrayString = new String[]{"hello", "simpleway", "protein", "the way we found.", "do samething for",
            "today I want to make a choice", "path for right direction", "what we selected was right on the way?", "keep going on."};

    private String[] FROM = new String[]{ItemContract.Column.ITEM_CONTENT,
            ItemContract.Column.ITEM_ALARM_CLOCK,
            ItemContract.Column.ITEM_CREATED_AT,
            ItemContract.Column.ITEM_IS_FINISHED,
            ItemContract.Column.ITEM_CREATED_AT_DAY
    };
    private int[] TO = new int[]{R.id.text_content,
            R.id.text_alarm,
            R.id.text_created_at,
            R.id.text_finish,
            R.id.text_created_at_day
    };

    private SimpleCursorAdapter.ViewBinder VIEW_BINDER = new SimpleCursorAdapter.ViewBinder() {

        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
            switch (view.getId()) {
                case R.id.text_alarm:
                    long alarm = cursor.getLong(columnIndex);
//                    if (alarm == 0) {
//                        view.setVisibility(View.GONE);
//                    } else {
                    String alarmString = DateFormat.format("yyyy-MM-dd HH:mm 提醒", alarm).toString();
                    ((TextView) view).setText(alarmString);
//                    }
                    return true;
                case R.id.text_created_at:
                    long created = cursor.getLong(columnIndex);
//                    if (created == 0) {
//                        view.setVisibility(View.GONE);
//                    } else {
                    String createdString = DateFormat.format("yyyy-MM-dd HH:mm 创建", created).toString();
                    ((TextView) view).setText(createdString);
//                    }
                    return true;
                case R.id.text_finish:
                    TextView textView = (TextView) view.findViewById(R.id.text_finish);

                    if (cursor.getInt(columnIndex) == 1) {
                        textView.setText("完成");

                    } else {
                        textView.setText("未完成");
                    }
                    return true;
                case R.id.text_content:

                    TextView textContent = (TextView) view.findViewById(R.id.text_content);
                    String text = cursor.getString(columnIndex);
                    long item_id = cursor.getLong(cursor.getColumnIndex(ItemContract.Column.ITEM_ID));
                    String at_day = cursor.getString(cursor.getColumnIndex(ItemContract.Column.ITEM_CREATED_AT_DAY));

                    if (mIdIndexHash.isEmpty() || !mIdIndexHash.containsKey(at_day)) {
                        ArrayList<Long> ids = new ArrayList<>();
                        mIdIndexHash.put(at_day, ids);
                    }

                    if (mIdIndexHash.containsKey(at_day)) {
                        ArrayList<Long> l = (ArrayList<Long>) mIdIndexHash.get(at_day);
                        if (l.isEmpty() || !l.contains(item_id)) {
                            l.add(item_id);
                        }
                    }

//                    if (mItemIDList.isEmpty() || !mItemIDList.contains(item_id)) {
//                        mItemIDList.add(item_id);
//                    }

                    ArrayList<Long> al = (ArrayList<Long>) mIdIndexHash.get(at_day);

                    text = "A" + String.valueOf(al.indexOf(item_id)) + " " + text;
                    textContent.setText(text);

                    if (cursor.getInt(cursor.getColumnIndex(ItemContract.Column.ITEM_IS_FINISHED)) == 1) {
                        //增加删除线
                        textContent.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    } else
                        //取消Flag;
                        textContent.setPaintFlags(0);
                    return true;

                case R.id.text_created_at_day:
                    TextView textCreatedAtDay = (TextView) view;
                    long itemId = cursor.getLong(cursor.getColumnIndex(ItemContract.Column.ITEM_ID));
                    String day = cursor.getString(columnIndex);
                    textCreatedAtDay.setText(day);

                    if (mCreatedAtDayHash.isEmpty() || !mCreatedAtDayHash.containsKey(day)) {
                        mCreatedAtDayHash.put(day, itemId);
                    }

                    if (mCreatedAtDayHash.containsValue(itemId)) {
                        textCreatedAtDay.setVisibility(View.VISIBLE);
                    } else
                        textCreatedAtDay.setVisibility(View.GONE);

                    return true;
                default:
                    return false;
            }
        }
    };

    static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_package);

        mListView = (ListView) findViewById(R.id.listView);

        //for test listview
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.itempackage_listview_item, R.id.text_content, arrayString);
//        mListView.setAdapter(adapter);

        //
        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.itempackage_listview_item, null, FROM, TO, 0);
        simpleCursorAdapter.setViewBinder(VIEW_BINDER);
        mListView.setAdapter(simpleCursorAdapter);
        getLoaderManager().initLoader(1, null, new ItemsLoader(this, simpleCursorAdapter));


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView = (TextView) view.findViewById(R.id.text_content);
                String content = "position:" + i;
                content += ",id:" + l;
                content += ",content:" + textView.getText().toString();

                Toast.makeText(ItemPackageActivity.this, content, Toast.LENGTH_SHORT).show();

                Intent itemContent = new Intent(ItemPackageActivity.this, ItemContentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putLong("id", l);    //get id of item inside of list
                bundle.putString("content", textView.getText().toString()); //get content
                itemContent.putExtras(bundle);
                startActivityForResult(itemContent, 1);
            }
        });

        TextView textCreate = (TextView) findViewById(R.id.tv_create);
        textCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent itemContent = new Intent(ItemPackageActivity.this, ItemContentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", "000");
                bundle.putString("content", "create new content");
                itemContent.putExtras(bundle);
                startActivityForResult(itemContent, 2);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            Toast.makeText(this, "it's ItemContent'detail come back.", Toast.LENGTH_SHORT).show();
        }
        if (requestCode == 2) {
            Toast.makeText(this, "it's ItemContent'create come back", Toast.LENGTH_SHORT).show();
        }
    }

    class ItemsLoader implements LoaderManager.LoaderCallbacks<Cursor> {

        SimpleCursorAdapter mSimpleCursorAdapter;
        Context mContext;

        public ItemsLoader(Context context, SimpleCursorAdapter adapter) {
            mSimpleCursorAdapter = adapter;
            mContext = context;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
            String selection = ItemContract.Column.ITEM_IS_FINISHED + "=0";
            return new CursorLoader(mContext, ItemContract.CONTENT_URI, null, selection, null, ItemContract.DEFAULT_SORT);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            mCreatedAtDayHash.clear();
            mIdIndexHash.clear();
            mSimpleCursorAdapter.swapCursor(cursor);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mCreatedAtDayHash.clear();
            mIdIndexHash.clear();
            mSimpleCursorAdapter.swapCursor(null);

        }
    }
}
