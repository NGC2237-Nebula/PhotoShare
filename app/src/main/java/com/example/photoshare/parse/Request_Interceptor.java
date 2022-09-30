package com.example.photoshare.parse;

import com.example.photoshare.constant.Constant_APP;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class Request_Interceptor implements Interceptor {
    /**
     * APP ID
     */
    String appId = Constant_APP.APP_ID;
    /**
     * APP SECRET
     */
    String appSecret = Constant_APP.APP_SECRET;

    /**
     * 请求体添加 appId、appSecret 信息
     */
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request().newBuilder()
                .addHeader("appId", appId)
                .addHeader("appSecret", appSecret)
                .build();
        return chain.proceed(request);
    }
}
