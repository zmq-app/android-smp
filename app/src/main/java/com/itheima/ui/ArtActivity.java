package com.itheima.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.itheima.smp.R;
import com.itheima.model.PeopleBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Subject 文章Activity,android8.0方向旋转生命周期的验证
 * @Author  zhangming
 * @Date    2020-09-02 09:33
 */
public class ArtActivity extends AppCompatActivity {
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.toolBar)
    Toolbar toolbar;

    private int mode;
    private List<PeopleBean> selectPeoples;
    private static final String TAG = ArtActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_art);
        /* 打印两者均为ClassName = "dalvik.system.PathClassLoader" */
        Log.i(TAG,"onCreate classLoader1 = "+ArtActivity.class.getClassLoader()+" classLoader2 = "+getClassLoader());

        ButterKnife.bind(this);
        mode = getIntent().getIntExtra("mode", 1);
        selectPeoples = (List<PeopleBean>) getIntent().getSerializableExtra("intent_list");

        initPresenter();
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG,"onStart......");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"onResume......");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG,"onPause......");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG,"onStop......");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy......");
    }

    /**
     * (1)如果AndroidManifest.xml文件中ArtActivity配置,android:screenOrientation="sensor",根据物理传感器方向转动,横竖屏切换均会先destroy原Activity,然后create新Activity
     * (2)如果AndroidManifest.xml文件中ArtActivity配置,android:screenOrientation="sensorLandscape",强制Surface旋转为横屏,一般横屏游戏会这样设置,90度和270度根据G-sensor切换
     * (3)如果AndroidManifest.xml文件中ArtActivity配置,android:screenOrientation="sensorPortrait",强制Surface旋转为竖屏,0度和180度根据G-sensor切换
     * (4)其中android:screenOrientation="landscape"为90度,android:screenOrientation="reverseLandscape"为270度
     * (5)配置android:configChanges="orientation",横竖屏转换仅调用一次onConfigurationChanged接口,而不再重新走Activity生命周期的创建过程
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                Log.i(TAG,"onConfigurationChanged newConfig is ORIENTATION_PORTRAIT......");
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                Log.i(TAG,"onConfigurationChanged newConfig is ORIENTATION_LANDSCAPE......");
                break;
        }
    }


    private void initPresenter() {

    }

    private void initView() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");
        title.setText(getIntent().getStringExtra("title"));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}