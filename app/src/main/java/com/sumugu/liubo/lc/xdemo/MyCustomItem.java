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
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sumugu.liubo.lc.R;
import com.sumugu.liubo.lc.utils.DisplayUtil;

/**
 * Created by liubo on 16/6/27.
 */
public class MyCustomItem extends FrameLayout {

    private final String TAG = MyCustomItem.class.getSimpleName();
    private Context mContext;
    private TextView tvDelete;
    private TextView tvFinish;
    private TextView tvDisplay;
    private EditText editText;
    private RelativeLayout rlActionPanel;


    public MyCustomItem(Context context) {
        super(context);
        loadLayout(context);
    }

    public MyCustomItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadLayout(context);
    }

    public MyCustomItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadLayout(context);
    }
    private void loadLayout(Context context)
    {
        setWillNotDraw(false);

        View view = LayoutInflater.from(context).inflate(R.layout.custom_item,this,true);
        tvDelete = (TextView)view.findViewById(R.id.ci_text_delete);
        tvDisplay = (TextView)view.findViewById(R.id.ci_text_dispaly);
        tvFinish =  (TextView)view.findViewById(R.id.ci_text_finish);
        editText = (EditText)view.findViewById(R.id.ci_edit_content);
        rlActionPanel = (RelativeLayout)view.findViewById(R.id.ci_action_panel);

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

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "mci start onDraw");

        int defaultHeight = DisplayUtil.dip2px(getContext(), 45f);
        int defaultWidth = getMeasuredWidth();

        Bitmap bitmap = Bitmap.createBitmap(defaultWidth, defaultHeight, Bitmap.Config.ARGB_8888);
        Canvas mcanvas = new Canvas(bitmap);
        mcanvas.drawColor(Color.GRAY);

        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(60f);
        textPaint.setColor(Color.WHITE);

        float deltaX = 0;
        int h = getMeasuredHeight();

        if (h < defaultHeight) {
            mcanvas.drawText("This.is.MyDemo.", 0, 100, textPaint);
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
        } else {
//            mcanvas.drawText("Release.to.restart.", 0, 100, textPaint);
//            canvas.drawBitmap(bitmap, 0, 0, null);
            editText.setVisibility(VISIBLE);
//        ViewGroup.LayoutParams editParams = editText.getLayoutParams();
//        editParams.height = getMeasuredHeight();
//        editText.setLayoutParams(editParams);
            editText.requestFocus();
            setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            super.onDraw(canvas);
        }

//        canvas.save();
//        canvas.restore();
//        super.onDraw(canvas);


    }
}
