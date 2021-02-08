package com.itheima.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itheima.smp.R;

/**
 * @Subject 有关Handler removeCallbacks问题的研究
 * @URL     https://www.jianshu.com/p/8e8ac28f6c1a
 * @Date    2020-09-04 19:19
 * @Author  zhangming
 */
public class TimerRunnableActivity extends Activity {
    private TextView startTimerView;
    private TextView stopTimerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewTree(300, 600);
    }

    /**
     * @ViewTree Activity -> PhoneWindow -> DecorView -> FrameLayout -> TextView
     * @param startTimerViewMargin
     * @param stopTimerViewMargin
     */
    private void initViewTree(final int startTimerViewMargin, final int stopTimerViewMargin) {
        FrameLayout.LayoutParams startParams = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        startParams.topMargin = startTimerViewMargin;
        startTimerView = new TextView(this);
        startTimerView.setLayoutParams(startParams);
        startTimerView.setGravity(Gravity.CENTER);
        startTimerView.setText("startTimer");
        startTimerView.setTextSize(32f);
        startTimerView.setTextColor(getResources().getColor(R.color.blue));
        startTimerView.setClickable(true);

        FrameLayout.LayoutParams stopParams = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        stopParams.topMargin = stopTimerViewMargin;
        stopTimerView = new TextView(this);
        stopTimerView.setLayoutParams(stopParams);
        stopTimerView.setGravity(Gravity.CENTER);
        stopTimerView.setText("stopTimer");
        stopTimerView.setTextSize(32f);
        stopTimerView.setTextColor(getResources().getColor(R.color.blue));
        stopTimerView.setClickable(true);

        View decorView = getWindow().getDecorView();
        if (decorView != null) {
            FrameLayout rootViewGroup = (FrameLayout) decorView.findViewById(android.R.id.content);
            if (rootViewGroup != null) {
                rootViewGroup.addView(startTimerView, startParams);
                rootViewGroup.addView(stopTimerView, stopParams);
            }
        }
    }
}
