package com.itheima.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.itheima.utils.CommonConstants;
import com.itheima.utils.CommonUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * @Subject 自定义日历控件外围的ViewGroup组件
 * @Author  zhangming
 * @Date    2018-09-22
 */
public class MyCalendarViewGroup extends ViewGroup {
    private Context mContext;
    private int nTotalPage;
    private Scroller mScroller;
    private int mLastX;
    private static final int yearofDays = 365;

    public MyCalendarViewGroup(Context context) {
        super(context);
        initView(context);
    }

    public MyCalendarViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context){
        this.mContext = context;
        this.nTotalPage = yearofDays;
        this.mScroller = new Scroller(context);
        this.velocityTracker = VelocityTracker.obtain();

        //获取当天的日期信息
        Calendar calendar = Calendar.getInstance();
        int curYear  = calendar.get(Calendar.YEAR);
        int curMonth = calendar.get(Calendar.MONTH)+1;
        int curDay   = calendar.get(Calendar.DATE);

        //添加自定义view控件
        //在自定义view中,margin属性由父容器控制处理,padding属性则是由子容器自身来进行处理
        for(int i=0;i<nTotalPage;i++){
            MyCalendarView calView = new MyCalendarView(context,null);
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
            calView.setLayoutParams(params);
            //calView.setPadding(0,0,0,0);

            /**
            //设置对应的日期信息,暂以三天为例
            if(i == 0){
                int year = CommonUtils.getPrevDay(curYear,curMonth,curDay).getYear()+1900;
                int month = CommonUtils.getPrevDay(curYear,curMonth,curDay).getMonth()+1; //月份下标索引值是从0开始
                int day = CommonUtils.getPrevDay(curYear,curMonth,curDay).getDate();
                calView.setDate(year,month,day);
            }else if(i == 1){
                calView.setDate(curYear,curMonth,curDay);
            }else if(i == 2){
                int year = CommonUtils.getNextDay(curYear,curMonth,curDay).getYear()+1900;
                int month = CommonUtils.getNextDay(curYear,curMonth,curDay).getMonth()+1; //月份下标索引值是从0开始
                int day = CommonUtils.getNextDay(curYear,curMonth,curDay).getDate();
                calView.setDate(year,month,day);
            }
            **/

            if(i < nTotalPage/2){
                Date mDate = CommonUtils.getPrevDay(curYear,curMonth,curDay,(nTotalPage/2 - i));
                int year = mDate.getYear()+1900;
                int month = mDate.getMonth()+1;
                int day = mDate.getDate();
                calView.setDate(year,month,day);
            }else if (i > nTotalPage/2){
                Date mDate = CommonUtils.getNextDay(curYear,curMonth,curDay,i - nTotalPage/2);
                int year = mDate.getYear()+1900;
                int month = mDate.getMonth()+1;
                int day = mDate.getDate();
                calView.setDate(year,month,day);
            }else{
                calView.setDate(curYear,curMonth,curDay);
            }
            this.addView(calView,i);
        }
    }

    /**
     * 两个参数widthMeasureSpec,heightMeasureSpec,代表自定义控件组MyCalendarViewGroup的测量规则,其已经在父类确定好了
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /** 注:对于ViewGroup而言,除了完成自己的measure过程外,还需遍历所有子元素对其测量 **/
        /** 注:使用提供的方法measureChildren,其内部就在遍历所有子节点进行measureChild的过程 **/
        /** 注:如果不进行这一步测量,那么下一步的onLayout函数中的calView.getMeasuredWidth,calView.getMeasuredHeight将返回0 **/
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        /** 对自身进行测量,由于XML布局中使用的layout_width,layout_height均是wrap_content方式 **/
        /** 添加以下代码,对此父控件高度覆盖范围缩小 **/
        int[] result       = CommonUtils.getScreenWidthandHeight(mContext);
        int wMeasureMode   = MeasureSpec.getMode(widthMeasureSpec);
        int hMeasureMode   = MeasureSpec.getMode(heightMeasureSpec);
        int widthSpecSize  = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if(wMeasureMode == MeasureSpec.AT_MOST){
            //使测量宽度等于屏幕的宽度,并以此更新宽度测量规则widthMeasureSpec
            widthSpecSize    = nTotalPage * result[0];
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSpecSize,MeasureSpec.EXACTLY);
        }
        if(hMeasureMode == MeasureSpec.AT_MOST){
            //使测量高度等于子控件的高度,并以此更新高度测量规则heightMeasureSpec
            //最终达到此ViewGroup实际的高度范围缩小到子view的高度值[ 120+120+900=1140 ]
            int childHeight   = getChildAt(0).getMeasuredHeight();
            heightSpecSize    = childHeight;
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSpecSize,MeasureSpec.EXACTLY);
        }
        //最后将修改后的测量宽度widthMeasureSpec和测量高度heightMeasureSpec的新规则存储起来
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for(int i=0;i<nTotalPage;i++){
            MyCalendarView calView = (MyCalendarView) getChildAt(i);

            int vWidth  = calView.getMeasuredWidth();
            int vHeight = calView.getMeasuredHeight();
            int vTop = 0;
            int vBottom = vTop + vHeight;

            //TODO 调用自定义view的layout方法,源码方法中会先调用setFrame,设置对应calView的四周边界值,之后调用onLayout方法
            //TODO view默认的onLayout为空方法,所以此处仅仅是确定自定义view的位置即可,无需考虑child,毕竟是view
            //TODO 但如果是自定义控件是viewgroup,那么就需要覆盖父类view的onLayout空方法,实现自己对子控件的layout过程;否则如果不覆盖就会什么都不做从而结束layout过程,那么子控件的位置确定将是一个未知数
            //TODO 子view位置是在父view上通过调用setFrame方法时就已设置好了.
            calView.layout(i*vWidth,vTop,(i+1)*vWidth,vBottom);
        }
    }

    private int tnlastX;
    private int downX,upX;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean isIntercept = false;
        int touchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();
        int x = (int) ev.getX();
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(!mScroller.isFinished()){
                    mScroller.abortAnimation();
                }
                downX = (int) ev.getX();
                mLastX = downX;
                Log.d(CommonConstants.TAG,"ACTION_DOWN...");
                break;
            case MotionEvent.ACTION_MOVE:
                if(tnlastX > 0 && Math.abs(tnlastX-x) > touchSlop) {
                    //判定为滑动事件
                    isIntercept = true;
                    Log.d(CommonConstants.TAG,"ACTION_MOVE slided, Math.abs(tnlastX-x) = "+Math.abs(tnlastX-x));
                }else{
                    //判定为点击事件
                    isIntercept = false;
                    Log.d(CommonConstants.TAG,"ACTION_MOVE clicked, Math.abs(tnlastX-x) = "+Math.abs(tnlastX-x));
                }
                break;
            case MotionEvent.ACTION_UP:
                upX = (int) ev.getX();
                Log.d(CommonConstants.TAG,"ACTION_UP touchSlop = "+touchSlop+" Math.abs(upX-downX) = "+ Math.abs(upX-downX));
                break;
        }
        tnlastX = x;

        return isIntercept;
    }

    private VelocityTracker velocityTracker;        //VelocityTracker类[追踪当前事件的速度]
    private int curChildViewIndex = nTotalPage/2;   //当前子控件对应的索引下标值
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(CommonConstants.TAG,"MyCalendarViewGroup onTouchEvent,MotionEvent = "+event.getAction());
        //为VelocityTracker添加具体事件event,以便进行追踪
        velocityTracker.addMovement(event);
        int x = (int) event.getX();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //由于父控件中onInterceptTouchEvent方法中对于ACTION_DOWN事件返回false,即此事件会分派到子view中,而在子view中使用默认分发,则最后调用到子view的onTouchEvent方法,消费掉此事件.
                //所以此父控件MyCalendarViewGroup中的onTouchEvent方法对于ACTION_DOWN事件将不会得到执行
                break;
            case MotionEvent.ACTION_MOVE:
                int mdx = x - mLastX;
                if(curChildViewIndex == (-1)*(nTotalPage/2) && mdx > 0){
                    return true;
                }
                if(curChildViewIndex == (1)*(nTotalPage/2) && mdx < 0){
                    return true;
                }
                scrollBy(-mdx,0);
                break;
            case MotionEvent.ACTION_UP:
                //使用VelocityTracker类[追踪当前事件的速度]
                //从左向右滑动:velocityX>0; 从右向左滑动:velocityX<0
                //velocityTracker.computeCurrentVelocity(10);       //velocityX=55
                //velocityTracker.computeCurrentVelocity(100);      //velocityX=553
                velocityTracker.computeCurrentVelocity(1000); //velocityX=5534
                int velocityX = (int)velocityTracker.getXVelocity();
                if(curChildViewIndex == (-1)*(nTotalPage/2) && velocityX > 0){
                    return true;
                }
                if(curChildViewIndex == (1)*(nTotalPage/2) && velocityX < 0){
                    return true;
                }

                //计算当前子控件view的索引下标
                int flag;
                if(Math.abs(velocityX) >= 1500){
                    flag = (velocityX > 0)?(-1):(1);
                }else{
                    flag = 0;
                }
                curChildViewIndex = curChildViewIndex + flag;

                //getScrollX()函数返回mScrollX,表示view控件左边缘与view内容左边缘的距离差值
                //若mScrollX<0 <==> 从左向右滑动
                //若mScorllX>0 <==> 从右向左滑动
                int mChildWidth = getChildAt(0).getMeasuredWidth();
                int startX = getScrollX();
                int endX = curChildViewIndex * mChildWidth;
                int udx  = endX - startX;
                smoothScrollBy(udx,0);

                //重置VelocityTracker
                velocityTracker.clear();
                break;
            default:
                break;
        }
        mLastX = x;
        return true;
    }

    private void smoothScrollBy(int dx,int dy){
        mScroller.startScroll(getScrollX(),0,dx,0,500);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if(mScroller.computeScrollOffset()){
            scrollTo(mScroller.getCurrX(),mScroller.getCurrY());
            postInvalidate();
        }
    }
}
