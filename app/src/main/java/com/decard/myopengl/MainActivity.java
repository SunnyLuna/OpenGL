package com.decard.myopengl;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private GLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initVeiws();
        //设置版本
        glSurfaceView.setEGLContextClientVersion(2);
        //设置渲染器
        glSurfaceView.setRenderer(new MyRender());
        //设置选热模式为连续模式
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    private void initVeiws() {
        glSurfaceView = findViewById(R.id.gl_surfaceView);
    }
}
