package com.itheima.kotlin.ui

import android.app.Activity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import com.itheima.smp.R
import com.itheima.kotlin.adapter.ConfAdapter
import com.itheima.kotlin.bean.DataBean
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.constant.SpinnerStyle
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader

/**
 * @Subject 最基本的Kotlin版RecyclerView列表案例,适配器使用继承BaseQuickAdapter的方式
 * @author  zhangming
 * @Date    2021-01-05 17:35
 */
class ConfActivity : Activity() {
    private lateinit var confAdapter: ConfAdapter
    private lateinit var rvList: RecyclerView
    private lateinit var refreshLayout: SmartRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conf)
        initView()
        addData()
    }

    private fun initView() {
        initRefreshLayout()
        initAdapter()
        rvList = (findViewById(R.id.rvList)) as RecyclerView
        rvList.adapter = confAdapter
        rvList.layoutManager = LinearLayoutManager(this)
    }

    private fun initRefreshLayout() {
        //初始化
        refreshLayout = (findViewById(R.id.refreshLayout)) as SmartRefreshLayout
        //设置头部
        refreshLayout.setRefreshHeader(ClassicsHeader(this).setSpinnerStyle(SpinnerStyle.Translate)
            .setAccentColor(ContextCompat.getColor(this, R.color.green))
        )
        //设置尾部
        refreshLayout.setRefreshFooter(ClassicsFooter(this).setSpinnerStyle(SpinnerStyle.Translate))

        //监听刷新
        refreshLayout.setOnRefreshListener {
            refreshLayout.finishRefresh(300)
        }
        //是否启用刷新
        refreshLayout.setEnableRefresh(true)
        refreshLayout.setEnableLoadMore(false)
        //设置是否在没有更多数据之后Footer跟随内容
        refreshLayout.setEnableFooterFollowWhenNoMoreData(true)
    }

    private fun initAdapter() {
        confAdapter = ConfAdapter(mutableListOf())
        confAdapter.setOnItemChildClickListener { adapter, view, position ->
            when (view?.id) {
                R.id.rl_item_conf -> { //item条目的根布局rl_item_conf
                    var dataBean = (confAdapter.getItem(position)) as DataBean
                    Toast.makeText(this@ConfActivity,"dataBean = "+dataBean?.toString(),Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun addData() {
        var dataList: ArrayList<DataBean> = ArrayList()
        dataList.add(DataBean("hello1","2021-01-01"))
        dataList.add(DataBean("hello2","2021-01-02"))
        dataList.add(DataBean("hello3","2021-01-03"))
        confAdapter.replaceData(dataList)
    }
}