package com.itheima.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * @Subject 数据库帮助实现类,继承于抽象类IDBOpenHelper,实现继承下来的抽象方法onCreate,onUpgrade
 * @Author  zhangming
 * @Date    2018-10-06 12:45
 */
public class DBOpenHelper extends IDBOpenHelper {
    public DBOpenHelper(Context context){
        super(context);
    }

    /**
     * 实现SQLiteOpenHelper类中的抽象方法onCreate
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BOOK_TABLE);
        db.execSQL(CREATE_USER_TABLE);
        Log.i("zhangming","DBOpenHelper onCreate...");
    }

    /**
     * 实现SQLiteOpenHelper类中的抽象方法onUpgrade
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("zhangming","DBOpenHelper onUpgrade oldVersion = "+oldVersion+" newVersion = "+newVersion);
    }
}
