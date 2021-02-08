package com.itheima.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itheima.smp.R;
import com.itheima.model.Event;
import com.itheima.model.MessageEvent;
import com.itheima.model.PeopleBean;
import com.itheima.ui.ArtActivity;
import com.itheima.utils.CommonConstants;
import com.itheima.utils.RxBus;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class LeftMenuFragment extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_left_menu, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.tv_close_left_menu, R.id.tv_article_menu, R.id.tv_video_menu})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_close_left_menu:
                RxBus.getInstance().postEvent(new Event(1000,"closeMenu"));
                break;
            case R.id.tv_article_menu:
                Intent intent = new Intent(getActivity(), ArtActivity.class);
                intent.putExtra("mode",1);
                intent.putExtra("title","Article");
                /* intent传递List对象集合,PeopleBean类需要实现Serializable接口,通过将List集合强转成Serializable传递 */
                intent.putExtra("intent_list", (Serializable) getPeopleLists());
                startActivity(intent);
                /* Activity切换动画[资源参考aosp源码的"frameworks/base/core/res/res/anim/activity_open_enter.xml,activity_open_exit.xml"] */
                getActivity().overridePendingTransition(R.anim.activity_open_enter,R.anim.activity_open_exit);
                break;
            case R.id.tv_video_menu:
                break;
            default:
                break;
        }
    }

    /* 订阅方法,当接收到事件的时候,会调用该方法 */
    /* ThreadMode.POSTING(默认): 该事件在哪个线程发布出来的,事件处理函数就会在这个线程中执行 */
    /* ThreadMode.BACKGROUND: 此事件处理函数中禁止进行UI更新操作 */
    /* ThreadMode.ASYNC: 新建子线程中执行,此事件处理函数中禁止进行UI更新操作 */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent event) {
        if (event != null) {
            Log.i(CommonConstants.TAG, "LeftMenuFragment onEvent,msg = " + event.getMessage());
        }
    }

    private List<PeopleBean> getPeopleLists() {
        List<PeopleBean> peopleBeans = new ArrayList<>();
        PeopleBean peopleBean1 = new PeopleBean();
        peopleBean1.showLine = true;
        peopleBean1.isCheck = true;
        peopleBean1.convertId = "ztandroid1";
        peopleBean1.text = "高健";
        peopleBean1.value = "27345@13770";
        peopleBean1.extendProperty = "+99111812299921";
        peopleBean1.fullDepName = "金融科技委员会/科技研发部/济南研发中心/业务研发二部";

        PeopleBean peopleBean2 = new PeopleBean();
        peopleBean2.showLine = true;
        peopleBean2.isCheck = true;
        peopleBean2.convertId = "ztandroid2";
        peopleBean2.text = "高健";
        peopleBean2.value = "27345@13770";
        peopleBean2.extendProperty = "+99111812299922";
        peopleBean2.fullDepName = "金融科技委员会2/科技研发部2/济南研发中心/业务研发二部";

        PeopleBean peopleBean3 = new PeopleBean();
        peopleBean3.showLine = true;
        peopleBean3.isCheck = true;
        peopleBean3.convertId = "ztandroid3";
        peopleBean3.text = "高健";
        peopleBean3.value = "27345@13770";
        peopleBean3.extendProperty = "+99111812299923";
        peopleBean3.fullDepName = "金融科技委员会3/科技研发部3/济南研发中心/业务研发二部";

        peopleBeans.add(peopleBean1);
        peopleBeans.add(peopleBean2);
        peopleBeans.add(peopleBean3);

        return peopleBeans;
    }
}
