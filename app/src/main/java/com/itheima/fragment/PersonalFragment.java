package com.itheima.fragment;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.itheima.smp.R;
import com.itheima.ui.AudioActivity;
import com.itheima.ui.CalendarActivity;
import com.itheima.ui.QRActivity;
import com.itheima.ui.ViewOperatorActivity;
import com.itheima.utils.CommonConstants;

/**
 * Created by zhangming on 2018/9/1.
 */
public class PersonalFragment extends Fragment {
    private View mFragmentView;
    private GridView mGridView;
    private Toolbar audioToolBar; //android.support.v7.widget.Toolbar

    //增加手机前端sensor光线感应
    private SensorManager sm;
    private Sensor sensor;
    private SensorEventListener myListener;

    private boolean isViewCreated; //Fragment的View加载完毕的标记
    private boolean isUIVisible;   //Fragment对用户可见的标记

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARouter.getInstance().inject(this);
        Log.i("zhangming","PersonalFragment onCreate,ARouter inject...");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(mFragmentView == null){
            mFragmentView = inflater.inflate(R.layout.fragment_personal,container,false);
        }
        return mFragmentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewCreated = true;
        lasyLoad();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //TODO isVisibleToUser这个boolean值表示:该Fragment的UI用户是否可见
        if(isVisibleToUser){
            isUIVisible = true;
            lasyLoad();
        }else {
            isUIVisible = false;
        }
    }

    @Override
    public void onDestroyView() {
        if(sm != null){
            sm.unregisterListener(myListener);
            sm = null;
        }
        isViewCreated = false;
        isUIVisible = false;
        super.onDestroyView();
    }

    private void lasyLoad() {
        if (isViewCreated && isUIVisible) {
            Log.i(CommonConstants.TAG,"lasyLoad PersonalFragment...");
            audioToolBar = (Toolbar) mFragmentView.findViewById(R.id.toolbar);
            mGridView = (GridView)mFragmentView.findViewById(R.id.grid);
            mGridView.setAdapter(new FlvGridAdapter(getActivity()));

            initToolBar();
            initSensor();

            sm.registerListener(myListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);

            isViewCreated = false;
            isUIVisible = false;
        }
    }

    public void initToolBar() {
        audioToolBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setClass(getContext(),AudioActivity.class);
                startActivity(i);
            }
        });
    }

    public void initSensor(){
        sm = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensor = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        myListener = new SensorEventListener(){
            @Override
            public void onSensorChanged(SensorEvent event) {
                Log.i("zhangming","distance: "+event.values[0]);
                final Window window = getActivity().getWindow();
                WindowManager.LayoutParams lAttrs = getActivity().getWindow().getAttributes();
                View view = ((ViewGroup) window.getDecorView().findViewById(android.R.id.content)).getChildAt(0);

                //if (event.timestamp == 0) return; //just ignoring for nexus 1
                boolean sLastProximitySensorValueNearby = isProximitySensorNearby(event);
                if (sLastProximitySensorValueNearby) {
                    Log.i("zhangming","view is invisible");
                    lAttrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    view.setVisibility(View.INVISIBLE);
                } else  {
                    Log.i("zhangming","view is visible");
                    lAttrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    view.setVisibility(View.VISIBLE);
                }
                window.setAttributes(lAttrs);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }

    public boolean isProximitySensorNearby(final SensorEvent event) {
        float threshold = 4.001f;  // <= 4 cm is near

        final float distanceInCm = event.values[0];
        final float maxDistance  = event.sensor.getMaximumRange();
        Log.i("zhangming", "Proximity sensor report ["+ distanceInCm +"] , for max range ["+ maxDistance+ "]");

        if (maxDistance <= threshold) {
            // Case binary 0/1 and short sensors
            threshold = maxDistance;
        }

        return distanceInCm < threshold;
    }

    static class FlvGridAdapter extends BaseAdapter{
        private static final int TILES_COUNT = 4;
        private static final int[] drawables = {
            R.drawable.blue_part_bg,
            R.drawable.green_part_bg,
            R.drawable.purple_part_bg,
            R.drawable.yellow_part_bg
        };
        private Context mContext;

        public FlvGridAdapter(Context context){
            this.mContext = context;
            assert(this.mContext != null);
        }

        @Override
        public int getCount() {
            return TILES_COUNT;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            RelativeLayout view;
            if(convertView == null){
                if(mContext instanceof Activity) {
                    Activity activity = (Activity) mContext;
                    view = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.grid_item,parent,false);
                } else{
                    view = null;
                }
            }else{
                view = (RelativeLayout)convertView;
            }

            //设置4块的背景色[使用Selector选择器]
            view.setBackground(mContext.getResources().getDrawable(drawables[position]));

            //设置4块的内容
            String string1 = "";
            String string2 = "";
            if(position == 0) {
                string1 = "Portrait";
                string2 = "Flv + Local";
            } else if(position == 1) {
                string1 = "Landscape";
                string2 = "Rtmp";
            } else if(position == 2) {
                string1 = "Portrait";
                string2 = "Part";
            } else if(position == 3) {
                string1 = "Portrait";
                string2 = "Screen + Rtmp";
            }
            TextView tv1 = (TextView) view.findViewById(R.id.text1);
            TextView tv2 = (TextView) view.findViewById(R.id.text2);
            tv1.setText(string1);
            tv2.setText(string2);

            //设置GridView中4块部分的点击事件
            final int currentPosition = position;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(currentPosition == 0) {
                        //自定义View实战 --- 日历控件
                        //Intent intent = new Intent(mContext, CalendarActivity.class);
                        //mContext.startActivity(intent);
                        //ARouter路由框架跳转日历控件
                        ARouter.getInstance().build(CommonConstants.Calendar_Activity).navigation();
                    } else if(currentPosition == 1) {
                        //二维码测试界面
                        Intent intent = new Intent(mContext, QRActivity.class);
                        mContext.startActivity(intent);
                    } else if(currentPosition == 2) {
                        //View的基本工作原理测试
                        Intent intent = new Intent(mContext, ViewOperatorActivity.class);
                        mContext.startActivity(intent);
                    } else if(currentPosition == 3) {
                        //自定义View实战 --- 用于包裹ListView的ViewGroup,实现下拉刷新,上拉加载的功能
                        //Intent intent = new Intent(mContext,RefreshViewActivity.class);
                        //mContext.startActivity(intent);
                        //使用隐式启动方式,在调用startActivity时候,默认会为Intent加上"android.intent.category.DEFAULT"这样一个category
                        Intent intent = new Intent();
                        intent.setAction("com.itheima.view1");
                        intent.addCategory("com.itheima.viewgroup");
                        intent.setDataAndType(Uri.parse("http://www.itheima.com"),"video/mpeg");

                        ComponentName cName = intent.resolveActivity(mContext.getPackageManager());
                        if(cName != null){
                            mContext.startActivity(intent);
                        }
                    }
                }
            });

            return view;
        }
    }
}
