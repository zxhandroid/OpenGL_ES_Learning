package com.example.opengleslearning.triangle;

import android.opengl.GLES30;
import android.opengl.Matrix;

import com.example.opengleslearning.utils.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * 三角形相关数据操作
 */
public class Triangle {

    //物体3D变化矩阵
    static float[] mMatrix = new float[16];
    //摄像机位置朝向的参数矩阵
    static float[] vMatrix = new float[16];
    //投影矩阵
    static float[] projectMatrix = new float[16];
    //总变化矩阵
    private static float[] mVPMatrix;
    //顶点数
    int vCount = 0;
    private FloatBuffer vertexBuffer;
    private String vertexShader;
    private String fragmentShader;
    private int program;
    private int aPositionHandle;
    private int aColorHandle;
    private int uMVPMatrixHandle;
    //x轴旋转角度
    float xAngle = 0;
    private FloatBuffer colorBuffer;

    public Triangle(TriangleSurfaceView triangleSurfaceView) {
        //初始化顶点数据
        initVertexData();
        initShader(triangleSurfaceView);

    }

    /**
     * 初始化三角形顶点数据
     */
    private void initVertexData() {
        vCount = 3;     //顶点数
        final float UNIT_SIZE = 0.2f;   //单位长度
        //顶点数据数组
        float[] vertices = new float[]{
                -4 * UNIT_SIZE, 0, 0,
                0, -4 * UNIT_SIZE, 0,
                4 * UNIT_SIZE, 0, 0
        };
        //分配顶点缓冲区
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);  //float 占4个字节
        vbb.order(ByteOrder.nativeOrder());                     //设置字节顺序为本地操作系统顺序
        vertexBuffer = vbb.asFloatBuffer();    //转换成浮点缓冲区
        vertexBuffer.put(vertices);             //在缓冲区写入数据
        vertexBuffer.position(0);               //设置缓冲区起始位置

        //顶点颜色数组,argb
        float[] colors = new float[]{
                1, 1, 1, 0,
                0, 0, 1, 0,
                0, 1, 0, 0
        };
        //分配颜色缓冲区
        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
        cbb.order(ByteOrder.nativeOrder());          //设置字节顺序为本地操作系统顺序
        colorBuffer = cbb.asFloatBuffer();
        colorBuffer.put(colors);                    //颜色缓冲区写入数据
        colorBuffer.position(0);                     //颜色缓冲区设置起始位置
    }

    /**
     * 创建并初始化着色器
     *
     * @param triangleSurfaceView
     */
    private void initShader(TriangleSurfaceView triangleSurfaceView) {
        //加载顶点着色器脚本内容
        vertexShader = ShaderUtil.loadFromAssetsFile("vertex.sh",triangleSurfaceView.getResources());
        //加载片元着色器的脚本内容
        fragmentShader = ShaderUtil.loadFromAssetsFile("frag.sh",triangleSurfaceView.getResources());
        //基于顶点着色器与片元着色器创建程序
        program = ShaderUtil.createProgram(vertexShader, fragmentShader);
        //获取顶点位置属性引用id
        aPositionHandle = GLES30.glGetAttribLocation(program,"aPosition");
        //获取顶点颜色属性引用id
        aColorHandle = GLES30.glGetAttribLocation(program,"aColor");
        //获取程序总变换矩阵引用id
        uMVPMatrixHandle = GLES30.glGetUniformLocation(program,"uMVPMatrix");
    }

    /**
     * 获取最终变化矩阵
     * @param spec
     * @return
     */
    public static float[] getFinalMatrix(float[] spec){
        mVPMatrix = new float[16];
        //摄像机位置矩阵与传入矩阵相乘
        Matrix.multiplyMM(mVPMatrix,0,vMatrix,0,spec,0);
        //投影矩阵与上一步变化矩阵相乘并赋值到总变化矩阵mVPMatrix
        Matrix.multiplyMM(mVPMatrix,0,projectMatrix,0,mVPMatrix,0);
        return mVPMatrix;
    }
    /**
     * 绘制三角形自己
     */
    public void  drawSelf(){
        //使用着色器程序进行绘制
        GLES30.glUseProgram(program);
        //初始化旋转变化矩阵，刚开始是与y轴平齐，即面向我们的三角形
        Matrix.setRotateM(mMatrix,0,0,0,1,0);
        //设置沿着z轴正向平移
        Matrix.translateM(mMatrix,0,0,0,1);
        //设置绕x轴旋转
        Matrix.rotateM(mMatrix,0,xAngle,1,0,0);
        //将变化矩阵传入渲染管线
        GLES30.glUniformMatrix4fv(uMVPMatrixHandle,1,false,getFinalMatrix(mMatrix),0);
        //将顶点位置数据传入渲染管线
        GLES30.glVertexAttribPointer(aPositionHandle,3,GLES30.GL_FLOAT,false,3*4,vertexBuffer);
        //将顶点颜色数据传入渲染管线
        GLES30.glVertexAttribPointer(aColorHandle,4, GLES30.GL_FLOAT,false,4 * 4,colorBuffer);

        //启用顶点位置数据
        GLES30.glEnableVertexAttribArray(aPositionHandle);
        //启用顶点着色数据
        GLES30.glEnableVertexAttribArray(aColorHandle);
        //绘制三角形
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES,0,vCount);


    }


}
