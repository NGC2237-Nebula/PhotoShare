package com.example.photoshare.entity;

import com.google.gson.annotations.SerializedName;

/*
        {
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


public class Entity_User {
    @SerializedName("id")
    private String mId;
    public String getId() {
        return mId;
    }

    @SerializedName("appKey")
    private String appKey;
    public String getAppKey() {
        return appKey;
    }

    @SerializedName("username")
    private String username;
    public String getUsername() {
        return username;
    }

    @SerializedName("password")
    private String password;
    public String getPassword() {
        return password;
    }

    @SerializedName("sex")
    private int sex;
    public int getSex() {
        return sex;
    }

    @SerializedName("introduce")
    private String introduce;
    public String getIntroduce() {
        return introduce;
    }

    @SerializedName("avatar")
    private String avatar;
    public String getAvatar() {
        return avatar;
    }

    @SerializedName("createTime")
    private String createTime;
    public String getCreateTime() {
        return createTime;
    }

    @SerializedName("lastUpdateTime")
    private String lastUpdateTime;
    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

}
