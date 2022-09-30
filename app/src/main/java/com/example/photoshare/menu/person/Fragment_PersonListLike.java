package com.example.photoshare.menu.person;

import android.annotation.SuppressLint;
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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.photoshare.constant.Constant_APP;
import com.example.photoshare.Activity_Menu;
import com.example.photoshare.entity.Entity_Photo;
import com.example.photoshare.R;
import com.example.photoshare.parse.Request_Interceptor;
import com.example.photoshare.parse.Response_PhotoList;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Fragment_PersonListLike extends Fragment {

    /* 数据 */
    private String userId = null;

    /* 控件 */
    private TextView tvHint;

    /* 适配器 */
    private final List<Entity_Photo> photoArrayList = new ArrayList<>();

    private PhotoAdapter photoAdapter;

    private static class photoViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivPhoto;
        private final TextView tvTitle;
        private final TextView tvContent;
        private final TextView tvName;

        public photoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.iv_person_list_like_image);
            tvTitle = itemView.findViewById(R.id.tv_person_list_like_title);
            tvName = itemView.findViewById(R.id.tv_person_list_like_name);
            tvContent = itemView.findViewById(R.id.tv_person_list_like_content);
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<photoViewHolder> {
        @NonNull
        @Override
        public photoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = View.inflate(getContext(), R.layout.item_person_list_like, null);
            return new photoViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull photoViewHolder holder, int position) {
            Entity_Photo photo = photoArrayList.get(position);
            Glide.with(requireContext()).load(photo.getImageUrlList()[0]).into(holder.ivPhoto);
            holder.tvTitle.setText(photo.getTitle());
            holder.tvName.setText(photo.getUsername());
            holder.tvContent.setText(photo.getContent());
        }
        @Override
        public int getItemCount() {
            return photoArrayList.size();
        }
    }


    /* 网络请求 */
    private final okhttp3.Callback showLikeListCallback = new okhttp3.Callback() {
        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            if (response.isSuccessful()) {
                String body = response.body().string();
                Log.d("LOG - 我的喜欢", "响应体 : " + body);
                new Thread(() -> {
                    Gson gson = new Gson();
                    Response_PhotoList responseParse = gson.fromJson(body, Response_PhotoList.class);
                    if(responseParse.getData() != null) {
                        ArrayList<Entity_Photo> photoList = responseParse.getData().getRecords();
                        for (Entity_Photo photo : photoList) {
                            Message message = new Message();
                            message.obj = photo;
                            photoSendHandler.sendMessage(message);
                        }
                    } else {
                        hintHandler.sendMessage(new Message());
                    }
                }).start();
            }
        }
        @Override
        public void onFailure(@NonNull Call call, IOException e) {
            e.printStackTrace();
        }
    };

    private final Handler photoSendHandler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message msg) {
            photoArrayList.add((Entity_Photo) msg.obj);
            photoAdapter.notifyDataSetChanged();
        }
    };

    private final Handler hintHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            tvHint.setVisibility(View.VISIBLE);
        }
    };

    private void networkRequest() {
        new Thread(() -> {
            Request request;
            String urlParam = "?" + "userId=" + userId;
            request = new Request.Builder().url(Constant_APP.LIKE_GET_URL + urlParam).get().build();
            try {
                OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Request_Interceptor()).build();
                client.newCall(request).enqueue(showLikeListCallback);
            } catch (NetworkOnMainThreadException ex) {
                ex.printStackTrace();
            }
        }).start();
    }


    /* 监听器 */
    private final View.OnClickListener ivBackListener = v ->
            Navigation.findNavController(requireView()).popBackStack();


    private void init(View root){
        RecyclerView rvPhotoList = root.findViewById(R.id.rv_like_list);
        ImageView ivBack = root.findViewById(R.id.iv_list_like_back);
        tvHint = root.findViewById(R.id.tv_list_like_hint);
        tvHint.setVisibility(View.INVISIBLE);

        ivBack.setOnClickListener(ivBackListener);

        photoAdapter = new PhotoAdapter();
        rvPhotoList.setAdapter(photoAdapter);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rvPhotoList.setLayoutManager(layoutManager);
        networkRequest();
   }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        userId = ((Activity_Menu) context).getUserId();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_person_list_like, container, false);
        init(root);
        return root;
    }
}