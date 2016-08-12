package com.tk.besselpro.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;
import com.tk.besselpro.R;

/**
 * Created by TK on 2016/8/11.
 */
public class RainRefreshView extends View {
    //贝塞尔圆常数
    private static final float CIRCLE = 0.55191502449f;
    //雨滴系数
    private static final int MIN_RAIN = 10;
    private static final int MAX_RAIN = 40;
    //球
    private static final int BALL_RADIUS = 80;
    //触发可滴落
    private static final int DOWN_RAIN = 30;
    //下拉阻力
    private static final int PRESSURE = 3;
    //最大下拉距离
    private static final int MAX_DISTANCE = 240;
    //产生水滴距离
    private static final int RAIN_DISTANCE = 60;
    //top回弹
    private static final int DURING = 1000;
    //top下拉比例
    private static final int TOP_OFFSET = 4;

    private Paint paint = new Paint();
    //主路径
    private Path mainPath = new Path();
    //雨点路径
    private Path rainPath = new Path();
    private boolean animLock;
    private float lastX;
    private float lastY;
    private float distance;
    private ValueAnimator topAnim;
    private ValueAnimator rotateAnim;
    private boolean refresh;
    private RotateAnimation animation;
    private float rotate;
    private Bitmap refreshBitmap;

    public RainRefreshView(Context context) {
        super(context);
        init();
    }

    public RainRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        paint.setDither(true);
        paint.setAntiAlias(true);
        paint.setColor(0xFF1E85D4);
        paint.setStyle(Paint.Style.FILL);
        rotateAnim = ValueAnimator.ofFloat(0, -360).setDuration(750);
        rotateAnim.setRepeatCount(-1);
        rotateAnim.setRepeatMode(ValueAnimator.RESTART);
        rotateAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                rotate = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        rotateAnim.setInterpolator(new LinearInterpolator());
        Drawable drawable = getContext().getResources().getDrawable(R.drawable.vector_loading);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        refreshBitmap = bitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!mainPath.isEmpty()) {
            canvas.drawPath(mainPath, paint);
        }
        if (!rainPath.isEmpty()) {
            canvas.drawPath(rainPath, paint);
        } else if (refresh) {
            canvas.drawCircle(getWidth() >> 1, getHeight() >> 1, BALL_RADIUS, paint);
            canvas.rotate(rotate, getWidth() >> 1, getHeight() >> 1);
            Rect rect = new Rect((getWidth() >> 1) - BALL_RADIUS, (getHeight() >> 1) - BALL_RADIUS,
                    (getWidth() >> 1) + BALL_RADIUS, (getHeight() >> 1) + BALL_RADIUS);
            canvas.drawBitmap(refreshBitmap, null, rect, null);
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (animLock) {
                    break;
                }
                lastX = event.getX();
                lastY = event.getY();
                return true;
            case MotionEvent.ACTION_MOVE:
                distance = (event.getY() - lastY) / PRESSURE;
                if (distance > MAX_DISTANCE) {
                    distance = MAX_DISTANCE;
                    break;
                }
                refreshPath(distance, true);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                animLock = true;
                float size = (distance - RAIN_DISTANCE) / (MAX_DISTANCE - RAIN_DISTANCE) * (MAX_RAIN - MIN_RAIN) + MIN_RAIN;
                startTopAnim(size < DOWN_RAIN);
                break;
        }
        return super.dispatchTouchEvent(event);
    }


    /**
     * 刷新水滴和雨点的path
     *
     * @param flag 是否显示水滴
     */
    private void refreshPath(float distance, boolean flag) {
        mainPath.reset();
        mainPath.rLineTo(0, distance / TOP_OFFSET);
        mainPath.quadTo(getWidth() >> 1, distance / TOP_OFFSET, getWidth() >> 1, distance);
        if (distance > RAIN_DISTANCE && flag) {
            //带水滴
            //水滴大小
            float size = (distance - RAIN_DISTANCE) / (MAX_DISTANCE - RAIN_DISTANCE) * (MAX_RAIN - MIN_RAIN) + MIN_RAIN;
            mainPath.rCubicTo(0, size * CIRCLE, -size, size * CIRCLE, -size, 3 * size * CIRCLE);

            float circleX = getWidth() >> 1;
            float circleY = distance + size * CIRCLE * 3;
            RectF rectf = new RectF(circleX - size, circleY - size, circleX + size, circleY + size);
            mainPath.arcTo(rectf, 180, -180);
            mainPath.rCubicTo(0, -2 * size * CIRCLE, -size, -2 * size * CIRCLE, -size, -3 * size * CIRCLE);

            mainPath.quadTo(getWidth() >> 1, distance / TOP_OFFSET, getWidth(), distance / TOP_OFFSET);
            mainPath.rLineTo(0, -distance / TOP_OFFSET);
            mainPath.close();
        } else {
            mainPath.quadTo(getWidth() >> 1, distance / TOP_OFFSET, getWidth() >> 1, distance);
            mainPath.quadTo(getWidth() >> 1, distance / TOP_OFFSET, getWidth(), distance / TOP_OFFSET);
            mainPath.rLineTo(0, -distance / TOP_OFFSET);
            mainPath.close();
        }
    }

    /**
     * 开启顶部动画
     *
     * @param flag 是否显示顶部水滴
     */
    private void startTopAnim(final boolean flag) {
        topAnim = ValueAnimator.ofFloat(distance, 0).setDuration(DURING);
        topAnim.setInterpolator(new DecelerateInterpolator());
        topAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float d = (float) animation.getAnimatedValue();
                refreshPath(d, flag);
                if (!flag) {
                    //雨点动画
                    initRainPath(d);
                }
                invalidate();
            }
        });
        topAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animLock = false;
                if (!flag) {
                    refresh = true;
                    rainPath.reset();
                    rotateAnim.start();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                animLock = false;
                refresh = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        topAnim.start();
    }

    private void initRainPath(float d) {
        //百分比 0-1
        float offset = (distance - d) / distance;
        //半径
        float size1 = (distance - RAIN_DISTANCE) / (MAX_DISTANCE - RAIN_DISTANCE) * (MAX_RAIN - MIN_RAIN) + MIN_RAIN;
        float size = size1 + (BALL_RADIUS - size1) * offset;
        //运动的距离
        float disY = (getHeight() >> 1) - distance - 3 * size * CIRCLE;
        //0-1
        float offsin = (float) Math.sin(offset * 90 * Math.PI / 180);
        //1-0
        float offcos = (float) Math.cos(offset * 90 * Math.PI / 180);
        //顶端到圆心的距离
        float offR = 3 * size * CIRCLE - size;
        rainPath.reset();
        //移动到顶端
        rainPath.moveTo(getWidth() >> 1, distance + (disY + offR) * offset);

        rainPath.rCubicTo(-offsin * size * CIRCLE, offcos * size * CIRCLE,
                -size, size + (1 - offset) * offR - size * CIRCLE * (2 - offset),
                -size, size + (1 - offset) * offR);

        float circleX = getWidth() >> 1;
        float circleY = (distance + 3 * size * CIRCLE) + disY * offset;
        RectF rectf = new RectF(circleX - size, circleY - size, circleX + size, circleY + size);
        rainPath.arcTo(rectf, 180, -180);
        rainPath.moveTo(getWidth() >> 1, distance + (disY + offR) * offset);
        rainPath.rCubicTo(offsin * size * CIRCLE, offcos * size * CIRCLE,
                size, size + (1 - offset) * offR - size * CIRCLE * (2 - offset),
                size, size + (1 - offset) * offR);
        rainPath.close();
    }
}
