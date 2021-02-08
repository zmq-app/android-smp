package com.itheima.ui.opengl.view;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.SurfaceHolder;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteTextures;

/**
 * @Subject 利用OpenGL生成纹理并绑定到SurfaceTexture上,然后用Camera的预览数据设置到SurfaceTexture中,OpenGL拿到摄像头数据并显示出来
 * @Author  zhangming
 * @Date    2019-10-19 18:32
 */
public class ThreeGlSurfaceView extends GLSurfaceView implements SurfaceTexture.OnFrameAvailableListener{
    private CameraRenderer mRenderer;
    public static final int cameraSurfaceView_width = 800;
    public static final int cameraSurfaceView_height = 600;

    public ThreeGlSurfaceView(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        //Camera管理类
        final GLCamera mGLCamera = new GLCamera();
        mRenderer = new ThreeGlSurfaceView.CameraRenderer();
        mRenderer.setOnSurfaceCreateListener(new CameraRenderer.OnSurfaceCreateListener() {
            @Override
            public void onSurfaceCreate(SurfaceTexture surfaceTexture) {
                mGLCamera.initCamera(surfaceTexture);
                //注册当一个新的图像帧可用于SurfaceTexture时要调用的回调
                surfaceTexture.setOnFrameAvailableListener(ThreeGlSurfaceView.this);
            }
        });
        //设置渲染器
        setRenderer(mRenderer);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    static class GLCamera {
        private SurfaceTexture mSurfaceTexture;
        private int width;
        private int height;

        public GLCamera() {
            this.width = cameraSurfaceView_width;
            this.height = cameraSurfaceView_height;
        }

        public void initCamera(SurfaceTexture surfaceTexture) {
            this.mSurfaceTexture = surfaceTexture;
            setCameraParm();
        }

        private void setCameraParm() {
            try {
                Camera mCamera = Camera.open();
                //将SurfaceTexture设置为预览surface
                mCamera.setPreviewTexture(mSurfaceTexture);

                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setFlashMode("off");
                parameters.setPreviewFormat(ImageFormat.NV21);

                Camera.Size size = getFitSize(parameters.getSupportedPictureSizes());
                parameters.setPictureSize(size.width, size.height);

                size = getFitSize(parameters.getSupportedPreviewSizes());
                parameters.setPreviewSize(size.width, size.height);

                mCamera.setParameters(parameters);
                //开始预览
                mCamera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private Camera.Size getFitSize(List<Camera.Size> sizes) {
            if(width < height) {
                int t = height;
                height = width;
                width = t;
            }
            for(Camera.Size size : sizes) {
                if(1.0f * size.width / size.height == 1.0f * width / height) {
                    return size;
                }
            }
            return sizes.get(0);
        }
    }

    static class CameraRenderer implements Renderer {
        private static final String vertexShaderCode =
            "attribute vec4 vertex_position;\n" +
            "attribute vec2 fragment_position;\n" +
            "varying vec2 a_position;\n" +
            "void main(){\n"+
                "gl_Position = vertex_position;\n"+
                "a_position = fragment_position;\n"+
            "}";

        private static final String fragmentShaderCode =
            "#extension GL_OES_EGL_image_external:require \n" +
            "precision mediump float;\n" +
            "varying vec2 a_position;\n" +
            "uniform samplerExternalOES vTexture;\n" +
            "void main(){\n" +
                "gl_FragColor = texture2D(vTexture,a_position);\n" +
            "}";

        private int mProgram;
        private int mVertexHandle;
        private int mFragmentHandle;

        private int mTextureId;
        private SurfaceTexture mSurfaceTexture;

        private FloatBuffer mVertexBuffer;
        private FloatBuffer mFragmentBuffer;

        private static final float[] vertexPosition = {
            -1.0f, 1.0f,   //左上角
            -1.0f,-1.0f,   //左下角
            1.0f,1.0f,     //右上角
            1.0f,-1.0f     //右下角
        };
        private static final float[] texturePosition = {
            0.0f, 0.0f,     //左上角
            0.0f, 1.0f,     //左下角
            1.0f, 0.0f,     //右上角
            1.0f, 1.0f      //右下角
        };

        public CameraRenderer() {
            ByteBuffer vertexBuffer = ByteBuffer.allocateDirect(vertexPosition.length * 4);
            vertexBuffer.order(ByteOrder.nativeOrder());
            mVertexBuffer = vertexBuffer.asFloatBuffer();
            mVertexBuffer.put(vertexPosition);
            mVertexBuffer.position(0);

            ByteBuffer fragmentBuffer = ByteBuffer.allocateDirect(texturePosition.length * 4);
            fragmentBuffer.order(ByteOrder.nativeOrder());
            mFragmentBuffer = fragmentBuffer.asFloatBuffer();
            mFragmentBuffer.put(texturePosition);
            mFragmentBuffer.position(0);
        }

        /* @region jide begin,add zmq for 回调接口OnSurfaceCreateListener定义 */
        private OnSurfaceCreateListener mOnSurfaceCreateListener;
        public interface OnSurfaceCreateListener {
            void onSurfaceCreate(SurfaceTexture surfaceTexture);
        }
        public void setOnSurfaceCreateListener(OnSurfaceCreateListener listener) {
            mOnSurfaceCreateListener = listener;
        }
        /* @region jide end,add zmq for 回调接口OnSurfaceCreateListener定义 */

        /**
         * @function 生成OpenGL Program
         * @param   vertexSource   顶点着色器代码
         * @param   fragmentSource 片元着色器代码
         * @return  生成OpenGL Program,如果为0,则表示创建失败
         */
        public int createOpenGLProgram(String vertexSource, String fragmentSource) {
            int vertex = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
            if (vertex == 0) {
                return 0;
            }
            int fragment = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
            if (fragment == 0) {
                return 0;
            }
            int program = GLES20.glCreateProgram();
            if (program != 0) {
                GLES20.glAttachShader(program, vertex);
                GLES20.glAttachShader(program, fragment);
                GLES20.glLinkProgram(program);
                int[] linkStatus = new int[1];
                GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
                if (linkStatus[0] != GLES20.GL_TRUE) {
                    GLES20.glDeleteProgram(program);
                    program = 0;
                }
            }
            return program;
        }

        /**
         * @function 加载着色器
         * @param type       加载着色器类型
         * @param shaderCode 加载着色器的代码
         */
        public int loadShader(int type, String shaderCode) {
            //根据type创建顶点着色器或者片元着色器
            int shader = GLES20.glCreateShader(type);
            //将着色器的代码加入到着色器中
            GLES20.glShaderSource(shader, shaderCode);
            //编译着色器
            GLES20.glCompileShader(shader);
            return shader;
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
            GLES20.glEnable(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);

            mProgram = createOpenGLProgram(vertexShaderCode,fragmentShaderCode);

            //获取顶点着色器和片元着色器的位置句柄
            mVertexHandle = GLES20.glGetAttribLocation(mProgram, "vertex_position");
            mFragmentHandle = GLES20.glGetAttribLocation(mProgram, "fragment_position");

            //生成纹理ID并执行绑定操作
            int[] textures = new int[1];
            GLES20.glGenTextures(1, textures, 0);
            mTextureId = textures[0];
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureId);

            //创建Camera预览的SurfaceTexture,并将之前生成的mTextureId(纹理ID)作为参数传入其构造方法中
            mSurfaceTexture = new SurfaceTexture(mTextureId);
            if (mOnSurfaceCreateListener != null){
                mOnSurfaceCreateListener.onSurfaceCreate(mSurfaceTexture);
            }

            //执行纹理环绕和过滤操作
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

            //解绑纹理
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            //纹理图像更新为图像流中的最新帧[注:必须先调用]
            mSurfaceTexture.updateTexImage();
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            //启动OpenGL程序
            GLES20.glUseProgram(mProgram);
            //使能顶点和纹理数组集
            GLES20.glEnableVertexAttribArray(mVertexHandle);
            GLES20.glEnableVertexAttribArray(mFragmentHandle);
            //向着色器中传入顶点坐标和纹理坐标
            GLES20.glVertexAttribPointer(mVertexHandle, 2, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
            GLES20.glVertexAttribPointer(mFragmentHandle, 2, GLES20.GL_FLOAT, false, 0, mFragmentBuffer);
            //绘制图像
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
            GLES20.glDisableVertexAttribArray(0);
        }

        public void finishOpenGLES20() {
            int[] textures = new int[1];
            textures[0] = mTextureId;
            glDeleteTextures(1, textures, 0);
            glDeleteProgram(mProgram);
        }
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        //开启下一帧的渲染绘制过程
        requestRender();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        //销毁必要的OpenGL ES2.0状态机资源
        mRenderer.finishOpenGLES20();
    }
}
