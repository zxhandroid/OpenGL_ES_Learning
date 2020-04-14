package com.example.opengleslearning.triangle;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * 三角形相关数据操作
 */
public class Triangle {

    //顶点数
    int vCount = 0;
    private FloatBuffer vertexBuffer;

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

        //顶点颜色数组
        float[] colors = new float[]{
                1, 1, 1, 0,
                0, 0, 1, 0,
                0, 1, 0, 0
        };
        //分配颜色缓冲区
        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
        cbb.order(ByteOrder.nativeOrder());          //设置字节顺序为本地操作系统顺序
        FloatBuffer colorBuffer = cbb.asFloatBuffer();
        colorBuffer.put(colors);                    //颜色缓冲区写入数据
        colorBuffer.position(0);                     //颜色缓冲区设置起始位置
    }

    /**
     * 创建并初始化着色器
     *
     * @param triangleSurfaceView
     */
    private void initShader(TriangleSurfaceView triangleSurfaceView) {


    }


}
