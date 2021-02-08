package com.itheima.test;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.itheima.smp.R;
import com.itheima.utils.CommonUtils;

/**
 * Created by zhangming on 2018/10/14.
 */

public class FragmentTwo extends Fragment {
    private Context mContext;
    private ImageView rightImgView;
    private Button rightBtn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout ll = new LinearLayout(mContext);
        ll.setOrientation(LinearLayout.VERTICAL);  //动态设置Fragment的方向

        rightImgView = new ImageView(mContext);
        LinearLayout.LayoutParams iv_params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        iv_params.gravity = Gravity.CENTER_HORIZONTAL;
        iv_params.topMargin = CommonUtils.dip2px(mContext,150f);
        rightImgView.setLayoutParams(iv_params);
        rightImgView.setImageResource(R.drawable.monitor_launcher);

        rightBtn = new Button(mContext);
        LinearLayout.LayoutParams btn_params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        btn_params.gravity = Gravity.CENTER_HORIZONTAL;
        btn_params.topMargin = CommonUtils.dip2px(mContext,100f);
        btn_params.leftMargin = CommonUtils.dip2px(mContext,10f);
        btn_params.rightMargin = CommonUtils.dip2px(mContext,10f);
        rightBtn.setLayoutParams(btn_params);
        rightBtn.setText("Click ME");
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTwo ft2 = FragmentTwo.this;
                FragmentOne ft1 = (FragmentOne) ((Activity)mContext).getFragmentManager().findFragmentByTag("FragmentOne");
                ft1.setImageView(FragmentDemoActivity.curImageId[(FragmentDemoActivity.nCount+1)%2]);
                ft2.setImageView(FragmentDemoActivity.curImageId[(FragmentDemoActivity.nCount)%2]);
                FragmentDemoActivity.nCount++;
            }
        });

        ll.addView(rightImgView);
        ll.addView(rightBtn);
        return ll;
    }

    public void setImageView(int imageId){
        rightImgView.setImageResource(imageId);
    }

    //TODO 对外Activity的访问接口
    public void setData(String data){
        Toast.makeText(mContext,data,Toast.LENGTH_SHORT).show();
    }
}
