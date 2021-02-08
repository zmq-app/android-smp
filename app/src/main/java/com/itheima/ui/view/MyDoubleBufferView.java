package com.itheima.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * 使用双缓冲绘图案例 每个Canvas内部持有一个Bitmap对象引用,画图的过程其实就是往这个Bitmap当中写入ARGB信息
 * 所谓的双缓冲,在android绘图中其实就是再创建一个Canvas和对应的Bitmap,然后在onDraw方法里
 * 默认的Canvas通过drawBitmap画刚才new的那个bitmap即可
 * @author zhangming
 * @date 2017/09/25
 */
public class MyDoubleBufferView extends View {
	private Paint mPaint;
	private Canvas mBufferCanvas;
	private Bitmap mBufferBitmap;
	private Rect allRect, topRect, bottomRect;

	public MyDoubleBufferView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint();
		mPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setColor(Color.GREEN);
		setBackgroundColor(Color.WHITE);
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (mBufferBitmap == null) {
				// 创建一个新的Bitmap对象,并创建一个新的Canvas对象与之关联
				mBufferBitmap = Bitmap.createBitmap(getWidth(),getHeight(),Config.ARGB_8888);
				mBufferCanvas = new Canvas(mBufferBitmap);
			}
			mBufferCanvas.drawCircle((int)event.getX(), (int)event.getY(),50,mPaint);
			break;
		case MotionEvent.ACTION_UP:
			// 绘制一个矩形区域
			mBufferCanvas.drawRect(new Rect(50, 50, 150, 150), mPaint);
			allRect = new Rect(0, 0, getWidth(), getHeight());
			topRect = new Rect(0, 0, getWidth() / 2, getHeight() / 2);
			bottomRect = new Rect(0, getHeight() / 2, getWidth(), getHeight());
			// 在UI主线程中用invalidate函数,本质是调用View的onDraw函数绘制
			// 主线程之外,则用postInvalidate
			// 定义刷新部分的矩形区域为dstRect
			// (1)联合代码canvas.drawBitmap(mBufferBitmap,0,0,mPaint)
			// 看到的效果是:仅仅点击屏幕下半区域才能看到绘制的图形
			// (2)联合代码canvas.drawBitmap(mBufferBitmap,srcRect,dstRect,mPaint);
			// 看到的效果是:仅点击屏幕左上1/4部分才能在下半区域显示绘制的图形,在屏幕其余区域点击不会显示
			// (3)绘制的圆形呈现的是椭圆,映射目标区域高度缩小一倍导致的
			//Rect refreshRect = allRect;  //设置刷新整个矩形区域,即整个屏幕部分
			Rect refreshRect = bottomRect; //设置只刷新目标区域,即屏幕下半部分
			setClipBounds(refreshRect);
			invalidate(refreshRect);
			break;
		}
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Log.i("zhangming", "onDraw method call...");
		if (mBufferBitmap == null) {
			return;
		}
		// 将内存中创建好的一张Bitmap图片贴到对应的Canvas画整个区域上
		canvas.drawBitmap(mBufferBitmap, 0, 0, mPaint);
		/*
		 * drawBitmap这个方法可以用来剪辑一张图片的一部分，即当我们把一组图片做成一张时， 我们可以用此方法来剪辑出单个图片。
		 * bitmap的默认坐标是0,0.我们可以在此基础上剪图片。 矩形src为我们所剪辑的图片的包围框，即你所剪的图片，如果为空，就是整张图片。
		 * 矩形dst容纳你所剪的图片，然后根据此矩形的位置设置图片的位置。此参数不能为空。 当你剪的图片大小大于dst时，多余的部分将不会显示。
		 * 也就是说src是裁减区，对原始图的裁减区域，而dst是代表图片显示位置. 即:缩放,区域映射,裁剪
		 */
		// canvas.drawBitmap(mBufferBitmap, srcRect, dstRect, mPaint);
	}
}
