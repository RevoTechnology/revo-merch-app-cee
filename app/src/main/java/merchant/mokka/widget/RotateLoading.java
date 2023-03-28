package merchant.mokka.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.core.content.ContextCompat;

import merchant.mokka.R;

public class RotateLoading extends View {

    private static final int DEFAULT_WIDTH = 3;
    private static final int DEFAULT_SPEED_OF_DEGREE = 10;

    private Paint mPaint;

    private RectF firstRectF;
    private RectF secondRectF;

    private float firstArcStart;
    private float secondArcStart;

    private float sweepArc = 270;

    private int arcWidth;
    private boolean isStart = false;
    private int loadingColor;
    private int speedOfDegree;
    private int size;

    public RotateLoading(Context context) {
        super(context);
        initView(context, null);
    }

    public RotateLoading(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public RotateLoading(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        loadingColor = ContextCompat.getColor(context, R.color.colorAccent);
        arcWidth = dpToPx(context, DEFAULT_WIDTH);
        speedOfDegree = DEFAULT_SPEED_OF_DEGREE;

        if (null != attrs) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RotateLoading);
            arcWidth = typedArray.getDimensionPixelSize(R.styleable.RotateLoading_loading_width, dpToPx(context, DEFAULT_WIDTH));
            speedOfDegree = typedArray.getInt(R.styleable.RotateLoading_loading_speed, DEFAULT_SPEED_OF_DEGREE);
            typedArray.recycle();
        }

        mPaint = new Paint();
        mPaint.setColor(loadingColor);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(arcWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        firstArcStart = 0;
        secondArcStart = 270;

        if (w > h) size = h; else size = w;
        int s = size / 5;

        firstRectF = new RectF(s, s, w - s, h - s);
        secondRectF = new RectF(
                s * 2 - arcWidth + 2,
                s * 2 - arcWidth + 2,
                w - s * 2 + arcWidth - 2,
                h - s * 2 + arcWidth - 2
        );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(size / 2, size / 2, size / 2, mPaint);

        if (!isStart) {
            return;
        }

        mPaint.setColor(loadingColor);
        mPaint.setStyle(Paint.Style.STROKE);

        canvas.drawArc(firstRectF, firstArcStart, sweepArc, false, mPaint);
        canvas.drawArc(secondRectF, secondArcStart, sweepArc, false, mPaint);

        firstArcStart += speedOfDegree - 2;
        if (firstArcStart > 360) {
            firstArcStart = firstArcStart - 360;
        }

        secondArcStart -= speedOfDegree - 4;
        if (secondArcStart < 0) {
            secondArcStart = secondArcStart + 360;
        }

        invalidate();
    }

    public void setLoadingColor(int color) {
        this.loadingColor = color;
    }

    public int getLoadingColor() {
        return loadingColor;
    }

    public void start() {
        startAnimator();
        isStart = true;
        invalidate();
    }

    public void stop() {
        stopAnimator();
        invalidate();
    }

    public boolean isStart() {
        return isStart;
    }

    private void startAnimator() {
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(this, "scaleX", 0.0f, 1);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(this, "scaleY", 0.0f, 1);
        scaleXAnimator.setDuration(300);
        scaleXAnimator.setInterpolator(new LinearInterpolator());
        scaleYAnimator.setDuration(300);
        scaleYAnimator.setInterpolator(new LinearInterpolator());
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator);
        animatorSet.start();
    }

    private void stopAnimator() {
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(this, "scaleX", 1, 0);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(this, "scaleY", 1, 0);
        scaleXAnimator.setDuration(300);
        scaleXAnimator.setInterpolator(new LinearInterpolator());
        scaleYAnimator.setDuration(300);
        scaleYAnimator.setInterpolator(new LinearInterpolator());
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isStart = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();
    }


    public int dpToPx(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, context.getResources().getDisplayMetrics());
    }
}