package com.itheima.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.itheima.smp.R;
import com.itheima.model.TreeNode;
import com.itheima.model.TreeNodeDataBean;
import com.itheima.ui.adapter.TreeRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @Subject  使用RecyclerView控件创建一颗多级树形选择列表
 * @Function AppCompatActivity从名字上来看,是compat系列中的一员,和ViewCompat,ContextCompat一样,是提供向下兼容功能的Activity.
 * 在AppCompatActivity类之前,不同版本的设备中,尤其是3.0设备之前,radioButton,EditText的外观都不一样.所以Android引入了AppCompatActivity来统一控件的外观风格,
 * 使得其在不同的设备上都能统一样式.在代码上,则是把TextView,EditView控件都自动转为了对应的CompatView类
 * AppCompatActivity主界面会带有toolbar的标题栏
 * @Author  zhangming
 * @Date    2021-01-26 21:35
 */
public class RecyclerViewTreeActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private TreeRecyclerViewAdapter mAdapter;
    private List<TreeNode> dataList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view_tree);

        /**
         * 增加getDelegate方法调用,获取android.support.v7.app.AppCompatDelegate实例,
         * 避免出现"对findViewById的方法引用不明确,Activity中的方法findViewById(int)和AppCompatActivity中的方法<T>findViewById(int)都匹配“的错误
         */
        mRecyclerView = getDelegate().findViewById(R.id.recycleviewTree);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        initData();

        //第一个参数  上下文
        //第二个参数  数据集
        //第三个参数  默认展开层级数 0为不展开
        //第四个参数  展开的图标
        //第五个参数  闭合的图标
        mAdapter = new TreeRecyclerViewAdapter(this, dataList, 0, R.mipmap.bt_arrow_up_gray, R.mipmap.bt_arrow_down_gray);
        mRecyclerView.setAdapter(mAdapter);

        //从BaseTreeRecyclerAdapter类中获取所有节点
        //final List<TreeNode> allNodes = mAdapter.getAllNodes();

        //选中状态监听
        mAdapter.setCheckedChangeListener((node, position, isChecked) -> {
            //获取所有选中节点
            List<TreeNode> selectedTreeNode = mAdapter.getSelectedNode();
            for (TreeNode n : selectedTreeNode) {
                Log.e("zhangming", "onCheckChange: " + n.getName());
            }
        });
    }

    /**
     * 模拟数据,实际开发中对返回的json数据进行封装
     */
    private void initData() {
        //根节点
        TreeNode<TreeNodeDataBean> treeNode = new TreeNode("0", "-1", "根节点",0);
        dataList.add(treeNode);

        //根节点的二级节点
        dataList.add(new TreeNode("3", "0", "二级节点",1));
        dataList.add(new TreeNode("4", "0", "二级节点",1));
        dataList.add(new TreeNode("5", "0", "二级节点",1));

        //三级节点
        dataList.add(new TreeNode("12", "3", "三级节点",2));
        dataList.add(new TreeNode("13", "3", "三级节点",2));
        dataList.add(new TreeNode("14", "3", "三级节点",2));

        dataList.add(new TreeNode("15", "4", "三级节点",2));
        dataList.add(new TreeNode("16", "4", "三级节点",2));
        dataList.add(new TreeNode("17", "4", "三级节点",2));

        dataList.add(new TreeNode("18", "5", "三级节点",2));
        dataList.add(new TreeNode("19", "5", "三级节点",2));
        dataList.add(new TreeNode("20", "5", "三级节点",2));

        //四级节点
        dataList.add(new TreeNode("21", "12", "四级节点",3));
        dataList.add(new TreeNode("22", "12", "四级节点",3));
        dataList.add(new TreeNode("23", "13", "四级节点",3));
        dataList.add(new TreeNode("24", "19", "四级节点",3));
        dataList.add(new TreeNode("25", "20", "四级节点",3));

        //五级节点
        dataList.add(new TreeNode("26", "21", "五级节点",4));
        dataList.add(new TreeNode("27", "22", "五级节点",4));
    }
}
