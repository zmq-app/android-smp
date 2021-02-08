package com.itheima.ui.opengl.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.itheima.smp.R;
import com.itheima.utils.permission.PermissionResultCallBack;
import com.itheima.utils.permission.PermissionUtil;

/**
 * @Subject SurfaceView,TextureView,GLSurfaceView显示相机预览
 * @URL     https://www.jianshu.com/p/92d02ac80611
 * @Author  zhangming
 * @Date    2019-07-12 21:16
 */
public class CameraSurfaceViewShowActivity extends Activity implements SurfaceHolder.Callback{
    private SurfaceView mSurfaceView;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Button rotateChangeBtn;

    private PermissionResultCallBack permissionResultCallBack;
    private static String[] ALL_PERMISSIONS = {
        Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_surfaceview_activity);

        //@region jide begin,add zmq for Camera Permission need to dynamic getting at Android6.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            initPermissions();
        }
        //@region jide end,add zmq for Camera Permission need to dynamic getting at Android6.0

        mSurfaceView = (SurfaceView) findViewById(R.id.mSurface);
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        rotateChangeBtn = (Button) findViewById(R.id.btn_rotation_change);
        rotateChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeSurfaceAnimation();
            }
        });
    }

    private void initPermissions() {
        permissionResultCallBack = new PermissionResultCallBack() {
            @Override
            public void onPermissionGranted() {
                Log.v("zhangming","onPermissionGranted....");
            }

            @Override
            public void onPermissionGranted(String... permissions) {
                Log.v("zhangming","onPermissionGranted permissions....");
            }

            @Override
            public void onPermissionDenied(String... permissions) {
                AlertDialog dialog = new AlertDialog.Builder(CameraSurfaceViewShowActivity.this)
                        .setMessage(getResources().getString(R.string.perm_grant_hite))
                        .setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivityForResult(intent,2);
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).create();
                dialog.show();
            }

            @Override
            public void onRationalShow(String... permissions) {
                AlertDialog dialog = new AlertDialog.Builder(CameraSurfaceViewShowActivity.this)
                        .setMessage(getResources().getString(R.string.perm_grant_hite))
                        .setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PermissionUtil.getInstance().request(CameraSurfaceViewShowActivity.this, ALL_PERMISSIONS, permissionResultCallBack);
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).create();
                dialog.show();
            }
        };
        PermissionUtil.getInstance().request(CameraSurfaceViewShowActivity.this, ALL_PERMISSIONS, permissionResultCallBack);
    }

    //@region jide begin,add zmq for execute Property Animation
    private void executeSurfaceAnimation() {
        PropertyValuesHolder rotationY = PropertyValuesHolder.ofFloat("rotationY", 0.0f, 360.0f, 0.0f);
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 0.5f,1.0f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 1.0f, 0.5f,1.0f);

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(mSurfaceView, rotationY, scaleX, scaleY);
        animator.setDuration(5000).start();
    }
    //@region jide end,add zmq for execute Property Animation

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            // Open the Camera in preview mode
            mCamera = Camera.open();
            mCamera.setDisplayOrientation(90);
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
            // Java Lamba表达式
            mCamera.setPreviewCallback((byte[] data, Camera camera) -> {
                Log.i("zhangming","camera rotation 90 degree bitmap data...");
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.v("zhangming","surfaceChanged width = "+width+" height = "+height+" format = "+format);
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (success) {
                    Camera.Parameters mParameters = mCamera.getParameters();
                    mParameters.setPictureFormat(PixelFormat.JPEG);  //设置图片输出格式,默认格式为PixelFormat.RGB_565 = 4
//                    mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH); //预览持续发光
                    mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE); //持续对焦模式
                    mCamera.setParameters(mParameters);
                    mCamera.startPreview();
                    mCamera.cancelAutoFocus();
                }
            }
        });
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }
}
