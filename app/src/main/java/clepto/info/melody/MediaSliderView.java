package clepto.info.melody;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


/**
 * TODO: document your custom view class.
 */
public class MediaSliderView extends View {

    private int mSliderColor;
    private int mSliderShadowColor;
    private int mSliderWidth;

    private int mPaddingLeft;
    private int mPaddingTop;
    private int mPaddingRight;
    private int mPaddingBottom;
    private int contentWidth;
    private int contentHeight;

    private RectF rectF;
    private float mStartAngle = 120;
    private float mEndAngle = 300;
    private float mSweepAngle;
    private Paint mSliderPaint;
    private Paint mSliderShadowPaint;

    public MediaSliderView(Context context) {
        super(context);
        init(null, 0);
    }

    public MediaSliderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public MediaSliderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.MediaSliderView, defStyle, 0);

//      if (a.hasValue(R.styleable.MediaSliderView_sliderColor)) {
        mSliderColor = a.getColor(R.styleable.MediaSliderView_sliderColor, Color.BLACK);

        mSliderPaint = new Paint();
        mSliderPaint.setStyle(Paint.Style.STROKE);
        mSliderPaint.setAntiAlias(true);
        mSliderPaint.setColor(mSliderColor);
        mSliderPaint.setStrokeWidth(5);
        //mSliderPaint.setShadowLayer(5f, 2f, 4f, Color.BLACK);
//      }

//      if (a.hasValue(R.styleable.MediaSliderView_sliderShadowColor)) {
        mSliderShadowColor = a.getColor(R.styleable.MediaSliderView_sliderShadowColor, Color.CYAN);

        mSliderShadowPaint = new Paint();
        mSliderShadowPaint.setStyle(Paint.Style.STROKE);
        mSliderShadowPaint.setAntiAlias(true);
        mSliderShadowPaint.setColor(mSliderShadowColor);
        mSliderShadowPaint.setStrokeWidth(5);
//      }

        a.recycle();

        mSliderWidth = 10;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawArc(rectF, mStartAngle, 300, false, mSliderShadowPaint);
        canvas.drawArc(rectF, mStartAngle, mSweepAngle, false, mSliderPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //Get padding values
        mPaddingLeft = getPaddingLeft();
        mPaddingTop = getPaddingTop();
        mPaddingRight = getPaddingRight();
        mPaddingBottom = getPaddingBottom();

        //Get dimensions of our content
        contentWidth = getWidth() - mPaddingLeft - mPaddingRight;
        contentHeight = getHeight() - mPaddingTop - mPaddingBottom;

        rectF = new RectF(mPaddingLeft, mPaddingTop,
                mPaddingRight + contentWidth, mPaddingBottom + contentHeight);
        Log.d("melody", "MediaSliderView rect's dimension changed: " + rectF.toString());
    }

    /**
     * Gets the slider drawable attribute value.
     * @return The slider drawable attribute value.
     */
    public int getSliderColor() {
        return mSliderColor;
    }

    /**
     * Sets the view's slider drawable attribute value.
     * @param sliderColor The example drawable attribute value to use.
     */
    public void setSliderDrawable(int sliderColor) {
        mSliderColor = sliderColor;
        invalidate();
        requestLayout();
    }

    public float getmSweepAngle() {
        return mSweepAngle;
    }

    public void setmSweepAngle(float mSweepAngle) {
        this.mSweepAngle = mSweepAngle;
    }

    public float getmEndAngle() {
        return mEndAngle;
    }

    /**
     * Gets the slider shadow drawable attribute value.
     * @return The slider shadow drawable attribute value.
     */
    public int getSliderShadowDrawable() {
        return mSliderShadowColor;
    }

    /**
     * Sets the view's slider drawable attribute value.
     * @param sliderShadowColor The example drawable attribute value to use.
     */
    public void setSliderShadowDrawable(int sliderShadowColor) {
        mSliderShadowColor = sliderShadowColor;
        invalidate();
        requestLayout();
    }

    public int getProgress() {
        return (int) mSweepAngle;
    }

    public void setProgress(int progress) {
        mSweepAngle = progress;
    }
}
