package com.tk.besselpro.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.nineoldandroids.animation.ValueAnimator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TK on 2016/8/10.
 * 下雨+水波纹
 */
public class RainWaveView extends View {
    //最多同时几滴雨
    private static final int MAX = 24;
    //雨点长度
    private static final int LENGTH = 18;
    //雨点悬空高度
    private static final int SPACE = 250;
    //雨速
    private static final int RAIN_SPEED = 6;

    private static final int DEGREE = 18;
    private static final int WAVE_WIDTH = 80;
    private Paint paint = new Paint();
    private Path path = new Path();
    private List<PointF> pointFList = new ArrayList<PointF>();
    private List<PointF> rainFList = new ArrayList<PointF>();
    private ValueAnimator valueAnimator;
    private float offsetX = 0f;

    public RainWaveView(Context context) {
        super(context);
        init();
    }

    public RainWaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        paint.setDither(true);
        paint.setAntiAlias(true);
        paint.setColor(0xFF1E85D4);
        paint.setStrokeWidth(3.5f);
        paint.setStyle(Paint.Style.FILL);
        valueAnimator = ValueAnimator.ofFloat(0f, 1f).setDuration(2000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.setRepeatCount(-1);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                offsetX = (float) animation.getAnimatedValue();
                calculRain();
                postInvalidate();
            }
        });
        postDelayed(new Runnable() {
            @Override
            public void run() {
                valueAnimator.start();
            }
        }, 1000);
    }

    /**
     * 计算雨点偏移并生成新的雨点
     */
    private void calculRain() {
        for (int i = 0; i < MAX; i++) {
            if (rainFList.get(i).y - LENGTH > (getHeight() >> 1)) {
                rainFList.set(i, new PointF((float) (getWidth() * Math.random()), 0));
            } else {
                rainFList.get(i).offset(0, RAIN_SPEED);
            }
        }

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int l = pointFList.size();
        path.reset();
        path.moveTo(pointFList.get(0).x, pointFList.get(0).y);
        for (int i = 0; i < l - 1; i++) {
            if (i % 2 == 0) {
                path.quadTo(pointFList.get(i).x + (WAVE_WIDTH >> 1), (getHeight() >> 1) - DEGREE,
                        pointFList.get(i + 1).x, pointFList.get(i + 1).y);
            } else {
                path.quadTo(pointFList.get(i).x + (WAVE_WIDTH >> 1), (getHeight() >> 1) + DEGREE,
                        pointFList.get(i + 1).x, pointFList.get(i + 1).y);
            }
        }
        path.rLineTo(0, getHeight() >> 1);
        path.lineTo(pointFList.get(0).x, getHeight());
        path.close();
        if (offsetX != 0f) {
            canvas.translate(offsetX * 2 * WAVE_WIDTH, 0);
        }
        canvas.drawPath(path, paint);
        canvas.restore();
//        canvas.drawLine(50, 50, 100, 100, paint);
        //画雨点
        int l2 = rainFList.size();
        for (int i = 0; i < l2; i++) {
            if (rainFList.get(i).y > 0) {
                canvas.drawLine(rainFList.get(i).x, rainFList.get(i).y, rainFList.get(i).x,
                        rainFList.get(i).y - LENGTH < 0 ? 0 : rainFList.get(i).y - LENGTH, paint);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int size = (int) (w / WAVE_WIDTH + 2);
        pointFList.clear();
        pointFList.add(new PointF(-WAVE_WIDTH << 1, h >> 1));
        pointFList.add(new PointF(-WAVE_WIDTH, h >> 1));
        for (int i = 0; i < size; i++) {
            pointFList.add(new PointF(i * WAVE_WIDTH, h >> 1));
        }
        rainFList.clear();
        for (int i = 0; i < MAX; i++) {
            rainFList.add(new PointF((float) (w * Math.random()), (float) -Math.random() * SPACE));
        }
    }


}
