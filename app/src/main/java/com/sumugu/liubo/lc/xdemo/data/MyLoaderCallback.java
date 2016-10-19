package com.sumugu.liubo.lc.xdemo.data;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.CursorAdapter;

import com.sumugu.liubo.lc.contract.ItemContract;

/**
 * Created by liubo on 16/8/16.
 */
public class MyLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {

    private Context mContext;
    private CursorAdapter mCursorAdapter;
    private int loaderId;

    public MyLoaderCallback(Context context, CursorAdapter adapter, int id) {
        mContext = context;
        mCursorAdapter = adapter;
        loaderId = id;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if (loaderId != i)
            return null;

        String where = ItemContract.Column.ITEM_IS_FINISHED + "=0";
        return new CursorLoader(mContext, ItemContract.CONTENT_URI, null, null, null, ItemContract.DEFAULT_SORT);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor.getCount() > 0) {
            mCursorAdapter.swapCursor(cursor);
        } else {
            //insert a new item
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
