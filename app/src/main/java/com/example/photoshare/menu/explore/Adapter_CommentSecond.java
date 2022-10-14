package com.example.photoshare.menu.explore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.photoshare.entity.Entity_Comment;
import com.example.photoshare.R;

import java.util.List;

public class Adapter_CommentSecond  extends ArrayAdapter<Entity_Comment> {

    private final int resourceId;

    private static class ViewHolder {
        TextView tvUsername;
        TextView tvContent;
        TextView tvCreateTime;
    }


    public Adapter_CommentSecond(Context context, int resourceId, List<Entity_Comment> data) {
        super(context, resourceId, data);
        this.resourceId = resourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Entity_Comment comment = getItem(position);
        final ViewHolder viewHolder;
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvUsername = view.findViewById(R.id.tv_comment_second_username);
            viewHolder.tvContent = view.findViewById(R.id.tv_comment_second_context);
            viewHolder.tvCreateTime = view.findViewById(R.id.tv_comment_second_create_time);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.tvUsername.setText(comment.getUsername());
        viewHolder.tvContent.setText(comment.getContent());
        viewHolder.tvCreateTime.setText(comment.getCreateTime());

        return view;
    }
}
