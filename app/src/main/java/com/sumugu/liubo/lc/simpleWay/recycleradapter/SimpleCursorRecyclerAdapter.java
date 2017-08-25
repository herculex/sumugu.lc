package com.sumugu.liubo.lc.simpleWay.recycleradapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by liubo on 2017/8/22.
 */

public class SimpleCursorRecyclerAdapter extends CursorRecyclerAdapter<SimpleCursorRecyclerAdapter.SimpleViewHolder> {

    ViewBinder mViewBinder;
    onItemClickListner mOnItemClickListenr;
    private int mLayout;
    private int[] mFrom;
    private int[] mTo;
    private String[] mOriginalFrom;

    public SimpleCursorRecyclerAdapter(int layout, Cursor c, String[] from, int[] to) {
        super(c);
        mLayout = layout;
        mTo = to;
        mOriginalFrom = from;
        findColumns(c, from);
    }

    private void findColumns(Cursor c, String[] from) {
        if (c != null) {
            int i;
            int count = from.length;
            if (mFrom == null || mFrom.length != count) {
                mFrom = new int[count];
            }
            for (i = 0; i < count; i++) {
                mFrom[i] = c.getColumnIndexOrThrow(from[i]);
            }
        } else {
            mFrom = null;
        }
    }

    @Override
    public Cursor swapCursor(Cursor c) {
        findColumns(c, mOriginalFrom);
        return super.swapCursor(c);
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mLayout, parent, false);
        return new SimpleViewHolder(view, mTo);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, Cursor cursor) {
        final int count = mTo.length;
        final int[] from = mFrom;

        final long _id = cursor.getInt(mRowIDColumn);

        if (mOnItemClickListenr != null)
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListenr.onClick(view, _id);
                }
            });

        for (int i = 0; i < count; i++) {
            if (mViewBinder == null || !mViewBinder.setViewValue(holder.views[i], cursor, from[i]))
                holder.views[i].setText(cursor.getString(from[i]));
        }
    }

    public void setViewBinder(ViewBinder binder) {
        mViewBinder = binder;
    }

    public void setOnItemClickListner(onItemClickListner listner) {
        mOnItemClickListenr = listner;
    }

    public interface ViewBinder {
        boolean setViewValue(View view, Cursor cursor, int columnIndex);
    }

    public interface onItemClickListner {
        void onClick(View view, long id);
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {
        public View rootView;
        public TextView[] views;

        public SimpleViewHolder(View itemView, int[] to) {
            super(itemView);

            rootView = itemView;
            views = new TextView[to.length];
            for (int i = 0; i < to.length; i++) {
                views[i] = (TextView) itemView.findViewById(to[i]);
            }
        }
    }
}
