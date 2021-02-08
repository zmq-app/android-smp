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

import com.itheima.smp.R;
import com.itheima.utils.CommonUtils;

/**
 * Created by zhangming on 2018/10/14.
 */
public class FragmentOne extends Fragment {
    private Context mContext;
    private ImageView leftImgView;
    private Button leftBtn;
    private OnDataTransmissionListener mListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout ll = new LinearLayout(mContext);
        ll.setOrientation(LinearLayout.VERTICAL);  //动态设置Fragment的方向

        leftImgView = new ImageView(mContext);
        LinearLayout.LayoutParams iv_params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        iv_params.gravity = Gravity.CENTER_HORIZONTAL;
        iv_params.topMargin = CommonUtils.dip2px(mContext,150f);
        leftImgView.setLayoutParams(iv_params);
        leftImgView.setImageResource(R.mipmap.ic_launcher);

        leftBtn = new Button(mContext);
        LinearLayout.LayoutParams btn_params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        btn_params.gravity = Gravity.CENTER_HORIZONTAL;
        btn_params.topMargin = CommonUtils.dip2px(mContext,100f);
        btn_params.leftMargin = CommonUtils.dip2px(mContext,10f);
        btn_params.rightMargin = CommonUtils.dip2px(mContext,10f);
        leftBtn.setLayoutParams(btn_params);
        leftBtn.setText("Click ME");
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 方法一: 通过findFragmentByTag获取到FragmentTwo的实例引用
                FragmentOne ft1 = FragmentOne.this;
                FragmentTwo ft2 = (FragmentTwo) ((Activity)mContext).getFragmentManager().findFragmentByTag("FragmentTwo");
                ft2.setImageView(FragmentDemoActivity.curImageId[(FragmentDemoActivity.nCount)%2]);
                ft1.setImageView(FragmentDemoActivity.curImageId[(FragmentDemoActivity.nCount+1)%2]);
                FragmentDemoActivity.nCount++;

                //TODO 方法二: 调用回调接口,准备向FragmentTwo中发送消息
                mListener.dataTransmission("From FragmentOne Message = Hello World!!!");
            }
        });

        ll.addView(leftImgView);
        ll.addView(leftBtn);
        return ll;
    }

    public void setImageView(int imageId){
        leftImgView.setImageResource(imageId);
    }

    //TODO 通过回调接口实现,将在Activity中设置此接口
    public interface OnDataTransmissionListener {
        void dataTransmission(String data);
    }
    public void setOnDataTransmissionListener(OnDataTransmissionListener mListener) {
        this.mListener = mListener;
    }
}
