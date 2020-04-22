package com.example.opengleslearning;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;

import com.example.opengleslearning.ortho.OrthoActivity;
import com.example.opengleslearning.triangle.TriangleActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SampleAdapter.ItemClickListener {

    private RecyclerView rvSample;
    private ArrayList<String> datas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDatas();
        initAdapter();
    }

    private void initDatas() {
        datas.add("旋转三角形示例");
        datas.add("正交投影示例");
    }

    private void initAdapter() {
        rvSample = findViewById(R.id.rv_sample);
        rvSample.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        SampleAdapter adapter = new SampleAdapter(datas);
        rvSample.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(int position) {
        switch (position){
            case 0:
                jump2Activity(TriangleActivity.class);
                break;
            case 1:
                jump2Activity(OrthoActivity.class);
                break;
            default:
                break;
        }
    }

    /**
     * 页面跳转
     * @param clazz
     */
    private void jump2Activity(Class clazz) {
        Intent intent = new Intent(this,clazz);
        startActivity(intent);
    }


}
