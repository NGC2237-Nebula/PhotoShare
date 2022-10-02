package com.example.photoshare.customize;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.photoshare.R;

public class Customize_Toast {

    private final View root;

    public Customize_Toast(View root) {
        this.root = root;
    }

    public void makeImgToast(Context context, int imgId, int time) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.toast_img, root.findViewById(R.id.rl_toast_img));
        
        ImageView imageView = view.findViewById(R.id.iv_toast_img);
        imageView.setImageResource(imgId);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(time);
        toast.setView(view);
        toast.show();
    }

    public void makeTextToast(Context context, String text, int time) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.toast_text, root.findViewById(R.id.rl_toast_text));

        TextView textView = view.findViewById(R.id.tv_toast_text);
        textView.setText(text);

        Toast toast = new Toast(context);
        toast.setDuration(time);
        toast.setView(view);
        toast.show();
    }
}
