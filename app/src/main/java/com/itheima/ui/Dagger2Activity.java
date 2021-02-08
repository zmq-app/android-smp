package com.itheima.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.itheima.smp.R;
import com.itheima.di.components.DaggerDagger2Component;
import com.itheima.di.modules.Dagger2Module;
import com.itheima.model.DaggerBean;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Subject Dagger2框架的基本使用
 * @Author  zhangming
 * @Date    2021-01-24 17:35
 */
public class Dagger2Activity extends AppCompatActivity {
    /* 作为依赖需求方,@Inject注解添加到成员变量上,其访问限制符不可以为private,否则编译报错,因为dagger2框架无法访问到它 */
    @Inject
    protected DaggerBean daggerBean;
    @BindView(R.id.toolBar)
    protected Toolbar toolBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dagger_audio_detail);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setSupportActionBar(toolBar);
        ActionBar actionBar = getSupportActionBar();
        //给左上角图标的左边加上一个返回的图标,对应ActionBar.DISPLAY_HOME_AS_UP
        actionBar.setDisplayHomeAsUpEnabled(true);
        ///使左上角图标是否显示,如果设成false,则没有程序图标,仅仅就个标题,否则显示应用程序图标,对应ActionBar.DISPLAY_SHOW_HOME
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("Audio Detail");
        //设置左上角返回键icon图标
        //actionBar.setHomeAsUpIndicator(R.drawable.back);
        toolBar.setNavigationOnClickListener(view -> {
            onBackPressed();
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            DaggerDagger2Component.builder()
                    .dagger2Module(new Dagger2Module(101,"zhangming"))
                    .build()
                    .inject(this);
            Toast.makeText(this,"DaggerBean = "+daggerBean.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
