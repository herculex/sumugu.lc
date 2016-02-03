package com.sumugu.liubo.lc;

import android.app.Activity;
import android.app.LauncherActivity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sumugu.liubo.lc.contract.ItemContract;
import com.sumugu.liubo.lc.ui.MyListView;

public class ItemLineFrameActivity extends Activity {

    final static String TAG = "lc_ItemLineFrame";
    private MyListView myListView;
    private MyCursorAdapter myCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_line_frame);

        myListView = (MyListView)findViewById(R.id.list_view);
        myCursorAdapter = new MyCursorAdapter(this,null,0);
        myListView.setAdapter(myCursorAdapter);

        getLoaderManager().initLoader(5, null, new MyLoaderCallback(this, myCursorAdapter, 5));

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "item position=" + String.valueOf(position) + ",id=" + String.valueOf(id));
            }
        });
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

            String id =cursor.getString(cursor.getColumnIndex(ItemContract.Column.ITEM_ID));
            String content = cursor.getString(cursor.getColumnIndex(ItemContract.Column.ITEM_CONTENT));

            holder.textView.setText(content+":"+id);
            holder.editText.setText(content);

            //完成赋值
        }

    }

    public class MyLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor>
    {
        final static String TAG="lc_myLoaderCallback";

        CursorAdapter mCursorAdapter;
        int mLoaderId;
        Context mContext;
        public MyLoaderCallback(Context context,CursorAdapter adapter,int id)
        {
            mContext =context;
            mCursorAdapter =adapter;
            mLoaderId =id;
            Log.d(TAG,"init..");
        }
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if(id!= mLoaderId)
                return null;

            String where = ItemContract.Column.ITEM_IS_FINISHED + "=0";
            return new CursorLoader(mContext,ItemContract.CONTENT_URI,null,null,null,ItemContract.DEFAULT_SORT);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mCursorAdapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mCursorAdapter.swapCursor(null);
        }
    }
}
