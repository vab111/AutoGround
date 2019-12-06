package com.example.autoground;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class TaskHistory extends AppCompatActivity {

    private historySurfaceView surfaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_history);
        surfaceView = findViewById(R.id.surfaceView2);
        surfaceView.taskname = getIntent().getStringExtra("filename");

    }
}
