package com.example.photoshare.menu.share;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.photoshare.R;
import com.example.photoshare.customize.Customize_Toast;
import com.example.photoshare.interfaces.Interface_MessageSend;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class Fragment_Share extends Fragment {

    private Context context;

    /* 数据 */
    private Uri cameraPhotoUri = null;
    private Customize_Toast toast;

    /* 控件 */
    private Dialog dialog;

    /* 标识符 */
    private final int INTENT_OPEN_CAMERA = 100;
    private final int INTENT_OPEN_ALBUM = 200;

    /* 权限 */
    private final int REQUEST_EXTERNAL_STORAGE = 1;
    private final int REQUEST_CAMERA = 2;
    /**
     * 权限 - 允许读写外部存储
     */
    private final static String[] PERMISSIONS_EXTERNAL_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    /**
     * 权限 - 允许访问摄像头进行拍照
     */
    private final static String[] PERMISSIONS_CAMERA = {
            Manifest.permission.CAMERA
    };

    /* 接口 */
    private Interface_MessageSend interface_messageSend;



    /* 监听器 */
    /**
     * 提示 图片上传大小 Get眨眼动画
     */
    private final View.OnClickListener rlHintListener = v ->
            toast.makeEyeToast(context, "请上传小于 5MB 的图片");
    /**
     * 提示 鸿蒙系统无法上传图片 Get眨眼动画
     */
    private final View.OnClickListener ivHintListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            toast.makeEyeToast(context, "鸿蒙系统因权限问题暂时无法使用上传功能");
        }
    };
    /**
     * 打开底部弹窗
     */
    private final View.OnClickListener btUpdataListener = v -> dialog.show();
    /**
     * 底部弹窗 - 打开相机
     */
    private final View.OnClickListener dialogTvCameraListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CAMERA)) {
                    requestPermissions(PERMISSIONS_CAMERA, REQUEST_CAMERA);
                }
            } else {
                openCamera();
                dialog.dismiss();
            }
        }
    };
    /**
     * 底部弹窗 - 打开相册
     */
    private final View.OnClickListener dialogTvAlbumListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    requestPermissions(PERMISSIONS_EXTERNAL_STORAGE, REQUEST_EXTERNAL_STORAGE);
                }
            } else {
                openAlbum();
                dialog.dismiss();
            }
        }
    };
    /**
     * 底部弹窗 - 取消
     */
    private final View.OnClickListener dialogTvCancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialog.dismiss();
        }
    };


    /**
     * 打开相机
     */
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ContentValues values = new ContentValues();
        cameraPhotoUri = requireActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraPhotoUri);
        startActivityForResult(intent, INTENT_OPEN_CAMERA);
    }

    /**
     * 打开相册
     */
    private void openAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, INTENT_OPEN_ALBUM);
    }

    /**
     * 初始化
     *
     * @param root 根视图
     */
    private void init(View root) {
        BottomNavigationView nav = requireActivity().findViewById(R.id.nav_view);
        nav.setVisibility(View.VISIBLE);

        toast = new Customize_Toast(root);

        // 初始化布局控件
        ImageView ivHint = root.findViewById(R.id.iv_photo_share_hint);
        ivHint.setOnClickListener(ivHintListener);

        ImageButton btUpload = root.findViewById(R.id.bt_photo_share_updata);
        btUpload.setOnClickListener(btUpdataListener);

        RelativeLayout rlHint = root.findViewById(R.id.rl_photo_share_text);
        rlHint.setOnClickListener(rlHintListener);

        // 初始化对话框
        dialog = new Dialog(requireContext(), R.style.BottomDialog);
        View view = View.inflate(requireContext(), R.layout.dialog_share_choose, null);
        dialog.setContentView(view);

        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.BottomDialog);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView tvCamera = dialog.findViewById(R.id.dialog_share_choose_camera);
        TextView tvAlbum = dialog.findViewById(R.id.dialog_share_choose_album);
        TextView tvCancel = dialog.findViewById(R.id.dialog_share_choose_cancel);

        tvCamera.setOnClickListener(dialogTvCameraListener);
        tvAlbum.setOnClickListener(dialogTvAlbumListener);
        tvCancel.setOnClickListener(dialogTvCancelListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == REQUEST_EXTERNAL_STORAGE) {
                dialog.dismiss();
                openAlbum();
            }
            if (requestCode == REQUEST_CAMERA) {
                dialog.dismiss();
                openCamera();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == INTENT_OPEN_ALBUM) {
                interface_messageSend.sendPhotoUri(data.getData());
                Navigation.findNavController(requireView()).navigate(R.id.action_navigation_photo_share_to_fragment_PhotoShareUpload);
            } else if (requestCode == INTENT_OPEN_CAMERA) {
                interface_messageSend.sendPhotoUri(cameraPhotoUri);
                Navigation.findNavController(requireView()).navigate(R.id.action_navigation_photo_share_to_fragment_PhotoShareUpload);
            }
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_photo_share, container, false);
        context = getActivity();
        init(root);
        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            interface_messageSend = (Interface_MessageSend) context;
        } catch (ClassCastException e) {
            throw new ClassCastException();
        }
    }
}