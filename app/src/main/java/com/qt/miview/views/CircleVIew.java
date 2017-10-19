package com.qt.miview.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by user on 2017/10/16.
 */

public class CircleVIew extends View {

    public static final int FULL_STEP = 2500;

    //各种画笔
    private Paint mPaint;//画撒花的圈的
    private Paint mBallPaint;//画撒花小球的,兼画步数进度顶点的实体圆
    private Paint mProgressPaintDash;//画步数进度条虚线圈的
    private Paint mProgressPaint;//画步数进度实线圈
    private Paint mLargeCirclePaint;//画最大圆的画笔
    //    各种路径
    private Path mPath;//画撒花圈
    private Path mProgressPath;//撒花进度条

    private int mBasePadding = 104;
    private int mWidth;
    private int mHeight;
    private int mCenterX;//各个圆的中心
    private int mCenterY;//
    private int mEndPointX;//计步进度条实线圆的中心点
    private int mEndPointY;
    private int mRadius;
    private int mSteps;//步数
    private int mAngle;//步数进度条角度
    private  int mIncreament = 52;//外面大圈相对于里面虚线圈半径大了多少
    private  int mMaxIncreament = 88;//放大的最大值
    private int mLargeRadius;//大圆半径
    private Matrix matrix;
    private float rotate;
    private int count;//计数器,用于绘制大圆放大缩小
    private ArrayList<Ball> ballSet = new ArrayList<Ball>();
    private boolean success = false;
    private onScaleListener onScaleListener;
    int  span = 2;

    public CircleVIew(Context context) {
        this(context, null);
    }

    public CircleVIew(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleVIew(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //初始化圈画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.WHITE);
        mPaint.setShadowLayer(2, 0, 0, Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(3);
        mPath = new Path();
        //撒花画笔
        mBallPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBallPaint.setColor(Color.WHITE);
        mBallPaint.setShadowLayer(10, 0, 0, Color.WHITE);
        mBallPaint.setStyle(Paint.Style.FILL);
        mBallPaint.setStrokeWidth(3);
        //进度条画笔虚线
        mProgressPaintDash = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressPaintDash.setColor(Color.parseColor("#88FFFFFF"));
        mProgressPaintDash.setShadowLayer(10, 0, 0, Color.WHITE);
        mProgressPaintDash.setStyle(Paint.Style.STROKE);
        mProgressPaintDash.setStrokeWidth(5);
        mProgressPath = new Path();
        //进度条画笔实线
        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressPaint.setColor(Color.parseColor("#EEFFFFFF"));
        mProgressPaint.setShadowLayer(10, 0, 0, Color.WHITE);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeWidth(5);
        //大圆画笔
        mLargeCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLargeCirclePaint.setColor(Color.parseColor("#ffFFFFFF"));
        mLargeCirclePaint.setShadowLayer(10, 0, 0, Color.WHITE);
        mLargeCirclePaint.setStyle(Paint.Style.STROKE);
        mLargeCirclePaint.setStrokeWidth(36);
        matrix = new Matrix();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getWidth();
        mHeight = getHeight();
        mCenterX = mWidth / 2;
        mCenterY = mHeight / 2;
        mRadius = mWidth / 2 - mBasePadding;
        mEndPointX = (int) (mCenterX + Math.sin(mAngle * Math.PI / 180) * mRadius);
        mEndPointY = (int) (mCenterY - Math.cos(mAngle * Math.PI / 180) * mRadius);
        mLargeRadius = mRadius + mIncreament;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.parseColor("#00000000"));
        if (!success) {
            drawCircle1(canvas);
        } else {
            if(count == 2){
                drawCircleProgress(canvas);
            }
            drawLargeCircle(canvas);
        }
        invalidate();
    }

    /**
     * 绘制外边最大的圆
     *
     * @param canvas
     */
    private void drawLargeCircle(Canvas canvas) {
        canvas.drawCircle(mCenterX, mCenterY, mLargeRadius, mLargeCirclePaint);
        Shader shader = new SweepGradient(mCenterX, mCenterY, new int[]{Color.parseColor("#88ffffff"), Color.parseColor("#ffffffff"), Color.parseColor("#88ffffff")},
                new float[]{0.25f, 0.5f, 0.75f});
        shader.setLocalMatrix(matrix);
        matrix.setRotate(rotate, mLargeRadius, mLargeRadius);
        rotate += 1f;
        if (rotate >= 360) {
            rotate = 0;
        }
        mLargeCirclePaint.setShadowLayer(10, 0, 0, Color.WHITE);
        mLargeCirclePaint.setShader(shader);
        if (count == 0) {
            mIncreament += span;
            if (mIncreament >= mMaxIncreament) {
                count = 1;
            }
        } else if (count == 1) {
            mIncreament -= span;
            if (mIncreament <= 52) {
                mIncreament = 52;
                count = 2;
            }
        }
        if (onScaleListener != null ){
            onScaleListener.onScale(mIncreament-52,mMaxIncreament - 52,count);
        }
        mLargeRadius = mRadius + mIncreament;
        //为大圆发亮部分加上阴影
    }

    /**
     * 绘制那个步数进度条的圈
     *
     * @param canvas
     */
    private void drawCircleProgress(Canvas canvas) {
        //画虚线圈
        mProgressPath.reset();
        mProgressPaintDash.setPathEffect(new DashPathEffect(new float[]{3f, 7f}, 10));
        mProgressPath.addCircle(mCenterX, mCenterY, mRadius, Path.Direction.CCW);
        canvas.drawPath(mProgressPath, mProgressPaintDash);
        //hua画实线圈
        mProgressPath.reset();
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPath.addArc(new RectF(mCenterX - mRadius, mCenterY - mRadius, mCenterX + mRadius, mCenterY + mRadius), 270, mAngle);
        canvas.drawPath(mProgressPath, mProgressPaint);
        //画顶点实心圆
        mBallPaint.setColor(Color.WHITE);
        mBallPaint.setShadowLayer(10, 0, 0, Color.WHITE);
        canvas.drawCircle(mEndPointX, mEndPointY, 10, mBallPaint);
    }

    /**
     * 绘制撒花的那个圈
     *
     * @param canvas
     */
    private void drawCircle1(Canvas canvas) {
        Shader shader = new SweepGradient(mCenterX, mCenterY, Color.parseColor("#88ffffff"), Color.parseColor("#03ffffff"));
        mPaint.setShader(shader);
        mPath.addCircle(mCenterX - 10, mCenterY, mRadius, Path.Direction.CCW);
        canvas.drawPath(mPath, mPaint);
        mPath.reset();
        mPath.addCircle(mCenterX, mCenterY, mRadius, Path.Direction.CCW);
        canvas.drawPath(mPath, mPaint);
        mPath.reset();
        mPath.addCircle(mCenterX + 10, mCenterY, mRadius, Path.Direction.CCW);
        canvas.drawPath(mPath, mPaint);
        mPath.reset();
        mPath.addCircle(mCenterX, mCenterY + 10, mRadius, Path.Direction.CCW);
        canvas.drawPath(mPath, mPaint);
        mPath.reset();
        mPath.addCircle(mCenterX, mCenterY - 10, mRadius, Path.Direction.CCW);
        canvas.drawPath(mPath, mPaint);
        drawBalls(canvas);
    }

    /**
     * 撒花效果绘制
     *
     * @param canvas
     */
    private void drawBalls(Canvas canvas) {
        int x = mCenterX + mRadius;
        int y = mCenterY + 20;
        Ball ball = new Ball(x, y, 7);
        ballSet.add(ball);
        for (int i = 0; i < ballSet.size(); i++) {
            Ball b = ballSet.get(i);
            b.drawSelf(canvas);
            if (b.y < b.orignY - mRadius * 3 / 4) {
                ballSet.remove(b);
            }
        }
    }

    /**
     * 小球(花~~)实体类
     */
    private class Ball {
        public int orignX;
        public int orignY;
        public int x;
        public int y;
        public int r;
        public int speedY = 20;
        public int speedX;


        public Ball(int x, int y, int r) {
            this.x = x + (int) (6 * 2 * (Math.random() - 0.5));
            this.y = y;
            this.r = r;
            orignX = x;
            orignY = y;
            speedX = (int) ((2 * Math.random() - 1.5) * speedY * Math.tan(20 * Math.PI / 180));
        }

        public void drawSelf(Canvas canvas) {

            int alpha = (255 * (y - orignY) * 4 / (mRadius * 3));
            mBallPaint.setColor(Color.WHITE);
            mBallPaint.setAlpha(alpha);
            canvas.drawCircle(x, y, r, mBallPaint);
            y -= speedY;
            x += speedX;
        }
    }

    public interface onScaleListener {
        /**
         * 大圈放大时调用
         * @param changeValue 当前半径变化量
         * @param max 半径最大变化量
         * @param count 缩放阶段
         */
        void onScale(int changeValue,int max,int count);
    }
    //对外部分==============================================

    /**
     * 调用此方法后第一阶段动画结束
     */
    public void setSuccess() {
        success = true;
    }

    public boolean isSuccess() {
        return success;
    }

    /**
     * 调用此方法后开始动画
     */
    public void startProgress() {
        success = false;
        count = 0;
    }

    /**
     * 调用此方法增加步数
     *
     * @param stepNum
     */
    public void setmSteps(int stepNum) {
        if (stepNum < 0) {
            stepNum = 0;
        }
        this.mSteps = stepNum;
        if (stepNum < FULL_STEP) {
            mAngle = (int) (360 * ((float) stepNum / (float) FULL_STEP));
        } else {
            mAngle = 360;
        }
        mEndPointX = (int) (mCenterX + Math.sin(mAngle * Math.PI / 180) * mRadius);
        mEndPointY = (int) (mCenterY - Math.cos(mAngle * Math.PI / 180) * mRadius);
    }
    /**
     * 设置onScaleListener
     */
    public void setOnScaleListener(onScaleListener onScaleListener){
        this.onScaleListener = onScaleListener;
    }

    public int getSpan(){
        return span;
    }
}
