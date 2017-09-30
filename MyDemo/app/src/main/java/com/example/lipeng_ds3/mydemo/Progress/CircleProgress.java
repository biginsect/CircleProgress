package com.example.lipeng_ds3.mydemo.Progress;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.lipeng_ds3.mydemo.BuildConfig;
import com.example.lipeng_ds3.mydemo.Progress.Utils.Constant;
import com.example.lipeng_ds3.mydemo.Progress.Utils.MiscUtil;
import com.example.lipeng_ds3.mydemo.R;


/**
 * Created by lipeng-ds3 on 2017/9/29.
 */

public class CircleProgress extends View {

    private static final String TAG = CircleProgress.class.getSimpleName();
    private Context mContext;

    /*默认大小*/
    private int mDefaultSize;
    //是否开启抗锯齿
    private boolean antiAlias;

    //draw hint
    private TextPaint mHintPaint;
    private CharSequence mHint;
    private int mHintColor;
    private float mHintSize;
    private float mHintOffset;

    //draw unit
    private TextPaint mUnitPaint;
    private CharSequence mUnit;
    private int mUnitColor;
    private float mUnitSize;
    private float mUnitOffset;

    //draw value
    private TextPaint mValuePaint;
    private float mValue;
    private float mMaxValue;
    private float mValueOffset;
    private int mPrecision;
    private String mPrecisionFormat;
    private int mValueColor;
    private float mValueSize;

    //draw Arc
    private Paint mArcPaint;
    private float mArcWidth;
    private float mStartAngle,mSweepAngle;
    private RectF mRectF;

    //gradual change is 360°, if 270, it will lack part of color
    private SweepGradient mSweepGradient;
    private int[] mGradientColors = {Color.GREEN, Color.YELLOW, Color.RED};

    // current progress, [0.0f, 1.0f]
    private float mPercent;

    //animation time
    private long mAnimTime;

    //Property Animation
    private ValueAnimator mAnimator;

    //draw background arc
    private Paint mBgArcPaint;
    private int mBgArcColor;
    private float mBgArcWidth;

    //coordinate of cicle centre ,radii
    private Point mCenterPoint;
    private float mRadius;
    private float mTextOffsetPercentInRadius;

    public CircleProgress(Context context, AttributeSet attrs){
        super(context, attrs);
        init(context, attrs);
    }

    //init
    private void init(Context context, AttributeSet attrs){
        mContext = context;
        mDefaultSize = MiscUtil.dipToPx(mContext, Constant.DEFAULT_SIZE);
        mAnimator = new ValueAnimator();
        mRectF = new RectF();
        mCenterPoint = new Point();
        initAttrs(attrs);
        initPaint();
    }

    //init attribute
    private void initAttrs(AttributeSet attrs){
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.CircleProgress);

        antiAlias = typedArray.getBoolean(R.styleable.CircleProgress_antiAlias, Constant.ANTI_ALIAS);

        mHint = typedArray.getString(R.styleable.CircleProgress_hint);
        mHintColor = typedArray.getColor(R.styleable.CircleProgress_hintColor, Color.BLACK);
        mHintSize = typedArray.getDimension(R.styleable.CircleProgress_hintSize, Constant.DEFAULT_HINT_SIZE);

        mValue = typedArray.getFloat(R.styleable.CircleProgress_value, Constant.DEFAULT_VALUE);
        mMaxValue = typedArray.getFloat(R.styleable.CircleProgress_maxValue, Constant.DEFAULT_MAX_VALUE);

        //precision format of content value
        mPrecision = typedArray.getInt(R.styleable.CircleProgress_precision, 0);
        mPrecisionFormat = MiscUtil.getPrecisionFormat(mPrecision);
        mValueColor = typedArray.getColor(R.styleable.CircleProgress_valueColor, Color.BLACK);
        mValueSize = typedArray.getDimension(R.styleable.CircleProgress_valueSize, Constant.DEFAULT_VALUE_SIZE);

        mUnit = typedArray.getString(R.styleable.CircleProgress_unit);
        mUnitColor = typedArray.getColor(R.styleable.CircleProgress_unitColor, Color.BLACK);
        mUnitSize = typedArray.getDimension(R.styleable.CircleProgress_unitSize, Constant.DEFAULT_UNIT_SIZE);

        mArcWidth = typedArray.getDimension(R.styleable.CircleProgress_arcWidth, Constant.DEFAULT_ARC_WIDTH);
        mStartAngle = typedArray.getFloat(R.styleable.CircleProgress_startAngle, Constant.DEFAULT_START_ANGLE);
        mSweepAngle = typedArray.getFloat(R.styleable.CircleProgress_sweepAngle, Constant.DEFAULT_SWEEP_ANGLE);

        mBgArcColor = typedArray.getColor(R.styleable.CircleProgress_bgArcColor, Color.WHITE);
        mBgArcWidth = typedArray.getDimension(R.styleable.CircleProgress_bgArcWidth, Constant.DEFAULT_ARC_WIDTH);
        mTextOffsetPercentInRadius = typedArray.getFloat(R.styleable.CircleProgress_textOffsetPercentInRadius, 0.33f);

        mAnimTime = typedArray.getInt(R.styleable.CircleProgress_animTime, Constant.DEFAULT_ANIM_TIME);

        int gradientArcColors = typedArray.getResourceId(R.styleable.CircleProgress_arcColors, 0);
        if (0 != gradientArcColors){
            try {
                int[] gradientColors = getResources().getIntArray(gradientArcColors);
                if (0 == gradientColors.length){
                    //考虑渐变色数组，为0则以单色读取色值
                    int color = getResources().getColor(gradientArcColors,null);
                    mGradientColors = new int[2];
                    mGradientColors[0] = color;
                    mGradientColors[1] = color;
                }else if (1 == gradientColors.length){
                    mGradientColors = new int[2];
                    mGradientColors[0] = gradientColors[0];
                    mGradientColors[1] = gradientColors[1];
                }else {
                    mGradientColors = gradientColors;
                }
            }catch (Resources.NotFoundException e){
                throw new Resources.NotFoundException("the give resource not found.");
            }
        }

        typedArray.recycle();
    }

    /**
     * init paint
     * */
    private void initPaint(){
        mHintPaint = new TextPaint();
        mHintPaint.setAntiAlias(antiAlias);

        //word size
        mHintPaint.setTextSize(mHintSize);
        mHintPaint.setColor(mHintColor);
        //from center to both sides
        mHintPaint.setTextAlign(Paint.Align.CENTER);

        mValuePaint = new TextPaint();
        mValuePaint.setAntiAlias(antiAlias);
        mValuePaint.setTextSize(mValueSize);
        mValuePaint.setColor(mValueColor);
        //word style
        mValuePaint.setTypeface(Typeface.DEFAULT_BOLD);
        mValuePaint.setTextAlign(Paint.Align.CENTER);

        mUnitPaint = new TextPaint();
        mUnitPaint.setAntiAlias(antiAlias);
        mUnitPaint.setTextSize(mUnitSize);
        mUnitPaint.setColor(mUnitColor);
        mUnitPaint.setTextAlign(Paint.Align.CENTER);

        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(antiAlias);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeMiter(mArcWidth);
        mArcPaint.setStrokeCap(Paint.Cap.ROUND);

        mBgArcPaint = new Paint();
        mBgArcPaint.setAntiAlias(antiAlias);
        mBgArcPaint.setColor(mBgArcColor);
        mBgArcPaint.setStyle(Paint.Style.STROKE);
        mBgArcPaint.setStrokeWidth(mBgArcWidth);
        mBgArcPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MiscUtil.measure(widthMeasureSpec, mDefaultSize),
                MiscUtil.measure(heightMeasureSpec, mDefaultSize));
    }

    private float getBaselineOffsetFromY(Paint paint){
        return MiscUtil.measureTextHeight(paint) / 2;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "onSizeChanged : w = " + w + "; h = " + h + "; oldw = " + oldw + "; oldh = " + oldh );

        //max width between arc and background arc
        float maxArcWidth = Math.max(mArcWidth, mBgArcWidth);

        //take mix value as actual value
        int minSize = Math.min(w - getPaddingLeft() - getPaddingRight() - 2 *(int)maxArcWidth,
                h - getPaddingTop() - getPaddingBottom() - 2 * (int)maxArcWidth);

        mRadius = minSize / 2;

        mCenterPoint.x = w / 2;
        mCenterPoint.y = h / 2;

        //draw arc border
        mRectF.left = mCenterPoint.x - mRadius - maxArcWidth / 2;
        mRectF.top = mCenterPoint.y - mRadius - maxArcWidth / 2;
        mRectF.right = mCenterPoint.x + mRadius + maxArcWidth / 2;
        mRectF.bottom = mCenterPoint.y + mRadius + maxArcWidth / 2;

        //calculate baseline of word draw
        mValueOffset = mCenterPoint.y + getBaselineOffsetFromY(mValuePaint);
        mHintOffset = mCenterPoint.y - mRadius * mTextOffsetPercentInRadius + getBaselineOffsetFromY(mHintPaint);
        mUnitOffset = mCenterPoint.y + mRadius * mTextOffsetPercentInRadius + getBaselineOffsetFromY(mUnitPaint);

        updateArcPaint();

        Log.d(TAG, "onSizeChanged : the size of control: " + "(" + w +
        ", " + h + ")" + "center coordinates = " + mCenterPoint.toString()
        + "; Radius = " + mRadius
        + "; 圆的外接矩形 = " + mRectF.toString());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawText(canvas);
        drawArc(canvas);
    }

    private void drawText(Canvas canvas){
        //计算文字宽度,paint为居中绘制，因此计算比较简单
        canvas.drawText(String.format(mPrecisionFormat, mValue), mCenterPoint.x, mValueOffset, mValuePaint);

        if (null != mHint){
            canvas.drawText(mHint.toString(), mCenterPoint.x, mHintOffset, mHintPaint);
        }

        if (null != mUnit){
            canvas.drawText(mUnit.toString(), mCenterPoint.x, mUnitOffset, mUnitPaint);
        }
    }

    private void drawArc(Canvas canvas){
        canvas.save();
        float currentAngle = mSweepAngle * mPercent;
        canvas.rotate(mStartAngle, mCenterPoint.x, mCenterPoint.y);
        canvas.drawArc(mRectF, currentAngle, mSweepAngle - currentAngle + 2, false, mBgArcPaint);

        // 第一个参数 oval 为 RectF 类型，即圆弧显示区域
        // startAngle 和 sweepAngle  均为 float 类型，分别表示圆弧起始角度和圆弧度数
        // 3点钟方向为0度，顺时针递增
        // 如果 startAngle < 0 或者 > 360,则 startAngle 对 360求余
        // useCenter:如果为True时，在绘制圆弧时将圆心包括在内，通常用来绘制扇形
        canvas.drawArc(mRectF, 2, currentAngle, false, mArcPaint);
        canvas.restore();
    }

    //update Arc painter
    private void updateArcPaint(){
        //gradual change
        int[] mGradientColors = {Color.GREEN, Color.YELLOW, Color.RED};
        mSweepGradient = new SweepGradient(mCenterPoint.x, mCenterPoint.y, mGradientColors, null);
        mArcPaint.setShader(mSweepGradient);
    }

    public boolean isAntiAlias() {
        return antiAlias;
    }

    public CharSequence getHint() {
        return mHint;
    }

    public void setHint(CharSequence hint) {
        this.mHint = hint;
    }

    public CharSequence getUnit() {
        return mUnit;
    }

    public void setUnit(CharSequence unit) {
        this.mUnit = unit;
    }

    public float getValue() {
        return mValue;
    }

    /**
     * set current value
     * @param value
     * */
    public void setValue(float value) {

        if (value > mMaxValue)
            value = mMaxValue;

        float start = mPercent;
        float end = value / mMaxValue;
        startAnimator(start, end, mAnimTime);
    }

    private void startAnimator(float start, float end, final long animTime){
        mAnimator = ValueAnimator.ofFloat(start, end);
        mAnimator.setDuration(animTime);

        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mPercent = (float) valueAnimator.getAnimatedValue();
                mValue = mPercent * mMaxValue;

                if (BuildConfig.DEBUG){
                    Log.d(TAG, "onAnimationUpdate: percent = " + mPercent
                    +"; currentAngle = " + (mSweepAngle * mPercent)
                    +"; value = " + mValue);
                }
                invalidate();
            }
        });

        mAnimator.start();
    }

    /**
     * obtain max value
     * @return
     * */
    public float getMaxValue() {
        return mMaxValue;
    }

    /**
     * set max value
     * @param maxValue
     * */
    public void setMaxValue(float maxValue) {
        this.mMaxValue = maxValue;
    }

    /**
     * obtain precision
     * @return
     * */
    public int getPrecision() {
        return mPrecision;
    }

    public void setPrecision(int precision){
        mPrecision = precision;
        mPrecisionFormat = MiscUtil.getPrecisionFormat(precision);
    }

    public int[] getGradientColors(){
        return mGradientColors;
    }

    public void setGradientColors(int[] gradientColors){
        mGradientColors = gradientColors;
        updateArcPaint();
    }

    public long getAnimTime(){
        return mAnimTime;
    }

    public void setAnimTime(long animTime) {
        this.mAnimTime = animTime;
    }

    /**
     * reset
     * */
    public void reset(){
        startAnimator(mPercent, 0.0f, 1000L);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //release resource
    }
}
