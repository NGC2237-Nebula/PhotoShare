package com.example.photoshare.parse;

import com.example.photoshare.entity.Entity_Photo;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Response_PhotoList {

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
        @SerializedName("records")
        private ArrayList<Entity_Photo> records;
        public ArrayList<Entity_Photo> getRecords() { return records; }

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

/* 获取图片分享发现列表 */
/*
{
    "code": 200,
    "msg": "成功",
    "data": {
        "records": [
                        {
                        "id": "59",
                        "pUserId": "1552956093590802432",
                        "imageCode": "1553348067254734848",
                        "title": "背影",
                        "content": "香港街头（黑白）",
                        "createTime": "1659182215416",
                        "imageUrlList": [ "https://guet-lab.oss-cn-hangzhou.aliyuncs.com/api/2022/07/30/1 (9).png" ],
                        "likeId": null,
                        "likeNum": null,
                        "hasLike": false,
                        "collectId": null,
                        "collectNum": null,
                        "hasCollect": false,
                        "hasFocus": false,
                        "username": "1234"
                        },
                        {
                        "id": "57",
                        "pUserId": "1552956093590802432",
                        "imageCode": "1553333615453147136",
                        "title": "1",
                        "content": "test",
                        "createTime": "1659179916246",
                        "imageUrlList": [ "https://guet-lab.oss-cn-hangzhou.aliyuncs.com/api/2022/07/30/1 (13).jpg" ],
                        "likeId": null,
                        "likeNum": null,
                        "hasLike": false,
                        "collectId": null,
                        "collectNum": null,
                        "hasCollect": false,
                        "hasFocus": false,
                        "username": "1234"
                       },
                       {
                        "id": "56",
                        "pUserId": "1552956093590802432",
                        "imageCode": "1553333615453147136",
                        "title": "1",
                        "content": "test",
                        "createTime": "1659178644218",
                        "imageUrlList": [ "https://guet-lab.oss-cn-hangzhou.aliyuncs.com/api/2022/07/30/1 (13).jpg" ],
                        "likeId": null,
                        "likeNum": null,
                        "hasLike": false,
                        "collectId": null,
                        "collectNum": null,
                        "hasCollect": false,
                        "hasFocus": false,
                        "username": "1234"
                       }
                ],
        "total": 3,
        "size": 10,
        "current": 1
    }
}
 */
