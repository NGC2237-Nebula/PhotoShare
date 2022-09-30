package com.example.photoshare.parse;

import com.example.photoshare.entity.Entity_User;
import com.google.gson.annotations.SerializedName;

/* 用户登录、用户注册成功、用户注册失败、用户信息修改成功 */

public class Response_UserGeneral {
    @SerializedName("code")
    private int code;
    public int getCode() { return code; }

    @SerializedName("msg")
    private String msg;
    public String getMsg() { return msg; }

    @SerializedName("data")
    private Entity_User user;
    public Entity_User getUser() { return user; }
}


/* 用户登录 */
/*
{
    "code": 200,
    "msg": "登录成功",
    "data": {
                "id": "1552956093590802432",
                "appKey": "28f6422b72374dc193ce88d5c9ed3d03",
                "username": "1234",
                "password": null,
                "sex": null,
                "introduce": null,
                "avatar": null,
                "createTime": "1659088559898",
                "lastUpdateTime": "1659088559898"
            }
}
 */

/* 用户注册成功 */
/*
{
    code:200
    msg:null
    data:null
}
*/

/* 用户注册失败 */
/*
{
    code:500
    msg:"username 不能为空"
    data:null
}
 */

/* 用户信息修改成功 */
/*
{
    code:200
    msg:null
    data:null
}
*/