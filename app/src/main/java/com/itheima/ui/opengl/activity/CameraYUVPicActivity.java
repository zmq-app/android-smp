package com.itheima.ui.opengl.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;

import com.itheima.smp.R;
import com.itheima.ui.opengl.view.ThreeGlSurfaceView;
import com.itheima.utils.permission.PermissionResultCallBack;
import com.itheima.utils.permission.PermissionUtil;

public class CameraYUVPicActivity extends Activity {
    private PermissionResultCallBack permissionResultCallBack;
    private static String[] ALL_PERMISSIONS = {
            Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* @region jide begin,add zmq for Camera Permission need to dynamic getting at Android6.0 */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions();
        }
        /* @region jide end,add zmq for Camera Permission need to dynamic getting at Android6.0 */

        ThreeGlSurfaceView leftGLSurfaceView = new ThreeGlSurfaceView(this);
        ThreeGlSurfaceView rightGLSurfaceView = new ThreeGlSurfaceView(this);
        LinearLayout cameraLayout = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            leftGLSurfaceView.cameraSurfaceView_width,
            leftGLSurfaceView.cameraSurfaceView_height);
        params.leftMargin = 200;
        leftGLSurfaceView.setLayoutParams(params);
        rightGLSurfaceView.setLayoutParams(params);
        cameraLayout.setOrientation(LinearLayout.HORIZONTAL);
        cameraLayout.setGravity(Gravity.CENTER_VERTICAL);
        cameraLayout.addView(leftGLSurfaceView);
        cameraLayout.addView(rightGLSurfaceView);
        setContentView(cameraLayout);
    }

    private void requestPermissions() {
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
                AlertDialog dialog = new AlertDialog.Builder(CameraYUVPicActivity.this)
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
                AlertDialog dialog = new AlertDialog.Builder(CameraYUVPicActivity.this)
                        .setMessage(getResources().getString(R.string.perm_grant_hite))
                        .setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PermissionUtil.getInstance().request(CameraYUVPicActivity.this, ALL_PERMISSIONS, permissionResultCallBack);
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
        PermissionUtil.getInstance().request(this, ALL_PERMISSIONS, permissionResultCallBack);
    }
}
