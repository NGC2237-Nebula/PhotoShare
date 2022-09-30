package com.example.photoshare.menu.person;

import static com.example.photoshare.constant.Constant_APP.SHARE_DELETE_POST_URL;
import static com.example.photoshare.constant.Constant_APP.SHARE_MYSELF_GET_URL;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.photoshare.Activity_Menu;
import com.example.photoshare.entity.Entity_Photo;
import com.example.photoshare.R;
import com.example.photoshare.parse.Request_Interceptor;
import com.example.photoshare.parse.Response_UserGeneral;
import com.example.photoshare.parse.Response_PhotoList;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Fragment_PersonListMyself extends Fragment {

    /* 数据 */
    private Context context;
    private String userId = null;
    private String shareId = null;
    private int deleteItemPosition;
    private List<Entity_Photo> photoList;

    /* 控件 */
    private ImageView ivBack;
    private ListView lvPhotoList;
    private TextView tvHint;
    private Adapter_Updata PhotoAdapter;

    /* 标识符 */
    /**
     * 网络请求 获取我的分享图片
     */
    private final int NET_GET_MYSELF_PHOTO = 1;
    /**
     * 网络请求 删除我的分享图片
     */
    private final int NET_DELETE_MYSELF_PHOTO = 2;



    /* 网络请求 */
    /**
     * 网络请求
     */
    private void networkRequest(int type) {
        if (type == NET_GET_MYSELF_PHOTO) {
            new Thread(() -> {
                Request request;
                String urlParam = "?" + "userId=" + userId;
                request = new Request.Builder().url(SHARE_MYSELF_GET_URL + urlParam).get().build();
                try {
                    Request_Interceptor requestInterceptor = new Request_Interceptor();
                    OkHttpClient client = new OkHttpClient.Builder().addInterceptor(requestInterceptor).build();
                    client.newCall(request).enqueue(showMyselfListCallback);
                } catch (NetworkOnMainThreadException ex) {
                    ex.printStackTrace();
                }
            }).start();
        } else if (type == NET_DELETE_MYSELF_PHOTO) {
            new Thread(() -> {
                Request request;
                String urlParam = "?" + "shareId=" + shareId + "&userId=" + userId;
                RequestBody requestBody = new FormBody.Builder().build();
                request = new Request.Builder().url(SHARE_DELETE_POST_URL + urlParam).post(requestBody).build();
                try {
                    OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Request_Interceptor()).build();
                    client.newCall(request).enqueue(deleteMyselfListCallback);
                } catch (NetworkOnMainThreadException ex) {
                    ex.printStackTrace();
                }
            }).start();
        }
    }


    /**
     * 回调 获取我的动态图片分享列表
     */
    private final okhttp3.Callback showMyselfListCallback = new okhttp3.Callback() {
        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            if (response.isSuccessful()) {
                String body = response.body().string();
                Log.d("LOG - 获取我的动态图片分享列表", "响应体 : " + body);
                new Thread(() -> {
                    Gson gson = new Gson();
                    Response_PhotoList responseParse = gson.fromJson(body, Response_PhotoList.class);
                    if(responseParse.getData() != null) {
                        ArrayList<Entity_Photo> photoList = responseParse.getData().getRecords();
                        for (Entity_Photo photo : photoList) {
                            Message message = new Message();
                            message.obj = photo;
                            showUpdataPhotoHandler.sendMessage(message);
                        }
                    } else hintHandler.sendMessage(new Message());
                }).start();
            }
        }
        @Override
        public void onFailure(@NonNull Call call, IOException e) {
            e.printStackTrace();
        }
    };
    /**
     * 操作响应 获取我的动态图片分享列表
     */
    private final Handler showUpdataPhotoHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            PhotoAdapter.add((Entity_Photo) msg.obj);
            PhotoAdapter.notifyDataSetChanged();
        }
    };
    /**
     * 操作响应 设置提示
     */
    private final Handler hintHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            tvHint.setVisibility(View.VISIBLE);
        }
    };


    /**
     * 回调 删除图文分享
     */
    private final okhttp3.Callback deleteMyselfListCallback = new okhttp3.Callback() {
        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            if (response.isSuccessful()) {
                String body = response.body().string();
                Log.d("LOG - 删除图文分享", "响应体 : " + body);
                new Thread(() -> {
                    Gson gson = new Gson();
                    Response_UserGeneral responseParse = gson.fromJson(body, Response_UserGeneral.class);

                    Message message = new Message();
                    if(responseParse.getMsg() == null) message.arg1 = 1;
                    else message.arg1 = -1;
                    deleteUpdataPhotoHandler.sendMessage(message);
                }).start();
            }
        }
        @Override
        public void onFailure(@NonNull Call call, IOException e) {
            e.printStackTrace();
        }
    };
    /**
     * 操作响应 获取我的动态图片分享列表
     */
    private final Handler deleteUpdataPhotoHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == 1) {
                photoList.remove(deleteItemPosition);
                PhotoAdapter.notifyDataSetChanged();
                Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
            }
        }
    };



    /* 监听器 */
    /**
     * 返回按钮 监听器
     */
    private final View.OnClickListener ivBackListener = v ->
            Navigation.findNavController(requireView()).popBackStack();
    /**
     * 长按删除列表项 监听器
     */
    private final AdapterView.OnItemLongClickListener itemDeleteListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
            // 确认删除对话框
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("确定删除该分享?");

            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteItemPosition = position;
                    shareId = photoList.get(position).getId();
                    networkRequest(NET_DELETE_MYSELF_PHOTO);
                }
            });

            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            builder.create().show();
            return false;
        }
    };


    /**
     * 初始化 视图
     * @param root 根视图
     */
    private void bindView(View root){
        lvPhotoList = root.findViewById(R.id.lv_list_myself_list);
        ivBack = root.findViewById(R.id.iv_list_myself_back);
        tvHint = root.findViewById(R.id.tv_list_list_myself_hint);
        ivBack.setOnClickListener(ivBackListener);
        tvHint.setVisibility(View.INVISIBLE);
    }

    /**
     * 初始化 数据
     */
    private void setDatase(){
        photoList = new ArrayList<>();
        PhotoAdapter = new Adapter_Updata(context, R.layout.item_person_list_myself, photoList);
        lvPhotoList.setAdapter(PhotoAdapter);
        lvPhotoList.setOnItemLongClickListener(itemDeleteListener);
        networkRequest(NET_GET_MYSELF_PHOTO);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        userId = ((Activity_Menu) context).getUserId();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_person_list_myself, container, false);
        context = getContext();
        bindView(root);
        setDatase();
        return root;
    }
}