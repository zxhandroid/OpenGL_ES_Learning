package com.example.opengleslearning.utils;

import android.opengl.Matrix;

/**
 * 矩阵变化工具类
 */
public class MatrixState {
    //总变化矩阵
    public static float[] MVPMatrix;
    //投影矩阵
    public static float[] projectMatrix = new float[16];
    //相机参数矩阵
    public static float[] vMatrix = new float[16];

    //设置相机的位置
    public static void setCamera(float eyeX,float eyeY,float eyeZ,float centerX,float centerY,float centerZ,float upX,float upY,float upZ){
        Matrix.setLookAtM(vMatrix,0,eyeX,eyeY,eyeZ,centerX,centerY,centerZ,upX,upY,upZ);
    }

    //设置正交投影矩阵
    public static void setProjectOrtho(float left,float right, float bottom,float top, float near ,float far ){
        Matrix.orthoM(projectMatrix,0,left,right,bottom,top,near,far);
    }

    //获取最终变化矩阵
    public static float[] getFinalMatrix(float[] matrix) {
        MVPMatrix = new float[16];
        //视图矩阵与变化矩阵相乘
        Matrix.multiplyMM(MVPMatrix,0,vMatrix,0,matrix,0);
        //投影矩阵与总变化矩阵相乘
        Matrix.multiplyMM(MVPMatrix,0,projectMatrix,0,MVPMatrix,0);

        return MVPMatrix;
    }
}
