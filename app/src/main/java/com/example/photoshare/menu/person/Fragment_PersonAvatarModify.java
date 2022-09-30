package com.example.photoshare.menu.person;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.photoshare.Activity_Menu;
import com.example.photoshare.entity.Entity_Photo;
import com.example.photoshare.interfaces.Interface_MessageSend;
import com.example.photoshare.interfaces.Interface_RecyclerClick;
import com.example.photoshare.R;

import java.util.ArrayList;
import java.util.Collections;


public class Fragment_PersonAvatarModify extends Fragment {

    /* 数据 */
    private ArrayList<Entity_Photo> photoList;

    /* 控件 */
    private ImageView ivArrow;
    private RecyclerView rvPhotoList;
    private TextView tvHint;

    /* 接口 */
    private Interface_MessageSend interface_messageSend;

    /* 适配器 */
    private PhotoAdapter photoAdapter;

    private final ArrayList<String> allPhotoUriList = new ArrayList<>();

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.photoViewHolder> {
        private Interface_RecyclerClick mListener;
        public void setOnItemClickListener(Interface_RecyclerClick onItemClickListener) {
            this.mListener = onItemClickListener;
        }
        @NonNull
        @Override
        public photoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = View.inflate(getContext(), R.layout.item_person_modify_avatar, null);
            return new photoViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull photoViewHolder holder, int position) {
            String photoUri = allPhotoUriList.get(position);
            Glide.with(requireContext()).load(photoUri).into(holder.ivPhoto);
        }
        @Override
        public int getItemCount() {
            return allPhotoUriList.size();
        }
        class photoViewHolder extends RecyclerView.ViewHolder {
            private final ImageView ivPhoto;
            public photoViewHolder(@NonNull View itemView) {
                super(itemView);
                ivPhoto = itemView.findViewById(R.id.iv_person_modify_avatar_image);
                ivPhoto.setOnClickListener(v -> {
                    if (mListener != null)
                        mListener.onItemClick(v, getLayoutPosition());
                });
            }
        }
    }


    /**
     * 解析图片列表
     */
    @SuppressLint("NotifyDataSetChanged")
    private void addAllPhoto(ArrayList<Entity_Photo> photoList) {
        if (photoList != null) {
            for (Entity_Photo photo : photoList) {
                String[] photoUriList = photo.getImageUrlList();
                Collections.addAll(allPhotoUriList, photoUriList);
            }
            photoAdapter.notifyDataSetChanged();
        } else {
            tvHint.setVisibility(View.VISIBLE);
        }
    }


    /* 监听器 */
    /**
     * 返回点击的图片
     */
    private final Interface_RecyclerClick itemClickListener = new Interface_RecyclerClick() {
        @Override
        public void onItemClick(View view, int position) {
            String avatarUri = allPhotoUriList.get(position);
            interface_messageSend.sendModifyAvatar(avatarUri);
            Navigation.findNavController(requireView()).popBackStack();
        }
    };
    /**
     * 返回上一个界面
     */
    private final View.OnClickListener ivArrowListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            interface_messageSend.sendModifyAvatar(null);
            Navigation.findNavController(requireView()).popBackStack();
        }
    };




    private void init(View root) {
        tvHint = root.findViewById(R.id.tv_person_avatar_modify_hint);
        tvHint.setVisibility(View.INVISIBLE);

        ivArrow = root.findViewById(R.id.iv_person_avatar_modify_back);
        ivArrow.setOnClickListener(ivArrowListener);

        photoAdapter = new PhotoAdapter();
        photoAdapter.setOnItemClickListener(itemClickListener);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);

        rvPhotoList = root.findViewById(R.id.sw_person_avatar_modify_swipe);
        rvPhotoList.setAdapter(photoAdapter);
        rvPhotoList.setLayoutManager(layoutManager);
        addAllPhoto(photoList);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_person_avatar_modify, container, false);
        init(root);
        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        photoList = ((Activity_Menu) getActivity()).getAllPhotoList();
        try {
            interface_messageSend = (Interface_MessageSend) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Interface not implemented");
        }
    }
}