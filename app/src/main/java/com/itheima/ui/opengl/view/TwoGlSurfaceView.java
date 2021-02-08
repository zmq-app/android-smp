package com.itheima.ui.opengl.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.view.SurfaceHolder;

import com.itheima.smp.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @Subject OpenGL纹理贴图组合案例
 * @Author  zhangming
 * @Date    2019-10-13 16:17
 */
public class TwoGlSurfaceView extends GLSurfaceView {
    private ImageRenderer renderer;
    public TwoGlSurfaceView(Context context) {
        super(context);
        setEGLContextClientVersion(2);

        renderer = new ImageRenderer(context);
        setRenderer(renderer);
    }

    static class ImageRenderer implements Renderer {
        //顶点着色器的内建变量gl_Position,通过矩阵vMatrix变换后计算新的顶点位置坐标,并赋值给gl_Position输入变量,用于Shader渲染
        private static final String vertexMatrixLeftTopShaderCode =
                "attribute vec4 vLeftTopPosition;\n" +
                        "attribute vec2 vCoordinate;\n" +
                        "uniform mat4 vMatrix;\n" +
                        "varying vec2 aCoordinate;\n" +
                        "void main(){\n" +
                        "    gl_Position=vMatrix*vLeftTopPosition;\n" +
                        "    aCoordinate=vCoordinate;\n" +
                        "}";

        private static final String vertexMatrixLeftBottomShaderCode =
                 "attribute vec4 vLeftBottomPosition;\n" +
                        "attribute vec2 vCoordinate;\n" +
                        "uniform mat4 vMatrix;\n" +
                        "varying vec2 aCoordinate;\n" +
                        "void main(){\n" +
                        "    gl_Position=vMatrix*vLeftBottomPosition;\n" +
                        "    aCoordinate=vCoordinate;\n" +
                        "}";

        private static final String vertexMatrixRightTopShaderCode =
                "attribute vec4 vRightTopPosition;\n" +
                        "attribute vec2 vCoordinate;\n" +
                        "uniform mat4 vMatrix;\n" +
                        "varying vec2 aCoordinate;\n" +
                        "void main(){\n" +
                        "    gl_Position=vMatrix*vRightTopPosition;\n" +
                        "    aCoordinate=vCoordinate;\n" +
                        "}";

        private static final String vertexMatrixRightBottomShaderCode =
                "attribute vec4 vRightBottomPosition;\n" +
                        "attribute vec2 vCoordinate;\n" +
                        "uniform mat4 vMatrix;\n" +
                        "varying vec2 aCoordinate;\n" +
                        "void main(){\n" +
                        "    gl_Position=vMatrix*vRightBottomPosition;\n" +
                        "    aCoordinate=vCoordinate;\n" +
                        "}";

        //sampler2D是GLSL变量类型之一的取样器
        //texture2D是GLSL的内置函数,用于2D纹理取样,根据纹理取样器和纹理坐标,可以获取当前纹理取样得到的像素颜色
        private static final String fragmentShaderCode =
                "precision mediump float;\n" +
                        "uniform sampler2D vTexture;\n" +
                        "varying vec2 aCoordinate;\n" +
                        "void main(){\n" +
                        "    gl_FragColor=texture2D(vTexture,aCoordinate);\n" +
                        "}";

        private static final float[][] tPositions = new float[][] {
            {
                //左上图片
                -1.0f, 1.0f,    //左上角
                -1.0f, 0.0f,    //左下角
                0.0f, 1.0f,     //右上角
                0.0f, 0.0f      //右下角
            },
            {
                //左下图片
                -1.0f, 0.0f,    //左上角
                -1.0f, -1.0f,   //左下角
                0.0f, 0.0f,     //右上角
                0.0f, -1.0f     //右下角
            },
            {
                //右上图片
                0.0f, 1.0f,     //左上角
                0.0f, 0.0f,     //左下角
                1.0f, 1.0f,     //右上角
                1.0f, 0.0f      //右下角
            },
            {
                //右下图片
                0.0f, 0.0f,     //左上角
                0.0f, -1.0f,    //左下角
                1.0f, 0.0f,     //右上角
                1.0f, -1.0f     //右下角
            }
        };

        private static final float[] sCoord = {
                0.0f, 0.0f,     //左上角
                0.0f, 1.0f,     //左下角
                1.0f, 0.0f,     //右上角
                1.0f, 1.0f      //右下角
        };

        private Context mContext;
        private int[] mPrograms = new int[4];
        private Bitmap mBitmap;

        private int glHTexture;
        private int glHCoordinate;
        private int glHMatrix;
        private int textureId = -1;

        private FloatBuffer[] bPositions = new FloatBuffer[4];
        private FloatBuffer bCoord;

        private float[] mViewMatrix = new float[16];       //相机视图变换矩阵mViewMatrix
        private float[] mProjectionMatrix = new float[16]; //投影矩阵mProjectionMatrix
        private float[] mMVPMatrix = new float[16];        //mProjectionMatrix与mViewMatrix合成后的矩阵mMVPMatrix[尚未应用旋转]
        private float[] mRotationMatrix = new float[16];   //旋转矩阵mRotationMatrix

        private final int BITMAP_LEFT_TOP_POSITION = 0;
        private final int BITMAP_LEFT_BOTTOM_POSITION = 1;
        private final int BITMAP_RIGHT_TOP_POSITION = 2;
        private final int BITMAP_RIGHT_BOTTOM_POSITION = 3;

        public ImageRenderer(Context context) {
            mContext = context;
            mBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.wallpaper_01);
            //虚拟机需要分配本地内存[allocateDirect]
            for (int i=0; i<bPositions.length; i++) {
                ByteBuffer bb = ByteBuffer.allocateDirect(tPositions[i].length * 4);
                bb.order(ByteOrder.nativeOrder());
                bPositions[i] = bb.asFloatBuffer();
                bPositions[i].put(tPositions[i]);
                bPositions[i].position(0);
            }
            ByteBuffer cc = ByteBuffer.allocateDirect(sCoord.length * 4);
            cc.order(ByteOrder.nativeOrder());
            bCoord = cc.asFloatBuffer();
            bCoord.put(sCoord);
            bCoord.position(0);
        }

        /**
         * 加载着色器
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

        /**
         * 生成OpenGL Program
         * @param vertexSource   顶点着色器代码
         * @param fragmentSource 片元着色器代码
         * @return 生成的OpenGL Program，如果为0，则表示创建失败
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

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
            GLES20.glEnable(GLES20.GL_TEXTURE_2D);

            mPrograms[0] = createOpenGLProgram(vertexMatrixLeftTopShaderCode, fragmentShaderCode);
            mPrograms[1] = createOpenGLProgram(vertexMatrixLeftBottomShaderCode, fragmentShaderCode);
            mPrograms[2] = createOpenGLProgram(vertexMatrixRightTopShaderCode, fragmentShaderCode);
            mPrograms[3] = createOpenGLProgram(vertexMatrixRightBottomShaderCode, fragmentShaderCode);

            //纹理坐标,2D纹理,变换矩阵是一致的,使用同一个mPrograms[0]获取即可
            glHCoordinate = GLES20.glGetAttribLocation(mPrograms[0], "vCoordinate"); //纹理坐标集句柄
            glHTexture = GLES20.glGetUniformLocation(mPrograms[0], "vTexture");      //sampler2D纹理句柄
            glHMatrix = GLES20.glGetUniformLocation(mPrograms[0], "vMatrix");
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            android.util.Log.v("zhangming","onSurfaceChanged width = "+width+" height = "+height); //width = 2160 height = 1321
            GLES20.glViewport(0, 0, width, height);
            calMultiplyMatrix(width, height);
        }

        private void calMultiplyMatrix(int width, int height) {
            int w = mBitmap.getWidth();
            int h = mBitmap.getHeight();
            float sWH = w / (float) h;
            float sWidthHeight = width / (float) height;
            if (width > height) {
                if (sWH > sWidthHeight) {
                    Matrix.orthoM(mProjectionMatrix, 0, -sWidthHeight * sWH, sWidthHeight * sWH, -1, 1, 3, 7);
                } else {
                    Matrix.orthoM(mProjectionMatrix, 0, -sWidthHeight / sWH, sWidthHeight / sWH, -1, 1, 3, 7);
                }
            } else {
                if (sWH > sWidthHeight) {
                    Matrix.orthoM(mProjectionMatrix, 0, -1, 1, -1 / sWidthHeight * sWH, 1 / sWidthHeight * sWH, 3, 7);
                } else {
                    Matrix.orthoM(mProjectionMatrix, 0, -1, 1, -sWH / sWidthHeight, sWH / sWidthHeight, 3, 7);
                }
            }
            //设置相机位置,计算相机视图变换
            Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 7.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
            //计算最终变换后的矩阵
            //如果仅仅只把投影矩阵应用的到你绘制的对象中,通常你只会得到一个非常空的显示,一般情况下,在屏幕上显示的任何内容需要应用相机视图
            Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            drawSurfaceTexture(BITMAP_LEFT_TOP_POSITION);
            drawSurfaceTexture(BITMAP_LEFT_BOTTOM_POSITION);
            drawSurfaceTexture(BITMAP_RIGHT_TOP_POSITION);
            drawSurfaceTexture(BITMAP_RIGHT_BOTTOM_POSITION);
        }

        private void drawSurfaceTexture(int positionType) {
            GLES20.glUseProgram(mPrograms[positionType]);

            float[] newScratchMatrix = new float[16];
            long time = SystemClock.uptimeMillis() % 600000L;
            float angle = 0.05f * ((int) time);  //计算旋转的角度
            //设置旋转矩阵参数
            Matrix.setRotateM(mRotationMatrix, 0, angle, 0, 0, -1.0f);
            //利用旋转矩阵和原始的结合矩阵mMVPMatrix来扩展生成新的结合矩阵
            Matrix.multiplyMM(newScratchMatrix, 0, mMVPMatrix, 0, mRotationMatrix, 0);
            //将最终变换后的矩阵传入到着色器(shader)程序中
            GLES20.glUniformMatrix4fv(glHMatrix, 1, false, newScratchMatrix, 0);

            //启用顶点和纹理数组集
            GLES20.glEnableVertexAttribArray(0);
            GLES20.glEnableVertexAttribArray(glHCoordinate);

            //激活第0个纹理单元GL_TEXTURE0
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glUniform1i(glHTexture, 0);

            //初始化创建纹理Texture
            if (textureId == -1) {
                textureId = createTexture();
            }

            //向着色器中传入顶点坐标
            GLES20.glVertexAttribPointer(0, 2, GLES20.GL_FLOAT, false, 0, bPositions[positionType]); //2元组
            //向着色器中传入纹理坐标
            GLES20.glVertexAttribPointer(glHCoordinate, 2, GLES20.GL_FLOAT, false, 0, bCoord); //2元组
            //绘制矩形(4个顶点坐标)
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

            GLES20.glDisableVertexAttribArray(0);
        }

        private int createTexture() {
            int[] texture = new int[1];
            if (mBitmap != null && !mBitmap.isRecycled()) {
                //生成纹理
                GLES20.glGenTextures(1, texture, 0);
                //绑定纹理
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
                //设置环绕和过滤方式
                //环绕(超出纹理坐标范围): (s==x t==y GL_REPEAT 重复)
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
                //过滤(纹理像素映射到坐标点):(缩小,放大:GL_LINEAR线性)
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
                //根据以上指定的参数,生成一个2D纹理
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
                //返回生成后的2D纹理
                return texture[0];
            }
            return 0;
        }

        private void destroy() {
            if (mBitmap != null && !mBitmap.isRecycled()) {
                mBitmap.recycle();
                mBitmap = null;
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        if (renderer != null) {
            renderer.destroy();
        }
    }
}
