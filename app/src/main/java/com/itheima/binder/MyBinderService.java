package com.itheima.binder;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import com.itheima.utils.CommonConstants;

/**
 * @Subject 手动编写Binder远程服务端
 * @Link    https://www.jianshu.com/p/bdef9e3178c9
 * @Author  zhangming
 * @Date    2018-10-05 22:30
 */
public class MyBinderService extends Service {
    public static final int REPORT_CODE = 0x03;
    private ReporterService mReporter;

    //定义接口IReporter
    public interface IReporter{
        int reportInt(String values, int type);
        String reportStr(String values,int type);
    }

    /** ReporterService类继承Binder类,重写onTransact函数,实现自己的业务逻辑 **/
    public final class ReporterService extends Binder implements IReporter{
        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case REPORT_CODE:
                    data.enforceInterface("reporter");

                    //获取从client端接收到的数据
                    int type = data.readInt();
                    String values = data.readString();
                    Log.i(CommonConstants.TAG, "ReporterService onTransact,receive type is "+type+",value is "+values);

                    //写入返回数据到Parcel类型变量reply中
                    int rType = ((IReporter) this).reportInt(values, type);
                    String rValues = ((IReporter) this).reportStr(values, type);
                    reply.writeInterfaceToken("reporter");
                    reply.writeInt(rType);
                    reply.writeString(rValues);

                    return true;
            }
            return super.onTransact(code,data,reply,flags);
        }

        @Override
        public int reportInt(String values, int type) {
            return type;
        }

        @Override
        public String reportStr(String values, int type) {
            return values;
        }
    }

    @Override
    public void onCreate() {
        mReporter = new ReporterService();
    }

    /** 在Service的onBind方法中返回ReporterService类的实例 **/
    @Override
    public IBinder onBind(Intent intent) {
        return mReporter;
    }
}
