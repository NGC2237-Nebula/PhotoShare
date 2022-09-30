package com.example.photoshare.menu.share;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.photoshare.R;

import java.io.File;
import java.util.ArrayList;

public class Adapter_PhotoSlideUpload extends RecyclerView.Adapter<Adapter_PhotoSlideUpload.ViewPagerViewHolder> {

    private final ArrayList<File> photoList;
    private final Context context;

    public Adapter_PhotoSlideUpload(Context context, ArrayList<File> photoList){
        this.context = context;
        this.photoList = photoList;
    }

    /**
     * 绑定布局以及属性
     */
    @NonNull
    @Override
    public ViewPagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewPagerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_share_upload_slide, parent, false));
    }

    /**
     * 设置数据
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewPagerViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        Glide.with(context).load(photoList.get(position)).into(viewHolder.ivPhoto);
    }

    /**
     * 页面的数量
     */
    @Override
    public int getItemCount() {
        return photoList.size();
    }

    /**
     * 绑定布局以及属性
     */
    static class ViewPagerViewHolder extends RecyclerView.ViewHolder{
        ImageView ivPhoto;
        public ViewPagerViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.iv_photo_share_upload_photo);
        }
    }
}
