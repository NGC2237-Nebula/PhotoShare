package com.example.photoshare.customize;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

public class Customize_Animator {
    /**
     * 隐藏 视图
     */
    public static int HIDE_VIEW = 1;
    /**
     * 显示 视图
     */
    public static int SHOW_VIEW = 2;

    /**
     * 设置 控件 展示以及隐藏时的动画控制逻辑
     *
     * @param view  控件
     * @param state 控件状态
     */
    public void setFadeAnimator(View view, int state) {
        if (state == SHOW_VIEW) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f).setDuration(300);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    view.setVisibility(View.VISIBLE);
                }
            });
            animator.start();
        } else if (state == HIDE_VIEW) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f).setDuration(300);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    view.setVisibility(View.INVISIBLE);
                }
            });
            animator.start();
        }
    }


    public void setSlideFadeAnimator(View view, int state){
        if (state == SHOW_VIEW) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(view,"translationY",100f,0f);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    view.setVisibility(View.VISIBLE);
                }
            });
            AnimatorSet animSet = new AnimatorSet();
            animSet.play(animator).with(animator1);
            animSet.setDuration(500);
            animSet.start();

        } else if (state == HIDE_VIEW) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f).setDuration(300);
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(view,"translationY",0f,100f);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    view.setVisibility(View.INVISIBLE);
                }
            });
            AnimatorSet animSet = new AnimatorSet();
            animSet.play(animator).with(animator1);
            animSet.setDuration(500);
            animSet.start();
        }

    }

    /**
     * 设置 心动 效果，用于点赞、收藏时的动画
     *
     * @param view 视图
     */
    public void setCardiacAnimator(View view) {
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.1f, 1f);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.1f, 1f);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(view, "alpha", 0f, 0.8f, 1f, 1f, 1f, 0.8f, 0f);

        AnimatorSet animSet = new AnimatorSet();
        animSet.play(scaleXAnimator).with(scaleYAnimator).with(alphaAnimator);
        animSet.setDuration(1400);
        animSet.start();
    }
}
