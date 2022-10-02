package com.example.photoshare.menu.explore;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.photoshare.Activity_Menu;
import com.example.photoshare.constant.Constant_APP;
import com.example.photoshare.entity.Entity_Photo;
import com.example.photoshare.interfaces.Interface_MessageSend;
import com.example.photoshare.R;
import com.example.photoshare.parse.Request_Interceptor;
import com.example.photoshare.parse.Response_PhotoList;
import com.example.photoshare.tool.Tool_SQLiteOpenHelper;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Fragment_PhotoExploreList extends Fragment {

    /* 控件 */
    /**
     * 图片列表视图
     */
    private ListView lvPhotoList;
    /**
     * 图片适配器
     */
    private Adapter_PhotoList photoAdapter;
    /**
     * 滑动刷新布局
     */
    private SwipeRefreshLayout swipe;
    /**
     * 搜索框
     */
    private CardView cvQuireBar;
    /**
     * 搜索框 输入条
     */
    private EditText etQuire;
    /**
     * 搜索框衬托背景
     */
    private RelativeLayout rlQuireMask;
    /**
     * 下拉选择框
     */
    private Spinner spQuire;

    /* 数据 */
    /**
     * 标题类型
     */
    private final String quireTitle = "标题";
    /**
     * 内容类型
     */
    private final String quireContent = "内容";
    /**
     * 下拉框集合
     */
    private ArrayList<String> quireTypeList;
    /**
     * 搜索类型
     */
    private String quireType = quireTitle;
    /**
     * 隐藏搜索框
     */
    private final int QUIRE_BAR_HIDE = 1;
    /**
     * 展示搜索框
     */
    private final int QUIRE_BAR_SHOW = 2;
    /**
     * 当前登录用户 ID
     */
    private String userId = null;
    /**
     * 向 Activity_Menu 发送图片列表信息
     */
    private ArrayList<Entity_Photo> photoListSend = null;
    /**
     * 从 Activity_Menu 接收图片列表信息
     */
    private ArrayList<Entity_Photo> photoListGet = null;
    /**
     * 数据库助手
     */
    private Tool_SQLiteOpenHelper helper;
    /**
     * 数据库
     */
    private SQLiteDatabase database;

    /* 接口 */
    private Interface_MessageSend interface_messageSend;


    /* 网络请求 */

    /**
     * 网络请求
     */
    private void networkRequest() {
        Log.d("LOG", "======== 网络请求 ========");
        new Thread(() -> {
            Request request;
            String urlParam = "?" + "userId=" + userId;
            request = new Request.Builder().url(Constant_APP.SHARE_GET_URL + urlParam).get().build();
            try {
                OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Request_Interceptor()).build();
                client.newCall(request).enqueue(photoShowCallback);
            } catch (NetworkOnMainThreadException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    /**
     * 网络请求 获得图片分享列表
     */
    private final okhttp3.Callback photoShowCallback = new okhttp3.Callback() {
        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                Log.d("LOG - 图片发现", "响应体 : " + responseBody);

                addNetDataInAdapter(responseBody);
            }
        }

        @Override
        public void onFailure(@NonNull Call call, IOException e) {
            e.printStackTrace();
        }
    };

    /**
     * 解析网络请求结果，添加到适配器中
     *
     * @param body 网络请求结果
     */
    private void addNetDataInAdapter(String body) {
        new Thread(() -> {
            Gson gson = new Gson();
            Response_PhotoList responseParse = gson.fromJson(body, Response_PhotoList.class);
            if (responseParse.getCode() == 5311) jumpErrorHandler.sendMessage(new Message());
            else {
                if (responseParse.getData() != null) {
                    photoListSend = responseParse.getData().getRecords();
                    interface_messageSend.sendAllPhotoList(photoListSend);
                    for (Entity_Photo photo : photoListSend) {
                        Message addMessage = new Message();
                        addMessage.obj = photo;
                        addPhotoHandler.sendMessage(addMessage);
                    }
                }
            }
        }).start();
    }

    /**
     * 加载 本地数据 到适配器中
     *
     * @param photoList 本地数据
     */
    @SuppressLint("NotifyDataSetChanged")
    private void addLocalDataInAdapter(ArrayList<Entity_Photo> photoList) {
        for (Entity_Photo photo : photoList) {
            photoAdapter.add(photo);
            photoAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 响应操作 - 将图片添加到适配器中
     */
    private final Handler addPhotoHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            photoAdapter.add((Entity_Photo) msg.obj);
            photoAdapter.notifyDataSetChanged();
        }
    };
    /**
     * 响应操作 - 跳转到错误信息界面
     */
    private final Handler jumpErrorHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Navigation.findNavController(requireView()).navigate(R.id.action_navigation_photo_explore_to_fragment_Error);
        }
    };





    /* 监听器 */
    /**
     * 列表布局 监听器 - 将点击的图片信息传到Activity
     */
    private final ListView.OnItemClickListener lvPhotoListListener = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long l) {
            Entity_Photo photo = photoAdapter.getItem(position);
            interface_messageSend.sendClickPhoto(photo, position);
            Navigation.findNavController(requireView()).navigate(R.id.action_navigation_photo_find_to_fragment_PhotoDetails);
        }
    };
    /**
     * 滑动刷新布局 监听器 - 刷新一次
     */
    private final SwipeRefreshLayout.OnRefreshListener swipeListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (!photoAdapter.isEmpty()) photoAdapter.clear();
            networkRequest();
            photoAdapter.notifyDataSetChanged();
            swipe.setRefreshing(false);
        }
    };
    /**
     * 跳转到大图模式
     */
    private final View.OnClickListener ivJumpBigListener = v ->
            Navigation.findNavController(requireView()).navigate(R.id.action_navigation_photo_explore_to_fragment_PhotoExploreBig);
    /**
     * 搜索框展示和隐藏
     */
    private final View.OnClickListener rlShowQuireListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (rlQuireMask.getAlpha() == 0f || cvQuireBar.getAlpha() == 0f) {
                setQuireBarState(QUIRE_BAR_SHOW);
            } else {
                setKeyboardState();
                setQuireBarState(QUIRE_BAR_HIDE);
            }
        }
    };
    /**
     * 搜索框 按回车搜索
     */
    private final TextView.OnEditorActionListener etQuireListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (quireType.equals(quireTitle))
                    searchQuireContent(quireTitle);
                else
                    searchQuireContent(quireContent);
            }
            return false;
        }
    };
    /**
     * 点击 暗色衬托背景 后取消搜索框
     */
    private final View.OnClickListener rlQuireMaskListener = v -> {
        setKeyboardState();
        setQuireBarState(QUIRE_BAR_HIDE);
    };
    /**
     * 选择搜索类型
     */
    private final AdapterView.OnItemSelectedListener spQuireListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            quireType = quireTypeList.get(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };


    /**
     * 通过 SQLite 搜索指定内容
     */
    private void searchQuireContent(String type) {
        String quireString = etQuire.getText().toString();
        if (!quireString.equals("")) {
            ArrayList<Entity_Photo> list;
            if (type.equals(quireTitle))
                list = helper.quireTitleFromTable(database, quireString);
            else
                list = helper.quireContentFromTable(database, quireString);

            if (list != null) {
                interface_messageSend.sendQuireContent(list);
                setKeyboardState();
                Navigation.findNavController(requireView()).navigate(R.id.action_navigation_photo_explore_to_fragment_PhotoQuire);
            } else {
                Toast.makeText(requireContext(), "没有找到相关内容,换个关键词试试", Toast.LENGTH_SHORT).show();
            }
            etQuire.setText("");
        } else {
            Toast.makeText(requireContext(), "输入内容不能为空", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 设置搜索框的展示以及隐藏
     *
     * @param type 搜索框状态
     */
    private void setQuireBarState(int type) {
        if (type == QUIRE_BAR_SHOW) {
            ObjectAnimator.ofFloat(rlQuireMask, "alpha", 0f, 1f).setDuration(300).start();
            ObjectAnimator.ofFloat(cvQuireBar, "alpha", 0f, 1f).setDuration(300).start();
        } else if (type == QUIRE_BAR_HIDE) {
            ObjectAnimator.ofFloat(rlQuireMask, "alpha", 1f, 0f).setDuration(300).start();
            ObjectAnimator.ofFloat(cvQuireBar, "alpha", 1f, 0f).setDuration(300).start();
        }
    }

    /**
     * 隐藏键盘
     */
    private void setKeyboardState() {
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etQuire.getWindowToken(), 0);
    }


    /**
     * 初始化 视图
     *
     * @param root 根视图
     */
    private void bindView(View root) {
        RelativeLayout rlConvert = root.findViewById(R.id.rl_photo_explore_list_convert);
        rlConvert.setOnClickListener(ivJumpBigListener);

        lvPhotoList = root.findViewById(R.id.lv_photo_explore_list);
        lvPhotoList.setOnItemClickListener(lvPhotoListListener);

        swipe = root.findViewById(R.id.sw_photo_explore_list_swipe);
        swipe.setOnRefreshListener(swipeListener);

        // 头顶的探索按钮
        RelativeLayout rlShowQuire = root.findViewById(R.id.rl_photo_explore_list_show_quire_button);
        rlShowQuire.setOnClickListener(rlShowQuireListener);

        // 搜索框
        cvQuireBar = root.findViewById(R.id.cv_photo_explore_list_quire_bar);

        // 搜索框 可编辑文本框
        etQuire = root.findViewById(R.id.et_photo_explore_list_quire);
        etQuire.setOnEditorActionListener(etQuireListener);

        // 搜索框 选择类型
        spQuire = root.findViewById(R.id.sp_photo_explore_list_quire_spinner);

        // 搜索时的衬托背景
        rlQuireMask = root.findViewById(R.id.rl_photo_explore_list_quire_mask);
        rlQuireMask.setOnClickListener(rlQuireMaskListener);
    }

    /**
     * 初始化 数据
     *
     * @param context 上下文
     */
    private void setData(Context context) {
        photoListGet = ((Activity_Menu) context).getAllPhotoList();

        // 图文列表
        List<Entity_Photo> photoList = new ArrayList<>();
        photoAdapter = new Adapter_PhotoList(context, R.layout.item_photo_explore_list, photoList);
        lvPhotoList.setAdapter(photoAdapter);

        if (photoListGet != null) addLocalDataInAdapter(photoListGet);
        else networkRequest();

        // 搜索框
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_head, quireTypeList);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        spQuire.setAdapter(adapter);
        spQuire.setDropDownVerticalOffset(125);
        spQuire.setDropDownHorizontalOffset(-40);
        spQuire.getBackground().setColorFilter(getResources().getColor(R.color.white_d), PorterDuff.Mode.SRC_ATOP);
        spQuire.setOnItemSelectedListener(spQuireListener);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_photo_explore_list, container, false);
        bindView(root);
        setData(getActivity());
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        photoAdapter.clear();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        userId = ((Activity_Menu) context).getUserId();
        try {
            interface_messageSend = (Interface_MessageSend) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Interface not implemented");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 搜索框
        quireTypeList = new ArrayList<>();
        quireTypeList.add(quireTitle);
        quireTypeList.add(quireContent);

        // 数据库
        helper = new Tool_SQLiteOpenHelper(requireContext());
        database = helper.getWritableDatabase();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        helper.close();
        database.close();
    }
}