package com.example.photoshare.menu.explore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.photoshare.R;

import java.util.List;

public class Adapter_PhotoSlideDetails extends RecyclerView.Adapter<Adapter_PhotoSlideDetails.ViewPagerViewHolder> {

    private final List<String> photoList;
    private final Context context;

    private ItemControlListener itemControlListener;
    public interface ItemControlListener {
        void setItemControl(View view, int position);
    }
    public void getItemControl(ItemControlListener listener){
        this.itemControlListener = listener;
    }

    public Adapter_PhotoSlideDetails(Context context, List<String> photoList){
        this.context = context;
        this.photoList = photoList;
    }

    /**
     * 绑定布局以及属性
     */
    @NonNull
    @Override
    public ViewPagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewPagerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_details_slide, parent, false));
    }

    /**
     * 设置数据
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewPagerViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        Glide.with(context).load(photoList.get(position)).into(viewHolder.ivPhoto);
        viewHolder.ivPhoto.setOnClickListener(v -> itemControlListener.setItemControl(v, position));

        String hintString = (position + 1) + " / " + photoList.size();
        viewHolder.tvHint.setText(hintString);
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
        TextView tvHint;
        public ViewPagerViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.iv_photo_details_slide_photo);
            tvHint = itemView.findViewById(R.id.tv_photo_details_slide_hint);
        }
    }
}