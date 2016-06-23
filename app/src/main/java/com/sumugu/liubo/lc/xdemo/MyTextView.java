package com.sumugu.liubo.lc.xdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.sumugu.liubo.lc.utils.DisplayUtil;

/**
 * Created by liubo on 16/6/23.
 */
public class MyTextView extends TextView {

    final String TAG = "TAG_MyTextView";

    public MyTextView(Context context) {
        super(context);
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
        Log.d(TAG, "start onDraw");

        int defaultHeight = DisplayUtil.dip2px(getContext(), 80f);
        int defaultWidth = getMeasuredWidth();
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(80f);
        textPaint.setColor(Color.WHITE);

        //原图
        Bitmap bitmap = Bitmap.createBitmap(defaultWidth, defaultHeight, Bitmap.Config.ARGB_8888);
        Canvas mcanvas = new Canvas(bitmap);
        mcanvas.drawColor(Color.GRAY);
        mcanvas.drawText("This.is.MyDemo.Let's.Roll.", 0, defaultHeight/2+20, textPaint);

        //取上半部分
        Bitmap bitmapUp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight()/2);
//        Canvas canvasUp = new Canvas(bitmapUp);
//        canvasUp.drawColor(Color.BLUE);

        //取下半部分
        Bitmap bitmapDown = Bitmap.createBitmap(bitmap,0,defaultHeight/2,bitmap.getWidth(),bitmap.getHeight()/2);
//        Canvas canvasDown = new Canvas(bitmapDown);
//        canvasDown.drawColor(Color.MAGENTA);

        float deltaX = 0;
        int h = getMeasuredHeight();

        if (h < defaultHeight) {
            mcanvas.drawText("This.is.MyDemo.Let's.Roll.", 0, defaultHeight/2+20, textPaint);
            deltaX = (float) Math.sqrt(Math.pow(defaultHeight, 2) - Math.pow(h, 2));

            //下半部分的矩阵
            float[] src = new float[]{
                    0, 0,           //左上(x,y)
                    defaultWidth, 0,//右上
                    defaultWidth, defaultHeight/2,//左下
                    0, defaultHeight/2            //右下
            };
            float[] dst = new float[]{
                    deltaX, 0,                  //左上(x,y)
                    defaultWidth - deltaX, 0,   //右上
                    defaultWidth, getMeasuredHeight()/2,  //左下
                    0, getMeasuredHeight()/2              //右下
            };
            Matrix matrix = new Matrix();
            matrix.setPolyToPoly(src, 0, dst, 0, src.length >> 1);
            bitmapDown = Bitmap.createBitmap(bitmapDown,0,0,bitmapDown.getWidth(),bitmapDown.getHeight(),matrix,false);
            //

            //上半部分的矩阵
            float[] upsrc = new float[]{
                    0, 0,           //左上(x,y)
                    defaultWidth, 0,//右上
                    defaultWidth, defaultHeight/2,//左下
                    0, defaultHeight/2            //右下
            };
            float[] updst = new float[]{
                    0, 0,                  //左上(x,y)
                    defaultWidth, 0,   //右上
                    defaultWidth-deltaX, getMeasuredHeight()/2,  //左下
                    deltaX, getMeasuredHeight()/2              //右下
            };
            Matrix upMatrix = new Matrix();
            upMatrix.setPolyToPoly(upsrc,0,updst,0,upsrc.length>>1);
            bitmapUp = Bitmap.createBitmap(bitmapUp,0,0,bitmapUp.getWidth(),bitmapUp.getHeight(),upMatrix,false);

//            canvas.drawBitmap(bitmap, matrix, null);

            canvas.drawBitmap(bitmapUp,0,0,null);
            canvas.drawBitmap(bitmapDown,0,h/2,null);

            float[] upValues=new float[9];
            float[] downValues=new float[9];
            upMatrix.getValues(upValues);
            matrix.getValues(downValues);
            for (int i=0;i<upValues.length;i++) {
                Log.d(TAG, "upMatrix:"+i+"="+upValues[i]);
            }
            for (int i=0;i<downValues.length;i++) {
                Log.d(TAG,"downMatrix:"+i+":"+downValues[i]);
            }

        } else {
            mcanvas.drawColor(Color.GRAY);
            mcanvas.drawText("Release.to.restart.", 0, defaultHeight/2+20, textPaint);
            canvas.drawBitmap(bitmap, 0, 0, null);
        }

//        canvas.save();
//        super.onDraw(canvas);
//        canvas.restore();

    }
}

