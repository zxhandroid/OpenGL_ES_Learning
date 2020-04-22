package com.example.opengleslearning.ortho;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

/**
 * 正交投影
 */
public class OrthoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //创建surfaceView
        OrthoSurfaceView orthoSurfaceView = new OrthoSurfaceView(this);
        orthoSurfaceView.requestFocus();
        orthoSurfaceView.setFocusableInTouchMode(true);
        //设置到窗口
        setContentView(orthoSurfaceView);

    }
}
