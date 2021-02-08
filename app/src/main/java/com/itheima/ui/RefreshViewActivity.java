package com.itheima.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.itheima.smp.R;
import com.itheima.ui.adapter.MyRefreshListAdapter;
import com.itheima.ui.view.RefreshViewGroup;
import com.itheima.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Subject 有关下拉刷新,上拉加载的自定义控件测试Activity
 * @Author  zhangming
 * @Date    2018-09-27 15:28
 */
public class RefreshViewActivity extends Activity {
    private RefreshViewGroup rvgroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.refresh_viewgroup);
        initView();
    }

    private void initView(){
        rvgroup = (RefreshViewGroup) findViewById(R.id.refresh_viewgroup);

        List<DataModel> mList = new ArrayList<>();
        for(int i=0;i<10;i++){
            DataModel model = new DataModel(R.mipmap.ic_launcher,String.valueOf(i+1),false);
            mList.add(model);
        }

        MyRefreshListAdapter mAdapter = new MyRefreshListAdapter(this,mList);
        rvgroup.setListViewAdapter(mAdapter);

        CommonUtils.hiddenVirtualKeyBoard(this);
    }

    public void onBack(View view){
        /**
        Intent intent = new Intent(this,HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        */
        finish();
    }

    public static class DataModel{
        public int image_local_id;
        public String  content;
        public boolean isChecked;

        public DataModel(int image_local_id, String content, boolean isChecked) {
            this.image_local_id = image_local_id;
            this.content = content;
            this.isChecked = isChecked;
        }
    }

}
