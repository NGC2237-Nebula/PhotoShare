package com.example.photoshare;

import static com.example.photoshare.constant.Constant_APP.USER_ID;
import static com.example.photoshare.constant.Constant_APP.USER_LOGIN_POST_URL;
import static com.example.photoshare.constant.Constant_APP.USER_MESSAGE;
import static com.example.photoshare.constant.Constant_APP.USER_PASSWORD;
import static com.example.photoshare.constant.Constant_APP.USER_USERNAME;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.photoshare.entity.Entity_User;
import com.example.photoshare.parse.Request_Interceptor;
import com.example.photoshare.parse.Response_UserGeneral;
import com.example.photoshare.tool.Tool_SharedPreferencesManager;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @author 孙昊君
 */

public class
Activity_Begin extends AppCompatActivity {

    /* 数据 */
    private String username;
    private String password;

    private Entity_User user;
    private String userMessage;


    /* 界面跳转 */

    /**
     * 跳转到 登录 界面
     */
    private void jumpToLogin() {
        Intent intent = new Intent(Activity_Begin.this, Activity_Login.class);
        startActivity(intent);
    }

    /**
     * 跳转到 主 界面
     */
    private void jumpToMenu() {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(Activity_Begin.this, Activity_Menu.class);
        intent.putExtra(USER_ID, user.getId());
        intent.putExtra(USER_MESSAGE, userMessage);
        intent.putExtra(USER_USERNAME, username);
        intent.putExtra(USER_PASSWORD, password);
        startActivity(intent);
    }


    /* 网络请求 */
    /**
     * 网络请求 响应操作
     */
    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            if (msg.arg1 == 1) jumpToMenu();
            else if (msg.arg1 == 2) jumpToLogin();
        }
    };
    /**
     * 网络请求 回调
     */
    private final okhttp3.Callback loginCallback = new okhttp3.Callback() {
        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            ResponseBody responseBody = response.body();
            userMessage = responseBody.string();
            Log.d("LOG - 登录", "响应体 : " + userMessage);
            if (response.isSuccessful()) {
                runOnUiThread(() -> {
                    Gson gson = new Gson();
                    Response_UserGeneral responseParse = gson.fromJson(userMessage, Response_UserGeneral.class);
                    user = responseParse.getUser();

                    Message message = new Message();
                    if (responseParse.getMsg().equals("登录成功")) message.arg1 = 1;
                    else message.arg1 = 2;
                    mHandler.sendMessage(message);
                });
            }
        }

        @Override
        public void onFailure(@NonNull Call call, IOException e) {
            e.printStackTrace();
        }
    };

    /**
     * 网络请求
     */
    private void networkRequest() {
        Log.d("LOG", "======== 网络请求 ========");
        new Thread(() -> {
            String urlParam = "?" + "&password=" + password + "&username=" + username;

            RequestBody requestBody = new FormBody.Builder().build();
            Request request = new Request.Builder().url(USER_LOGIN_POST_URL + urlParam).post(requestBody).build();

            // 在拦截器中添加 appId、appSecret
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Request_Interceptor()).build();
            client.newCall(request).enqueue(loginCallback);
        }).start();
    }


    /**
     * 判断用户是否已经登录
     */
    private void judgeWhetherLogin() {
        Tool_SharedPreferencesManager manager = new Tool_SharedPreferencesManager(getApplicationContext());
        if (manager.getWhetherLoginKey()) {
            username = manager.getUsernameKey();
            password = manager.getPasswordKey();
            if (username != null && password != null) networkRequest();
            else {
                manager.removeData();
                jumpToLogin();
            }
        } else jumpToLogin();
    }

    /**
     * 将 状态栏 设置为透明
     */
    private void setStatusBarTransparent() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_begin);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin);

        getSupportActionBar().hide();
        setStatusBarTransparent();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                judgeWhetherLogin();
            }
        }, 3 * 1000);
    }
}