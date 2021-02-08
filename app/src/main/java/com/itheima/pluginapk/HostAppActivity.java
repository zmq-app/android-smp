package com.itheima.pluginapk;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.View;

import com.itheima.smp.R;
import com.itheima.utils.permission.PermissionResultCallBack;
import com.itheima.utils.permission.PermissionUtil;

import java.io.File;

/**
 * @Subject  宿主APP的调用入口类Activity
 * @Description 注意事项:保证公共接口PluginBaseInterface在宿主APP和第三方插件APP的包名保持一致
 * @Problem  问题:java.lang.RuntimeException: Unable to start activity ComponentInfo
 * {com.itheima.smp/com.itheima.pluginapplication.ProxyPluginActivity}:
 * android.content.res.Resources$NotFoundException: Resource ID #0x7f030007
 * @Reason  原因:查看宿主APP的R文件,发现ID=#0x7f030007为R.mipmap.abc_aa_launcher;之后查看插件APP的R文件,发现R.mipmap.ic_launche对应的ID=#0x7f030000;
 * 由于R文件同名的属性值不一致,导致通过宿主APP的代理ProxyPluginActivity调用appInterface.onCreate(bundle);启动插件APP的MainActivity时,在onCreate方法中,
 * 设置XML视图后调用基类BaseActivity::setContentView方法,最终在执行mHostActivity.setContentView(layoutResID);这行代码报错.
 * @Solution 解决:将宿主APP对应主项目ChatRoom_Smp的mipmap-xxhdpi目录下的其余png资源移入drawable目录,确保mipmap-XXX中的图片资源顺序保持一致,这样引用到的ID值将是一致的
 * @URL     参考链接:[https://blog.csdn.net/colinandroid/article/details/79431502]
 * @Author  zhangming
 * @Date    2019-11-07 19:35
 */
public class HostAppActivity extends Activity {
    private PermissionResultCallBack permissionResultCallBack;
    private static String[] RUNNING_PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_app_entry);
        /* 在普通APP[非系统APP,uid!=1000],当Android版本是6.0以上,需要动态申请APP的运行权限,否则默认此权限不给予,后续运行必定出错 */
        /* 此处由于是系统APP,所以不必动态申请APP的运行权限,已经具备[manifest节点上配置android:sharedUserId="android.uid.system"] */
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        //    getRunningPermission();
        //}
        /* 向插件APP中注入上下文Activity实例 */
        PluginManager.getInstance().setContext(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            loadPluginApk();
        }
    }

    /* @region jide begin,add zmq for 普通APP级别 dangerous运行权限获取 */
    private void getRunningPermission() {
        permissionResultCallBack = new PermissionResultCallBack() {
            @Override
            public void onPermissionGranted() {
            }

            @Override
            public void onPermissionGranted(String... permissions) {
                int nSize = permissions.length;
                for (int i=0; i<nSize; i++) {
                    android.util.Log.d("zhangming", "permissions["+i+"]"+" = "+permissions[i]);
                }
            }

            @Override
            public void onPermissionDenied(String... permissions) {
                AlertDialog dialog = new AlertDialog.Builder(HostAppActivity.this)
                        .setMessage(getResources().getString(R.string.perm_grant_hite))
                        .setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivityForResult(intent,2);
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).create();
                dialog.show();
            }

            @Override
            public void onRationalShow(String... permissions) {
                AlertDialog dialog = new AlertDialog.Builder(HostAppActivity.this)
                        .setMessage(getResources().getString(R.string.perm_grant_hite))
                        .setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PermissionUtil.getInstance().request(HostAppActivity.this, RUNNING_PERMISSIONS, permissionResultCallBack);
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).create();
                dialog.show();
            }
        };
        PermissionUtil.getInstance().request(HostAppActivity.this, RUNNING_PERMISSIONS, permissionResultCallBack);
    }
    /* @region jide end,add zmq for 普通APP级别 dangerous运行权限获取 */

    /**
     * 事先放置到SD卡根目录的plugin.apk
     * 现实场景中是有服务端下发
     */
    private void loadPluginApk() {
        File file = new File(Environment.getExternalStorageDirectory().getPath()+"/plugin_apk", "plugin.apk");
        PluginManager.getInstance().loadPluginPath(file.getAbsoluteFile().getPath());
    }

    /**
     * 宿主APP跳转插件APP的入口函数
     * @param view
     */
    public void jumpPluginApp(View view) {
        /* 点击跳往插件app的activity,一律跳转到ProxyPluginActivity,并传递入口ActivityName参数 */
        Intent intent = new Intent(this, ProxyPluginActivity.class);
        intent.putExtra("className", PluginManager.getInstance().getEntryActivityName());
        startActivity(intent);
    }
}
