package com.itheima.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.itheima.fragment.MainVerticalFragment;
import com.itheima.model.Item;

import java.util.ArrayList;
import java.util.List;

public class VerticalPagerAdapter extends FragmentStatePagerAdapter {
    private List<Item> dataList = new ArrayList<>();

    public VerticalPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    public void setDataList(List<Item> data){
        dataList.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return MainVerticalFragment.instance(dataList.get(position));
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    public String getLastItemId(){
        if (dataList.size()==0){
            return "0";
        }
        Item item = dataList.get(dataList.size()-1);
        return item.getId();
    }

    public String getLastItemCreateTime(){
        if (dataList.size()==0){
            return "0";
        }
        Item item = dataList.get(dataList.size()-1);
        return item.getCreate_time();
    }
}
