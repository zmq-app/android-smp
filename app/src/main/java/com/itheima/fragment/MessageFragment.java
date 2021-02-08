package com.itheima.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itheima.smp.R;
import com.itheima.ui.SlideViewPager;
import com.itheima.utils.CommonConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangming on 2018/9/1.
 */

public class MessageFragment extends Fragment{
    private View mFragmentView;
    private SlideViewPager msgViewPager;
    private List<Fragment> childMsgFragments;

    private ChatMsgFragment cMsgFragment;
    private RequestMsgFragment rMsgFragment;
    private MsgPagerAdapter mAdapter;

    private boolean isViewCreated; //Fragment的View加载完毕的标记
    private boolean isUIVisible;   //Fragment对用户可见的标记

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(mFragmentView == null){
            mFragmentView = inflater.inflate(R.layout.fragment_message,container,false);
        }
        return mFragmentView;
    }

    //TODO onViewCreated方法在onCreateView方法之后被调用
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewCreated = true;
        initView();
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
        Log.i(CommonConstants.TAG,"MessageFragment onDestroyView...");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(CommonConstants.TAG,"MessageFragment onDestroy...");
    }

    private void initView(){
        if(msgViewPager == null){
            msgViewPager = (SlideViewPager) mFragmentView.findViewById(R.id.msg_viewpager);
            msgViewPager.setSlide(true);            //设置是否可以进行滑动
            msgViewPager.setOffscreenPageLimit(1);  //设置viewpager缓存页数[最小值=1]
        }

        if(childMsgFragments == null){
            childMsgFragments = new ArrayList<>();
            cMsgFragment = new ChatMsgFragment();
            rMsgFragment = new RequestMsgFragment();
            childMsgFragments.add(cMsgFragment);
            childMsgFragments.add(rMsgFragment);
        }

        if(mAdapter == null){
            FragmentManager fm = getFragmentManager();  //Fragment中获取FragmentManager管理类的实例对象
            mAdapter = new MsgPagerAdapter(fm,childMsgFragments);
            msgViewPager.setAdapter(mAdapter);
        }
    }

    private void lasyLoad(){
        if(isViewCreated && isUIVisible){
            Log.i(CommonConstants.TAG,"lasyLoad MessageFragment...");

            //TODO 防止数据重复加载
            isViewCreated = false;
            isUIVisible = false;
        }
    }

    static class MsgPagerAdapter extends FragmentStatePagerAdapter{
        private List<Fragment> fragmentList;

        public MsgPagerAdapter(FragmentManager fm,List<Fragment> fragmentList){
            super(fm);
            this.fragmentList = fragmentList;
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            Log.i(CommonConstants.TAG,"MsgPagerAdapter destroyItem");
            super.destroyItem(container, position, object);
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }
    }
}
