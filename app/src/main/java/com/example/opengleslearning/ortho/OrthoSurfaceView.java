package com.example.opengleslearning.ortho;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class OrthoSurfaceView extends GLSurfaceView {

    private float previousX;
    private float previousY;

    public OrthoSurfaceView(Context context) {
        super(context);
        //创建render
        OrthoRender orthoRender = new OrthoRender();
        this.setRenderer(orthoRender);
    }

    /**
     * 触屏事件，获取触摸x，y 的变化量
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //获取按下点的位置
                previousX = event.getX();
                previousY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE :
                float x = event.getX();
                float y = event.getY();
                float dx = event.getX() - previousX;
                float dy = event.getY() -previousY;
                //更新六角形的旋转角度


                previousX = x;
                previousY = y;
                break;
            default:
                break;
        }




        return true;
    }

    /**
     * render
     */
    static class OrthoRender implements GLSurfaceView.Renderer{

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
           //设置清屏色黑色
            GLES30.glClearColor(0.5f,0.5f,0.5f,1.0f);
            //创建六个六角形

        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视图口
            GLES30.glViewport(0,0,width,height);


        }

        @Override
        public void onDrawFrame(GL10 gl) {

        }
    }
}
