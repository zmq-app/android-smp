package com.itheima.kotlin.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.itheima.smp.R

/**
 * @Subject LinearLayout直播列表条目适配器
 * @Author  zhangming
 * @Date    2021-01-29 16:25
 */
class TvBoxLiveConfListAdapter : RecyclerView.Adapter<TvBoxLiveConfListAdapter.ViewHolder> {
    private var mRecyclerView: RecyclerView? = null
    private var mContext: Context
    private var liveConfItemLists : ArrayList<String>

    constructor(context: Context, confItemLists: ArrayList<String>) {
        this.mContext = context
        this.liveConfItemLists = confItemLists
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var itemView = LayoutInflater.from(mContext).inflate(R.layout.item_recyclerview_linearlayout_liveconf, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemContent = liveConfItemLists?.get(position)
        holder?.tvConfListItem?.text = itemContent
        holder?.tvConfListItem.setOnClickListener(object: View.OnClickListener {
            override fun onClick(view: View?) {
                if (view != null) {
                    Toast.makeText(mContext, itemContent, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun getItemCount(): Int {
        return liveConfItemLists?.size
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        if (mRecyclerView == null) {
            mRecyclerView = recyclerView
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        if (mRecyclerView != null) {
            mRecyclerView = null
        }
    }

    class ViewHolder : RecyclerView.ViewHolder {
        var tvConfListItem : TextView
        constructor (itemView: View) : super(itemView) {
            tvConfListItem = itemView?.findViewById(R.id.tv_conf_list_item) as TextView
        }
    }
}