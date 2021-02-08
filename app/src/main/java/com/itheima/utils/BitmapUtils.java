package com.itheima.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class BitmapUtils {
    private static final String TAG = BitmapUtils.class.getSimpleName();
    private static volatile BitmapUtils bmInstance;

    /**
     * 双重检查锁定单例模式
     * @return bmInstance
     */
    public static BitmapUtils getInstance(){
        if(bmInstance == null){
            synchronized (BitmapUtils.class) {
                if(bmInstance == null) {
                    bmInstance = new BitmapUtils();
                }
            }
        }
        return bmInstance;
    }

    /**
     * 质量压缩
     * 不会减少图片的像素,它是在保持像素的前提下改变图片的位深及透明度,来达到压缩图片的目的,图片的长,宽,像素都不会改变. 那么bitmap所占内存大小是不会变的
     * quality参数可以调节你压缩的比例. 但是还要注意一点就是,质量压缩对png格式这种图片没有作用,因为png图片格式是无损压缩
     * @param bitmap  位图
     * @param quality 质量率(比如:quality=80; 表示压缩20%)
     */
    public Bitmap compressQuality(Bitmap bitmap,int quality) {
        String mSrcSize = bitmap.getByteCount() + "byte";

        //通过内存字节数组生成压缩后的Bitmap
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
        byte[] bytes = bos.toByteArray();
        Bitmap mCompressBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        String mCompSize = mCompressBitmap.getByteCount() + "byte";
        Log.i(TAG,"mSrcSize = "+mSrcSize+" mCompressSize = "+mCompSize);

        return mCompressBitmap;
    }

    /**
     * 采样率压缩
     * @param bitmap
     * @param sample
     */
    public Bitmap compressSampling(Bitmap bitmap,int sample) {
        String mSrcSize = bitmap.getByteCount() + "byte";

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        InputStream in = new ByteArrayInputStream(bos.toByteArray());

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sample;
        Bitmap mCompressBitmap = BitmapFactory.decodeStream(in,null,options);

        String mCompSize = mCompressBitmap.getByteCount() + "byte";
        Log.i(TAG,"mSrcSize = "+mSrcSize+" mCompressSize = "+mCompSize);

        return mCompressBitmap;
    }


    /**
     * 矩阵缩放法压缩
     * @param bitmap
     * @param widthScale   小于1.0f,缩小矩阵宽
     * @param heightScale  小于1.0f,缩小矩阵高
     */
    public Bitmap compressMatrix(Bitmap bitmap,int widthScale,int heightScale) {
        String mSrcSize = bitmap.getByteCount() + "byte";

        Matrix matrix = new Matrix();
        matrix.setScale(widthScale, heightScale);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        InputStream in = new ByteArrayInputStream(bos.toByteArray());

        Bitmap bm = BitmapFactory.decodeStream(in);
        Bitmap mCompressBitmap = Bitmap.createBitmap(bm,0,0,bm.getWidth(),bm.getHeight(),matrix,true);

        String mCompSize = mCompressBitmap.getByteCount() + "byte";
        Log.i(TAG,"mSrcSize = "+mSrcSize+" mCompressSize = "+mCompSize);

        return mCompressBitmap;
    }

    public Bitmap getRoundRectBitmap(Bitmap srcBitmap,int radiusRect){
        //准备画笔
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        //准备裁剪的矩形
        Rect rect = new Rect(0,0,srcBitmap.getWidth(),srcBitmap.getHeight());
        RectF rectF = new RectF(rect);

        //准备一个空位图
        Bitmap roundBitmap = Bitmap.createBitmap(srcBitmap.getWidth(),srcBitmap.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(roundBitmap);

        //圆角矩阵,radiusRect为圆角大小
        canvas.drawRoundRect(rectF,radiusRect,radiusRect,paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(srcBitmap,rect,rect,paint);

        return roundBitmap;
    }

    /**
     * Bitmap自身旋转变换
     * @param bitmap  原图
     * @param degress 旋转角度,可正可负
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmap(Bitmap bitmap,float degress) {
        if (bitmap != null) {
            Matrix m = new Matrix();
            m.postRotate(degress);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m,true);
            return bitmap;
        }
        return bitmap;
    }
}
