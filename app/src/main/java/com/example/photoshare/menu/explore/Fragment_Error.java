package com.example.photoshare.menu.explore;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.photoshare.R;


public class Fragment_Error extends Fragment {

    private ImageView ivCat;
    private int currentCat = 0;
    private final int[] resourceId = new int[]{
            R.drawable.ic_error_1,
            R.drawable.ic_error_2,
            R.drawable.ic_error_3,
            R.drawable.ic_error_4,
            R.drawable.ic_error_5,
            R.drawable.ic_error_6
    };

    private final View.OnClickListener ivCatListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            currentCat += 1;
            if(currentCat >= 6) currentCat = 0;
            ivCat.setImageResource(resourceId[currentCat]);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_error, container, false);
        ivCat = root.findViewById(R.id.iv_error_cat);
        ivCat.setOnClickListener(ivCatListener);
        return root;
    }
}