package com.itheima.kotlin.ui

import android.graphics.Rect
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.itheima.smp.R
import com.itheima.kotlin.adapter.TvBoxLiveConfChannelAdapter
import com.itheima.kotlin.adapter.TvBoxLiveConfListAdapter

/**
 * @Subject 内蒙古广电机顶盒会议直播页面Activity,使用RecyclerView LinearLayoutManager线性布局管理器 or GridLayoutManager网格布局管理器
 * @Author  zhangming
 * @Date    2021-01-28 17:28
 */
class TvBoxLiveConfActivity : AppCompatActivity() {
    private lateinit var rvConfList: RecyclerView
    private lateinit var rvChannelList: RecyclerView
    private lateinit var liveConfListAdapter: TvBoxLiveConfListAdapter
    private lateinit var liveConfChannelAdapter: TvBoxLiveConfChannelAdapter

    private var confLists = ArrayList<String>()
    private var imageUrlLists = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tvbox_liveconf)
        initConfView()
        initChannelView()
        addLiveConfItemLists()
        addLiveConfChannelImages()
    }

    private fun initConfView() {
        /* 初始化LinearLayout版的RecyclerView */
        rvConfList = findViewById(R.id.tvbox_conflist) as RecyclerView
        /* 初始化直播列表条目Adapter */
        liveConfListAdapter = TvBoxLiveConfListAdapter(this, confLists)
        rvConfList.adapter = liveConfListAdapter
        /* RecyclerView控件设置布局管理器为LinearLayoutManager */
        rvConfList.layoutManager = LinearLayoutManager(this)
    }

    private fun initChannelView() {
        /* 初始化Gridlayout版的RecyclerView  */
        rvChannelList = findViewById(R.id.tvbox_channel_gridlayout) as RecyclerView
        /* 初始化直播频道条目图片Adapter */
        liveConfChannelAdapter = TvBoxLiveConfChannelAdapter(this, imageUrlLists)
        rvChannelList.adapter = liveConfChannelAdapter
        /* RecyclerView控件设置布局管理器为GridLayoutManager,条目每行两列排布 */
        var gridLayoutManager = GridLayoutManager(this, 2)
        gridLayoutManager.orientation = GridLayoutManager.VERTICAL
        rvChannelList.layoutManager = gridLayoutManager
        /* Gridlayout版的RecyclerView添加分割线 */
        //rvList.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL))
        //rvList.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        /* Gridlayout版的RecyclerView每个条目设置边距 */
        rvChannelList.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                var index = parent.getChildLayoutPosition(view)
                if ((index % 2) == 0) {
                    if (index == 0) {
                        /* 起始行第0行不设置上边距 */
                        outRect.set(0, 0, 0, 0)
                    } else {
                        /* 第0列非起始行的左边距为0,上边距为20 */
                        outRect.set(0, 20, 0, 0)
                    }
                } else {
                    if (index == 1) {
                        /* 起始行第0行不设置上边距  */
                        outRect.set(20, 0, 0, 0)
                    } else {
                        /* 第1列非起始行的左边距为20,上边距为20 */
                        outRect.set(20, 20, 0, 0)
                    }
                }
            }
        })
    }

    private fun addLiveConfItemLists() {
        confLists.add("hello111")
        confLists.add("hello222")
        confLists.add("hello333")
        confLists.add("hello444")
        confLists.add("hello555")
        confLists.add("hello666")
        confLists.add("hello777")
        confLists.add("hello888")
        confLists.add("hello999")
        rvConfList.adapter?.notifyDataSetChanged()
    }

    private fun addLiveConfChannelImages() {
        imageUrlLists.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1601266292514&di=95c75c1bdc303eaca8cb525ee5d596fa&imgtype=0&src=http%3A%2F%2Fp.ssl.qhimg.com%2Fbdr%2F__85%2Fd%2F_open360%2Fcar0911%2F10.jpg")
        imageUrlLists.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1601266292514&di=95c75c1bdc303eaca8cb525ee5d596fa&imgtype=0&src=http%3A%2F%2Fp.ssl.qhimg.com%2Fbdr%2F__85%2Fd%2F_open360%2Fcar0911%2F10.jpg")
        imageUrlLists.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1601266292514&di=95c75c1bdc303eaca8cb525ee5d596fa&imgtype=0&src=http%3A%2F%2Fp.ssl.qhimg.com%2Fbdr%2F__85%2Fd%2F_open360%2Fcar0911%2F10.jpg")
        imageUrlLists.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1601266292514&di=95c75c1bdc303eaca8cb525ee5d596fa&imgtype=0&src=http%3A%2F%2Fp.ssl.qhimg.com%2Fbdr%2F__85%2Fd%2F_open360%2Fcar0911%2F10.jpg")
        imageUrlLists.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1601266292514&di=95c75c1bdc303eaca8cb525ee5d596fa&imgtype=0&src=http%3A%2F%2Fp.ssl.qhimg.com%2Fbdr%2F__85%2Fd%2F_open360%2Fcar0911%2F10.jpg")
        imageUrlLists.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1601266292514&di=95c75c1bdc303eaca8cb525ee5d596fa&imgtype=0&src=http%3A%2F%2Fp.ssl.qhimg.com%2Fbdr%2F__85%2Fd%2F_open360%2Fcar0911%2F10.jpg")
        imageUrlLists.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1601266292514&di=95c75c1bdc303eaca8cb525ee5d596fa&imgtype=0&src=http%3A%2F%2Fp.ssl.qhimg.com%2Fbdr%2F__85%2Fd%2F_open360%2Fcar0911%2F10.jpg")
        imageUrlLists.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1601266292514&di=95c75c1bdc303eaca8cb525ee5d596fa&imgtype=0&src=http%3A%2F%2Fp.ssl.qhimg.com%2Fbdr%2F__85%2Fd%2F_open360%2Fcar0911%2F10.jpg")
        imageUrlLists.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1601266292514&di=95c75c1bdc303eaca8cb525ee5d596fa&imgtype=0&src=http%3A%2F%2Fp.ssl.qhimg.com%2Fbdr%2F__85%2Fd%2F_open360%2Fcar0911%2F10.jpg")
        imageUrlLists.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1601266292514&di=95c75c1bdc303eaca8cb525ee5d596fa&imgtype=0&src=http%3A%2F%2Fp.ssl.qhimg.com%2Fbdr%2F__85%2Fd%2F_open360%2Fcar0911%2F10.jpg")
        imageUrlLists.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1601266292514&di=95c75c1bdc303eaca8cb525ee5d596fa&imgtype=0&src=http%3A%2F%2Fp.ssl.qhimg.com%2Fbdr%2F__85%2Fd%2F_open360%2Fcar0911%2F10.jpg")
        imageUrlLists.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1601266292514&di=95c75c1bdc303eaca8cb525ee5d596fa&imgtype=0&src=http%3A%2F%2Fp.ssl.qhimg.com%2Fbdr%2F__85%2Fd%2F_open360%2Fcar0911%2F10.jpg")
        rvChannelList.adapter?.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (confLists.size > 0) {
            confLists.clear()
        }
        if (imageUrlLists.size > 0) {
            imageUrlLists.clear()
        }
    }
}