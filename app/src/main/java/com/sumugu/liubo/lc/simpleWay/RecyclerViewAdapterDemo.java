package com.sumugu.liubo.lc.simpleWay;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sumugu.liubo.lc.R;

import java.util.ArrayList;

/**
 * Created by liubo on 2017/8/21.
 */

public class RecyclerViewAdapterDemo extends RecyclerView.Adapter<RecyclerViewAdapterDemo.ViewHolder> {

    Context mContext;
    ArrayList<String> mDatas;

    public RecyclerViewAdapterDemo(Context context, ArrayList<String> datas) {
        mContext = context;
        mDatas = datas;
    }

    @Override
    public RecyclerViewAdapterDemo.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View itemView = inflater.inflate(R.layout.simpleway_listview_item, parent, false);

        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapterDemo.ViewHolder holder, int position) {

        String string = mDatas.get(position);
        TextView textView = holder.contentView;
        textView.setText(string);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView contentView;

        public ViewHolder(View itemView) {
            super(itemView);

            contentView = (TextView) itemView.findViewById(R.id.text_content);
        }
    }
}

