package com.example.photoshare.entity;

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

import com.google.gson.annotations.SerializedName;

public class Entity_CopyWriting {
    @SerializedName("content")
    private String content;
    public String getContent() {
        return content;
    }

    @SerializedName("source")
    private String source;
    public String getSource() {
        return source;
    }

}
