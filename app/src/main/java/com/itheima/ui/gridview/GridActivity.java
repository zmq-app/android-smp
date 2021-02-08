package com.itheima.ui.gridview;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.itheima.smp.R;

import java.util.ArrayList;
import java.util.List;

public class GridActivity extends Activity {
    private ArrayList<AppListsAdapter.AppItemInfo> mAppLists = new ArrayList<>();
    private AppListsAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_app_lists);
        initView();
        initData();
    }

    private void initView() {
        GridView mAllAppGridView = (GridView) findViewById(R.id.all_app_list_gridview);
        mAdapter = new AppListsAdapter(this, mAppLists);
        mAllAppGridView.setAdapter(mAdapter);
        mAllAppGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                AppListsAdapter.AppItemInfo itemInfo = mAppLists.get(position);
                if(itemInfo != null){
                    Intent it = new Intent();
                    it.setComponent(new ComponentName(itemInfo.appPackage,itemInfo.appClassName));
                    it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(it);
                }
            }
        });
    }

    private void initData() {
        PackageManager pm = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfos) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            if(activityInfo != null){
                AppListsAdapter.AppItemInfo appInfo = new AppListsAdapter.AppItemInfo();
                appInfo.appIcon = activityInfo.loadIcon(pm);
                appInfo.appName = activityInfo.loadLabel(pm).toString();
                appInfo.appPackage = activityInfo.packageName;
                appInfo.appClassName = activityInfo.name;
                mAppLists.add(appInfo);
            }
        }
        mAdapter.notifyDataSetChanged();
    }
}
