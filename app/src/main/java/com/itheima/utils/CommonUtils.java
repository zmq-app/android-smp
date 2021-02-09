package com.itheima.utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Looper;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.itheima.di.modules.GlideApp;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;

public class CommonUtils {
    public interface INotUIProcessor {
        void process();
    }

    /** 通过Looper的方式判断是否在主线程中 **/
    public static void processNotUI(final INotUIProcessor processor){
        if(Looper.myLooper() == Looper.getMainLooper()){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    processor.process();
                }
            }).start();
        }else{
            processor.process();
        }
    }

    public static int[] getScreenWidthandHeight(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display ds = wm.getDefaultDisplay();

        int params[] = new int[2];
        params[0] = ds.getWidth();
        params[1] = ds.getHeight();
        //Log.i(CommonConstants.TAG,"screen width = "+params[0]+" screen height = "+params[1]);

        return params;
    }

    public static String getDeviceId(Context context){
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /** 通过Calendar日历类获取指定某一天的昨天和明天对应的日期信息 **/
    public static Date getPrevDay(int year,int month,int day){
        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month-1,day); //第二个参数月份下标索引值是从0开始
        calendar.add(Calendar.DATE,-1);

        return calendar.getTime();
    }

    public static Date getNextDay(int year,int month,int day){
        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month-1,day); //第二个参数月份下标索引值是从0开始
        calendar.add(Calendar.DATE,1);

        return calendar.getTime();
    }

    /** 功能拓展: 通过Calendar日历类获取指定某一天向前或向后推算dValue后对应的日期信息 **/
    public static Date getPrevDay(int year,int month,int day,int dValue){
        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month-1,day);
        calendar.add(Calendar.DATE,(-1)*dValue);

        return calendar.getTime();
    }

    public static Date getNextDay(int year,int month,int day,int dValue){
        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month-1,day);
        calendar.add(Calendar.DATE,dValue);

        return calendar.getTime();
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        //获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if(listAdapter == null) {
            return;
        }

        int listHeight = 0;
        for(int i=0;i<listAdapter.getCount();i++){
            View item = listAdapter.getView(i,null,listView);
            item.measure(0,0); //使用wrap_content方式测量
            listHeight += item.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = listHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    /**
     * @Subject 隐藏虚拟键盘
     * @param mActivity
     */
    public static void hiddenVirtualKeyBoard(Activity mActivity){
        View decorView = mActivity.getWindow().getDecorView();

        int flag = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        //判断当前版本在4.0以上并且存在虚拟按键，否则不做操作
        if (Build.VERSION.SDK_INT < 19 || !checkDeviceHasNavigationBar(mActivity)) {
            //一定要判断是否存在按键，否则在没有按键的手机调用会影响别的功能。如之前没有考虑到，导致图传全屏变成小屏显示。
            return;
        } else {
            // 获取属性
            decorView.setSystemUiVisibility(flag);
        }
    }

    /**
     * @Subject 判断是否存在虚拟按键
     * @return
     */
    private static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class<?> systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;
    }

    public static void alarmFunc(Context context, Intent intent, int type, long intervalTime) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT < 19) {
            if (type == AlarmManager.RTC_WAKEUP || type == AlarmManager.RTC) {
                /* System.currentTimeMillis()获取的是系统时间,是距离1970年1月1日开始的一个时间戳 */
                alarmManager.set(type, System.currentTimeMillis()+intervalTime, pendingIntent);
            } else if (type == AlarmManager.ELAPSED_REALTIME_WAKEUP || type == AlarmManager.ELAPSED_REALTIME) {
                /* SystemClock.elapsedRealtime()获取从设备boot后经历的时间值 */
                alarmManager.set(type, SystemClock.elapsedRealtime()+intervalTime, pendingIntent);
            }
        } else {
            if (type == AlarmManager.RTC_WAKEUP || type == AlarmManager.RTC) {
                alarmManager.setExact(type, System.currentTimeMillis()+intervalTime, pendingIntent);
            } else if (type == AlarmManager.ELAPSED_REALTIME_WAKEUP || type == AlarmManager.ELAPSED_REALTIME) {
                alarmManager.setExact(type, SystemClock.elapsedRealtime()+intervalTime, pendingIntent);
            }
        }
    }

    /**
     * @Subject GlideApp加载Image,可在Java环境下运行,项目若要在Kotlin环境中使用GlideApp,Gradle配置文件中需增加kapt插件
     * @param context
     * @param imageView
     * @param imageUrl
     */
    public static void GlideAppLoadImage(Context context, ImageView imageView, String imageUrl) {
        GlideApp.with(context).load(imageUrl).centerCrop().into(imageView);
    }

    /**
     * @Subject 使用pingyin4j函数库,将输入的中文字符串转化为拼音全称.后续可通过比较首字母排序和条目分类
     * @param inputStr
     * @return
     */
    public static String getPingYin(String inputStr) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);

        String output = "";
        char[] input = inputStr.trim().toCharArray();
        try {
            for (char curchar : input) {
                if (java.lang.Character.toString(curchar).matches("[\\u4E00-\\u9FA5]+")) {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(curchar, format);
                    output += temp[0];
                } else {
                    output += java.lang.Character.toString(curchar);
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }

        return output;
    }
}
