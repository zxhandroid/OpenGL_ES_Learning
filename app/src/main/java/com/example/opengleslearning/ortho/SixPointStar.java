package com.example.opengleslearning.ortho;

import android.opengl.GLES30;
import android.opengl.Matrix;

import com.example.opengleslearning.utils.MatrixState;
import com.example.opengleslearning.utils.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * 六角星
 */
public class SixPointStar {

    private String vertexShader;
    private String fragmentShader;
    private int program;
    private int aPositionHandle;
    private int aColorHandle;
    private int uMVPMatrixHandle;

    final float UNIT_SIZE = 1f;
    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;
    //初始变化矩阵
    private float[] mMatrix = new float[16];
    //绕x轴旋转角度
    public float xAngle = 0;
    //绕y轴旋转角度
    public float yAngle = 0;
    private int vCount;

    /**
     * 构造函数
     * @param orthoSurfaceView 渲染显示的view
     * @param R  六角形内部小圆半径
     * @param r  六角形外角大圆半径
     * @param z  六角形z轴位置
     */
    public SixPointStar(OrthoSurfaceView orthoSurfaceView, float R, float r, float z) {
        //初始化顶点数据
        initVertexData(R, r, z);
        //initShader
        initShader(orthoSurfaceView);
    }

    /**
     * 初始化着色器
     *
     * @param surfaceView
     */
    private void initShader(OrthoSurfaceView surfaceView) {
        //加载顶点着色器脚本内容
        vertexShader = ShaderUtil.loadFromAssetsFile("vertex.sh", surfaceView.getResources());
        //加载片元着色器的脚本内容
        fragmentShader = ShaderUtil.loadFromAssetsFile("frag.sh", surfaceView.getResources());
        //基于顶点着色器与片元着色器创建程序
        program = ShaderUtil.createProgram(vertexShader, fragmentShader);
        //获取顶点位置属性引用id
        aPositionHandle = GLES30.glGetAttribLocation(program, "aPosition");
        //获取顶点颜色属性引用id
        aColorHandle = GLES30.glGetAttribLocation(program, "aColor");
        //获取程序总变换矩阵引用id
        uMVPMatrixHandle = GLES30.glGetUniformLocation(program, "uMVPMatrix");
    }

    /**
     * 初始化顶点数据
     *
     * @param R
     * @param r
     * @param z
     */
    private void initVertexData(float R, float r, float z) {
        List<Float> list = new ArrayList<>();
        float tempAngle = 360 / 6f;
        for (int angle = 0; angle < 360; angle += tempAngle) {
            //第一个三角形，逆时针描述点的位置
            //第一个点
            list.add(0f);
            list.add(0f);
            list.add(z);
            //第二个点
            list.add((float) (R * UNIT_SIZE * Math.cos(Math.toRadians(angle))));
            list.add((float) (R * UNIT_SIZE * Math.sin(Math.toRadians(angle))));
            list.add(z);
            //第三个点
            list.add((float) (r * UNIT_SIZE * Math.cos(Math.toRadians(angle + tempAngle / 2))));
            list.add((float) (r * UNIT_SIZE * Math.sin(Math.toRadians(angle + tempAngle / 2))));
            list.add(z);

            //第二个三角形，逆时针描述点位置
            //第一个点
            list.add(0f);
            list.add(0f);
            list.add(z);
            //第二个点
            list.add((float) (r * UNIT_SIZE * Math.cos(Math.toRadians(angle + tempAngle / 2))));
            list.add((float) (r * UNIT_SIZE * Math.sin(Math.toRadians(angle + tempAngle / 2))));
            list.add(z);
            //第三个点
            list.add((float) (R * UNIT_SIZE * Math.cos(Math.toRadians(angle + tempAngle))));
            list.add((float) (R * UNIT_SIZE * Math.sin(Math.toRadians(angle + tempAngle))));
            list.add(z);
        }
        //总共有多少个点
        vCount = list.size() / 3;
        //将list中的数据存放到float数组中
        float[] vertexArray = new float[list.size()];
        for (int i = 0; i < vCount; i++) {
            vertexArray[i * 3] = list.get(i * 3);
            vertexArray[i * 3 + 1] = list.get(i * 3 + 1);
            vertexArray[i * 3 + 2] = list.get(i * 3 + 2);
        }

        //分配顶点缓冲区
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertexArray.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        vertexBuffer = vbb.asFloatBuffer();
        vertexBuffer.put(vertexArray);
        vertexBuffer.position(0);

        //添加顶点颜色数据
        float[] colorArray = new float[vCount * 4];
        for (int i = 0; i < vCount; i++) {
            if (i % 3 == 0) {
                colorArray[i * 4] = 1;
                colorArray[i * 4 + 1] = 1;
                colorArray[i * 4 + 2] = 1;
                colorArray[i * 4 + 3] = 0;
            } else {
                colorArray[i * 4] = 0.75f;
                colorArray[i * 4 + 1] = 0.75f;
                colorArray[i * 4 + 2] = 0.75f;
                colorArray[i * 4 + 3] = 0;
            }
        }
        //分配颜色缓冲区
        ByteBuffer cbb = ByteBuffer.allocateDirect(colorArray.length * 4);
        cbb.order(ByteOrder.nativeOrder());
        colorBuffer = cbb.asFloatBuffer();
        //将顶点颜色数据放入缓冲器
        colorBuffer.put(colorArray);
        colorBuffer.position(0);

    }

    /**
     * 绘制自身的方法
     */
    public void drawSelf(){
        //启用着色器程序
        GLES30.glUseProgram(program);
        //初始化变化矩阵
        Matrix.setRotateM(mMatrix,0,0,0,1,0);
        //沿z轴正平衡一个单位
        Matrix.translateM(mMatrix,0,0,0,1);
        //设置旋转的角度
        Matrix.rotateM(mMatrix,0,xAngle,1,0,0); //绕x轴
        Matrix.rotateM(mMatrix,0,yAngle,0,1,0); //绕Y轴

        //将最终变化矩阵传入
        GLES30.glUniformMatrix4fv(uMVPMatrixHandle,1,false, MatrixState.getFinalMatrix(mMatrix),0);
        //将顶点位置数据传入
        GLES30.glVertexAttribPointer(aPositionHandle,3,GLES30.GL_FLOAT,false,3*4,vertexBuffer);
        //将顶点颜色数据传入
        GLES30.glVertexAttribPointer(aColorHandle,4,GLES30.GL_FLOAT,false,4*4,colorBuffer);

        //启用顶点位置数据数组
        GLES30.glEnableVertexAttribArray(aPositionHandle);
        //启用顶点颜色数据数组
        GLES30.glEnableVertexAttribArray(aColorHandle);

        //开始绘制自己
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES,0, vCount);
    }
}
