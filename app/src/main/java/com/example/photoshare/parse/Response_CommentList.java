package com.example.photoshare.parse;

import com.example.photoshare.entity.Entity_Comment;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Response_CommentList {
    @SerializedName("code")
    private int code;
    public int getCode() { return code; }

    @SerializedName("msg")
    private String msg;
    public String getMsg() { return msg; }

    @SerializedName("data")
    private Response_CommentList.Data data;
    public Response_CommentList.Data getData() { return data; }

    static public class Data {
        @SerializedName("records")
        private ArrayList<Entity_Comment> records;
        public ArrayList<Entity_Comment> getRecords() { return records; }

        @SerializedName("total")
        private int total;
        public int getTotal() { return total; }

        @SerializedName("size")
        private int size;
        public int getSize() { return size; }

        @SerializedName("current")
        private int current;
        public int getCurrent() { return current; }
    }
}


/*
{
  "code": 200,
  "msg": "成功",
  "data": {
    "records": [
      {
        "id": "31",
        "appKey": "28f6422b72374dc193ce88d5c9ed3d03",
        "pUserId": "1552956093590802432",
        "userName": "1234",
        "shareId": "116",
        "parentCommentId": null,
        "parentCommentUserId": null,
        "replyCommentId": null,
        "replyCommentUserId": null,
        "commentLevel": 1,
        "content": "我把星星撒向大海，从此大海就是我的银河",
        "status": 1,
        "praiseNum": 0,
        "topStatus": 0,
        "createTime": "2022-08-26 05:34:46"
      },
      {
        "id": "29",
        "appKey": "28f6422b72374dc193ce88d5c9ed3d03",
        "pUserId": "1552956093590802432",
        "userName": "1234",
        "shareId": "116",
        "parentCommentId": null,
        "parentCommentUserId": null,
        "replyCommentId": null,
        "replyCommentUserId": null,
        "commentLevel": 1,
        "content": "你说星星睡不着的时候会不会数人类啊？",
        "status": 1,
        "praiseNum": 0,
        "topStatus": 0,
        "createTime": "2022-08-25 08:51:20"
      }
    ],
    "total": 2,
    "size": 10,
    "current": 1
  }
}

*/
