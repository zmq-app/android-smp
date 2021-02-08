package com.itheima.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.itheima.smp.R;
import com.itheima.utils.BitmapUtils;
import com.itheima.utils.DESUtils;

public class QRActivity extends Activity {
    private static final String TAG = QRActivity.class.getSimpleName();
    private ImageView imageView;

    private ImageView mPwdEyeImage;
    private EditText mPwdEditText;
    boolean isEyeOpen = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_activity);
        initView();
        createQR();
        onClicked();
    }

    /** 当layout资源文件被inflate加载到DecorView的mContentParent中,会回调此方法,并结束setContentView过程 **/
    @Override
    public void onContentChanged() {
        super.onContentChanged();
    }

    private void initView(){
        imageView = (ImageView) findViewById(R.id.codeImageView);
        mPwdEditText = (EditText) findViewById(R.id.passwd_edit);
        mPwdEyeImage = (ImageView) findViewById(R.id.pwd_eye_image);

        mPwdEyeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEyeOpen){
                    //密码 TYPE_CLASS_TEXT 和 TYPE_TEXT_VARIATION_PASSWORD 必须一起使用
                    mPwdEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mPwdEditText.setSelection(mPwdEditText.getText().length());
                    mPwdEyeImage.setImageResource(R.drawable.clear);
                    isEyeOpen = false;
                }else {
                    //明文
                    mPwdEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    mPwdEditText.setSelection(mPwdEditText.getText().length());
                    mPwdEyeImage.setImageResource(R.drawable.pen);
                    isEyeOpen = true;
                }
            }
        });
    }

    private void createQR() {
        String data = "Hello World!!!";
        String encodeStr = DESUtils.encryptPassword(data);
        Log.i(TAG,"encodeStr = "+encodeStr);

        try {
            //生成二维矩阵,编码时指定大小
            BitMatrix matrix = new MultiFormatWriter().encode(encodeStr, BarcodeFormat.QR_CODE, 450, 450);
            int width = matrix.getWidth();
            int height = matrix.getHeight();
            //二维矩阵转为一维像素数组
            int[] pixels = new int[width * height];
            //为此一维像素数组添加颜色(黑 or 白)
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (matrix.get(x, y)) {
                        pixels[y * width + x] = Color.BLACK;
                    } else {
                        pixels[y * width + x] = Color.WHITE;
                    }
                }
            }
            //创建一个空的bitmap,并通过上面的一维像素数组来填充
            Bitmap bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.RGB_565);
            bitmap.setPixels(pixels,0,width,0,0,width,height);

            //显示压缩后的bitmap
            Bitmap compressBitmap = BitmapUtils.getInstance().compressQuality(bitmap,80);
            Bitmap roundBitmap = BitmapUtils.getInstance().getRoundRectBitmap(compressBitmap,8);
            imageView.setImageBitmap(roundBitmap);
        }catch (WriterException e){
            e.printStackTrace();
        }
    }

    private void onClicked() {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
