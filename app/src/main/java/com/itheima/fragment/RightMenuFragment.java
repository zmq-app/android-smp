package com.itheima.fragment;

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
import com.itheima.utils.CommonConstants;
import com.itheima.utils.RxBus;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class RightMenuFragment extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_right_menu, container, false);
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

    @OnClick(R.id.tv_right_menu)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_right_menu:
                RxBus.getInstance().postEvent(new Event(1000,"closeMenu"));
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
            Log.i(CommonConstants.TAG, "RightMenuFragment onEvent,msg = " + event.getMessage());
        }
    }
}
