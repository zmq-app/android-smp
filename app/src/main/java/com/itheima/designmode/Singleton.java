package com.itheima.designmode;

public class Singleton {
    /**
     * @Subject 懒汉式单例模式
     * @Author  zhangming
     */
    static class LasySingleton {
        /* volatile变量时会立刻从主内存中取最新的值,子线程给volatile变量赋值后及时更新至主内存 */
        private static volatile LasySingleton mInstance = null;

        /* 构造器私有 */
        private LasySingleton() {}

        /* 双重null判断,解决多线程中可能出现两个实例的问题 */
        public static LasySingleton getInstance() {
            if (mInstance == null) {
                synchronized (LasySingleton.class) {
                    if (mInstance == null) {
                        mInstance = new LasySingleton();
                    }
                }
            }
            return mInstance;
        }

        public void printClassName() {
            System.out.println("One ClassName = "+LasySingleton.class.getSimpleName());
        }
    }

    /**
     * @Subject 饿汉式单例模式
     * @Author  zhangming
     */
    static class HungrySingleton {
        /* 饿汉式单实例在类加载时就已经完成了初始化,所以类加载较慢,但获取对象的速度快 */
        private static HungrySingleton mInstance = new HungrySingleton();

        /* 构造器私有 */
        private HungrySingleton() {}

        public static HungrySingleton getInstance() {
            return mInstance;
        }

        public void printClassName() {
            System.out.println("Two ClassName = "+LasySingleton.class.getSimpleName());
        }
    }

    /**
     * @Subject  测试入口main函数
     * @Description 直接run as main XXX,之后会在4:run控制台上输出对应的结果;最后点击关闭控制台即可退出
     * @Author  zhangming
     */
    public static void main(String args[]) {
        LasySingleton.getInstance().printClassName();
        HungrySingleton.getInstance().printClassName();
    }
}
