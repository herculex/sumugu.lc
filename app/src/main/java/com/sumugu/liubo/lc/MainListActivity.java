package com.sumugu.liubo.lc;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.sumugu.liubo.lc.contract.ItemContract;

public class MainListActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    static final String[]  PROJECTION = new String[] {ItemContract.Column.ITEM_ID,ItemContract.Column.ITEM_CONTENT};
    static final String SELECTION = "";

    CursorAdapter cursorAdapter;
    SwipeLayout mainList;

    class ViewHolder{
        TextView display_text;
        TextView archire_text;
        TextView delete_text;
        ImageView magnifier2_image;
        ImageView star2_image;
        ImageView trash2_image;
        View starboot;
        View bottom_wrapper;
        View bottom_wrapper_2;
        SwipeLayout swipeLayout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);


        mainList = (SwipeLayout)findViewById(R.id.main_list);
        mainList.setShowMode(SwipeLayout.ShowMode.PullOut);

        View searching = findViewById(R.id.layer_searching);
        View filter = findViewById(R.id.layer_filter);
        View setting = findViewById(R.id.layer_setting);

        mainList.addDrag(SwipeLayout.DragEdge.Right, filter);
        mainList.addDrag(SwipeLayout.DragEdge.Left,setting);
        mainList.addDrag(SwipeLayout.DragEdge.Top, searching);


        EditText searchingText = (EditText)findViewById(R.id.searching_text);
        searchingText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainList.open(true,true, SwipeLayout.DragEdge.Top);
                Toast.makeText(MainListActivity.this, "click the searching texting", Toast.LENGTH_SHORT).show();
            }
        });

        TextView filterUndo = (TextView)findViewById(R.id.filter_undo);
        TextView filterDone = (TextView)findViewById(R.id.filter_done);
        TextView filterExpired = (TextView)findViewById(R.id.filter_expired);
        TextView filterAll = (TextView)findViewById(R.id.filter_all);

        filterUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "click the undo filter", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        filterDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainListActivity.this, "click the done", Toast.LENGTH_SHORT).show();
            }
        });
        filterExpired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainListActivity.this, "click the expired", Toast.LENGTH_SHORT).show();
            }
        });
        filterAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainListActivity.this, "click the all", Toast.LENGTH_SHORT).show();
            }
        });

        //adapter building
        cursorAdapter = new CursorAdapter(this,null,0) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                ViewHolder holder = new ViewHolder();
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.item_swipe_item,null);

                holder.display_text = (TextView)view.findViewById(R.id.displayText);
                holder.archire_text = (TextView)view.findViewById(R.id.archive);
                holder.delete_text = (TextView)view.findViewById(R.id.delete);
                holder.magnifier2_image = (ImageView)view.findViewById(R.id.magnifier2);
                holder.star2_image = (ImageView)view.findViewById(R.id.star2);
                holder.trash2_image = (ImageView)view.findViewById(R.id.trash2);
                holder.starboot = view.findViewById(R.id.starbott);
                holder.bottom_wrapper = view.findViewById(R.id.bottom_wrapper);
                holder.bottom_wrapper_2 = view.findViewById(R.id.bottom_wrapper_2);
                holder.swipeLayout = (SwipeLayout)view.findViewById(R.id.swipe_sample1);

                view.setTag(holder);
                return view;
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                ViewHolder holder=(ViewHolder)view.getTag();

                String name = cursor.getString(cursor.getColumnIndex(ItemContract.Column.ITEM_CONTENT));
                holder.display_text.setText(name);

                holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
                holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, holder.bottom_wrapper);
                holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, holder.bottom_wrapper_2);

                holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Top,holder.starboot);   //貌似焦点获取不到，只swipe listview 上下
                holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Bottom,holder.starboot);    //貌似焦点获取不到，只swipe listview 上下

                holder.swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
                    @Override
                    public void onDoubleClick(SwipeLayout layout, boolean surface) {
                        Toast.makeText(MainListActivity.this, "Double click", Toast.LENGTH_SHORT).show();
                    }
//                    @Override
//                    public void onClick(View v) {
//                        Toast.makeText(ListContactActivity.this, "Click on surface", Toast.LENGTH_SHORT).show();
//                    }
                });
                holder.swipeLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Toast.makeText(MainListActivity.this, "longClick on surface", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
                holder.star2_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainListActivity.this, "click the star", Toast.LENGTH_SHORT).show();
                    }
                });
                holder.trash2_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainListActivity.this, "Click the trash", Toast.LENGTH_SHORT).show();
                    }
                });
                holder.magnifier2_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainListActivity.this, "click the magnifier", Toast.LENGTH_SHORT).show();
                    }
                });
                holder.archire_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainListActivity.this, "click the archire", Toast.LENGTH_SHORT).show();
                    }
                });
                holder.delete_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainListActivity.this, "click the delele", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        };
        setListAdapter(cursorAdapter);
        getLoaderManager().initLoader(100, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(MainListActivity.this,ItemContract.CONTENT_URI,PROJECTION,SELECTION,null,ItemContract.DEFAULT_SORT);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }
}
