package com.sumugu.liubo.lc.xdemo.data;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.sumugu.liubo.lc.R;
import com.sumugu.liubo.lc.contract.ItemContract;
import com.sumugu.liubo.lc.xdemo.MyCustomItem;

/**
 * Created by liubo on 16/8/16.
 */
public class MyCursorAdapter extends CursorAdapter {

    private Context mContext;

    public MyCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        mContext = context;
    }

    public MyCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        ViewHolder holder = new ViewHolder();
        View view = View.inflate(context, R.layout.xdemo_list_item, null);
        holder.textView = (TextView) view.findViewById(R.id.textView);
//        holder.customItem = (MyCustomItem)view.findViewById(R.id.customItem);

        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();

        String id = cursor.getString(cursor.getColumnIndex(ItemContract.Column.ITEM_ID));
        String content = cursor.getString(cursor.getColumnIndex(ItemContract.Column.ITEM_CONTENT));
        long reminder = cursor.getLong(cursor.getColumnIndex(ItemContract.Column.ITEM_ALARM_CLOCK));
        int finish = cursor.getInt(cursor.getColumnIndex(ItemContract.Column.ITEM_IS_FINISHED));

        holder.textView.setText(content);
//        holder.customItem.setText(content);

        //ok finished.
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view =  super.getView(position, convertView, parent);
        if(view != convertView){
            //
        }
        return view;
    }

    private class ViewHolder {
        public TextView textView;
//        public MyCustomItem customItem;
    }
}
