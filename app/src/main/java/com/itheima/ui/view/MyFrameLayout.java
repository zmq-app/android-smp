package com.itheima.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.itheima.smp.R;
import com.itheima.utils.CommonConstants;

public class MyFrameLayout extends FrameLayout {
    private Context mContext;
    private float mAspectRatio = 9.0f/16;

    public MyFrameLayout(@NonNull Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public MyFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
        initAspectRatio(attrs);
    }

    public MyFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    public void initView(){
        MyCircleView view = new MyCircleView(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);
        view.setPadding(20,20,20,20);  //设置view的padding距
        view.setBackgroundColor(getResources().getColor(R.color.purple)); //此处view圆四周包围的矩形区域是紫色,内部圆的颜色是蓝色

        MyLinearLayout ll = new MyLinearLayout(mContext);
        ll.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
        ll.setBackgroundColor(getResources().getColor(R.color.green));

        ll.addView(view);
        this.addView(ll);

        //以下两处均为零,尚未开始测量
        Log.i(CommonConstants.TAG,"MyView Width: "+this.getWidth()+" MyView Height: "+this.getHeight());
        Log.i(CommonConstants.TAG,"MyView Measure Width: "+this.getMeasuredWidth()+" MyView Measure Height: "+this.getMeasuredHeight());
    }

    private void initAspectRatio(AttributeSet attrs){
        TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.FrameLayoutView);
        mAspectRatio = ta.getFloat(R.styleable.FrameLayoutView_aspect_ratio,9.0f/16);
        ta.recycle();
        Log.d(CommonConstants.TAG,"mAspectRatio = "+mAspectRatio);
    }

    /** 从打印上看,遵守从measure,layout,draw的顺序流程,只有执行完父子控件各个onMeasure函数,接着才执行父子控件各个onLayout函数,... **/
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        Log.i(CommonConstants.TAG,"onMeasure widthSpecSize: "+widthSpecSize+" heightSpecSize: "+heightSpecSize);

        //对于继承view的自定义控件一般需要重写onMeasure方法,并设置wrap_content时的自身大小,否则在布局中使用wrap_content就相当于使用match_parent
        //诸如:TextView和ImageView控件,针对其wrap_content的情况均做过特殊处理
        if(widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.AT_MOST){
            heightSpecSize = (int)(widthSpecSize / mAspectRatio);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSpecSize,MeasureSpec.EXACTLY);
        }else if(widthMeasureSpec == MeasureSpec.AT_MOST && heightMeasureSpec == MeasureSpec.EXACTLY){
            widthSpecSize    = (int)(heightSpecSize * mAspectRatio);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSpecSize,MeasureSpec.EXACTLY);
        }
        Log.i(CommonConstants.TAG,"Measure Width = "+getMeasuredWidth()+" Measure Height = "+getMeasuredHeight());

        /**
        //测量该ViewGroup控件中的子view
        Log.i(CommonConstants.TAG,"getChildCount = "+getChildCount());
        for (int i = 0; i < getChildCount(); i++) {
            int[] args = CommonUtils.getScreenWidthandHeight(mContext);
            int wMeasureSpec = MeasureSpec.makeMeasureSpec(args[0],MeasureSpec.EXACTLY);
            int hMeasureSpec = MeasureSpec.makeMeasureSpec(args[1]/2,MeasureSpec.EXACTLY);
            getChildAt(i).measure(wMeasureSpec,hMeasureSpec);
        }
     　　*/
        //保存更改后的测量宽高widthSpecSize,heightSpecSize
        //setMeasuredDimension(widthSpecSize,heightSpecSize);

        //调用FrameLayout类中的onMeasure方法,将自定义父控件中的测量规则以及子控件中的LayoutParams配置
        //通过进入measureChildWithMargins方法去调用getChildMeasureSpec函数,从而获取到每个子控件对应的测量规则
        //最后使用上述获取子控件到的的测量规则,调用measure方法对每个子控件进行测量
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.i(CommonConstants.TAG,"MyFrameLayout onLayout getChildCount = "+getChildCount());
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            int nWidth = child.getMeasuredWidth();
            int nHeight = child.getMeasuredHeight();
            Log.i(CommonConstants.TAG,"nWidth = "+nWidth+" nHeight = "+nHeight);

            int mLeft = (right - nWidth) / 2;
            int mTop  = (bottom - nHeight) / 2;
            child.layout(mLeft, mTop, mLeft + nWidth, mTop + nHeight);
        }
    }

    /**
     * 测量规则参考函数
     * 根据父View测量模式和子View的LayoutParams,计算子类的宽度(width)测量规则
     * @param parentWidthMeasureSpec
     * @param view
     */
    private int createChildWidthMeasureSpec(int parentWidthMeasureSpec, View view) {
        // 获取父 View 的测量模式
        int parentWidthMode = MeasureSpec.getMode(parentWidthMeasureSpec);
        // 获取父 View 的测量尺寸
        int parentWidthSize = MeasureSpec.getSize(parentWidthMeasureSpec);

        // 定义子 View 的测量规则
        int childWidthMeasureSpec = 0;
        // 获取子 View 的LayoutParams
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();

        // 通过测量模式和子view的LayoutParams参数配置来决定子view的测量规则
        if (parentWidthMode == MeasureSpec.EXACTLY){
            /* 这是当父类的模式是 dp 的情况 */
            if (layoutParams.width > 0){
                childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(layoutParams.width, MeasureSpec.EXACTLY);
            } else if(layoutParams.width == LayoutParams.WRAP_CONTENT){
                childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(parentWidthSize, MeasureSpec.AT_MOST);
            } else if (layoutParams.width == LayoutParams.MATCH_PARENT){
                childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(parentWidthSize, MeasureSpec.EXACTLY);
            }
        } else if (parentWidthMode == MeasureSpec.AT_MOST){
            /* 这是当父类的模式是 WRAP_CONTENT 的情况 */
            if (layoutParams.width > 0){
                childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(layoutParams.width, MeasureSpec.EXACTLY);
            } else if(layoutParams.width == LayoutParams.WRAP_CONTENT){
                childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(parentWidthSize, MeasureSpec.AT_MOST);
            } else if (layoutParams.width == LayoutParams.MATCH_PARENT){
                childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(parentWidthSize, MeasureSpec.EXACTLY);
            }
        } else if (parentWidthMode == MeasureSpec.UNSPECIFIED){
            /* 这是当父类的模式是 MATCH_PARENT 的情况 */
            if (layoutParams.width > 0){
                childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(layoutParams.width, MeasureSpec.EXACTLY);
            } else if(layoutParams.width == LayoutParams.WRAP_CONTENT){
                childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            } else if (layoutParams.width == LayoutParams.MATCH_PARENT){
                childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            }
        }

        // 返回子 View 的测量规则
        return childWidthMeasureSpec;
    }
}
