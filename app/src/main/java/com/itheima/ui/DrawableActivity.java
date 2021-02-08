package com.itheima.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.itheima.smp.R;
import com.itheima.ui.view.CornerDrawable;
import com.itheima.utils.CommonConstants;

/**
 * @Subject  Android Drawable案例
 * @Function Drawable简介: Drawable是一个抽象类,是每个具体Drawable的基类
 * @Function Drawable分类: BitmapDrawable,ShapeDrawable,LayerDrawable,StateListDrawable,
 * LevelListDrawable,TransitionDrawable,InsetDrawable,ScaleDrawable,ClipDrawable
 * @Author  zhangming
 * @Date    2018-10-23 19:16
 */
public class DrawableActivity extends Activity {
    private ImageView levelImage;
    private ImageView scaleImage1,scaleImage2;
    private ImageView clipImage;
    private ImageView cornerImage;
    private TextView  mTextView;
    private Bitmap    mBitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawable);
        initView();
    }

    private void initView() {
        //TODO levelListDrawable 通过指定不同的等级来进行图片image的切换
        levelImage = (ImageView) findViewById(R.id.iv_level_drawable);
        levelImage.setImageResource(R.drawable.level_drawable);
        levelImage.setImageLevel(3);

        //TODO Drawable淡入淡出的动画效果展示
        mTextView = (TextView) findViewById(R.id.tv_transition_drawable);
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitionDrawable drawable = (TransitionDrawable) mTextView.getBackground();
                drawable.startTransition(3000);
            }
        });

        //TODO Drawable缩放效果展示
        //TODO level级别越大,Drawable看起来越大; 缩放比例越大,Drawable看起来越小
        //TODO android:background="@drawable/scale_drawable"
        scaleImage1 = (ImageView) findViewById(R.id.iv_scale_drawable1);
        scaleImage2 = (ImageView) findViewById(R.id.iv_scale_drawable2);
        ScaleDrawable sDrawable1 = (ScaleDrawable) scaleImage1.getBackground();
        sDrawable1.setLevel(3000);
        ScaleDrawable sDrawable2 = (ScaleDrawable) scaleImage2.getBackground();
        sDrawable2.setLevel(30);

        //TODO 等级范围0~10000,其中等级0表示完全裁剪,等级10000表示不裁剪
        //TODO XML android:clipOrientation="vertical" 表示竖直方向裁剪
        //TODO XML android:gravity="bottom" 将内部的Drawable放入容器的底部,不改变大小;如果为竖直裁剪,则从顶部开始裁剪
        //TODO XML android:src="@drawable/clip_drawable"
        clipImage = (ImageView) findViewById(R.id.iv_crop_drawable);
        ClipDrawable cDrawable = (ClipDrawable) clipImage.getDrawable();
        cDrawable.setLevel(7000);

        Log.i(CommonConstants.TAG,"DrawableActivity,cornerDrawable...");
        cornerImage = (ImageView) findViewById(R.id.corner_drawable);
        mBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.cpp);
        CornerDrawable cornerDrawable = new CornerDrawable(mBitmap);
        cornerDrawable.invalidateSelf();
        cornerImage.setImageDrawable(cornerDrawable);
    }

    @Override
    protected void onDestroy() {
        if(mBitmap != null){
            mBitmap.recycle();
            mBitmap = null;
        }
        super.onDestroy();
    }
}
