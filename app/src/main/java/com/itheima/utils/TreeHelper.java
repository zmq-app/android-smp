package com.itheima.utils;

import com.itheima.model.TreeNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @Subject 树形结构菜单帮助类
 * @Author  zhangming
 * @Date    2021-01-26
 */
public class TreeHelper {
    /**
     * 传入node,返回排序后的Node
     * 拿到用户传入的数据,转化为List<TreeNode>以及设置Node间关系,然后根节点,从根往下遍历进行排序
     * @param datas
     * @param defaultExpandLevel
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static List<TreeNode> getSortedNodes(List<TreeNode> datas, int defaultExpandLevel) {
        List<TreeNode> result = new ArrayList();
        // 设置Node间父子关系
        List<TreeNode> treeNodes = convetData2Node(datas);
        // 拿到根节点
        List<TreeNode> rootTreeNodes = getRootNodes(treeNodes);
        // 排序以及设置Node间关系
        for (TreeNode treeNode : rootTreeNodes) {
            addNode(result, treeNode, defaultExpandLevel, 1);
        }
        return result;
    }

    /**
     * 过滤出所有可见的Node
     * 过滤Node的代码很简单，遍历所有的Node，只要是根节点或者父节点是展开状态就添加返回
     * @param treeNodes
     * @return
     */
    public static List<TreeNode> filterVisibleNode(List<TreeNode> treeNodes) {
        List<TreeNode> result = new ArrayList();
        for (TreeNode treeNode : treeNodes) {
            // 如果为根节点,或者上层目录为展开状态
            if (treeNode.isRootNode() || treeNode.isParentExpand()) {
                setNodeIcon(treeNode);
                result.add(treeNode);
            }
        }
        return result;
    }

    /**
     * 将我们的数据转化为树的节点
     * 设置Node间,父子关系;让每两个节点都比较一次,即可设置其中的关系
     */
    private static List<TreeNode> convetData2Node(List<TreeNode> treeNodes) {
        for (int i = 0; i < treeNodes.size(); i++) {
            TreeNode n = treeNodes.get(i);
            for (int j = i + 1; j < treeNodes.size(); j++) {
                TreeNode m = treeNodes.get(j);
                if (m.getPid() instanceof String) {
                    if (m.getPid().equals(n.getId())) {
                        n.getChildren().add(m);
                        m.setParent(n);
                    } else if (m.getId().equals(n.getPid())) {
                        m.getChildren().add(n);
                        n.setParent(m);
                    }
                } else {
                    if (m.getPid() == n.getId()) {
                        n.getChildren().add(m);
                        m.setParent(n);
                    } else if (m.getId() == n.getPid()) {
                        m.getChildren().add(n);
                        n.setParent(m);
                    }
                }
            }
        }
        return treeNodes;
    }

    /**
     * 获得根节点
     * @param treeNodes
     * @return
     */
    private static List<TreeNode> getRootNodes(List<TreeNode> treeNodes) {
        List<TreeNode> root = new ArrayList();
        for (TreeNode treeNode : treeNodes) {
            if (treeNode.isRootNode()) {
                root.add(treeNode);
            }
        }
        return root;
    }

    /**
     * 把一个节点上的所有的内容都挂上去
     * 通过递归的方式，把一个节点上的所有的子节点等都按顺序放入
     */
    private static <T> void addNode(List<TreeNode> treeNodes, TreeNode<T> treeNode, int defaultExpandLeval, int currentLevel) {
        treeNodes.add(treeNode);
        if (defaultExpandLeval >= currentLevel) {
            treeNode.setExpand(true);
        }
        if (treeNode.isLeaf()) {
            return;
        }
        for (int i = 0; i < treeNode.getChildren().size(); i++) {
            addNode(treeNodes, treeNode.getChildren().get(i), defaultExpandLeval, currentLevel + 1);
        }
    }

    /**
     * 设置节点的图标
     * 初始化TreeRecyclerViewAdapter构造器时赋值
     * 展开状态 int treeNode.iconExpand = R.mipmap.bt_arrow_up_gray
     * 收缩状态 int treeNode.iconNoExpand = R.mipmap.bt_arrow_down_gray
     * 叶子节点 int iconId = -1
     * @param treeNode
     */
    private static void setNodeIcon(TreeNode treeNode) {
        if (treeNode.getChildren().size() > 0 && treeNode.isExpand()) {
            treeNode.setIcon(treeNode.iconExpand);
        } else if (treeNode.getChildren().size() > 0 && !treeNode.isExpand()) {
            treeNode.setIcon(treeNode.iconNoExpand);
        } else {
            treeNode.setIcon(-1);
        }
    }
}
