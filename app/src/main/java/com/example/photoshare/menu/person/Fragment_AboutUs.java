package com.example.photoshare.menu.person;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.photoshare.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class Fragment_AboutUs extends Fragment {

    private BottomNavigationView nav;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_about_us, container, false);

        nav = requireActivity().findViewById(R.id.nav_view);
        nav.setVisibility(View.INVISIBLE);

        ImageView ivBack = root.findViewById(R.id.iv_about_us_back);
        ivBack.setOnClickListener(v -> Navigation.findNavController(requireView()).popBackStack());

        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        nav.setVisibility(View.VISIBLE);
    }
}