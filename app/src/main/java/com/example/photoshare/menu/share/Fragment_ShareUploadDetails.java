package com.example.photoshare.menu.share;

import static com.example.photoshare.constant.Constant_APP.FRIEND_CIRCLE_GET_ERL;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.NetworkOnMainThreadException;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.example.photoshare.constant.Constant_APP;
import com.example.photoshare.Activity_Menu;
import com.example.photoshare.entity.Entity_CopyWriting;
import com.example.photoshare.R;
import com.example.photoshare.parse.Request_Interceptor;
import com.example.photoshare.parse.Response_CopyWriting;
import com.example.photoshare.parse.Response_PhotoUpload;
import com.example.photoshare.customize.Customize_PageTransformer_ZoomOut;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Fragment_ShareUploadDetails extends Fragment {

    /* 控件 */
    private EditText etTitle;
    private EditText etContext;
    private ViewPager2 vpPhoto;
    private BottomNavigationView nav;

    /* 数据 */
    private String userId = null;
    private String imageCode = null;
    private String title;
    private String content;
    private ArrayList<File> imageFileList = null;
    private String copyWriting = null;

    /* 类型码 - handle */
    private final int HANDLER_SUCCESS = 1;
    private final int HANDLER_SERVER_ERROR = 2;
    private final int HANDLER_USE_UP = 3;

    private final int HANDLER_GET_SUCCESS = 4;
    private final int HANDLER_GET_FAIL = 5;

    /* 类型码 - netRequest */
    private final int NET_SHARE_ADD = 1;
    private final int NET_GET_COPY_WRITE = 2;


    /* 监听器 */
    /**
     * 返回上一级
     */
    private final View.OnClickListener ivCloseListener = v ->
            Navigation.findNavController(requireView()).popBackStack();
    /**
     * 请求获取一个朋友圈文案
     */
    private final View.OnClickListener cvGetListener = v -> {
        Toast.makeText(requireContext(), "获取文案中,请稍后", Toast.LENGTH_SHORT).show();
        networkRequest(NET_GET_COPY_WRITE);
    };
    /**
     * 请求上传图片分享
     */
    private final View.OnClickListener cvConfirmListener = v -> {
        title = etTitle.getText().toString();
        content = etContext.getText().toString();
        if (title.equals(""))
            Toast.makeText(requireContext(), "标题不能为空", Toast.LENGTH_SHORT).show();
        else if (content.equals(""))
            Toast.makeText(requireContext(), "内容不能为空", Toast.LENGTH_SHORT).show();
        else if (imageCode.equals("") || userId.equals(""))
            Toast.makeText(requireContext(), "内部错误,请重试", Toast.LENGTH_SHORT).show();
        else networkRequest(NET_SHARE_ADD);
    };



    /* 网络请求 */

    /**
     * 网络请求 - 添加一个图文分享
     */
    private void networkRequest(int type) {
        if (type == NET_SHARE_ADD) {
            new Thread(() -> {
                try {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("content", content);
                        jsonObject.put("imageCode", imageCode);
                        jsonObject.put("pUserId", userId);
                        jsonObject.put("title", title);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), jsonObject.toString());
                    Request request = new Request.Builder()
                            .url(Constant_APP.SHARE_ADD_POST_URL)
                            .post(requestBody)
                            .build();

                    OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Request_Interceptor()).build();
                    client.newCall(request).enqueue(shareAddCallback);

                } catch (NetworkOnMainThreadException ex) {
                    ex.printStackTrace();
                }
            }).start();
        } else if (type == NET_GET_COPY_WRITE) {
            new Thread(() -> {
                String urlParams = "?key=" + Constant_APP.TIAN_APP_KET;
                Request request = new Request.Builder()
                        .url(FRIEND_CIRCLE_GET_ERL + urlParams)
                        .get()
                        .build();
                Log.d("LOG -  获取文案", "开始获取");
                try {
                    OkHttpClient client = new OkHttpClient();
                    client.newCall(request).enqueue(getCopyWritingCallback);
                } catch (NetworkOnMainThreadException ex) {
                    ex.printStackTrace();
                }
            }).start();
        }
    }

    /**
     * 回调 - 添加图文
     */
    private final okhttp3.Callback shareAddCallback = new okhttp3.Callback() {
        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            if (response.isSuccessful()) {
                String body = response.body().string();
                Log.d("LOG -  新增图文分享", "响应体 : " + body);

                new Thread(() -> {
                    Gson gson = new Gson();
                    Response_PhotoUpload responseParse = gson.fromJson(body, Response_PhotoUpload.class);
                    Message message = new Message();
                    if (responseParse.getCode() == 200) {
                        message.what = HANDLER_SUCCESS;
                    } else if (responseParse.getCode() == 500) {
                        message.what = HANDLER_SERVER_ERROR;
                    } else if (responseParse.getCode() == 5311) {
                        message.what = HANDLER_USE_UP;
                    }
                    shareAddHandler.sendMessage(message);
                }).start();
            }
        }

        @Override
        public void onFailure(@NonNull Call call, IOException e) {
            e.printStackTrace();
        }
    };

    /**
     * 回调 - 获取一个朋友圈文案
     */
    private final okhttp3.Callback getCopyWritingCallback = new okhttp3.Callback() {
        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            if (response.isSuccessful()) {
                String body = response.body().string();
                Log.d("LOG -  获取文案", "响应体 : " + body);
                new Thread(() -> {
                    Gson gson = new Gson();
                    Response_CopyWriting responseParse = gson.fromJson(body, Response_CopyWriting.class);
                    Message message = new Message();

                    if (responseParse.getCode() == 200) {
                        ArrayList<Entity_CopyWriting> copyWritingList = responseParse.getNewsList();
                        copyWriting = copyWritingList.get(0).getContent();
                        message.what = HANDLER_GET_SUCCESS;
                    } else {
                        message.what = HANDLER_GET_FAIL;
                    }
                    setCopyWritingHandler.sendMessage(message);
                }).start();
            }
        }

        @Override
        public void onFailure(@NonNull Call call, IOException e) {
            e.printStackTrace();
        }
    };


    /**
     * 操作响应 - 添加图文
     */
    private final Handler shareAddHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            if (msg.what == HANDLER_SUCCESS) {
                Toast.makeText(requireContext(), "成功新增图文分享,刷新查看", Toast.LENGTH_SHORT).show();
                nav.setVisibility(View.VISIBLE);
                Navigation.findNavController(requireView()).navigate(R.id.action_fragment_PhotoShareUploadDetails_to_navigation_photo_explore);
            } else if (msg.what == HANDLER_SERVER_ERROR) {
                Toast.makeText(requireContext(), "服务器内部错误,请重试", Toast.LENGTH_SHORT).show();
                nav.setVisibility(View.VISIBLE);
                Navigation.findNavController(requireView()).navigate(R.id.action_fragment_PhotoShareUploadDetails_to_navigation_photo_share);
            } else if (msg.what == HANDLER_USE_UP) {
                Toast.makeText(requireContext(), "接口使用次数不够,请次日上传", Toast.LENGTH_SHORT).show();
                nav.setVisibility(View.VISIBLE);
                Navigation.findNavController(requireView()).navigate(R.id.action_fragment_PhotoShareUploadDetails_to_navigation_photo_share);
            }
        }
    };

    /**
     * 操作响应 - 获取一个朋友圈文案
     */
    private final Handler setCopyWritingHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            if (msg.what == HANDLER_GET_SUCCESS) {
                etContext.setText(copyWriting);
                Toast.makeText(requireContext(), "获取成功", Toast.LENGTH_SHORT).show();
            } else if (msg.what == HANDLER_GET_FAIL) {
                Toast.makeText(requireContext(), "你这么懒,自己去写 Σ(っ °Д °;)っ", Toast.LENGTH_SHORT).show();
            }
        }
    };


    /**
     * 初始化 数据
     */
    private void setData() {
        Adapter_PhotoSlide viewPagerAdapter = new Adapter_PhotoSlide(getContext(), imageFileList);
        vpPhoto.setAdapter(viewPagerAdapter);
        vpPhoto.setPageTransformer(new Customize_PageTransformer_ZoomOut());
    }

    /**
     * 初始化 视图
     */
    private void bindView(View root) {
        nav = requireActivity().findViewById(R.id.nav_view);
        nav.setVisibility(View.INVISIBLE);

        /* 控件 */
        ImageView ivClose = root.findViewById(R.id.iv_photo_share_details_upload_close);
        ivClose.setOnClickListener(ivCloseListener);

        vpPhoto = root.findViewById(R.id.vp_photo_share_upload_details_viewPager);

        etTitle = root.findViewById(R.id.et_photo_share_upload_details_title);

        // 设置 输入内容自动换行
        etContext = root.findViewById(R.id.et_photo_share_upload_details_context);
        etContext.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE); // 设置EditText的显示方式为多行文本输入
        etContext.setSingleLine(false); // 改变默认的单行模式
        etContext.setHorizontallyScrolling(false); // 水平滚动设置为False

        CardView cvConfirm = root.findViewById(R.id.cv_photo_share_upload_details_confirm);
        cvConfirm.setOnClickListener(cvConfirmListener);

        CardView cvGet = root.findViewById(R.id.cv_photo_share_upload_details_get);
        cvGet.setOnClickListener(cvGetListener);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_photo_share_upload_details, container, false);
        bindView(root);
        setData();
        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        userId = ((Activity_Menu) context).getUserId();
        imageCode = ((Activity_Menu) context).getImageCode();
        imageFileList = ((Activity_Menu) context).getImageFileList();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}