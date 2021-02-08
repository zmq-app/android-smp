package com.itheima.ui.opengl.view;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.itheima.ui.opengl.model.Lines;
import com.itheima.ui.opengl.model.Triangle;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

public class OneGlSurfaceView extends GLSurfaceView {
    public OneGlSurfaceView(Context context) {
        super(context);
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);
        // OpenGL ES抗锯齿
        setEGLConfigChooser(new MSAAConfigChooser());
        // 设置GLSurfaceView Renderer渲染
        setRenderer(new OneGlRenderer());
    }

    /* @region jide begin,add zmq for GLSurfaceView设置多重采样MSAAConfigChooser */
    static class MSAAConfigChooser implements EGLConfigChooser {
        @Override
        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
            //attribs是以EGL_NONE结束的参数数组,通常以id,value依次存放
            int attribs[] = {
                EGL10.EGL_LEVEL, 0,
                EGL10.EGL_RENDERABLE_TYPE, 4,
                EGL10.EGL_COLOR_BUFFER_TYPE, EGL10.EGL_RGB_BUFFER,
                EGL10.EGL_RED_SIZE, 8,
                EGL10.EGL_GREEN_SIZE, 8,
                EGL10.EGL_BLUE_SIZE, 8,
                EGL10.EGL_DEPTH_SIZE, 24,
                EGL10.EGL_SAMPLE_BUFFERS, 1,
                EGL10.EGL_SAMPLES, 4,
                EGL10.EGL_NONE
            };
            EGLConfig[] configs = new EGLConfig[1];
            int[] configCounts = new int[1];
            egl.eglChooseConfig(display, attribs, configs, 1, configCounts);

            if (configCounts[0] == 0) {
                return null;
            } else {
                return configs[0];
            }
        }
    }
    /* @region jide end,add zmq for GLSurfaceView设置多重采样MSAAConfigChooser */

    static class OneGlRenderer implements Renderer {
        private Triangle mTriangle;
        private List<float[]> mLineCoords = new ArrayList<>();

        private float[] mViewMatrix = new float[16];       //相机视图变换矩阵mViewMatrix
        private float[] mProjectionMatrix = new float[16]; //投影矩阵mProjectionMatrix
        private float[] mMVPMatrix = new float[16];        //mProjectionMatrix与mViewMatrix合成后的矩阵mMVPMatrix[尚未应用旋转]

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            Log.v("zhangming","onSurfaceCreated......");
            //设置当前帧的初始背景颜色
            GLES20.glClearColor(1.0f,0.0f,0.0f,1.0f);
            //设置GLSurfaceView开启多重采样
            GLES20.glEnable(GL10.GL_MULTISAMPLE);
            //设置绘制线的宽度
            gl.glLineWidth(3.0f);
            mTriangle = new Triangle();
            mLineCoords.add(new float[] {
                -0.8f,-0.8f,0.0f,
                -0.8f,0.8f,0.0f
            });
            mLineCoords.add(new float[] {
                -0.8f,0.8f,0.0f,
                0.8f,0.8f,0.0f
            });
            mLineCoords.add(new float[] {
                0.8f,0.8f,0.0f,
                0.8f,-0.8f,0.0f
            });
            mLineCoords.add(new float[] {
                0.8f,-0.8f,0.0f,
                -0.8f,-0.8f,0.0f
            });
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            Log.v("zhangming","onSurfaceChanged width = "+width+" height = "+height);
            //设置图像显示窗口和当前系统窗口一样大小,即图像随窗口的变化而变化
            GLES20.glViewport(0, 0, width, height);
            //按照Surface的长宽比例来计算投影矩阵mProjectionMatrix
            float ratio = (float) width / height;
            Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            //清除当前帧的颜色
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            //不断回调设置每帧的绘制颜色
            GLES20.glClearColor(0.0f,1.0f,0.0f,1.0f);
            // Set the camera position (View matrix)
            Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
            // Calculate the projection and view transformation
            Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
            //绘制一个三角形,传入合成后的矩阵mMVPMatrix
            mTriangle.drawTrangle(mMVPMatrix);
            //绘制外围矩形边框
            for (int i=0; i<mLineCoords.size(); i++) {
                Lines oneLine = new Lines(mLineCoords.get(i));
                oneLine.drawLines();
            }
        }
    }
}
