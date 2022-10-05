package com.example.photoshare.interfaces;

import android.view.View;

/**
 * 用于传递 Adapter 中被点击的视图以及信息的接口
 */
public interface Interface_ClickViewSend {
    /**
     * 点击返回 view 以及 对应位置
     * @param view 被点击的 view
     * @param position 被点击 view 的对应位置
     */
    void onItemClick(View view, int position);
}
