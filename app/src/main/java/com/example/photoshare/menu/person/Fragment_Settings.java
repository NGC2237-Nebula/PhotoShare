package com.example.photoshare.menu.person;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.photoshare.Activity_Login;
import com.example.photoshare.R;
import com.example.photoshare.tool.Tool_CacheDataManager;
import com.example.photoshare.tool.Tool_SharedPreferencesManager;

public class Fragment_Settings extends Fragment {

    /* 数据 */
    private Context context;

    /* 控件 */
    private TextView tvCache;
    private Dialog dialog;

    /* 标识符 */
    private final int HANDLER_CLEAR_SUCCESS = 1;
    private final int HANDLER_CLEAR_FAIL = 2;

    /* 监听器 */
    private final View.OnClickListener ivBackListener = v ->
            Navigation.findNavController(requireView()).popBackStack();

    private final View.OnClickListener rlAboutUsListener = v ->
            Navigation.findNavController(requireView()).navigate(R.id.action_fragment_Settings_to_fragment_AboutUs);

    private final View.OnClickListener rlCheckUpdataListener = v ->
            Toast.makeText(context, "当前为最新版本", Toast.LENGTH_SHORT).show();

    private final View.OnClickListener rlFeedbackListener = v ->
            Toast.makeText(context, "你没有意见反馈哦 o(=•ェ•=)m", Toast.LENGTH_SHORT).show();

    private final View.OnClickListener btQuitListener = v -> {
        Tool_SharedPreferencesManager manager = new Tool_SharedPreferencesManager(requireContext());
        manager.clearData();
        Intent intent = new Intent(getActivity(), Activity_Login.class);
        startActivity(intent);
    };

    private final View.OnClickListener cvCancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialog.dismiss();
        }
    };

    private final View.OnClickListener tvConfirmListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startClearCache();
            dialog.dismiss();
        }
    };

    private final View.OnClickListener rlClearCacheListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialog.show();
        }
    };


    /**
     * 线程异步清理缓存
     */
    private void startClearCache(){
        new Thread(() -> {
            try {
                Tool_CacheDataManager.clearAllCache(context);
                Thread.sleep(1000);
                if (Tool_CacheDataManager.getTotalCacheSize(context).startsWith("0")) {
                    clearCacheHandler.sendEmptyMessage(HANDLER_CLEAR_SUCCESS);
                } else clearCacheHandler.sendEmptyMessage(HANDLER_CLEAR_FAIL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private final Handler clearCacheHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == HANDLER_CLEAR_SUCCESS) {
                Toast.makeText(context, "清理完成", Toast.LENGTH_SHORT).show();
                try {
                    tvCache.setText(Tool_CacheDataManager.getTotalCacheSize(context));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (msg.what == HANDLER_CLEAR_FAIL) {
                Toast.makeText(context, "清理失败，请重试", Toast.LENGTH_SHORT).show();
            }
        }
    };



    private void init(View root){
        ImageView ivBack = root.findViewById(R.id.iv_settings_back);
        ivBack.setOnClickListener(ivBackListener);

        RelativeLayout rlAboutUs = root.findViewById(R.id.rl_settings_about_us);
        rlAboutUs.setOnClickListener(rlAboutUsListener);

        RelativeLayout rlFeedback = root.findViewById(R.id.rl_settings_feed_back);
        rlFeedback.setOnClickListener(rlFeedbackListener);

        RelativeLayout rlCheckUpdata = root.findViewById(R.id.rl_settings_check_updates);
        rlCheckUpdata.setOnClickListener(rlCheckUpdataListener);

        RelativeLayout rlClearCache = root.findViewById(R.id.rl_settings_clear_cache);
        rlClearCache.setOnClickListener(rlClearCacheListener);

        tvCache = root.findViewById(R.id.tv_settings_cache);
        try {
            tvCache.setText(Tool_CacheDataManager.getTotalCacheSize(context));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Button btQuit = root.findViewById(R.id.bt_settings_quit);
        btQuit.setOnClickListener(btQuitListener);


        /* 设置 底部对话框 */
        dialog = new Dialog(requireContext(), R.style.BottomDialog);
        View view = View.inflate(requireContext(), R.layout.dialog_settings_clean_cache, null);
        dialog.setContentView(view);

        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.BottomDialog);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView tvConfirm = dialog.findViewById(R.id.dialog_myself_delete_confirm_confirm);
        CardView cvCancel = dialog.findViewById(R.id.dialog_myself_delete_confirm_cancel);

        tvConfirm.setOnClickListener(tvConfirmListener);
        cvCancel.setOnClickListener(cvCancelListener);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        context = requireContext();
        init(root);
        return root;
    }
}