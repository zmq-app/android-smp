package com.itheima.utils;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * @Subject RxBus单例模式
 */
public class RxBus {
    private static volatile RxBus instance;

    private final Subject<Object,Object> bus;

    private RxBus() {
        bus = new SerializedSubject<>(PublishSubject.create());
    }

    public static RxBus getInstance() {
        if (instance == null){
            synchronized (RxBus.class) {
                if (instance == null) {
                    instance = new RxBus();
                }
            }
        }
        return instance;
    }

    public void postEvent(Object event) {
        bus.onNext(event);
    }

    public <T>Observable<T> toObservable(Class<T> eventype) {
        return bus.ofType(eventype);
    }
}
