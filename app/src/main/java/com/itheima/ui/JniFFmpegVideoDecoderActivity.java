package com.itheima.ui;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.itheima.smp.R;

/**
 * @Subject 使用ffmpeg实现视频播放
 * @URL1    https://www.jianshu.com/p/c7de148e951c
 * @URL2    https://github.com/2016lc/FFmpegDemo [GitHub源码地址]
 * @Author  zhangming
 * @Date    2020-07-02 17:35
 */
public class JniFFmpegVideoDecoderActivity extends Activity {
    private SurfaceView surfaceView;
    private Button btnPlay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ffmpeg_video_decoder);

        surfaceView = (SurfaceView) findViewById(R.id.ffmpeg_video_surfaceview);

        btnPlay = (Button) findViewById(R.id.btn_play);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                surfaceView.getHolder().setFormat(PixelFormat.RGBA_8888);
                Surface mSurface = surfaceView.getHolder().getSurface();
                if (ffmpeg_video_render("/sdcard/ffmpeg_movie.mp4", mSurface) < 0) {
                    Log.e("zhangming","JniFFmpegVideoDecoderActivity video render is failed...");
                }
            }
        });
    }

    /** 加载本地共享库libjni_ffmpeg_video.so,后者会回调 **/
    static {
        System.loadLibrary("jni_ffmpeg_video");
    }

    public native int ffmpeg_video_render(String inputVideoPath,Object surface);
}
