package com.sumugu.liubo.lc;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.sumugu.liubo.lc.contract.ListContract;

import java.util.Date;
import java.util.Random;

/**

 */
public class ListFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = ListFragment.class.getSimpleName();

    //定义布局控件将释放到内容的变量
    //标题
    private EditText mEditTextTitle;
    //内容
    private EditText mEditTextContent;
    //提交
    private Button mButtonCommit;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //装入布局
        View view = inflater.inflate(R.layout.fragment_list,container,false);

        //找到控件，并赋值变量
        mButtonCommit = (Button)view.findViewById(R.id.buttonListCommit);
        mEditTextContent = (EditText)view.findViewById(R.id.editListContent);
        mEditTextTitle = (EditText)view.findViewById(R.id.editListTitle);

        mButtonCommit.setOnClickListener(this); //注册按钮的onclick响应事件

        return view;
    }

    @Override
    public void onClick(View v) {

    }

    //AsyncTask <Params,Progress,Result>
    private final class PostTask extends AsyncTask<String,Void,String>
    {
        String title = mEditTextTitle.getText().toString();
        String content = mEditTextContent.getText().toString();

        @Override
        protected String doInBackground(String... params) {
            //TODO: 建设设置项目是否完备

            if(TextUtils.isEmpty(mEditTextContent.getText()) || TextUtils.isEmpty(mEditTextTitle.getText()))
            {
                return "至少输入标题吧";
            }

            //新增List条目
            try
            {
                ContentValues values = new ContentValues();
                values.clear();
                Random rand = new Random();
                String idValue = String.valueOf(rand.nextInt(999)+1);
                values.put(ListContract.Column.LIST_ID,idValue);
                values.put(ListContract.Column.LIST_TITLE,title);
                values.put(ListContract.Column.LIST_CONTENT,content);
                values.put(ListContract.Column.LIST_CREATED_AT,new Date().getTime());

                //用Provider插入新数据，而非用DbHelper。在Fragment里，要用Provider，需获取的当前的Activity。
                Uri uri = getActivity().getContentResolver().insert(ListContract.CONTENT_URI, values);
                if(uri!=null){
                    Log.d(TAG,String.format("%s:%s", values.getAsString(ListContract.Column.LIST_TITLE),values.getAsString(ListContract.Column.LIST_CONTENT)));
                    return "Successfully posted!";
                }
                else
                    return "What's wrong!";

            }catch (Exception e)
            {
                e.printStackTrace();
                Log.e(TAG,e.toString());
                return e.toString();
            }
        }
    }

}
