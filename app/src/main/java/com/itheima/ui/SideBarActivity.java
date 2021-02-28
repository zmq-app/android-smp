package com.itheima.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import com.itheima.model.ConfItemBean;
import com.itheima.smp.R;
import com.itheima.ui.adapter.SideBarConfListAdapter;
import com.itheima.utils.ScreenInfoUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * @Subject 自定义滑动侧边栏SideBar案例
 * @Author  zhangming
 * @Date    2021-02-28 16:06
 */
public class SideBarActivity extends AppCompatActivity {
    private DrawerLayout sideDrawer;
    private NavigationView sideBarView;
    private RecyclerView rvConfLists;
    private SideBarConfListAdapter sideBarConfListAdapter;
    private Button btnSwitchSideBar;

    private ArrayList<ConfItemBean> confLists = new ArrayList();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenInfoUtils.fullScreen(this);
        setContentView(R.layout.activity_sidebar);
        initView();
        addData();
        setListeners();
    }

    private void initView() {
        sideDrawer = getDelegate().findViewById(R.id.side_drawer);
        sideBarView = getDelegate().findViewById(R.id.side_bar_view);
        rvConfLists = getDelegate().findViewById(R.id.rv_conf_lists);
        btnSwitchSideBar = getDelegate().findViewById(R.id.btn_switch_side_bar);

        sideBarConfListAdapter = new SideBarConfListAdapter(confLists);
        rvConfLists.setAdapter(sideBarConfListAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvConfLists.setLayoutManager(layoutManager);


        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rvConfLists.addItemDecoration(itemDecoration);

        /*--------------------------- 使用Fragment方式自定义侧边栏布局 -----------------------------*/
        //getSupportFragmentManager().beginTransaction().replace(R.id.side_bar_view,new SideBarViewFragment()).commit();
    }

    private void addData() {
        for (int i=0; i<30; i++) {
            ConfItemBean confItemBean = new ConfItemBean();
            confItemBean.setConfName("hello"+i);
            confLists.add(confItemBean);
        }
        if (sideBarConfListAdapter != null) {
            sideBarConfListAdapter.notifyDataSetChanged();
        }
    }

    private void setListeners() {
        btnSwitchSideBar.setOnClickListener(v -> {
            if (sideDrawer.isDrawerOpen(sideBarView)) {
                sideDrawer.closeDrawer(sideBarView, true);
            } else {
                sideDrawer.openDrawer(sideBarView, true);
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if ((sideDrawer != null) && hasFocus) {
            setDrawerRightEdgeSize(sideDrawer);
        }
    }

    private void setDrawerRightEdgeSize(DrawerLayout drawerLayout) {
        if (drawerLayout == null) {
            return;
        }
        try {
            //找到ViewDragHelper并设置Accessible为true
            Field rightDraggerField = drawerLayout.getClass().getDeclaredField("mRightDragger");//Right
            rightDraggerField.setAccessible(true);
            ViewDragHelper rightDragger = (ViewDragHelper) rightDraggerField.get(drawerLayout);

            //找到edgeSizeField,并设置Accessible为true
            Field edgeSizeField = rightDragger.getClass().getDeclaredField("mEdgeSize");
            edgeSizeField.setAccessible(true);
            int edgeSize = edgeSizeField.getInt(rightDragger);

            //设置新的边缘大小
            edgeSizeField.setInt(rightDragger, Math.max(edgeSize, sideBarView.getMeasuredWidth()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
