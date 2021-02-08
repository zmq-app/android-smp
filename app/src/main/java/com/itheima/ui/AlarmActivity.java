package com.itheima.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.itheima.smp.R;
import com.itheima.service.AlarmService;

public class AlarmActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent intent = new Intent(this, AlarmService.class);
        startService(intent);
        /* 结束Activity,从Task中移除 */
        //finish();
        /* 结束本进程process="com.itheima.smp" */
        //System.exit(0);
    }
}
