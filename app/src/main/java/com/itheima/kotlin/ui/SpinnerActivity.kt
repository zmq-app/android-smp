package com.itheima.kotlin.ui

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import com.itheima.smp.R
import com.itheima.kotlin.adapter.CitySpinnerAdapter
import com.itheima.kotlin.bean.CityBean
import kotlinx.android.synthetic.main.activity_spinner.*

/**
 * @Subject Spinner下拉列表框控件的基本使用,省市区三级菜单联动
 * @URL     https://github.com/supertaohaili/Spinner [开源项目 AppCompatTextView+PopupWindow 实现下拉选择]
 * @Author  zhangming
 * @Date    2021-01-16 23:13
 */
class SpinnerActivity : Activity() {
    /* 各个适配器填充的lists集合,属于动态变化 */
    private var provinceLists : ArrayList<CityBean> = ArrayList() //省集合
    private var cityLists : ArrayList<CityBean> = ArrayList() //市集合
    private var areaLists : ArrayList<CityBean> = ArrayList() //区集合

    private var pcMaps : HashMap<String,ArrayList<CityBean>> = HashMap() //key为省级id,省市关联Maps集合
    private var caMaps : HashMap<String,ArrayList<CityBean>> = HashMap() //key为市级id,市区关联Maps集合

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spinner)
        initView()
        addData()
    }


    private fun initView() {
        /* spinner控件的适配器,省级adapter关联provinceLists集合,市级adapter关联cityLists集合,区级adapter关联areaLists集合 */
        province_spinner.adapter = CitySpinnerAdapter(this, provinceLists)
        city_spinner.adapter = CitySpinnerAdapter(this, cityLists)
        area_spinner.adapter = CitySpinnerAdapter(this, areaLists)

        /* kotlin匿名类,object关键字 */
        province_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            /* spinner省市二级菜单联动效果(仅当省级spinner选中条目发生变化时,才会回调onItemSelected方法) */
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                /* 通过获取一级spinner对应的省,加载其下的市级list列表,存储到cityLists集合中 */
                var provinceBean = provinceLists.get(position)
                var pSelectLists = pcMaps.get(provinceBean?.id)
                cityLists.clear()
                cityLists.addAll(pSelectLists?:return)

                /* 重置city spinner控件的选中索引为第一个,通知CitySpinnerAdapter适配器使用最新的cityLists数据集合进行刷新 */
                city_spinner.setSelection(0, true)
                if ((city_spinner.adapter) is CitySpinnerAdapter) {
                    ((city_spinner.adapter) as CitySpinnerAdapter)?.notifyDataSetChanged()
                }

                /* 通过获取二级spinner对应的市,加载其下的区县级list列表,存储到areaLists集合中 */
                /* 注意此处市级索引位置position=0,即查找当前联动市对应的第一个区信息 */
                var cityBean = cityLists.get(0)
                var cSelectLists = caMaps.get(cityBean?.id)
                areaLists.clear()
                areaLists.addAll(cSelectLists?:return)

                /* 重置area spinner控件的选中索引为第一个,通知CitySpinnerAdapter适配器使用最新的areaLists数据集合进行刷新 */
                area_spinner.setSelection(0, true)
                if ((area_spinner.adapter) is CitySpinnerAdapter) {
                    ((area_spinner.adapter) as CitySpinnerAdapter)?.notifyDataSetChanged()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        city_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                /* 通过获取二级spinner对应的市,加载其下的区县级list列表,存储到areaLists集合中 */
                var cityBean = cityLists.get(position)
                var selectLists = caMaps.get(cityBean?.id)
                areaLists.clear()
                areaLists.addAll(selectLists?:return)

                /* 重置area spinner控件的选中索引为第一个,通知CitySpinnerAdapter适配器使用最新的areaLists数据集合进行刷新 */
                area_spinner.setSelection(0, true)
                if ((area_spinner.adapter) is CitySpinnerAdapter) {
                    ((area_spinner.adapter) as CitySpinnerAdapter)?.notifyDataSetChanged()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun addData() {
        var provinceBean1 = CityBean("101","湖北省")
        var provinceBean2 = CityBean("102","河北省")
        var provinceBean3 = CityBean("103","河南省")

        provinceLists.add(provinceBean1)
        provinceLists.add(provinceBean2)
        provinceLists.add(provinceBean3)
        province_spinner.setSelection(0, true)

        //ProvinceSpinner默认展示第一行的湖北省
        if ((province_spinner.adapter) is CitySpinnerAdapter) {
            ((province_spinner.adapter) as CitySpinnerAdapter)?.notifyDataSetChanged()
        }

        var cityList1 = ArrayList<CityBean>()
        var cityBean1 = CityBean("10101","武汉市")
        var cityBean2 = CityBean("10102","宜昌市")
        var cityBean3 = CityBean("10103","襄阳市")
        cityList1.clear()
        cityList1.add(cityBean1)
        cityList1.add(cityBean2)
        cityList1.add(cityBean3)
        pcMaps.put(provinceBean1.id,cityList1)

        var cityList2 = ArrayList<CityBean>()
        var cityBean4 = CityBean("10201","石家庄市")
        var cityBean5 = CityBean("10202","邯郸市")
        var cityBean6 = CityBean("10203","保定市")
        cityList2.add(cityBean4)
        cityList2.add(cityBean5)
        cityList2.add(cityBean6)
        pcMaps.put(provinceBean2.id,cityList2)

        var cityList3 = ArrayList<CityBean>()
        var cityBean7 = CityBean("10301","郑州市")
        var cityBean8 = CityBean("10302","信阳市")
        var cityBean9 = CityBean("10303","许昌市")
        cityList3.add(cityBean7)
        cityList3.add(cityBean8)
        cityList3.add(cityBean9)
        pcMaps.put(provinceBean3.id,cityList3)

        //CitySpinner默认展示湖北省下的武汉市
        cityLists.clear()
        cityLists.addAll((pcMaps.get(provinceBean1.id))?:return)
        //pcMaps.get("101")?.let { cityLists.addAll(it) }
        if ((city_spinner.adapter) is CitySpinnerAdapter) {
            ((city_spinner.adapter) as CitySpinnerAdapter)?.notifyDataSetChanged()
        }

        //使用双重循环方式初始化地区信息
        var ratio1 = 0
        for (cityBean:CityBean in cityList1) { //遍历湖北省下的每个市级
            var areaList1 = ArrayList<CityBean>()
            areaList1.clear()
            for (index in (1+(3*ratio1))..(3+(3*ratio1))) { //1,2,3  4,5,6  7,8,9
                var areaBean = CityBean(cityBean.id+index,"A"+index)
                areaList1.add(areaBean)
            }
            caMaps.put(cityBean.id,areaList1)
            ratio1++
        }

        var ratio2 = 0
        for (cityBean:CityBean in cityList2) { //遍历河北省下的每个市级
            var areaList2 = ArrayList<CityBean>()
            areaList2.clear()
            for (index in (1+(3*ratio2))..(3+(3*ratio2))) { //1,2,3  4,5,6  7,8,9
                var areaBean = CityBean(cityBean.id+index,"B"+index)
                areaList2.add(areaBean)
            }
            caMaps.put(cityBean.id,areaList2)
            ratio2++
        }

        var ratio3 = 0
        for (cityBean:CityBean in cityList3) { //遍历河南省下的每个市级
            var areaList3 = ArrayList<CityBean>()
            areaList3.clear()
            for (index in (1+(3*ratio3))..(3+(3*ratio3))) { //1,2,3  4,5,6  7,8,9
                var areaBean = CityBean(cityBean.id+index,"C"+index)
                areaList3.add(areaBean)
            }
            caMaps.put(cityBean.id,areaList3)
            ratio3++
        }

        //AreaSpinner默认展示湖北省武汉市下的A1区
        areaLists.clear()
        areaLists.addAll((caMaps.get(cityBean1.id))?:return)
        if ((area_spinner.adapter) is CitySpinnerAdapter) {
            ((area_spinner.adapter) as CitySpinnerAdapter)?.notifyDataSetChanged()
        }
    }
}