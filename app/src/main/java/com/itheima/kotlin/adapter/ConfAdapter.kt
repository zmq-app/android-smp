package com.itheima.kotlin.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.itheima.smp.R
import com.itheima.kotlin.bean.DataBean

class ConfAdapter : BaseQuickAdapter<DataBean,BaseViewHolder> {
    constructor(data: MutableList<DataBean>?) : super(R.layout.recycle_item_conf, data)

    override fun convert(helper: BaseViewHolder, item: DataBean?) {
        item?.apply {
            helper.setText(R.id.tvConfName, confName)
            helper.setText(R.id.tvConfTime, confTime)
            /* 为rl_item_conf添加点击事件,使用setOnItemChildClickListener方法监听 */
            helper.addOnClickListener(R.id.rl_item_conf)
        }
    }
}