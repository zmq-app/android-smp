package com.itheima.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.itheima.app.SmpApplication;
import com.itheima.smp.R;
import com.itheima.di.components.DaggerMainComponent;
import com.itheima.di.modules.MainModule;
import com.itheima.model.Item;
import com.itheima.presenter.MainContract;
import com.itheima.presenter.MainPresenter;
import com.itheima.ui.adapter.VerticalPagerAdapter;
import com.itheima.ui.view.VerticalViewPager;
import com.itheima.utils.CommonUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Subject owspace项目中MVP模式,Dagger2框架,垂直ViewPager控件学习
 * @URL     https://blog.csdn.net/gfg156196/article/details/85097588
 * @Author  zhangming
 * @Date    2020-11-01 16:24
 */
public class MvpMainActivity extends AppCompatActivity implements MainContract.View {
    @BindView(R.id.view_pager)
    VerticalViewPager viewPager;
    @Inject
    MainPresenter presenter;

    private VerticalPagerAdapter pagerAdapter;

    private int page = 1;
    private boolean isLoading = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mvp_main);
        ButterKnife.bind(this); //使用ButterKnife BindView注解

        initViews();
        initData();
        loadData(1, 0, "0", "0");
    }

    private void initViews() {
        pagerAdapter = new VerticalPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (pagerAdapter.getCount() <= position + 2 && !isLoading) {
                    if (isLoading){
                        Toast.makeText(MvpMainActivity.this,"正在努力加载...",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Log.i("zhangming","MvpMainActivity page = " + page + " getLastItemId = " + pagerAdapter.getLastItemId());
                    loadData(page, 0, pagerAdapter.getLastItemId(), pagerAdapter.getLastItemCreateTime());
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initData() {
        DaggerMainComponent.builder()
            .mainModule(new MainModule(this))
            .netComponent(SmpApplication.get(this).getNetComponent())
            .build()
            .inject(this);
    }

    private void loadData(int page, int mode, String pageId, String createTime) {
        isLoading = true;
        final String deviceId = CommonUtils.getDeviceId(this);
        presenter.getListByPage(page, mode, pageId, deviceId, createTime);
    }

    @Override
    public void showNoMore() {
        Toast.makeText(this, "没有更多数据了", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showOnFailure() {
        Toast.makeText(this, "加载数据失败,请检查您的网络", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateListUI(List<Item> itemList) {
        Log.i("zhangming","MvpMainActivity updateListUI itemList.size = "+itemList.size());
        isLoading = false;
        pagerAdapter.setDataList(itemList);
        page++;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
