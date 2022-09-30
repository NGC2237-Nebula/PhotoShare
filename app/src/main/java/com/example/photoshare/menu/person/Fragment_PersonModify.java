package com.example.photoshare.menu.person;

import static com.example.photoshare.constant.Constant_APP.MAN;
import static com.example.photoshare.constant.Constant_APP.SECRET;
import static com.example.photoshare.constant.Constant_APP.WOMAN;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.photoshare.constant.Constant_APP;
import com.example.photoshare.Activity_Menu;
import com.example.photoshare.entity.Entity_User;
import com.example.photoshare.interfaces.Interface_MessageSend;
import com.example.photoshare.R;
import com.example.photoshare.parse.Request_Interceptor;
import com.example.photoshare.parse.Response_InternalServerError;
import com.example.photoshare.parse.Response_UserGeneral;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class Fragment_PersonModify extends Fragment {

    Context context;

    /* 控件 */
    private EditText etName;
    private EditText etSign;
    private TextView tvSex;
    private Button btSave;

    /* 数据 */
    private String password = null;
    private String username = null;
    private String userAvatar = null;
    private String modifyAvatar = null;
    private String userId = null;

    private int userSexFlag = SECRET;
    private final String[] sexArray = {"男","女","保密"};

    private JSONObject jsonObject = null;

    /* 接口 */
    private Interface_MessageSend interface_messageSend;

    /* 监听器 */
    /**
     * 确认修改按钮 监听器 - 点击后将 修改信息 打包进行网络请求
     */
    private final View.OnClickListener btSaveListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String userName = etName.getText().toString();
            String userSign = etSign.getText().toString();
            setData();
            if (modifyAvatar != null) {
                if(!modifyAvatar.equals("")) {
                    try {
                        jsonObject.put("avatar", modifyAvatar);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (!userName.equals("")) {
                try {
                    jsonObject.put("username", userName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (!userSign.equals("")) {
                try {
                    jsonObject.put("introduce", userSign);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (userSexFlag == MAN) {
                try {
                    jsonObject.put("sex", MAN);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (userSexFlag == WOMAN){
                try {
                    jsonObject.put("sex", WOMAN);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                try {
                    jsonObject.put("sex", SECRET);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            networkRequest(NET_MODIFY);

        }
    };
    /**
     * 性别按钮 监听器 - 判断性别
     */
    private final View.OnClickListener rlSexListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etName.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(etSign.getWindowToken(), 0);

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("请选择性别");
            builder.setSingleChoiceItems(sexArray, userSexFlag - 1, (dialog, which) -> {
                tvSex.setText(sexArray[which]);
                userSexFlag = which + 1;
                dialog.dismiss();
            });
            builder.show();
        }
    };
    /**
     * 返回按钮 监听器 - 返回个人信息页
     */
    private final View.OnClickListener ivBackListener = v ->
            Navigation.findNavController(requireView()).popBackStack();
    /**
     * 头像按钮 监听器 - 选择头像
     */
    private final View.OnClickListener rlAvatarListener = v ->
            Navigation.findNavController(requireView()).navigate(R.id.action_fragment_PersonModify_to_fragment_PersonAvatarModify);




    /* 网络请求 */
    private final int NET_MODIFY = 1;
    private final int NET_REFRESH = 2;

    private final int HANDLER_MODIFY_SUCCESS = 1;
    private final int HANDLER_MODIFY_BAD_REQUEST = 2;
    private final int HANDLER_MODIFY_TYPE_ERROR = 3;
    private final int HANDLER_MODIFY_INTERNET_ERROR = 4;
    private final int HANDLER_REFRESH_SUCCESS = 1;

    /**
     * 网络请求
     */
    private void networkRequest(int type) {
        Log.d("LOG", "======== 网络请求 ========");
        if(type == NET_MODIFY) {
            new Thread(() -> {
                try {
                    RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), jsonObject.toString());
                    Request request = new Request.Builder().url(Constant_APP.USER_UPDATE_POST_URL).post(requestBody).build();

                    OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Request_Interceptor()).build();
                    client.newCall(request).enqueue(modifyCallback);
                } catch (NetworkOnMainThreadException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        else if(type == NET_REFRESH){
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
    }

    /**
     * 修改信息 回调
     */
    private final okhttp3.Callback modifyCallback = new okhttp3.Callback() {
        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            ResponseBody responseBody = response.body();
            String body = responseBody.string();

            Log.d("LOG - 信息修改", "响应体 : " + body);

            if (response.isSuccessful()) {
                Message message = new Message();
                message.arg1 = HANDLER_MODIFY_SUCCESS;
                modifyHandler.sendMessage(message);
            } else {
                new Thread(() -> {
                    Gson gson = new Gson();
                    Response_InternalServerError responseParse = gson.fromJson(body, Response_InternalServerError.class);
                    Message message = new Message();

                    if (responseParse.getError().equals("Bad Request"))
                        message.arg1 = HANDLER_MODIFY_BAD_REQUEST;
                    else if (responseParse.getError().equals("Internal Server Error"))
                        message.arg1 = HANDLER_MODIFY_TYPE_ERROR;
                    else
                        message.arg1 = HANDLER_MODIFY_INTERNET_ERROR;
                    modifyHandler.sendMessage(message);
                }).start();
            }
        }
        @Override
        public void onFailure(@NonNull Call call, IOException e) {
            e.printStackTrace();
        }
    };
    /**
     * 修改信息 操作响应
     */
    private final Handler modifyHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
                if(msg.arg1 == HANDLER_MODIFY_SUCCESS) {
                    networkRequest(NET_REFRESH);
                }
                else if(msg.arg1 == HANDLER_MODIFY_BAD_REQUEST) Toast.makeText(context, "输入类型错误", Toast.LENGTH_SHORT).show();
                else if(msg.arg1 == HANDLER_MODIFY_TYPE_ERROR) Toast.makeText(context, "个性签名暂时支持英文", Toast.LENGTH_SHORT).show();
                else if(msg.arg1 == HANDLER_MODIFY_INTERNET_ERROR) Toast.makeText(context, "网络请求异常错误", Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 刷新信息 回调
     */
    private final okhttp3.Callback refreshCallback = new okhttp3.Callback() {
        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            ResponseBody responseBody = response.body();
            String body = responseBody.string();

            Log.d("LOG - 个人信息刷新", "响应体 : " + body);

            if (response.isSuccessful()) {
                new Thread(() -> {
                    Gson gson = new Gson();
                    Response_UserGeneral responseParse = gson.fromJson(body, Response_UserGeneral.class);

                    Message message = new Message();
                    if ("登录成功".equals(responseParse.getMsg())) {
                        message.obj = responseParse.getUser();
                        message.what = HANDLER_REFRESH_SUCCESS;
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
     * 刷新信息 操作响应
     */
    private final Handler refreshHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            if (msg.what == HANDLER_REFRESH_SUCCESS) {
                interface_messageSend.sendUser((Entity_User) msg.obj);
                Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).popBackStack();
            }
        }
    };


    /**
     * 获取数据
     */
    private void getData(Context context){
        Entity_User user = ((Activity_Menu) context).getUser();
        password = ((Activity_Menu) context).getPassword();
        username = ((Activity_Menu) context).getUsername();
        modifyAvatar = ((Activity_Menu) context).getModifyAvatar();

        userId = user.getId();
        userAvatar = user.getAvatar();
        userSexFlag = user.getSex();
    }
    /**
     * 初始化视图
     */
    private void bindView(View root){
        ImageView ivBack = root.findViewById(R.id.iv_person_modify_arrow_back);
        ivBack.setOnClickListener(ivBackListener);

        ImageView ivAvatar = root.findViewById(R.id.iv_person_modify_avatar);
        if(modifyAvatar != null) Glide.with(requireContext()).load(modifyAvatar).into(ivAvatar);
        else if(userAvatar != null) Glide.with(requireContext()).load(userAvatar).into(ivAvatar);

        RelativeLayout rlAvatar = root.findViewById(R.id.rl_person_modify_avatar);
        rlAvatar.setOnClickListener(rlAvatarListener);

        etName = root.findViewById(R.id.et_person_modify_name);
        etSign = root.findViewById(R.id.et_person_modify_sign);

        tvSex = root.findViewById(R.id.sw_person_modify_sex);
        if(userSexFlag == MAN) tvSex.setText("男");
        else if(userSexFlag == WOMAN) tvSex.setText("女");
        else tvSex.setText("保密");

        RelativeLayout rlSex = root.findViewById(R.id.rl_person_modify_sex);
        rlSex.setOnClickListener(rlSexListener);

        btSave = root.findViewById(R.id.bt_person_modify_confirm);
        btSave.setOnClickListener(btSaveListener);
    }
    /**
     * 初始化数据
     */
    private void setData(){
        jsonObject = new JSONObject();
        if (userId == null) {
            Toast.makeText(context, "初始化错误", Toast.LENGTH_SHORT).show();
            btSave.setClickable(false);
        }
        else {
            try {
                jsonObject.put("id", userId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_person_modify, container, false);
        context = getActivity();
        getData(context);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        modifyAvatar = null;
    }
}