package com.itheima.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.itheima.model.IBinderPool;
import com.itheima.utils.CommonConstants;

import java.util.concurrent.CountDownLatch;

/**
 * @Subject Binder连接池,位于客户端AIDL接口和服务端Service之间的中间件
 * @Function 向远程的单一Service通过binderCode参数来查询对应的Binder,并将此Binder返还给客户端对应的AIDL接口
 * @Author zhangming
 * @Date 2018-10-06 13:25
 */
public class BinderPool {
    //定义BinderCode
    public static final int BINDER_COMPUTE = 0;
    public static final int BINDER_SECURITY_CENTER = 1;

    private Context mContext;
    //TODO Binder连接池通过操控mBinderPool实例向Service端发起请求
    private IBinderPool mBinderPool;
    //TODO volatile 用来修饰被不同线程访问和修改的变量
    private static volatile BinderPool sInstance;

    /**
     * CountDownLatch类是一个同步计数器,构造时传入int参数,该参数就是计数器的初始值，每调用一次countDown()方法，计数器减1,计数器大于0 时，await()方法会阻塞程序继续执行
     * CountDownLatch如其所写，是一个倒计数的锁存器，当计数减至0时触发特定的事件。利用这种特性，可以让主线程等待子线程的结束。
     */
    private CountDownLatch mConnectBinderPoolCountDownLatch;

    private BinderPool(Context context) {
        mContext = context.getApplicationContext();
        connectBinderPoolService();
    }

    public static BinderPool getInstance(Context context) {
        if (sInstance == null) {
            synchronized (BinderPool.class) {
                if (sInstance == null) {
                    sInstance = new BinderPool(context);
                }
            }
        }
        return sInstance;
    }

    /** 向Service端发起查询Binder请求,返还给客户端的AIDL接口中对应的Binder实例对象SecurityCenterImpl or ComputerImpl **/
    public IBinder queryBinder(int binderCode) {
        try {
            return mBinderPool.queryBinder(binderCode);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    private synchronized void connectBinderPoolService() {
        // 只有一个线程有效
        mConnectBinderPoolCountDownLatch = new CountDownLatch(1);
        //TODO 启动Binder连接池对应的单一远程服务类BinderPoolService
        Intent intent = new Intent(mContext, BinderPoolService.class);
        mContext.bindService(intent, mBinderPoolConnection, Context.BIND_AUTO_CREATE);
        try{
            // 等待,直到CountDownLatch中的线程数为0
            mConnectBinderPoolCountDownLatch.await();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    private ServiceConnection mBinderPoolConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //TODO service = android.os.BinderProxy@b9a29de
            Log.i(CommonConstants.TAG,"BinderPool onServiceConnected,service = "+service);
            mBinderPool = IBinderPool.Stub.asInterface(service);
            try{
                mBinderPool.asBinder().linkToDeath(mBinderPoolDeathRecipient, 0);
            }catch (RemoteException e){
                e.printStackTrace();
            }
            //TODO 执行一次countDown,其计数减一
            //TODO 当Binder连接池对远程Service成功建立连接后,执行countDown方法,目的唤醒当前线程(执行了await),结束客户端的单例方法[BinderPool.getInstance(this);]
            mConnectBinderPoolCountDownLatch.countDown();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private IBinder.DeathRecipient mBinderPoolDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            // 解除死亡绑定
            mBinderPool.asBinder().unlinkToDeath(mBinderPoolDeathRecipient, 0);
            mBinderPool = null;
            // 重连
            connectBinderPoolService();
        }
    };
}
