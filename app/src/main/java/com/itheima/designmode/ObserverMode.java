package com.itheima.designmode;

import java.util.ArrayList;
import java.util.List;

/**
 * @Subject 观察者模式(Adapter notifyDataSetChanged)
 * @Author  zhangming
 */
public class ObserverMode {
    /* 抽象观察者类Observer */
    public interface Observer {
        void update(String message);
    }

    public class Boy implements Observer {
        private String name;  //名字
        public Boy(String name) {
            this.name = name;
        }
        @Override
        public void update(String message) {
            System.out.println(name + " receive message = " + message);
        }
    }

    public class Girl implements Observer {
        private String name;  //名字
        public Girl(String name) {
            this.name = name;
        }
        @Override
        public void update(String message) {
            System.out.println(name + " receive message = " + message);
        }
    }

    /* 抽象被观察者,定义添加,删除,通知等方法 */
    public interface Observable {
        void add(Observer observer);    //添加观察者
        void remove(Observer observer); //删除观察者
        void notify(String message);    //通知观察者
    }

    public class Postman implements Observable {
        private List<Observer> personList = new ArrayList<Observer>();

        @Override
        public void add(Observer observer) {
            personList.add(observer);
        }

        @Override
        public void remove(Observer observer) {
            personList.remove(observer);
        }

        @Override
        public void notify(String message) {
            for (Observer observer : personList) {
                observer.update(message);
            }
        }
    }

    public static void main(String[] args) {
        ObserverMode observerMode = new ObserverMode();
        Observer boy1 = observerMode.new Boy("boy1");
        Observer boy2 = observerMode.new Boy("boy2");
        Observer girl1 = observerMode.new Girl("girl1");

        Postman postman = observerMode.new Postman();
        postman.add(boy1);
        postman.add(boy2);
        postman.add(girl1);

        postman.notify("postman hello world...");
    }
}
