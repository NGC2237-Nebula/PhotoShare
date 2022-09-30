package com.example.photoshare.entity;

import com.google.gson.annotations.SerializedName;

/*
    {
        id:"31"
        appKey:"28f6422b72374dc193ce88d5c9ed3d03"
        pUserId:"1552956093590802432"
        userName:"1234"
        shareId:"116"
        parentCommentId:null
        parentCommentUserId:null
        replyCommentId:null
        replyCommentUserId:null
        commentLevel:1
        content:"我把星星撒向大海，从此大海就是我的银河"
        status:1
        praiseNum:0
        topStatus:0
        createTime:"2022-08-26 05:34:46"
    }
 */

public class Entity_Comment {
    @SerializedName("id")
    private String mId;
    public String getId() {
        return mId;
    }

    @SerializedName("appKey")
    private String mAppKey;
    public String getAppKey() {
        return mAppKey;
    }

    @SerializedName("pUserId")
    private String pUserId;
    public String getPUserId() {
        return pUserId;
    }

    @SerializedName("userName")
    private String username;
    public String getUsername() {
        return username;
    }

    @SerializedName("shareId")
    private String shareId;
    public String getShareId() {
        return shareId;
    }

    @SerializedName("parentCommentId")
    private String parentCommentId;
    public String getParentCommentId() {
        return parentCommentId;
    }

    @SerializedName("parentCommentUserId")
    private String parentCommentUserId;
    public String getParentCommentUserId() {
        return parentCommentUserId;
    }

    @SerializedName("replyCommentId")
    private String replyCommentId;
    public String getReplyCommentId() {
        return replyCommentId;
    }

    @SerializedName("replyCommentUserId")
    private String replyCommentUserId;
    public String getReplyCommentUserId() {
        return replyCommentUserId;
    }

    @SerializedName("commentLevel")
    private int commentLevel;
    public int getCommentLevel() {
        return commentLevel;
    }

    @SerializedName("content")
    private String content;
    public String getContent() {
        return content;
    }

    @SerializedName("status")
    private int status;
    public int getStatus() {
        return status;
    }

    @SerializedName("praiseNum")
    private int praiseNum;
    public int getPraiseNum() {
        return praiseNum;
    }

    @SerializedName("topStatus")
    private int topStatus;
    public int getTopStatus() {
        return topStatus;
    }

    @SerializedName("createTime")
    private String createTime;
    public String getCreateTime() {
        return createTime;
    }

    private boolean isHost = false;
    public boolean getIsHost(){
        return isHost;
    }
    public void setIsHost(boolean isHost){
        this.isHost = isHost;
    }
}
