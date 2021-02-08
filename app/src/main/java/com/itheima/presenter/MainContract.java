package com.itheima.presenter;

import com.itheima.model.Item;

import java.util.List;

public interface MainContract {
    interface Presenter {
        void getListByPage(int page, int model, String pageId,String deviceId,String createTime);
    }

    interface View {
        void updateListUI(List<Item> itemList);
        void showNoMore();
        void showOnFailure();
    }
}
