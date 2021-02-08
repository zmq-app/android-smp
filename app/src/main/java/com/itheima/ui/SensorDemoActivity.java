package com.itheima.ui;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.itheima.smp.R;

public class SensorDemoActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mvp_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        SensorManager sm = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        sm.registerListener(myAccelerometerListener,sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);

    }

    private final SensorEventListener myAccelerometerListener = new SensorEventListener(){
        private static final int ORIENTATION_UNKNOWN = -1;
        private static final int _DATA_X = 0;
        private static final int _DATA_Y = 1;
        private static final int _DATA_Z = 2;

        //复写onSensorChanged方法
        public void onSensorChanged(SensorEvent sensorEvent){
            if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                //图解中已经解释三个值的含义
                float[] values = sensorEvent.values;
                int orientation = ORIENTATION_UNKNOWN;
                float X = -values[_DATA_X];
                float Y = -values[_DATA_Y];
                float Z = -values[_DATA_Z];
                float magnitude = X * X + Y * Y;
                // Don't trust the angle if the magnitude is small compared to the y
                // value
                if (magnitude * 4 >= Z * Z) {
                    // 屏幕旋转时
                    float OneEightyOverPi = 57.29577957855f;
                    float angle = (float) Math.atan2(-Y, X) * OneEightyOverPi;
                    orientation = 90 - (int) Math.round(angle);
                    // normalize to 0 - 359 range
                    while (orientation >= 360) {
                        orientation -= 360;
                    }
                    while (orientation < 0) {
                        orientation += 360;
                    }
                }

                if (orientation > 45 && orientation < 135) {

                } else if (orientation > 135 && orientation < 225) {

                } else if (orientation > 225 && orientation < 315) {

                } else if ((orientation > 315 && orientation < 360) || (orientation > 0 && orientation < 45)) {

                }
                Log.i("zhangming","myAccelerometerListener orientation = "+orientation);
            }
        }
        //复写onAccuracyChanged方法
        public void onAccuracyChanged(Sensor sensor, int accuracy){
        }
    };
}
