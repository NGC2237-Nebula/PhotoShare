package com.example.photoshare.entity;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;


/*
        id:"67"
        pUserId:"1552956093590802432"
        imageCode:"1555205045455294464"
        title:"雪山"
        content:"山海相隔的朝暮 是我们每个人的朝暮 以此启程 永远有远方可以奔赴."
        createTime:"1659624975811"
        imageUrlList:[ 0:"https://guet-lab.oss-cn-hangzhou.aliyuncs.com/api/2022/08/04/风景 (68).jpg" ]
        likeId:null
        likeNum:4
        hasLike:false
        collectId:null
        collectNum:2
        hasCollect:false
        hasFocus:false
        username:"1234"
*/


public class Entity_Photo {
    @SerializedName("id")
    private String mId;
    public String getId() {
        return mId;
    }
    public void setId(String mId) {
        this.mId = mId;
    }

    @SerializedName("pUserId")
    private String pUserId;
    public String getPUserId() {
        return pUserId;
    }
    public void setPUserId(String pUserId) {
        this.pUserId = pUserId;
    }

    @SerializedName("imageCode")
    private String imageCode;
    public String getImageCode() {
        return imageCode;
    }
    public void setImageCode(String imageCode) {
        this.imageCode = imageCode;
    }

    @SerializedName("title")
    private String title;
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    @SerializedName("content")
    private String content;
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    @SerializedName("createTime")
    private String createTime;
    public String getCreateTime() {
        return createTime;
    }
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @SerializedName("imageUrlList")
    private String[] imageUrlList;
    public String[] getImageUrlList() {
        return imageUrlList;
    }
    public void setImageUrlList(String[] imageUrlList) {
        this.imageUrlList = imageUrlList;
    }

    @SerializedName("likeId")
    private String likeId;
    public String getLikeId() {
        return likeId;
    }
    public void setLikeId(String likeId) {
        this.likeId = likeId;
    }

    @SerializedName("likeNum")
    private int likeNum;
    public int getLikeNum() {
        return likeNum;
    }
    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }

    @SerializedName("hasLike")
    private boolean hasLike;
    public boolean getHasLike() {
        return hasLike;
    }
    public void setHasLike(boolean hasLike) {
        this.hasLike = hasLike;
    }

    @SerializedName("collectId")
    private String collectId;
    public String getCollectId() {
        return collectId;
    }
    public void setCollectId(String collectId) {
        this.collectId = collectId;
    }

    @SerializedName("collectNum")
    private int collectNum;
    public int getCollectNum() {
        return collectNum;
    }
    public void setCollectNum(int collectNum) {
        this.collectNum = collectNum;
    }

    @SerializedName("hasCollect")
    private boolean hasCollect;
    public boolean getHasCollect() {
        return hasCollect;
    }
    public void setHasCollect(boolean hasCollect) {
        this.hasCollect = hasCollect;
    }

    @SerializedName("username")
    private String username;
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    @NonNull
    @Override
    public String toString() {
        return "id : " + mId + "\npUserId : " + pUserId +
                "\nimageCode : " + imageCode + "\ntitle : " + title +
                "\ncontent : " + content + "\ncreateTime : " + createTime +
                "\nimageUrlList : " + imageUrlList[0] + "\nusername : " + username;
    }
}
