package com.example.photoshare.menu.explore;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.photoshare.R;
import com.example.photoshare.entity.Entity_Photo;
import com.example.photoshare.interfaces.Interface_ClickViewSend;

import java.util.List;

public class Adapter_PhotoExploreBig extends RecyclerView.Adapter<Adapter_PhotoExploreBig.photoViewHolder> {
    private final Context context;
    private final List<Entity_Photo> photoList;
    private Interface_ClickViewSend listener;

    public Adapter_PhotoExploreBig(Context context, List<Entity_Photo> photoList) {
        this.photoList = photoList;
        this.context = context;
    }

    public void setOnPhotoClickListener(Interface_ClickViewSend listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Adapter_PhotoExploreBig.photoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_photo_explore_big, null);
        return new photoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_PhotoExploreBig.photoViewHolder holder, int position) {
        Entity_Photo photo = photoList.get(position);
        Glide.with(context).load(photo.getImageUrlList()[0]).into(holder.ivPhoto);
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    class photoViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivPhoto;

        public photoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.iv_explore_photo_big_image);
            ivPhoto.setOnClickListener(v -> {
                if (listener != null)
                    listener.onItemClick(v, getLayoutPosition());
            });
        }
    }
}
