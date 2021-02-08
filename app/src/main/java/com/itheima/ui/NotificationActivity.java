package com.itheima.ui;

import android.app.Activity;
import android.app.NotificationManager;
//import android.content.Context;
import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.INetworkManagementService;
import android.os.Looper;
//import android.os.SysPropManager;
//import android.os.SystemProperties;
import android.os.ServiceManager;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;
import android.app.PendingIntent;
import android.widget.Toast;
//import android.widget.Toast;

import com.itheima.smp.R;

import java.io.File;

public class NotificationActivity extends Activity {
    private NotificationManager mNotificationManager;
    private SysSettingsObserver mSysSettingsObserver;
    private static final int notificationId = 100;
    private static final int delayMills = 5000;

    /************** (1) @region jide begin,add zmq for 系统通知管理 **************/
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private Runnable mainRunnable = new Runnable() {
        @Override
        public void run() {
            //@region jide begin,add zmq for 代码执行在主线程中
            notificationChecked();
            mainHandler.postDelayed(this,delayMills);
            //@region jide end,add zmq for 代码执行在主线程中
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mSysSettingsObserver = new SysSettingsObserver();
        getContentResolver().registerContentObserver(Settings.System.getUriFor("sys_settings_key"),false, mSysSettingsObserver);
    }

    public void show_notification(View view) {
        createNewNotification(notificationId);
        mainHandler.postDelayed(mainRunnable,delayMills);
    }

    public void cancel_notification(View view) {
        if (mNotificationManager != null) {
            mNotificationManager.cancel(notificationId);
        }
        if (mainRunnable != null) {
            mainHandler.removeCallbacks(mainRunnable);
            mainRunnable = null;
        }
    }

    public void createNewNotification(int notificationId) {
        RemoteViews rViews = new RemoteViews(getPackageName(),R.layout.notification_item);

        PendingIntent homeIntent = PendingIntent.getBroadcast(this,1,new Intent("com.itheima.home.action"),PendingIntent.FLAG_UPDATE_CURRENT);
        rViews.setOnClickPendingIntent(R.id.exit_divider_btn,homeIntent);

        PendingIntent contentIntent = PendingIntent.getBroadcast(this,2,new Intent("com.itheima.click.action"),PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
        mBuilder.setContent(rViews).setSmallIcon(R.mipmap.ic_launcher).setDefaults(NotificationCompat.DEFAULT_ALL).setAutoCancel(false).
                setContentTitle(getResources().getString(R.string.app_name)).setWhen(System.currentTimeMillis()).setContentIntent(contentIntent);

        mNotificationManager.notify(notificationId, mBuilder.build());
    }

    public void notificationChecked() {
        if (mNotificationManager != null) {
            StatusBarNotification[] notifications = mNotificationManager.getActiveNotifications();
            if (notifications != null) {
                boolean existed = false;
                for (int i=0; i<notifications.length; i++) {
                    if (notificationId == notifications[i].getId()) {
                        existed = true;
                        break;
                    }
                }
                if (!existed) {
                    createNewNotification(notificationId);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mainRunnable != null) {
            mainHandler.removeCallbacks(mainRunnable);
            mainRunnable = null;
        }
        if (mSysSettingsObserver != null) {
            getContentResolver().unregisterContentObserver(mSysSettingsObserver);
            mSysSettingsObserver = null;
        }
    }
    /************** (1) @region jide end,add zmq for 系统通知管理 **************/


    /************** (2) @region jide begin,add zmq for 静默安装测试代码块 **************/
    /*** @region jide begin,add zmq for 静默安装 PackageManagerService::installPackageAsUser ***/
    /*** @region jide begin,add zmq for 静默卸载 PackageManagerService::deletePackageAsUser ***/
    public void silence_installer(View view) {
        InstallInfo installInfo = new InstallInfo("com.tencent.mm","file:///sdcard/weixin.apk");
        getPackageManager().installPackage(Uri.parse(installInfo.location),new PackageInstallObserver(installInfo, new Callback() {
            @Override
            public void onSuccess() {
                android.util.Log.v("zhangming","silence_installer succeed...");
            }

            @Override
            public void onError(int errorCode) {
                android.util.Log.v("zhangming","silence_installer failed errorCode = "+errorCode);
            }
        }), PackageManager.INSTALL_REPLACE_EXISTING, getPackageName());
    }

    private static class InstallInfo {
        public String packageName;
        public String location;
        public boolean doneInstalling;

        public InstallInfo(String packageName,String location) {
            this.packageName = packageName;
            this.location = location;
        }
    }

    private class PackageInstallObserver extends IPackageInstallObserver.Stub {
        private InstallInfo mInstallInfo;
        private Callback mCallback;
        private static final int ERROR_INSTALLATION_FAILED = -1;
        private static final int ERROR_PACKAGE_INVALID = -2;

        public PackageInstallObserver(InstallInfo installInfo,Callback callback) {
            this.mInstallInfo = installInfo;
            this.mCallback = callback;
        }

        @Override
        public void packageInstalled(String packageName, int returnCode) {
            android.util.Log.v("zhangming","packageInstalled packageName = "+packageName+" returnCode = "+returnCode);
            if (packageName != null && !packageName.equals(mInstallInfo.packageName)) {
                mCallback.onError(ERROR_PACKAGE_INVALID);
                return;
            }
            if (returnCode == PackageManager.INSTALL_SUCCEEDED) {
                mInstallInfo.doneInstalling = true;
                checkSuccess();
            } else if (returnCode == PackageManager.INSTALL_FAILED_VERSION_DOWNGRADE) {
                mInstallInfo.doneInstalling = true;
                checkSuccess();
            } else {
                mCallback.onError(ERROR_INSTALLATION_FAILED);
            }
            // remove the file containing the apk in order not to use too much space.
            new File(mInstallInfo.location).delete();
        }

        private void checkSuccess() {
            if (!mInstallInfo.doneInstalling) {
                return;
            }
            // Set package verification flag to its original value.
            Settings.Global.putInt(getContentResolver(), Settings.Global.PACKAGE_VERIFIER_ENABLE, 1);
            mCallback.onSuccess();
        }
    }

    //TODO add zmq for 定义回调抽象类Callback
    public abstract static class Callback {
        public abstract void onSuccess();
        public abstract void onError(int errorCode);
    }
    /*** @region jide end,add zmq for 静默安装 PackageManagerService::installPackageAsUser ***/
    /*** @region jide end,add zmq for 静默卸载 PackageManagerService::deletePackageAsUser ***/
    /************** (2) @region jide end,add zmq for 静默安装测试代码块 **************/


    /************** (3) @region jide begin,add zmq for 系统属性设置和隐藏类方法调用 **************/
    private class SysSettingsObserver extends ContentObserver {
        public SysSettingsObserver(){
            super(new Handler());
        }

        /* @region jide begin,add zmq for当ContentProvider的数据有变化时,将会回调onChange接口 */
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            android.util.Log.v("zhangming","SysSettingsObserver onChange uri = "+uri+" selfChange = "+selfChange);
        }
        /* @region jide end,add zmq for当ContentProvider的数据有变化时,将会回调onChange接口 */
    }

    /* @region jide begin,add zmq for 调用系统隐藏类SystemProperties的方法[使用编译后的framework.jar包替换系统的android.jar] */
    //public void calling_hidden_class(View view) {
    //(1)@region jide begin,add zmq for need to use system user[uid = 1000] and have SEAndroid permission [Call system hidden class SystemProperties]
    //(1)@region jide begin,add zmq for setenforce 0 临时关闭SELinux,修改的是/sys/fs/selinux/enforce节点的值,是kernel意义上的修改selinux的策略,断电之后,节点值会复位

    //(2)@region jide begin,add zmq for 需要配置SELinux权限,在property_contexts文件中增加此属性配置: persist.hidden.class  u:object_r:system_prop:s0
    //SystemProperties.set("persist.hidden.class","hidden class...");
    //String result = SystemProperties.get("persist.hidden.class","");
    //Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
    //(2)@region jide end,add zmq for 需要配置SELinux权限,在property_contexts文件中增加此属性配置: persist.hidden.class  u:object_r:system_prop:s0

    //(3)@region jide begin,add zmq for 需要配置SELinux权限,自定义类型systemproperty,需要配置的文件 [property.te, property_contexts, system_app.te]
    //SystemProperties.set("persist.systemproperty.class","SystemProperty...");
    //String result = SystemProperties.get("persist.systemproperty.class","");
    //Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
    //(3)@region jide end,add zmq for 需要配置SELinux权限,自定义类型systemproperty,需要配置的文件 [property.te, property_contexts, system_app.te]

    //(4)@region jide begin,add zmq for 系统已经配置此类属性"persist.sys.",无需再配置此属性"persist.sys.hidden.class"
    //SystemProperties.set("persist.sys.hidden.class","ni hao");
    //String result = SystemProperties.get("persist.sys.hidden.class","");
    //Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
    //(4)@region jide end,add zmq for 系统已经配置此类属性"persist.sys.",无需再配置此属性"persist.sys.hidden.class"

    //(1)@region jide end,add zmq for need to use system user[uid = 1000] and have SEAndroid permission [Call system hidden class SystemProperties]
    //(1)@region jide end,add zmq for setenforce 0 临时关闭SELinux,修改的是/sys/fs/selinux/enforce节点的值,是kernel意义上的修改selinux的策略,断电之后,节点值会复位
    //}
    /* @region jide end,add zmq for 调用系统隐藏类SystemProperties的方法[使用编译后的framework.jar包替换系统的android.jar] */

    public void callingSysPropManager(View view) {
        //SysPropManager sysPropManager = (SysPropManager) getSystemService(Context.SYSPROP_SERVICE);
        //sysPropManager.setProp("system_key","hello");
        //String content = sysPropManager.getProp("system_key");
        //Toast.makeText(this, content, Toast.LENGTH_SHORT).show();

        /* @region jide begin,add zmq for Android N上使用内部类SettingProvider::SettingsRegistry中的三个全局xml文件,存储对应的key和value */
        /* @region jide begin,add zmq for /data/system/users/[userID = 0]/settings_global.xml,settings_system.xml,settings_secure.xml */
        //Settings.Global.putInt(getContentResolver(),"global_settings_key",3);
        //int gValue = Settings.Global.getInt(getContentResolver(),"global_settings_key",0);
        //Toast.makeText(this, "gValue = "+gValue, Toast.LENGTH_SHORT).show();

        Settings.System.putInt(getContentResolver(),"sys_settings_key",6);
        int sValue = Settings.System.getInt(getContentResolver(),"sys_settings_key",0);
        Toast.makeText(this, "sValue = "+sValue, Toast.LENGTH_SHORT).show();
        /* @region jide end,add zmq for /data/system/users/[userID = 0]/settings_global.xml,settings_system.xml,settings_secure.xml */
        /* @region jide end,add zmq for Android N上使用内部类SettingProvider::SettingsRegistry中的三个全局xml文件,存储对应的key和value */
    }
    /************** (3) @region jide end,add zmq for 系统属性设置和隐藏类方法调用 **************/


    /************** (4) @region jide begin,add zmq for 系统防火墙的开启和关闭操作 **************/
    public void openSystemFirewall(View view) {
        try {
            final INetworkManagementService netManager = INetworkManagementService.Stub
                    .asInterface(ServiceManager.getService(Context.NETWORKMANAGEMENT_SERVICE));
            netManager.setFirewallEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeSystemFirewall(View view) {
        try {
            final INetworkManagementService netManager = INetworkManagementService.Stub
                    .asInterface(ServiceManager.getService(Context.NETWORKMANAGEMENT_SERVICE));
            netManager.setFirewallEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /************** (4) @region jide end,add zmq for 系统防火墙的开启和关闭操作 **************/
}
