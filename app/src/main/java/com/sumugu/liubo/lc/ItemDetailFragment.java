package com.sumugu.liubo.lc;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.sumugu.liubo.lc.contract.ItemContract;


/**
 * A simple {@link Fragment} subclass.
 */
public class ItemDetailFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = ItemDetailFragment.class.getSimpleName();
    private TextView itemContent;
    private TextView createdAtText;
    private TextView timerText;

    private SwipeLayout swipelayoutFinish,swipelayoutSetting;
    private String mItemId="";

    public ItemDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_item_detail, container, false);
        //find all views
        timerText = (TextView)view.findViewById(R.id.panel_setting_ctl_timertext);
        itemContent = (TextView)view.findViewById(R.id.editItemContent);
        createdAtText = (TextView)view.findViewById(R.id.panel_finish_ctl_createdAt);

        //
        Intent intent = getActivity().getIntent();
        mItemId = intent.getStringExtra(ItemContract.Column.ITEM_ID);
        updateView(mItemId);
        //


        //完成控制板
        swipelayoutFinish = (SwipeLayout)view.findViewById(R.id.panel_finish_ctl);
        swipelayoutFinish.setShowMode(SwipeLayout.ShowMode.PullOut);
        swipelayoutFinish.addDrag(SwipeLayout.DragEdge.Left, swipelayoutFinish.findViewById(R.id.panel_finish_ctl_layer_bottom));
        swipelayoutFinish.addDrag(SwipeLayout.DragEdge.Right, null);

        //完成控制板的各项OnClick
        swipelayoutFinish.findViewById(R.id.panel_finish_ctl_commit).setOnClickListener(this);
        swipelayoutFinish.findViewById(R.id.panel_finish_ctl_delete).setOnClickListener(this);


        //设置提醒控制板
        swipelayoutSetting = (SwipeLayout)view.findViewById(R.id.panel_setting_ctl);
        swipelayoutSetting.setShowMode(SwipeLayout.ShowMode.PullOut);
        swipelayoutSetting.addDrag(SwipeLayout.DragEdge.Left, swipelayoutSetting.findViewById(R.id.panel_setting_ctl_layer_bottom));
        swipelayoutSetting.addDrag(SwipeLayout.DragEdge.Right, null);

        //设置提醒控制板的各项OnClick
        swipelayoutSetting.findViewById(R.id.panel_setting_ctl_timercancel).setOnClickListener(this);
        swipelayoutSetting.findViewById(R.id.panel_setting_ctl_timer1).setOnClickListener(this);
        swipelayoutSetting.findViewById(R.id.panel_setting_ctl_timer2).setOnClickListener(this);
        swipelayoutSetting.findViewById(R.id.panel_setting_ctl_timer3).setOnClickListener(this);

        return view;
    }
    private void updateView(String id)
    {
        if(id=="" || id.isEmpty()) {
            itemContent.setText("什么都没有！");
            return;
        }
        //根据ItemID查询结果
//        Uri uri = ContentUris.withAppendedId(ItemContract.CONTENT_URI, id);
        Uri uri = Uri.withAppendedPath(ItemContract.CONTENT_URI,id);

        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        if(!cursor.moveToFirst())
            return;

        //获取查询结果各项值
        String content = cursor.getString(cursor.getColumnIndex(ItemContract.Column.ITEM_CONTENT));
        long createdAt = cursor.getLong(cursor.getColumnIndex(ItemContract.Column.ITEM_CREATED_AT));
        long alarmclock = cursor.getLong(cursor.getColumnIndex(ItemContract.Column.ITEM_ALARM_CLOCK));
        long hasClock = cursor.getLong(cursor.getColumnIndex(ItemContract.Column.ITEM_HAS_CLOCK));
        long isFinished = cursor.getLong(cursor.getColumnIndex(ItemContract.Column.ITEM_IS_FINISHED));

        //设置各项UI内容
        itemContent.setText(content);
        createdAtText.setText(DateUtils.getRelativeTimeSpanString(createdAt));
        if(alarmclock>0) {
            timerText.setText(android.text.format.DateFormat.format("yyyy-MM-dd kk:mm:ss", alarmclock));
        }
    }
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.panel_setting_ctl_timercancel:
                //TODO
                return;
            case R.id.panel_setting_ctl_timer1:
                setAlarmClock(1);   //1分钟后提醒
                backToItemLine();
                return;
            case R.id.panel_setting_ctl_timer2:
                setAlarmClock(5);   //5分钟后提醒
                backToItemLine();
                return;
            case R.id.panel_setting_ctl_timer3:
                setAlarmClock(15);  //15分钟后提醒
                backToItemLine();
                return;
            case R.id.panel_finish_ctl_commit:
                finishItem();       //完成
                backToItemLine();
                return;
            case R.id.panel_finish_ctl_delete:
                deleteItem();       //删除
                backToItemLine();
                return;
            default:
                return;
        }
    }

    private void backToItemLine()
    {
//        startActivity(new Intent(getActivity(),ComboItemLineActivity.class));
        startActivity(new Intent(getActivity(),MainListActivity.class));
    }

    private void setAlarmClock(int interval) {

//        //定义各种以下用到的变量
//        AlarmUntils alarmUntils = new AlarmUntils();
//        Calendar calendar = Calendar.getInstance();
//        Uri uri = Uri.withAppendedPath(ItemContract.CONTENT_URI, String.valueOf(mItemId));
//
//        //step 1，找出已经设置的闹钟提醒，并取消之。
//        cancelAlarmClock();
//
//        //step 2，设置新的闹钟并更新纪录。
//        //设置新闹钟
//        calendar.setTime(new Date());
//        calendar.add(Calendar.MINUTE, interval);
//
//        //重复提醒，间隔1分钟
//        String result = alarmUntils.SetAlarmClock(getActivity(),calendar,true,60*1000,mItemId);
//
//        //step 3，更新记录的闹钟信息
//        ContentValues values = new ContentValues();
//        values.put(ItemContract.Column.ITEM_IS_FINISHED,0);
//        values.put(ItemContract.Column.ITEM_HAS_CLOCK,1);
//        values.put(ItemContract.Column.ITEM_ALARM_CLOCK,calendar.getTimeInMillis());
//
//        int count = getActivity().getContentResolver().update(uri,values,null,null);
//
//        Toast.makeText(getActivity(), "已经设置" + interval + "后再见！", Toast.LENGTH_SHORT).show();

    }

    private void cancelAlarmClock()
    {
//        //定义各种以下用到的变量
//        AlarmUntils alarmUntils = new AlarmUntils();
//        Calendar calendar = Calendar.getInstance();
//        Uri uri = Uri.withAppendedPath(ItemContract.CONTENT_URI, String.valueOf(mItemId));
//
//        //找出已经设置的闹钟提醒，并取消之。
//        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
//        if(!cursor.moveToFirst()) {
//            return;
//        }
//
//        int hasAlarm = cursor.getInt(cursor.getColumnIndex(ItemContract.Column.ITEM_HAS_CLOCK));
//        long alarmClock = cursor.getLong(cursor.getColumnIndex(ItemContract.Column.ITEM_ALARM_CLOCK));
//
//        if(hasAlarm>0)
//        {
//            // /取消闹钟
//            calendar.setTimeInMillis(alarmClock);
//            String result = alarmUntils.CancelAlarmClock(getActivity(),calendar,mItemId);
////            Toast.makeText(ItemDetailActivity.this, result, Toast.LENGTH_SHORT).show();
//            Log.d(TAG, "sumugu:" + result);
//        }
    }

    private void finishItem() {

//        Uri uri = Uri.withAppendedPath(ItemContract.CONTENT_URI,String.valueOf(mItemId));

        Uri uri = ItemContract.CONTENT_URI;
        String where = ItemContract.Column.ITEM_ID + "=?";
        String[] params = new String[] {mItemId};

        //step 1，查处闹钟并取消掉
        cancelAlarmClock();

        //step 2，更新记录的闹钟和标记
        ContentValues values = new ContentValues();
        values.put(ItemContract.Column.ITEM_IS_FINISHED, 1);
        values.put(ItemContract.Column.ITEM_HAS_CLOCK,0);
        values.put(ItemContract.Column.ITEM_ALARM_CLOCK,0);

        int count = getActivity().getContentResolver().update(uri,values,where,params);
//        int count = getContentResolver().update(Uri.withAppendedPath(ItemContract.CONTENT_URI,String.valueOf(mItemId)),values,null,null);

        if(count>0)
        {
            Toast.makeText(getActivity(), "继续努力！", Toast.LENGTH_SHORT).show();
            Log.d(TAG,"sumugu:finish item"+mItemId);
        }
    }

    private void deleteItem() {

        //step 1，查处闹钟并取消
        cancelAlarmClock();

        //创建访问内容提供器的URI
        Uri uri = Uri.withAppendedPath(ItemContract.CONTENT_URI,mItemId);

        //step 2，删除指定ID的纪录
        int count = getActivity().getContentResolver().delete(uri,null,null);

        //删除成功提示
        if(count>0)
        {
            Toast.makeText(getActivity(), "已经删除！", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "sumugu:delete item:" + mItemId);
        }
    }
}
