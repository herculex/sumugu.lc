package com.sumugu.liubo.lc;


import android.content.ClipData;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.daimajia.swipe.SwipeLayout;
import com.sumugu.liubo.lc.contract.ItemContract;
import com.sumugu.liubo.lc.contract.ListContract;

import java.util.Date;
import java.util.Random;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 */
public class ItemFragment extends Fragment implements View.OnClickListener{

    private static final String TAG=ItemFragment.class.getSimpleName();

    //存放释放的控件的变量
//    private EditText mEditTitle;    //阿尔法3已删除
    private EditText mEditContent;
    private Button mButtonCommit;

    private SwipeLayout swipePanelAdding,swipePanelSetting;

    public ItemFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_item, container, false);

        //找到控件并赋值
//        mEditTitle = (EditText)view.findViewById(R.id.editItemTitle); //阿尔法3已删除
        mEditContent = (EditText)view.findViewById(R.id.editItemContent);

        //
        swipePanelAdding = (SwipeLayout)view.findViewById(R.id.panel_adding_ctl);
        swipePanelAdding.setShowMode(SwipeLayout.ShowMode.LayDown);
        swipePanelAdding.addDrag(SwipeLayout.DragEdge.Left, swipePanelAdding.findViewById(R.id.panel_adding_ctl_layer_bottom));
        swipePanelAdding.addDrag(SwipeLayout.DragEdge.Right, null);
        swipePanelAdding.findViewById(R.id.panel_adding_ctl_commit).setOnClickListener(this);
        swipePanelAdding.findViewById(R.id.panel_adding_ctl_cancel).setOnClickListener(this);


        //
        swipePanelSetting = (SwipeLayout)view.findViewById(R.id.panel_setting_ctl);
        swipePanelSetting.setShowMode(SwipeLayout.ShowMode.LayDown);
        swipePanelSetting.addDrag(SwipeLayout.DragEdge.Left, swipePanelSetting.findViewById(R.id.panel_setting_ctl_layer_bottom));
        swipePanelSetting.addDrag(SwipeLayout.DragEdge.Right,null);

        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.panel_adding_ctl_commit:
                Log.d(TAG, "onClicked to commit.");
                new PostTask().execute();
                return;
            case R.id.panel_adding_ctl_cancel:
                startActivity(new Intent(getActivity(),MainListActivity.class));
                return;
            default:
                return;
        }
    }
    private final class PostTask extends AsyncTask<String,Void,String>
    {
        String title = "default_alpha_list"; //阿尔法3，默认Item的标题    //TODO
        String content = mEditContent.getText().toString();

        @Override
        protected String doInBackground(String... params) {
            //TODO: 建设设置项目是否完备

            if(TextUtils.isEmpty(content))
            {
                return "需要输入内容！";
            }

            //新增Item条目
            try
            {
                long listId = getActivity().getIntent().getLongExtra(ListContract.Column.LIST_ID,0);   //获取发送过来意图中的LIST_ID值，默认0

                ContentValues values = new ContentValues();
                values.clear();

//                Random rand = new Random();
//                String idValue = String.valueOf(rand.nextInt(999)+1);   //TODO 不设置，自动产生ID值?

                String idValue = UUID.randomUUID().toString();  //alpha 4,UUID replaced int for the value of id.
                values.put(ItemContract.Column.ITEM_ID,idValue);

                values.put(ItemContract.Column.ITEM_TITLE,title);
                values.put(ItemContract.Column.ITEM_CONTENT,content);
                values.put(ItemContract.Column.ITEM_CREATED_AT,new Date().getTime());
                values.put(ItemContract.Column.ITEM_IS_FINISHED,0);
                values.put(ItemContract.Column.ITEM_HAS_CLOCK,0);
                values.put(ItemContract.Column.ITEM_ALARM_CLOCK,0);
                values.put(ItemContract.Column.ITEM_INDEX,0);   //TODO 需要默认0，以后列表可以排序再处理

                values.put(ItemContract.Column.ITEM_LIST_ID,listId);

                //用Provider插入新数据，而非用DbHelper。在Fragment里，要用Provider，需获取的当前的Activity。
                Uri uri = getActivity().getContentResolver().insert(ItemContract.CONTENT_URI, values);
                if(uri!=null){
                    Log.d(TAG, String.format("%s:%s", values.getAsString(ItemContract.Column.ITEM_TITLE), values.getAsString(ItemContract.Column.ITEM_CONTENT)));
                    return "马上就可以看见！";
                }
                else
                    return "发生可怕的事情！";

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
            mEditContent.setText("");

            Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
        }
    }
}
