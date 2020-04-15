package com.example.opengleslearning.triangle;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 自定义glSurfaceView
 */
public class TriangleSurfaceView extends GLSurfaceView {
    final float ANGLE_SPAN = 0.375f;    //每次旋转的角度

    private final TraiangleRender render;

    public TriangleSurfaceView(Context context) {
        super(context);
        //明确使用Gl ES 3.0来渲染
        this.setEGLContextClientVersion(3);
        //创建渲染器
        render = new TraiangleRender();
        //设置渲染器
        this.setRenderer(render);
        //设置渲染模式,为持续渲染
        this.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    private class TraiangleRender implements GLSurfaceView.Renderer {

        private Triangle triangle;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //1.设置清屏色为黑色
            GLES30.glClearColor(0,0,0,1.0f);
            //2.创建一个三角形对象
            triangle = new Triangle(TriangleSurfaceView.this);
            //3.打开深度检测
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            //4.开启一个线程不断改变旋转的角度
            RotateThread rotateThread = new RotateThread();
            rotateThread.start();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视口
            GLES30.glViewport(0,0,width,height);
            //获取宽高比
            float ratio = (float)width / height;
            //设置平截头体,即定义一个投影矩阵
            Matrix.frustumM(Triangle.projectMatrix,0,-ratio,ratio,-1,1,1,10);
            //设置摄像机位置
            Matrix.setLookAtM(Triangle.vMatrix,0,0,0,0,3,0,0,0,1,0);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            //清除颜色缓存 和深度缓存
            GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
            //开始绘制
            triangle.drawSelf();
        }
    }

    private class RotateThread extends Thread {
        public boolean flag = true;
        @Override
        public void run() {
            while (flag){
                render.triangle.xAngle = render.triangle.xAngle += ANGLE_SPAN;
                try {
                    //休眠
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
