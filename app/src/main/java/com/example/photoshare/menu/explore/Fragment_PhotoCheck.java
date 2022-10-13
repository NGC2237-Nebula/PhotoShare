package com.example.photoshare.menu.explore;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.photoshare.Activity_Menu;
import com.example.photoshare.entity.Entity_Photo;
import com.example.photoshare.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Fragment_PhotoCheck extends Fragment {

    /* 数据 */
    private int position;
    private Entity_Photo photo;
    private Bitmap photoBitmap;

    private final DisplayMetrics dm = new DisplayMetrics();

    /**
     * 最小缩放比例
     */
    private float minScale;
    /**
     * 最大缩放比例
     */
    private float maxScale;

    /**
     * 当前 Matrix
     */
    private final Matrix matrix = new Matrix();
    /**
     * 暂存 Matrix
     */
    private final Matrix savedMatrix = new Matrix();

    /**
     * 首次按下点的坐标
     */
    private final PointF prePoint = new PointF();
    /**
     * 中间点的坐标
     */
    private final PointF midPoint = new PointF();
    /**
     * 再次按下点的坐标
     */
    private final PointF postPoint = new PointF();

    /**
     * 两手指首次按下后的距离
     */
    private float preDist = 1f;
    /**
     * 两手指再次按下后的距离
     */
    private float postDist = 1f;


    /* 控件 */
    private ImageView ivPhoto;
    private TextView tvHint;
    private CardView cvDownload;
    private CardView cvShare;
    private BottomNavigationView nav;

    /* 标识符 */
    private final int NET_REQUEST_SAVE = 1;
    private final int NET_REQUEST_SHOW = 2;
    /**
     * 初始模式
     */
    private static final int NONE = 0;
    /**
     * 拖动模式
     */
    private static final int DRAG = 1;
    /**
     * 缩放模式
     */
    private static final int ZOOM = 2;
    /**
     * 当前模式
     */
    private int MODE = NONE;

    /* 权限 */
    private final int REQUEST_EXTERNAL_STORAGE = 1;
    private final static String[] PERMISSIONS_EXTERNAL_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };





    /* 监听器 */
    /**
     * 返回 上一个界面
     */
    private final View.OnClickListener ivArrowListener = v -> {
        nav.setVisibility(View.VISIBLE);
        Navigation.findNavController(requireView()).popBackStack();
    };
    /**
     * 点击下载图片到本地
     */
    private final View.OnClickListener cvDownloadListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    requestPermissions(PERMISSIONS_EXTERNAL_STORAGE, REQUEST_EXTERNAL_STORAGE);
                }
            } else {
                if (photoBitmap != null) saveWebPhoto(photoBitmap);
                else requestWebPhotoBitmap(photo.getImageUrlList()[position], NET_REQUEST_SAVE);
            }
        }
    };
    /**
     * 点击分享图片
     */
    private final View.OnClickListener cvShareListener = v -> shareWebPhotoString();
    /**
     * 图片触屏监听
     */
    private final View.OnTouchListener ivPhotoTouchListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {

                // 主点按下 - 设置模式为移动模式
                case MotionEvent.ACTION_DOWN:
                    savedMatrix.set(matrix);
                    prePoint.set(event.getX(), event.getY());
                    MODE = DRAG;
                    break;

                // 副点按下 - 设置模式为缩放模式
                case MotionEvent.ACTION_POINTER_DOWN:
                    preDist = calculateDistance(event);
                    // 如果连续两点距离大于10，判定为多点模式
                    if (calculateDistance(event) > 10f) {
                        savedMatrix.set(matrix);
                        calculateMidPoint(midPoint, event);
                        MODE = ZOOM;
                    }
                    break;

                // 手指抬起 - 重新设置模式为初始模式
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    MODE = NONE;
                    break;

                // 手指移动 - 判断模式并进行相应操作
                case MotionEvent.ACTION_MOVE:
                    if (MODE == DRAG) {
                        matrix.set(savedMatrix);
                        postPoint.set(event.getX(),event.getY());
                        matrix.preTranslate(postPoint.x - prePoint.x, postPoint.y - prePoint.y); // 计算平移距离进行位移
                    }
                    else if (MODE == ZOOM) {
                        postDist = calculateDistance(event);
                        if (postDist > 10f) {
                            matrix.set(savedMatrix);
                            float tScale = postDist / preDist; // 计算缩放比例
                            matrix.postScale(tScale, tScale, midPoint.x, midPoint.y); // 以 midPoint 为中心的进行缩放
                        }
                    }
                    break;
            }
            ivPhoto.setImageMatrix(matrix);
            limitScale();
            return true;
        }
    };



    /* 图片触屏控制 相关函数 */
    /**
     * 限制最大最小缩放比例，同时自动居中
     */
    private void limitScale() {
        float[] param = new float[9];
        matrix.getValues(param); // 将 matrix 中的数值拷贝进 param 的前9位中
        if (MODE == ZOOM) {
            if (param[0] < minScale)
                matrix.setScale(minScale, minScale); // 设置缩放到matrix中，sx，sy代表缩放的倍数
            if (param[0] > maxScale) matrix.set(savedMatrix);
        }
        centerPhoto(true, true);
    }

    /**
     * 居中图片
     *
     * @param centerHorizontal 是否水平居中
     * @param centerVertical   是否垂直居中
     */
    private void centerPhoto(boolean centerHorizontal, boolean centerVertical) {

        RectF rect = new RectF(0, 0, photoBitmap.getWidth(), photoBitmap.getHeight());
        matrix.mapRect(rect); // 测量rect经过矩阵matrix变换后的位置,将结果放回rect中

        float height = rect.height();
        float width = rect.width();

        float dx = 0;
        float dy = 0;

        if (centerVertical) {
            int screenHeight = dm.heightPixels;
            // 图片小于屏幕，计算图片居中显示时上边需要偏移的偏移量。
            if (height < screenHeight) {
                dy = (screenHeight - height) / 2 - rect.top;
            }
            // 图片大于屏幕，上边留空则往上移，下边留空则往下移
            else if (rect.top > 0) {
                dy = -rect.top;
            } else if (rect.bottom < screenHeight) {
                dy = ivPhoto.getHeight() - rect.bottom;
            }
        }
        if (centerHorizontal) {
            int screenWidth = dm.widthPixels;
            // 图片小于屏幕，，计算图片居中显示时左边需要偏移的偏移量。
            if (width < screenWidth) {
                dx = (screenWidth - width) / 2 - rect.left;
            }
            // 图片大于屏幕，左边留空则往上移，右边留空则往下移
            else if (rect.left > 0) {
                dx = -rect.left;
            } else if (rect.right < screenWidth) {
                dx = screenWidth - rect.right;
            }
        }
        matrix.postTranslate(dx, dy);
        // [minScale,0,0]    [minScale,0,dx]
        // [0,minScale,0] => [0,minScale,dy]
        // [0,   0,    1]    [0,    0,    1]
    }

    /**
     * 设置最小缩放比例 - 图片缩放最小为屏幕宽度/高度
     */
    private void initMinScale() {
        // 计算 最小缩放比例
        minScale = Math.min(
                (float) dm.widthPixels / (float) photoBitmap.getWidth(),
                (float) dm.heightPixels / (float) photoBitmap.getHeight());
        // 如果 缩放比例小于1 ,即图片大小超过屏幕大小,缩放图片为屏幕大小
        if (minScale < 1.0) {
            matrix.postScale(minScale, minScale);
            // [1,0,0]    [minScale,0,0]
            // [0,1,0] => [0,minScale,0]
            // [0,0,1]    [0,   0,    1]
        }
    }

    /**
     * 设置最大缩放比例 - 图片缩放最大为原图的4倍
     */
    private void initMaxScale() {
        maxScale = 4f;
    }

    /**
     * 计算两点 距离
     */
    private float calculateDistance(MotionEvent event) {
        float dx = event.getX(0) - event.getX(1);
        float dy = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * 计算两点 中点
     */
    private void calculateMidPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }



    /* 网络图片获取、展示、保存 相关函数 */
    /**
     * 根据 URL 网络请求获取图片 Bitmap 格式,进行后续操作
     *
     * @param url  网络图片 url
     * @param type 后续操作类型码
     */
    private void requestWebPhotoBitmap(String url, int type) {
        Glide.with(requireContext()).asBitmap().load(url).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                photoBitmap = resource;
                if (type == NET_REQUEST_SHOW) showWebPhoto(resource);
                else if (type == NET_REQUEST_SAVE) saveWebPhoto(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
            }
        });
    }

    /**
     * 展示网络图片，设置触屏事件
     *
     * @param bitmap 网络图片 Bitmap
     */
    @SuppressLint("ClickableViewAccessibility")
    private void showWebPhoto(Bitmap bitmap) {
        cvDownload.setVisibility(View.VISIBLE);
        cvShare.setVisibility(View.VISIBLE);

        ivPhoto.setImageBitmap(bitmap);
        ivPhoto.setOnTouchListener(ivPhotoTouchListener);

        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);// 获取分辨率
        initMinScale();
        initMaxScale();
        centerPhoto(true, true);
        ivPhoto.setImageMatrix(matrix); // 将计算后的居中设置到图片中
    }

    /**
     * 保存网络图片到本地
     *
     * @param bitmap 网络图片 Bitmap
     */
    private void saveWebPhoto(Bitmap bitmap) {
        Toast.makeText(getContext(), "开始下载", Toast.LENGTH_SHORT).show();

        // 获取当前时间作为图片名称
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar calendar = Calendar.getInstance();
        String photoName = sdf.format(calendar.getTime());

        // 保存到本地
        File pathFile = new File(Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_PICTURES + File.separator);
        if (!pathFile.exists()) pathFile.mkdir();
        File file = new File(pathFile, photoName + ".jpg");

        try {
            FileOutputStream fos = new FileOutputStream(file);
            boolean isSuccess = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

            // 发送广播通知系统图库刷新数据
            Uri localUri = Uri.fromFile(file);
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
            requireActivity().sendBroadcast(intent);

            if (isSuccess) {
                Toast.makeText(getContext(), "已保存至 " + pathFile + " 目录下", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(requireContext(), "保存失败,请检查网络", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 分享网络图片 String
     */
    private void shareWebPhotoString() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "" + photo.getImageUrlList()[position]);
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, "分享到"));
    }

    /**
     * 分享网络图片 Bitmap
     */
    private void shareWebPhotoBitmap(Bitmap bitmap) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(requireActivity().getContentResolver(), bitmap, null,null));
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent, "title"));
    }



    private void bindView(View root) {
        nav = requireActivity().findViewById(R.id.nav_view);
        nav.setVisibility(View.INVISIBLE);

        ImageView ivArrow = root.findViewById(R.id.iv_photo_check_back);
        ivArrow.setOnClickListener(ivArrowListener);

        ivPhoto = root.findViewById(R.id.iv_photo_check_photo);

        cvDownload = root.findViewById(R.id.cv_photo_check_download);
        cvDownload.setOnClickListener(cvDownloadListener);
        cvDownload.setVisibility(View.INVISIBLE);

        cvShare = root.findViewById(R.id.cv_photo_check_share);
        cvShare.setOnClickListener(cvShareListener);
        cvShare.setVisibility(View.INVISIBLE);

        tvHint = root.findViewById(R.id.tv_photo_check_hint);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setData() {
        String hintString = (position + 1) + " / " + photo.getImageUrlList().length;
        tvHint.setText(hintString);
        requestWebPhotoBitmap(photo.getImageUrlList()[position], NET_REQUEST_SHOW);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_photo_check, container, false);
        bindView(root);
        setData();
        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        photo = ((Activity_Menu) context).getClickPhoto();
        position = ((Activity_Menu) context).getClickPosition();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        nav.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (photoBitmap != null) saveWebPhoto(photoBitmap);
                else requestWebPhotoBitmap(photo.getImageUrlList()[position], NET_REQUEST_SAVE);
            }
        }
    }
}