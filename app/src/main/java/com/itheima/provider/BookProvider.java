package com.itheima.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.itheima.utils.CommonConstants;

/**
 * @Subject 自定义Provider
 * @Author  zhangming
 * @Date    2018-10-06 12:18.
 */
public class BookProvider extends ContentProvider {
    private Context mContext;
    private SQLiteDatabase db;

    public static final String AUTHORITY = "com.itheima.provider.BookProvider";
    public static final int BOOK_URI_CODE = 0;
    public static final int USER_URI_CODE = 1;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static{
        uriMatcher.addURI(AUTHORITY,DBOpenHelper.BOOK_TABLE_NAME,BOOK_URI_CODE);
        uriMatcher.addURI(AUTHORITY,DBOpenHelper.USER_TABLE_NAME,USER_URI_CODE);
    }

    /** onCreate方法运行在独立进程中的main线程下,其余诸如:insert,delete,update,query方法均运行在Binder线程中 **/
    @Override
    public boolean onCreate() {
        Log.i(CommonConstants.TAG,"BookProvider onCreate,Current Thread = "+Thread.currentThread().getName());
        mContext = getContext();
        initProviderData();
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Log.i(CommonConstants.TAG,"BookProvider query,Current Thread = "+Thread.currentThread().getName());
        String tableName = getTableName(uri);
        if (tableName == null) {
            throw new IllegalArgumentException("Unsupported URI:" + uri);
        }
        return db.query(tableName, projection, selection, selectionArgs, null, null, sortOrder, null);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Log.i(CommonConstants.TAG,"BookProvider insert,Current Thread = "+Thread.currentThread().getName());
        String tableName = getTableName(uri);
        if(tableName == null){
            throw new IllegalArgumentException("Unsupported URI:" + uri);
        }
        db.insert(tableName,null,values);
        /** ContentResolver:内容解析者 **/
        mContext.getContentResolver().notifyChange(uri,null);

        return uri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.i(CommonConstants.TAG,"BookProvider delete,Current Thread = "+Thread.currentThread().getName());
        String tableName = getTableName(uri);
        if (tableName == null) {
            throw new IllegalArgumentException("Unsupported URI:" + uri);
        }
        int count = db.delete(tableName, selection, selectionArgs);
        if (count > 0) {
            mContext.getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.i(CommonConstants.TAG,"BookProvider update,Current Thread = "+Thread.currentThread().getName());
        String tableName = getTableName(uri);
        if (tableName == null) {
            throw new IllegalArgumentException("Unsupported URI:" + uri);
        }
        int row = db.update(tableName, values, selection, selectionArgs);
        if (row > 0) {
            mContext.getContentResolver().notifyChange(uri, null);
        }
        return row;
    }


    /**
     * 哈希表节点的数据结构定义
     * static class Entry<K,V> implements Map.Entry<K,V> {
     *      final K key;
     *      V value;
     *      Entry<K,V> next;
     *      final int hash;
     *      ……
     * };
     */
    /** 初始化ContentProvider中的数据,此处添加三本书,三个作者 **/
    private void initProviderData(){
        //获取SQLiteDatabase实例对象
        db = new DBOpenHelper(mContext).getWritableDatabase();
        db.beginTransaction();

        //TODO ContentValues操控put,remove,clear方法,其本质是一个HashMap操作[实现基于哈希表]
        ContentValues cValues = new ContentValues();
        cValues.put("bookName","数据结构");
        db.insert(DBOpenHelper.BOOK_TABLE_NAME,null,cValues);
        cValues.put("bookName","编译原理");
        db.insert(DBOpenHelper.BOOK_TABLE_NAME,null,cValues);
        cValues.put("bookName","网络原理");
        db.insert(DBOpenHelper.BOOK_TABLE_NAME,null,cValues);
        cValues.clear();

        cValues.put("userName", "叶");
        cValues.put("sex", "女");
        db.insert(DBOpenHelper.USER_TABLE_NAME, null, cValues);
        cValues.put("userName", "叶叶");
        cValues.put("sex", "男");
        db.insert(DBOpenHelper.USER_TABLE_NAME, null, cValues);
        cValues.put("userName", "叶应是叶");
        cValues.put("sex", "男");
        db.insert(DBOpenHelper.USER_TABLE_NAME, null, cValues);
        cValues.clear();

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    private String getTableName(Uri uri) {
        String tableName = null;
        switch (uriMatcher.match(uri)) {
            case BOOK_URI_CODE:
                tableName = DBOpenHelper.BOOK_TABLE_NAME;
                break;
            case USER_URI_CODE:
                tableName = DBOpenHelper.USER_TABLE_NAME;
                break;
        }
        return tableName;
    }
}
