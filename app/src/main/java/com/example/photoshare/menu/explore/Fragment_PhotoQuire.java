package com.example.photoshare.menu.explore;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.photoshare.Activity_Menu;
import com.example.photoshare.R;
import com.example.photoshare.entity.Entity_Photo;
import com.example.photoshare.interfaces.Interface_MessageSend;

import java.util.ArrayList;

public class Fragment_PhotoQuire extends Fragment {

    /* 控件 */
    private ListView lvPhotoList;

    /* 接口 */
    private Interface_MessageSend interface_messageSend;

    /* 数据 */
    /**
     * 图片适配器
     */
    private Adapter_PhotoExploreList photoAdapter;

    /* 监听器 */
    /**
     * 列表布局 监听器 - 将点击的图片信息传到Activity
     */
    private final ListView.OnItemClickListener lvPhotoListListener = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long l) {
            Entity_Photo photo = photoAdapter.getItem(position);
            interface_messageSend.sendClickPhoto(photo,position);
            Navigation.findNavController(requireView()).navigate(R.id.action_fragment_PhotoQuire_to_fragment_PhotoDetails);
        }
    };
    /**
     * 返回上一个界面
     */
    private final View.OnClickListener rlBackListener = v ->
            Navigation.findNavController(requireView()).popBackStack();


    private void bindView(View root){
        RelativeLayout rlBack = root.findViewById(R.id.rl_photo_quire_head_close);
        rlBack.setOnClickListener(rlBackListener);

        lvPhotoList = root.findViewById(R.id.lv_photo_quire_list);
        lvPhotoList.setOnItemClickListener(lvPhotoListListener);
    }

    private void setData(Context context){
        ArrayList<Entity_Photo> photoList = ((Activity_Menu) context).getQuirePhotoList();

        // 图文列表
        photoAdapter = new Adapter_PhotoExploreList(context, R.layout.item_photo_explore_list, photoList);
        lvPhotoList.setAdapter(photoAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_photo_quire, container, false);
        Context context = getActivity();

        bindView(root);
        setData(context);

        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            interface_messageSend = (Interface_MessageSend) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Interface not implemented");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        interface_messageSend.sendQuireContent(null);
    }
}