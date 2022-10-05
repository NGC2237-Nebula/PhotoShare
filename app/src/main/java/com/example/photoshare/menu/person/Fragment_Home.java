package com.example.photoshare.menu.person;

import static com.example.photoshare.constant.Constant_APP.MAN;
import static com.example.photoshare.constant.Constant_APP.SECRET;
import static com.example.photoshare.constant.Constant_APP.WOMAN;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.photoshare.Activity_Menu;
import com.example.photoshare.entity.Entity_User;
import com.example.photoshare.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class Fragment_Home extends Fragment {

    Context context;

    /* 控件 */
    private CircleImageView civAvatar;
    private TextView tvName;
    private TextView tvSex;

    /**
     * 获取手机高度
     * @return 手机高度（单位：像素 px）
     */
    private int getScreenHeight(){
        DisplayMetrics metrics = new DisplayMetrics(); //定义DisplayMetrics 对象
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics); //取得窗口属性
        return metrics.heightPixels;
    }

    /**
     * c初始化 视图
     * @param root 根视图
     */
    private void bindView(View root) {
        civAvatar = root.findViewById(R.id.civ_home_avatar);
        civAvatar.setOnClickListener(v ->
                Navigation.findNavController(requireView()).navigate(R.id.action_navigation_home_to_fragment_AvatarView));

        tvName = root.findViewById(R.id.tv_home_username);
        tvSex = root.findViewById(R.id.tv_home_usersex);

        ImageView ivBackground = root.findViewById(R.id.iv_home_background);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivBackground.getLayoutParams();
        params.height = getScreenHeight() * 4 / 5;
        ivBackground.setLayoutParams(params);

        ImageView ivSettings = root.findViewById(R.id.iv_home_settings);
        ivSettings.setOnClickListener(v ->
                Navigation.findNavController(requireView()).navigate(R.id.action_navigation_home_to_fragment_Settings));

        ImageView ivDetails = root.findViewById(R.id.iv_home_details);
        ivDetails.setOnClickListener(v ->
                Navigation.findNavController(requireView()).navigate(R.id.action_navigation_home_to_fragment_PersonDetails));

        RelativeLayout rlMyself = root.findViewById(R.id.rl_home_my);
        rlMyself.setOnClickListener(v ->
                Navigation.findNavController(requireView()).navigate(R.id.action_navigation_home_to_fragment_ListMySelf));

        RelativeLayout rlLike = root.findViewById(R.id.rl_home_like);
        rlLike.setOnClickListener(v ->
                Navigation.findNavController(requireView()).navigate(R.id.action_navigation_home_to_fragment_ListLike));

        RelativeLayout rlCollect = root.findViewById(R.id.rl_home_collect);
        rlCollect.setOnClickListener(v ->
                Navigation.findNavController(requireView()).navigate(R.id.action_navigation_home_to_fragment_ListCollect));

        RelativeLayout rlAboutUs = root.findViewById(R.id.rl_home_about_us);
        rlAboutUs.setOnClickListener(v ->
                Navigation.findNavController(requireView()).navigate(R.id.action_navigation_home_to_fragment_AboutUs));
    }

    /**
     * 初始化 数据
     */
    private void setData() {
        Entity_User user = ((Activity_Menu) context).getUser();
        if (user == null)
            Toast.makeText(context, "用户信息初始化错误", Toast.LENGTH_SHORT).show();
        else {
            String userAvatar = user.getAvatar();
            String userName = user.getUsername();
            int userSex = user.getSex();

            if(userAvatar != null) Glide.with(this).load(userAvatar).into(civAvatar);
            tvName.setText(userName);
            if (userSex == MAN) tvSex.setText("男");
            else if (userSex == WOMAN) tvSex.setText("女");
            else if (userSex == SECRET) tvSex.setText("保密");
            else tvSex.setText("未填写");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        context = getActivity();
        bindView(root);
        setData();
        return root;
    }
}