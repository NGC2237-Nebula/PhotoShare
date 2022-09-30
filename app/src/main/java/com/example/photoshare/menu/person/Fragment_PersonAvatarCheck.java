package com.example.photoshare.menu.person;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.photoshare.Activity_Menu;
import com.example.photoshare.entity.Entity_User;
import com.example.photoshare.R;


public class Fragment_PersonAvatarCheck extends Fragment {

    private String userAvatar = null;
    private Entity_User user = null;

    private ImageView ivAvatar;

    private void setData(Context context) {
        Glide.with(context).load(userAvatar).into(ivAvatar);
    }

    private void bindView(View root) {
        CardView cvBack = root.findViewById(R.id.cv_photo_avatar_check_back);
        cvBack.setOnClickListener(v -> Navigation.findNavController(requireView()).popBackStack());
        ivAvatar = root.findViewById(R.id.iv_photo_avatar_check_avatar);
    }

    private void initData() {
        userAvatar = user.getAvatar();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_person_avatar_check, container, false);
        initData();
        bindView(root);
        setData(getActivity());
        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        user = ((Activity_Menu) context).getUser();
    }
}