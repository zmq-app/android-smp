package com.itheima.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.itheima.model.Book;
import com.itheima.model.IBookManager;
import com.itheima.utils.CommonConstants;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Subject 远程服务端Service的实现,使用Binder机制
 * @Author  zhangming
 * @Date    2018/10/4 21:32
 */
public class BookManagerService extends Service {
    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();

    private Binder mBinder = new IBookManager.Stub() {
        @Override
        public void addBook(Book book) throws RemoteException {
            mBookList.add(book);
        }

        @Override
        public List<Book> getBookList() throws RemoteException {
            Log.i(CommonConstants.TAG,"Remote BookManagerService getBookList...");
            return mBookList;
        }
    };

    @Override
    public void onCreate() {
        Log.i(CommonConstants.TAG,"Remote BookManagerService onCreate...");
        mBookList.add(new Book(101,"Java"));
        mBookList.add(new Book(102,"Android"));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        Log.i(CommonConstants.TAG,"Remote BookManagerService onStartCommand,return value = "+result);
        return result;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(CommonConstants.TAG,"Remote BookManagerService onBind...");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(CommonConstants.TAG,"Remote BookManagerService onUnbind...");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(CommonConstants.TAG,"Remote BookManagerService onDestroy...");
    }
}
