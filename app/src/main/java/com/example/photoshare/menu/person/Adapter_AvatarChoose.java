package com.example.photoshare.menu.person;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.photoshare.R;
import com.example.photoshare.interfaces.Interface_ClickViewSend;

import java.util.ArrayList;

public class Adapter_AvatarChoose extends RecyclerView.Adapter<Adapter_AvatarChoose.photoViewHolder> {

    private final Context context;
    private final ArrayList<String> uriList;

    private Interface_ClickViewSend listener;

    public Adapter_AvatarChoose(Context context, ArrayList<String> uriList) {
        this.context = context;
        this.uriList = uriList;
    }

    public void setOnPhotoClickListener(Interface_ClickViewSend listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public photoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_person_modify_avatar, null);
        return new photoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull photoViewHolder holder, int position) {
        String photoUri = uriList.get(position);
        Glide.with(context).load(photoUri).into(holder.ivPhoto);
    }

    @Override
    public int getItemCount() {
        return uriList.size();
    }

    class photoViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivPhoto;

        public photoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.iv_person_modify_avatar_image);
            ivPhoto.setOnClickListener(v -> {
                if (listener != null)
                    listener.onItemClick(v, getLayoutPosition());
            });
        }
    }


}
