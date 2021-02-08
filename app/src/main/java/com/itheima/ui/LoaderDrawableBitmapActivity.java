package com.itheima.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.itheima.smp.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Subject    关于Bitmap加载内存占用彻底分析
 * @Reference1 frameworks/base/core/jni/android/graphics/BitmapFactory.cpp文件的doDecode函数
 * @Variable1  变量density是当前加载的bitmap图片资源的目录所对应的dpi值,"drawable or drawable-mdpi"=160dpi; "drawable-xhdpi"=320dpi; "drawable-xxhdpi"=480dpi; "drawable-xxxhdpi"=640dpi;
 * @Variable2  变量targetDensity是当前输入"wm density"命令获取bitmap加载对应的屏幕dpi值,可以通过此命令进行更改dpi值,是Override density的数值,而不是Physical density的数值
 * @Variable3  变量scale=(float) targetDensity / density;
 * @Variable4  "wm density"结果输出的"Physical density"需要通过更换物理触摸屏改变,"Override density"可以看作逻辑上的dpi,
 *             当逻辑dpi增大一倍,则同样的5dp控件或文字,实际像素就会增大一倍,而物理dpi始终保持不变,则看到的控件或文字所占的尺寸会增大一倍
 * @URL        https://blog.csdn.net/axlchen/article/details/78230920
 * @Author     zhangming
 * @Date       2020-10-04 20:16
 */
public class LoaderDrawableBitmapActivity extends Activity implements AdapterView.OnItemClickListener {
    private ListView loader_menu_list;
    private ImageView iv_loader_bitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader_drawable_bitmap);

        loader_menu_list = (ListView) findViewById(R.id.loader_menu_list);
        SimpleAdapter adapter = new SimpleAdapter(this, initMenuData(),
                android.R.layout.simple_list_item_1, new String[]{"path"},
                new int[]{android.R.id.text1});
        loader_menu_list.setAdapter(adapter);
        loader_menu_list.setOnItemClickListener(this);

        iv_loader_bitmap = (ImageView) findViewById(R.id.iv_loader_bitmap);
    }

    private List<Map<String, String>> initMenuData() {
        List<Map<String, String>> datalists = new ArrayList<>();

        Map<String, String> map1 = new HashMap();
        map1.put("path", "从assets目录加载图片");

        Map<String, String> map2 = new HashMap();
        map2.put("path", "从drawable目录加载图片");

        Map<String, String> map3 = new HashMap();
        map3.put("path", "从drawable-mdpi目录加载图片");

        Map<String, String> map4 = new HashMap();
        map4.put("path", "从drawable-hdpi目录加载图片");

        Map<String, String> map5 = new HashMap();
        map5.put("path", "从drawable-xhdpi目录加载图片");

        Map<String, String> map6 = new HashMap();
        map6.put("path", "从drawable-xxhdpi目录加载图片");

        Map<String, String> map7 = new HashMap();
        map7.put("path", "从drawable-xxxhdpi目录加载图片");

        datalists.add(map1);
        datalists.add(map2);
        datalists.add(map3);
        datalists.add(map4);
        datalists.add(map5);
        datalists.add(map6);
        datalists.add(map7);

        return datalists;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                loadBitmap("asserts");
                break;
            case 1:
                loadBitmap("");
                break;
            case 2:
                loadBitmap("mdpi");
                break;
            case 3:
                loadBitmap("hdpi");
                break;
            case 4:
                loadBitmap("xhdpi");
                break;
            case 5:
                loadBitmap("xxhdpi");
                break;
            case 6:
                loadBitmap("xxxhdpi");
                break;
            default:
                break;
        }
    }

    private void loadBitmap(String type) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        if (type.equals("assets")) {
            try {
                InputStream inputStream = getAssets().open("model_assets.jpg");
                bitmap = BitmapFactory.decodeStream(inputStream, new Rect(), options);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (type.equals("")) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.model, options);
        } else if (type.equals("mdpi")) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.model_mdpi, options);
        } else if (type.equals("hdpi")) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.model_hdpi, options);
        } else if (type.equals("xhdpi")) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.model_xhdpi, options);
        } else if (type.equals("xxhdpi")) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.model_xxhdpi, options);
        } else if (type.equals("xxxhdpi")) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.model_xxxhdpi, options);
        }
        if (bitmap != null) {
            iv_loader_bitmap.setImageBitmap(bitmap);
        }
        printBitmapInfo(bitmap,options);
    }

    private void printBitmapInfo(Bitmap bitmap,BitmapFactory.Options options) {
        StringBuilder builder = new StringBuilder();
        builder.append("内存占用: " + bitmap.getByteCount() + " byte\n");
        builder.append("width: " + bitmap.getWidth() + "\n");
        builder.append("height: " + bitmap.getHeight() + "\n");
        builder.append("inPreferredConfig: " + options.inPreferredConfig.toString() + "\n");
        builder.append("inSampleSize: " + options.inSampleSize + "\n");
        builder.append("inDensity: " + options.inDensity + "\n");
        builder.append("inTargetDensity: " + options.inTargetDensity + "\n");
        builder.append("inScaled: " + options.inScaled + "\n");
        System.out.print(builder.toString());
    }

    private void bitmapFunc(ImageView imageView) {
        BitmapDrawable bd = (BitmapDrawable) imageView.getDrawable();
        if (bd != null) {
            /* Bitmap scale参考"frameworks/base/core/jni/android/graphics/BitmapFactory.cpp"文件的doDecode函数 */
            /* width = height = (300*280/160) = 525,density = 280 */
            Bitmap bitmap = bd.getBitmap();
            System.out.println("LoaderDrawableBitmapActivity width = "+bitmap.getWidth()+
                    " height = "+bitmap.getHeight()+" byteCounts = "+bitmap.getByteCount()+" density = "+bitmap.getDensity());
        }
    }

    /**
     * drawable-mdpi下的图片打印情况,可知图片被等比例放大拉伸,内存占用byteCounts=525*525*4=1102500(byte)
     * drawable-mdpi下的图片width = height = (300*280/160) = 525,密度inDensity = 160,inTargetDensity = 280,scale=(280/160)=1.75
     * 2020-10-04 19:48:50.203 2882-2882/com.itheima.smp I/BitmapFactory: doDecode density = 160,targetDensity = 280,screenDensity = 0,scale = 1.750000
     * 2020-10-04 19:48:50.206 2882-2882/com.itheima.smp D/skia: JPEG Decode 3
     * 2020-10-04 19:48:50.218 2882-2882/com.itheima.smp I/System.out: 内存占用: 1102500 byte
     * 2020-10-04 19:48:50.218 2882-2882/com.itheima.smp I/System.out: width: 525
     * 2020-10-04 19:48:50.219 2882-2882/com.itheima.smp I/System.out: height: 525
     * 2020-10-04 19:48:50.219 2882-2882/com.itheima.smp I/System.out: inPreferredConfig: ARGB_8888
     * 2020-10-04 19:48:50.219 2882-2882/com.itheima.smp I/System.out: inSampleSize: 0
     * 2020-10-04 19:48:50.219 2882-2882/com.itheima.smp I/System.out: inDensity: 160
     * 2020-10-04 19:48:50.219 2882-2882/com.itheima.smp I/System.out: inTargetDensity: 280 [cmd = "wm density" = 280]
     * 2020-10-04 19:48:50.219 2882-2882/com.itheima.smp I/System.out: inScaled: true
     * drawable,drawable-mdpi,drawable-hdpi目录下的图片将会等比例放大拉伸(密度inDensity<inTargetDensity,而inTargetDensity是通过命令"wm density"设置和获取)
     * drawable-xhdpi,drawable-xxhdpi,drawable-xxxhdpi目录下的图片将会等比例缩小(密度inDensity>inTargetDensity,而inTargetDensity是通过命令"wm density"设置和获取)
     */
}
