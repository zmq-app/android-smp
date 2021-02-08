package com.itheima.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.itheima.model.Book;
import com.itheima.model.IBookManager;
import com.itheima.service.BookManagerService;
import com.itheima.utils.CommonConstants;

import java.util.List;

/**
 * @Subject   客户端的实现,绑定远程服务
 * @Subcrible 一旦绑定成功后将服务端返回的Binder对象转化成AIDL接口,通过此接口调用远程服务端的方法
 * @Function  startService,bindService混合调用的生命周期探究
 * @Author    zhangming
 * @Date      2018/10/4 21:36
 */
public class BookManagerActivity extends Activity {
    private Intent intent;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //TODO 绑定成功后将服务端返回的Binder对象转化成AIDL接口
            Log.i(CommonConstants.TAG,"BookManagerActivity onServiceConnected...");
            IBookManager bookManager = IBookManager.Stub.asInterface(service);
            try{
                List<Book> mBookList = bookManager.getBookList();
                Log.i(CommonConstants.TAG,"query book list: "+mBookList.toString());
            }catch (RemoteException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(CommonConstants.TAG,"BookManagerActivity onCreate,ready to bind remote service...");
        /* @region jide begin,add zmq for 使用两种方式启动Service,bindService and startService */
        intent = new Intent(this, BookManagerService.class);
        bindService(intent,mConnection, Context.BIND_AUTO_CREATE);
        startService(intent);
        /* @region jide end,add zmq for 使用两种方式启动Service,bindService and startService */
    }

    @Override
    protected void onDestroy() {
        Log.i(CommonConstants.TAG,"BookManagerActivity onDestroy,ready to unbind remote service...");
        if (mConnection != null) {
            unbindService(mConnection);
            mConnection = null;
        }
        if (intent != null) {
            stopService(intent);
            intent = null;
        }
        super.onDestroy();
    }

    /**
     * Client: BookManagerActivity onCreate,ready to bind remote service...
     * Server: Remote BookManagerService onCreate...
     * Server: Remote BookManagerService onBind...
     * Client: BookManagerActivity onServiceConnected... [之后开始执行AIDL封装方法过程]
     * Server: Remote BookManagerService getBookList...
     * Client: query book list: [101 Java, 102 Android]
     */
 }
