package com.tk.besselpro.view;

import android.graphics.PointF;

import com.nineoldandroids.animation.TypeEvaluator;

/**
 * Created by TK on 2016/8/11.
 */
public class BesselTypeEvaluator implements TypeEvaluator<PointF> {
    private PointF p1;
    private PointF p2;

    public BesselTypeEvaluator(PointF p1, PointF p2) {
        //三阶贝塞尔控制点
        this.p1 = p1;
        this.p2 = p2;
    }

    @Override
    public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
        PointF newP = new PointF();
        newP.x = (float) (startValue.x * Math.pow(1 - fraction, 3)
                + 3 * p1.x * fraction * Math.pow(1 - fraction, 2)
                + 3 * p2.x * Math.pow(fraction, 2) * (1 - fraction)
                + endValue.x * Math.pow(fraction, 3));
        newP.y = (float) (startValue.y * Math.pow(1 - fraction, 3)
                + 3 * p1.y * fraction * Math.pow(1 - fraction, 2)
                + 3 * p2.y * Math.pow(fraction, 2) * (1 - fraction)
                + endValue.y * Math.pow(fraction, 3));

        return newP;
    }
}
