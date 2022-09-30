package com.example.photoshare.menu.explore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.photoshare.entity.Entity_Photo;
import com.example.photoshare.R;

import java.util.List;

public class Adapter_PhotoList extends ArrayAdapter<Entity_Photo> {
    private final Context mContext;
    private final int resourceId;

    private static class ViewHolder {
        ImageView ivImage;
        TextView tvTitle;
        TextView tvName;
        TextView tvContent;
        TextView tvLike;
        TextView tvCollect;
    }

    public Adapter_PhotoList(Context context, int resourceId, List<Entity_Photo> data) {
        super(context, resourceId, data);
        this.mContext = context;
        this.resourceId = resourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Entity_Photo photo = getItem(position);
        View view;
        final ViewHolder viewHolder;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvTitle = view.findViewById(R.id.tv_photo_explore_list_title);
            viewHolder.tvName = view.findViewById(R.id.tv_photo_explore_list_name);
            viewHolder.tvContent = view.findViewById(R.id.tv_photo_explore_list_content);
            viewHolder.ivImage = view.findViewById(R.id.iv_photo_explore_list_image);
            viewHolder.tvLike = view.findViewById(R.id.tv_photo_explore_list_like);
            viewHolder.tvCollect = view.findViewById(R.id.tv_photo_explore_list_collect);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        String photoUrl = photo.getImageUrlList()[0];
        String nameString = "" + photo.getUsername();
        String likeString = "0";
        String collectString = "0";
        if (photo.getLikeNum() != 0) likeString = "" + photo.getLikeNum();
        if (photo.getCollectNum() != 0) collectString = "" + photo.getCollectNum();

        Glide.with(mContext).load(photoUrl).into(viewHolder.ivImage);
        viewHolder.tvTitle.setText(photo.getTitle());
        viewHolder.tvName.setText(nameString);
        viewHolder.tvContent.setText(photo.getContent());
        viewHolder.tvLike.setText(likeString);
        viewHolder.tvCollect.setText(collectString);
        return view;
    }
}