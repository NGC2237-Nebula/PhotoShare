package com.example.photoshare.customize;

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

    public void makeShortToast(Context context, String text, int resId) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.toast_text, root.findViewById(R.id.rl_toast_text));

        TextView textView = view.findViewById(R.id.tv_toast_text);
        textView.setText(text);

        ImageView imageView = view.findViewById(R.id.iv_toast_img);
        imageView.setImageResource(resId);

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.show();
    }
}
