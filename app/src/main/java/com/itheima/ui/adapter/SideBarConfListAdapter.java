package com.itheima.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.itheima.model.ConfItemBean;
import com.itheima.smp.R;

import java.util.ArrayList;

/**
 * @Subject 侧边栏SideBar会场列表适配器Adapter
 * @Author  zhangming
 * @Date    2021-02-28 16:30
 */
public class SideBarConfListAdapter extends RecyclerView.Adapter<SideBarConfListAdapter.ViewHolder> {
    private ArrayList<ConfItemBean> mConfLists;

    public SideBarConfListAdapter(ArrayList<ConfItemBean> confLists) {
        this.mConfLists = confLists;
    }

    @NonNull
    @Override
    public SideBarConfListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //布局加载器
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sidebar_conflist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SideBarConfListAdapter.ViewHolder viewHolder, int position) {
        ConfItemBean confItemBean = mConfLists.get(position);
        if ((confItemBean != null) && !TextUtils.isEmpty(confItemBean.getConfName())) {
            viewHolder.tvConfName.setText(confItemBean.getConfName());
        }
    }

    @Override
    public int getItemCount() {
        return (mConfLists != null)?(mConfLists.size()):(0);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivConfImgView;
        TextView tvConfName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivConfImgView = (ImageView) itemView.findViewById(R.id.iv_status);
            tvConfName = (TextView) itemView.findViewById(R.id.tv_conf_name);
        }
    }
}
