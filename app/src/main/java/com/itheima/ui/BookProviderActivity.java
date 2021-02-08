package com.itheima.ui;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.itheima.utils.CommonConstants;

/**
 * @Subject 内容提供者的访问案例
 * @Author  zhangming
 * @Date    2018-10-06 13:11
 */
public class BookProviderActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO 获取内容解析者
        ContentResolver resolver = getContentResolver();

        //TODO Book Table Query
        Uri bookUri = Uri.parse("content://com.itheima.provider.BookProvider/book");
        ContentValues cValues = new ContentValues();
        cValues.put("bookName", "Android开发");
        resolver.insert(bookUri,cValues);

        Cursor bookCursor = resolver.query(bookUri,new String[]{"_id","bookName"},null,null,null);
        if(bookCursor != null){
           while(bookCursor.moveToNext()){
               Log.e(CommonConstants.TAG, "ID: " + bookCursor.getInt(bookCursor.getColumnIndex("_id"))
                   + "  BookName: " + bookCursor.getString(bookCursor.getColumnIndex("bookName")));
           }
           bookCursor.close();
        }

        //TODO User Table Query
        Uri userUri = Uri.parse("content://com.itheima.provider.BookProvider/user");
        cValues.clear();
        cValues.put("userName", "叶叶叶");
        cValues.put("sex", "男");
        resolver.insert(userUri, cValues);

        Cursor userCursor = resolver.query(userUri, new String[]{"_id", "userName", "sex"}, null, null, null);
        if (userCursor != null) {
            while (userCursor.moveToNext()) {
                Log.e(CommonConstants.TAG, "ID:" + userCursor.getInt(userCursor.getColumnIndex("_id"))
                    + "  UserName:" + userCursor.getString(userCursor.getColumnIndex("userName"))
                    + "  Sex:" + userCursor.getString(userCursor.getColumnIndex("sex")));
            }
            userCursor.close();
        }
    }
}
