package com.example.photoshare.menu.person;

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

public class Adapter_Updata extends ArrayAdapter<Entity_Photo> {
    private final Context mContext;
    private final int resourceId;

    private static class ViewHolder {
        ImageView ivImage;
        TextView tvTitle;
        TextView tvContent;
    }

    public Adapter_Updata(Context context, int resourceId, List<Entity_Photo> data) {
        super(context, resourceId, data);
        this.mContext = context;
        this.resourceId = resourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Entity_Photo photo = getItem(position);
        final ViewHolder viewHolder;
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvTitle = view.findViewById(R.id.tv_person_list_myself_title);
            viewHolder.tvContent = view.findViewById(R.id.tv_person_list_myself_content);
            viewHolder.ivImage = view.findViewById(R.id.iv_person_list_myself_image);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        String photoUrl = photo.getImageUrlList()[0];
        Glide.with(mContext).load(photoUrl).into(viewHolder.ivImage);
        viewHolder.tvTitle.setText(photo.getTitle());
        viewHolder.tvContent.setText(photo.getContent());

        return view;
    }
}
