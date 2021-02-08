package com.itheima.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.itheima.smp.R;
import com.itheima.utils.CommonConstants;

/**
 * @Subject 有关日历控件测试的案例
 * @Author  zhangming
 * @Date    2018-09-22
 */
@Route(path = CommonConstants.Calendar_Activity)
public class CalendarActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
    }

    @Override
    public void finish() {
        super.finish();
        Log.i(CommonConstants.TAG,"CalendarActivity finish...");
    }

    @Override
    public void finishAndRemoveTask() {
        super.finishAndRemoveTask();
        Log.i(CommonConstants.TAG,"CalendarActivity finishAndRemoveTask...");
    }

    @Override
    public void finishActivity(int requestCode) {
        super.finishActivity(requestCode);
        Log.i(CommonConstants.TAG,"CalendarActivity finishActivity requestCode = "+requestCode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(CommonConstants.TAG,"CalendarActivity onDestroy...");
    }
}
