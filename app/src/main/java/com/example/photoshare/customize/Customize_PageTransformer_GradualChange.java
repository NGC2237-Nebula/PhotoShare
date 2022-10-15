package com.example.photoshare.customize;

import android.util.Log;
import android.view.View;

import androidx.viewpager2.widget.ViewPager2;


public class Customize_PageTransformer_GradualChange implements ViewPager2.PageTransformer {
    private static final float MIN_SCALE = 0.75f;

    public void transformPage(View view, float position) {
        Log.d("LOG - PageTransformer", "PageTransformer : " + position);
        int pageWidth = view.getWidth();


        // [-无穷大,-1) 图片在 当前手机屏幕图片 的左边 2 页
        if (position < -1) {
            view.setAlpha(0);
        }


        // （-1,0]
        // position = -1 图片在 当前手机屏幕图片 的左边
        // position = 0  图片即 当前手机屏幕图片
        else if (position <= 0) {
            view.setTranslationX(0);  // 没有抵消滑动的增量
            view.setScaleX(1);
            view.setScaleY(1);
        }

        // [0,1]
        // position = 0  图片即 当前手机屏幕图片
        // position = 1  图片在 当前手机屏幕图片 的右边
        else if (position <= 1) {
            // 根据 position 设置透明度
            view.setAlpha(1 - position);
            // 根据 position 设置偏移量
            view.setTranslationX(pageWidth * -position); // 添加一个负的偏移量去抵消原本的偏移量
            // 根据 position 设置
            float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
        }

        // (1,+无穷大] 图片在 当前手机屏幕图片 的右边 2 页
        else {
            view.setAlpha(0);
        }
    }
}