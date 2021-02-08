package com.itheima.ui;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.itheima.smp.R;
import com.itheima.ui.view.MyCircleView;
import com.itheima.ui.view.MyFrameLayout;
import com.itheima.ui.view.MyLinearLayout;
import com.itheima.utils.CommonConstants;

/**
 * @Subject View的工作原理基本的测试案例
 * @Author zhangming
 * @Date 2018-09-22
 */
public class ViewOperatorActivity extends Activity {
    private MyCircleView circleView;
    private MyDownloadTask task;
    private ProgressBar myProgressBar;
    private static int nCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_operator);

        //获取根布局rootview实例,这里从布局查看实质是一个LinearLayout
        ViewGroup rootView = (ViewGroup) findViewById(android.R.id.content);
        View myView = rootView.getChildAt(0);
        if(myView instanceof LinearLayout){
            //根据rootview向下查找最后的view节点
            MyFrameLayout fl = (MyFrameLayout)((LinearLayout) myView).getChildAt(0);
            MyLinearLayout ll = (MyLinearLayout)fl.getChildAt(0);
            circleView = (MyCircleView)ll.getChildAt(0);
            myProgressBar = (ProgressBar) rootView.findViewById(R.id.my_progressBar);
        }else{
            circleView = null;
            Log.d(CommonConstants.TAG,"is not LinearLayout");
        }

        if(circleView != null){
            circleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(nCount % 2 == 0){
                        Log.i(CommonConstants.TAG,"Begin Excute...");
                        if(task == null){
                            //开始执行一个任务,每次均重新启动一个AsyncTask的实例对象
                            task = new MyDownloadTask();
                            task.execute();
                        }
                    }else{
                        Log.i(CommonConstants.TAG,"Cancel Excute...");
                        if(task != null){
                            //取消一个正在执行的任务,onCancelled方法将会被调用
                            task.cancel(true);
                            task = null;
                        }
                    }
                    nCount++;
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(CommonConstants.TAG,"TestActivity onDestroy...");
        if(myProgressBar != null){
            myProgressBar.setProgress(0);
        }
        if(task != null){
            task.cancel(true);
            task = null;
        }
        nCount = 0;
    }


    /**
     * 一个AsyncTask对象仅能被执行一次,即只能调用一次execute方法,否则会出现如下Java运行时异常
     * java.lang.IllegalStateException: Cannot execute task: the task has already been executed (a task can be executed only once)
     * 三个泛型参数的类型
     * Params:   开始异步任务执行时传入的参数类型
     * Progress: 异步任务执行过程中,返回下载进度值的类型
     * Result:   异步任务执行完成后,返回的结果类型
     * 如果AsyncTask确定不需要传递具体参数,那么这三个泛型参数可以用Void来代替
     */
    class MyDownloadTask extends AsyncTask<Void,Integer,Boolean> {
        // 方法1: onPreExecute
        // 作用: 执行线程任务前的操作
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        // 方法2: doInBackground
        // 作用: 接收输入参数,执行任务中的耗时操作,返回线程任务执行的结果
        // 此处通过计算从而模拟“加载进度”的情况
        @Override
        protected Boolean doInBackground(Void... params) {
            int nCount = 0;
            try{
                while (nCount < 100){
                    nCount++;
                    publishProgress(nCount);
                    Thread.sleep(50); //当执行cancel动作时,会产生线程中断异常java.lang.InterruptedException
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return true;
        }

        // 方法3: onProgressUpdate
        // 作用: 在主线程显示线程任务执行的进度
        @Override
        protected void onProgressUpdate(Integer... values) {
            myProgressBar.setProgress(values[0]);
        }

        // 方法4: onPostExecute
        // 作用: 接收线程任务执行结果,将执行结果显示到UI组件
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            Log.i(CommonConstants.TAG,"Loading is over!!!");
            myProgressBar.setProgress(0);
            task = null;
            nCount = 0;
        }

        // 方法5: onCancelled
        // 作用: 将异步任务设置为取消状态
        @Override
        protected void onCancelled() {
            myProgressBar.setProgress(0);
            nCount = 0;
        }
    }
}
