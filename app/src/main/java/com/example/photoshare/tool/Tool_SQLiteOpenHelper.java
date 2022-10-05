package com.example.photoshare.tool;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.photoshare.constant.Constant_SQLite;
import com.example.photoshare.entity.Entity_Photo;

import java.util.ArrayList;

public class Tool_SQLiteOpenHelper extends SQLiteOpenHelper {

    public final int FALSE = 0;
    public final int TRUE = 1;

    /**
     * 数据库版本号
     */
    public static final int DATABASE_VERSION = 1;
    /**
     * 数据库名称
     */
    public static final String DATABASE_NAME = "photoShare.db";
    /**
     * 创建表
     */
    private static final String SQL_CREATE_TABLE = "CREATE TABLE " +
            Constant_SQLite.PhotoListEntry.TABLE_NAME + " (" +

            Constant_SQLite.PhotoListEntry._ID + " INTEGER PRIMARY KEY, " +
            Constant_SQLite.PhotoListEntry.COLUMN_NAME_ID + " TEXT, " +
            Constant_SQLite.PhotoListEntry.COLUMN_NAME_PUSERID + " TEXT, " +
            Constant_SQLite.PhotoListEntry.COLUMN_NAME_IMAGECODE + " TEXT, " +
            Constant_SQLite.PhotoListEntry.COLUMN_NAME_TITLE + " TEXT, " +
            Constant_SQLite.PhotoListEntry.COLUMN_NAME_CONTENT + " TEXT, " +

            Constant_SQLite.PhotoListEntry.COLUMN_NAME_IMAGEURLLIST_0 + " TEXT, " +
            Constant_SQLite.PhotoListEntry.COLUMN_NAME_IMAGEURLLIST_1 + " TEXT, " +
            Constant_SQLite.PhotoListEntry.COLUMN_NAME_IMAGEURLLIST_2 + " TEXT, " +
            Constant_SQLite.PhotoListEntry.COLUMN_NAME_IMAGEURLLIST_3 + " TEXT, " +
            Constant_SQLite.PhotoListEntry.COLUMN_NAME_IMAGEURLLIST_4 + " TEXT, " +
            Constant_SQLite.PhotoListEntry.COLUMN_NAME_IMAGEURLLIST_5 + " TEXT, " +
            Constant_SQLite.PhotoListEntry.COLUMN_NAME_IMAGEURLLIST_6 + " TEXT, " +
            Constant_SQLite.PhotoListEntry.COLUMN_NAME_IMAGEURLLIST_7 + " TEXT, " +
            Constant_SQLite.PhotoListEntry.COLUMN_NAME_IMAGEURLLIST_8 + " TEXT, " +

            Constant_SQLite.PhotoListEntry.COLUMN_NAME_LIKEID + " TEXT, " +
            Constant_SQLite.PhotoListEntry.COLUMN_NAME_LIKENUM + " INTEGER, " +
            Constant_SQLite.PhotoListEntry.COLUMN_NAME_HASLIKE + " INTEGER, " +

            Constant_SQLite.PhotoListEntry.COLUMN_NAME_COLLECTID + " TEXT, " +
            Constant_SQLite.PhotoListEntry.COLUMN_NAME_COLLECTNUM + " INTEGER, " +
            Constant_SQLite.PhotoListEntry.COLUMN_NAME_HASCOLLECT + " INTEGER, " +

            Constant_SQLite.PhotoListEntry.COLUMN_NAME_USERNAME + " TEXT " + ")";
    /**
     * 删除表
     */
    private static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + Constant_SQLite.PhotoListEntry.TABLE_NAME;
    /**
     * 清除表数据
     */
    private static final String SQL_CLEAR_TABLE = "DELETE FROM " + Constant_SQLite.PhotoListEntry.TABLE_NAME;


    public Tool_SQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * 创建数据库以及初始化数据
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
    }

    /**
     * 数据库更新操作
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(SQL_DROP_TABLE);
        onCreate(sqLiteDatabase);
    }


    /**
     * 清除表数据
     *
     * @param sqLiteDatabase SQLite数据库
     */
    public void clearTable(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CLEAR_TABLE);
    }

    /**
     * 刷新表数据
     *
     * @param sqLiteDatabase SQLite数据库
     * @param photoArrayList 刷新后的图片数组
     */
    public void refreshTable(SQLiteDatabase sqLiteDatabase, ArrayList<Entity_Photo> photoArrayList) {
        clearTable(sqLiteDatabase);
        setDataInTable(sqLiteDatabase, photoArrayList);
    }


    /**
     * 向数据库中设置数据
     *
     * @param sqLiteDatabase SQLite数据库
     * @param photoArrayList 待设置的图片数组
     */
    public void setDataInTable(SQLiteDatabase sqLiteDatabase, ArrayList<Entity_Photo> photoArrayList) {
        for (int i = 0; i < photoArrayList.size(); i++) {

            /* Bundle <=> HashMap<String, Object>() */
            /* ContentValues <=> HashMap<String,基本类型数据>() */

            ContentValues values = new ContentValues();

            values.put(Constant_SQLite.PhotoListEntry.COLUMN_NAME_ID, photoArrayList.get(i).getId());
            values.put(Constant_SQLite.PhotoListEntry.COLUMN_NAME_PUSERID, photoArrayList.get(i).getId());
            values.put(Constant_SQLite.PhotoListEntry.COLUMN_NAME_IMAGECODE, photoArrayList.get(i).getImageCode());
            values.put(Constant_SQLite.PhotoListEntry.COLUMN_NAME_TITLE, photoArrayList.get(i).getTitle());
            values.put(Constant_SQLite.PhotoListEntry.COLUMN_NAME_CONTENT, photoArrayList.get(i).getContent());

            if (photoArrayList.get(i).getImageUrlList().length > 0)
                values.put(Constant_SQLite.PhotoListEntry.COLUMN_NAME_IMAGEURLLIST_0, photoArrayList.get(i).getImageUrlList()[0]);
            else values.put(Constant_SQLite.PhotoListEntry.COLUMN_NAME_IMAGEURLLIST_0, "");

            if (photoArrayList.get(i).getImageUrlList().length > 1)
                values.put(Constant_SQLite.PhotoListEntry.COLUMN_NAME_IMAGEURLLIST_1, photoArrayList.get(i).getImageUrlList()[1]);
            else values.put(Constant_SQLite.PhotoListEntry.COLUMN_NAME_IMAGEURLLIST_1, "");

            if (photoArrayList.get(i).getImageUrlList().length > 2)
                values.put(Constant_SQLite.PhotoListEntry.COLUMN_NAME_IMAGEURLLIST_2, photoArrayList.get(i).getImageUrlList()[2]);
            else values.put(Constant_SQLite.PhotoListEntry.COLUMN_NAME_IMAGEURLLIST_2, "");

            if (photoArrayList.get(i).getImageUrlList().length > 3)
                values.put(Constant_SQLite.PhotoListEntry.COLUMN_NAME_IMAGEURLLIST_3, photoArrayList.get(i).getImageUrlList()[3]);
            else values.put(Constant_SQLite.PhotoListEntry.COLUMN_NAME_IMAGEURLLIST_3, "");

            if (photoArrayList.get(i).getImageUrlList().length > 4)
                values.put(Constant_SQLite.PhotoListEntry.COLUMN_NAME_IMAGEURLLIST_4, photoArrayList.get(i).getImageUrlList()[4]);
            else values.put(Constant_SQLite.PhotoListEntry.COLUMN_NAME_IMAGEURLLIST_4, "");

            if (photoArrayList.get(i).getImageUrlList().length > 5)
                values.put(Constant_SQLite.PhotoListEntry.COLUMN_NAME_IMAGEURLLIST_5, photoArrayList.get(i).getImageUrlList()[5]);
            else values.put(Constant_SQLite.PhotoListEntry.COLUMN_NAME_IMAGEURLLIST_5, "");

            if (photoArrayList.get(i).getImageUrlList().length > 6)
                values.put(Constant_SQLite.PhotoListEntry.COLUMN_NAME_IMAGEURLLIST_6, photoArrayList.get(i).getImageUrlList()[6]);
            else values.put(Constant_SQLite.PhotoListEntry.COLUMN_NAME_IMAGEURLLIST_6, "");

            if (photoArrayList.get(i).getImageUrlList().length > 7)
                values.put(Constant_SQLite.PhotoListEntry.COLUMN_NAME_IMAGEURLLIST_7, photoArrayList.get(i).getImageUrlList()[7]);
            else values.put(Constant_SQLite.PhotoListEntry.COLUMN_NAME_IMAGEURLLIST_7, "");

            if (photoArrayList.get(i).getImageUrlList().length > 8)
                values.put(Constant_SQLite.PhotoListEntry.COLUMN_NAME_IMAGEURLLIST_8, photoArrayList.get(i).getImageUrlList()[8]);
            else values.put(Constant_SQLite.PhotoListEntry.COLUMN_NAME_IMAGEURLLIST_8, "");

            values.put(Constant_SQLite.PhotoListEntry.COLUMN_NAME_LIKEID, photoArrayList.get(i).getLikeId());
            values.put(Constant_SQLite.PhotoListEntry.COLUMN_NAME_LIKENUM, photoArrayList.get(i).getLikeNum());

            if (photoArrayList.get(i).getHasLike())
                values.put(Constant_SQLite.PhotoListEntry.COLUMN_NAME_HASLIKE, TRUE);
            else values.put(Constant_SQLite.PhotoListEntry.COLUMN_NAME_HASLIKE, FALSE);

            values.put(Constant_SQLite.PhotoListEntry.COLUMN_NAME_COLLECTID, photoArrayList.get(i).getCollectId());
            values.put(Constant_SQLite.PhotoListEntry.COLUMN_NAME_COLLECTNUM, photoArrayList.get(i).getCollectNum());

            if (photoArrayList.get(i).getHasCollect())
                values.put(Constant_SQLite.PhotoListEntry.COLUMN_NAME_HASCOLLECT, TRUE);
            else values.put(Constant_SQLite.PhotoListEntry.COLUMN_NAME_HASCOLLECT, FALSE);

            values.put(Constant_SQLite.PhotoListEntry.COLUMN_NAME_USERNAME, photoArrayList.get(i).getUsername());

            sqLiteDatabase.insert(Constant_SQLite.PhotoListEntry.TABLE_NAME, null, values);
            values.clear();
        }
    }

    /**
     * 从数据库中获取数据
     *
     * @param sqLiteDatabase SQLite数据库
     * @return 获取的图片数组
     */
    public ArrayList<Entity_Photo> getDataFromTable(SQLiteDatabase sqLiteDatabase) {
        Cursor cursor = sqLiteDatabase.query(Constant_SQLite.PhotoListEntry.TABLE_NAME,
                null, null, null, null, null, null);
        if (cursor.getCount() != 0) {
            ArrayList<Entity_Photo> photoList = getListByCursor(cursor);
            cursor.close();
            return photoList;
        } else {
            cursor.close();
            return null;
        }
    }


    /**
     * 查询表数据
     *
     * @param sqLiteDatabase SQLite数据库
     * @param quireContent   查询标题
     * @return 返回符合条件的图文ID列表
     */
    public ArrayList<Entity_Photo> quireTitleFromTable(SQLiteDatabase sqLiteDatabase, String quireContent) {
        Cursor cursor = sqLiteDatabase.query(Constant_SQLite.PhotoListEntry.TABLE_NAME, null,
                Constant_SQLite.PhotoListEntry.COLUMN_NAME_TITLE + " like ?", new String[]{"%" + quireContent + "%"},
                null, null, null);

        if (cursor.getCount() != 0) {
            ArrayList<Entity_Photo> photoList = getListByCursor(cursor);
            cursor.close();
            return photoList;
        } else {
            cursor.close();
            return null;
        }
    }


    /**
     * 查询表数据
     *
     * @param sqLiteDatabase SQLite数据库
     * @param quireContent   查询内容
     * @return 返回符合条件的图文ID列表
     */
    public ArrayList<Entity_Photo> quireContentFromTable(SQLiteDatabase sqLiteDatabase, String quireContent) {
        Cursor cursor = sqLiteDatabase.query(Constant_SQLite.PhotoListEntry.TABLE_NAME, null,
                Constant_SQLite.PhotoListEntry.COLUMN_NAME_CONTENT + " like ?", new String[]{"%" + quireContent + "%"},
                null, null, null);

        if (cursor.getCount() != 0) {
            ArrayList<Entity_Photo> photoList = getListByCursor(cursor);
            cursor.close();
            return photoList;
        } else {
            cursor.close();
            return null;
        }
    }


    /**
     * 通过给定索引获取图片信息列表
     *
     * @param cursor 指定索引
     * @return 图片信息列表
     */
    private ArrayList<Entity_Photo> getListByCursor(Cursor cursor) {
        ArrayList<Entity_Photo> photoList = new ArrayList<>();

        int idIndex = cursor.getColumnIndex(Constant_SQLite.PhotoListEntry.COLUMN_NAME_ID);
        int pUserIdIndex = cursor.getColumnIndex(Constant_SQLite.PhotoListEntry.COLUMN_NAME_PUSERID);
        int imageCodeIndex = cursor.getColumnIndex(Constant_SQLite.PhotoListEntry.COLUMN_NAME_IMAGECODE);
        int titleIndex = cursor.getColumnIndex(Constant_SQLite.PhotoListEntry.COLUMN_NAME_TITLE);
        int contentIndex = cursor.getColumnIndex(Constant_SQLite.PhotoListEntry.COLUMN_NAME_CONTENT);

        int imageUrlListIndex0 = cursor.getColumnIndex(Constant_SQLite.PhotoListEntry.COLUMN_NAME_IMAGEURLLIST_0);
        int imageUrlListIndex1 = cursor.getColumnIndex(Constant_SQLite.PhotoListEntry.COLUMN_NAME_IMAGEURLLIST_1);
        int imageUrlListIndex2 = cursor.getColumnIndex(Constant_SQLite.PhotoListEntry.COLUMN_NAME_IMAGEURLLIST_2);
        int imageUrlListIndex3 = cursor.getColumnIndex(Constant_SQLite.PhotoListEntry.COLUMN_NAME_IMAGEURLLIST_3);
        int imageUrlListIndex4 = cursor.getColumnIndex(Constant_SQLite.PhotoListEntry.COLUMN_NAME_IMAGEURLLIST_4);
        int imageUrlListIndex5 = cursor.getColumnIndex(Constant_SQLite.PhotoListEntry.COLUMN_NAME_IMAGEURLLIST_5);
        int imageUrlListIndex6 = cursor.getColumnIndex(Constant_SQLite.PhotoListEntry.COLUMN_NAME_IMAGEURLLIST_6);
        int imageUrlListIndex7 = cursor.getColumnIndex(Constant_SQLite.PhotoListEntry.COLUMN_NAME_IMAGEURLLIST_7);
        int imageUrlListIndex8 = cursor.getColumnIndex(Constant_SQLite.PhotoListEntry.COLUMN_NAME_IMAGEURLLIST_8);

        int likeIdIndex = cursor.getColumnIndex(Constant_SQLite.PhotoListEntry.COLUMN_NAME_LIKEID);
        int likeNumIndex = cursor.getColumnIndex(Constant_SQLite.PhotoListEntry.COLUMN_NAME_LIKENUM);
        int hasLikeIndex = cursor.getColumnIndex(Constant_SQLite.PhotoListEntry.COLUMN_NAME_HASLIKE);
        int collectIdIndex = cursor.getColumnIndex(Constant_SQLite.PhotoListEntry.COLUMN_NAME_COLLECTID);
        int collectNumIndex = cursor.getColumnIndex(Constant_SQLite.PhotoListEntry.COLUMN_NAME_COLLECTNUM);
        int hasCollectIndex = cursor.getColumnIndex(Constant_SQLite.PhotoListEntry.COLUMN_NAME_HASCOLLECT);
        int usernameIndex = cursor.getColumnIndex(Constant_SQLite.PhotoListEntry.COLUMN_NAME_USERNAME);


        while (cursor.moveToNext()) {
            Entity_Photo photo = new Entity_Photo();
            photo.setId(cursor.getString(idIndex));
            photo.setPUserId(cursor.getString(pUserIdIndex));
            photo.setImageCode(cursor.getString(imageCodeIndex));
            photo.setTitle(cursor.getString(titleIndex));
            photo.setContent(cursor.getString(contentIndex));

            ArrayList<String> temp = new ArrayList<>();
            if (!cursor.getString(imageUrlListIndex0).equals(""))
                temp.add(cursor.getString(imageUrlListIndex0));
            if (!cursor.getString(imageUrlListIndex1).equals(""))
                temp.add(cursor.getString(imageUrlListIndex1));
            if (!cursor.getString(imageUrlListIndex2).equals(""))
                temp.add(cursor.getString(imageUrlListIndex2));
            if (!cursor.getString(imageUrlListIndex3).equals(""))
                temp.add(cursor.getString(imageUrlListIndex3));
            if (!cursor.getString(imageUrlListIndex4).equals(""))
                temp.add(cursor.getString(imageUrlListIndex4));
            if (!cursor.getString(imageUrlListIndex5).equals(""))
                temp.add(cursor.getString(imageUrlListIndex5));
            if (!cursor.getString(imageUrlListIndex6).equals(""))
                temp.add(cursor.getString(imageUrlListIndex6));
            if (!cursor.getString(imageUrlListIndex7).equals(""))
                temp.add(cursor.getString(imageUrlListIndex7));
            if (!cursor.getString(imageUrlListIndex8).equals(""))
                temp.add(cursor.getString(imageUrlListIndex8));

            String[] imageUrlList = temp.toArray(new String[0]);

            photo.setImageUrlList(imageUrlList);

            photo.setLikeId(cursor.getString(likeIdIndex));
            photo.setLikeNum(cursor.getInt(likeNumIndex));
            photo.setHasLike(cursor.getInt(hasLikeIndex) == TRUE);

            photo.setCollectId(cursor.getString(collectIdIndex));
            photo.setCollectNum(cursor.getInt(collectNumIndex));
            photo.setHasCollect(cursor.getInt(hasCollectIndex) == TRUE);

            photo.setUsername(cursor.getString(usernameIndex));
            photoList.add(photo);
        }
        return photoList;
    }
}