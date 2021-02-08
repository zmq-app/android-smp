package com.itheima.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @Subject 树形机构节点数据结构
 * @Author  zhangming
 * @Date    2021-01-26 18:53
 */
public class TreeNode<T> {
    /**
     * 当前节点id
     */
    private String id;

    /**
     * 父节点id
     */
    private String pid;

    /**
     * 节点数据实体类
     */
    private T data;

    /**
     * 设置开启 关闭的图片
     */
    public int iconExpand = -1, iconNoExpand = -1;

    /**
     * 节点名称
     */
    private String name;

    /**
     * 当前的级别
     */
    private int level;

    /**
     * 是否展开
     */
    private boolean isExpand = false;

    private int icon = -1;

    /**
     * 下一级的子Node
     */
    private List<TreeNode> children = new ArrayList<>();

    /**
     * 父Node
     */
    private TreeNode parent;

    /**
     * 是否被checked选中
     */
    private boolean isChecked;

    public TreeNode() {
    }

    public TreeNode(String id, String pid, String name, int level) {
        this.id = id;
        this.pid = pid;
        this.name = name;
        //this.level = level;
    }

    public TreeNode(String id, String pid, T data, String name) {
        this.id = id;
        this.pid = pid;
        this.data = data;
        this.name = name;
    }

    /**
     * 是否为根节点
     * @return
     */
    public boolean isRootNode() {
        return parent == null;
    }

    /**
     * 判断父节点是否展开
     *
     * @return
     */
    public boolean isParentExpand() {
        if (parent == null)
            return false;
        return parent.isExpand();
    }

    /**
     * 是否是叶子节点
     * @return
     */
    public boolean isLeaf() {
        return children.size() == 0;
    }

    /**
     * 获取当前的级别level
     */
    public int getLevel() {
        return parent == null ? 0 : parent.getLevel() + 1;
        //return parent == null ? 0 : level;
    }

    /**
     * 设置展开
     * @param isExpand
     */
    public void setExpand(boolean isExpand) {
        this.isExpand = isExpand;
        if (!isExpand) {
            for (TreeNode treeNode : children) {
                treeNode.setExpand(isExpand);
            }
        }
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getIconExpand() {
        return iconExpand;
    }

    public void setIconExpand(int iconExpand) {
        this.iconExpand = iconExpand;
    }

    public int getIconNoExpand() {
        return iconNoExpand;
    }

    public void setIconNoExpand(int iconNoExpand) {
        this.iconNoExpand = iconNoExpand;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }

    public TreeNode getParent() {
        return parent;
    }

    public void setParent(TreeNode parent) {
        this.parent = parent;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
