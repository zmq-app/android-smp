package com.itheima.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.itheima.smp.R;

public class InputMethodSettingActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inputmethod_setting);
        finish();
    }
}
