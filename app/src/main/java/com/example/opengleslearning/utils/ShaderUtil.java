package com.example.opengleslearning.utils;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * 着色器加载工具类
 */
public class ShaderUtil {
    private  static final String TAG = "ShaderUtil";
    /**
     * 加载顶点或片元着色器源码进行编译
     * @param shaderType 顶点或片元着色器
     * @param source 顶点或片元源码
     * @return
     */
    public static int loadShader(int shaderType,String source){
        //1. 创建shader,并记录id
        int shader = GLES30.glCreateShader(shaderType);
        if (shader != 0){
            //创建成功
            //2. 加载着色器源码
            GLES30.glShaderSource(shader,source);
            //3. 编译着色器源码
            GLES30.glCompileShader(shader);
            //4. 获取shader编译情况,并输出到compiled数组中
            int[] compiled = new int[1];
            GLES30.glGetShaderiv(shader,GLES30.GL_COMPILE_STATUS,compiled,0);
            if (compiled[0] == 0) {
                Log.e(TAG, "Could not compile shader " + shaderType);
                Log.e(TAG, "error:" + GLES30.glGetShaderInfoLog(shader) );
                //5. 失败，则删除shader
                GLES30.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    /**
     * 创建着色器程序
     * @param vertexSource  顶点数据
     * @param fragmentSource 片元数据
     * @return
     */
    public static int createProgram(String vertexSource,String fragmentSource){
        //1. 加载顶点着色器
        int vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) return 0;
        //2. 加载片元着色器
        int fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentSource);
        if (fragmentShader == 0) return 0;
        //3. 创建程序
        int program = GLES30.glCreateProgram();
        if (program != 0){
            //4.创建成功，向程序加入顶点着色器与片元着色器
            GLES30.glAttachShader(program,vertexShader);    //加入顶点着色器
            checkGlError("glAttachShader-vertexShader");
            GLES30.glAttachShader(program,fragmentShader);  //加入片元着色器
            checkGlError("glAttachShader-fragmentShader");

            //5. 链接程序
            GLES30.glLinkProgram(program);
            //6. 获取链接状态,输出到linkStatus中
            int[]linkStatus = new int[1];
            GLES30.glGetProgramiv(program,GLES30.GL_LINK_STATUS,linkStatus,0);
            // 7. 如果链接失败，则报错并删除程序
            if (linkStatus[0] != GLES30.GL_TRUE){
                Log.e(TAG, "createProgram: Could not link program");
                Log.e(TAG, "createProgram: " + GLES30.glGetProgramInfoLog(program));
                //删除
                GLES30.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }

    /**
     * 从sh脚本中加载着色器内容
     * @param fname  文件名
     * @param resources  应用资源对象
     * @return
     */
    public static String loadFromAssetsFile(String fname, Resources resources){
        String result = null;
        try{
            //从assets中读取文件
            InputStream in = resources.getAssets().open(fname);
            int ch = 0;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((ch = in.read()) != -1){
                baos.write(ch);
            }
            byte[] buff = baos.toByteArray();
            baos.close();
            in.close();
            result = new String(buff, StandardCharsets.UTF_8);
            result = result.replaceAll("\\r\\n","\n");
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 检查每一步是否有错误
     * @param op 步骤
     */
    public static void checkGlError(String op){
        int error;
        while ((error = GLES30.glGetError()) != GLES30.GL_NO_ERROR){
            Log.e(TAG, "checkGlError: " + op  + ",error:" + error);
            //有问题则直接抛出异常，避免异常累积，不易排查
            throw new RuntimeException("checkGlError: " + op  + ",error:" + error);
        }
    }
}
