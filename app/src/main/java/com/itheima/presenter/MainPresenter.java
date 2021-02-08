package com.itheima.presenter;

import com.itheima.model.Item;
import com.itheima.model.Result;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainPresenter implements MainContract.Presenter {
    private MainContract.View view;
    private ApiService apiService;

    /* MainModule类的provideMainView方法提供MainContract.View实例 */
    /* NetModule类的provideApiService方法提供ApiService实例 */
    @Inject
    public MainPresenter(MainContract.View view,ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }


    @Override
    public void getListByPage(int page, int model, String pageId, String deviceId, String createTime) {
        final long curTimes = System.currentTimeMillis()/1000;
        apiService.getList("api", "getList", page, model, pageId, createTime, "android", "1.3.0", curTimes, deviceId, 1)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Subscriber<Result.Data<List<Item>>>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                    view.showOnFailure();
                }

                @Override
                public void onNext(Result.Data<List<Item>> listData) {
                    int size = listData.getDatas().size();
                    if (size > 0) {
                        view.updateListUI(listData.getDatas());
                    } else {
                        view.showNoMore();
                    }
                }
            });
    }
}
