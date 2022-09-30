package com.example.photoshare.menu.person;

import static com.example.photoshare.constant.Constant_APP.MAN;
import static com.example.photoshare.constant.Constant_APP.SECRET;
import static com.example.photoshare.constant.Constant_APP.WOMAN;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.photoshare.constant.Constant_APP;
import com.example.photoshare.Activity_Login;
import com.example.photoshare.Activity_Menu;
import com.example.photoshare.entity.Entity_User;
import com.example.photoshare.interfaces.Interface_MessageSend;
import com.example.photoshare.R;
import com.example.photoshare.parse.Request_Interceptor;
import com.example.photoshare.parse.Response_UserGeneral;
import com.example.photoshare.tool.Tool_SharedPreferencesManager;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Fragment_PersonInformation extends Fragment {

    /* 数据 */
    private Entity_User user = null;
    private String username = null;
    private String password = null;
    private String userMessage = null;

    /* 控件 */
    private ImageView ivAvatar;
    private TextView tvName;
    private TextView tvSex;
    private TextView tvId;
    private TextView tvSign;
    private SwipeRefreshLayout swipe;

    /* 接口 */
    private Interface_MessageSend interface_messageSend;

    /* 网络请求 */
    private final int HANDLER_SUCCESS = 1;
    private final int HANDLER_FAIL = 2;

    /**
     * 网络请求
     */
    private void networkRequest() {
        Log.d("LOG", "======== 网络请求 ========");
        new Thread(() -> {

            String urlParam = "?" + "&password=" + password + "&username=" + username;
            RequestBody requestBody = new FormBody.Builder().build();
            Request request = new Request.Builder().url(Constant_APP.USER_LOGIN_POST_URL + urlParam).post(requestBody).build();

            try {
                OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Request_Interceptor()).build();
                client.newCall(request).enqueue(refreshCallback);
            } catch (NetworkOnMainThreadException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    /**
     * 网络请求 回调
     */
    private final okhttp3.Callback refreshCallback = new okhttp3.Callback() {
        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            ResponseBody responseBody = response.body();
            userMessage = responseBody.string();
            Log.d("LOG - 个人信息刷新", "响应体 : " + userMessage);

            if (response.isSuccessful()) {
                new Thread(() -> {
                    Gson gson = new Gson();
                    Response_UserGeneral responseParse = gson.fromJson(userMessage, Response_UserGeneral.class);
                    user = responseParse.getUser();

                    Message message = new Message();
                    if ("登录成功".equals(responseParse.getMsg())) {
                        message.obj = user;
                        message.what = HANDLER_SUCCESS;
                    } else {
                        message.what = HANDLER_FAIL;
                    }
                    refreshHandler.sendMessage(message);
                }).start();
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
    private final Handler refreshHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            if (msg.what == HANDLER_SUCCESS) {
                interface_messageSend.sendUser((Entity_User) msg.obj);
                setData();
            }
            if (msg.what == HANDLER_FAIL)
                Toast.makeText(getContext(), "网络错误", Toast.LENGTH_SHORT).show();
        }
    };



    /* 监听器 */
    /**
     * 跳转到修改界面
     */
    private final View.OnClickListener jumpModifyListener = v ->
            Navigation.findNavController(requireView()).navigate(R.id.action_fragment_PersonDetails_to_fragment_PersonModify);
    /**
     * 跳转到个人主页
     */
    private final View.OnClickListener jumpHomeListener = v ->
            Navigation.findNavController(requireView()).popBackStack();
    /**
     * 跳转到登录界面
     */
    private final View.OnClickListener jumpLoginListener = v -> {
        Tool_SharedPreferencesManager manager = new Tool_SharedPreferencesManager(requireContext());
        manager.clearData();
        Intent intent = new Intent(getActivity(), Activity_Login.class);
        startActivity(intent);
    };
    /**
     * 刷新 用户信息
     */
    private final SwipeRefreshLayout.OnRefreshListener swipeListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            networkRequest();
            swipe.setRefreshing(false);
        }
    };


    /**
     * 初始化 数据
     */
    private void setData() {
        if (user == null)
            Toast.makeText(getActivity(), "用户信息初始化错误,请退出重试", Toast.LENGTH_SHORT).show();
        else {
            int userSexFlag = user.getSex();
            String userName = user.getUsername();
            String userAvatar = user.getAvatar();
            String userId = user.getId();
            String userSign = user.getIntroduce();

            if (userAvatar != null) Glide.with(this).load(userAvatar).into(ivAvatar);
            if (userName != null) tvName.setText(userName);
            if (userId != null) tvId.setText(userId);
            if (userSign != null) tvSign.setText(userSign);

            if (userSexFlag == MAN) tvSex.setText("男");
            else if (userSexFlag == WOMAN) tvSex.setText("女");
            else if (userSexFlag == SECRET) tvSex.setText("保密");
            else tvSex.setText("未填写");
        }
    }

    /**
     * 初始化 视图
     */
    private void bindView(View root) {
        // 返回按钮
        ImageView ivArrowReturn = root.findViewById(R.id.iv_person_information_back);
        ivArrowReturn.setOnClickListener(jumpHomeListener);
        // 刷新
        swipe = root.findViewById(R.id.sw_person_information_swipe);
        swipe.setOnRefreshListener(swipeListener);

        // 头像
        ivAvatar = root.findViewById(R.id.iv_person_information_avatar);
        RelativeLayout rlAvatar = root.findViewById(R.id.rl_person_information_avatar);
        rlAvatar.setOnClickListener(jumpModifyListener);
        // 昵称
        tvName = root.findViewById(R.id.tv_person_information_name);
        RelativeLayout rlName = root.findViewById(R.id.rl_person_information_name);
        rlName.setOnClickListener(jumpModifyListener);
        // 性别
        tvSex = root.findViewById(R.id.tv_person_information_sex);
        RelativeLayout rlSex = root.findViewById(R.id.rl_person_information_sex);
        rlSex.setOnClickListener(jumpModifyListener);
        // ID
        tvId = root.findViewById(R.id.tv_person_information_id);
        RelativeLayout rlId = root.findViewById(R.id.rl_person_information_id);
        rlId.setOnClickListener(jumpModifyListener);
        // 签名
        tvSign = root.findViewById(R.id.tv_person_information_sign);
        RelativeLayout rlSign = root.findViewById(R.id.rl_person_information_sign);
        rlSign.setOnClickListener(jumpModifyListener);

        // 退出登录按钮
        Button btQuit = root.findViewById(R.id.bt_person_information_quit);
        btQuit.setOnClickListener(jumpLoginListener);
    }

    /**
     * 获取 数据
     */
    private void getData(Context context) {
        user = ((Activity_Menu) context).getUser();
        username = ((Activity_Menu) context).getUsername();
        password = ((Activity_Menu) context).getPassword();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_person_information, container, false);

        getData(getActivity());
        bindView(root);
        setData();

        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            interface_messageSend = (Interface_MessageSend) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Interface not implemented");
        }
    }
}