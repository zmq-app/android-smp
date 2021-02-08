package com.itheima.kotlin.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.itheima.smp.R
import com.itheima.kotlin.bean.CityBean

class CitySpinnerAdapter : BaseAdapter {
    private var mContext : Context
    private var dataList : ArrayList<CityBean>

    constructor(ctx: Context, dataList : ArrayList<CityBean>) { //次构造函数
        this.mContext = ctx
        this.dataList = dataList
    }

    override fun getCount(): Int {
        return dataList.size
    }

    override fun getItem(position: Int): Any {
        return dataList.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var itemView : View?
        var viewHolder : ViewHolder?

        if (convertView == null) {
            itemView = View.inflate(mContext, R.layout.spinner_city_item, null)
            viewHolder = ViewHolder(itemView)
        } else {
            itemView = convertView
            /* 通过itemView的tag变量,获取到ViewHolder实例 */
            viewHolder = (itemView?.tag) as ViewHolder
        }

        if (viewHolder?.tv_city_name != null) {
            viewHolder?.tv_city_name.setText((getItem(position) as CityBean).name)
        }

        /* itemView条目的点击事件监听 */
        //itemView?.setOnClickListener(object : View.OnClickListener {
        //    override fun onClick(v: View?) {
        //    }
        //})
        return itemView
    }

    private class ViewHolder {
        var tv_city_name : TextView
        constructor (convertView : View) {
            tv_city_name = (convertView.findViewById(R.id.tv_city_name)) as TextView
            /* 将当前ViewHolder实例设置到itemView的tag变量中保存 */
            convertView.setTag(this)
        }
    }
}