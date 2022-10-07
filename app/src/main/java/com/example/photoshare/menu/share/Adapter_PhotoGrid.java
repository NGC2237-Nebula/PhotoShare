package com.example.photoshare.menu.share;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.photoshare.R;
import com.example.photoshare.interfaces.Interface_ClickViewSend;

import java.util.ArrayList;

public class Adapter_PhotoGrid extends RecyclerView.Adapter<Adapter_PhotoGrid.holder> {

    private final Context context;
    private final int photoLength;
    private final ArrayList<Uri> uriList;

    private Interface_ClickViewSend listener;

    public Adapter_PhotoGrid (Context context, ArrayList<Uri> uriList,int photoLength){
        this.context = context;
        this.photoLength = photoLength;
        this.uriList = uriList;
    }

    public void setOnPhotoClickListener(Interface_ClickViewSend listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_photo_share_upload_list, null);
        return new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {
        Uri photo = uriList.get(position);
        Glide.with(context).load(photo).into(holder.ivPhoto);
    }

    @Override
    public int getItemCount() {
        return uriList.size();
    }

    class holder extends RecyclerView.ViewHolder {
        private final ImageView ivPhoto;

        public holder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.iv_photo_share_upload_list_image);

            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) ivPhoto.getLayoutParams();
            params.height = photoLength;
            ivPhoto.setLayoutParams(params);

            ivPhoto.setOnClickListener(v -> {
                if (listener != null)
                    listener.onItemClick(v, getLayoutPosition());
            });
        }
    }
}
