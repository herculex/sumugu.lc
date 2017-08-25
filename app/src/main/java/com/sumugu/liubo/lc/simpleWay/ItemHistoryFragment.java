package com.sumugu.liubo.lc.simpleWay;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sumugu.liubo.lc.R;
import com.sumugu.liubo.lc.contract.ItemContract;
import com.sumugu.liubo.lc.simpleWay.recycleradapter.CursorRecyclerAdapter;
import com.sumugu.liubo.lc.simpleWay.recycleradapter.SimpleCursorRecyclerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class ItemHistoryFragment extends Fragment {


    public SimpleCursorRecyclerAdapter.ViewBinder BINDER = new SimpleCursorRecyclerAdapter.ViewBinder() {
        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
            switch (view.getId()) {
                case R.id.text_content:
                    ((TextView) view).setText("mybinder:" + cursor.getString(columnIndex));
                    return true;
                default:
                    return false;
            }
        }
    };
    String mTitle;

    public ItemHistoryFragment() {
        // Required empty public constructor
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_item_history, container, false);

        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        final SimpleCursorRecyclerAdapter simpleCursorRecyclerAdapter = new SimpleCursorRecyclerAdapter(R.layout.simpleway_listview_item, null, new String[]{ItemContract.Column.ITEM_CONTENT}, new int[]{R.id.text_content});

        simpleCursorRecyclerAdapter.setViewBinder(BINDER);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.scrollToPosition(0);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(simpleCursorRecyclerAdapter);
        getLoaderManager().initLoader(0, null, new HistoryLoader(getActivity(), simpleCursorRecyclerAdapter));

        simpleCursorRecyclerAdapter.setOnItemClickListner(new SimpleCursorRecyclerAdapter.onItemClickListner() {
            @Override
            public void onClick(View view, final long id) {
//                Toast.makeText(getActivity(), "click at:" + String.valueOf(id), Toast.LENGTH_SHORT).show();

                View sheet = inflater.inflate(R.layout.bottom_sheet_main, null);
                final BottomSheetDialog sheetDialog = new BottomSheetDialog(getActivity());
                sheetDialog.setContentView(sheet);
                sheetDialog.show();

                sheet.findViewById(R.id.text_item1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int result = getContext().getContentResolver().delete(Uri.withAppendedPath(ItemContract.CONTENT_URI, String.valueOf(id)), null, null);
                        Toast.makeText(getActivity(), result > 0 ? "deleted ok at:" + id : "no one can be deleted at all.", Toast.LENGTH_SHORT).show();
                        sheetDialog.dismiss();
                    }
                });
                sheet.findViewById(R.id.text_item2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getActivity(), "close ok at:" + id, Toast.LENGTH_SHORT).show();
                        sheetDialog.dismiss();
                    }
                });
            }
        });
        return view;
    }

    class HistoryLoader implements LoaderManager.LoaderCallbacks<Cursor> {

        Context mContext;
        CursorRecyclerAdapter mSimpleCursorAdapter;

        public HistoryLoader(Context c, CursorRecyclerAdapter adapter) {
            mContext = c;
            mSimpleCursorAdapter = adapter;
        }

        @Override
        public android.support.v4.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
            String order = " DESC";
            if (bundle != null) {
                order = bundle.getString("order");
            }
            String selection = ItemContract.Column.ITEM_IS_FINISHED + "=1";
            return new android.support.v4.content.CursorLoader(mContext, ItemContract.CONTENT_URI, null, selection, null, ItemContract.Column.ITEM_FINISHED_AT + order);
        }


        @Override
        public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor cursor) {
            mSimpleCursorAdapter.swapCursor(cursor);

        }

        @Override
        public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
            mSimpleCursorAdapter.swapCursor(null);

        }
    }
}
