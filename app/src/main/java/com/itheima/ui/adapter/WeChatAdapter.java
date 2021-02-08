package com.itheima.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itheima.smp.R;
import com.itheima.model.WeChatModel;

import java.util.ArrayList;

/**
 * @Subject 模拟微信聊天的Adapter适配器
 * @Author  zhangming
 * @Date    2021-02-07 16:35
 */
public class WeChatAdapter extends RecyclerView.Adapter<WeChatAdapter.ViewHolder> {
    /* 存放数据的集合 */
    private ArrayList<WeChatModel> chatLists;

    /* 通过构造函数传入数据 */
    public WeChatAdapter(ArrayList<WeChatModel> dataLists) {
        this.chatLists = dataLists;
    }

    @Override
    public WeChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        //布局加载器
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wechat_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeChatAdapter.ViewHolder viewHolder, int position) {
        WeChatModel chatModel = chatLists.get(position);
        if (chatModel.getType() == WeChatModel.SEND) {
            viewHolder.leftLayout.setVisibility(View.GONE);
            viewHolder.rightLayout.setVisibility(View.VISIBLE);
            viewHolder.rightNameTextView.setText(chatModel.getName());
            viewHolder.rightContentTextView.setText(chatModel.getContent());
            viewHolder.rightImageView.setImageResource(chatModel.getImgId());
        } else {
            viewHolder.rightLayout.setVisibility(View.GONE);
            viewHolder.leftLayout.setVisibility(View.VISIBLE);
            viewHolder.leftNameTextView.setText(chatModel.getName());
            viewHolder.leftContentTextView.setText(chatModel.getContent());
            viewHolder.leftImageView.setImageResource(chatModel.getImgId());
        }
    }

    @Override
    public int getItemCount() {
        return chatLists.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView leftImageView;
        TextView leftNameTextView;
        TextView leftContentTextView;
        LinearLayout leftLayout;

        ImageView rightImageView;
        TextView rightNameTextView;
        TextView rightContentTextView;
        LinearLayout rightLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            leftImageView = (ImageView) itemView.findViewById(R.id.iv_left_image);
            leftContentTextView = (TextView) itemView.findViewById(R.id.left_content);
            leftNameTextView = (TextView) itemView.findViewById(R.id.left_name);
            leftLayout = (LinearLayout) itemView.findViewById(R.id.left_bubble);

            rightImageView = (ImageView) itemView.findViewById(R.id.iv_right_image);
            rightContentTextView = (TextView) itemView.findViewById(R.id.right_content);
            rightNameTextView = (TextView) itemView.findViewById(R.id.right_name);
            rightLayout = (LinearLayout) itemView.findViewById(R.id.right_bubble);
        }
    }
}
