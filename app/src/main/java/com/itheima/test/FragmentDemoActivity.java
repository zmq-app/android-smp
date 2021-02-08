package com.itheima.test;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.itheima.smp.R;

/**
 * Created by zhangming on 2018/10/14.
 */
public class FragmentDemoActivity extends Activity{
    public static int curImageId[] = {R.mipmap.ic_launcher,R.drawable.monitor_launcher};
    public static int nCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_demo);

        final FragmentOne ft1 = new FragmentOne();
        final FragmentTwo ft2 = new FragmentTwo();

        //TODO 布局是使用FramgLayout这种ViewGroup
        //TODO 开启一个事务进行Fragment的动态替换FramgLayout
        FragmentManager fm = getFragmentManager(); //android.app.FragmentManager
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.leftView,ft1,"FragmentOne");
        transaction.replace(R.id.rightView,ft2,"FragmentTwo");
        //TODO 如果你调用了transaction.addToBackStack(null)方法,那么当你按下back键的时候不是直接结束当前activity,而是先从fragment栈里尝试pop出来栈顶的fragment
        //transaction.addToBackStack(null);
        transaction.commit();

        ft1.setOnDataTransmissionListener(new FragmentOne.OnDataTransmissionListener() {
            @Override
            public void dataTransmission(String data) {
                ft2.setData(data);
            }
        });
    }
}
