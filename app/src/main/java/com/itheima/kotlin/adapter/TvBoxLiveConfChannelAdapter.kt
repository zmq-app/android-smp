package com.itheima.kotlin.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.itheima.smp.R
import com.itheima.kotlin.util.scale
import com.itheima.utils.CommonUtils
import java.lang.Exception

/**
 * @Subject GridLayout直播频道条目Image适配器
 * @Author  zhangming
 * @Date    2021-01-29 10:00
 */
class TvBoxLiveConfChannelAdapter : RecyclerView.Adapter<TvBoxLiveConfChannelAdapter.ViewHolder> {
    private var mContext: Context
    private var liveConfImageLists : ArrayList<String>

    constructor(context: Context, imageLists: ArrayList<String>) {
        this.mContext = context
        this.liveConfImageLists = imageLists
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var itemView = LayoutInflater.from(mContext).inflate(R.layout.item_recyclerview_gridlayout_liveconf, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val imageUrl = liveConfImageLists.get(position)
            CommonUtils.GlideAppLoadImage(mContext, holder?.imgView, imageUrl)
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return liveConfImageLists?.size
    }

    class ViewHolder : RecyclerView.ViewHolder {
        var imgView : ImageView
        constructor (itemView: View) : super(itemView) {
            imgView = itemView?.findViewById(R.id.image_live_conf) as ImageView
            imgView.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    v.scale(1.1f, 1.1f, 300)
                } else {
                    v.scale(1.0f, 1.0f, 300)
                }
            }
        }
    }
}