package com.decard.myopengl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 自定义渲染器
 *
 * @author ZJ
 * created at 2019/6/27 16:19
 */
public class MyRender implements GLSurfaceView.Renderer {

    private static final String TAG = "-----MyRender";
    private int mProgram;
    private int mColor;
    private int mPosition;
    // 顶点着色器的脚本
    private static final String verticesShader
            = "attribute vec2 vPosition;            \n" // 顶点位置属性vPosition
            + "void main(){                         \n"
            + "   gl_Position = vec4(vPosition,0,1);\n" // 确定顶点位置
            + "}";

    // 片元着色器的脚本
    private static final String fragmentShader
            = "precision mediump float;         \n" // 声明float类型的精度为中等(精度越高越耗资源)
            + "uniform vec4 uColor;             \n" // uniform的属性uColor
            + "void main(){                     \n"
            + "   gl_FragColor = uColor;        \n" // 给此片元的填充色
            + "}";

    //创建shader程序的方法
    private int createProgram(String vertexSource, String fragmentSource) {
        //加载顶点着色器
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }
        //加载片源着色器
        int pixelShaser = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShaser == 0) {
            return 0;
        }
        //创建程序
        int program = GLES20.glCreateProgram();
        //如果程序创建成功，向程序中加入顶点着色器和片源着色器
        if (program != 0) {
            //加入顶点着色器
            GLES20.glAttachShader(program, vertexShader);
            //加入片源着色器
            GLES20.glAttachShader(program, pixelShaser);
            //连接程序
            GLES20.glLinkProgram(program);
            //存放连接成功program数量的数组
            int[] programs = new int[1];
            //获取program的连接情况
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, programs, 0);
            //如果连接失败，打印log，并删除program
            if (programs[0] != GLES20.GL_TRUE) {
                Log.d(TAG, "createProgram: " + GLES20.glGetProgramInfoLog(programs[0]));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }

    /**
     * 获取图形的顶点
     * 由于不同平台字节顺序不同，数据单元不是字节的一定要经过byteBuffer转换
     * 关键是要通过ByteOrder设置nativeOrder，否则可能会有问题
     *
     * @return
     */
    private FloatBuffer getVertices() {
        float vertice[] = {0.0f, 0.5f, -0.5f, -0.5f, 0.5f, -0.5f};
        //创建顶点坐标数据缓冲
        //vertices.length*4是因为一个float占4个字节
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertice.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());                //设置字节顺序
        FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();     //转换为floatBuffer
        floatBuffer.put(vertice);                                 //向缓冲区中放入顶点坐标数据
        floatBuffer.position(0);                                  //设置缓冲区起始位置
        return floatBuffer;
    }

    /**
     * 加载指定shader的方法
     *
     * @param shaderType
     * @param source
     * @return
     */
    private int loadShader(int shaderType, String source) {
        //创建shader
        int shader = GLES20.glCreateShader(shaderType);
        Log.d(TAG, "loadShader: shader=" + shader);
        //创建成功，加载shader
        if (shader != 0) {
            //加载shader的源码
            GLES20.glShaderSource(shader, source);
            //编译shader
            GLES20.glCompileShader(shader);
            //存放编译成功shader的数量
            int[] compiled = new int[1];
            //获取shader的编译情况
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            //编译失败，删除此shader
            if (compiled[0] == 0) {
                Log.d(TAG, "loadShader: " + GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;

            }
        }
        return shader;
    }

    /**
     * 当GLSurfaceView中的Surface被创建的时候(界面显示)回调此方法，一般在这里做一些初始化
     *
     * @param gl     gl10 1.0版本的OpenGL对象，这里用于兼容老版本，用处不大
     * @param config eglConfig egl的配置信息(GLSurfaceView会自动创建egl，这里可以先忽略)
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //初始化着色器
        //基于顶点着色器与片源着色器创建程序
        mProgram = createProgram(verticesShader, fragmentShader);
        //获取着色器中的属性引用id（传入的字符串就是着色器脚本中的属性名）
        mPosition = GLES20.glGetAttribLocation(mProgram, "vPosition");
        mColor = GLES20.glGetUniformLocation(mColor, "uColor");
        //设置clearColor颜色RGBA (这里仅仅是设置清屏时GLES20.glClear()用的颜色值，而不是执行清屏)
        GLES20.glClearColor(0, 0, 1.0f, 1.0f);
    }

    /**
     * 当GLSurfaceView中的Surface被改变的时候回调此方法(一般是大小变化)
     *
     * @param gl
     * @param width
     * @param height
     */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // 设置绘图的窗口(可以理解成在画布上划出一块区域来画图)
        GLES20.glViewport(0, 0, width, height);
    }

    /**
     * 当Surface需要绘制的时候回调此方法
     * 根据GLSurfaceView.setRenderMode()设置的渲染模式不同回调的策略也不同：
     * GLSurfaceView.RENDERMODE_CONTINUOUSLY : 固定一秒回调60次(60fps)
     * GLSurfaceView.RENDERMODE_WHEN_DIRTY   : 当调用GLSurfaceView.requestRender()之后回调一次
     *
     * @param gl 同onSurfaceCreated()
     */
    @Override
    public void onDrawFrame(GL10 gl) {
        //获取图形的顶点坐标
        FloatBuffer floatBuffer = getVertices();
        //清屏
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        //使用某套shader程序
        GLES20.glUseProgram(mProgram);
        //为画笔指定顶点位置数据（vPosition）
        GLES20.glVertexAttribPointer(mPosition, 2, GLES20.GL_FLOAT, false, 0, floatBuffer);
        //允许顶点位置数据数组
        GLES20.glEnableVertexAttribArray(mPosition);
        //设置属性color
        GLES20.glUniform4f(mPosition, 0.0f, 1.0f, 0.0f, 1.0f);
        //绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 3);
    }
}
