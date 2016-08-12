package com.tk.besselpro.view;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.tk.besselpro.R;

/**
 * Created by TK on 2016/8/11.
 * 献爱心
 */
public class GiveLoveView extends RelativeLayout {
    //动画时长
    private static final int DURING = 3000;
    private static final int INIT_DURING = 300;
    //爱心大小
    private static final int SIZE = 240;
    //一次3个
    private static final int CREATE_SIZE = 3;
    private static final int[] DRAWABLE = new int[]{
            R.drawable.vector_heart_orange,
            R.drawable.vector_heart_pink,
            R.drawable.vector_heart_purple,
            R.drawable.vector_heart_red,
            R.drawable.vector_heart_yellow};

    public GiveLoveView(Context context) {
        super(context);
    }

    public GiveLoveView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }


    /**
     * 生成爱心
     */
    public void showLove() {
        for (int i = 0; i < CREATE_SIZE; i++) {
            ImageView imageView = new ImageView(getContext());
            initHeart(imageView);
            startAnim(imageView);
        }
    }

    /**
     * 初始化爱心
     *
     * @param imageView
     */
    private void initHeart(ImageView imageView) {
        imageView.setImageDrawable(getContext().getResources().getDrawable(DRAWABLE[(int) (Math.random() * DRAWABLE.length)]));
        imageView.setLayoutParams(new ViewGroup.LayoutParams(SIZE, SIZE));
        imageView.setX((getWidth() - SIZE) / 2);
        imageView.setY(getHeight() - SIZE);
        addView(imageView);
    }

    /**
     * 开启动画
     *
     * @param imageView
     */
    private void startAnim(final ImageView imageView) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(imageView, "scaleX", 0.3f, 1f).setDuration(INIT_DURING);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(imageView, "scaleY", 0.3f, 1f).setDuration(INIT_DURING);
        ValueAnimator valueAnimator = ValueAnimator.ofObject(new BesselTypeEvaluator(
                        new PointF((float) (Math.random() * (getWidth() - SIZE)),
                                (float) (((getHeight() - SIZE) >> 1) + Math.random() * ((getHeight() - SIZE) >> 1))),
                        new PointF((float) (Math.random() * (getWidth() - SIZE)),
                                (float) Math.random() * ((getHeight() - SIZE) >> 1))
                ),
                new PointF((getWidth() - SIZE) / 2, getHeight() - SIZE),
                new PointF((float) (Math.random() * (getWidth() - SIZE)), 0))
                .setDuration(DURING);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                PointF p = (PointF) animation.getAnimatedValue();
                imageView.setAlpha(p.y / (getHeight() - SIZE));
                imageView.setX(p.x);
                imageView.setY(p.y);
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                imageView.setImageDrawable(null);
                removeView(imageView);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                imageView.setImageDrawable(null);
                removeView(imageView);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        AnimatorSet allAnim = new AnimatorSet();
        allAnim.setDuration(DURING);
        allAnim.playTogether(scaleX, scaleY, valueAnimator);
        allAnim.start();
    }
}
