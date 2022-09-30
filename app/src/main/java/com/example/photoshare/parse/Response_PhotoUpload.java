package com.example.photoshare.parse;

import com.google.gson.annotations.SerializedName;

/*
{
        "msg": "string",
        "code": 0,
        "data": {
            "imageCode": 0,
            "imageUrlList": [ {} ]
            }
        }
*/

public class Response_PhotoUpload {
    @SerializedName("code")
    private int code;
    public int getCode() { return code; }

    @SerializedName("msg")
    private String msg;
    public String getMsg() { return msg; }

    @SerializedName("data")
    private Data data;
    public Data getData() { return data; }

    public static class Data {
        @SerializedName("imageCode")
        private String imageCode;
        public String getImageCode() { return imageCode; }

        @SerializedName("imageUrlList")
        private String[] imageUrlList;
        public String[] getImageUrlList() { return imageUrlList; }
    }
}
