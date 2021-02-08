package com.itheima.ui.view;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.itheima.utils.CommonConstants;

/**
 * @Subject 自定义圆角Drawable
 * @Author  zhangming
 * @Date    2018-10-16
 */
public class CornerDrawable extends Drawable {
    private Paint mPaint;
    private Bitmap bmp;
    private RectF rectF;

    public CornerDrawable(Bitmap bmp) {
        this.bmp = bmp;
//      CLAMP  拉伸:如果需要填充的内容大小超过了bitmap size,就选bitmap边界的颜色进行扩展
//      REPEAT 重复:不断的重复bitmap去填满,如果绘制的区域大于纹理图片的话,纹理图片会在这片区域不断重复
//      MIRROR 镜像:如果绘制的区域大于纹理图片的话,纹理图片会以镜像的形式重复出现
//      BitmapShader使用特定的图片来作为纹理来使用,从画布的左上角开始绘制的
        BitmapShader shader = new BitmapShader(bmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setShader(shader);
    }

    @Override
    public void draw(Canvas canvas) {
        Log.i(CommonConstants.TAG,"CornerDrawable draw function...");
        //TODO 对比分析 默认在调用draw方法之前就会执行this.setBounds(bmp.getWidth(),bmp.getHeight()); this.setBounds(206,206);
        //TODO 以下两处调用均是手动调用,将会覆盖之前设置好的left,right,top,bottom参数
        //this.setBounds(30,30,236,236);
        //this.setBounds(0,0,206,206);

        Rect rect = getBounds();
        Log.i(CommonConstants.TAG, rect.left + ":" + rect.width() + ":" + rect.height());
        Log.i(CommonConstants.TAG, rectF.left + ":" + rectF.width() + ":" + rectF.height()); //[rectF == rect]

        canvas.drawRoundRect(rectF, 25, 25, mPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    // getIntrinsicWidth、getIntrinsicHeight主要是为了在View使用wrap_content的时候,提供一下尺寸
    @Override
    public int getIntrinsicHeight() {
        Log.i(CommonConstants.TAG,"Bitmap Height = "+bmp.getHeight());
        return bmp.getHeight();
    }

    @Override
    public int getIntrinsicWidth() {
        Log.i(CommonConstants.TAG,"Bitmap Width = "+bmp.getWidth());
        return bmp.getWidth();
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        Log.i(CommonConstants.TAG,"CornerDrawable setBounds,left = "+left+" top = "+top+" right = "+right+" bottom = "+bottom);
        rectF = new RectF(left, top, right, bottom);
        super.setBounds(left, top, right, bottom);
    }
}
