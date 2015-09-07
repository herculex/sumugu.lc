package com.sumugu.liubo.lc;


import android.content.ClipData;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sumugu.liubo.lc.contract.ItemContract;
import com.sumugu.liubo.lc.contract.ListContract;

import java.util.Date;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class ItemFragment extends Fragment implements View.OnClickListener{

    private static final String TAG=ItemFragment.class.getSimpleName();

    //存放释放的控件的变量
    private EditText mEditTitle;
    private EditText mEditContent;
    private Button mButtonCommit;

    public ItemFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_item, container, false);

        //找到控件并赋值
        mEditContent = (EditText)view.findViewById(R.id.editItemContent);
        mEditTitle = (EditText)view.findViewById(R.id.editItemTitle);
        mButtonCommit = (Button)view.findViewById(R.id.buttonItemCommit);
        mButtonCommit.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View v) {
        String title = mEditTitle.getText().toString();
        String content = mEditContent.getText().toString();

        Log.d(TAG, "onClicked to commit.");
        new PostTask().execute();
    }
    private final class PostTask extends AsyncTask<String,Void,String>
    {
        String title = mEditTitle.getText().toString();
        String content = mEditContent.getText().toString();

        @Override
        protected String doInBackground(String... params) {
            //TODO: 建设设置项目是否完备

            if(TextUtils.isEmpty(mEditContent.getText()) || TextUtils.isEmpty(mEditContent.getText()))
            {
                return "至少输入标题吧";
            }

            //新增List条目
            try
            {
                long listId = getActivity().getIntent().getLongExtra(ListContract.Column.LIST_ID,-1);

                ContentValues values = new ContentValues();
                values.clear();
                Random rand = new Random();
                String idValue = String.valueOf(rand.nextInt(999)+1);   //TODO 不设置，看是否是自动产生ID值

                values.put(ItemContract.Column.ITEM_ID,idValue);
                values.put(ItemContract.Column.ITEM_TITLE,title);
                values.put(ItemContract.Column.ITEM_CONTENT,content);
                values.put(ItemContract.Column.ITEM_CREATED_AT,new Date().getTime());

                values.put(ItemContract.Column.ITEM_LIST_ID,listId);

                //用Provider插入新数据，而非用DbHelper。在Fragment里，要用Provider，需获取的当前的Activity。
                Uri uri = getActivity().getContentResolver().insert(ItemContract.CONTENT_URI, values);
                if(uri!=null){
                    Log.d(TAG, String.format("%s:%s", values.getAsString(ItemContract.Column.ITEM_TITLE), values.getAsString(ItemContract.Column.ITEM_CONTENT)));
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

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
        }
    }
}
