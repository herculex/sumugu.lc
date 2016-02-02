package com.sumugu.liubo.lc;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.sumugu.liubo.lc.contract.ItemContract;
import com.sumugu.liubo.lc.provider.ItemProvider;
import com.sumugu.liubo.lc.ui.MyListView;

public class ItemLineFrameActivity extends Activity {

    final static String TAG = "lc_ItemLineFrameActivity";
    private MyListView myListView;
    private MyCursorAdapter myCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_line_frame);

        myListView = (MyListView)findViewById(R.id.list_view);
    }

    public class MyCursorAdapter extends CursorAdapter{

        public MyCursorAdapter(Context context,Cursor cursor,int flag){
            super(context,cursor,flag);
        }
        public MyCursorAdapter(Context context,Cursor cursor,boolean autoRequst){
            super(context,cursor,autoRequst);
        }

        class MyViewHolder{
            TextView textView;
            EditText editText;
            TextView deleteView;
            TextView doneView;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            //找到布局文件，保存控件到Holder
            MyViewHolder holder = new MyViewHolder();
            View view = getLayoutInflater().inflate(R.layout.item_frame,null);

            holder.textView=(TextView)view.findViewById(R.id.text_content);
            holder.editText=(EditText)view.findViewById(R.id.edit_content);

            view.setTag(holder);
            //返回view，会自动传给bindView方法。
            return view;

        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            //接收view的holder里的控件对象，然后赋值
            MyViewHolder holder = (MyViewHolder)view.getTag();

            String content = cursor.getString(cursor.getColumnIndex(ItemContract.Column.ITEM_CONTENT));

            holder.textView.setText(content);
            holder.editText.setText(content);

            //完成赋值
        }

    }
}
