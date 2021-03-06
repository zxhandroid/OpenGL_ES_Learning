package com.example.opengleslearning.ortho;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import com.example.opengleslearning.utils.MatrixState;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 *
 */
public class OrthoSurfaceView extends GLSurfaceView {
    private static final float TOUCH_SCALE_FACTOR = 180.f / 320;    //角度缩放比例
    private float previousX;
    private float previousY;
    private final OrthoRender orthoRender;

    public OrthoSurfaceView(Context context) {
        super(context);
        //明确使用Gl ES 3.0来渲染,必须指定，不然会显示不出来，报 glDrawArrays is called with VERTEX_ARRAY client state disabled!
        this.setEGLContextClientVersion(3);
        //创建render
        orthoRender = new OrthoRender();
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
                for (SixPointStar star : orthoRender.stars) {
                    star.xAngle += dy * TOUCH_SCALE_FACTOR;
                    star.yAngle += dx * TOUCH_SCALE_FACTOR;
                }

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
     class OrthoRender implements GLSurfaceView.Renderer{
        //六个六角形
        SixPointStar[] stars = new SixPointStar[6];

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
           //设置清屏色黑色
            GLES30.glClearColor(0.5f,0.5f,0.5f,1.0f);
            //创建六个六角形
            for (int i = 0; i < stars.length; i++) {
                stars[i] = new SixPointStar(OrthoSurfaceView.this,0.2f, 0.5f, -0.3f * i);
            }
            //打开深度检测
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视图口
            GLES30.glViewport(0,0,width,height);
            //设置视口的宽高比
            float ratio  = (float) width / height;
            //设置正交投影矩阵
            MatrixState.setProjectOrtho(-ratio,ratio,-1 ,1,1,10);

            //设置相机位置
            MatrixState.setCamera(0,0,3f,0,0,0,0,1f,0);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            //清除测试缓冲与颜色缓冲
            GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
            //开始循环绘制六角形
            for (SixPointStar star : stars) {
                star.drawSelf();
            }
        }
    }
}
