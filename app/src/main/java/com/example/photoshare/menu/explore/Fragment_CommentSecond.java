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
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.photoshare.constant.Constant_APP;
import com.example.photoshare.Activity_Menu;
import com.example.photoshare.entity.Entity_Comment;
import com.example.photoshare.entity.Entity_Photo;
import com.example.photoshare.interfaces.Interface_MessageSend;
import com.example.photoshare.R;
import com.example.photoshare.parse.Request_Interceptor;
import com.example.photoshare.parse.Response_CommentList;
import com.example.photoshare.parse.Response_UserGeneral;
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

public class Fragment_CommentSecond extends Fragment {

    /* 数据 */
    private Entity_Comment firstComment;
    private Entity_Photo photo;
    private String username;
    private String userId;
    private String content;

    private StringBuffer secondCommentSend = null;
    private StringBuffer secondCommentGet = null;

    private Adapter_CommentSecond secondCommentAdapter;

    /* 接口 */
    private Interface_MessageSend interface_messageSend;

    /* 控件 */
    private CardView cvIsHost;
    private ImageView ivBack;
    private TextView tvFirstName;
    private TextView tvFirstContent;
    private TextView tvFirstTime;
    private TextView tvHint;
    private TextView tvSecondNum;
    private ListView lvSecondComment;
    private EditText etWrite;
    private Button btSend;
    private SwipeRefreshLayout swipe;

    /* 标识符 */
    private final int NET_ADD_SECOND_COMMENT = 1;
    private final int NET_GET_SECOND_COMMENT = 2;

    private final int HANDLE_REQUEST_SUCCESS = 1;
    private final int HANDLE_REQUEST_FAIL = 2;
    private final int HANDLE_NO_ENOUGH_TIMES = 3;
    private final int HANDLER_NO_COMMENT = 4;
    private final int HANDLE_UNKNOWN_ERROR = 5;


    /* 网络请求 */
    /**
     * 网络请求 获得二级评论、新增二级评论
     */
    private void networkRequest(int type) {

        Log.d("LOG", "======== 网络请求 ========");

        if (type == NET_GET_SECOND_COMMENT) {
            new Thread(() -> {
                Request request;
                String urlParam = "?" + "commentId=" + firstComment.getId() + "&shareId=" + photo.getId();
                request = new Request.Builder().url(Constant_APP.COMMENT_SECOND_GET_URL + urlParam).get().build();
                try {
                    Request_Interceptor requestInterceptor = new Request_Interceptor();
                    OkHttpClient client = new OkHttpClient.Builder().addInterceptor(requestInterceptor).build();
                    client.newCall(request).enqueue(getSecondCommentCallback);
                } catch (NetworkOnMainThreadException ex) {
                    ex.printStackTrace();
                }
            }).start();
        }
        if (type == NET_ADD_SECOND_COMMENT) {
            new Thread(() -> {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("content", content);
                    jsonObject.put("parentCommentId", firstComment.getId());
                    jsonObject.put("parentCommentUserId", firstComment.getPUserId());
                    jsonObject.put("replyCommentId", firstComment.getId());
                    jsonObject.put("replyCommentUserId", firstComment.getPUserId());
                    jsonObject.put("shareId", photo.getId());
                    jsonObject.put("userId", userId);
                    jsonObject.put("userName", username);

                    RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), jsonObject.toString());
                    Request request = new Request.Builder().url(Constant_APP.COMMENT_SECOND_POST_URL).post(requestBody).build();

                    OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Request_Interceptor()).build();
                    client.newCall(request).enqueue(addSecondCommentCallback);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    /**
     * 回调 获得二级评论
     */
    private final okhttp3.Callback getSecondCommentCallback = new okhttp3.Callback() {
        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                Log.d("LOG - 获取二级评论", "响应体 : " + responseBody);
                addAdapterData(responseBody);


                if(secondCommentSend == null) secondCommentSend = new StringBuffer(responseBody);
                else secondCommentSend.replace(0, secondCommentSend.length(),responseBody);
                interface_messageSend.sendSecondCommentList(secondCommentSend,photo.getId(),firstComment.getId());
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
            Gson gson = new Gson();
            Response_CommentList responseParse = gson.fromJson(body, Response_CommentList.class);
            if (responseParse.getCode() == 5311) {
                Message message = new Message();
                message.arg1 = HANDLE_NO_ENOUGH_TIMES;
                setHintHandler.sendMessage(message);
            } else if (responseParse.getCode() == 200) {
                if (responseParse.getData() == null) {
                    Message message = new Message();
                    message.arg1 = HANDLER_NO_COMMENT;
                    setHintHandler.sendMessage(message);
                } else {
                    ArrayList<Entity_Comment> commentList = responseParse.getData().getRecords();

                    Message setNumMessage = new Message();
                    setNumMessage.arg1 = commentList.size();
                    setNumHandler.sendMessage(setNumMessage);

                    for (Entity_Comment comment : commentList) {
                        comment.setIsHost(comment.getUsername().equals(username));
                        Message addCommentMessage = new Message();
                        addCommentMessage.obj = comment;
                        addCommentHandler.sendMessage(addCommentMessage);
                    }
                }
            } else if (responseParse.getCode() == 500) {
                Message message = new Message();
                message.arg1 = HANDLE_REQUEST_FAIL;
                setHintHandler.sendMessage(message);
            }
        }).start();
    }
    /**
     * 响应操作 - 设置评论数
     */
    private final Handler setNumHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            String commentNumString = "回复 " + msg.arg1;
            tvSecondNum.setText(commentNumString);
        }
    };
    /**
     * 响应操作 - 将评论添加到适配器中
     */
    private final Handler addCommentHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            secondCommentAdapter.add((Entity_Comment) msg.obj);
            secondCommentAdapter.notifyDataSetChanged();
        }
    };
    /**
     * 响应操作 - 设置 提示
     */
    private final Handler setHintHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if(msg.arg1 == HANDLER_NO_COMMENT) tvHint.setText("暂无评论");
            else if(msg.arg1 == HANDLE_NO_ENOUGH_TIMES) tvHint.setText("接口每天免费使用次数已用尽");
            else if(msg.arg1 == HANDLE_REQUEST_FAIL) tvHint.setText("网络错误");
        }
    };




    /**
     * 回调 添加二级评论
     */
    private final okhttp3.Callback addSecondCommentCallback = new okhttp3.Callback() {
        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();

                Log.d("LOG - 添加二级评论", "响应体 : " + responseBody);

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
     * 响应操作 - 添加二级评论
     */
    private final Handler addFirstCommentHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == HANDLE_REQUEST_SUCCESS) {
                Toast.makeText(getContext(), "回复成功", Toast.LENGTH_SHORT).show();
                etWrite.setText("");
                if(tvHint.getText().toString().equals("暂无评论")) tvHint.setText("");
                secondCommentAdapter.clear();
                networkRequest(NET_GET_SECOND_COMMENT);
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
     * 返回一级评论区
     */
    private final View.OnClickListener ivBackListener = v ->
            Navigation.findNavController(requireView()).popBackStack();
    /**
     * 发送输入框的内容
     */
    private final View.OnClickListener btSendListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            content = etWrite.getText().toString();
            if (content.equals(""))
                Toast.makeText(getContext(), "内容不能为空", Toast.LENGTH_SHORT).show();
            else networkRequest(NET_ADD_SECOND_COMMENT);
            hideKeyboard(v);
        }
    };
    /**
     * 刷新 评论区
     */
    private final SwipeRefreshLayout.OnRefreshListener swipeListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if(!secondCommentAdapter.isEmpty()) secondCommentAdapter.clear();
            networkRequest(NET_GET_SECOND_COMMENT);
            swipe.setRefreshing(false);
        }
    };





    /**
     * 隐藏输入框
     */
    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    private void bindView(View root) {
        ivBack = root.findViewById(R.id.iv_photo_comment_second_back);

        cvIsHost = root.findViewById(R.id.cv_comment_first_host);
        tvFirstName = root.findViewById(R.id.tv_comment_first_username);
        tvFirstContent = root.findViewById(R.id.tv_comment_first_context);
        tvFirstTime = root.findViewById(R.id.tv_comment_first_create_time);

        tvSecondNum = root.findViewById(R.id.tv_photo_comment_second_num);
        tvHint = root.findViewById(R.id.tv_photo_comment_second_hint);
        lvSecondComment = root.findViewById(R.id.lv_photo_comment_second_list);

        etWrite = root.findViewById(R.id.et_photo_comment_second_write);
        btSend = root.findViewById(R.id.bt_photo_comment_second_send);
        swipe = root.findViewById(R.id.sw_photo_comment_second_swipe);
    }

    private void setData(Context context) {
        if(!firstComment.getIsHost()) cvIsHost.setVisibility(View.INVISIBLE);
        tvFirstName.setText(firstComment.getUsername());
        tvFirstContent.setText(firstComment.getContent());
        tvFirstTime.setText(firstComment.getCreateTime());

        ivBack.setOnClickListener(ivBackListener);
        btSend.setOnClickListener(btSendListener);
        swipe.setOnRefreshListener(swipeListener);

        List<Entity_Comment> commentList = new ArrayList<>();
        secondCommentAdapter = new Adapter_CommentSecond(context, R.layout.item_comment_second, commentList);
        lvSecondComment.setAdapter(secondCommentAdapter);

        if(secondCommentGet != null){
            String responseBody = secondCommentGet.toString();
            addAdapterData(responseBody);
        } else networkRequest(NET_GET_SECOND_COMMENT);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_photo_comment_second, container, false);
        Context context = getContext();
        bindView(root);
        setData(context);
        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        firstComment = ((Activity_Menu) context).getClickFirstComment();
        photo = ((Activity_Menu) context).getClickPhoto();
        secondCommentGet = ((Activity_Menu) context).getSecondCommentList(photo.getId(),firstComment.getId());
        username = ((Activity_Menu) context).getUsername();
        userId = ((Activity_Menu) context).getUserId();

        try {
            interface_messageSend = (Interface_MessageSend) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Interface not implemented");
        }
    }
}