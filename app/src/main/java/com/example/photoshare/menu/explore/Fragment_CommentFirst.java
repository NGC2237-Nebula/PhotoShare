package com.example.photoshare.menu.explore;

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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.photoshare.constant.Constant_APP;
import com.example.photoshare.Activity_Menu;
import com.example.photoshare.entity.Entity_Comment;
import com.example.photoshare.entity.Entity_Photo;
import com.example.photoshare.interfaces.Interface_ClickViewSend;
import com.example.photoshare.interfaces.Interface_MessageSend;
import com.example.photoshare.R;
import com.example.photoshare.parse.Request_Interceptor;
import com.example.photoshare.parse.Response_CommentList;
import com.example.photoshare.parse.Response_UserGeneral;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Fragment_CommentFirst extends Fragment {

    private Context context;

    /* 接口 */
    private Interface_MessageSend interface_messageSend;

    /* 数据 */
    private String username;
    private String userId;
    private String content;
    private Entity_Photo photo;
    private Adapter_CommentFirst firstCommentAdapter;

    private StringBuffer firstCommentSend = null;
    private StringBuffer firstCommentGet = null;




    /* 控件 */
    private ListView lvComment;
    private TextView tvHint;
    private TextView tvNum;
    private EditText etWrite;
    private Button btSend;
    private ImageView ivBack;
    private SwipeRefreshLayout swipe;
    private BottomNavigationView nav;


    /* 标识符 */
    private final int NET_ADD_FIRST_COMMENT = 1;
    private final int NET_GET_FIRST_COMMENT = 2;

    private final int HANDLE_REQUEST_SUCCESS = 1;
    private final int HANDLE_REQUEST_FAIL = 2;
    private final int HANDLE_NO_ENOUGH_TIMES = 3;
    private final int HANDLE_NO_COMMENT = 4;
    private final int HANDLE_UNKNOWN_ERROR = 5;


    /* 网络请求 */
    /**
     * 网络请求 获得一级评论、新增一级评论
     */
    private void networkRequest(int type) {
        Log.d("LOG", "======== 网络请求 ========");
        if (type == NET_GET_FIRST_COMMENT) {
            new Thread(() -> {
                Request request;
                String urlParam = "?" + "shareId=" + photo.getId();
                request = new Request.Builder().url(Constant_APP.COMMENT_FIRST_GET_URL + urlParam).get().build();
                try {
                    OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Request_Interceptor()).build();
                    client.newCall(request).enqueue(getFirstCommentCallback);
                } catch (NetworkOnMainThreadException ex) {
                    ex.printStackTrace();
                }
            }).start();
        } else if (type == NET_ADD_FIRST_COMMENT) {
            new Thread(() -> {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("content", content);
                    jsonObject.put("shareId", photo.getId());
                    jsonObject.put("userId", userId);
                    jsonObject.put("userName", username);

                    RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), jsonObject.toString());
                    Request request = new Request.Builder()
                            .url(Constant_APP.COMMENT_FIRST_POST_URL)
                            .post(requestBody)
                            .build();

                    OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Request_Interceptor()).build();
                    client.newCall(request).enqueue(addFirstCommentCallback);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }


    /**
     * 回调 获得一级评论
     */
    private final okhttp3.Callback getFirstCommentCallback = new okhttp3.Callback() {
        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                Log.d("LOG - 获取一级评论", "响应体 : " + responseBody);
                addAdapterData(responseBody);

                if(firstCommentSend == null) firstCommentSend = new StringBuffer(responseBody);
                else firstCommentSend.replace(0, firstCommentSend.length(),responseBody);
                interface_messageSend.sendFirstCommentList(firstCommentSend,photo.getId());
            }
        }

        @Override
        public void onFailure(@NonNull Call call, IOException e) {
            e.printStackTrace();
        }
    };
    /**
     * 解析网络请求结果，添加到适配器中
     *
     * @param body 网络请求结果
     */
    private void addAdapterData(String body) {
        new Thread(() -> {
            Response_CommentList responseParse = new Gson().fromJson(body, Response_CommentList.class);
            if (responseParse.getCode() == 5311) {
                Message message = new Message();
                message.arg1 = HANDLE_NO_ENOUGH_TIMES;
                setHintHandler.sendMessage(message);
            }
            else if(responseParse.getCode() == 200){
                ArrayList<Entity_Comment> commentList = responseParse.getData().getRecords();
                Message setNumMessage = new Message();
                setNumMessage.arg1 = commentList.size();
                setNumHandler.sendMessage(setNumMessage);

                if (commentList.size() != 0) {
                    for (Entity_Comment comment : commentList) {
                        comment.setIsHost(photo.getUsername().equals(username));
                        Message addCommentMessage = new Message();
                        addCommentMessage.obj = comment;
                        addCommentHandler.sendMessage(addCommentMessage);
                    }
                } else {
                    Message message = new Message();
                    message.arg1 = HANDLE_NO_COMMENT;
                    setHintHandler.sendMessage(message);
                }
            }
            else {
                Message message = new Message();
                message.arg1 = HANDLE_UNKNOWN_ERROR;
                setHintHandler.sendMessage(message);
            }
        }).start();
    }
    /**
     * 响应操作 - 设置评论数
     */
    private final Handler setNumHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            String commentNumString = "评论 " + msg.arg1;
            tvNum.setText(commentNumString);
        }
    };
    /**
     * 响应操作 - 将评论添加到适配器中
     */
    private final Handler addCommentHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            firstCommentAdapter.add((Entity_Comment) msg.obj);
            firstCommentAdapter.notifyDataSetChanged();
        }
    };
    /**
     * 响应操作 - 设置 提示
     */
    private final Handler setHintHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            if(msg.arg1 == HANDLE_NO_COMMENT) tvHint.setText("暂无评论");
            else if(msg.arg1 == HANDLE_NO_ENOUGH_TIMES) tvHint.setText("接口每天免费使用次数已用尽");
            else if(msg.arg1 == HANDLE_UNKNOWN_ERROR) tvHint.setText("网络错误");
        }
    };


    /**
     * 回调 添加一级评论
     */
    private final okhttp3.Callback addFirstCommentCallback = new okhttp3.Callback() {
        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();

                Log.d("LOG - 添加一级评论", "响应体 : " + responseBody);

                new Thread(() -> {
                    Gson gson = new Gson();
                    Response_UserGeneral responseParse = gson.fromJson(responseBody, Response_UserGeneral.class);
                    Message message = new Message();

                    if (responseParse.getCode() == 200) {
                        message.arg1 = HANDLE_REQUEST_SUCCESS;
                    } else if (responseParse.getCode() == 500) {
                        message.arg1 = HANDLE_REQUEST_FAIL;
                    } else if (responseParse.getCode() == 5311) {
                        message.arg1 = HANDLE_NO_ENOUGH_TIMES;
                    } else {
                        message.arg1 = HANDLE_UNKNOWN_ERROR;
                    }
                    addFirstCommentHandler.sendMessage(message);
                }).start();
            }
        }
        @Override
        public void onFailure(@NonNull Call call, IOException e) {
            e.printStackTrace();
        }
    };
    /**
     * 响应操作 - 添加一级评论
     */
    private final Handler addFirstCommentHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == HANDLE_REQUEST_SUCCESS) {
                Toast.makeText(getContext(), "发布成功", Toast.LENGTH_SHORT).show();
                firstCommentAdapter.clear();
                networkRequest(NET_GET_FIRST_COMMENT);
                if(tvHint.getText().toString().equals("暂无评论")) tvHint.setText("");
                etWrite.setText("");
            } else if (msg.arg1 == HANDLE_REQUEST_FAIL) {
                Toast.makeText(getContext(), "网络请求失败,请重试", Toast.LENGTH_SHORT).show();
            } else if (msg.arg1 == HANDLE_NO_ENOUGH_TIMES) {
                Toast.makeText(getContext(), "接口使用次数不够", Toast.LENGTH_SHORT).show();
            } else if (msg.arg1 == HANDLE_UNKNOWN_ERROR) {
                Toast.makeText(getContext(), "未知错误,请重试", Toast.LENGTH_SHORT).show();
            }
        }
    };







    /* 监听器 */
    /**
     * 发送输入框的内容
     */
    private final View.OnClickListener btSendListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            content = etWrite.getText().toString();
            if (content.equals(""))
                Toast.makeText(getContext(), "内容不能为空", Toast.LENGTH_SHORT).show();
            else networkRequest(NET_ADD_FIRST_COMMENT);
            hideKeyboard(v);
        }
    };

    /**
     * 返回 图片详情页
     */
    private final View.OnClickListener ivBackListener = v ->
            Navigation.findNavController(requireView()).popBackStack();

    /**
     * 获取被点击的列表项上的 ”查看更多“ 控件,设置点击事件,传递数据
     */
    private final Interface_ClickViewSend rlMoreListener = new Interface_ClickViewSend() {
        @Override
        public void onItemClick(View view, int position) {
            interface_messageSend.sendClickFirstComment(firstCommentAdapter.getItem(position));
            nav.setVisibility(View.INVISIBLE);
            Navigation.findNavController(requireView()).navigate(R.id.action_fragment_CommentFirst_to_fragment_CommentSecond);
        }
    };

    /**
     * 刷新 评论区
     */
    private final SwipeRefreshLayout.OnRefreshListener swipeListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if(!firstCommentAdapter.isEmpty()) firstCommentAdapter.clear();
            networkRequest(NET_GET_FIRST_COMMENT);
            swipe.setRefreshing(false);
        }
    };




    /**
     * 初始化视图
     * @param root 根视图
     */
    private void bindView(View root) {
        nav = requireActivity().findViewById(R.id.nav_view);
        nav.setVisibility(View.INVISIBLE);

        lvComment = root.findViewById(R.id.lv_photo_comment_first_list);
        tvHint = root.findViewById(R.id.tv_photo_comment_first_hint);
        tvNum = root.findViewById(R.id.tv_photo_comment_first_num);
        etWrite = root.findViewById(R.id.et_photo_comment_first_write);
        btSend = root.findViewById(R.id.bt_photo_comment_first_send);
        ivBack = root.findViewById(R.id.iv_photo_comment_first_back);
        swipe = root.findViewById(R.id.sw_photo_comment_first_swipe);
    }

    /**
     * 初始化数据
     */
    private void setData() {
        btSend.setOnClickListener(btSendListener);
        ivBack.setOnClickListener(ivBackListener);
        swipe.setOnRefreshListener(swipeListener);

        List<Entity_Comment> commentList = new ArrayList<>();
        firstCommentAdapter = new Adapter_CommentFirst(context, R.layout.item_comment_first, commentList);
        firstCommentAdapter.setOnCommentMoreClick(rlMoreListener);
        lvComment.setAdapter(firstCommentAdapter);

        firstCommentGet = ((Activity_Menu) context).getFirstCommentList(photo.getId());
        if(firstCommentGet != null) addAdapterData(firstCommentGet.toString());
        else networkRequest(NET_GET_FIRST_COMMENT);
    }

    /**
     * 隐藏输入框
     */
    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_photo_comment_first, container, false);
        context = getContext();
        bindView(root);
        setData();
        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        photo = ((Activity_Menu) context).getClickPhoto();
        username = ((Activity_Menu) context).getUsername();
        userId = ((Activity_Menu) context).getUserId();

        try {
            interface_messageSend = (Interface_MessageSend) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Interface not implemented");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        nav.setVisibility(View.VISIBLE);
    }
}