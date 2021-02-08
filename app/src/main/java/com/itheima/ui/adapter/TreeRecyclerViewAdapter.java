package com.itheima.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.smp.R;
import com.itheima.model.TreeNode;
import com.itheima.utils.CommonUtils;

import java.util.List;

/**
 * @Subject 树形结构的RecyclerView的实际适配器adapter
 * @Author  zhangming
 * @Date    2021-01-26 19:53
 */
public class TreeRecyclerViewAdapter extends BaseTreeRecyclerAdapter {
    public TreeRecyclerViewAdapter(Context context, List<TreeNode> datas, int defaultExpandLevel, int iconExpand, int iconNoExpand) {
        super(context, datas, defaultExpandLevel, iconExpand, iconNoExpand);
    }

    public TreeRecyclerViewAdapter(Context context, List<TreeNode> datas, int defaultExpandLevel) {
        super(context, datas, defaultExpandLevel);
    }

    private OnTreeNodeCheckedChangeListener checkedChangeListener;
    public void setCheckedChangeListener(OnTreeNodeCheckedChangeListener checkedChangeListener) {
        this.checkedChangeListener = checkedChangeListener;
    }
    public interface OnTreeNodeCheckedChangeListener {
        void onCheckChange(TreeNode treeNode, int position, boolean isChecked);
    }

    @Override
    public void onBindViewHolder(final TreeNode treeNode, final RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder viewHolder = (ViewHolder) holder;
        final int nodeLevel = treeNode.getLevel();
        final int rootImgNodeLeftMargin = CommonUtils.dip2px(mContext,15);
        final int rootTextNodeLeftMargin = CommonUtils.dip2px(mContext,50);

        /* 动态设置ImageView收缩展开控件的左边距以及其位置参数为垂直居中 */
        if (viewHolder.ivExpand != null) {
            RelativeLayout.LayoutParams ivNodeParams = new RelativeLayout.LayoutParams(
                    CommonUtils.dip2px(mContext,35), RelativeLayout.LayoutParams.WRAP_CONTENT);
            ivNodeParams.leftMargin = rootImgNodeLeftMargin + nodeLevel * 50;
            ivNodeParams.addRule(RelativeLayout.CENTER_VERTICAL); //代码方式设置RelativeLayout子控件垂直居中
            viewHolder.ivExpand.setLayoutParams(ivNodeParams);
            if (treeNode.getIcon() == -1) {
                viewHolder.ivExpand.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.ivExpand.setVisibility(View.VISIBLE);
                viewHolder.ivExpand.setImageResource(treeNode.getIcon());
            }
        }

        /* 动态设置TextView节点名称控件的左边距 */
        if (viewHolder.tvNodeName != null) {
            RelativeLayout.LayoutParams tvNodeParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            tvNodeParams.leftMargin = rootTextNodeLeftMargin + nodeLevel * 50;
            viewHolder.tvNodeName.setLayoutParams(tvNodeParams);
            viewHolder.tvNodeName.setText(treeNode.getName());
        }


        /* checkbox点击事件配置和选中与否,此处直接在XML布局中干掉此控件,不再使用勾选框 */
        if (viewHolder.checkBox != null) {
            viewHolder.checkBox.setOnClickListener(v -> {
                setChecked(treeNode, viewHolder.checkBox.isChecked());
                if (checkedChangeListener != null) {
                    checkedChangeListener.onCheckChange(treeNode, position, viewHolder.checkBox.isChecked());
                }
            });
            if (treeNode.isChecked()) {
                viewHolder.checkBox.setChecked(true);
            } else {
                viewHolder.checkBox.setChecked(false);
            }
            //设置仅叶子节点显示勾选框CheckBox
            //if (treeNode.isLeaf()) {
            //    viewHolder.checkBox.setVisibility(View.VISIBLE);
            //} else {
            //    viewHolder.checkBox.setVisibility(View.GONE);
            //}
        }


        /* 设置TextView节点控件的点击回调事件 */
        viewHolder.tvNodeName.setOnClickListener(v-> {
            final String content = viewHolder.tvNodeName.getText().toString().trim();
            Toast.makeText(mContext, content, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View treeNodeItem = View.inflate(mContext, R.layout.activity_recycler_view_tree_item, null);
        return new ViewHolder(treeNodeItem);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public CheckBox checkBox;
        public TextView tvNodeName;
        public ImageView ivExpand;

        public ViewHolder(View itemView) {
            super(itemView);
            ivExpand = (ImageView) itemView.findViewById(R.id.iv_expand);
            checkBox = (CheckBox) itemView.findViewById(R.id.ck_box);
            tvNodeName = (TextView) itemView.findViewById(R.id.tv_node_name);
        }
    }
}
