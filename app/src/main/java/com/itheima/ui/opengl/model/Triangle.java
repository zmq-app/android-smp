package com.itheima.ui.opengl.model;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * @Subject 三角形的绘制model
 * @Author  zhangming
 * @Date    2019-10-12 10:35
 */
public class Triangle {
    private FloatBuffer vertexBuffer;

    // number of coordinates per vertex in this array
    private final int COORDS_PER_VERTEX = 3;
    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    // in counterclockwise order
    static float triangleCoords[] = {
        0.0f,  0.5f, 0.0f,  // top
        -0.5f, -0.5f, 0.0f, // bottom left
        0.5f, -0.5f, 0.0f   // bottom right
    };
    // Set color with red, green, blue and alpha (opacity) values
    static float triangleColor[] = {
        1.0f, 0.0f, 0.0f, 0.8f
    };

    private final String vertexShaderCode =
        "attribute vec4 vPosition;" +
                "void main() {" +
                "  gl_Position = vPosition;" +
                "}";
    private final String fragmentShaderCode =
        "precision mediump float;" +
                "uniform vec4 vColor;" +
                "uniform mat4 vMatrix;" +
                "void main() {" +
                "  gl_FragColor = vColor;" +
                "}";

    private int mVertexHandle;       //顶点着色器的位置的句柄
    private int mFragmentHandle;     //片段着色器的颜色的句柄
    private int mMVPMatrixHandle;    //投影与相机视图合成矩阵的句柄

    public Triangle() {
        // 初始化ByteBuffer,长度为arr数组的长度*4,因为一个float占4个字节
        ByteBuffer buffer = ByteBuffer.allocateDirect(triangleCoords.length * 4);
        // 由于Java的缓冲区数据存储结构为大端字节序(BigEdian),而OpenGl的数据为小端字节序(LittleEdian)
        // 因为数据存储结构的差异,在Android中使用OpenGl的时候必须要进行下转换,此处数组排列用nativeOrder
        buffer.order(ByteOrder.nativeOrder());
        // 从ByteBuffer创建一个浮点缓冲区
        vertexBuffer = buffer.asFloatBuffer();
        // 将坐标添加到FloatBuffer
        vertexBuffer.put(triangleCoords);
        // 设置缓冲区来读取第一个坐标
        vertexBuffer.position(0);

        // 创建顶点和片段着色器
        int vertexShader = TriangleShader.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = TriangleShader.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        // 创建空的OpenGL ES程序
        int mProgram = GLES20.glCreateProgram();
        // 添加顶点着色器到程序中
        GLES20.glAttachShader(mProgram, vertexShader);
        // 添加片段着色器到程序中
        GLES20.glAttachShader(mProgram, fragmentShader);
        // 创建OpenGL ES程序可执行文件
        GLES20.glLinkProgram(mProgram);
        // 将程序添加到OpenGL ES环境
        GLES20.glUseProgram(mProgram);

        // 获取顶点着色器的位置的句柄
        mVertexHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        // 获取片段着色器的颜色的句柄
        mFragmentHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        // 获取投影与相机视图合成矩阵的句柄
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "vMatrix");
    }

    public void drawTrangle(float[] mvpMatrix) {
        // 启用三角形顶点位置的句柄
        GLES20.glEnableVertexAttribArray(mVertexHandle);
        // 准备三角形坐标数据
        GLES20.glVertexAttribPointer(mVertexHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);
        // 设置绘制三角形的颜色
        GLES20.glUniform4fv(mFragmentHandle, 1, triangleColor, 0);
        // 设置矩阵mvpMatrix
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        // 绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
        // 禁用顶点数组
        GLES20.glDisableVertexAttribArray(mVertexHandle);
    }

    static class TriangleShader {
        /**
         * @param type 着色器类型
         * @param shaderCode 着色器命令片段
         * @return
         */
        public static int loadShader(int type, String shaderCode){
            // 创造顶点着色器类型(GLES20.GL_VERTEX_SHADER)
            // 或者是片段着色器类型(GLES20.GL_FRAGMENT_SHADER)
            int shader = GLES20.glCreateShader(type);
            // 添加上面编写的着色器代码并编译它
            GLES20.glShaderSource(shader, shaderCode);
            GLES20.glCompileShader(shader);
            return shader;
        }
    }
}
