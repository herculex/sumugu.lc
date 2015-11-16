package com.sumugu.liubo.lc;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.sumugu.liubo.lc.contract.ItemContract;

import java.util.Date;

public class MainListActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor>,View.OnClickListener{

    static final String TAG = MainListActivity.class.getSimpleName();

    static final String[]  PROJECTION = new String[] {ItemContract.Column.ITEM_ID,ItemContract.Column.ITEM_CONTENT,ItemContract.Column.ITEM_IS_FINISHED};
    String SELECTION = "";
    String[] PARAMS;
    String searchingText ="";

    SearchingFilter searchingfilter=SearchingFilter.Undo;

    CursorAdapter cursorAdapter;
    SwipeLayout mainList;

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.mainlist_control:
                startActivity(new Intent(MainListActivity.this,ItemActivity.class));
                return;
            default:
                return;
        }
    }
    enum SearchingFilter{
        All,
        Undo,
        Expired,
        Done
    }

    class ViewHolder{
        TextView display_text;
        TextView archire_text;
        TextView delete_text;
        View starboot;
        View bottom_wrapper;
        View bottom_wrapper_2;
        SwipeLayout swipeLayout;
        TextView timercancel;
        TextView timer1;
        TextView timer2;
        TextView timer3;
        TextView itemId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);

        findViewById(R.id.mainlist_control).setOnClickListener(this);

        mainList = (SwipeLayout)findViewById(R.id.main_list);
        mainList.setShowMode(SwipeLayout.ShowMode.PullOut);

        View searching = findViewById(R.id.layer_searching);
        View filter = findViewById(R.id.layer_filter);
        View setting = findViewById(R.id.layer_setting);

        mainList.addDrag(SwipeLayout.DragEdge.Right, filter);
        mainList.addDrag(SwipeLayout.DragEdge.Left,setting);
/*        mainList.addDrag(SwipeLayout.DragEdge.Top, searching);


        EditText searchingText = (EditText)findViewById(R.id.searching_text);
        searchingText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainList.open(true,true, SwipeLayout.DragEdge.Top);
                Toast.makeText(MainListActivity.this, "click the searching texting", Toast.LENGTH_SHORT).show();
            }
        });*/

        TextView filterUndo = (TextView)findViewById(R.id.filter_undo);
        TextView filterDone = (TextView)findViewById(R.id.filter_done);
        TextView filterExpired = (TextView)findViewById(R.id.filter_expired);
        TextView filterAll = (TextView)findViewById(R.id.filter_all);

        filterUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView)findViewById(R.id.title_list)).setText("未完成的");
                restartLoader(SearchingFilter.Undo);
            }
        });

        filterDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView)findViewById(R.id.title_list)).setText("已完成");
                restartLoader(SearchingFilter.Done);
            }
        });
        filterExpired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView)findViewById(R.id.title_list)).setText("过期的");
                restartLoader(SearchingFilter.Expired);
            }
        });
        filterAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView)findViewById(R.id.title_list)).setText("所有");
                restartLoader(SearchingFilter.All);
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
                holder.bottom_wrapper = view.findViewById(R.id.bottom_wrapper);
                holder.swipeLayout = (SwipeLayout)view.findViewById(R.id.swipe_sample1);

                holder.itemId = (TextView)view.findViewById(R.id.displayItemId);

                view.setTag(holder);
                return view;
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                ViewHolder holder=(ViewHolder)view.getTag();

                int isfinished = cursor.getInt(cursor.getColumnIndex(ItemContract.Column.ITEM_IS_FINISHED));
                String name = cursor.getString(cursor.getColumnIndex(ItemContract.Column.ITEM_CONTENT));
                holder.display_text.setText((isfinished == 1 ? "[已完成] " : "") + name);
                final String itemid = cursor.getString(cursor.getColumnIndex(ItemContract.Column.ITEM_ID));
                holder.itemId.setText(itemid);

                holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
                holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, holder.bottom_wrapper);
                holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, null);

                holder.swipeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainListActivity.this, ItemDetailActivity.class);
                        intent.putExtra(ItemContract.Column.ITEM_ID, itemid);
                        startActivity(intent);
//                        Toast.makeText(MainListActivity.this, itemid, Toast.LENGTH_SHORT).show();
                    }
                });

                holder.archire_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = ItemContract.CONTENT_URI;
                        String where = ItemContract.Column.ITEM_ID + "=?";
                        String[] params = new String[] {itemid};

                        //step 1，查处闹钟并取消掉
//                        cancelAlarmClock();   //// TODO: 15/11/13

                        //step 2，更新记录的闹钟和标记
                        ContentValues values = new ContentValues();
                        values.put(ItemContract.Column.ITEM_IS_FINISHED, 1);
                        values.put(ItemContract.Column.ITEM_HAS_CLOCK,0);
                        values.put(ItemContract.Column.ITEM_ALARM_CLOCK,0);

                        int count = getContentResolver().update(uri,values,where,params);

                        if(count>0)
                        {
                            Snackbar.make(v, "好样的，继续加油！", Snackbar.LENGTH_LONG).setAction("晒吗？", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(MainListActivity.this, "想的美！", Toast.LENGTH_SHORT).show();
                                }
                            }).show();

                            Log.d(TAG, "sumugu:finish item" + itemid);
                        }
                    }
                });
                holder.delete_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                      //step 1，查处闹钟并取消
//                        cancelAlarmClock();   //// TODO: 15/11/13

                        //创建访问内容提供器的URI
                        Uri uri = ItemContract.CONTENT_URI;
                        String where = ItemContract.Column.ITEM_ID + "=?";
                        String[] params = new String[] {itemid};

                        //step 2，删除指定ID的纪录
                        int count = getContentResolver().delete(uri,where,params);

                        //删除成功提示
                        if(count>0)
                        {
                            Snackbar.make(v, "删掉了，想恢复吗？", Snackbar.LENGTH_LONG).setAction("后悔？", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(MainListActivity.this, "然并卵！", Toast.LENGTH_SHORT).show();
                                }
                            }).show();

                            Log.d(TAG, "sumugu:delete item:" + itemid);
                        }
                    }
                });
            }
        };
        setListAdapter(cursorAdapter);
        getLoaderManager().initLoader(100, null, this);

        //SearchView
        ((SearchView)findViewById(R.id.search_view)).setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                MainListActivity.this.searchingText =newText;
                restartLoader(searchingfilter);
                return true;
            }
        });
    }

    private void restartLoader(SearchingFilter filter)
    {
        searchingfilter=filter;

        if(null!=cursorAdapter)
            getLoaderManager().restartLoader(100,null,this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (searchingfilter)
        {
            case Done:
                if(searchingText.isEmpty()) {
                    SELECTION = ItemContract.Column.ITEM_IS_FINISHED + "=?";
                    PARAMS = new String[]{"1"};
                }
                else
                {
                    SELECTION = ItemContract.Column.ITEM_CONTENT+" like ? and " + ItemContract.Column.ITEM_IS_FINISHED + "=?";
                    PARAMS = new String[]{'%'+ searchingText +'%',"1"};
                }
                break;
            case All:
                if(searchingText.isEmpty()) {
                    SELECTION = "";
                    PARAMS = new String[]{};
                }
                else{
                    SELECTION = ItemContract.Column.ITEM_CONTENT+" like ? ";
                    PARAMS = new String[] {'%'+ searchingText +'%'};
                }
                break;
            case Undo:
                if(searchingText.isEmpty()) {
                    SELECTION = ItemContract.Column.ITEM_IS_FINISHED + "=?";
                    PARAMS = new String[]{"0"};
                }
                else
                {
                    SELECTION = ItemContract.Column.ITEM_CONTENT+" like ? and " + ItemContract.Column.ITEM_IS_FINISHED + "=?";
                    PARAMS = new String[]{'%'+ searchingText +'%',"0"};
                }
                break;
            case Expired:
                if(searchingText.isEmpty()) {
                    SELECTION = ItemContract.Column.ITEM_IS_FINISHED + "=? and " + ItemContract.Column.ITEM_HAS_CLOCK + "=? and " + ItemContract.Column.ITEM_ALARM_CLOCK + "<?";
                    PARAMS = new String[]{"0", "1", String.valueOf(new Date().getTime())};
                }else{
                    SELECTION = ItemContract.Column.ITEM_CONTENT+" like ? and " +ItemContract.Column.ITEM_IS_FINISHED + "=? and " + ItemContract.Column.ITEM_HAS_CLOCK + "=? and " + ItemContract.Column.ITEM_ALARM_CLOCK + "<?";
                    PARAMS = new String[]{'%'+ searchingText +'%',"0", "1", String.valueOf(new Date().getTime())};
                }
                break;
            default:
                break;
        }
        return new CursorLoader(MainListActivity.this,ItemContract.CONTENT_URI,PROJECTION,SELECTION,PARAMS,ItemContract.DEFAULT_SORT);
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
