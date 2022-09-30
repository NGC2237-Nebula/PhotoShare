package com.example.photoshare.constant;

import android.provider.BaseColumns;

/*
        id:"322"
        pUserId:"1552956093590802432"
        imageCode:"1571427313738977280"
        title:"cat"
        content:"假如你把秘密告诉了风，就别怪风把它告诉整片森林。"
        createTime:"1663492466114"
        imageUrlList:[
            0:"https://guet-lab.oss-cn-hangzhou.aliyuncs.com/api/2022/09/18/7f97e443-e5cc-4888-8c2f-680a027a04ab.jpg"
            ]
        likeId:"344"
        likeNum:1
        hasLike:true
        collectId:"321"
        collectNum:1
        hasCollect:true
        hasFocus:false
        username:"1234"
*/


public final class Constant_SQLite {
    public static class PhotoListEntry implements BaseColumns {
        public static final String TABLE_NAME = "tb_photolist";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_PUSERID = "pUserId";
        public static final String COLUMN_NAME_IMAGECODE = "imageCode";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_CONTENT = "content";

        public static final String COLUMN_NAME_IMAGEURLLIST_0 = "imageUrlList0";
        public static final String COLUMN_NAME_IMAGEURLLIST_1 = "imageUrlList1";
        public static final String COLUMN_NAME_IMAGEURLLIST_2 = "imageUrlList2";
        public static final String COLUMN_NAME_IMAGEURLLIST_3 = "imageUrlList3";
        public static final String COLUMN_NAME_IMAGEURLLIST_4 = "imageUrlList4";
        public static final String COLUMN_NAME_IMAGEURLLIST_5 = "imageUrlList5";
        public static final String COLUMN_NAME_IMAGEURLLIST_6 = "imageUrlList6";
        public static final String COLUMN_NAME_IMAGEURLLIST_7 = "imageUrlList7";
        public static final String COLUMN_NAME_IMAGEURLLIST_8 = "imageUrlList8";

        public static final String COLUMN_NAME_LIKEID = "likeId";
        public static final String COLUMN_NAME_LIKENUM = "likeNum";
        public static final String COLUMN_NAME_HASLIKE = "hasLike";
        public static final String COLUMN_NAME_COLLECTID = "collectId";
        public static final String COLUMN_NAME_COLLECTNUM = "collectNum";
        public static final String COLUMN_NAME_HASCOLLECT = "hasCollect";
        public static final String COLUMN_NAME_USERNAME = "username";
    }
}
