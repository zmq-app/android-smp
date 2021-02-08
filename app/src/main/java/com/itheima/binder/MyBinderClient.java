package com.itheima.binder;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.itheima.utils.CommonConstants;

/**
 * @Subject 手动编写Binder本地客户端
 * @Link    https://www.jianshu.com/p/bdef9e3178c9
 * @Author  zhangming
 * @Date    2018-10-05 22:30
 */
public class MyBinderClient extends Activity {
    private ServiceConnection mConnection;

    //参数mBinder为后台Service中的onBind返回的IBinder对象,也就是Binder IPC架构中的mRemote对象.
    //通过Parcel.obtain()获取发送包对象,应答包对象,写入数据;
    //调用IBinder的transact接口,即mRemote.transact()的调用.
    private class BindConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder mBinder) {
            Log.i(CommonConstants.TAG,"BindConnection onServiceConnected...");

            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            data.writeInterfaceToken("reporter");
            data.writeInt(0x16);
            data.writeString("Hello World");

            try{
                mBinder.transact(MyBinderService.REPORT_CODE, data, reply, 0);
                reply.enforceInterface("reporter");
                int retType = reply.readInt();
                String retValue = reply.readString();
                Log.i(CommonConstants.TAG,"MyBinderClient onServiceConnected retType is "+retType+",retValue is "+retValue);
            }catch (RemoteException e){
                e.printStackTrace();
            }finally {
                data.recycle();
                reply.recycle();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(CommonConstants.TAG,"BindConnection onServiceDisconnected...");
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mConnection = new BindConnection();
        Intent intent = new Intent(this, MyBinderService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        if(mConnection != null){
            unbindService(mConnection);
            mConnection = null;
        }
        super.onDestroy();
    }
}
