package com.example.photoshare.customize;

import android.util.Log;
import android.view.View;
import androidx.viewpager2.widget.ViewPager2;

public class Customize_PageTransformer_ZoomOut implements ViewPager2.PageTransformer {
    private static final float MIN_SCALE = 0.85f;
    private static final float MIN_ALPHA = 0.5f;

    public void transformPage(View view, float position) {
        Log.d("LOG - PageTransformer","PageTransformer : "+position);

        // [-无穷大,-1) 图片在 当前手机屏幕图片 的左边 2 页
        if (position < -1) {
            view.setAlpha(0);
        }
        // [-1,1] 图片在 当前手机屏幕图片 的右边或左边
        // position = -1 图片在 当前手机屏幕图片 的左边
        // position = 0  图片即 当前手机屏幕图片
        // position = 1  图片在 当前手机屏幕图片 的右边
        else if (position <= 1) {
            view.setTranslationX(0);
            // 缩放过滤器 - 保证图片缩小时不会小于 设定的最小缩放比例
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
            // 设置 缩放 (在 MIN_SCALE 和 1 之间)
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
            // 设置 透明度
            view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));
        }

        // (1,+无穷大] 图片在 当前手机屏幕图片 的右边 2 页
        else {
            view.setAlpha(0);
        }
    }
}
