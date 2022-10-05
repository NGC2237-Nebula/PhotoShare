package com.example.photoshare.interfaces;

import android.net.Uri;

import com.example.photoshare.entity.Entity_Comment;
import com.example.photoshare.entity.Entity_Photo;
import com.example.photoshare.entity.Entity_User;

import java.io.File;
import java.util.ArrayList;

public interface Interface_MessageSend {

    /* Fragment_ShareUploadPhoto */
    /**
     * 刷新后的用户信息
     */
    void sendUser(Entity_User user);

    /* Fragment_ShareUploadPhoto */
    /**
     * 传送上传成功的 imageCode
     */
    void sendImageCode(String imageCode);
    /**
     * 传送上传成功的 imageUrlList
     */
    void sendImageFileList(ArrayList<File> imageFileList);


    /* Fragment_Share */
    /**
     *  传送选择的图片的 URI
     */
    void sendPhotoUri(Uri uri);

    /* Fragment_CommentFirst */
    /**
     * 发送一级级评论 进行缓存
     */
    void sendClickFirstComment(Entity_Comment comment);
    /**
     * 发送点击的一级级评论 转到二级评论界面
     */
    void sendFirstCommentList(StringBuffer comment, String photoId);

    /* Fragment_CommentSecond */
    /**
     * 发送二级评论 进行缓存
     */
    void sendSecondCommentList(StringBuffer comment, String photoId ,String FirstCommentId);

    /* Fragment_PhotoDetails */
    /**
     * 发送被点击的图片位置 转到图片放大页
     */
    void sendViewPagerClickPosition(int position);


    /* Fragment_PhotoExploreList */
    /**
     * 发送被点击的图片位置 转到图片详情页
     */
    void sendClickPhoto(Entity_Photo photo, int clickPhotoPosition);
    /**
     * 发送网络请求获得的图片列表 进行缓存
     */
    void sendAllPhotoList(ArrayList<Entity_Photo> photoList);
    /**
     * 在 Fragment_PhotoDetails 点赞后修改 allPhotoList 中对应 Photo 的点赞状态
     */
    void setPhotoLikeState(int position, boolean isLike);
    /**
     * 在 Fragment_PhotoDetails 收藏后修改 allPhotoList 中对应 Photo 的收藏状态
     */
    void setPhotoCollectState(int position, boolean isCollect);
    /**
     * 发送被查询到的图文ID列表
     */
    void sendQuireContent(ArrayList<Entity_Photo> photoList);


    /* Fragment_PersonAvatarModify */
    /**
     * 用户选择的头像图片Uri
     */
    void sendModifyAvatar(String avatarUri);
}
