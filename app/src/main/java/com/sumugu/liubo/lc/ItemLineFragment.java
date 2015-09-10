package com.sumugu.liubo.lc;

import android.app.LoaderManager;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.sumugu.liubo.lc.contract.ItemContract;
import com.sumugu.liubo.lc.contract.ListContract;


public class ItemLineFragment extends android.app.ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = ItemLineFragment.class.getSimpleName();
    //
    //数据：要绑定显示的列表内容，标题TITLE，内容CONTENT，创建时间CREATED_AT，是否完成IS_FINISHED，有闹钟HAS_CLOCK，闹钟时间ALARM_CLOCK。
    private static final String[] FROM = {ItemContract.Column.ITEM_TITLE,ItemContract.Column.ITEM_CONTENT,ItemContract.Column.ITEM_CREATED_AT,
    ItemContract.Column.ITEM_IS_FINISHED,ItemContract.Column.ITEM_HAS_CLOCK,ItemContract.Column.ITEM_ALARM_CLOCK};

    //视图：绑定到布局中具体对应的控件
    private static final int[] TO = {R.id.item_item_text_title,R.id.item_item_text_content,R.id.item_item_text_created_at,
    R.id.item_item_check_is_finished,R.id.item_item_check_has_clock};

    private static final int LOADER_ID=12;  //这个是一个任意的ID，它帮助我们确保装载器回调的是我们发起的那个。

    private SimpleCursorAdapter mSimpleCursorAdapter;
    private final SimpleCursorAdapter.ViewBinder mVIEW_BINDER = new SimpleCursorAdapter.ViewBinder() {
        @Override
        public boolean setViewValue(View view, final Cursor cursor, final int columnIndex) {
            long timestamp;
            int check;

            //自定义绑定  将时间戳转换成相对时间。无奈SQLite的数据类型很少。
            switch(view.getId()){
                case R.id.item_item_text_created_at:
                    timestamp=cursor.getLong(columnIndex);
                    CharSequence relTime = DateUtils.getRelativeTimeSpanString(timestamp);
                    ((TextView) view).setText(relTime);
                    return true;
                case R.id.item_item_check_has_clock:

                    ((CheckBox) view).setChecked(cursor.getInt(columnIndex)==1 ? true:false);

                    return true;
                case R.id.item_item_check_is_finished:

                    final CheckBox checkBox=(CheckBox)view;
                    checkBox.setChecked(cursor.getInt(columnIndex) == 1 ? true : false);

                    final long itemId=cursor.getInt(0);
                    checkBox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            Toast.makeText(getActivity(),itemId+"m:+"+columnIndex+" make me ok!",Toast.LENGTH_LONG).show();
//                            Toast.makeText(getActivity(),+cursor.getInt(0)+"m:+"+columnIndex+" make me ok!",Toast.LENGTH_LONG).show();
//                            注册的事件，里不应该用cursor，因为onItemClick被点击后，cursor会被定位，导致所有的checkbox的onclick都是同一个游标的位置，所以游标变量不应该放在改注册的事件里
//
                            ContentValues values = new ContentValues();
                            values.put(ItemContract.Column.ITEM_IS_FINISHED, ((CheckBox)v).isChecked()?1:0);

                            int count = getActivity().getContentResolver().update(Uri.withAppendedPath(ItemContract.CONTENT_URI, String.valueOf(itemId)), values, null, null);
//                            if(count>0)
//                            {
//                                Toast.makeText(getActivity(),String.valueOf(count)+"updated."+itemId,Toast.LENGTH_LONG).show();
//                            }
                        }
                    });
                    return true;

                default:
                    return false;
            }
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //获取ListLineFragment，点击List后发送过来的数据，LIST_ID.
        long listId = getActivity().getIntent().getLongExtra(ListContract.Column.LIST_ID,-1);
//        Toast.makeText(getActivity(),String.valueOf (listId),Toast.LENGTH_LONG).show();

        setEmptyText("加载 "+String.valueOf(listId)+":Loading Items ...");

        //将一个自定义列表布局文件（fragment_item_line.xml），并按FROM数据 TO视图 的映射关系对应加载数据
        mSimpleCursorAdapter = new SimpleCursorAdapter(getActivity(),R.layout.fragment_item_line,null,FROM,TO,0);

        mSimpleCursorAdapter.setViewBinder(mVIEW_BINDER);

        setListAdapter(mSimpleCursorAdapter);

        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();

//        updateItemLine(); TODO
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        //TODO on click item to finish it
        Log.d(TAG, "list item click " + String.valueOf(id));
        Toast.makeText(getActivity(),String.valueOf(id)+":"+String.valueOf(position),Toast.LENGTH_LONG).show();//显示ITEM的ID值。

        //点击条目一样，设置完成或取消完成
        CheckBox checkBox = (CheckBox)v.findViewById(R.id.item_item_check_is_finished);

        ContentValues values = new ContentValues();
        values.put(ItemContract.Column.ITEM_IS_FINISHED, checkBox.isChecked() ? 0 : 1);

        int count = getActivity().getContentResolver().update(Uri.withAppendedPath(ItemContract.CONTENT_URI, String.valueOf(id)), values, null, null);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id!=LOADER_ID)
            return null;
        Log.d(TAG, "onCreateLoader");
        //CursorLoader 加载来自内容提供器的数据 ItemProvider,通过URI获取列表。要根据LIST_ID,修改第二参数 获取LIST_ID值
        String where = ItemContract.Column.ITEM_LIST_ID +"=" +String.valueOf(getActivity().getIntent().getLongExtra(ListContract.Column.LIST_ID,-1));

        return new CursorLoader(getActivity(),ItemContract.CONTENT_URI,null,where,null,ItemContract.DEFAULT_SORT);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(null == cursor) {
            setEmptyText("发生了想象不到的事情！");
            Log.d(TAG, "cursor is NULL!!");
        }
        else {
            int count = cursor.getCount();
            setEmptyText(count==0 ? "什么都没有，点击 + 添加。":"");
            Log.d(TAG, "onLoadFinished with cursor:" + cursor.getCount());
        }

        mSimpleCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mSimpleCursorAdapter.swapCursor(null);
    }

}
