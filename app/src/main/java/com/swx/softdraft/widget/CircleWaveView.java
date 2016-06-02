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
 * -----------------------------|---> x
 * |                 |          |
 * |              yoffset       |
 * |            ___________     |
 * |            |    |    |   height
 * |- xoffset - |-- 2R -- |     |
 * |            |    |    |     |
 * |            |_________|     |
 * |                            |
 * |_______________width________|
 * |
 * y
 */

public class CircleWaveView extends View {

    private int circleColor;
    private int waveColor;

    private Paint mCirclePaint;
    private Paint mWavePaint;

    private Paint mPaint;

    private int mWidth;
    private int mHeight;

    private float mRadius;
    private final float defRadius;

    private int xoffset = 0;
    private int y;

    private int percent = 50;
    private int dx = 0;

    boolean isWaveEnable = true;

    private Path mPath;

    private PorterDuffXfermode mPorterDuffXferMode;

    private final static int DEFAULT_SIZE = 400;

    private float amplitude = 0;
    private int mWaveLength = 0;

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
        circleColor = array.getColor(R.styleable.CircleWaveView_circleColor, Color.BLACK);
        waveColor = array.getColor(R.styleable.CircleWaveView_waveColor, Color.YELLOW);
        defRadius = array.getDimension(R.styleable.CircleWaveView_waveRadius, 0);
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


    private int mDx = 1;

    public void setDx(int dx) {
        if (dx < 1) {
            dx = 1;
        }
        mDx = dx;
    }

    public int getDx() {
        return mDx;
    }

    private int mDelay = 20;

    public void setDrawDelay(int delay) {
        if (delay < 20) {
            delay = 20;
        }
        mDelay = delay;
    }

    public int getDelay() {
        return mDelay;
    }

    private int mWaveCoefficient = 3;

    public int getWaveCoefficient() {
        return mWaveCoefficient;
    }

    public void setWaveCoefficient(int coefficient) {
        if (coefficient < 1) {
            coefficient = 1;
        }
        mWaveCoefficient = coefficient;
        mWaveLength = (int) mRadius * mWaveCoefficient;
        xoffset = (int) (mWidth / 2 - mRadius - mWaveLength / 2);
    }

    private int amCoefficient = 1;

    public int getAmCoefficient() {
        return amCoefficient;
    }

    public void setAmCoefficient(int coefficient) {
        if (coefficient < 1) {
            coefficient = 1;
        }
        amCoefficient = coefficient;
        float offset = mHeight / 2 - mRadius;
        amplitude = (y < mHeight / 2 ? y - offset : 2 * mRadius - y + offset) * amCoefficient * 0.2f;
    }

    public void setPercent(int percent) {
        if (percent < 0) {
            this.percent = 0;
        } else if (percent > 100) {
            this.percent = 100;
        } else {
            this.percent = percent;
        }
        setPercent();
        postInvalidate();
    }

    private void setPercent() {
        if (mHeight > 0) {
            float offset = mHeight / 2 - mRadius;
            y = (int) ((1 - percent / 100f) * 2 * mRadius + offset);
            amplitude = (y < mHeight / 2 ? y - offset : 2 * mRadius - y + offset) * amCoefficient * 0.2f;
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
        mRadius = mWidth > mHeight ? mHeight / 2 : mWidth / 2;
        if (defRadius != 0 && defRadius < mRadius) {
            mRadius = defRadius;
        }
        mWaveLength = (int) mRadius * mWaveCoefficient;
        xoffset = (int) (mWidth / 2 - mRadius - mWaveLength / 2);
        setPercent();
        setMeasuredDimension(mWidth, mHeight);
    }

    // src path
    // dst circle

    private void makeSrc(int width, int height) {
        if (mSrc == null || mSrc.getWidth() != width || mSrc.getHeight() != height) {
            mSrc = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(mSrc);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(circleColor);
            canvas.drawCircle(mWidth / 2, mHeight / 2, mRadius, paint);
        }
    }

    private void makeDst(int width, int height) {
        if (mDst == null || mDst.getWidth() != width || mDst.getHeight() != height) {
            mDst = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            mDstCanvas = new Canvas(mDst);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (dx < mWaveLength) {
            dx += mDx;
        } else {
            dx = 0;
        }

        mPath.reset();
        if (isWaveEnable) {
            mPath.moveTo(-mWaveLength + dx + xoffset, y);
            for (int i = -mWaveLength; i < mRadius * 2 + xoffset + mWaveLength; i += mWaveLength) {
                mPath.rCubicTo(mWaveLength / 4.0f, amplitude, 3.0f * mWaveLength / 4, -amplitude, mWaveLength, 0);
            }
//            mPath.cubicTo(100 + x * 2, y + 50, 100 + x * 2, y - 50, mWidth, y);
        } else {
            mPath.moveTo(0, y);
            mPath.lineTo(mWidth, y);
        }

        mPath.lineTo(mWidth, mHeight);
        mPath.lineTo(0, mHeight);
        mPath.close();

        super.onDraw(canvas);

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

        if (isWaveEnable) {
            postInvalidateDelayed(mDelay);
        }
    }
}
