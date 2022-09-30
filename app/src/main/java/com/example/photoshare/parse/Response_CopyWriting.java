package com.example.photoshare.parse;

import com.example.photoshare.entity.Entity_CopyWriting;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Response_CopyWriting {

    @SerializedName("code")
    private int code;
    public int getCode() { return code; }

    @SerializedName("msg")
    private String msg;
    public String getMsg() { return msg; }

    @SerializedName("newslist")
    private ArrayList<Entity_CopyWriting> newslist;
    public ArrayList<Entity_CopyWriting> getNewsList() { return newslist; }
}

/*
{
  "code": 200,
  "msg": "success",
  "newslist": [
    {
      "content": "每个人都有属于自己的时刻表，别让任何人打乱你人生的节奏。",
      "source": "佚名"
    }
  ]
}
 */

