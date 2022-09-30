package com.example.photoshare.parse;

import com.example.photoshare.entity.Entity_Photo;
import com.google.gson.annotations.SerializedName;

public class Response_PhotoDetails {

    @SerializedName("code")
    private int code;
    public int getCode() { return code; }

    @SerializedName("msg")
    private String msg;
    public String getMsg() { return msg; }

    @SerializedName("data")
    private Entity_Photo data;
    public Entity_Photo getData() { return data; }
}

/* 获取单个图文分享的详情 */
/*
{
    code:200
    msg:"成功"
    data:
        {
            id:"62"
            pUserId:"1552956093590802432"
            imageCode:"1553661280865357824"
            title:"水草"
            content:"日落与水草"
            createTime:"1659256732105"
            imageUrlList:[ 0:"https://guet-lab.oss-cn-hangzhou.aliyuncs.com/api/2022/07/31/test3.jpg" ]
            likeId:"44"
            likeNum:2
            hasLike:true
            collectId:"26"
            collectNum:3
            hasCollect:true
            hasFocus:false
            username:"1234"
        }
}
*/