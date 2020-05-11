package com.example.opengleslearning.project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.opengleslearning.R;

public class ProjectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ProjectSurfaceView projectSurfaceView = new ProjectSurfaceView(this);
        projectSurfaceView.requestFocus();
        projectSurfaceView.setFocusableInTouchMode(true);

        setContentView(projectSurfaceView);

    }
}
