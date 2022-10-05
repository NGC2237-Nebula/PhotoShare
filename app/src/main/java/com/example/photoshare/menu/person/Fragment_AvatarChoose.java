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

import com.example.photoshare.Activity_Menu;
import com.example.photoshare.R;
import com.example.photoshare.entity.Entity_Photo;
import com.example.photoshare.interfaces.Interface_ClickViewSend;
import com.example.photoshare.interfaces.Interface_MessageSend;

import java.util.ArrayList;
import java.util.Collections;


public class Fragment_AvatarChoose extends Fragment {

    /* 数据 */
    private final ArrayList<String> allPhotoUriList = new ArrayList<>();
    private ArrayList<Entity_Photo> allPhotoList;

    /* 控件 */
    private TextView tvHint;

    /* 接口 */
    private Interface_MessageSend interface_messageSend;

    /* 适配器 */
    private Adapter_AvatarChoose photoAdapter;


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
    private final Interface_ClickViewSend itemClickListener = new Interface_ClickViewSend() {
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

        ImageView ivArrow = root.findViewById(R.id.iv_person_avatar_modify_back);
        ivArrow.setOnClickListener(ivArrowListener);


        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        photoAdapter = new Adapter_AvatarChoose(requireContext(),allPhotoUriList);
        photoAdapter.setOnPhotoClickListener(itemClickListener);

        RecyclerView rvPhotoList = root.findViewById(R.id.sw_person_avatar_modify_swipe);
        rvPhotoList.setAdapter(photoAdapter);
        rvPhotoList.setLayoutManager(layoutManager);
        addAllPhoto(allPhotoList);
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
        allPhotoList = ((Activity_Menu) context).getAllPhotoList();
        try {
            interface_messageSend = (Interface_MessageSend) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Interface not implemented");
        }
    }
}