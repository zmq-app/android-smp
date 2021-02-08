package com.itheima.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.itheima.model.ICompute;
import com.itheima.model.ISecurityCenter;
import com.itheima.service.BinderPool;
import com.itheima.service.BinderPoolService;
import com.itheima.utils.CommonConstants;

/**
 * @Subject Binder连接池客户端测试程序
 * @Author  zhangming
 * @Date    2018-10-06 13:19
 */
public class BinderPoolActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Thread(new Runnable() {
            @Override
            public void run() {
                doWork();
            }
        }).start();
    }

    /**
     * 此处必须将远程调用这段代码放入子线程中,原因是在getInstance调用中使用了CountDownLatch await方法,会阻塞当前线程
     * 一旦将其放入主线程中,那么当执行完bindService操作后,主线程将会进入休眠状态,无法完成后续的Binder连接操作,
     * 这样onServiceConnected回调方法将永远不会被执行,也就不会执行countDown操作来唤醒主线程.
     * 而放入子线程中,后续的Binder连接操作是在主线程中完成,这样连接上后onServiceConnected回调方法将会被执行,
     * 最后执行countDown操作唤醒子线程,继续后续的查询Binder的操作
     */
    private void doWork() {
        //获取Binder连接池的单一实例binderPool
        BinderPool binderPool = BinderPool.getInstance(this);

        //远程调用计算的AIDL接口
        IBinder computeBinder = binderPool.queryBinder(BinderPool.BINDER_COMPUTE); //获取返还的Binder实例
        ICompute compute = BinderPoolService.ComputerImpl.asInterface(computeBinder); //转化为对应的AIDL接口
        try{
            int rs = compute.add(3,5);
            Log.i(CommonConstants.TAG,"3 + 5 = "+rs);
        }catch (RemoteException e){
            e.printStackTrace();
        }

        //远程调用加密和解密的AIDL接口
        IBinder securityCenterBinder = binderPool.queryBinder(BinderPool.BINDER_SECURITY_CENTER);
        ISecurityCenter iSecurityCenter = BinderPoolService.SecurityCenterImpl.asInterface(securityCenterBinder);
        try {
            String content = "I love this book";
            Log.e(CommonConstants.TAG,"加密前："+content);
            content = iSecurityCenter.encrypt(content);
            Log.e(CommonConstants.TAG,"加密后："+content);
            content = iSecurityCenter.decrypt(content);
            Log.e(CommonConstants.TAG,"解密后："+content);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
