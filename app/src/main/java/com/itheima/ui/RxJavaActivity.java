package com.itheima.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.itheima.smp.R;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import rx.Completable;
import rx.CompletableSubscriber;
import rx.Observable;
import rx.Observer;
import rx.Single;
import rx.SingleSubscriber;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.AsyncSubject;
import rx.subjects.PublishSubject;

/**
 * @Subject RxJava语法学习
 * @URL     https://www.jianshu.com/p/abced47815d9
 * @Author  zhangming
 */
public class RxJavaActivity extends Activity {
    private Button rxJavaBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rxjava);
        RxViewClicks();
    }

    /**
     * @Subject1 RxBinding是是一组开源库,可将Android的中各类UI控件的动作事件转换为RxJava中的数据流
     * @Subject2 使用RxBinding,以RxJava观察者模式来处理UI事件,其中RxView是RxBinding中的一个组件
     */
    private void RxViewClicks() {
        rxJavaBtn = (Button) findViewById(R.id.rxjava_btn);
        RxView.clicks(rxJavaBtn).debounce(3000, TimeUnit.MILLISECONDS).subscribe(
            obj -> {
                Log.i("zhangming", "RxJavaActivity Clicks:点击了按钮,3秒内防抖");
                //func1();
                //func2();
                func3();
            }
        );
        //RxJava Button防抖机制
        //RxView.clicks(rxJavaBtn).debounce(3000, TimeUnit.MILLISECONDS).subscribe(new Consumer<Object>() {
        //    @Override
        //    public void accept(Object o) {
        //        Log.i("zhangming", "RxJavaActivity Clicks:点击了按钮,3秒内防抖");
        //    }
        //});
    }

    /**
     * @Subject1 RxJava2  Single,Completable,Maybe简化版的Observable
     * @Subject2 RxJava2  Observerable,Flowable都是用来发送数据流
     * @URL      https://segmentfault.com/a/1190000016993569
     */
    private void func1() {
        /* 只能发射一条单一的数据或者一条异常通知,不能发射完成通知 */
        Single.create(new Single.OnSubscribe<Integer>() {
            @Override
            public void call(SingleSubscriber<? super Integer> emitter) {
                emitter.onSuccess(1001); //发射成功数据1001
            }
        }).subscribe(new SingleSubscriber<Integer>() {
            @Override
            public void onSuccess(Integer value) {
                Log.i("zhangming","func1 value = "+value); //接收成功数据
            }

            @Override
            public void onError(Throwable error) {

            }
        });

        /* 只能发射一条完成通知或者一条异常通知,不能发射数据 */
        Completable.create(new Completable.OnSubscribe() {
            @Override
            public void call(CompletableSubscriber emitter) {
                emitter.onCompleted();
            }
        }).subscribe(new CompletableSubscriber() {
            @Override
            public void onCompleted() {
                Log.i("zhangming","func1 onCompleted...");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onSubscribe(Subscription d) {

            }
        });
    }

    /**
     * @Subject just是一种特殊的create,它不能指定Schedulers,只能在当前线程中运行.而create可以指定Schedulers实现异步处理.
     * just不管是否被subscribe订阅均会被调用,而create如果不被订阅是不会被调用的,所以通常可以用just传递简单参数,而用create处理复杂异步逻辑
     */
    private void func2() {
        Observable.just("hello world").observeOn(Schedulers.newThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.i("zhangming","func2 just sValue = "+s);
            }
        });

        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> emitter) {
                /* Observable被观察者使用发送数据流 */
                emitter.onNext("Welcome to Beijing!!!"); //发送数据
                emitter.onCompleted();  //发送完成通知
            }
        }).subscribeOn(Schedulers.io())
          .observeOn(Schedulers.io())
          .subscribe(new Observer<String>() {
              @Override
              public void onCompleted() {
                  Log.i("zhangming","func2 create onCompleted,threadName = "+Thread.currentThread().getName()); //IO threadName = RxIoScheduler-XX
              }

              @Override
              public void onError(Throwable e) {

              }

              @Override
              public void onNext(String s) {
                  Log.i("zhangming","func2 create onCompleted,sValue = "+s+" threadName = "+Thread.currentThread().getName()); //IO threadName = RxIoScheduler-XX
              }
          });

        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> emitter) {
                emitter.onNext(123);
            }
        }).flatMap(new Func1<Integer, Observable<String>>() { //类型转换
            @Override
            public Observable<String> call(Integer integer) {
                return Observable.just("hello Beijing");
            }
        }).filter(new Func1<String, Boolean>() {
            @Override
            public Boolean call(String s) {
                /* 返回true,则放行调用onNext方法;返回false,则过滤掉,后续的onNext方法不会被执行 */
                return s.contains("hello");
            }
        }).observeOn(AndroidSchedulers.mainThread())
          .subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {
                Log.i("zhangming","func2 create onNext,sValue = "+s+" threadName = "+Thread.currentThread().getName());
            }
        });
    }

    /**
     * @Subject PublishSubject,AsyncSubject
     * @URL     https://www.jianshu.com/p/1257c8ba7c0c
     */
    private void func3() {
        /* PublishSubject extends Subject,既是Observable也是Observer,自身即可以作为订阅的被观察者监控事件,也可以作为观察者发送数据和通知 */
        PublishSubject<String> publishSubject = PublishSubject.create();

        //subject作为观察者发送数据和完成通知(配置延迟600ms,从哪里订阅,就从那里开始取出数据进行发送)
        publishSubject.onNext("hello111");
        publishSubject.onNext("hello222");
        publishSubject.throttleWithTimeout(600, TimeUnit.MILLISECONDS).subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {
                Log.i("zhangming","func3 PublishSubject onCompleted...");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {
                /* 打印hello333,onCompleted,而hello111,hello222数据,由于在订阅之前发送的,所以接收不到 */
                Log.i("zhangming","func3 PublishSubject onNext sValue = "+s);
            }
        });
        publishSubject.onNext("hello333");
        publishSubject.onCompleted();


        /* AsyncSubject无论订阅的时候是否Completed,均可以收到最后一个值的回调 */
        AsyncSubject<Integer> asyncSubject = AsyncSubject.create();
        asyncSubject.throttleWithTimeout(600, TimeUnit.MILLISECONDS).subscribe(new Observer<Integer>() {
            @Override
            public void onCompleted() {
                Log.i("zhangming","func3 AsyncSubject onCompleted...");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Integer value) {
                /* 打印最后一条数据789和完成的通知onCompleted */
                Log.i("zhangming","func3 AsyncSubject onNext sValue = "+value);
            }
        });
        asyncSubject.onNext(123);
        asyncSubject.onNext(456);
        asyncSubject.onNext(789);
        asyncSubject.onCompleted();
    }
}
