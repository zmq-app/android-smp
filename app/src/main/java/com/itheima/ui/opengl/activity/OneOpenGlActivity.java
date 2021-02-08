package com.itheima.ui.opengl.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.itheima.ui.opengl.view.OneGlSurfaceView;

/**
 * @Subject OpenGL基本图形绘制
 * @URL     https://www.jianshu.com/p/92d02ac80611
 * @Author  zhangming
 * @Date    2019-07-11 20:10
 */
public class OneOpenGlActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OneGlSurfaceView glSurfaceView = new OneGlSurfaceView(this);
        setContentView(glSurfaceView);
    }
}
