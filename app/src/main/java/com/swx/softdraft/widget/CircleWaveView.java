package com.swx.softdraft.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

import com.swx.softdraft.R;

/**
 * Created by swx on 3/31/16.
 * wave animation
 */

public class CircleWaveView extends View {

    private int circleColor;
    private int waveColor;

    private Paint mCirclePaint;
    private Paint mWavePaint;

    private Paint mPaint;

    private int mWidth;
    private int mHeight;

    private int x = 51;
    private int y;

    private int percent = 50;

    boolean isLeft = true;

    boolean isWaveEnable = true;

    private Path mPath;

    private PorterDuffXfermode mPorterDuffXferMode;

    private final static int DEFAULT_SIZE = 400;

    private Bitmap mSrc;
    private Bitmap mDst;

    private Canvas mDstCanvas;

    public CircleWaveView(Context context) {
        this(context, null);
    }

    public CircleWaveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleWaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CircleWaveView, defStyleAttr, 0);
        circleColor = array.getColor(R.styleable.CircleWaveView_circleColor, Color.BLUE);
        waveColor = array.getColor(R.styleable.CircleWaveView_waveColor, Color.YELLOW);
        array.recycle();

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        mPorterDuffXferMode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);

        mCirclePaint = new Paint();
        mCirclePaint.setColor(circleColor);
        mCirclePaint.setAntiAlias(true);

        mWavePaint = new Paint();
        mWavePaint.setAntiAlias(true);
        mWavePaint.setColor(waveColor);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mPath = new Path();
    }

    public void setWaveEnable(boolean enableWave) {
        isWaveEnable = enableWave;
        postInvalidate();
    }

    public void setPercent(int percent) {
        if (percent < 0) {
            this.percent = 0;
        } else if (percent > 100) {
            this.percent = 100;
        } else {
            this.percent = percent;
        }
        postInvalidate();
    }

    private void makeSrc(int width, int height) {
        if (mSrc == null || mSrc.getWidth() != width || mSrc.getHeight() != height) {
            mSrc = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(mSrc);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(circleColor);
            canvas.drawCircle(mWidth / 2, mHeight / 2, mWidth / 2, paint);
        }
    }

    private void makeDst(int width, int height) {
        if (mDst == null || mDst.getWidth() != width || mDst.getHeight() != height) {
            mDst = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            mDstCanvas = new Canvas(mDst);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode == MeasureSpec.AT_MOST) {
            mWidth = DEFAULT_SIZE;
        } else {
            mWidth = widthSize;
        }
        if (heightMode == MeasureSpec.AT_MOST) {
            mHeight = DEFAULT_SIZE;
        } else {
            mHeight = heightSize;
        }
        setMeasuredDimension(mWidth, mHeight);
    }

    // src path
    // dst circle
//
//    private Bitmap mBitmap;
//    private Canvas mCanvas;
//
//    private void makeBitmap() {
//        if (mBitmap == null || mBitmap.getWidth() != mWidth || mBitmap.getHeight() != mHeight) {
//            mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
//            mCanvas = new Canvas(mBitmap);
//        }
//    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (x > 50) {
            isLeft = true;
        } else if (x < 0) {
            isLeft = false;
        }
        if (isLeft) {
            x -= 10;
        } else {
            x += 10;
        }

        mPath.reset();

        y = (int) ((1 - percent / 100f) * mHeight);
        mPath.moveTo(0, y);
        if (isWaveEnable) {
            mPath.cubicTo(100 + x * 2, y + 50, 100 + x * 2, y - 50, mWidth, y);
        } else {
            mPath.lineTo(mWidth, y);
        }

        mPath.lineTo(mWidth, mHeight);
        mPath.lineTo(0, mHeight);
        mPath.close();

        super.onDraw(canvas);

//        makeBitmap();
//        mBitmap.eraseColor(Color.TRANSPARENT);
//        mCanvas.drawCircle(mWidth / 2, mHeight / 2, mWidth / 2, mCirclePaint);
//        mWavePaint.setXfermode(mPorterDuffXferMode);
//        mCanvas.drawPath(mPath, mWavePaint);
//        mWavePaint.setXfermode(null);
//        canvas.drawBitmap(mBitmap, 0, 0, null);

//----
        makeSrc(mWidth, mHeight);
        makeDst(mWidth, mHeight);

        mDstCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        mDstCanvas.drawPath(mPath, mWavePaint);

        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        int id = canvas.saveLayer(0, 0, mWidth, mHeight, null, Canvas.ALL_SAVE_FLAG);
        canvas.drawBitmap(mSrc, 0, 0, mPaint);
        mPaint.setXfermode(mPorterDuffXferMode);
        canvas.drawBitmap(mDst, 0, 0, mPaint);
        mPaint.setXfermode(null);
        canvas.restoreToCount(id);
//-----
//        canvas.drawColor(Color.WHITE);
//        int canvasWidth = canvas.getWidth();
//        int canvasHeight = canvas.getHeight();
//        int saveLayerId = canvas.saveLayer(0, 0, canvasWidth, canvasHeight, null, Canvas.ALL_SAVE_FLAG);
//        canvas.drawCircle(mWidth / 2, mHeight / 2, mWidth / 2, mCirclePaint);
//        canvas.drawPath(mPath, mWavePaint);
//        canvas.restoreToCount(saveLayerId);

        if (isWaveEnable) {
            postInvalidateDelayed(50);
        }
    }
}
