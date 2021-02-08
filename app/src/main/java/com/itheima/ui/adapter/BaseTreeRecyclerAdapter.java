package com.itheima.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.itheima.model.TreeNode;
import com.itheima.utils.TreeHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @Subject 树形结构的RecyclerView的抽象基类适配器Adapter
 * @Author  zhangming
 * @Date    2021-01-26 18:50
 */
public abstract class BaseTreeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    /**
     * 树形适配器上下文
     */
    protected Context mContext;

    /**
     * 默认不展开
     */
    private int defaultExpandLevel = 0;

    /**
     * 展开与关闭的图片
     */
    private int iconExpand = -1, iconNoExpand = -1;

    /**
     * 存储所有的Node
     */
    protected List<TreeNode> mAllTreeNodes = new ArrayList();

    /**
     * 存储所有可见的Node
     */
    protected List<TreeNode> mTreeNodes = new ArrayList();

    /**
     * View视图的加载器LayoutInflater实例
     */
    protected LayoutInflater mInflater;

    /**
     * 点击的回调接口
     */
    private OnTreeNodeClickListener onTreeNodeClickListener;

    public void setOnTreeNodeClickListener(OnTreeNodeClickListener onTreeNodeClickListener) {
        this.onTreeNodeClickListener = onTreeNodeClickListener;
    }

    public interface OnTreeNodeClickListener {
        void onClick(TreeNode treeNode, int position);
    }

    /**
     * RecyclerViewTree基类适配器Adapter的构造器
     * @param context
     * @param datas
     * @param defaultExpandLevel 默认展开几级树
     */
    public BaseTreeRecyclerAdapter(Context context, List<TreeNode> datas, int defaultExpandLevel) {
        this(context, datas, defaultExpandLevel, -1, -1);
    }

    public BaseTreeRecyclerAdapter(Context context, List<TreeNode> datas, int defaultExpandLevel, int iconExpand, int iconNoExpand) {
        mContext = context;
        this.defaultExpandLevel = defaultExpandLevel;
        this.iconExpand = iconExpand;
        this.iconNoExpand = iconNoExpand;

        for (TreeNode treeNode : datas) {
            treeNode.getChildren().clear();
            treeNode.iconExpand = iconExpand;
            treeNode.iconNoExpand = iconNoExpand;
        }

        /* 对所有的Node进行排序 */
        mAllTreeNodes = TreeHelper.getSortedNodes(datas, defaultExpandLevel);

        /* 过滤出可见的Node */
        mTreeNodes = TreeHelper.filterVisibleNode(mAllTreeNodes);

        /* View视图的加载器LayoutInflater实例 */
        mInflater = LayoutInflater.from(context);
    }

    /**
     * 抽象方法,子类覆写
     * @param treeNode
     * @param holder
     * @param position
     */
    public abstract void onBindViewHolder(TreeNode treeNode, RecyclerView.ViewHolder holder, final int position);

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        TreeNode treeNode = mTreeNodes.get(position);
        //convertView = getConvertView(treeNode, position, convertView, parent);
        /* 根据树节点层级大小,动态更改节点的左内边距 */
        /* @region zmq 注释此段代码,展开后会出现同级别节点不能对齐的bug,通过在子类的onBindViewHolder方法中通过setLayoutParams方法动态设置ivExpand,tvNodeName控件 */
        //holder.itemView.setPadding(nodeLevel * 50, 10, 10, 10);
        /* 设置节点点击时,可以展开以及关闭,将事件继续往外公布 */
        holder.itemView.setOnClickListener(v-> {
            expandOrCollapse(position);
            if (onTreeNodeClickListener != null) {
                onTreeNodeClickListener.onClick(mTreeNodes.get(position), position);
            }
        });
        onBindViewHolder(treeNode, holder, position);
    }

    @Override
    public int getItemCount() {
        return mTreeNodes.size();
    }


    /**
     * 获取排序后所有节点
     * @return
     */
    public List<TreeNode> getAllNodes() {
        if (mAllTreeNodes == null)
            mAllTreeNodes = new ArrayList<TreeNode>();
        return mAllTreeNodes;
    }

    /**
     * 获取所有选中节点
     * @return
     */
    public List<TreeNode> getSelectedNode() {
        List<TreeNode> checks = new ArrayList<TreeNode>();
        for (int i = 0; i < mAllTreeNodes.size(); i++) {
            TreeNode node = mAllTreeNodes.get(i);
            if (node.isChecked()) {
                checks.add(node);
            }
        }
        return checks;
    }


    /**
     * 相应RecycleView的点击事件 展开或关闭某节点
     * @param position
     */
    public void expandOrCollapse(int position) {
        TreeNode node = mTreeNodes.get(position);
        if (node != null) {// 排除传入参数错误异常
            if (!node.isLeaf()) {
                node.setExpand(!node.isExpand());
                mTreeNodes = TreeHelper.filterVisibleNode(mAllTreeNodes);
                notifyDataSetChanged();// 刷新视图
            }
        }
    }

    /**
     * 节点设置多选
     * @param treeNode
     * @param checked
     */
    protected void setChecked(final TreeNode treeNode, boolean checked) {
        treeNode.setChecked(checked);
        setChildChecked(treeNode, checked);
        /* @region zmq,勾选某节点时,无需级联勾选其父节点 [由于非叶子节点不再显示CheckBox勾选框,所以勾选叶子节点时,仅仅勾选其本身的node] */
        //if (treeNode.getParent() != null) {
        //    setNodeParentChecked(treeNode.getParent(), checked);
        //}
        notifyDataSetChanged();
    }

    /**
     * 节点设置是否选中
     * @param treeNode
     * @param checked
     */
    public <T> void setChildChecked(TreeNode<T> treeNode, boolean checked) {
        if (!treeNode.isLeaf()) {
            treeNode.setChecked(checked);
            for (TreeNode childrenTreeNode : treeNode.getChildren()) {
                setChildChecked(childrenTreeNode, checked);
            }
        } else {
            treeNode.setChecked(checked);
        }
    }

    private void setNodeParentChecked(TreeNode treeNode, boolean checked) {
        if (checked) {
            treeNode.setChecked(checked);
            if (treeNode.getParent() != null)
                setNodeParentChecked(treeNode.getParent(), checked);
        } else {
            List<TreeNode> childrens = treeNode.getChildren();
            boolean isChecked = false;
            for (TreeNode children : childrens) {
                if (children.isChecked()) {
                    isChecked = true;
                }
            }
            //如果所有自节点都没有被选中 父节点也不选中
            if (!isChecked) {
                treeNode.setChecked(checked);
            }
            if (treeNode.getParent() != null)
                setNodeParentChecked(treeNode.getParent(), checked);
        }
    }

    /**
     * 清除掉之前数据并刷新  重新添加
     * @param mlists
     * @param defaultExpandLevel 默认展开几级列表
     */
    public void addDataAll(List<TreeNode> mlists, int defaultExpandLevel) {
        mAllTreeNodes.clear();
        addData(-1, mlists, defaultExpandLevel);
    }

    /**
     * 在指定位置添加数据并刷新 可指定刷新后显示层级
     * @param index
     * @param mlists
     * @param defaultExpandLevel 默认展开几级列表
     */
    public void addData(int index, List<TreeNode> mlists, int defaultExpandLevel) {
        this.defaultExpandLevel = defaultExpandLevel;
        notifyData(index, mlists);
    }

    /**
     * 在指定位置添加数据并刷新
     * @param index
     * @param mlists
     */
    public void addData(int index, List<TreeNode> mlists) {
        notifyData(index, mlists);
    }

    /**
     * 添加数据并刷新
     * @param mlists
     */
    public void addData(List<TreeNode> mlists) {
        addData(mlists, defaultExpandLevel);
    }

    /**
     * 添加数据并刷新 可指定刷新后显示层级
     * @param mlists
     * @param defaultExpandLevel
     */
    public void addData(List<TreeNode> mlists, int defaultExpandLevel) {
        this.defaultExpandLevel = defaultExpandLevel;
        notifyData(-1, mlists);
    }

    /**
     * 添加数据并刷新
     * @param treeNode
     */
    public void addData(TreeNode treeNode) {
        addData(treeNode, defaultExpandLevel);
    }

    /**
     * 添加数据并刷新 可指定刷新后显示层级
     * @param treeNode
     * @param defaultExpandLevel
     */
    public void addData(TreeNode treeNode, int defaultExpandLevel) {
        List<TreeNode> treeNodes = new ArrayList<>();
        treeNodes.add(treeNode);
        this.defaultExpandLevel = defaultExpandLevel;
        notifyData(-1, treeNodes);
    }

    /**
     * 刷新数据
     * @param index
     * @param mListTreeNodes
     */
    private void notifyData(int index, List<TreeNode> mListTreeNodes) {
        for (int i = 0; i < mListTreeNodes.size(); i++) {
            TreeNode treeNode = mListTreeNodes.get(i);
            treeNode.getChildren().clear();
            treeNode.iconExpand = iconExpand;
            treeNode.iconNoExpand = iconNoExpand;
        }
        for (int i = 0; i < mAllTreeNodes.size(); i++) {
            TreeNode treeNode = mAllTreeNodes.get(i);
            treeNode.getChildren().clear();
        }
        if (index != -1) {
            mAllTreeNodes.addAll(index, mListTreeNodes);
        } else {
            mAllTreeNodes.addAll(mListTreeNodes);
        }
        /* 对所有的Node进行排序 */
        mAllTreeNodes = TreeHelper.getSortedNodes(mAllTreeNodes, defaultExpandLevel);
        /* 过滤出可见的Node */
        mTreeNodes = TreeHelper.filterVisibleNode(mAllTreeNodes);
        /* Adapter刷新数据 */
        notifyDataSetChanged();
    }
}
