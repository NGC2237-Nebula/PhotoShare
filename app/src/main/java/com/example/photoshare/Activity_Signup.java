package com.example.photoshare;

import static com.example.photoshare.constant.Constant_APP.*;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.photoshare.constant.Constant_APP;
import com.example.photoshare.parse.Request_Interceptor;
import com.example.photoshare.parse.Response_UserGeneral;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Activity_Signup extends AppCompatActivity {

    /* 账号 */
    /**
     * 账号框
     */
    private EditText etUsername;
    /**
     * 登录账号
     */
    private String username;

    /* 密码 */
    /**
     * 密码框
     */
    private EditText etPassword;
    /**
     * 登录密码
     */
    private String password;


    /* 监听器 */
    /**
     * 注册按钮 监听器 - 判断输入的账号、密码能否注册，是否转入下一个界面
     */
    private final View.OnClickListener btSignupListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            username = etUsername.getText().toString();
            password = etPassword.getText().toString();
            if (username.equals("") || password.equals(""))
                Toast.makeText(Activity_Signup.this, "账号或密码不能为空!", Toast.LENGTH_LONG).show();
            else
                networkRequest();
        }
    };
    /**
     * 返回按钮 监听器 - 返回登录界面
     */
    private final View.OnClickListener ivArrowListener = v -> {
        Intent intent = new Intent();
        intent.setClass(Activity_Signup.this, Activity_Login.class);
        startActivity(intent);
    };


    /* 网络请求 */

    /**
     * 网络请求
     */
    private void networkRequest() {
        Log.d("LOG", "======== 网络请求 ========");
        new Thread(() -> {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("password", password);
                jsonObject.put("username", username);
                RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), jsonObject.toString());
                Request request = new Request.Builder().url(Constant_APP.USER_REGISTER_POST_URL).post(requestBody).build();

                OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Request_Interceptor()).build();
                client.newCall(request).enqueue(signupCallback);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 网络请求 回调
     */
    private final okhttp3.Callback signupCallback = new okhttp3.Callback() {
        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            ResponseBody responseBody = response.body();
            String body = responseBody.string();

            Log.d("LOG - 注册", "响应体 : " + body);

            if (response.isSuccessful()) {
                runOnUiThread(() -> {
                    Response_UserGeneral responseParse = new Gson().fromJson(body, Response_UserGeneral.class);

                    Message message = new Message();
                    if (responseParse.getMsg() == null) message.arg1 = 1;
                    else if (responseParse.getMsg().equals("用户名已存在")) message.arg1 = 2;
                    else if (responseParse.getMsg().equals("password must not be blank"))
                        message.arg1 = 3;
                    else if (responseParse.getMsg().equals("username must not be blank"))
                        message.arg1 = 4;
                    else message.arg1 = -1;
                    signupHandler.sendMessage(message);
                });
            }
        }

        @Override
        public void onFailure(@NonNull Call call, IOException e) {
            e.printStackTrace();
        }
    };
    /**
     * 网络操作 操作响应
     */
    private final Handler signupHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            if (msg.arg1 == 1) {
                Toast.makeText(Activity_Signup.this, "注册成功,请输入账号、密码登录!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(Activity_Signup.this, Activity_Login.class);
                startActivity(intent);
            }
            if (msg.arg1 == 2)
                Toast.makeText(Activity_Signup.this, "存在相同用户名,请重新输入!", Toast.LENGTH_SHORT).show();
            if (msg.arg1 == 3)
                Toast.makeText(Activity_Signup.this, "密码不能为空!", Toast.LENGTH_SHORT).show();
            if (msg.arg1 == 4)
                Toast.makeText(Activity_Signup.this, "账号不能为空!", Toast.LENGTH_SHORT).show();
            if (msg.arg1 == -1)
                Toast.makeText(Activity_Signup.this, "网络连接失败!", Toast.LENGTH_SHORT).show();
        }
    };


    /**
     * 初始化 视图
     */
    private void initView() {
        etUsername = findViewById(R.id.et_signup_username);
        etPassword = findViewById(R.id.et_signup_password);
        Button btSignup = findViewById(R.id.bt_signup_signup);
        ImageView ivArrow = findViewById(R.id.iv_signup_arrow);

        btSignup.setOnClickListener(btSignupListener);
        ivArrow.setOnClickListener(ivArrowListener);
    }

    /**
     * 初始化 数据
     */
    private void initData() {
        Intent intent = getIntent();
        password = intent.getStringExtra(USER_PASSWORD);
        username = intent.getStringExtra(USER_USERNAME);
        if (!username.equals("") || !password.equals("")) {
            etUsername.setText(username);
            etPassword.setText(password);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().hide();
        initView();
        initData();
    }
}
