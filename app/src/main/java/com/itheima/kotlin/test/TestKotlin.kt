package com.itheima.kotlin.test

import android.app.Fragment
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import com.itheima.app.SmpApplication

class TestKotlin : Fragment(),View.OnClickListener {
    override fun onClick(v: View?) {
        when(v?.tag) {
            0 -> {
               var bean = Bean()
               println("Init Bean...")
               println(bean.str)
               println(bean.str)
            }
        }
    }

    private class NullClass (val params: String){
        private val nullParams by lazy {
            params
        }

        fun addFunc(): Int {
            val a = 3
            val b = 5
            return (a+b)
        }

        fun concat(): String? {
            val sb = StringBuilder()
            sb.append("welcome").append(" ").append(nullParams)
            return sb.toString()
        }
    }

    private class Bean {
        /* 仅在第一次使用时进行初始化变量str */
        val str by lazy {
            println("Init lazy")
            "Hello World"
        }
    }

    /* 主构造器 > init代码块 > 次构造器 */
    open class Parent(name: String) {
        constructor() : this("zhangming",18) {
            println("parent bean constructor1...")
        }
        constructor(name: String,age : Int): this(name) {
            println("parent bean constructor2,age = "+age)
        }
        init {
            println("parent bean name = "+name)
        }
    }

    class Person : Parent {
        /* 次构造函数(无参) */
        constructor() : super("hello"){ //调用父类的主构造函数
            println("person bean constructor1...")
        }

        constructor(name: String) : super(name, 19) { //调用父类的次构造函数
            println("person bean constructor2...")
        }

        /* init代码块 */
        init {
            println("person bean init...")
        }
    }

    private val handler : Handler = Handler(Looper.getMainLooper())

    /**
     * kotlin有关'?','!!'的区别
     * 在新建一个参数的类名后面加一个'?‘ 表示这个参数可以为空
     * 用到这个参数的时候后面加’?‘ 表示空参数就跳过并且程序继续执行
     * ’!!‘只用于用到这个参数的时候在后面加’!!‘ 表示空参数就抛出异常
     * 参考链接URL: [https://blog.csdn.net/wuditwj/article/details/84302715]
     */
    fun main(params: String) {
        var nullClass: NullClass?  //NullClass类实例可以为null
        if (params == null) {
            nullClass = null
        } else {
            nullClass = NullClass(params)
        }
        handler.postDelayed(object : Runnable {
            override fun run() {
                Toast.makeText(SmpApplication.getApp(),"main result1 = "+nullClass?.addFunc(),Toast.LENGTH_SHORT).show()
            }
        },0L)

        handler.postDelayed(object : Runnable {
            override fun run() {
                Toast.makeText(SmpApplication.getApp(),"main result2 = "+nullClass?.concat(),Toast.LENGTH_SHORT).show()
            }
        },2000L)

        var person1 = Person()
        var person2 = Person("hello world")
    }
}


