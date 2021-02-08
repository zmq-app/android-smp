package com.itheima.pluginapk;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * @Subject      管理第三方APP的基类Activity,其生命周期的回调有宿主App调用
 * @Description  宿主APP和第三方APP插件之间均会实现公共接口PluginBaseInterface
 * @Author       zhangming
 * @Date         2019-11-07 19:35
 */
public class BaseActivity extends Activity implements PluginBaseInterface {
    protected Activity mHostActivity;

    @Override
    public void attach(Activity activity) {
        this.mHostActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (this.mHostActivity == null) {
            super.onCreate(savedInstanceState);
        } else {
            //mHostActivity.onCreate(savedInstanceState);
        }
    }

    @Override
    public void onStart() {
        if (this.mHostActivity == null) {
            super.onStart();
        } else {
            //mHostActivity.onStart();
        }
    }

    @Override
    public void onResume() {
        if (this.mHostActivity == null) {
            super.onResume();
        } else {
            //mHostActivity.onResume();
        }
    }

    @Override
    public void onPause() {
        if (this.mHostActivity == null) {
            super.onPause();
        } else {
            //mHostActivity.onPause();
        }
    }

    @Override
    public void onDestroy() {
        if (this.mHostActivity == null) {
            super.onDestroy();
        } else {
            //mHostActivity.onDestroy();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (this.mHostActivity == null) {
            super.onSaveInstanceState(outState);
        } else {
            //mHostActivity.onSaveInstanceState(outState);
        }
    }

    @Override
    public LayoutInflater getLayoutInflater() {
        if (mHostActivity == null) {
            return super.getLayoutInflater();
        } else {
            return mHostActivity.getLayoutInflater();
        }
    }


    @Override
    public WindowManager getWindowManager() {
        if (mHostActivity == null) {
            return super.getWindowManager();
        } else {
            return mHostActivity.getWindowManager();
        }
    }

    @Override
    public Window getWindow() {
        if (mHostActivity == null) {
            return super.getWindow();
        } else {
            return mHostActivity.getWindow();
        }
    }

    /**
     * super.setContentView(layoutResID)最终调用的是系统给我们注入的上下文
     * 而mHostActivity才是宿主APP注入的上下文环境
     */
    @Override
    public void setContentView(int layoutResID) {
        if (this.mHostActivity == null) {
            super.setContentView(layoutResID);
        } else {
            mHostActivity.setContentView(layoutResID);
        }
    }

    @Override
    public View findViewById(int id) {
        if (mHostActivity == null) {
            return super.findViewById(id);
        } else {
            return mHostActivity.findViewById(id);
        }
    }
}
