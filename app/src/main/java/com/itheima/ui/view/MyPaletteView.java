package com.itheima.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义View控件,实现一个简易的画板,包括画笔绘制,撤销,重绘,橡皮檫,清除等功能
 * @author zhangming
 * @date 2017/09/27 16:35:45
 */
public class MyPaletteView extends View{
    private Paint mPaint;
    private Path mPath;
    private Bitmap mBufferBitmap;
    private Canvas mBufferCanvas;
    
    private float mLastX;
    private float mLastY;
    private List<PathDrawingInfo> mDrawingList = new ArrayList<PathDrawingInfo>(); //记录绘制的路径集合
    private List<PathDrawingInfo> mRemovedList = new ArrayList<PathDrawingInfo>(); //记录移除的路径集合
    
    //五个view依次是undo,redo,pen,erase,clear
    private View[] views = new View[5]; //关心底部五个子控件view的状态,是否可点击,enabled默认值为true,即可点击
    private boolean mCanEraser;  //记录是否可以使用擦除功能
    
    private float mDrawSize = 20;
    private float mEraserSize = 40;
    private static final int MAX_CACHE_STEP = 20;
    
    public enum Mode{
    	DRAW,
    	ERASE
    }
    public Mode mMode = Mode.DRAW;
    private Xfermode clearMode;
    
    private class PathDrawingInfo{
    	private Path cachePath;
    	private Paint cachePaint;
    }
    
	public MyPaletteView(Context context){
		this(context,null);
	}

	public MyPaletteView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initPaint();
	}
	
    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mDrawSize);
        mPaint.setColor(0XFF000000);
        clearMode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
    }
    
    public void setViewUIHandler(View[] views){
    	for(int i=0;i<views.length;i++){
    		this.views[i] = views[i];
    		if(i==0 || i == 1){ //redo undo view初始态不可点击
    			this.views[i].setEnabled(false);
    		}else{
    			this.views[i].setEnabled(true);
    		}
    	}
    }
    
    /** 撤销功能   **/
    public void undo(){
    	int drawSize = mDrawingList.size();
    	if(drawSize > 0){
    		PathDrawingInfo removePath = mDrawingList.remove(drawSize-1);
    		mRemovedList.add(removePath);
    		mBufferBitmap.eraseColor(Color.TRANSPARENT); //清除背景
    		int remainPathSize = mDrawingList.size();
    		if(remainPathSize > 0){
    			//还原显示剩余的路径
    			for(int i=0;i<remainPathSize;i++)
    			{
    				PathDrawingInfo drawInfo = mDrawingList.get(i);
    				mBufferCanvas.drawPath(drawInfo.cachePath, drawInfo.cachePaint);
    			}
    			this.views[0].setEnabled(true);
    			mCanEraser = true;
    		}else{
    			this.views[0].setEnabled(false);
    			mCanEraser = false;
    		}
    		if(mRemovedList.size() > 0){
    			this.views[1].setEnabled(true);
    		}else{
    			this.views[1].setEnabled(false);
    		}
    		invalidate();
    	}
    }
    
    /** 恢复功能   **/
    public void redo(){
    	int removeSize = mRemovedList.size();
    	if(removeSize > 0){
    		PathDrawingInfo removePath= mRemovedList.remove(removeSize-1); //获取最新的移除路径,准备恢复到原有的路径上
    		mDrawingList.add(removePath);
    		mBufferBitmap.eraseColor(Color.TRANSPARENT); //清除背景
    		int drawPathSize = mDrawingList.size();
    		if(drawPathSize > 0){
    			//恢复还原上次移除的路径
    			for(int i=0;i<drawPathSize;i++){
    				PathDrawingInfo drawInfo = mDrawingList.get(i);
    				mBufferCanvas.drawPath(drawInfo.cachePath, drawInfo.cachePaint);
    			}
    			this.views[0].setEnabled(true);
    		}else{
    			this.views[0].setEnabled(false);
    		}
    		if(mRemovedList.size() > 0){
    			this.views[1].setEnabled(true);
    		}else{
    			this.views[1].setEnabled(false);
    		}
    		mCanEraser = true;
    		invalidate();
    	}
    }
    
    /** 清除功能   **/
    public void clear() {
        if (mBufferBitmap != null) {
        	mDrawingList.clear();
        	mRemovedList.clear();
        	this.views[0].setEnabled(false);
        	this.views[1].setEnabled(false);
        	mCanEraser = false;
        	mBufferBitmap.eraseColor(Color.TRANSPARENT); //使用透明颜色填充画布mBufferBitmap,即擦除画布
        	invalidate();
        }
    }
    
    /** 设置当前模式,绘画模式和擦除模式  **/
    public void setMode(Mode mode){
    	if(mode != mMode){
    		mMode = mode;
    		if(mMode == Mode.DRAW){ //绘画模式
    			mPaint.setXfermode(null);
    			mPaint.setStrokeWidth(mDrawSize); 
    		}else if(mMode == Mode.ERASE){ //擦除模式 
    			mPaint.setXfermode(clearMode);
    			mPaint.setStrokeWidth(mEraserSize);
    		}
    	}
    }
    
    private void saveDrawingPath(){
    	if (mDrawingList == null) {
            mDrawingList = new ArrayList<PathDrawingInfo>(MAX_CACHE_STEP);
    	}else if (mDrawingList.size() == MAX_CACHE_STEP) {
            mDrawingList.remove(0);
        }
    	Path cachePath = new Path(mPath);
    	Paint cachePaint = new Paint(mPaint);
    	PathDrawingInfo drawInfo = new PathDrawingInfo();
    	drawInfo.cachePath = cachePath;
    	drawInfo.cachePaint = cachePaint;
    	mDrawingList.add(drawInfo);
    	this.views[0].setEnabled(true);
    	mCanEraser = true;
    }
    
    /**
     * 创建一块空画布mBufferBitmap,关联一个Canvas对象
     */
    private void CreateBufferBitmap(){
    	mBufferBitmap = Bitmap.createBitmap(getWidth(),getHeight(),Bitmap.Config.ARGB_8888);
    	mBufferCanvas = new Canvas(mBufferBitmap);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
    	if(mBufferBitmap != null){
    		//将绘制好的画布(mBufferBitmap)贴到对应的屏幕缓冲区中,展示到屏幕上
    		canvas.drawBitmap(mBufferBitmap, 0, 0, mPaint);
    	}
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	final int action = event.getAction() & MotionEvent.ACTION_MASK;
        final float x = event.getX();
        final float y = event.getY();
    	switch(action)
    	{
    	case MotionEvent.ACTION_DOWN:
    		mLastX = x;
            mLastY = y;
    		if(mPath == null){
    			mPath = new Path();
    		}
    		//moveTo,lineTo的坐标都是对于画布左上角(0,0)来说的,是一个绝对坐标
    		//rLineTo的坐标是相对坐标
    		mPath.moveTo(x,y); //起始绘图点定位到坐标(x,y)
    		break;
    	case MotionEvent.ACTION_MOVE:
    		//这里终点设为两点的中心点的目的在于使绘制的曲线更平滑,如果终点直接设置为x,y,效果和lineto是一样的,实际是折线效果
    		mPath.quadTo(mLastX, mLastY, (x + mLastX) / 2, (y + mLastY) / 2); //使用二阶贝赛尔曲线
    		if (mBufferBitmap == null) {
    			CreateBufferBitmap();
            }
    		if(mMode == Mode.ERASE && !mCanEraser){ //当前处于擦除模式,并且mCanEraser=false
    			break;
    		}
    		mBufferCanvas.drawPath(mPath,mPaint); //画家(mBufferCanvas)在画布(mBufferBitmap)上绘制一条路径
    		invalidate(); //刷新整个屏幕区域
    		mLastX = x;
    		mLastY = y; //重新记录上次手指所处的屏幕位置
    		break;
    	case MotionEvent.ACTION_UP:
    		if(mMode == Mode.DRAW || mCanEraser){
    			saveDrawingPath();
    		}
    		mPath.reset();
    		break;
    	}
    	return true;
    }
}
