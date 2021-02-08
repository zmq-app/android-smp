package com.itheima.ui.opengl.model;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * @Subject 线条的绘制model
 * @Author  zhangming
 * @Date    2019-10-12 11:25
 */
public class Lines {
    // number of coordinates per vertex in this array
    private final int COORDS_PER_VERTEX = 3;

    private FloatBuffer vertexBuffer;

    /* 顶点着色器编译代码指令 */
    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +  //顶点位置属性vPosition
                    "void main() {" +
                    "  gl_Position = vPosition;" +  //确定顶点位置
                    "}";
    /* 片段着色器编译代码指令 */
    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +  //uniform的属性uColor
                    "void main() {" +
                    "  gl_FragColor = vColor;" +  //给此片元的填充色
                    "}";
    private int mVertexHandle;      //顶点着色器的位置的句柄
    private int mFragmentHandle;    //片段着色器的颜色的句柄

    public Lines(final float[] lineCoords) {
        // 初始化ByteBuffer,长度为arr数组的长度*4,因为一个float占4个字节,分配空间的大小=数组的长度*sizeof(float)
        ByteBuffer buffer = ByteBuffer.allocateDirect(lineCoords.length * 4);
        // 由于Java的缓冲区数据存储结构为大端字节序(BigEdian),而OpenGl的数据为小端字节序(LittleEdian)
        // 因为数据存储结构的差异,在Android中使用OpenGl的时候必须要进行下转换,此处数组排列用nativeOrder
        buffer.order(ByteOrder.nativeOrder());
        // 从ByteBuffer创建一个浮点缓冲区
        vertexBuffer = buffer.asFloatBuffer();
        // 将坐标添加到FloatBuffer
        vertexBuffer.put(lineCoords);
        // 设置缓冲区来读取第一个坐标
        vertexBuffer.position(0);

        // 创建顶点着色器,并执行编译操作
        int vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vertexShader, vertexShaderCode);
        GLES20.glCompileShader(vertexShader);

        // 创建片段着色器,并执行编译操作
        int fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fragmentShader,fragmentShaderCode);
        GLES20.glCompileShader(fragmentShader);

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

        // 获取顶点着色器的位置的句柄[获取着色器中的属性引用id,传入的字符串是着色器脚本中的属性名]
        mVertexHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        // 获取片段着色器的颜色的句柄[获取着色器中的属性引用id,传入的字符串是着色器脚本中的属性名]
        mFragmentHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
    }

    public void drawLines() {
        // 启用顶点位置的句柄
        GLES20.glEnableVertexAttribArray(mVertexHandle);
        // 准备坐标数据 [第二个参数size: 指定每个顶点属性的组件数量,值必须为1,2,3,4.其中初始值为4(如position是由3个(x,y,z)组成,而颜色是4个(r,g,b,a)]
        GLES20.glVertexAttribPointer(mVertexHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        // 设置绘制线条的颜色
        GLES20.glUniform4f(mFragmentHandle,0.0f ,0.0f, 1.0f, 1.0f);
        // 绘制线条
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, 2);  //顶点的数目vertexCount=2
    }
}
