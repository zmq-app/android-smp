package com.itheima.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.itheima.model.IBinderPool;
import com.itheima.model.ICompute;
import com.itheima.model.ISecurityCenter;
import com.itheima.utils.CommonConstants;

/**
 * @Subject Binder连接池服务,共有一个Service,提供客户端不同的Binder
 * @Author  zhangming
 * @Date    2018-10-06 13:17
 */
public class BinderPoolService extends Service{
    /** Binder实现具体静态类SecurityCenterImpl **/
    /** 在ISecurityCenter.Stub类中onTransact方法会回调encrypt,decrypt方法 **/
    public static class SecurityCenterImpl extends ISecurityCenter.Stub {
        @Override
        public String encrypt(String content) throws RemoteException {
            char[] chars = content.toCharArray();
            for (int i=0; i<chars.length;i++) {
                chars[i] = (char) (chars[i]+1);
            }
            return new String(chars);
        }

        @Override
        public String decrypt(String password) throws RemoteException {
            char[] chars = password.toCharArray();
            for (int i=0; i<chars.length;i++) {
                chars[i] = (char) (chars[i]-1);
            }
            return new String(chars);
        }
    }

    /** Binder实现具体静态类ComputerImpl **/
    /** 在ICompute.Stub类中onTransact方法会回调add方法 **/
    public static class ComputerImpl extends ICompute.Stub {
        @Override
        public int add(int a, int b) throws RemoteException {
            return a+b;
        }
    }

    /** 接收来自Binder连接池中发来的请求,返还给Binder连接池一个具体的Binder实例对象SecurityCenterImpl or ComputerImpl**/
    private Binder mBinderPool = new IBinderPool.Stub() {
        @Override
        public IBinder queryBinder(int binderCode) throws RemoteException {
            switch (binderCode) {
                case BinderPool.BINDER_COMPUTE:
                    return new ComputerImpl();
                case BinderPool.BINDER_SECURITY_CENTER:
                    return new SecurityCenterImpl();
            }
            return null;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO 返回给Binder连接池一个具体的Binder实例对象,这个实例用于绑定连接池和服务之间的连线
        //TODO mBinderPool = com.itheima.service.BinderPoolService$1@89b85ad
        Log.i(CommonConstants.TAG,"mBinderPool = "+ mBinderPool);
        return mBinderPool;
    }
}
