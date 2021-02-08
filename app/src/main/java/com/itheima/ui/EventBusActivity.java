package com.itheima.ui;

import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.itheima.smp.R;
import com.itheima.fragment.LeftMenuFragment;
import com.itheima.fragment.RightMenuFragment;
import com.itheima.kotlin.test.TestKotlin;
import com.itheima.localbroadcast.LocBroadcast;
import com.itheima.localbroadcast.LocBroadcastReceiver;
import com.itheima.model.ConfBaseInfo;
import com.itheima.model.Event;
import com.itheima.model.MessageEvent;
import com.itheima.utils.CommonConstants;
import com.itheima.utils.RxBus;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * @Subject EventBus,RxBus,SlideMenu,ButterKnife,LocBroadcast开源库的使用,属性动画(ObjectAnimator)
 * @Author  zhangming
 * @Date    2020-08-21  20:34
 */
public class EventBusActivity extends AppCompatActivity {
    private SlidingMenu slidingMenu;
    private LeftMenuFragment leftMenu;
    private RightMenuFragment rightMenu;
    private long mLastClickTime;

    @BindView(R.id.left_slide)
    ImageView leftSlideImg;  //属性变量不能使用private,static修饰
    @BindView(R.id.right_slide)
    ImageView rightSlideImg;
    @BindView(R.id.hello_text)
    TextView mTextView;
    @BindView(R.id.kotlin_test)
    TextView mKotlinView;

    private Gson gson = new Gson();
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 100) {
                /* 将json字符串反序列化成ConfBaseInfo对象实例 */
                ConfBaseInfo cInfo = gson.fromJson((String)(msg.obj), ConfBaseInfo.class);
                Log.i("zhangming","EventBusActivity subject = "+cInfo.getSubject()+",pwd = "+cInfo.getGuestPwd());
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventbus);
        /* 侧滑菜单SlideMenu初始化 */
        initSlideMenu();
        /* 用于绑定Activity DecorView */
        ButterKnife.bind(this);
        /* RxJava观察者模式,事件event接收 */
        RxBus.getInstance().toObservable(Event.class).subscribe(new Action1<Event>() {
            @Override
            public void call(Event event) {
                if (event != null && "closeMenu".equals(event.getName())) {
                    slidingMenu.showContent();
                }
            }
        });
        LocBroadcast.getInstance().registerBroadcast(receiver,actions);
    }

    private void initSlideMenu() {
        slidingMenu = new SlidingMenu(this);
        slidingMenu.setMode(SlidingMenu.LEFT_RIGHT);
        // 设置触摸屏幕的模式
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        // 设置渐入渐出效果的值
        slidingMenu.setFadeDegree(0.35f);
        slidingMenu.setFadeEnabled(true);
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);

        slidingMenu.setMenu(R.layout.left_menu);
        leftMenu = new LeftMenuFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.left_menu, leftMenu).commit();
        slidingMenu.setSecondaryMenu(R.layout.right_menu);
        rightMenu = new RightMenuFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.right_menu, rightMenu).commit();
    }

    private int[] getImgSize(ImageView imageView) {
        String resEntryName = getResources().getResourceEntryName(imageView.getId());
        Rect rect = imageView.getDrawable().getBounds();
        final int width = rect.right - rect.left;
        final int height = rect.bottom - rect.top;
        final int iWidth = imageView.getDrawable().getIntrinsicWidth();
        final int iHeight = imageView.getDrawable().getIntrinsicHeight();
        Log.i(CommonConstants.TAG,"getImgSize resEntryName = "+resEntryName+
            " width = "+width+" height = "+height+" iWidth = "+iWidth+" iHeight = "+iHeight);

        //ImageView android:background属性需要设置
        //BitmapDrawable bd = (BitmapDrawable) imageView.getBackground();
        //if (bd != null) {
        //    Bitmap bitmap = bd.getBitmap();
        //    final int bWidth = bitmap.getWidth();
        //    final int bHeight = bitmap.getHeight();
        //}

        return new int[] {width,height};
    }

    /* 子线程发送消息message,发送本地广播LocBroadcast */
    private void dispatchMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ConfBaseInfo info = getConfBaseInfo();
                Message message = mHandler.obtainMessage();
                message.what = 100;
                /* 将一个ConfBaseInfo实例对象info序列化为json字符串传输 */
                message.obj = gson.toJson(info);
                mHandler.sendMessage(message);
                /* 发送本地广播,传递ConfBaseInfo对象实例,本质是执行Runnable线程的run方法 */
                LocBroadcast.getInstance().sendBroadcast("com.android.confbaseinfo",info);
            }
        }).start();
    }

    private ConfBaseInfo getConfBaseInfo() {
        ConfBaseInfo confDetailInfo = new ConfBaseInfo();
        //入会密码
        confDetailInfo.setGuestPwd("Hello Guest");
        //主席密码
        confDetailInfo.setChairmanPwd("12345678");
        //会议主题
        confDetailInfo.setSubject("Subject Detail info");
        //开始时间
        confDetailInfo.setStartTime("2020-08-27 14:29");
        //结束时间
        confDetailInfo.setEndTime("2020-08-27 15:29");
        //会议id
        confDetailInfo.setConfID("123");
        return confDetailInfo;
    }

    @OnClick({R.id.left_slide, R.id.right_slide,R.id.hello_text,R.id.kotlin_test})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_slide:
                slidingMenu.showMenu();
                getImgSize(leftSlideImg);  //src_pic 63*35 => 1080*2160 dpi=440 => 58,32 => 440dpi向上查找480dpi的资源,显示的图片会缩小
                dispatchMessage();
                break;
            case R.id.right_slide:
                slidingMenu.showSecondaryMenu();
                getImgSize(rightSlideImg); //src_pic 72*72 => 1080*2160 dpi=440 => 66,66 => 440dpi向上查找480dpi的资源,显示的图片会缩小
                dispatchMessage();
                break;
            case R.id.hello_text:
                ViewWrapper viewWrapper = new ViewWrapper(mTextView);
                ObjectAnimator.ofInt(viewWrapper,"width",800).setDuration(3000).start();
                ObjectAnimator.ofInt(viewWrapper,"height",800).setDuration(3000).start();
                break;
            case R.id.kotlin_test:
                //TODO add zmq for kotlin learning test
                TestKotlin kotlin = new TestKotlin();
                kotlin.main("Beijing");
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (slidingMenu.isMenuShowing() || slidingMenu.isSecondaryMenuShowing()) {
            slidingMenu.showContent();
            EventBus.getDefault().post(new MessageEvent("close SlideMenu..."));
        } else {
            /** 主界面返回键双击退出示例 **/
            if (System.currentTimeMillis() - mLastClickTime <= 2000L) {
                super.onBackPressed();
            } else {
                mLastClickTime = System.currentTimeMillis();
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            LocBroadcast.getInstance().unRegisterBroadcast(receiver,actions);
        }
    }

    /**
     * @Subject 属性动画要求作用的对象提供该属性的set/get方法,使用包装类ViewWrapper提供
     * @Author  zhangming
     * @Date    2020-10-05 19:42
     */
    private static class ViewWrapper {
        private View mTarget;
        public ViewWrapper(View target) {
            this.mTarget = target;
        }

        public int getWidth() {
            return mTarget.getLayoutParams().width;
        }
        public void setWidth(int width) {
            mTarget.getLayoutParams().width = width;
            mTarget.requestLayout();
        }

        public int getHeight() {
            return mTarget.getLayoutParams().height;
        }
        public void setHeight(int height) {
            mTarget.getLayoutParams().height = height;
            mTarget.requestLayout();
        }
    }

    private String[] actions = new String[]{"com.android.confbaseinfo"};
    private LocBroadcastReceiver receiver = new LocBroadcastReceiver() {
        @Override
        public void onReceive(String broadcastName, Object obj) {
            if ("com.android.confbaseinfo".equals(broadcastName)) {
                if (obj instanceof ConfBaseInfo) {
                    ConfBaseInfo cInfo = (ConfBaseInfo) (obj);
                    Log.i(CommonConstants.TAG," chairmanPwd = "+cInfo.getChairmanPwd()+" guestPwd = "+cInfo.getGuestPwd());
                }
            }
        }
    };
}