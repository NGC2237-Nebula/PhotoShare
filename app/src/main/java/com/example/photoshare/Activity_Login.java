package com.example.photoshare;

import static com.example.photoshare.constant.Constant_APP.USER_ID;
import static com.example.photoshare.constant.Constant_APP.USER_MESSAGE;
import static com.example.photoshare.constant.Constant_APP.USER_PASSWORD;
import static com.example.photoshare.constant.Constant_APP.USER_USERNAME;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.NetworkOnMainThreadException;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.photoshare.constant.Constant_APP;
import com.example.photoshare.entity.Entity_User;
import com.example.photoshare.parse.Request_Interceptor;
import com.example.photoshare.parse.Response_UserGeneral;
import com.example.photoshare.tool.Tool_SharedPreferencesManager;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Activity_Login extends AppCompatActivity {

    /* 数据库 */
    /**
     * SharedPreferences管理器
     */
    Tool_SharedPreferencesManager manager;

    /* 账号 */
    /**
     * 账号框
     */
    private EditText etUsername;
    /**
     * 登录账号
     */
    private String username = null;

    /* 密码 */
    /**
     * 密码框
     */
    private EditText etPassword;
    /**
     * 登录密码
     */
    private String password = null;
    /**
     * 是否记住密码 复选框
     */
    private CheckBox cbRememberPwd;
    /**
     * 是否记住密码
     */
    private boolean rememberPassword;
    /**
     * 密码是否可见图标
     */
    private ImageView ivPwdSwitch;
    /**
     * 密码是否可见
     */
    private boolean bPwdSwitch = false;


    /* 用户信息 */
    /**
     * 登录用户
     */
    private Entity_User user;
    /**
     * 登录用户信息
     */
    public String userMessage = null;



    /* 监听器 */
    /**
     * 图标 监听器 - 密码是否可见
     */
    private final View.OnClickListener ivPasswordKeyListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            bPwdSwitch = !bPwdSwitch;//密码可见
            if (bPwdSwitch) {
                ivPwdSwitch.setImageResource(R.drawable.ic_baseline_visibility_white);
                etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {//密码不可见
                ivPwdSwitch.setImageResource(R.drawable.ic_baseline_visibility_off_white);
                etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                etPassword.setTypeface(Typeface.DEFAULT);
            }
        }
    };
    /**
     * 登录按钮 监听器 - 判断输入的账号、密码是否正确，是否转入下一个界面
     */
    private final View.OnClickListener btLoginListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            username = etUsername.getText().toString();
            password = etPassword.getText().toString();
            if (username.equals("") || password.equals(""))
                Toast.makeText(Activity_Login.this, "账号或密码不能为空!", Toast.LENGTH_SHORT).show();
            else networkRequest();
        }
    };
    /**
     * 注册按钮 监听器 - 转入注册界面
     */
    private final View.OnClickListener tvSignupListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            username = etUsername.getText().toString();
            password = etPassword.getText().toString();
            Intent intent = new Intent(Activity_Login.this, Activity_Signup.class);
            intent.putExtra(USER_PASSWORD, password);
            intent.putExtra(USER_USERNAME, username);
            startActivity(intent);
        }
    };



    /* 网络请求 */

    /**
     * 网络请求
     */
    private void networkRequest() {
        Log.d("LOG", "======== 网络请求 ========");

        new Thread(() -> {
            Request request;
            RequestBody requestBody;

            String urlParam = "?" + "&password=" + password + "&username=" + username;
            requestBody = new FormBody.Builder().build();
            request = new Request.Builder().url(Constant_APP.USER_LOGIN_POST_URL + urlParam).post(requestBody).build();

            try {
                OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Request_Interceptor()).build();
                client.newCall(request).enqueue(loginCallback);
            } catch (NetworkOnMainThreadException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

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
                    switch (responseParse.getMsg()) {
                        case "登录成功":
                            message.arg1 = 1;
                            break;
                        case "密码错误":
                            message.arg1 = 2;
                            break;
                        case "当前登录用户不存在":
                            message.arg1 = 3;
                            break;
                        case "必传字段不能为空":
                            message.arg1 = 4;
                            break;
                        default:
                            message.arg1 = -1;
                            break;
                    }
                    mLoginHandler.sendMessage(message);
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
    private final Handler mLoginHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            if (msg.arg1 == 1) {
                saveInSharedPreferences();
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(Activity_Login.this, Activity_Menu.class);

                username = etUsername.getText().toString();
                password = etPassword.getText().toString();

                intent.putExtra(USER_PASSWORD, password);
                intent.putExtra(USER_USERNAME, username);

                intent.putExtra(USER_ID, user.getId());
                intent.putExtra(USER_MESSAGE, userMessage);
                startActivity(intent);
            }
            if (msg.arg1 == 2)
                Toast.makeText(Activity_Login.this, "密码错误!", Toast.LENGTH_SHORT).show();
            if (msg.arg1 == 3)
                Toast.makeText(Activity_Login.this, "用户不存在!", Toast.LENGTH_SHORT).show();
            if (msg.arg1 == 4)
                Toast.makeText(Activity_Login.this, "账号或密码不能为空!", Toast.LENGTH_SHORT).show();
            if (msg.arg1 == -1)
                Toast.makeText(Activity_Login.this, "网络连接失败!", Toast.LENGTH_SHORT).show();
        }
    };



    /* SharedPreferences */

    /**
     * 将 账号、密码 保存到 SharedPreferences 中
     */
    private void saveInSharedPreferences() {
        if (cbRememberPwd.isChecked()) {
            manager.saveData(username, password, true, true);
        } else {
            manager.removeData();
        }
    }


    /* 初始化 */

    /**
     * 初始化 数据
     */
    private void initData() {
        manager = new Tool_SharedPreferencesManager(getApplicationContext());
        username = manager.getUsernameKey();
        password = manager.getPasswordKey();
        rememberPassword = manager.getRememberPassword();
    }

    /**
     * 初始化 视图
     */
    private void initView() {
        /* 绑定视图 */
        etUsername = findViewById(R.id.et_login_username);
        etPassword = findViewById(R.id.et_login_password);
        cbRememberPwd = findViewById(R.id.cb_login_remember_pwd);
        ivPwdSwitch = findViewById(R.id.iv_login_pwd_switch);
        TextView tvSignup = findViewById(R.id.tv_login_signup);
        Button btLogin = findViewById(R.id.bt_login_login);

        /* 监听器 */
        ivPwdSwitch.setOnClickListener(ivPasswordKeyListener);
        tvSignup.setOnClickListener(tvSignupListener);
        btLogin.setOnClickListener(btLoginListener);

        /* 读取 SharedPreferences 存储的用户账号信息 */
        if (username != null && !TextUtils.isEmpty(username)) etUsername.setText(username);
        if (password != null && !TextUtils.isEmpty(password)) etPassword.setText(password);
        cbRememberPwd.setChecked(rememberPassword);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Objects.requireNonNull(getSupportActionBar()).hide();
        initData();
        initView();
    }
}