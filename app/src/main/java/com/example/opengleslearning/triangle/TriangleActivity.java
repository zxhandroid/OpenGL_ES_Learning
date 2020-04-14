package com.example.opengleslearning.triangle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

/**
 * 主activity 负责展示绘制的视图
 */
public class TriangleActivity extends AppCompatActivity {

    private TriangleSurfaceView triangleSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //自定义一个surfaceView
        triangleSurfaceView = new TriangleSurfaceView(this);
        triangleSurfaceView.requestFocus();                     //获取焦点
        triangleSurfaceView.setFocusableInTouchMode(true);      //可触摸
        //设置到视图中
        setContentView(triangleSurfaceView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        triangleSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        triangleSurfaceView.onPause();
    }
}
