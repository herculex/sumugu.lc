package com.sumugu.liubo.lc;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sumugu.liubo.lc.contract.ItemContract;
import com.sumugu.liubo.lc.ui.MyListView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ItemLineFrameActivity extends Activity {

    final static String TAG = "lc_ItemLineFrame";
    private MyListView myListView;
    private MyCursorAdapter myCursorAdapter;
    private EditText mEditView;
    private MyLoaderCallback myCursorLoader;

    private long mUpdateItemId=0;
    private int mScrollDistance=0;
    private int mCurrentPosition=0;
    private int mCurrentPositionTop=0;

    private LinearLayout mContainerEditor;
    private TextView mTextReminder;
    private long mReminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_line_frame);

        mContainerEditor=(LinearLayout)findViewById(R.id.container_editor);
        mTextReminder=(TextView)findViewById(R.id.text_reminder);
        mTextReminder.setOnClickListener(new View.OnClickListener() {
            Calendar today = Calendar.getInstance();
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(ItemLineFrameActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        Calendar cal = Calendar.getInstance();
//                        cal.set(i,i1,i2);
                        cal.set(Calendar.YEAR,i);
                        cal.set(Calendar.MONTH,i1);
                        cal.set(Calendar.DAY_OF_MONTH,i2);

                        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
                        mTextReminder.setText(formater.format(cal.getTime()));
                        mReminder=cal.getTimeInMillis();
                    }
                }, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(today.DAY_OF_MONTH));

                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "移除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mTextReminder.setText("Add Reminder");
                        mReminder=0;
                    }
                });

                dialog.show();
            }
        });


        if(mCover==null) {
            mCover = (RelativeLayout) findViewById(R.id.layer_cover);
            mCover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (showEditView)
                        finishOff();
                }
            });
        }
        mEditView = (EditText) findViewById(R.id.edit_view);
        mEditView.setInputType(InputType.TYPE_NULL);

        //软键盘“完成” 隐藏软键盘，并打开遮罩
        //其实估计都不用写代码来隐藏软键盘，因为EditView，的Type为Text类型，“回车”变成了“完成”状态
        mEditView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.d(TAG, "softinput press what: " + String.valueOf(actionId));
                if (EditorInfo.IME_ACTION_DONE == actionId) {
                    Log.d(TAG, "you pressed the action done!");
                    finishOff();
                    return true;
                }

                return false;
            }
        });

        myListView = (MyListView) findViewById(R.id.list_view);
        myCursorAdapter = new MyCursorAdapter(this, null, 0);
        myListView.setAdapter(myCursorAdapter);

        myCursorLoader = new MyLoaderCallback(this, myCursorAdapter, 5);
        getLoaderManager().initLoader(5, null,myCursorLoader);

    }

    /**
     * Handle touch events to fade/move dragged items as they are swiped out
     * 用于Textview的onTouchListner
     */

    boolean mSwiping = false;
    boolean mItemPressed = false;
    int SWIPE_DURATION = 1000;
    int MOVE_DURATION = 2000;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        float mDownX;
        private int mSwipeSlop = -1;

        @Override
        public boolean onTouch(final View v, MotionEvent event) {
            if (mSwipeSlop < 0) {
                mSwipeSlop = ViewConfiguration.get(ItemLineFrameActivity.this).
                        getScaledTouchSlop();
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mItemPressed) {
                        // Multi-item swipes not handled
                        return false;
                    }
                    mItemPressed = true;
                    mDownX = event.getX();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    v.setAlpha(1);
                    v.setTranslationX(0);
                    mItemPressed = false;
                    break;
                case MotionEvent.ACTION_MOVE: {
                    float x = event.getX() + v.getTranslationX();
                    float deltaX = x - mDownX;
                    float deltaXAbs = Math.abs(deltaX);
                    if (!mSwiping) {
                        if (deltaXAbs > mSwipeSlop) {
                            mSwiping = true;
                            myListView.requestDisallowInterceptTouchEvent(true);
                        }
                    }
                    if (mSwiping) {
                        v.setTranslationX((x - mDownX));
//                        v.setAlpha(1 - deltaXAbs / v.getWidth());

                        //设置提醒背景色
                        if(deltaXAbs>v.getWidth()/4)
                        {
                            if(deltaX<0)
                                v.setBackgroundColor(Color.GREEN);
                            else
                                v.setBackgroundColor(Color.RED);
                        }
                        else
                        {
                            v.setBackgroundColor(Color.WHITE);  // TODO: 16/3/1   是要还原背景色，暂且白色
                        }

                        //设置固定距离
                        if(deltaXAbs>v.getWidth()/3)
                        {
                            if(deltaX<0) {
                                v.setTranslationX(-v.getWidth() / 3);
                            }
                            else {
                                v.setTranslationX(v.getWidth() / 3);
                            }
                        }
                    }
                }
                break;
                case MotionEvent.ACTION_UP: {
                    Log.d(TAG,"TextVIEW____ACTION__UP");
                    // User let go - figure out whether to animate the view out, or back into place
                    if (mSwiping) {
                        float x = event.getX() + v.getTranslationX();
                        float deltaX = x - mDownX;
                        float deltaXAbs = Math.abs(deltaX);
                        float fractionCovered;
                        float endX=0;
                        float endAlpha;
                        final boolean remove;
                        final int swipedResult;
                        if (deltaXAbs > v.getWidth() / 4) {
                            // Greater than a quarter of the width - animate it out
                            fractionCovered = deltaXAbs / v.getWidth();
                            endAlpha = 0;
                            if(deltaX>0) {
                                remove = true;
                                swipedResult=1;// is deleted.
                                endX = v.getWidth();
                            }
                            else
                            {
                                remove=false;
                                swipedResult=2;// is done.
                            }
                        } else {
                            // Not far enough - animate it back
                            fractionCovered = 1 - (deltaXAbs / v.getWidth());
                            endAlpha = 1;
                            remove = false;
                            swipedResult=0; //nothing happend.
                        }
                        // Animate position and alpha of swiped item
                        // NOTE: This is a simplified version of swipe behavior, for the
                        // purposes of this demo about animation. A real version should use
                        // velocity (via the VelocityTracker class) to send the item off or
                        // back at an appropriate speed.
                        long duration = (int) ((1 - fractionCovered) * SWIPE_DURATION);
                        myListView.setEnabled(false);
                        final View contaier=myListView.getChildAt(myListView.getPositionForView(v)).findViewById(R.id.container_del_done);

                        if (swipedResult>0) {
                            contaier.animate().alpha(0).setDuration(duration / 2);
                        }
                        v.animate().setDuration(duration).translationX(endX).
                                withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Restore animated values
                                        v.setBackgroundColor(Color.WHITE);  // TODO: 16/3/1   是要还原背景色，暂且白色
                                        v.setAlpha(1);
                                        v.setTranslationX(0);
                                        contaier.setAlpha(1);
                                        if (swipedResult==1) {
                                            animateRemoval(myListView, v);
                                        }
                                        else if(swipedResult==2)
                                        {
                                            updateToFinished(myListView,v);
                                        }
                                        else {
                                            mSwiping = false;
                                        }
                                        myListView.setEnabled(true);
                                    }
                                });
                    }
                    else
                    {
                        //no swiping ,but click
                        int currentPosition = myListView.getPositionForView(v);

                        mUpdateItemId = myListView.getAdapter().getItemId(currentPosition);
                        mEditView.setText(((TextView) v).getText());

                        myListView.animate().translationY(mContainerEditor.getHeight()).setDuration(250);  // TODO: 16/3/1 改成（编辑框在前，列表在后的方案）

                        //打开遮罩，进入编辑状态
                        showUp();
                        showEditView =true;

                    }
                }
                mItemPressed = false;
                break;
                default:
                    return false;
            }
            return true;
        }
    };
    private int updateToFinished(final ListView listView,final View viewToFinish)
    {
        int finishPosition= listView.getPositionForView(viewToFinish);
        long targetId = myCursorAdapter.getItemId(finishPosition);
        Uri uri = Uri.withAppendedPath(ItemContract.CONTENT_URI, String.valueOf(targetId));

        //获取查询结果各项值
        Cursor cursor = myCursorAdapter.getCursor();
        int finshed = cursor.getInt(cursor.getColumnIndex(ItemContract.Column.ITEM_IS_FINISHED));
        //
        ContentValues values = new ContentValues();
        values.put(ItemContract.Column.ITEM_IS_FINISHED, finshed==0?1:0);
        int count = getContentResolver().update(uri, values, null, null);

        return count;
    }

    HashMap<Long,Integer> mItemIdTopMap = new HashMap<Long,Integer>();
    private void animateRemoval(final ListView listview, View viewToRemove) {

        final int deletePosition = listview.getPositionForView(viewToRemove);
        int firstVisiblePosition = listview.getFirstVisiblePosition();
        for (int i = 0; i < listview.getChildCount(); ++i) {
            View child = listview.getChildAt(i);
            if (child.findViewById(R.id.text_content) != viewToRemove) {    //问题的根源在此，删除的view，不同与listview get出来的child－2016.2.19
                int position = firstVisiblePosition + i;
                long itemId = myCursorAdapter.getItemId(position);
                mItemIdTopMap.put(itemId, child.getTop());
            }
        }
//        mAdapter.remove(mAdapter.getItem(position)); //CursorAdapter是没有remove方法

        //只有删除数据，更新cursoradapter
        long targetId=myCursorAdapter.getItemId(deletePosition);
        Uri uri = Uri.withAppendedPath(ItemContract.CONTENT_URI, String.valueOf(targetId));
        int count = getContentResolver().delete(uri,null,null);

        Log.d(TAG,"delete ITEM!!!"+String.valueOf(count));

//      getLoaderManager().restartLoader(5,null,myCursorLoader);没用！？

        final ViewTreeObserver observer = listview.getViewTreeObserver();
        // TODO: 16/3/1 删除Item的之后的动画，超过一页的，从最低开始删除，APP会崩溃
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                observer.removeOnPreDrawListener(this);
                boolean firstAnimation = true;
                int firstVisiblePosition = listview.getFirstVisiblePosition();

                for (int i = 0; i < listview.getChildCount(); ++i) {
                    final View child = listview.getChildAt(i);
                    int position = firstVisiblePosition + i;
                    //
                    if(position>=deletePosition){   //问题的根源2在此，adapter的数据没有更新，所以要跳过被删除的位置－2016.2.19
//                        position=+1;//position not change!!!!
//                        position=position+1;
                        position++;
                    }
                    //

                    long itemId = myCursorAdapter.getItemId(position);
                    Integer startTop = mItemIdTopMap.get(itemId);
                    int top = child.getTop();
                    if (startTop != null) {
                        if (startTop != top) {
                            int delta = startTop - top;
                            child.setTranslationY(delta);
                            child.animate().setDuration(MOVE_DURATION).translationY(0);
                            Log.d(TAG,"LV__animatie____:"+String.valueOf(itemId));
                            if (firstAnimation) {
                                child.animate().withEndAction(new Runnable() {
                                    public void run() {
                                        mSwiping = false;
                                        listview.setEnabled(true);
                                    }
                                });
                                firstAnimation = false;
                            }
                        }
                    } else {
                        // Animate new views along with the others. The catch is that they did not
                        // exist in the start state, so we must calculate their starting position
                        // based on neighboring views.
                        int childHeight = child.getHeight() + listview.getDividerHeight();
                        startTop = top + (i > 0 ? childHeight : -childHeight);
                        int delta = startTop - top;
                        child.setTranslationY(delta);
                        child.animate().setDuration(MOVE_DURATION).translationY(0);
                        if (firstAnimation) {
                            child.animate().withEndAction(new Runnable() {
                                public void run() {
                                    mSwiping = false;
                                    listview.setEnabled(true);
                                }
                            });
                            firstAnimation = false;
                        }
                    }
                }
                mItemIdTopMap.clear();
                return true;
            }
        });
    }

    /*
    * 用本Activity的onTcouhEvent ，以控制各层级的位移
     */

    GestureDetector gestureDetector;
    boolean mListPressed = false;
    boolean mListSwiping = false;
    int mListSwipeSlop = -1;
    float mListDownY = 0;
    boolean showEditView = false;
    RelativeLayout mCover;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        if(null==gestureDetector)
//            gestureDetector = new GestureDetector(this,new MyFrameTouchGestureListner());
//
//        return gestureDetector.onTouchEvent(event);

        if(mCover.getVisibility()==View.VISIBLE)
            return super.onTouchEvent(event);

        if (mListSwipeSlop < 0) {
            mListSwipeSlop = ViewConfiguration.get(ItemLineFrameActivity.this).
                    getScaledTouchSlop();
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mListPressed) {
                    // Multi-item swipes not handled
                    return false;
                }
                mListPressed = true;
                mListDownY = event.getY();
                Log.d(TAG, "AT___DOWN.");
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.d(TAG, "AT___CANCEL.");
                mListPressed = false;
                mListSwiping = false;
                myListView.setTranslationY(0);
                break;
            case MotionEvent.ACTION_MOVE: {
                if (mListDownY == 0)
                    mListDownY = event.getY();

                Log.d(TAG, String.valueOf(mListDownY) + ":AT___MOVE");
                float y = event.getY();
                float deltaY = y - mListDownY;
                float deltaYAbs = Math.abs(deltaY);
                if (!mListSwiping) {
                    if (deltaYAbs > mListSwipeSlop) {
                        mListSwiping = true;
                    }
                }
                if (mListSwiping) {

                    if (showEditView)
                        myListView.setTranslationY(y - mListDownY + mContainerEditor.getHeight());
                    else
                        myListView.setTranslationY(y - mListDownY);
//                    myListView.setAlpha(1 - deltaYAbs / myListView.getWidth());

                    if (myListView.getTranslationY() <= 0) {
                        myListView.setTranslationY(0);
                        myListView.requestAllowSuperTouch(true);
                        Log.d(TAG,"AT___LV__TOP!");
                    }
                }
                Log.d(TAG, "donwY=" + String.valueOf(mListDownY) + ";y=" + String.valueOf(y) + ";DeltaY=" + String.valueOf(deltaY));
            }
            break;
            case MotionEvent.ACTION_UP: {
                Log.d(TAG, "AT___UP.");
                // User let go - figure out whether to animate the view out, or back into place
                if (mListSwiping) {
                    float y = event.getY() + myListView.getTranslationY();
                    float deltaY = y - mListDownY;
                    float deltaYAbs = Math.abs(deltaY);
                    float fractionCovered;
                    float endY;
                    float endAlpha;
                    if (deltaY > 0 && deltaYAbs > mContainerEditor.getHeight()) {
                        fractionCovered = (deltaYAbs / mContainerEditor.getHeight()) > 1 ? 1 : 0;
                        endY = mContainerEditor.getHeight();
//                        endAlpha = 0;
                        showEditView = true;

                    } else {
                        // Not far enough - animate it back
                        fractionCovered = 1 - (deltaYAbs / mContainerEditor.getHeight());
                        endY = 0;
//                        endAlpha = 1;
                        showEditView = false;

                    }
                    long duration = (int) ((1 - fractionCovered) * SWIPE_DURATION);

                    myListView.animate().setDuration(duration).translationY(endY)
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    if (showEditView) {
                                        //
                                        showUp();
                                        //
                                    } else {
                                        mListSwiping = false;
                                    }
                                }
                            });
                }
            }
            mListPressed = false;
            mListDownY = 0;
            break;
            default:
                return false;
        }
        return true;
    }


    //打开遮罩，并进入编辑状态
    private void showUp()
    {
        mCover.setVisibility(View.VISIBLE);
        mCover.setTranslationY(mContainerEditor.getHeight());

        mEditView.requestFocus();
        mEditView.setInputType(InputType.TYPE_CLASS_TEXT);
        mEditView.setSelection(mEditView.getText().length());

        //打开软键盘
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.showSoftInput(mEditView, InputMethodManager.SHOW_IMPLICIT);
    }

    //完成后关闭遮罩
    private void finishOff()
    {
        //隐藏软键盘
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(mEditView.getWindowToken(), 0);

        mEditView.setInputType(InputType.TYPE_NULL);

        final String content = mEditView.getText().toString();
        if(TextUtils.isEmpty(content))
        {
            mContainerEditor.animate().setDuration(500).translationX(-mEditView.getWidth()).withEndAction(new Runnable() {
                @Override
                public void run() { // TODO: 16/3/1 统一编辑框消失动画时间
                    mCover.animate().translationY(0).setDuration(500).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            mCover.setVisibility(View.GONE);
                        }
                    });
                    myListView.animate().translationY(0).setDuration(500).withEndAction(new Runnable() {
                        @Override
                        public void run() {// TODO: 16/3/1 统一列表动画时间
                            mContainerEditor.setTranslationX(0);
                            myListView.requestFocus();
                        }
                    });
                }
            });

            if(mUpdateItemId!=0) {
                getContentResolver().delete(Uri.withAppendedPath(ItemContract.CONTENT_URI, String.valueOf(mUpdateItemId)), null, null);
                mUpdateItemId=0;
                Log.d(TAG, "Editor EMPTY! Delete it!!");
            }
        }
        else {
            mContainerEditor.animate().setDuration(500).alpha(0);// TODO: 16/3/1 编辑框消失动画时间
            mCover.animate().translationY(0).setDuration(500).withEndAction(new Runnable() {
                @Override
                public void run() {
                    mCover.setVisibility(View.GONE);
                }
            });
            myListView.animate().translationY(0).alpha(1).setDuration(500).withEndAction(new Runnable() {
                @Override
                public void run() { // TODO: 16/3/1 统一列表位移复位的动画时间
                    mContainerEditor.setAlpha(1);
                    mEditView.setText("");
                    if (mUpdateItemId == 0) {
                        postNewContent(content);  //处理一些保存数据的操作
                    }
                    else {
                        updateContent(content);
                    }
                    myListView.requestFocus();
                }
            });
        }

        showEditView =false;
    }

    private int updateContent(String content)
    {
        Uri uri = ItemContract.CONTENT_URI;
        String where = ItemContract.Column.ITEM_ID + "=?";
        String[] params = new String[] {String.valueOf(mUpdateItemId)};

        //step 1，查处闹钟并取消掉
        //cancelAlarmClock();

        //step 2，更新记录的闹钟和标记
        ContentValues values = new ContentValues();
        values.put(ItemContract.Column.ITEM_CONTENT,content);

        int count = getContentResolver().update(uri,values,where,params);   //方法1
//        int count = getContentResolver().update(Uri.withAppendedPath(ItemContract.CONTENT_URI,String.valueOf(mItemId)),values,null,null);//方法2

        mUpdateItemId=0;    //恢复默认

        return count;
    }
    private boolean postNewContent(String content)
    {
        ContentValues values = new ContentValues();

        values.put(ItemContract.Column.ITEM_TITLE,"");
        values.put(ItemContract.Column.ITEM_CONTENT,content);
        values.put(ItemContract.Column.ITEM_CREATED_AT,new Date().getTime());
        values.put(ItemContract.Column.ITEM_IS_FINISHED,0);
        values.put(ItemContract.Column.ITEM_HAS_CLOCK,0);
        values.put(ItemContract.Column.ITEM_ALARM_CLOCK,0);

        values.put(ItemContract.Column.ITEM_LIST_ID,-1);

        //用Provider插入新数据，而非用DbHelper。在Fragment里，要用Provider，需获取的当前的Activity。
        Uri uri = getContentResolver().insert(ItemContract.CONTENT_URI, values);
        if(uri!=null){
            Log.d(TAG, String.format("%s:%s", values.getAsString(ItemContract.Column.ITEM_TITLE), values.getAsString(ItemContract.Column.ITEM_CONTENT)));
            return true;
        }
        else
        {
            return false;
        }
        //
    }


    public class MyFrameTouchGestureListner extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if(distanceY<0)
                return false;

            myListView.setTranslationY(distanceY);
            return true;
        }
    }

    public class MyCursorAdapter extends CursorAdapter {

        public MyCursorAdapter(Context context, Cursor cursor, int flag) {
            super(context, cursor, flag);
        }

        public MyCursorAdapter(Context context, Cursor cursor, boolean autoRequst) {
            super(context, cursor, autoRequst);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            //找到布局文件，保存控件到Holder
            MyViewHolder holder = new MyViewHolder();
            View view = getLayoutInflater().inflate(R.layout.item_frame, null);

            holder.textView = (TextView) view.findViewById(R.id.text_content);

//            holder.textView.setOnTouchListener(mTouchListener);
//            holder.textView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.d(TAG,"ppppppppppp---------");
//                }
//            });

            view.setTag(holder);
            //返回view，会自动传给bindView方法。
            return view;

        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            //接收view的holder里的控件对象，然后赋值
            MyViewHolder holder = (MyViewHolder) view.getTag();

            String id = cursor.getString(cursor.getColumnIndex(ItemContract.Column.ITEM_ID));
            String content = cursor.getString(cursor.getColumnIndex(ItemContract.Column.ITEM_CONTENT));
            int finish=cursor.getInt(cursor.getColumnIndex(ItemContract.Column.ITEM_IS_FINISHED));

            holder.textView.setText(content);
            if(finish==1)
                holder.textView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);   //删除线
            else
                holder.textView.getPaint().setFlags(0);

            holder.textView.getPaint().setAntiAlias(true);  //抗锯齿

            //完成赋值
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            if (view != convertView) {
                // Add touch listener to every new view to track swipe motion
                view.findViewById(R.id.text_content).setOnTouchListener(mTouchListener);
            }
            return view;
        }

        class MyViewHolder {
            TextView textView;
            TextView deleteView;
            TextView doneView;
        }
    }

    public class MyLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {
        final static String TAG = "lc_myLoaderCallback";

        CursorAdapter mCursorAdapter;
        int mLoaderId;
        Context mContext;

        public MyLoaderCallback(Context context, CursorAdapter adapter, int id) {
            mContext = context;
            mCursorAdapter = adapter;
            mLoaderId = id;
            Log.d(TAG, "init..");
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (id != mLoaderId)
                return null;

            String where = ItemContract.Column.ITEM_IS_FINISHED + "=0";
            return new CursorLoader(mContext, ItemContract.CONTENT_URI, null, null, null, ItemContract.DEFAULT_SORT);
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
