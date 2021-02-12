package com.itheima.test;

import java.util.HashMap;

public class ThreadDemo {
    private static int nCount = 0;
    private static int maxCount = 10;
    private static HashMap<Integer,Integer> mapCounts = new HashMap();

    public class MyThread extends Thread {
        @Override
        public void run() {
            synchronized (ThreadDemo.class) {
                final int key = nCount % maxCount;
                int value;
                if (mapCounts.get(key) == null) {
                    mapCounts.put(key,nCount);
                    value = nCount;
                } else {
                    value = mapCounts.get(key);
                }
                nCount++;
                System.out.println("ThreadDemo threadName = "+getName()+" value = "+value);
            }
        }
    }

    public static void main(String[] args) {
        ThreadDemo thd = new ThreadDemo();
        for (int i = 0; i < 100; i++) {
            MyThread t = thd.new MyThread();
            t.start();
        }
    }
}
