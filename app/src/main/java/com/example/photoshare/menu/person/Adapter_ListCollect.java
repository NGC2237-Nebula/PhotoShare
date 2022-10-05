package com.example.photoshare.menu.person;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.photoshare.R;
import com.example.photoshare.entity.Entity_Photo;

import java.util.List;

public class Adapter_ListCollect extends RecyclerView.Adapter<Adapter_ListCollect.photoViewHolder>{
    private final Context context;
    private final List<Entity_Photo> photoList;

    public Adapter_ListCollect(Context context, List<Entity_Photo> photoList) {
        this.context = context;
        this.photoList = photoList;
    }


    @NonNull
    @Override
    public photoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_person_list_collect, null);
        return new photoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull photoViewHolder holder, int position) {
        Entity_Photo photo = photoList.get(position);
        Glide.with(context).load(photo.getImageUrlList()[0]).into(holder.ivPhoto);
        holder.tvTitle.setText(photo.getTitle());
        holder.tvName.setText(photo.getUsername());
        holder.tvContent.setText(photo.getContent());
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    static class photoViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivPhoto;
        private final TextView tvTitle;
        private final TextView tvContent;
        private final TextView tvName;

        public photoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.iv_person_list_collect_image);
            tvTitle = itemView.findViewById(R.id.tv_person_list_collect_title);
            tvName = itemView.findViewById(R.id.tv_person_list_collect_name);
            tvContent = itemView.findViewById(R.id.tv_person_list_collect_content);
        }
    }
}
