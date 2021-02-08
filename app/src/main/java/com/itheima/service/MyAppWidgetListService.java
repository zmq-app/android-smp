package com.itheima.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.itheima.receiver.MyAppWidgetReceiver;

import java.util.ArrayList;
import java.util.List;

/**
 * @Subject AppWidget ListView控件需要使用的Service
 * @Author  zhangming
 * @Date    2018-11-04 16:25
 */
public class MyAppWidgetListService extends RemoteViewsService {
    public static final String INITENT_DATA_KEY = "extra_data";

    public MyAppWidgetListService() {
        super();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    private class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
        private Context mContext;
        private List<String> mList = new ArrayList<>();

        public ListRemoteViewsFactory(Context context, Intent intent) {
            mContext = context;
        }

        @Override
        public void onCreate() {
            mList.add("111");
            mList.add("222");
            mList.add("333");
            mList.add("444");
            mList.add("555");
            mList.add("666");
        }

        @Override
        public void onDataSetChanged() {

        }

        @Override
        public void onDestroy() {
            mList.clear();
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews views = new RemoteViews(mContext.getPackageName(), android.R.layout.simple_list_item_1);
            views.setTextViewText(android.R.id.text1, "item:" + mList.get(position));

            Bundle extras = new Bundle();
            extras.putInt(INITENT_DATA_KEY, position);
            Intent changeIntent = new Intent();
            changeIntent.setAction(MyAppWidgetReceiver.CHANGE_IMAGE_ACTION);
            changeIntent.putExtras(extras);

            /**
             * android.R.layout.simple_list_item_1 --- id --- text1
             * listview的item click：将changeIntent发送
             * changeIntent: 它默认的就有action是provider中使用setPendingIntentTemplate设置的action
             */
            views.setOnClickFillInIntent(android.R.id.text1, changeIntent);
            return views;
        }

        /*
         * 在更新界面的时候如果耗时就会显示正在加载... 的默认字样,但是你可以更改这个界面
         * 如果返回null显示默认界面
         * 否则加载自定义的,返回RemoteViews
         */
        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}
