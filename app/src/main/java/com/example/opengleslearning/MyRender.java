package com.example.opengleslearning;

import android.opengl.GLSurfaceView;

import java.util.LinkedHashMap;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 渲染器
 */
public class MyRender implements GLSurfaceView.Renderer {

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //清除一下颜色
        gl.glClearColor(0,0,0,1);
        LinkedHashMap map = new LinkedHashMap();
//        map.put()



    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //设置一下视口,为当前宽高
        gl.glViewport(0,0,width,height);
        //设置一下模式,为投影
        gl.glMatrixMode(GL10.GL_PROJECTION);
        //设置一下标准矩阵
        gl.glLoadIdentity();
        //设置一下投影四棱柱
        float ratio = (float) width / (float) height;
        gl.glFrustumf(-1,1,-ratio,ratio,3,5);
        //设置相机的位置
//        gl.
    }

    @Override
    public void onDrawFrame(GL10 gl) {

    }
}
