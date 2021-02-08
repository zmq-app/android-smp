package com.itheima.ui.opengl.activity;

import android.annotation.Nullable;
import android.app.Activity;
import android.os.Bundle;

import com.itheima.ui.opengl.view.TwoGlSurfaceView;

public class TwoOpenGlActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TwoGlSurfaceView glSurfaceView = new TwoGlSurfaceView(this);
        setContentView(glSurfaceView);
    }
}
