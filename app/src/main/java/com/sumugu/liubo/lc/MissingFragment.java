package com.sumugu.liubo.lc;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.CallLog;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sumugu.liubo.lc.alarmclock.AlarmUntils;
import com.sumugu.liubo.lc.contract.ItemContract;
import com.sumugu.liubo.lc.missing.MissingUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class MissingFragment extends Fragment implements View.OnClickListener {

    public MissingFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_missing, container, false);
        Button button = (Button)view.findViewById(R.id.btn_add_missing);
        button.setOnClickListener(this);


        MissingUtils missingUtils = new MissingUtils();
        int sms,mms,call;
        sms = missingUtils.getNewSmsCount(getActivity());
//        mms = missingUtils.getNewMmsCount(getActivity());
        call = missingUtils.getMissCallCount(getActivity());

        String missingCount = "";
        if(sms>0)
        {
            missingCount = "短信："+sms;
        }
//        取消彩信
//        if(mms>0)
//        {
//            missingCount += " 彩信："+mms;
//        }
        if(call>0)
        {
            missingCount += " 电话："+call;
        }
        if(!missingCount.isEmpty())
        {
            TextView textView = (TextView)view.findViewById(R.id.text_missing_content);
            textView.setText(missingCount);
            button.setText(missingCount);
        }


        return view;

    }


    @Override
    public void onClick(View v) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("5分后提醒？")
                .setCancelable(false)
                .setPositiveButton("是的", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        addToItems();
                    }
                })
                .setNegativeButton("不是的", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void addToItems()
    {
        MissingUtils missingUtils = new MissingUtils();

        //获取短信
        Cursor cursor = missingUtils.getNewSms(getActivity());
        while (cursor.moveToNext())
        {

            String title = cursor.getString(cursor.getColumnIndex("address"));
            String body = cursor.getString(cursor.getColumnIndex("body"));
            if(title==null)
                title="无显示号码 ";
            if(body==null)
                body="无内容 ";
            String content = "回复短信："+title+","+body;

            add(title,content);
        }

        //获取电话
        cursor = missingUtils.getMissCall(getActivity());
        while (cursor.moveToNext())
        {
            String title = cursor.getString(0);
            String number = cursor.getString(1);
            if(title==null)
                title = "陌生人 ";
            if(number==null)
                number = "无显示号码 ";
            String content = "回复电话："+title+","+number;

            add(title,content);
        }
    }

    private void add(String title,String content)
    {
        AlarmUntils alarmUntils = new AlarmUntils();
        Uri uri = ItemContract.CONTENT_URI;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        ContentValues values = new ContentValues();

        Random rand = new Random();
        String idValue = String.valueOf(rand.nextInt(999)+1);   //TODO 不设置，自动产生ID值?

        values.clear();
        values.put(ItemContract.Column.ITEM_ID,idValue);
        values.put(ItemContract.Column.ITEM_LIST_ID, 0);
        values.put(ItemContract.Column.ITEM_TITLE, title);
        values.put(ItemContract.Column.ITEM_CONTENT, content);
        values.put(ItemContract.Column.ITEM_CREATED_AT, calendar.getTimeInMillis());
        values.put(ItemContract.Column.ITEM_IS_FINISHED, 0);
        values.put(ItemContract.Column.ITEM_HAS_CLOCK, 1);
        calendar.add(Calendar.MINUTE, 5);
        values.put(ItemContract.Column.ITEM_ALARM_CLOCK, calendar.getTimeInMillis());
        uri = getActivity().getContentResolver().insert(uri,values);
        if(uri!=null)
        {
            long itemId = Long.parseLong(idValue);
            alarmUntils.SetAlarmClock(getActivity(),calendar,true,60,itemId);
        }
    }
}
