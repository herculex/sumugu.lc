package com.sumugu.liubo.lc.xdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sumugu.liubo.lc.R;
import com.sumugu.liubo.lc.utils.DisplayUtil;

import java.util.Date;

/**
 * Created by liubo on 16/6/27.
 */
public class MyCustomItem extends FrameLayout {

    private String mDiplayText;

    public interface OnEditingListener {
        void begin(int index);

        void finish(int index);
    }

    public interface OnFinishListener {
        void end(int index);
    }

    public interface OnDeleteListner {
        void end(int index);
    }

    public interface OnPreparingListener {

        void end(int index);
    }

    public void setText(String text) {
        mDiplayText = text;
        tvDisplay.setText(mDiplayText);
        tvDisplay.setVisibility(VISIBLE);
        rlActionPanel.setVisibility(VISIBLE);
        editText.setVisibility(GONE);

        setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

    }

    public void edit() {
        if (onEditingListener != null)
            onEditingListener.begin(getItemIndex());

        //
        tvDisplay.setVisibility(GONE);
        rlActionPanel.setVisibility(GONE);
        //
        editText.setVisibility(VISIBLE);
        editText.requestFocus();
        //call softinput // TODO: 16/7/21
        setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        //
        if (onEditingListener != null)
            onEditingListener.finish(getItemIndex());
    }

    public void moveOut() {
        //
        //
        if (onDeleteListner != null)
            onDeleteListner.end(getItemIndex());
    }

    protected int getItemIndex() {
        return -9;
    }

    public final class PreparingType {
        public final static int DEFAULT = 1;//default
        public final static int Trapezium = -1;   //
        public final static int POLYGON = 2; //polygon
    }

    public final class StateType {

        public final static int PREPARING = 1;
        public final static int DISPLAY_TEXT = 3;
        public final static int EDITING_TEXT = 4;
        public final static int MOVING_OUT = 5;

    }

    private OnEditingListener onEditingListener;
    private OnFinishListener onFinishListener;
    private OnDeleteListner onDeleteListner;
    private OnPreparingListener onPreparingListener;

    private int mPreparingType = PreparingType.DEFAULT;
    private int mStateType;

    private final String TAG = MyCustomItem.class.getSimpleName();
    private Context mContext;
    private TextView tvDelete;
    private TextView tvFinish;
    private TextView tvDisplay;
    private EditText editText;
    private RelativeLayout rlActionPanel;

    public void setOnEditingListener(OnEditingListener listener) {
        onEditingListener = listener;
    }

    public void setOnFinishListener(OnFinishListener listener) {
        onFinishListener = listener;
    }

    public void setOnDeleteListner(OnDeleteListner listener) {
        onDeleteListner = listener;
    }

    public void setOnPreparingListener(OnPreparingListener listener) {
        onPreparingListener = listener;
    }

    public MyCustomItem(Context context) {
        super(context);
        initView(context);
    }

    public MyCustomItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MyCustomItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void setPreparingType(int type) {
        mPreparingType = type;
    }

    private String mPreparingTextBegin = "", mPreparingTextEnd = "";

    public void setPreparingText(String begin, String end) {
        mPreparingTextEnd = end;
        mPreparingTextBegin = begin;
    }

    protected int getStateType() {
        return mStateType;
    }

    protected int getPreparingType() {
        return mPreparingType;
    }

    private void initView(Context context) {
        setWillNotDraw(false);

        //inflate the view
        View view = LayoutInflater.from(context).inflate(R.layout.custom_item, this, true);

        //find them out
        tvDelete = (TextView) view.findViewById(R.id.ci_text_delete);
        tvDisplay = (TextView) view.findViewById(R.id.ci_text_dispaly);
        tvFinish = (TextView) view.findViewById(R.id.ci_text_finish);
        editText = (EditText) view.findViewById(R.id.ci_edit_content);
        rlActionPanel = (RelativeLayout) view.findViewById(R.id.ci_action_panel);

        //

        tvDisplay.setOnTouchListener(new DisplayTouchListener());

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "onMeasure,width=" + MeasureSpec.getSize(widthMeasureSpec) + ",height=" + MeasureSpec.getSize(heightMeasureSpec));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d(TAG, "onSizeChanged,w=" + w + ",h=" + h + ",oldw=" + oldw + ",oldh=" + oldh);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    boolean isPreparingListenerEndCalled = false;

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "mci start onDraw");

        if (tvDisplay.getVisibility() == VISIBLE) {
            super.onDraw(canvas);
            return;
        }
        if (editText.getVisibility() == VISIBLE) {
            super.onDraw(canvas);
            return;
        }

        int defaultHeight = DisplayUtil.dip2px(getContext(), 45f);
        int defaultWidth = getMeasuredWidth();

        Bitmap bitmap = Bitmap.createBitmap(defaultWidth, defaultHeight, Bitmap.Config.ARGB_8888);
        int h = getMeasuredHeight();

        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(60f);
        textPaint.setColor(Color.WHITE);

        Canvas mcanvas = new Canvas(bitmap);


        if (h < defaultHeight) {
            isPreparingListenerEndCalled = false;
            if (!mPreparingTextBegin.isEmpty()) {
                mcanvas.drawColor(Color.GRAY);
//                    mcanvas.drawText("This.is.Trapezium.", 0, 100, textPaint);
                mcanvas.drawText(mPreparingTextBegin, 0, 100, textPaint);
            }
            drawTrapezium(canvas, bitmap, h, defaultHeight, defaultWidth);
        } else {

            if (!mPreparingTextEnd.isEmpty()) {
                mcanvas.drawColor(Color.LTGRAY);
//                    mcanvas.drawText("Release.into.editing.mode", 0, 100, textPaint);
                mcanvas.drawText(mPreparingTextEnd, 0, 100, textPaint);
            }
            canvas.drawBitmap(bitmap, 0, 0, null);

            if (!isPreparingListenerEndCalled && onPreparingListener != null) {
                onPreparingListener.end(0);
                isPreparingListenerEndCalled = true;
            }
        }


//        canvas.save();
//        canvas.restore();
//        super.onDraw(canvas);


    }


    private void drawTrapezium(Canvas canvas, Bitmap bitmap, int h, int defaultHeight, int defaultWidth) {


        float deltaX = 0;

        if (h < defaultHeight) {

            deltaX = (float) Math.sqrt(Math.pow(defaultHeight, 2) - Math.pow(h, 2));

            float[] src = new float[]{
                    0, 0,           //左上(x,y)
                    defaultWidth, 0,//右上
                    defaultWidth, defaultHeight,//左下
                    0, defaultHeight            //右下
            };
            float[] dst = new float[]{
                    deltaX, 0,                  //左上(x,y)
                    defaultWidth - deltaX, 0,   //右上
                    defaultWidth, getMeasuredHeight(),  //左下
                    0, getMeasuredHeight()              //右下
            };
            Matrix matrix = new Matrix();
            matrix.setPolyToPoly(src, 0, dst, 0, src.length >> 1);

            canvas.drawBitmap(bitmap, matrix, null);
//        canvas.save();
//        canvas.restore();
        }
    }

    private void drawPolygon(Canvas canvas, Bitmap bitmap, int h, int defaultHeight, int defaultWidth) {


        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(60f);
        textPaint.setColor(Color.WHITE);

        Canvas mcanvas = new Canvas(bitmap);
        mcanvas.drawColor(Color.GRAY);
        mcanvas.drawText("This.is.Polygon.Let's.Roll.It.", 0, defaultHeight / 2 + 20, textPaint);

        //取上半部分
        Bitmap bitmapUp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight() / 2);
//        Canvas canvasUp = new Canvas(bitmapUp);
//        canvasUp.drawColor(Color.LTGRAY);

        //取下半部分
        Bitmap bitmapDown = Bitmap.createBitmap(bitmap, 0, defaultHeight / 2, bitmap.getWidth(), bitmap.getHeight() / 2);
//        Canvas canvasDown = new Canvas(bitmapDown);
//        canvasDown.drawColor(Color.LTGRAY);

        float deltaX = 0;

        if (h < defaultHeight) {

            deltaX = (float) Math.sqrt(Math.pow(defaultHeight, 2) - Math.pow(h, 2));

            //下半部分的矩阵
            float[] src = new float[]{
                    0, 0,           //左上(x,y)
                    defaultWidth, 0,//右上
                    defaultWidth, defaultHeight / 2,//左下
                    0, defaultHeight / 2            //右下
            };
            float[] dst = new float[]{
                    deltaX, 0,                  //左上(x,y)
                    defaultWidth - deltaX, 0,   //右上
                    defaultWidth, getMeasuredHeight() / 2,  //左下
                    0, getMeasuredHeight() / 2              //右下
            };
            Matrix matrix = new Matrix();
            matrix.setPolyToPoly(src, 0, dst, 0, src.length >> 1);
            bitmapDown = Bitmap.createBitmap(bitmapDown, 0, 0, bitmapDown.getWidth(), bitmapDown.getHeight(), matrix, false);
            //

            //上半部分的矩阵
            float[] upsrc = new float[]{
                    0, 0,           //左上(x,y)
                    defaultWidth, 0,//右上
                    defaultWidth, defaultHeight / 2,//左下
                    0, defaultHeight / 2            //右下
            };
            float[] updst = new float[]{
                    0, 0,                  //左上(x,y)
                    defaultWidth, 0,   //右上
                    defaultWidth - deltaX, getMeasuredHeight() / 2,  //左下
                    deltaX, getMeasuredHeight() / 2              //右下
            };
            Matrix upMatrix = new Matrix();
            upMatrix.setPolyToPoly(upsrc, 0, updst, 0, upsrc.length >> 1);
            bitmapUp = Bitmap.createBitmap(bitmapUp, 0, 0, bitmapUp.getWidth(), bitmapUp.getHeight(), upMatrix, false);


            canvas.drawBitmap(bitmapUp, 0, 0, null);
            canvas.drawBitmap(bitmapDown, 0, h / 2, null);
//        canvas.save();
//        canvas.restore();
        }
    }

    private class DisplayTouchListener implements OnTouchListener {

        float slop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        float downX = 0;
        boolean swiping = false;
        boolean pressed = false;
        int maxOffsetX = DisplayUtil.dip2px(getContext(), 60f);
        long timeDist = 0;

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            Log.d(TAG, slop + ",display:" + view);
            switch (motionEvent.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    Log.d(TAG, "display DONW");
                    if (pressed)
                        return false;
                    downX = motionEvent.getX();
                    pressed = true;
                    if (timeDist == 0)
                        timeDist = new Date().getTime();
                    return true;

                case MotionEvent.ACTION_MOVE:
                    Log.d(TAG, "display MOVE");
                    float x = motionEvent.getX() + view.getTranslationX();
                    float offsetX = x - downX;
                    float absOffsetX = Math.abs(offsetX);
                    if (!swiping) {
                        if (absOffsetX > slop) {
                            swiping = true;
                        }
                    }
                    if (swiping) {
                        view.setTranslationX(offsetX);
                        if (absOffsetX >= maxOffsetX) {
                            //start to translate the ci_action_panel
                            if (offsetX > 0) {
                                //swiping to right
                                rlActionPanel.setTranslationX(offsetX - maxOffsetX);
                            } else {
                                //swiping to left
                                rlActionPanel.setTranslationX(offsetX + maxOffsetX);
                            }
                        } else {
                            rlActionPanel.setTranslationX(0);
                        }
                        return true;
                    } else {
                        return false;
                    }
                case MotionEvent.ACTION_CANCEL:
                    Log.d(TAG, "display CANCEL");
                    pressed = false;
                    swiping = false;

                    view.setTranslationX(0);
                    return true;
                case MotionEvent.ACTION_UP:
                    Log.d(TAG, "display UP");
                    if (swiping) {
                        float absViewX = Math.abs(view.getTranslationX());
                        float viewX = view.getTranslationX();

                        if (absViewX >= maxOffsetX) {
                            //do finish or delete
                            if (viewX > 0) {
                                //do finish
                                rlActionPanel.animate().translationX(0).setDuration(500);
                                view.animate().translationX(0).setDuration(500)
                                        .withEndAction(new Runnable() {
                                            @Override
                                            public void run() {
                                                onFinishListener.end(getItemIndex());
                                            }
                                        });

                            } else {
                                //do delete
                                view.animate().translationX(viewX - view.getWidth()).setDuration(500);
                                rlActionPanel.animate().translationX(rlActionPanel.getTranslationX() - rlActionPanel.getWidth()).setDuration(500)
                                        .withEndAction(new Runnable() {
                                            @Override
                                            public void run() {
                                                onDeleteListner.end(getItemIndex());
                                            }
                                        });

                            }
                        } else {
                            view.animate().translationX(0).setDuration(250);
                            rlActionPanel.animate().translationX(0).setDuration(250);
                        }
                    } else {
                        timeDist = new Date().getTime() - timeDist;
                        if (timeDist <= 1000) {
                            Toast.makeText(getContext(), "jerk off", Toast.LENGTH_SHORT).show();
                            //trans to edit mode
                            edit();
                        }
                    }
                    pressed = false;
                    swiping = false;
                    timeDist = 0;

                    return true;

                default:
                    return false;
            }

        }
    }

}
