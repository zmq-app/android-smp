package com.itheima.provider;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 抽象类IDBOpenHelper
 * 注明: 一个类不包含抽象方法,只是用abstract修饰的话也是抽象类.也就是说抽象类不一定必须含有抽象方法
 * Created by zhangming on 2018/10/6.
 */
public abstract class IDBOpenHelper extends SQLiteOpenHelper {
    private static final String DATA_BASE_NAME = "book.db"; //数据库名
    private static final int DATE_BASE_VERSION = 3;         //数据库版本号
    public  static final String BOOK_TABLE_NAME = "book";    //表名-书
    public  static final String USER_TABLE_NAME = "user";    //表名-用户

    //创建表--书 (两列:主键自增长,书名)
    protected final String CREATE_BOOK_TABLE = "create table " + BOOK_TABLE_NAME
            + "(_id integer primary key autoincrement, bookName text)";
    //创建表--用户 (三列:主键自增长,用户名,性别)
    protected final String CREATE_USER_TABLE = "create table " + USER_TABLE_NAME
            + "(_id integer primary key autoincrement, userName text, sex text)";

    public IDBOpenHelper(Context context){
        super(context,DATA_BASE_NAME,null,DATE_BASE_VERSION);
    }

    /**
    private SQLiteDatabase getDatabaseLocked(boolean writable) {
        if (mDatabase != null) {
            if (!mDatabase.isOpen()) {
                // Darn!  The user closed the database by calling mDatabase.close().
                mDatabase = null;
            } else if (!writable || !mDatabase.isReadOnly()) {
                // The database is already open for business.
                return mDatabase;
            }
        }

        if (mIsInitializing) {
            throw new IllegalStateException("getDatabase called recursively");
        }

        SQLiteDatabase db = mDatabase;
        try {
            mIsInitializing = true;

            if (db != null) {
                if (writable && db.isReadOnly()) {
                    db.reopenReadWrite();
                }
            } else if (mName == null) {
                db = SQLiteDatabase.createInMemory(mOpenParamsBuilder.build());
            } else {
                final File filePath = mContext.getDatabasePath(mName);
                SQLiteDatabase.OpenParams params = mOpenParamsBuilder.build();
                try {
                    db = SQLiteDatabase.openDatabase(filePath, params);
                    // Keep pre-O-MR1 behavior by resetting file permissions to 660
                    setFilePermissionsForDb(filePath.getPath());
                } catch (SQLException ex) {
                    if (writable) {
                        throw ex;
                    }
                    Log.e(TAG, "Couldn't open " + mName
                            + " for writing (will try read-only):", ex);
                    params = params.toBuilder().addOpenFlags(SQLiteDatabase.OPEN_READONLY).build();
                    db = SQLiteDatabase.openDatabase(filePath, params);
                }
            }

            onConfigure(db);

            final int version = db.getVersion();
            if (version != mNewVersion) {
                if (db.isReadOnly()) {
                    throw new SQLiteException("Can't upgrade read-only database from version " +
                            db.getVersion() + " to " + mNewVersion + ": " + mName);
                }

                if (version > 0 && version < mMinimumSupportedVersion) {
                    File databaseFile = new File(db.getPath());
                    onBeforeDelete(db);
                    db.close();
                    if (SQLiteDatabase.deleteDatabase(databaseFile)) {
                        mIsInitializing = false;
                        return getDatabaseLocked(writable);
                    } else {
                        throw new IllegalStateException("Unable to delete obsolete database "
                                + mName + " with version " + version);
                    }
                } else {
                    db.beginTransaction();
                    try {
                        if (version == 0) {
                            onCreate(db); //第一次安装执行
                        } else {
                            //再次安装,若传递新版本号到父类SQLiteOpenHelper构造器中,执行onDowngrade or onUpgrade
                            if (version > mNewVersion) {
                                onDowngrade(db, version, mNewVersion);
                            } else {
                                onUpgrade(db, version, mNewVersion);
                            }
                        }
                        db.setVersion(mNewVersion);
                        db.setTransactionSuccessful();
                    } finally {
                        db.endTransaction();
                    }
                }
            }

            onOpen(db);

            if (db.isReadOnly()) {
                Log.w(TAG, "Opened " + mName + " in read-only mode");
            }

            mDatabase = db;
            return db;
        } finally {
            mIsInitializing = false;
            if (db != null && db != mDatabase) {
                db.close();
            }
        }
    }
    */
}
