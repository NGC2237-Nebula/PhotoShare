package com.example.photoshare.menu.explore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.photoshare.entity.Entity_Comment;
import com.example.photoshare.R;

import java.util.List;

public class Adapter_CommentFirst extends ArrayAdapter<Entity_Comment> {

    private final int resourceId;

    private ItemControlListener itemControlListener;
    public interface ItemControlListener {
        void setItemControl(View view, int position);
    }
    public void getItemControl(ItemControlListener listener){
        this.itemControlListener = listener;
    }

    private static class ViewHolder {
        TextView tvUsername;
        TextView tvContent;
        TextView tvCreateTime;
        CardView cvIsHost;
        RelativeLayout rlMore;
    }

    public Adapter_CommentFirst(Context context, int resourceId, List<Entity_Comment> data) {
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
            viewHolder.tvUsername = view.findViewById(R.id.tv_comment_first_username);
            viewHolder.tvContent = view.findViewById(R.id.tv_comment_first_context);
            viewHolder.tvCreateTime = view.findViewById(R.id.tv_comment_first_create_time);
            viewHolder.cvIsHost = view.findViewById(R.id.cv_comment_first_host);
            viewHolder.rlMore = view.findViewById(R.id.rl_comment_first_more);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        String usernameString = "用户 "+comment.getUsername();
        if(!comment.getIsHost()) viewHolder.cvIsHost.setVisibility(View.INVISIBLE);
        viewHolder.tvContent.setText(comment.getContent());
        viewHolder.tvUsername.setText(usernameString);
        viewHolder.tvCreateTime.setText(comment.getCreateTime());
        viewHolder.rlMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemControlListener.setItemControl(v,position);
            }
        });
        return view;
    }
}