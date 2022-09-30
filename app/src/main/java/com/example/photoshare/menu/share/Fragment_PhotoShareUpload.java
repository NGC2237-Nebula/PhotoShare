package com.example.photoshare.menu.share;

import static com.example.photoshare.constant.Constant_APP.IMAGE_UPLOAD_POST_URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.NetworkOnMainThreadException;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.photoshare.Activity_Menu;
import com.example.photoshare.interfaces.Interface_MessageSend;
import com.example.photoshare.interfaces.Interface_RecyclerClick;
import com.example.photoshare.R;
import com.example.photoshare.parse.Request_Interceptor;
import com.example.photoshare.parse.Response_InternalServerError;
import com.example.photoshare.parse.Response_PhotoUpload;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Fragment_PhotoShareUpload extends Fragment {

    /* 控件 */
    private RecyclerView rvPhotoFrame;
    private BottomNavigationView nav;

    /* 接口 */
    private Interface_MessageSend interface_messageSend;

    /* 数据 */
    private int photoFrameLength;
    private int photoLength;
    private String imageCode = null;
    private Uri currentUri = null;
    private boolean isUploading = false;
    private final ArrayList<File> fileList = new ArrayList<>();
    private final ArrayList<Uri> uriList = new ArrayList<>();

    /* 适配器 */
    private PhotoAdapter photoAdapter;
    private class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.photoViewHolder> {
        private Interface_RecyclerClick mOnItemClickListener;

        public void setOnItemClickListener(Interface_RecyclerClick onItemClickListener) {
            this.mOnItemClickListener = onItemClickListener;
        }
        @NonNull
        @Override
        public photoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = View.inflate(getContext(), R.layout.item_photo_share_upload_list, null);
            return new photoViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull photoViewHolder holder, int position) {
            Uri photo = uriList.get(position);
            Glide.with(requireContext()).load(photo).into(holder.ivPhoto);
        }
        @Override
        public int getItemCount() {
            return uriList.size();
        }

        class photoViewHolder extends RecyclerView.ViewHolder {
            private final ImageView ivPhoto;

            public photoViewHolder(@NonNull View itemView) {
                super(itemView);
                ivPhoto = itemView.findViewById(R.id.iv_photo_share_upload_list_image);

                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) ivPhoto.getLayoutParams();
                params.height = photoLength;
                ivPhoto.setLayoutParams(params);

                ivPhoto.setOnClickListener(v -> {
                    if (mOnItemClickListener != null)
                        mOnItemClickListener.onItemClick(v, getLayoutPosition());
                });
            }
        }
    }

    /* 类型码 */
    private final int REQUEST_IMAGE_OPEN = 200;

    /* 类型码 - handle */
    /**
     * Handle - 上传成功
     */
    private final int HANDLER_SUCCESS = 1;
    /**
     * Handle - 内部错误
     */
    private final int HANDLER_SERVER_ERROR = 2;
    /**
     * Handle - 使用次数用尽
     */
    private final int HANDLER_USE_UP = 3;
    /**
     * Handle - 文件过大
     */
    private final int HANDLER_FILE_OVER_MAXSIZE = 4;


    /* 网络请求 */
    /**
     * 网络请求 上传图片
     */
    private void networkRequest(List<File> fileList) {
        new Thread(() -> {
            isUploading = true;
            MediaType mediaType = MediaType.parse("form-data");
            MultipartBody.Builder multipartBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM);

            for (int i = 0; i < fileList.size(); i++) {
                File file = fileList.get(i);
                RequestBody fileBody = RequestBody.create(mediaType, file);
                multipartBody.addFormDataPart("fileList", file.getName(), fileBody);
            }

            RequestBody requestBody = multipartBody.build();
            Request request = new Request.Builder()
                    .url(IMAGE_UPLOAD_POST_URL)
                    .post(requestBody)
                    .build();

            try {
                OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Request_Interceptor()).build();
                client.newCall(request).enqueue(photoUploadCallback);
            } catch (NetworkOnMainThreadException ex) {
                ex.printStackTrace();
            }
        }).start();
    }
    /**
     * 回调
     */
    private final okhttp3.Callback photoUploadCallback = new okhttp3.Callback() {
        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            String body = response.body().string();
            Log.d("LOG - 上传文件", "响应体 : " + body);

            if (response.isSuccessful()) {
                new Thread(() -> {
                    Gson gson = new Gson();
                    Response_PhotoUpload responseParse = gson.fromJson(body, Response_PhotoUpload.class);
                    Message message = new Message();
                    if (responseParse.getCode() == 200) {
                        imageCode = responseParse.getData().getImageCode();
                        message.what = HANDLER_SUCCESS;
                    } else if (responseParse.getCode() == 500) {
                        message.what = HANDLER_SERVER_ERROR;
                    } else if (responseParse.getCode() == 5311) {
                        message.what = HANDLER_USE_UP;
                    }
                    dealHandler.sendMessage(message);
                }).start();
            } else {
                new Thread(() -> {
                    Gson gson = new Gson();
                    Response_InternalServerError responseParse = gson.fromJson(body, Response_InternalServerError.class);
                    Message message = new Message();
                    if (responseParse.getError().equals("Internal Server Error")) {
                        message.what = HANDLER_FILE_OVER_MAXSIZE;
                    }
                    dealHandler.sendMessage(message);
                }).start();
            }
        }
        @Override
        public void onFailure(@NonNull Call call, IOException e) {
            e.printStackTrace();
        }
    };
    /**
     * 操作响应
     */
    private final Handler dealHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            isUploading = false;
            if(msg.what == HANDLER_SUCCESS){
                Toast.makeText(requireContext(), "上传成功", Toast.LENGTH_SHORT).show();
                interface_messageSend.sendImageCode(imageCode);
                interface_messageSend.sendImageFileList(fileList);
                Navigation.findNavController(requireView()).navigate(R.id.action_fragment_PhotoShareUpload_to_fragment_PhotoShareUploadDetails);
            }
            else if(msg.what == HANDLER_SERVER_ERROR){
                Toast.makeText(requireContext(), "服务器内部错误,请重试", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).popBackStack();
            }
            else if(msg.what == HANDLER_USE_UP){
                Toast.makeText(requireContext(), "接口使用次数不够,请次日上传", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).popBackStack();
            }
            else if(msg.what == HANDLER_FILE_OVER_MAXSIZE){
                Toast.makeText(requireContext(), "请上传不超过 5MB 的图片", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).popBackStack();
            }
        }
    };


    /* 监听器 */
    /**
     * 添加图片
     */
    private final View.OnClickListener cvAddListener = v -> {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_OPEN);
    };
    /**
     * 确认上传
     */
    private final View.OnClickListener cvConfirmListener = v -> {
        if (fileList.size() == 0) {
            Toast.makeText(requireContext(), "至少上传 1 张图片", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "上传图片中,请稍后", Toast.LENGTH_SHORT).show();
            if(!isUploading) networkRequest(fileList);
        }
    };
    /**
     * 返回上一级
     */
    private final View.OnClickListener cvBackListener = v ->
            Navigation.findNavController(requireView()).popBackStack();
    /**
     * 点击删除列表项 监听器
     */
    private final Interface_RecyclerClick itemClickListener = (view, position) -> {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("确定删除该图片?");
        builder.setPositiveButton("确定", (dialog, which) -> removeItem(position));
        builder.setNegativeButton("取消", (dialog, which) -> {});
        builder.create().show();
    };


    /**
     * 从 fileList 、uriList 以及适配器中移除数据
     * @param position 待移除图片位置 position
     */
    @SuppressLint("NotifyDataSetChanged")
    private void removeItem(int position){
        fileList.remove(position);
        uriList.remove(position);
        photoAdapter.notifyDataSetChanged();
    }
    /**
     * 添加数据到 fileList 、uriList  以及适配器中
     * @param currentUri 当前图片 uri
     */
    @SuppressLint("NotifyDataSetChanged")
    private void addItem(Uri currentUri) {
        if (fileList.size() >= 9 || uriList.size() >= 9) {
            Toast.makeText(requireContext(), "一次最多上传 9 张图片", Toast.LENGTH_SHORT).show();
        } else {
            File currentFile = new File(getPathFromUri(requireContext(), currentUri));
            if (currentFile.length() >= 1048576*5) {
                Toast.makeText(requireContext(), "请上传小于 5MB 的图片", Toast.LENGTH_SHORT).show();
            } else {
                fileList.add(currentFile);
                uriList.add(currentUri);
                photoAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * API < 19 时根据相册中图片 uri 获取 path
     * <p>结果类似于 /storage/emulated/0/Pictures/12345.jpg
     * @param context 上下文对象
     * @param uri 图片 uri
     * @return 图片 path
     */
    private static String getPathFromUri(Context context, Uri uri) {
        Cursor cursor = null;
        String path;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(uri, proj, null, null, null);
            //获得用户选择的图片的索引值
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            //将光标移至开头
            cursor.moveToFirst();
            //根据索引值获取图片路径
            path = cursor.getString(column_index);
        } finally {
            if (cursor != null) cursor.close();
        }
        return path;
    }


    /**
     * 获取手机宽度
     * @return 手机宽度（单位：像素 px）
     */
    private int getScreenWidth(){
        DisplayMetrics metrics = new DisplayMetrics(); //定义DisplayMetrics 对象
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics); //取得窗口属性
        return metrics.widthPixels;
    }

    private void bindView(View root) {
        nav = requireActivity().findViewById(R.id.nav_view);
        nav.setVisibility(View.INVISIBLE);

        CardView cvAdd = root.findViewById(R.id.cv_photo_share_upload_add);
        cvAdd.setOnClickListener(cvAddListener);

        CardView cvConfirm = root.findViewById(R.id.cv_photo_share_upload_confirm);
        cvConfirm.setOnClickListener(cvConfirmListener);

        CardView cvBack = root.findViewById(R.id.cv_photo_share_upload_close);
        cvBack.setOnClickListener(cvBackListener);

        photoFrameLength = getScreenWidth() - 40;
        photoLength = (getScreenWidth() - 148) / 3;

        rvPhotoFrame = root.findViewById(R.id.rv_photo_share_upload_list);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) rvPhotoFrame.getLayoutParams();
        params.height = photoFrameLength;
        rvPhotoFrame.setLayoutParams(params);
    }

    private void setData(){
        photoAdapter = new PhotoAdapter();
        photoAdapter.setOnItemClickListener(itemClickListener);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),3);

        rvPhotoFrame.setAdapter(photoAdapter);
        rvPhotoFrame.setLayoutManager(layoutManager);
        if(uriList.size() == 0) {
            addItem(currentUri);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_OPEN && resultCode == Activity.RESULT_OK && data != null) {
            addItem(data.getData());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_photo_share_upload, container, false);
        bindView(root);
        setData();
        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        currentUri = ((Activity_Menu) context).getPhotoUri();
        try{
            interface_messageSend = (Interface_MessageSend) context;
        }catch (ClassCastException e){
            throw new ClassCastException();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fileList.clear();
        uriList.clear();
        imageCode = null;
        currentUri = null;
        interface_messageSend.sendPhotoUri(null);
    }
}