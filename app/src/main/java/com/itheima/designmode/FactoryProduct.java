package com.itheima.designmode;

/**
 * @Subject 工厂设计模式(BitmapFactory)
 * @Author  zhangming
 */
public class FactoryProduct {
    //抽象产品类
    static abstract class Product {
        public abstract void show();
    }

    //具体产品类A
    static class ProductA extends Product {
        @Override
        public void show() {
            System.out.println("ProductA......");
        }
    }

    //具体产品类B
    static class ProductB extends Product {
        @Override
        public void show() {
            System.out.println("ProductB......");
        }
    }

    //抽象工厂类
    static abstract class Factory {
        public abstract Product create();
    }

    //具体工厂类A
    static class FactoryA extends Factory {
        @Override
        public Product create() {
            return new ProductA();
        }
    }

    //具体工厂类B
    static class FactoryB extends Factory {
        @Override
        public Product create() {
            return new ProductB();
        }
    }

    /**
     * @Subject  测试入口main函数
     * @Description 直接run as main XXX,之后会在4:run控制台上输出对应的结果;最后点击关闭控制台即可退出
     * @Author  zhangming
     */
    public static void main(String args[]) {
        /* 构建工厂类A,返回实际的产品类A */
        Product productA = new FactoryA().create();
        productA.show();

        /* 构建工厂类B,返回实际的产品类B */
        Product productB = new FactoryB().create();
        productB.show();
    }
}
