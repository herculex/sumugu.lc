package com.sumugu.liubo.lc;

import android.app.LoaderManager;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.sumugu.liubo.lc.alarmclock.AlarmUntils;
import com.sumugu.liubo.lc.contract.ItemContract;
import com.sumugu.liubo.lc.contract.ListContract;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;


public class ItemLineFragment extends android.app.ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = ItemLineFragment.class.getSimpleName();
    //
    //数据：要绑定显示的列表内容，标题TITLE（阿尔法3删除），内容CONTENT，创建时间CREATED_AT，是否完成IS_FINISHED，有闹钟HAS_CLOCK，闹钟时间ALARM_CLOCK。
    private static final String[] FROM = {ItemContract.Column.ITEM_CONTENT,ItemContract.Column.ITEM_CREATED_AT,
    ItemContract.Column.ITEM_HAS_CLOCK,ItemContract.Column.ITEM_ALARM_CLOCK};

    //视图：绑定到布局中具体对应的控件
    private static final int[] TO = {R.id.item_item_text_content,R.id.item_item_text_created_at,
    R.id.item_item_check_has_clock,R.id.item_item_text_clock};

    private static final int LOADER_ID=12;  //这个是一个任意的ID，它帮助我们确保装载器回调的是我们发起的那个。

    private SimpleCursorAdapter mSimpleCursorAdapter;
    private final SimpleCursorAdapter.ViewBinder mVIEW_BINDER = new SimpleCursorAdapter.ViewBinder() {
        @Override
        public boolean setViewValue(View view, final Cursor cursor, final int columnIndex) {
            long timestamp;
            int check;

            //自定义绑定  将时间戳转换成相对时间。无奈SQLite的数据类型很少。
            switch(view.getId()){
                case R.id.item_item_text_clock:
                    timestamp = cursor.getLong(columnIndex);
                    if(timestamp!=0) {
//                    String formatTime = DateFormat.format("yyyy:MM:dd kk:mm:ss",timestamp).toString();
//                    CharSequence time = DateUtils.formatDateTime(getActivity(), timestamp, DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME);

                      CharSequence time = android.text.format.DateFormat.format("yyyy-MM-dd kk:mm:ss",timestamp).toString();
                      ((TextView) view).setText(time);
                    }
                    return true;
                case R.id.item_item_text_created_at:
                    timestamp=cursor.getLong(columnIndex);
                    CharSequence relTime = DateUtils.getRelativeTimeSpanString(timestamp);
                    ((TextView) view).setText(relTime);
                    return true;

//                case R.id.item_item_check_is_finished:
//
//                    final CheckBox checkBox=(CheckBox)view;
//                    checkBox.setChecked(cursor.getInt(columnIndex) == 1 ? true : false);
//
//                    //获取Item的ID值
//                    final long itemId=cursor.getInt(0);
//                    //设置Checkbox的点击事件
//                    checkBox.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
////                            Toast.makeText(getActivity(),itemId+"m:+"+columnIndex+" make me ok!",Toast.LENGTH_LONG).show();
////                            Toast.makeText(getActivity(),+cursor.getInt(0)+"m:+"+columnIndex+" make me ok!",Toast.LENGTH_LONG).show();
////                            注册的事件，里不应该用cursor，因为onItemClick被点击后，cursor会被定位，导致所有的checkbox的onclick都是同一个游标的位置，所以游标变量不应该放在改注册的事件里
////
//                            //原型，获取Item的闹钟
//                            Uri uri = Uri.withAppendedPath(ItemContract.CONTENT_URI,String.valueOf(itemId));
//                            Cursor itemcursor = getActivity().getContentResolver().query(uri, null, null, null, null);
//                            if(!itemcursor.moveToFirst())
//                                return;
//                            long clocktime = itemcursor.getLong(itemcursor.getColumnIndex(ItemContract.Column.ITEM_ALARM_CLOCK));
//                            Calendar calendar = Calendar.getInstance();
//                            calendar.setTimeInMillis(clocktime);
//                            //取消Item的闹钟
//                            String strCancel = new AlarmUntils().CancelAlarmClock(getActivity(), calendar);
//                            Log.d(TAG, "sumugu,cancel lock:" + String.valueOf(itemId) + ":" + strCancel);
//
//                            //原型，设置完成状态，并清除闹钟
//                            ContentValues values = new ContentValues();
//                            values.put(ItemContract.Column.ITEM_IS_FINISHED, ((CheckBox)v).isChecked()?1:0);
//                            values.put(ItemContract.Column.ITEM_HAS_CLOCK,0);
//                            values.put(ItemContract.Column.ITEM_ALARM_CLOCK,0);
//
//                            int count = getActivity().getContentResolver().update(Uri.withAppendedPath(ItemContract.CONTENT_URI, String.valueOf(itemId)), values, null, null);
//                            if(count>0)
//                            {
//                                Toast.makeText(getActivity(),String.valueOf(count)+"finished!"+itemId,Toast.LENGTH_LONG).show();
//                            }
//                        }
//                    });
//                    return true;



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
        Log.d(TAG, "sumugu:list item click at id:" + String.valueOf(id) + " position:" + String.valueOf(position));
//        Toast.makeText(getActivity(),String.valueOf(id)+":"+String.valueOf(position),Toast.LENGTH_LONG).show();//显示ITEM的ID值。

        Intent intentDetail = new Intent(getActivity(),ItemDetailActivity.class);
        intentDetail.putExtra(ItemContract.Column.ITEM_ID,id);
        getActivity().startActivity(intentDetail);

        //原型，点击条目一样，设置完成或取消完成
//        CheckBox checkBox = (CheckBox)v.findViewById(R.id.item_item_check_is_finished);
//
//        ContentValues values = new ContentValues();
//        values.put(ItemContract.Column.ITEM_IS_FINISHED, checkBox.isChecked() ? 0 : 1);
//
//        int count = getActivity().getContentResolver().update(Uri.withAppendedPath(ItemContract.CONTENT_URI, String.valueOf(id)), values, null, null);

        //原型，点击条目快速设置5分钟提醒
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Date());
//        calendar.add(Calendar.MINUTE, 1);
//        String strDate = calendar.get(Calendar.YEAR) + "年" + calendar.get(Calendar.MONTH) + "月" + calendar.get(Calendar.DATE) + "日"
//                + calendar.get(Calendar.HOUR) + "时" + calendar.get(Calendar.MINUTE) + "分" + calendar.get(Calendar.SECOND) + "秒";
//        Log.d(TAG, "sumugu,setting clock:" + strDate);
//
//        ContentValues values =new ContentValues();
//        values.put(ItemContract.Column.ITEM_IS_FINISHED,0);
//        values.put(ItemContract.Column.ITEM_HAS_CLOCK,1);
//        values.put(ItemContract.Column.ITEM_ALARM_CLOCK,calendar.getTimeInMillis());
//
//        //更新数据库Item表对应的Item的信息，设置闹钟
//        int count = getActivity().getContentResolver().update(Uri.withAppendedPath(ItemContract.CONTENT_URI,String.valueOf(id)),values,null,null);
//        if(count>0)
//            Log.d(TAG,"sumugu,clock updated in Db."+String.valueOf(id)+":"+strDate);
//
//        //设置闹钟
//        AlarmUntils alarmUntils = new AlarmUntils();
//        String strClock = alarmUntils.SetAlarmClock(getActivity(), calendar, true, 10);
//        Log.d(TAG,"sumugu,clock installed."+strClock);


    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id!=LOADER_ID)
            return null;
        Log.d(TAG, "onCreateLoader");

//        //CursorLoader 加载来自内容提供器的数据 ItemProvider,通过URI获取列表。要根据LIST_ID,修改第二参数 获取LIST_ID值。阿尔法2
//        String where = ItemContract.Column.ITEM_LIST_ID +"=" +String.valueOf(getActivity().getIntent().getLongExtra(ListContract.Column.LIST_ID,-1));
//        return new CursorLoader(getActivity(),ItemContract.CONTENT_URI,null,where,null,ItemContract.DEFAULT_SORT);

        //ItemProvider 查询所有未完成的Item，不再以LIST_ID进行筛选。阿尔法3
//        String where = "";
        String where = ItemContract.Column.ITEM_IS_FINISHED +"=0";
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
