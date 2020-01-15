package com.example.autoground;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class TaskHistory extends BaseActivity{

    private historySurfaceView surfaceView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_history);
        surfaceView = findViewById(R.id.surfaceView2);
        surfaceView.taskname = getIntent().getStringExtra("filename");
        toolbar = findViewById(R.id.taskhistoryBar);
        setToolbar();
        surfaceView.refresh = true;
    }
    private void setToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);//设计隐藏标题

        //设置显示返回键
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // finish();
                surfaceView.refresh = false;

                finish();
            }
        });
    }
}
