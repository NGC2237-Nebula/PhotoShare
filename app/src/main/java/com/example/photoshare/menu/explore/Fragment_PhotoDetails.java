package com.example.photoshare.menu.explore;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.example.photoshare.constant.Constant_APP;
import com.example.photoshare.Activity_Menu;
import com.example.photoshare.entity.Entity_Photo;
import com.example.photoshare.interfaces.Interface_ClickViewSend;
import com.example.photoshare.interfaces.Interface_MessageSend;
import com.example.photoshare.R;
import com.example.photoshare.parse.Request_Interceptor;
import com.example.photoshare.parse.Response_PhotoDetails;
import com.example.photoshare.parse.Response_PhotoList;
import com.example.photoshare.customize.Customize_Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Fragment_PhotoDetails extends Fragment {

    private Context context;

    /* 类型码 - 网络请求 */
    /**
     * 网络请求 - 获取 图片列表 likeId
     */
    private final int NET_GET_PHOTO_LIKE_ID = 1;
    /**
     * 网络请求 - 获取 图片列表 collectId
     */
    private final int NET_GET_PHOTO_COLLECT_ID = 2;
    /**
     * 网络请求 - 点赞
     */
    private final int NET_PHOTO_LIKE = 3;
    /**
     * 网络请求 - 取消点赞
     */
    private final int NET_PHOTO_LIKE_CANCEL = 4;
    /**
     * 网络请求 - 收藏
     */
    private final int NET_PHOTO_COLLECT = 5;
    /**
     * 网络请求 - 取消收藏
     */
    private final int NET_PHOTO_COLLECT_CANCEL = 6;

    /* 类型码 - handle */
    /**
     * Handle - 点赞
     */
    private final int HANDLER_PHOTO_LIKE = 1;
    /**
     * Handle - 收藏
     */
    private final int HANDLER_PHOTO_COLLECT = 2;
    /**
     * Handle - 取消点赞
     */
    private final int HANDLER_PHOTO_LIKE_CANCEL = 3;
    /**
     * Handle - 取消收藏
     */
    private final int HANDLER_PHOTO_COLLECT_CANCEL = 4;
    /**
     * Handle - 已经取消点赞
     */
    private final int HANDLER_ALREADY_CANCEL_LIKE = 5;
    /**
     * Handle - 已经取消收藏
     */
    private final int HANDLER_ALREADY_CANCEL_COLLECT = 6;

    /* 接口 */
    private Interface_MessageSend interface_messageSend;

    /* 数据 */
    private Entity_Photo photo;
    private String userId;
    private String photoId;
    private String photoLikeId;
    private String photoCollectId;
    private int photoPosition;

    private boolean hasLike = false;
    private boolean hasCollect = false;

    private boolean isRequestLike = false;
    private boolean isRequestCancelLike = false;
    private boolean isRequestCollect = false;
    private boolean isRequestCancelCollect = false;

    /* 控件 */
    private ImageView ivBack;
    private TextView tvUsername;
    private ImageView ivMore;

    private ViewPager2 vpPhoto;

    private ImageView ivLike;
    private ImageView ivCollect;
    private ImageView ivComment;
    private ImageView ivAnimatorLike;
    private ImageView ivAnimatorCollect;

    private RelativeLayout rlContent;

    private TextView tvTitle;
    private TextView tvContent;

    private TextView tvId;
    private TextView tvCode;

    private Customize_Toast toast;


    private Date startTime = null;
    private Date endTime = null;

    private static final int NONE = 0;
    private static final int FIRST = 1;
    private static final int SECOND = 2;
    private int MODE = NONE;


    /* 网络请求 */

    /**
     * 网络异步请求
     */
    private void networkRequest(int type) {
        Log.d("LOG", "======== 网络请求 ========" + type);

        new Thread(() -> {
            Request request;

            /* 获得 当前图片 LikeId */
            if (type == NET_GET_PHOTO_LIKE_ID) {
                isRequestCancelLike = true;
                String urlParam = "?" + "userId=" + userId;
                request = new Request.Builder().url(Constant_APP.SHARE_GET_URL + urlParam).get().build();
                try {
                    OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Request_Interceptor()).build();
                    client.newCall(request).enqueue(getLikeIdCallback);
                } catch (NetworkOnMainThreadException ex) {
                    ex.printStackTrace();
                }
            }

            /* 获得 当前图片 CollectId */
            if (type == NET_GET_PHOTO_COLLECT_ID) {
                isRequestCancelCollect = true;
                String urlParam = "?" + "userId=" + userId;
                request = new Request.Builder().url(Constant_APP.SHARE_GET_URL + urlParam).get().build();
                try {
                    OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Request_Interceptor()).build();
                    client.newCall(request).enqueue(getCollectIdCallback);
                } catch (NetworkOnMainThreadException ex) {
                    ex.printStackTrace();
                }
            }

            /* 点赞 */
            if (type == NET_PHOTO_LIKE) {
                isRequestLike = true;
                String urlParam = "?" + "shareId=" + photoId + "&userId=" + userId;
                RequestBody requestBody = new FormBody.Builder().build();
                request = new Request.Builder().url(Constant_APP.LIKE_POST_URL + urlParam).post(requestBody).build();
                try {
                    OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Request_Interceptor()).build();
                    client.newCall(request).enqueue(likeCallback);
                } catch (NetworkOnMainThreadException ex) {
                    ex.printStackTrace();
                }
            }

            /* 取消点赞 */
            if (type == NET_PHOTO_LIKE_CANCEL) {
                String urlParam = "?" + "likeId=" + photoLikeId;
                RequestBody requestBody = new FormBody.Builder().build();
                request = new Request.Builder().url(Constant_APP.LIKE_CANCEL_POST_URL + urlParam).post(requestBody).build();
                try {
                    OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Request_Interceptor()).build();
                    client.newCall(request).enqueue(cancelLikeCallback);
                } catch (NetworkOnMainThreadException ex) {
                    ex.printStackTrace();
                }
            }

            /* 收藏 */
            if (type == NET_PHOTO_COLLECT) {
                isRequestCollect = true;
                String urlParam = "?" + "shareId=" + photoId + "&userId=" + userId;
                RequestBody requestBody = new FormBody.Builder().build();
                request = new Request.Builder().url(Constant_APP.COLLECT_POST_URL + urlParam).post(requestBody).build();
                try {
                    OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Request_Interceptor()).build();
                    client.newCall(request).enqueue(collectCallback);
                } catch (NetworkOnMainThreadException ex) {
                    ex.printStackTrace();
                }
            }

            /* 取消收藏 */
            if (type == NET_PHOTO_COLLECT_CANCEL) {
                String urlParam = "?" + "collectId=" + photoCollectId;
                RequestBody requestBody = new FormBody.Builder().build();
                request = new Request.Builder().url(Constant_APP.COLLECT_CANCEL_POST_URL + urlParam).post(requestBody).build();
                try {
                    OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Request_Interceptor()).build();
                    client.newCall(request).enqueue(cancelCollectCallback);
                } catch (NetworkOnMainThreadException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * 回调 - 点赞
     */
    private final okhttp3.Callback likeCallback = new okhttp3.Callback() {
        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            if (response.isSuccessful()) {
                String body = response.body().string();
                Log.d("LOG - 点赞", "响应体 : " + body);
                new Thread(() -> {
                    Gson gson = new Gson();
                    Response_PhotoDetails responseParse = gson.fromJson(body, Response_PhotoDetails.class);

                    Message message = new Message();
                    message.what = HANDLER_PHOTO_LIKE;

                    if (responseParse.getMsg() == null) {
                        message.arg1 = 1;
                    } else if (responseParse.getMsg().equals("该图文已经点过赞")) {
                        message.arg1 = 2;
                    } else if (responseParse.getMsg().equals("当前应用下无此图文分享的主键id")) {
                        message.arg1 = -1;
                    } else message.arg1 = -1;

                    likeHandler.sendMessage(message);
                }).start();
            }
        }

        @Override
        public void onFailure(@NonNull Call call, IOException e) {
            e.printStackTrace();
        }
    };
    /**
     * 操作响应 - 点赞
     */
    private final Handler likeHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            isRequestLike = false;
            if (msg.what == HANDLER_PHOTO_LIKE) {
                if (msg.arg1 == 1) {
                    setCardiacAnimator(ivAnimatorLike);
                    ivLike.setImageResource(R.drawable.ic_baseline_like_white);
                    hasLike = true;
                    interface_messageSend.setPhotoLikeState(photoPosition, true);
                }
                if (msg.arg1 == 2) {
                    toast.makeTextToast(context, "该图已经点赞", Toast.LENGTH_SHORT);
                    ivLike.setImageResource(R.drawable.ic_baseline_like_white);
                    hasLike = true;
                    interface_messageSend.setPhotoLikeState(photoPosition, true);
                }
                if (msg.arg1 == -1) {
                    toast.makeTextToast(context, "点赞失败", Toast.LENGTH_SHORT);
                    hasLike = false;
                }
            }
        }
    };

    /**
     * 回调 - 取消点赞
     */
    private final okhttp3.Callback cancelLikeCallback = new okhttp3.Callback() {
        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            if (response.isSuccessful()) {
                String body = response.body().string();
                Log.d("LOG - 取消点赞", "响应体 : " + body);
                new Thread(() -> {
                    Gson gson = new Gson();
                    Response_PhotoDetails responseParse = gson.fromJson(body, Response_PhotoDetails.class);

                    Message message = new Message();
                    message.what = HANDLER_PHOTO_LIKE_CANCEL;

                    if (responseParse.getMsg() == null) {
                        message.arg1 = 1;
                    } else if (responseParse.getMsg().equals("当前应用下无此点赞表主键id")) {
                        message.arg1 = -1;
                    } else message.arg1 = -1;
                    cancelLikeHandler.sendMessage(message);
                }).start();
            }
        }

        @Override
        public void onFailure(@NonNull Call call, IOException e) {
            e.printStackTrace();
        }
    };
    /**
     * 操作响应 - 取消点赞
     */
    private final Handler cancelLikeHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            isRequestCancelLike = false;
            if (msg.what == HANDLER_PHOTO_LIKE_CANCEL) {
                if (msg.arg1 == 1)
                    toast.makeTextToast(context, "取消成功", Toast.LENGTH_SHORT);
                else if (msg.arg1 == -1)
                    toast.makeTextToast(context, "网络错误", Toast.LENGTH_SHORT);
                ivLike.setImageResource(R.drawable.ic_baseline_like_border_white);
                hasLike = false;
                interface_messageSend.setPhotoLikeState(photoPosition, false);
            }
        }
    };

    /**
     * 回调 - 获得当前图片的 LikeId
     */
    private final okhttp3.Callback getLikeIdCallback = new okhttp3.Callback() {
        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            if (response.isSuccessful()) {
                String body = response.body().string();
                new Thread(() -> {
                    Gson gson = new Gson();
                    Response_PhotoList responseParse = gson.fromJson(body, Response_PhotoList.class);
                    ArrayList<Entity_Photo> photoList = responseParse.getData().getRecords();
                    for (Entity_Photo photo : photoList)
                        if (photo.getId().equals(photoId)) {
                            photoLikeId = photo.getLikeId();
                            if (photoLikeId == null) {
                                Message message = new Message();
                                message.what = HANDLER_ALREADY_CANCEL_LIKE;
                                hintHandler.sendMessage(message);
                            } else {
                                getLikeIdHandler.sendMessage(new Message());
                            }
                        }
                }).start();
            }
        }

        @Override
        public void onFailure(@NonNull Call call, IOException e) {
            e.printStackTrace();
        }
    };
    /**
     * 操作响应 - 获得当前图片的 LikeId
     */
    private final Handler getLikeIdHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            networkRequest(NET_PHOTO_LIKE_CANCEL);
        }
    };


    /**
     * 回调 - 收藏
     */
    private final okhttp3.Callback collectCallback = new okhttp3.Callback() {
        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            if (response.isSuccessful()) {
                String body = response.body().string();
                new Thread(() -> {
                    Gson gson = new Gson();
                    Response_PhotoDetails responseParse = gson.fromJson(body, Response_PhotoDetails.class);

                    Message message = new Message();
                    message.what = HANDLER_PHOTO_COLLECT;
                    if (responseParse.getMsg() == null) {
                        message.arg1 = 1;
                    } else if (responseParse.getMsg().equals("已收藏该图文")) {
                        message.arg1 = 2;
                    } else if (responseParse.getMsg().equals("当前应用下无此图文分享的主键id")) {
                        message.arg1 = -1;
                    } else message.arg1 = -1;
                    collectHandler.sendMessage(message);
                }).start();
            }
        }

        @Override
        public void onFailure(@NonNull Call call, IOException e) {
            e.printStackTrace();
        }
    };
    /**
     * 操作响应 - 收藏
     */
    private final Handler collectHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            isRequestCollect = false;
            if (msg.what == HANDLER_PHOTO_COLLECT) {
                if (msg.arg1 == 1) {
                    setCardiacAnimator(ivAnimatorCollect);
                    ivCollect.setImageResource(R.drawable.ic_baseline_collect_white);
                    hasCollect = true;
                    interface_messageSend.setPhotoCollectState(photoPosition, true);
                }
                if (msg.arg1 == 2) {
                    toast.makeTextToast(context, "该图已经收藏", Toast.LENGTH_SHORT);
                    ivCollect.setImageResource(R.drawable.ic_baseline_collect_white);
                    hasCollect = true;
                    interface_messageSend.setPhotoCollectState(photoPosition, true);
                }
                if (msg.arg1 == -1) {
                    toast.makeTextToast(context, "收藏失败", Toast.LENGTH_SHORT);
                    hasCollect = false;
                }
            }
        }
    };

    /**
     * 回调 - 取消收藏
     */
    private final okhttp3.Callback cancelCollectCallback = new okhttp3.Callback() {
        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            if (response.isSuccessful()) {
                String body = response.body().string();
                Log.d("LOG - 取消收藏", "响应体 : " + body);
                new Thread(() -> {
                    Gson gson = new Gson();
                    Response_PhotoDetails responseParse = gson.fromJson(body, Response_PhotoDetails.class);

                    Message message = new Message();
                    message.what = HANDLER_PHOTO_COLLECT_CANCEL;
                    if (responseParse.getMsg() == null) {
                        message.arg1 = 1;
                    } else message.arg1 = -1;
                    cancelCollectHandler.sendMessage(message);

                }).start();
            }
        }

        @Override
        public void onFailure(@NonNull Call call, IOException e) {
            e.printStackTrace();
        }
    };
    /**
     * 操作响应 - 取消收藏
     */
    private final Handler cancelCollectHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            isRequestCancelCollect = false;
            if (msg.what == HANDLER_PHOTO_COLLECT_CANCEL) {
                if (msg.arg1 == 1)
                    toast.makeTextToast(context, "取消成功", Toast.LENGTH_SHORT);
                else if (msg.arg1 == -1)
                    toast.makeTextToast(context, "网络错误", Toast.LENGTH_SHORT);
                ivCollect.setImageResource(R.drawable.ic_baseline_collect_border_white);
                hasCollect = false;
                interface_messageSend.setPhotoCollectState(photoPosition, false);
            }
        }
    };

    /**
     * 回调 - 获得当前图片的 CollectId
     */
    private final okhttp3.Callback getCollectIdCallback = new okhttp3.Callback() {
        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            if (response.isSuccessful()) {
                String body = response.body().string();
                new Thread(() -> {
                    Gson gson = new Gson();
                    Response_PhotoList responseParse = gson.fromJson(body, Response_PhotoList.class);
                    ArrayList<Entity_Photo> photoList = responseParse.getData().getRecords();
                    for (Entity_Photo photo : photoList)
                        if (photo.getId().equals(photoId)) {
                            photoCollectId = photo.getCollectId();
                            if (photoCollectId == null) {
                                Message message = new Message();
                                message.what = HANDLER_ALREADY_CANCEL_COLLECT;
                                hintHandler.sendMessage(message);
                            } else {
                                getCollectIdHandler.sendMessage(new Message());
                            }
                        }
                }).start();
            }
        }

        @Override
        public void onFailure(@NonNull Call call, IOException e) {
            e.printStackTrace();
        }
    };
    /**
     * 操作响应 - 获得当前图片的 CollectId
     */
    private final Handler getCollectIdHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            networkRequest(NET_PHOTO_COLLECT_CANCEL);
        }
    };

    /**
     * 操作响应 - 已取消点赞/收藏
     */
    private final Handler hintHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            if (msg.what == HANDLER_ALREADY_CANCEL_LIKE) {
                toast.makeTextToast(context, "已取消点赞", Toast.LENGTH_SHORT);
                ivLike.setImageResource(R.drawable.ic_baseline_like_border_white);
            } else if (msg.what == HANDLER_ALREADY_CANCEL_COLLECT) {
                toast.makeTextToast(context, "已取消收藏", Toast.LENGTH_SHORT);
                ivCollect.setImageResource(R.drawable.ic_baseline_collect_border_white);
            }
        }
    };



    /* 监听器 */
    /**
     * 点赞按钮 监听器
     */
    private final View.OnClickListener ivLikeListener = v -> {
        if (hasLike) {
            if (!isRequestLike) networkRequest(NET_GET_PHOTO_LIKE_ID); // 取消点赞
            else toast.makeTextToast(context, "请不要频繁点击哦", Toast.LENGTH_SHORT);
        } else {
            if (!isRequestCancelLike) networkRequest(NET_PHOTO_LIKE); // 点赞
            else toast.makeTextToast(context, "请不要频繁点击哦", Toast.LENGTH_SHORT);
        }
    };
    /**
     * 收藏按钮 监听器
     */
    private final View.OnClickListener ivCollectListener = v -> {
        if (hasCollect) {
            if (!isRequestCollect) networkRequest(NET_GET_PHOTO_COLLECT_ID); // 取消收藏
            else toast.makeTextToast(context, "请不要频繁点击哦", Toast.LENGTH_SHORT);
        } else {
            if (!isRequestCancelCollect) networkRequest(NET_PHOTO_COLLECT); // 收藏
            else toast.makeTextToast(context, "请不要频繁点击哦", Toast.LENGTH_SHORT);
        }
    };
    /**
     * 返回按钮 监听器
     */
    private final View.OnClickListener ivArrowListener = v ->
            Navigation.findNavController(requireView()).popBackStack();
    /**
     * 评论按钮 监听器
     */
    private final View.OnClickListener ivCommentListener = v ->
            Navigation.findNavController(requireView()).navigate(R.id.action_fragment_PhotoDetails_to_fragment_CommentFirst);
    /**
     * 更多按钮 监听器 （分享）
     */
    private final View.OnClickListener ivMoreListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String shareText = "图片下载链接 " + Arrays.toString(photo.getImageUrlList());
            Intent textIntent = new Intent(Intent.ACTION_SEND);
            textIntent.setType("text/plain");
            textIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            startActivity(Intent.createChooser(textIntent, "分享"));
        }
    };
    /**
     * 获取被点击的 viewPager 上的图片按钮
     */
    private final Interface_ClickViewSend ivPhotoListener = new Interface_ClickViewSend() {
        @Override
        public void onItemClick(View view, int position) {
            interface_messageSend.sendViewPagerClickPosition(position);
            Navigation.findNavController(requireView()).navigate(R.id.action_fragment_PhotoDetails_to_fragment_PhotoCheck);
        }
    };
    /**
     * 双击内容进行点赞
     */
    private final View.OnTouchListener rlContentTouchListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {

                case MotionEvent.ACTION_DOWN:
                    if (startTime == null && endTime == null && MODE == NONE) {
                        startTime = new Date(System.currentTimeMillis());
                        MODE = FIRST;
                    } else if (startTime != null && endTime == null && MODE == FIRST) {
                        endTime = new Date(System.currentTimeMillis());
                        MODE = SECOND;
                    } else if (startTime != null && endTime != null && MODE == SECOND) {
                        startTime = null;
                        endTime = null;
                        MODE = NONE;
                    }

                case MotionEvent.ACTION_UP:
                    if (startTime != null && endTime != null && MODE == SECOND) {
                        long range = countTimeDifferential(startTime, endTime);
                        if (!isRequestLike) {
                            if (range <= 8 * 100) {
                                networkRequest(NET_PHOTO_LIKE);
                            }
                        } else toast.makeTextToast(context, "请不要频繁点击哦", Toast.LENGTH_SHORT);
                        startTime = null;
                        endTime = null;
                        MODE = NONE;
                    }
                    break;
            }
            return false;
        }
    };


    /**
     * 获取时间差 (毫秒级)
     *
     * @param startDate 起始时间
     * @param endDate   中止时间
     * @return 时间差 (毫秒级)
     */
    private static long countTimeDifferential(Date startDate, Date endDate) {
        Calendar startCalender = Calendar.getInstance();
        startCalender.setTime(startDate);
        long start = startCalender.getTimeInMillis();

        Calendar endCalender = Calendar.getInstance();
        endCalender.setTime(endDate);
        long end = endCalender.getTimeInMillis();

        return end - start;
    }

    /**
     * 设置 心动 效果，用于点赞、收藏时的动画
     * @param view 视图
     */
    private void setCardiacAnimator(View view){
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(view,"scaleX",1f,1.1f,1f);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(view,"scaleY",1f,1.1f,1f);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(view,"alpha",0f,0.8f,1f,1f,1f,0.8f,0f);

        AnimatorSet animSet = new AnimatorSet();
        animSet.play(scaleXAnimator).with(scaleYAnimator).with(alphaAnimator);
        animSet.setDuration(1400);
        animSet.start();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setData() {
        /* 数据初始化 */
        photoId = photo.getId();
        String photoTitle = photo.getTitle();
        String photoContext = photo.getContent();
        String photoUsername = "用户 " + photo.getUsername();
        String photoID = "ID : " + photo.getId();
        String photoCode = "Code : " + photo.getImageCode();

        List<String> photoUrlList = new ArrayList<>(Arrays.asList(photo.getImageUrlList()));
        Adapter_PhotoSlideDetails viewPagerAdapter = new Adapter_PhotoSlideDetails(getContext(), photoUrlList);
        viewPagerAdapter.setOnPhotoClick(ivPhotoListener);


        /* 界面内容填充 */
        vpPhoto.setAdapter(viewPagerAdapter);
        vpPhoto.setPageTransformer(new PageTransformer_GradualChange());

        tvUsername.setText(photoUsername);
        tvTitle.setText(photoTitle);
        tvContent.setText(photoContext);
        tvId.setText(photoID);
        tvCode.setText(photoCode);

        if (photo.getHasLike()) {
            ivLike.setImageResource(R.drawable.ic_baseline_like_white);
            hasLike = true;
        }
        if (photo.getHasCollect()) {
            ivCollect.setImageResource(R.drawable.ic_baseline_collect_white);
            hasCollect = true;
        }

        /* 设置监听器 */
        ivBack.setOnClickListener(ivArrowListener);
        ivMore.setOnClickListener(ivMoreListener);
        rlContent.setOnTouchListener(rlContentTouchListener);
        ivLike.setOnClickListener(ivLikeListener);
        ivCollect.setOnClickListener(ivCollectListener);
        ivComment.setOnClickListener(ivCommentListener);
    }

    private void bindView(View root) {
        toast = new Customize_Toast(root);

        tvUsername = root.findViewById(R.id.tv_photo_details_username);
        ivBack = root.findViewById(R.id.iv_photo_details_arrow);
        ivMore = root.findViewById(R.id.iv_photo_details_more);

        vpPhoto = root.findViewById(R.id.iv_photo_details_viewPager);
        ivLike = root.findViewById(R.id.iv_photo_details_like);
        ivCollect = root.findViewById(R.id.iv_photo_details_collect);
        ivComment = root.findViewById(R.id.iv_photo_details_comment);

        rlContent = root.findViewById(R.id.rl_photo_details_content_part);

        tvTitle = root.findViewById(R.id.tv_photo_details_title);
        tvContent = root.findViewById(R.id.tv_photo_details_context);

        tvId = root.findViewById(R.id.tv_photo_details_id);
        tvCode = root.findViewById(R.id.tv_photo_details_imageCode);

        ivAnimatorLike = root.findViewById(R.id.iv_photo_details_animator_like);
        ivAnimatorLike.setAlpha(0f);

        ivAnimatorCollect = root.findViewById(R.id.iv_photo_details_animator_collect);
        ivAnimatorCollect.setAlpha(0f);

        BottomNavigationView nav = requireActivity().findViewById(R.id.nav_view);
        nav.setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_photo_details, container, false);
        context = getActivity();
        try {
            interface_messageSend = (Interface_MessageSend) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Interface not implemented");
        }
        bindView(root);
        setData();
        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        photo = ((Activity_Menu) context).getClickPhoto();
        photoPosition = ((Activity_Menu) context).getClickPhotoPosition();
        userId = ((Activity_Menu) context).getUserId();
    }
}