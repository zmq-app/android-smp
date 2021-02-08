package com.itheima.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.itheima.smp.R;
import com.itheima.fragment.FriendFragment;
import com.itheima.fragment.MessageFragment;
import com.itheima.fragment.PersonalFragment;
import com.itheima.utils.CommonConstants;
import com.itheima.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangming on 2018/8/4.
 * 聊天软件主页[fragment+viewpager]
 */
public class HomeActivity extends FragmentActivity implements View.OnClickListener{
    private MessageFragment  mFragment;
    private FriendFragment   fFragment;
    private PersonalFragment pFragment;

    private SlideViewPager  mViewPager;
    private FragmentManager fm;
    private List<Fragment>  fragmentList;

    private Button tab_message,tab_contact,tab_personal;
    private LinearLayout ll_top_bar;

    private PopupWindow popupWindow;
    private TextView addSignTextView;

    private static final int MESSAGE_PAGE  = 0;
    private static final int FRIEND_PAGE   = 1;
    private static final int PERSONAL_PAGE = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
    }

    private void initView() {
        mViewPager = (SlideViewPager) findViewById(R.id.home_viewpager);
        mViewPager.setSlide(false);  //设置是否可以进行滑动
        mViewPager.setOffscreenPageLimit(1);  //设置viewpager缓存页数[最小值=1]

        //Fragment初始化,并追加到集合列表fragmentList中管理
        //其中MessageFragment实例内部包含两个子fragment[ChatMsgFragment or RequestMsgFragment]
        fm = getSupportFragmentManager();
        fragmentList = new ArrayList<>();
        mFragment = new MessageFragment();
        fFragment = new FriendFragment();
        pFragment = new PersonalFragment();
        fragmentList.add(mFragment);
        fragmentList.add(fFragment);
        fragmentList.add(pFragment);

        //设置viewpager adapter,并设置监听函数
        MainFragAdapter mPageAdapter = new MainFragAdapter(fm,fragmentList);
        mViewPager.setAdapter(mPageAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case MESSAGE_PAGE:
                        ll_top_bar.setVisibility(View.VISIBLE);
                        break;
                    case FRIEND_PAGE:
                        ll_top_bar.setVisibility(View.GONE);
                        break;
                    case PERSONAL_PAGE:
                        ll_top_bar.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //初始化顶部的Top Bar
        ll_top_bar = (LinearLayout) findViewById(R.id.ll_top_bar);
        ll_top_bar.setVisibility(View.VISIBLE);

        //初始化底部Button Tab,并设置点击事件
        tab_message = (Button) findViewById(R.id.tab_message);
        tab_contact = (Button) findViewById(R.id.tab_contact);
        tab_personal = (Button) findViewById(R.id.tab_personal);
        tab_message.setOnClickListener(this);
        tab_contact.setOnClickListener(this);
        tab_personal.setOnClickListener(this);

        //使用PopWindow+ProgrssBar模拟弹出式旋转进度对话框
        View view = LayoutInflater.from(this).inflate(R.layout.popup_dialog,null);
        popupWindow = new PopupWindow();
        popupWindow.setContentView(view);
        popupWindow.setFocusable(true);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Home Activity").setMessage("Base Page Content");
        builder.setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                popupWindow.showAtLocation(getWindow().getDecorView(),Gravity.CENTER,0,0);
            }
        });
        builder.create().show();

        //创建并添加子Window,向其中添加view
        addSignTextView = (TextView) findViewById(R.id.tv_add_sign);
        addSignTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
                    //版本大于6.0则需要判断是否获取了overlays权限
                    //从Android6.0开始,应用要想弹出一个悬浮在任意App上的Window的话需要用户手动为这个应用设置overlays权限
                    //这个权限就连运行时权限也无法拿到,必须要用户前往手机的权限界面为应用设置该权限
                    if (!Settings.canDrawOverlays(HomeActivity.this)) {
                        startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:"+getPackageName())),1);
                    }
                }

                int[] screenPixels = CommonUtils.getScreenWidthandHeight(HomeActivity.this);
                final TextView mView = new TextView(HomeActivity.this);
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
                mView.setLayoutParams(lp);
                mView.setText("Hello World!!!");
                mView.setBackgroundColor(Color.CYAN);
                mView.setGravity(Gravity.CENTER);
                //TODO 此处需要指定测量模式来计算TextView控件的宽高,否则下面调用getMeasuredWidth,getMeasuredHeight均为0
                //TODO 若此处指定的测量模式为AT_MOST,那么无论该TextView的LayoutParam如何,生成的测量规则均为AT_MOST,并将此测量模式作为实参传递给回调函数onMeasure,且大小不会超过父容器指定的大小500
                //TODO 若指定AT_MOST测量模式,则实际的getMeasureWidth(),getMeasureHeight()为包裹字加上padding距周边围成矩形的宽高
                mView.measure(View.MeasureSpec.makeMeasureSpec(500, View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(500, View.MeasureSpec.EXACTLY));
                mView.setWidth(500);
                mView.setHeight(500); //getWidth(),getHeight();

                WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT,
                    0,0, PixelFormat.TRANSPARENT);
                params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
                params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
                //TODO 此处需要设置其LayoutParams的gravity属性,如果不设置,那么下面设置的x和y值将不会起作用
                params.gravity = Gravity.LEFT | Gravity.TOP;
                params.x = (screenPixels[0] - mView.getMeasuredWidth())/2;
                params.y = (screenPixels[1] - mView.getMeasuredHeight())/2;
                Log.i(CommonConstants.TAG,"screenPixels[0] = "+screenPixels[0]+" mWidth = "+mView.getMeasuredWidth()+" screenPixels[1] = "+screenPixels[1]+" mHeight = "+mView.getMeasuredHeight());
                getWindowManager().addView(mView,params);

                Log.i(CommonConstants.TAG,"getMeasuredWidth = "+mView.getMeasuredWidth()+" getMeasuredHeight = "+mView.getMeasuredHeight());
                mView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        Log.i(CommonConstants.TAG,"getWidth = "+mView.getWidth()+" getHeight = "+mView.getHeight());
                    }
                });
            }
        });
    }

    static class MainFragAdapter extends FragmentStatePagerAdapter {
        private List<Fragment> fragmentList;

        public MainFragAdapter(FragmentManager fm , List<Fragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab_message:
                mViewPager.setCurrentItem(MESSAGE_PAGE,false);
                break;
            case R.id.tab_contact:
                mViewPager.setCurrentItem(FRIEND_PAGE,false);
                break;
            case R.id.tab_personal:
                mViewPager.setCurrentItem(PERSONAL_PAGE,false);
                break;
        }
    }
}
