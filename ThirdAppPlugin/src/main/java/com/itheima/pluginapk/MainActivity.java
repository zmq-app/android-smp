package com.itheima.pluginapk;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * @Subject 插件APP的主入口Activity
 * @Author  zhangming
 * @Date    2019-11-07 20:45
 */
public class MainActivity extends BaseActivity {
    private ImageView imageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.img_launcher);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mHostActivity,mHostActivity.getString(R.string.click_img_toast),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
