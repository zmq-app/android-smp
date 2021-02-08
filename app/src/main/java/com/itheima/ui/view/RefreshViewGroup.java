package com.itheima.ui.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.smp.R;
import com.itheima.ui.RefreshViewActivity;
import com.itheima.ui.adapter.MyRefreshListAdapter;
import com.itheima.utils.CommonConstants;
import com.itheima.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Subject  自定义刷新的ViewGroup控件,可用于包围ListView
 * @Function 基本功能包含下拉刷新,上拉加载,滑动删除等功能
 * @Author   zhangming
 * @Date     2018-09-26 14:48
 */
public class RefreshViewGroup extends ViewGroup {
    private Context mContext;
    private View mHeadView;
    private View mTailView;
    private ListView mListView;
    private BaseAdapter mAdapter;
    private Scroller mViewGroupScroller;

    private ImageView refreshDownRowView,refreshUpRowView;
    private ProgressBar refreshDownProgressBar,refreshUpProgressBar;
    private TextView  refreshDownStatusView,refreshUpStatusView;

    private MyRefreshTask rTask;    //异步刷新任务实例

    private final int headViewHeight = 300;   //ListView头部下拉刷新的总高度
    private final int tailViewHeight = 300;   //ListView底部上拉加载的总高度
    private int mItemHeight = 0;              //存储ListView Item条目的高度

    private static final int ORITATION_UNKNOW = 0;
    private static final int ORITATION_UP     = 1;
    private static final int ORITATION_DOWN   = 2;
    private static final int ORITATION_LEFT   = 3;
    private static final int ORITATION_RIGHT  = 4;

    public RefreshViewGroup(Context context) {
        super(context);
        initView(context);
        setListener();
    }

    public RefreshViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        setListener();
    }

    public RefreshViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        setListener();
    }

    private void initView(Context context){
        this.mContext = context;

        this.mViewGroupScroller = new Scroller(context);
        this.mHeadView = LayoutInflater.from(mContext).inflate(R.layout.listview_header,this,false);
        this.mTailView = LayoutInflater.from(mContext).inflate(R.layout.listview_tail,this,false);
        this.mListView = new ListView(mContext);

        //添加ListView的头尾分割线
        //mListView.addHeaderView(new ViewStub(context));
        //mListView.addFooterView(new ViewStub(context));

        this.refreshDownRowView = (ImageView) mHeadView.findViewById(R.id.iv_refresh_down_rrow);
        this.refreshDownStatusView = (TextView) mHeadView.findViewById(R.id.tv_refresh_down_status);
        this.refreshDownProgressBar = (ProgressBar) mHeadView.findViewById(R.id.iv_refresh_down_pb);
        this.refreshUpRowView = (ImageView) mTailView.findViewById(R.id.iv_refresh_up_rrow);
        this.refreshUpStatusView = (TextView) mTailView.findViewById(R.id.tv_refresh_up_status);
        this.refreshUpProgressBar = (ProgressBar) mTailView.findViewById(R.id.iv_refresh_up_pb);

        this.refreshDownProgressBar.setVisibility(View.GONE);
        this.refreshUpProgressBar.setVisibility(View.GONE);

        this.addView(mHeadView);
        this.addView(mListView);
        this.addView(mTailView);
    }

    private void setListener(){
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String msg = "Current click item position = "+position;
                Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** 对外提供的设置适配器的接口,有UI上Activity进行控制; 本ViewGroup负责ListView控件的数据填充 **/
    public void setListViewAdapter(BaseAdapter listAdapter){
        this.mAdapter = listAdapter;
        this.mListView.setAdapter(mAdapter);
        CommonUtils.setListViewHeightBasedOnChildren(mListView);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int[] result = CommonUtils.getScreenWidthandHeight(mContext);

        int wMeasureMode = MeasureSpec.getMode(widthMeasureSpec);
        int hMeasureMode = MeasureSpec.getMode(heightMeasureSpec);
        if(wMeasureMode == MeasureSpec.AT_MOST){
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(result[0],MeasureSpec.EXACTLY);
        }
        if(hMeasureMode == MeasureSpec.AT_MOST){
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(result[1]+headViewHeight+tailViewHeight,MeasureSpec.EXACTLY);
        }

        int childCount = getChildCount();
        for(int i=0;i<childCount;i++){
            View child = getChildAt(i);
            measureChild(child,widthMeasureSpec,heightMeasureSpec);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int[] result = CommonUtils.getScreenWidthandHeight(mContext);

        View headView = getChildAt(0);
        headView.layout(0,(-1)*headViewHeight,result[0],0);

        View listView = getChildAt(1);
        listView.layout(0,0,result[0],result[1]);

        View tailView = getChildAt(2);
        tailView.layout(0,result[1],result[0],result[1]+tailViewHeight);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if(hasWindowFocus){
            mItemHeight = getListItemHeight();
            Log.i(CommonConstants.TAG,"onWindowFocusChanged get ItemView Height = "+mItemHeight);
        }
    }

    class MyRefreshTask extends AsyncTask<Void,Integer,Boolean>{
        private int refreshOritation;
        private List<RefreshViewActivity.DataModel> mList;

        public MyRefreshTask(int refreshOritation){
            this.refreshOritation = refreshOritation;
            this.mList = new ArrayList<>();
        }

        @Override
        protected void onPreExecute() {
            if(refreshOritation == ORITATION_DOWN){
                refreshDownRowView.setVisibility(View.GONE);
                refreshDownProgressBar.setVisibility(View.VISIBLE);
                refreshDownStatusView.setText(getResources().getString(R.string.pull_to_refresh_refreshing_label));
            }else if(refreshOritation == ORITATION_UP){
                refreshUpRowView.setVisibility(View.GONE);
                refreshUpProgressBar.setVisibility(View.VISIBLE);
                refreshUpStatusView.setText(getResources().getString(R.string.pull_to_refresh_footer_refreshing_label));
            }
        }

        //第一个参数Void,异步任务执行时传入的参数
        //第三个参数Boolean,异步任务执行完成后返回的结果类型
        @Override
        protected Boolean doInBackground(Void... params) {
            int nCount = 0;
            int num = (int)System.currentTimeMillis()%1000;

            try{
                while (nCount < 10){
                    nCount++;
                    if(refreshOritation == ORITATION_UP){
                        //仅在上拉加载,添加数据;下拉刷新则保持不变
                        RefreshViewActivity.DataModel model = new RefreshViewActivity.DataModel(R.mipmap.ic_launcher,String.valueOf(num++),false);
                        mList.add(model);
                    }
                    Thread.sleep(200);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            return true;
        }


        //参数result:异步任务执行完成后返回的结果类型
        @Override
        protected void onPostExecute(Boolean result) {
            //平滑滚动到初始处
            smoothVerticalScrollBy(0, (-1) * getScrollY());

            //恢复View的初始状态
            if(refreshOritation == ORITATION_DOWN){
                refreshDownRowView.setVisibility(View.VISIBLE);
                refreshDownProgressBar.setVisibility(View.GONE);
                refreshDownStatusView.setText(getResources().getString(R.string.pull_to_refresh_pull_label));
            }else if(refreshOritation == ORITATION_UP){
                refreshUpRowView.setVisibility(View.VISIBLE);
                refreshUpProgressBar.setVisibility(View.GONE);
                refreshUpStatusView.setText(getResources().getString(R.string.pull_to_refresh_footer_pull_label));
            }

            //释放Task引用
            if(rTask != null){
                //保护措施:再次检查,确保刷新后的平滑能恢复到原状
                if(getScrollY() != 0){
                    smoothVerticalScrollBy(0, (-1) * getScrollY());
                }
                rTask = null;
            }

            if((mAdapter != null) && (mAdapter instanceof MyRefreshListAdapter)){
                MyRefreshListAdapter rlAdapter = (MyRefreshListAdapter) mAdapter;
                rlAdapter.refreshListener.onRefreshData(mList);  //回调监听的方法
            }
        }

        //应当调用rTask.cancel()方可执行此onCancelled回调方法
        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    private int mLastX;
    private int mLastY;
    private int oritation;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(rTask != null){
            Log.i(CommonConstants.TAG,"Refresh Task is excuting,Slide should stop");
            return true;
        }

        int x = (int)ev.getX();
        int y = (int)ev.getY();
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mLastX = 0;
                mLastY = 0;
                oritation = ORITATION_UNKNOW;
                break;
            case MotionEvent.ACTION_MOVE:
                int mdx = x - mLastX;
                int mdy = y - mLastY;
                if(oritation == ORITATION_UNKNOW){
                    //判断水平方向还是竖直方向滑动
                    if(Math.abs(mdx) < Math.abs(mdy)){
                        if(mdy > 0){
                            oritation = ORITATION_DOWN; //记录起始操作为向下滑动
                        }else if(mdy < 0){
                            oritation = ORITATION_UP;   //记录起始操作为向上滑动
                        }
                    }else{
                        if(mdx > 0){
                            oritation = ORITATION_RIGHT; //记录起始操作为向右滑动
                        }else if(mdx < 0){
                            oritation = ORITATION_LEFT;  //记录起始操作为向左滑动
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        mLastX = x;
        mLastY = y;

        return super.dispatchTouchEvent(ev);
    }

    private int mDownX;
    private int mDownY;
    private int tnlastX;
    private int tnlastY;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean isIntercept = false;
        int touchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop()*4/5; //减小认定为滑动的最小距离,提升手指滑动的流畅度;使用0.8的系数
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.d(CommonConstants.TAG,"onInterceptTouchEvent ACTION_DOWN...");
                if(!mViewGroupScroller.isFinished()){
                    mViewGroupScroller.abortAnimation();
                }
                tnlastX = 0;
                tnlastY = 0;
                mDownX  = x;
                mDownY  = y;
                mTLastX = x;  //记录第一次按下的横坐标mTLastX,用于后续的onTouchEvent事件
                mTLastY = y;  //记录第一次按下的纵坐标mTLastY,用于后续的onTouchEvent事件
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(CommonConstants.TAG,"onInterceptTouchEvent ACTION_MOVE...");
                if(oritation == ORITATION_LEFT || oritation == ORITATION_RIGHT){ //水平方向
                    if(tnlastX > 0 && Math.abs(tnlastX-x) > touchSlop){
                        //判定为滑动事件
                        isIntercept = true;
                    }else{
                        //判定为点击事件
                        isIntercept = false;
                        Log.d(CommonConstants.TAG,"onInterceptTouchEvent ACTION_MOVE clicked, Math.abs(tnlastX-x) = "+Math.abs(tnlastX-x));
                    }
                }else if(oritation == ORITATION_DOWN || oritation == ORITATION_UP){ //竖直方向
                    if(tnlastY > 0 && Math.abs(tnlastY-y) > touchSlop) {
                        //判定为滑动事件
                        if((mListView.getFirstVisiblePosition() == 0) && (y- tnlastY) > 0){
                            //处于第一个条目,并向下滑动,则执行下拉刷新
                            isIntercept = true;
                        }
                        if((mListView.getLastVisiblePosition() == mAdapter.getCount()-1) && (y-tnlastY) < 0){
                            //处于最后一个条目,并向上滑动,则执行上拉加载
                            isIntercept = true;
                        }
                        Log.d(CommonConstants.TAG,"onInterceptTouchEvent ACTION_MOVE slided, Math.abs(tnlastY-y) = "+Math.abs(tnlastY-y));
                    }else{
                        //判定为点击事件
                        isIntercept = false;
                        Log.d(CommonConstants.TAG,"onInterceptTouchEvent ACTION_MOVE clicked, Math.abs(tnlastY-y) = "+Math.abs(tnlastY-y));
                    }
                }else{
                    isIntercept = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d(CommonConstants.TAG,"onInterceptTouchEvent ACTION_UP...");
                break;
        }
        tnlastX = x;
        tnlastY = y;

        return isIntercept;
    }

    private int mTLastX;
    private int mTLastY;
    private View slideView;
    private int slideItemIndex = -1;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(CommonConstants.TAG,"onTouchEvent event = "+event.getAction());
        int x = (int)event.getX();
        int y = (int)event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                //没有条目时,滑动没有意义,直接和点击事件一样,不做任何处理返回
                if(mListView.getMeasuredHeight() == 0){
                    return true;
                }

                //竖直方向滑动事件处理
                if(oritation == ORITATION_DOWN || oritation == ORITATION_UP){
                    int mdy = y - mTLastY;
                    if((getScrollY() > 0) && (mdy < 0) && (oritation == ORITATION_DOWN)){
                        //起始操作为向下滑动,此时向上回退到顶部,不能再向上拖动;但可以再向下滑动
                        return true;
                    }
                    if((getScrollY() < 0) && (mdy > 0) && (oritation == ORITATION_UP)){
                        //起始操作为向上滑动,此时向下回退到底部,不能再向下拖动;但可以再向上滑动
                        return true;
                    }
                    if((getScrollY() <= (-1)*headViewHeight) && (mdy > 0) && (oritation == ORITATION_DOWN)){
                        return true;
                    }
                    if((getScrollY() >= tailViewHeight) && (mdy < 0) && (oritation == ORITATION_UP)){
                        return true;
                    }

                    //下拉状态变化实时更新
                    if((getScrollY() < 0) && (oritation == ORITATION_DOWN)){
                        if(getScrollY() > (-1)*(headViewHeight*3/4)){
                            refreshDownRowView.setImageResource(R.drawable.rectangle_rrow_down);
                            refreshDownStatusView.setText(mContext.getResources().getString(R.string.pull_to_refresh_pull_label));
                        }else if(getScrollY() <= (-1)*(headViewHeight*3/4)){
                            refreshDownRowView.setImageResource(R.drawable.rectangle_rrow_up);
                            refreshDownStatusView.setText(mContext.getResources().getString(R.string.pull_to_refresh_release_label));
                        }
                    }
                    //上拉状态变化实时更新
                    if((getScrollY() > 0) && (oritation == ORITATION_UP)){
                        if(getScrollY() < (tailViewHeight*3/4)){
                            refreshUpRowView.setImageResource(R.drawable.rectangle_rrow_up);
                            refreshUpStatusView.setText(mContext.getResources().getString(R.string.pull_to_refresh_footer_pull_label));
                        }else if(getScrollY() >= (tailViewHeight*3/4)){
                            refreshUpRowView.setImageResource(R.drawable.rectangle_rrow_down);
                            refreshUpStatusView.setText(mContext.getResources().getString(R.string.pull_to_refresh_footer_release_label));
                        }
                    }
                    scrollBy(0,(-1)*mdy);
                }

                //水平方向滑动事件处理
                if(oritation == ORITATION_LEFT || oritation == ORITATION_RIGHT){
                    //TODO 计算ListView上方隐藏条目的总高度
                    int firstVisibleItemIndex = mListView.getFirstVisiblePosition();
                    int firstVisibleItemHidHeight = Math.abs(mListView.getChildAt(0).getTop());
                    int totalHidHeight = (firstVisibleItemIndex)*(mItemHeight+mListView.getDividerHeight()) + (firstVisibleItemHidHeight);

                    //TODO 计算手指按下的位置距离ListView控件上方顶部的距离
                    //TODO 使用按下的起始坐标mDownY,而不是使用当前滑动时的坐标y来计算参考,用于解决斜着滑出现两个条目同时出现删除Button的情况
                    int hy = totalHidHeight + mDownY;
                    int nCount = mListView.getAdapter().getCount();
                    for(int i=0;i<nCount;i++){
                        int beginY = i*(mItemHeight+mListView.getDividerHeight());
                        int endY = (i+1)*(mItemHeight+mListView.getDividerHeight());
                        if((hy > beginY) && (hy <= endY)){
                            slideItemIndex = i;
                            break;
                        }
                    }

                    if(slideItemIndex >= 0){
                        slideView = MyRefreshListAdapter.lMaps.get(slideItemIndex);
                        MyRefreshListAdapter.DeleteButton delBtn = (MyRefreshListAdapter.DeleteButton) slideView.findViewById(R.id.delete_btn);
                        int leftSlideMaxDistance = delBtn.getMeasuredWidth() + 10;

                        //当前横坐标与第一次按下的横坐标进行差值计算,用于比较此时的位置相对于起始的位置的方向
                        int mdx = x - mTLastX;
                        if((slideView.getScrollX() <= 0) && (oritation == ORITATION_RIGHT)){
                            return true;
                        }
                        if((slideView.getScrollX() < 0) && (mdx > 0) && (oritation == ORITATION_LEFT)){
                            return true;
                        }
                        if((slideView.getScrollX() >= leftSlideMaxDistance) && (mdx < 0) && (oritation == ORITATION_LEFT)){
                            return true;
                        }
                        if((slideView.getScrollX() + Math.abs(mdx) >= leftSlideMaxDistance) && (oritation == ORITATION_LEFT)){
                            mdx = (-1)*Math.abs(leftSlideMaxDistance - slideView.getScrollX());
                        }

                        //此滑动方法调用放入回调函数中进行调用
                        //slideView.scrollBy((-1)*mdx,0);
                        if(delBtn.mListener != null){
                            //执行回调方法,记录滑动view的位置对应的状态,传入滑动值,准备调用scrollBy方法滑动
                            delBtn.mListener.onSlideView(slideItemIndex,true,mdx);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if(oritation == ORITATION_DOWN || oritation == ORITATION_UP){
                    //竖直方向滑动后抬起操作
                    if(((oritation == ORITATION_DOWN) && (getScrollY() > (-1)*(headViewHeight*3/4)))
                        ||((oritation == ORITATION_UP) && (getScrollY() < (tailViewHeight*3/4)))){
                        smoothVerticalScrollBy(0,(-1)*getScrollY());
                    }else{
                        //使用异步任务执行刷新操作
                        rTask = new MyRefreshTask(oritation);
                        rTask.execute();
                    }
                }
                if(oritation == ORITATION_LEFT || oritation == ORITATION_RIGHT){
                    //水平方向滑动后抬起操作
                    if(slideView != null){
                        int totalSlideX = slideView.getScrollX();
                        if(totalSlideX < 0){
                            totalSlideX = 0;
                            //使用属性动画模拟滑动效果
                            //ObjectAnimator.ofFloat(slideView,"translationX",0,Math.abs(x - mDownX)).setDuration(1000).start();
                            final int startX = (-1)*totalSlideX;
                            final int deltaX = (-1)*totalSlideX;
                            final ValueAnimator animator = ValueAnimator.ofInt(0,1).setDuration(1000);
                            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    float fraction = animator.getAnimatedFraction();  //fraction百分比,取值范围为0~1
                                    slideView.scrollTo(startX - (int)(deltaX*fraction),0);
                                }
                            });
                            animator.start();

                            if(slideItemIndex != -1){
                                MyRefreshListAdapter.DeleteButton delBtn = (MyRefreshListAdapter.DeleteButton) slideView.findViewById(R.id.delete_btn);
                                delBtn.mListener.onSlideView(slideItemIndex,false,0);
                            }
                        }
                    }
                }
                oritation = ORITATION_UNKNOW;
                break;
        }
        if(oritation == ORITATION_LEFT || oritation == ORITATION_RIGHT) {
            mTLastX = x;
        }else if(oritation == ORITATION_DOWN || oritation == ORITATION_UP){
            mTLastY = y;
        }
        return true;
    }

    private int getListItemHeight(){
       /**
        * 此处获取ListView控件的总高度必须使用getMeasuredHeight,而不能使用getHeight
        * View中getMeasuredHeight和getHeight的区别
        * 实际上在当屏幕可以包裹内容的时候,他们的值相等. 只有当view超出屏幕后,才能看出他们的区别
        * getMeasuredHeight方法获取的大小是实际View的大小,与屏幕无关
        * 而getHeight方法获取的大小此时则是屏幕的大小
        * 当超出屏幕后,getMeasuredHeight()等于getHeight()加上屏幕之外没有显示的大小
        **/
        int nCount = mListView.getAdapter().getCount();
        int divideHeight = mListView.getDividerHeight();
        int divideTotalHeight = (nCount-1)*divideHeight;
        int totalHeight = mListView.getMeasuredHeight();

        return (totalHeight - divideTotalHeight)/nCount;
    }

    private void smoothVerticalScrollBy(int dx, int dy){
        mViewGroupScroller.startScroll(0,getScrollY(),0,dy,500);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if(mViewGroupScroller != null && mViewGroupScroller.computeScrollOffset()){
            scrollTo(mViewGroupScroller.getCurrX(),mViewGroupScroller.getCurrY());
            postInvalidate();
        }
    }
}
