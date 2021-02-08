package com.itheima.ui.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.itheima.smp.R;
import com.itheima.utils.CommonConstants;
import com.itheima.utils.CommonUtils;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * @Subject 自定义View[日历控件]
 * @Author zhangming
 * @Date 2018-09-22
 */
public class MyCalendarView extends View {
    //private Bitmap[] recycleBitmaps;
    //private int curIndex;
    //private float calendarHeightRatio;

    private Context mContext;
    private int yearMonthHeight = 120;
    private int weekHeight      = 120;
    private int dayHeight       = 900;

    private String[] weekDays   = {"Sun","Mon","Tues","Wed","Thur","Fri","Sat"};
    private int[] dates         = {0,0,0};
    private Map<Integer,Rect> dayRectMaps;  //关联记录日期与对应于view中所在方格的边界Map集合

    public MyCalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        dayRectMaps = new TreeMap<>();
        //recycleBitmaps = new Bitmap[3];
        //TypedArray ta = mContext.obtainStyledAttributes(attrs,R.styleable.CalenderView);
        //calendarHeightRatio = ta.getFloat(R.styleable.CalenderView_calender_height_ratio,0.5f);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d(CommonConstants.TAG,"onAttachedToWindow...");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int[] result = CommonUtils.getScreenWidthandHeight(mContext);
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(result[0],MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(yearMonthHeight+weekHeight+dayHeight,MeasureSpec.EXACTLY);
        Log.i(CommonConstants.TAG,"onMeasure widthMeasureSpec = "+widthMeasureSpec+" heightMeasureSpec = "+heightMeasureSpec);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.i(CommonConstants.TAG,"onLayout left = "+left+" top = "+top+" right = "+right+" bottom = "+bottom);
    }

    /** 当View离开附着的窗口时触发,比如在Activity调用onDestroy方法时View就会离开窗口 **/
    /** 与刚开始的AttachedToWindow相比对,都只会被调用一次 **/
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d(CommonConstants.TAG,"onDetachedFromWindow...");
        //destroyBitmap();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //clearCanvas(canvas);
        //drawRectText(canvas);
        //drawMatrixBitmap(canvas);

        Paint rectPaint = new Paint();
        rectPaint.setStrokeWidth(3f);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setColor(getResources().getColor(R.color.green));

        Paint fontPaint = new Paint();
        fontPaint.setTextSize(60f);
        fontPaint.setAntiAlias(true);
        fontPaint.setStyle(Paint.Style.FILL);
        fontPaint.setColor(getResources().getColor(android.R.color.holo_orange_dark));
        fontPaint.setTextAlign(Paint.Align.CENTER);

        Log.i(CommonConstants.TAG,"onDraw dates[0] = "+dates[0]+" dates[1] = "+dates[1]+" dates[2] = "+dates[2]);
        drawYearMonthTitle(canvas,rectPaint,fontPaint);
        drawWeekTitle(canvas,rectPaint,fontPaint);
        drawDay(canvas,rectPaint,fontPaint);
    }

    public void setDate(int year,int month,int day){
        dates[0] = year;
        dates[1] = month;
        dates[2] = day;
    }

    private void drawYearMonthTitle(Canvas canvas,Paint rectPaint,Paint fontPaint){
        int[] result = CommonUtils.getScreenWidthandHeight(mContext);

        Rect mRect = new Rect(0,0,result[0],yearMonthHeight);
        canvas.drawRect(new RectF(mRect),rectPaint);
        canvas.drawLine(0,1,result[0],1,rectPaint);

        int curYear = dates[0];
        int curMonth = dates[1];
        StringBuilder sb = new StringBuilder();
        sb = sb.append(curYear).append("年");
        if(curMonth < 10){
            sb.append("0").append(curMonth).append("月");
        }else{
            sb.append(curMonth).append("月");
        }

        Paint.FontMetrics fontMetrics = fontPaint.getFontMetrics();
        float top = fontMetrics.top;        //为基线到字体上边框的距离,即上图中的top
        float bottom = fontMetrics.bottom;  //为基线到字体下边框的距离,即上图中的bottom
        int baseLineX = mRect.centerX();
        int baseLineY = (int) (mRect.centerY() - top/2 - bottom/2);//基线中间点的y轴计算公式
        canvas.drawText(new String(sb),baseLineX,baseLineY,fontPaint);
    }

    private void drawWeekTitle(Canvas canvas,Paint rectPaint,Paint fontPaint){
        int[] result = CommonUtils.getScreenWidthandHeight(mContext);
        canvas.drawRect(0,yearMonthHeight*2,result[0],weekHeight,rectPaint);

        //添加星期数字段
        for(int i=0;i<weekDays.length;i++) {
            int left = result[0]/weekDays.length * i;
            int right = result[0]/weekDays.length *(i+1);
            Rect mRect = new Rect(left,yearMonthHeight,right,yearMonthHeight+weekHeight);
            Paint.FontMetrics fontMetrics = fontPaint.getFontMetrics();
            float top = fontMetrics.top;        //为基线到字体上边框的距离,即上图中的top
            float bottom = fontMetrics.bottom;  //为基线到字体下边框的距离,即上图中的bottom
            int baseLineX = mRect.centerX();
            int baseLineY = (int) (mRect.centerY() - top/2 - bottom/2);//基线中间点的y轴计算公式
            canvas.drawText(weekDays[i],baseLineX,baseLineY,fontPaint);
        }

        //添加星期字段的竖直分割线
        for(int i=0;i<weekDays.length-1;i++) {
            Paint linePaint = new Paint();
            linePaint.setStrokeWidth(3f);
            linePaint.setStyle(Paint.Style.STROKE);
            linePaint.setColor(getResources().getColor(R.color.green));

            int curX = result[0]/weekDays.length *(i+1);
            int top  = yearMonthHeight;
            int bottom = top + weekHeight;
            canvas.drawLine(curX,top,curX,bottom,linePaint);
        }
    }

    private void drawDay(Canvas canvas,Paint rectPaint,Paint fontPaint){
        int[] result = CommonUtils.getScreenWidthandHeight(mContext);

        int curYear  = dates[0];
        int curMonth = dates[1];
        int curDay   = dates[2];
        Log.d(CommonConstants.TAG,"Date = "+curYear+"-"+curMonth+"-"+curDay);

        //根据高度绘制日期信息的矩形外框
        int lineNum = calLineNum(curYear,curMonth);
        int dayLineHeight = dayHeight / lineNum;
        canvas.drawRect(0,yearMonthHeight+weekHeight,result[0],yearMonthHeight+weekHeight+dayHeight,rectPaint);

        //获取当月总的实际天数
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(curYear-1900,curMonth-1,1));
        int dayofMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        //添加当月下所有天的数据信息
        int nLineIndex = 0;
        for(int d=1;d<=dayofMonth;d++){
            calendar.setTime(new Date(curYear-1900,curMonth-1,d));
            int wDay  = calendar.get(Calendar.DAY_OF_WEEK);
            int index = (wDay-1) % (weekDays.length);
            if((index == 0) && (d != 1)){
                nLineIndex++;
            }

            if(curDay == d){
                //当天,需要画一个圆圈提示用户
                int padding = 10;
                int left   = index * (result[0]/(weekDays.length)) + padding;
                int top    = (yearMonthHeight + weekHeight) + nLineIndex * dayLineHeight + padding;
                int right  = left + result[0]/(weekDays.length) - 2*padding;
                int bottom = top + dayLineHeight - 2*padding;

                int cx     = left + (right-left)/2;
                int cy     = top + (bottom-top)/2;
                int radius = Math.min((right-left),(bottom-top)) / 2;

                Paint circlePaint = new Paint();
                circlePaint.setStrokeWidth(2f);
                circlePaint.setStyle(Paint.Style.FILL);
                circlePaint.setColor(getResources().getColor(R.color.blue));
                canvas.drawCircle(cx,cy,radius,circlePaint);
            }

            //计算日期文字对应的周边矩形边框
            int left   = index * (result[0]/weekDays.length);
            int right  = (index+1) * (result[0]/weekDays.length);
            int top    = yearMonthHeight + weekHeight + (nLineIndex * dayLineHeight);
            int bottom = top + dayLineHeight;
            Rect mDayRect = new Rect(left,top,right,bottom);
            dayRectMaps.put(d,mDayRect);

            //绘制对应的日期字段
            Paint.FontMetrics fontMetrics = fontPaint.getFontMetrics();
            int baseLineX = mDayRect.centerX();
            int baseLineY = (int) (mDayRect.centerY() - (fontMetrics.top)/2 - (fontMetrics.bottom)/2);  //基线中间点的y轴计算公式
            canvas.drawText(String.valueOf(d),baseLineX,baseLineY,fontPaint);
        }

        //添加水平方向的间隔线
        for(int i=1;i<lineNum;i++){
            int curYIndex = i*(dayHeight/lineNum);
            canvas.drawLine(0,(yearMonthHeight + weekHeight + curYIndex),result[0],(yearMonthHeight + weekHeight + curYIndex),rectPaint);
        }
        canvas.drawLine(0,(yearMonthHeight + weekHeight + dayHeight - 2),result[0],(yearMonthHeight + weekHeight + dayHeight - 2),rectPaint);

        //添加竖直方向的间隔线[不包含日期边框的左右处]
        for(int i=1;i<weekDays.length;i++){
            int curXIndex = i*(result[0]/(weekDays.length));
            canvas.drawLine(curXIndex,(yearMonthHeight+weekHeight),curXIndex,(yearMonthHeight+weekHeight+dayHeight),rectPaint);
        }
    }

    private int calLineNum(int year,int month){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(year-1900,month-1,1));

        int wDay  = calendar.get(Calendar.DAY_OF_WEEK);
        int dayofMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);  //获取某月实际的天数
        if(dayofMonth == 28 && wDay == Calendar.SUNDAY){
            return 4;
        } else {
            boolean isAdd;
            if(wDay == Calendar.SATURDAY && dayofMonth >= 30){
                isAdd = true;
            } else if(wDay == Calendar.FRIDAY && dayofMonth == 31){
                isAdd = true;
            } else{
                isAdd = false;
            }
            return (isAdd)?(6):(5);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.d(CommonConstants.TAG,"MyCalendarView dispatchTouchEvent called,MotionEvent = "+event.getAction());
        //boolean rs = super.dispatchTouchEvent(event);
        //Log.d(CommonConstants.TAG,"MyCalendarView dispatchTouchEvent,result = "+rs);
        //返回值为false,则此view不进行事件分发[结果等价于onTouchEvent返回false],事件直接向上传递给父控件MyCalendarViewGroup的onTouchEvent函数进行处理
        //返回值为true,则表示此view消费掉整个事件,即不会再分发,也不会再处理
        //return super.dispatchTouchEvent(event),则执行分发动作,对ViewGroup而言先判断是否拦截,对View而言执行onTouchEvent方法
        return super.dispatchTouchEvent(event);
    }

    private int mIndex = 0;
    private Rect mRect = null;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(CommonConstants.TAG,"MyCalendarView onTouchEvent,clicked event arrived,MotionEvent = "+event.getAction());

        int tx = (int)event.getX();
        int ty = (int)event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                for(int i=1;i<=dayRectMaps.size();i++){
                    Rect rect = dayRectMaps.get(i);
                    if(tx >= rect.left && tx <= rect.right && ty >= rect.top && ty <= rect.bottom){
                        mRect = rect;
                        mIndex = i;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if(mRect != null && mIndex != 0){
                    if(tx >= mRect.left && tx <= mRect.right && ty >= mRect.top && ty <= mRect.bottom){
                        String mDate = dates[0]+"-"+dates[1]+"-"+mIndex;

                        /** 类似于Linux mktime系统调用 **/
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(new Date(dates[0]-1900,dates[1]-1,mIndex));
                        int wDay = calendar.get(Calendar.DAY_OF_WEEK);  //(wDay>=1 && wDay<=7)
                        String mWeek = weekDays[wDay-1];                //(0~6)
                        Toast.makeText(mContext,"Date="+mDate+",Week="+mWeek,Toast.LENGTH_SHORT).show();

                        /**
                         * invalidate方法不会执行measureHierarchy和performLayout方法,
                         * 也就不会执行measure和layout流程,只执行draw流程,
                         * 如果开启了硬件加速则只进行调用者View的重绘.
                         * requestLayout方法会依次measureHierarchy,performLayout,performDraw方法,
                         * 调用者View和它的父级View会重新进行measure,layout方法
                         * 但一般情况下如果view的位置和大小没变的话,是不会触发onDraw方法.
                         */
                        //invalidate();
                        //requestLayout();

                        /** 关闭Activity,如果是该task的rootActivity,则移除该Task,Recent中将看不到此Activity **/
                        //((Activity)mContext).finishAndRemoveTask();
                    }
                }
                break;
        }

        return true;
    }

    /**
     // 测试方法: 清空canvas画布
     private void clearCanvas(Canvas canvas){
     if(canvas != null){
     //清空Canvas画布
     Paint clearPaint = new Paint();
     clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
     canvas.drawPaint(clearPaint);
     }
     }

     // 测试方法: 画一个矩形和内部中央绘制文字
     private void drawRectText(Canvas canvas){
     Paint bgPaint = new Paint();
     bgPaint.setStrokeWidth(2f);
     bgPaint.setAntiAlias(true);
     bgPaint.setStyle(Paint.Style.FILL);
     bgPaint.setColor(getResources().getColor(R.color.blue));

     Rect mRect = new Rect(0,getMeasuredHeight()/4,getMeasuredWidth()/2,getMeasuredHeight()*3/4);
     RectF mRectF = new RectF(mRect);
     canvas.drawRect(mRectF,bgPaint);

     Paint fontPaint = new Paint();
     fontPaint.setTextSize(70f);
     fontPaint.setAntiAlias(true);
     fontPaint.setStyle(Paint.Style.FILL);
     fontPaint.setColor(getResources().getColor(android.R.color.holo_orange_dark));
     fontPaint.setTextAlign(Paint.Align.CENTER);

     String mText = "Hello World!!!";
     Paint.FontMetrics fontMetrics = fontPaint.getFontMetrics();
     float top = fontMetrics.top;        //为基线到字体上边框的距离,即上图中的top
     float bottom = fontMetrics.bottom;  //为基线到字体下边框的距离,即上图中的bottom
     int baseLineX = mRect.centerX();
     int baseLineY = (int) (mRect.centerY() - top/2 - bottom/2);//基线中间点的y轴计算公式
     Log.i(CommonConstants.TAG,"top = "+ top +" bottom = "+bottom+" baseLineX = "+baseLineX+" baseLineY = "+baseLineY);

     canvas.drawText(mText,baseLineX,baseLineY,fontPaint);
     }

     // 测试方法: 画一个bitmap,通过Matrix矩阵进行旋转平移等操作
     private void drawMatrixBitmap(Canvas canvas){
     try{
     Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.monitor_launcher);
     Matrix matrix = new Matrix();
     matrix.postScale(3f,3f);
     matrix.postRotate(30);
     matrix.postTranslate(120,0);

     if(recycleBitmaps != null){
     destroyBitmap();
     curIndex = 0;
     recycleBitmaps = new Bitmap[3];
     }

     Bitmap newBmp = Bitmap.createBitmap(bmp,0,0,bmp.getWidth(),bmp.getHeight(),matrix,true);
     int nLeft = getMeasuredWidth()*3/4 - bmp.getWidth()/2;
     int nTop  = getMeasuredHeight()/2 - bmp.getHeight()/2;
     Rect bmpRect = new Rect(nLeft,nTop,nLeft+bmp.getWidth(),nTop+bmp.getHeight());
     canvas.drawBitmap(newBmp,null,bmpRect,null);

     if(curIndex > recycleBitmaps.length){
     int nSize = recycleBitmaps.length * 2;
     recycleBitmaps = new Bitmap[nSize];
     }
     recycleBitmaps[curIndex++] = newBmp;

     if(bmp != null && bmp.isRecycled()){
     Log.d(CommonConstants.TAG,"bmp is recycled");
     bmp.recycle();
     }
     }catch (Exception e){
     e.printStackTrace();
     }
     }

     private void destroyBitmap(){
     if(recycleBitmaps != null && recycleBitmaps.length > 0){
     for(int i=0;i<recycleBitmaps.length;i++){
     Bitmap tmp = recycleBitmaps[i];
     Log.d(CommonConstants.TAG,"tmp_bitmap = "+tmp);
     if(tmp != null && tmp.isRecycled()){
     tmp.recycle();
     }else{
     break;
     }
     }
     recycleBitmaps = null;
     System.gc();
     }
     }
     **/

    /**
     * View的关键生命周期与Activity生命周期关系
     * Activity --> onCreate()
     * View     --> 构造View()
     * View     --> onFinishInflate()
     * Activity --> onStart()
     * Activity --> onResume()
     * View     --> onAttachedToWindow()
     * View     --> onMeasure()
     * View     --> onSizeChanged()
     * View     --> onLayout()
     * View     --> onDraw()
     * View     --> onWindowFocusChanged()  true
     * Activity --> onPause()
     * View     --> onWindowFocusChanged()  false
     * Activity --> onStop()
     * Activity --> onDestroy()
     * View     --> onDetackedFromWindow()
     */
}
