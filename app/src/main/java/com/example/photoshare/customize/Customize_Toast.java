package com.example.photoshare.customize;

import android.animation.ObjectAnimator;
import android.content.Context;
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

    public void makeImgToast(Context context, String text, int resId) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.toast_img, root.findViewById(R.id.rl_toast_img));

        TextView textView = view.findViewById(R.id.tv_toast_img_text);
        textView.setText(text);

        ImageView imageView = view.findViewById(R.id.iv_toast_img_img);
        imageView.setImageResource(resId);

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.show();
    }

    /*眨眼睛*/

    public void makeEyeToast(Context context, String text) {
        Toast toast = new Toast(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.toast_eye, root.findViewById(R.id.rl_toast_eye));

        TextView textView = view.findViewById(R.id.tv_toast_eye_text);
        textView.setText(text);

        ImageView imageView = view.findViewById(R.id.iv_toast_eye_eye);
        imageView.setScaleY(0f);
        ObjectAnimator.ofFloat(imageView,"scaleY",0f,1f,0f,1f).setDuration(900).start();

        toast.setView(view);
        toast.show();
    }
}
