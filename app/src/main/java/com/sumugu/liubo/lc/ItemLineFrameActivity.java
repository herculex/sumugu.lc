package com.sumugu.liubo.lc;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sumugu.liubo.lc.contract.ItemContract;
import com.sumugu.liubo.lc.ui.MyListView;

public class ItemLineFrameActivity extends Activity {

    final static String TAG = "lc_ItemLineFrame";
    private MyListView myListView;
    private MyCursorAdapter myCursorAdapter;
    private EditText mEditNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_line_frame);

        mEditNew = (EditText) findViewById(R.id.edit_new);
        mEditNew.setInputType(InputType.TYPE_NULL);

        myListView = (MyListView) findViewById(R.id.list_view);
        myCursorAdapter = new MyCursorAdapter(this, null, 0);
        myListView.setAdapter(myCursorAdapter);

        getLoaderManager().initLoader(5, null, new MyLoaderCallback(this, myCursorAdapter, 5));

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "item position=" + String.valueOf(position) + ",id=" + String.valueOf(id));
            }
        });
    }

    /**
     * Handle touch events to fade/move dragged items as they are swiped out
     * 用于Textview的onTouchListner
     */

    boolean mSwiping = false;
    boolean mItemPressed = false;
    int SWIPE_DURATION = 250;
    int MOVE_DURATION = 150;
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
                        v.setAlpha(1 - deltaXAbs / v.getWidth());
                    }
                }
                break;
                case MotionEvent.ACTION_UP: {
                    // User let go - figure out whether to animate the view out, or back into place
                    if (mSwiping) {
                        float x = event.getX() + v.getTranslationX();
                        float deltaX = x - mDownX;
                        float deltaXAbs = Math.abs(deltaX);
                        float fractionCovered;
                        float endX;
                        float endAlpha;
                        final boolean remove;
                        if (deltaXAbs > v.getWidth() / 4) {
                            // Greater than a quarter of the width - animate it out
                            fractionCovered = deltaXAbs / v.getWidth();
                            endX = deltaX < 0 ? -v.getWidth() : v.getWidth();
                            endAlpha = 0;
                            remove = true;
                        } else {
                            // Not far enough - animate it back
                            fractionCovered = 1 - (deltaXAbs / v.getWidth());
                            endX = 0;
                            endAlpha = 1;
                            remove = false;
                        }
                        // Animate position and alpha of swiped item
                        // NOTE: This is a simplified version of swipe behavior, for the
                        // purposes of this demo about animation. A real version should use
                        // velocity (via the VelocityTracker class) to send the item off or
                        // back at an appropriate speed.
                        long duration = (int) ((1 - fractionCovered) * SWIPE_DURATION);
                        myListView.setEnabled(false);
                        v.animate().setDuration(duration).
                                alpha(endAlpha).translationX(endX).
                                withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Restore animated values
                                        v.setAlpha(1);
                                        v.setTranslationX(0);
                                        if (remove) {
//                                            animateRemoval(myListView, v);
                                        } else {
                                            mSwiping = false;
                                            myListView.setEnabled(true);
                                        }
                                    }
                                });
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

    /*
    * 用本Activity的onTcouhEvent ，以控制各层级的位移
     */

    GestureDetector gestureDetector;
    boolean mListPressed = false;
    boolean mListSwiping = false;
    int mListSwipeSlop = -1;
    float mListDownY = 0;
    boolean showEditNew = false;
    RelativeLayout mCover;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        if(null==gestureDetector)
//            gestureDetector = new GestureDetector(this,new MyFrameTouchGestureListner());
//
//        return gestureDetector.onTouchEvent(event);

        if(mCover==null)
            mCover=(RelativeLayout)findViewById(R.id.layer_cover);
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

                    if (showEditNew)
                        myListView.setTranslationY(y - mListDownY + mEditNew.getHeight());
                    else
                        myListView.setTranslationY(y - mListDownY);
//                    myListView.setAlpha(1 - deltaYAbs / myListView.getWidth());

                    if (myListView.getTranslationY() <= 0) {
                        myListView.setTranslationY(0);
                        myListView.requestAllowSuperTouch(true);
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
                    if (deltaY > 0 && deltaYAbs > mEditNew.getHeight()) {
                        fractionCovered = (deltaYAbs / mEditNew.getHeight()) > 1 ? 1 : 0;
                        endY = mEditNew.getHeight();
//                        endAlpha = 0;
                        showEditNew = true;

                    } else {
                        // Not far enough - animate it back
                        fractionCovered = 1 - (deltaYAbs / mEditNew.getHeight());
                        endY = 0;
//                        endAlpha = 1;
                        showEditNew = false;

                    }
                    long duration = (int) ((1 - fractionCovered) * SWIPE_DURATION);

                    myListView.animate().setDuration(duration).translationY(endY)
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    if (showEditNew) {
                                        //
                                        showup(null);
                                        //
                                    } else {
                                        mListSwiping = false;
                                    }
                                }
                            });

                    if(showEditNew)
                    {
                        mEditNew.requestFocus();
                        mEditNew.setInputType(InputType.TYPE_CLASS_TEXT);
                        //打开softinput
                        mEditNew.setSelection(mEditNew.getText().length());
                        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.showSoftInput(mEditNew, InputMethodManager.SHOW_IMPLICIT);
                    }

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

    public void showup(View view)
    {
        mCover.setVisibility(View.VISIBLE);
        mCover.setTranslationY(myListView.getTranslationY());
        myListView.setAlpha(0.5f);
    }
    public void showoff(View view) {

        mEditNew.animate().setDuration(1000).translationX(-mEditNew.getWidth()).withEndAction(new Runnable() {
            @Override
            public void run() {
                mCover.setVisibility(View.GONE);
                myListView.animate().translationY(0).setDuration(1000).alpha(1).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        mEditNew.setTranslationX(0);
                    }
                });
            }
        });

        showEditNew=false;

        myListView.requestFocus();
        //隐藏软键盘
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(mEditNew.getWindowToken(), 0);
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
            holder.editText = (EditText) view.findViewById(R.id.edit_content);

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

            holder.textView.setText(id+":"+content );
            holder.editText.setText(content);

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
            EditText editText;
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
