package com.itheima.ui.gridview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.itheima.smp.R;

import java.util.List;

/**
 * @Subject Recents长按后进入分屏模式,显示应用列表界面的GridView Adapter
 * @Author  zhangming
 * @Date    2019-06-12 15:36
 */
public class AppListsAdapter extends BaseAdapter {
    private Context mContext;
    private List<AppItemInfo> mAppList;
    public AppListsAdapter(Context context, List<AppItemInfo> appList){
        this.mContext = context;
        this.mAppList = appList;
    }

    @Override
    public int getCount() {
        return mAppList.size();
    }

    @Override
    public Object getItem(int position) {
        return mAppList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.applist_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.appImageView = (ImageView) convertView.findViewById(R.id.app_item_img);
            viewHolder.appTextView = (TextView) convertView.findViewById(R.id.app_item_text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(mAppList != null && mAppList.size() > 0){
            AppItemInfo appInfo = mAppList.get(position);
            viewHolder.appImageView.setImageDrawable(appInfo.appIcon);
            viewHolder.appTextView.setText(appInfo.appName);
        }

        return convertView;
    }

    class ViewHolder {
        ImageView appImageView;
        TextView appTextView;
    }

    static class AppItemInfo {
        Drawable appIcon;
        String appName;
        String appPackage;
        String appClassName;
    }
}
