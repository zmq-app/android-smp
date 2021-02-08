package com.itheima.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itheima.smp.R;
import com.itheima.utils.CommonConstants;

/**
 * Created by zhangming on 2018/9/1.
 */

public class RequestMsgFragment extends Fragment {
    private View mFragmentView;

    private boolean isViewCreated; //Fragment的View加载完毕的标记
    private boolean isUIVisible;   //Fragment对用户可见的标记

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(mFragmentView == null) {
            mFragmentView = inflater.inflate(R.layout.fragment_request_msg, container, false);
        }
        return mFragmentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewCreated = true;
        lasyLoad();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //TODO isVisibleToUser这个boolean值表示:该Fragment的UI用户是否可见
        if(isVisibleToUser){
            isUIVisible = true;
            lasyLoad();
        }else {
            isUIVisible = false;
        }
    }

    @Override
    public void onDestroyView() {
        isViewCreated = false;
        isUIVisible = false;
        super.onDestroyView();
    }

    private void lasyLoad() {
        if (isViewCreated && isUIVisible) {
            Log.i(CommonConstants.TAG,"lasyLoad RequestMsgFragment...");
            //TODO 加载数据...

            isViewCreated = false;
            isUIVisible = false;
        }
    }
}
