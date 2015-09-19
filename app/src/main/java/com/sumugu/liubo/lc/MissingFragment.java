package com.sumugu.liubo.lc;


import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sumugu.liubo.lc.missing.MissingUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MissingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MissingFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MissingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MissingFragment newInstance(String param1, String param2) {
        MissingFragment fragment = new MissingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MissingFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_missing, container, false);

        MissingUtils missingUtils = new MissingUtils();
        int sms,mms,call;
        sms = missingUtils.getNewSmsCount(getActivity());
        mms = missingUtils.getNewMmsCount(getActivity());
        call = missingUtils.getMissCallCount(getActivity());

        String missingCount = "";
        if(sms>0)
        {
            missingCount = "短信："+sms;
        }
        if(mms>0)
        {
            missingCount += "彩信："+mms;
        }
        if(call>0)
        {
            missingCount += "未连来电："+call;
        }
        if(missingCount.isEmpty())
        {
            Button button = (Button)view.findViewById(R.id.btn_add_missing);
        }


        //换行符 \n
        String smsText = "\n nothing!";

        Cursor cursor = missingUtils.getNewSms(getActivity());
//        if(cursor!=null && cursor.getCount()>0)
//        {
//        }
        //显示头条未读短信内容，仅供原型阿尔法2。这里已经完成读取短信内容，就可以去完成讲未读短信内容插入到Clear里。
//        if(cursor.moveToFirst())
//        {
//            smsText = "\n person:"+cursor.getString(cursor.getColumnIndex("person"));
//            smsText += "\n address:"+cursor.getString(cursor.getColumnIndex("address"));
//            smsText += "\n body:"+cursor.getString(cursor.getColumnIndex("body"));
//            smsText += "\n date:"+ DateFormat.format("yyyy-MM-dd kk:hh:ss", cursor.getLong(cursor.getColumnIndex("date")));
//            smsText += "\n type:"+cursor.getString(cursor.getColumnIndex("type"));
//        }

        TextView textView = (TextView)view.findViewById(R.id.text_missing_content);
        textView.setText(missingCount);

        return view;
    }


    @Override
    public void onClick(View v) {

    }
}
