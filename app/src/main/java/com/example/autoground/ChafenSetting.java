package com.example.autoground;

import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ChafenSetting extends BaseActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chafen_setting);
        toolbar = findViewById(R.id.chafenbar);
        setToolbar();
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
                finish();
            }
        });
    }
    public void innerSig(View view) {
        Intent intent = new Intent();
        intent.setClass(ChafenSetting.this, Cfsource.class);
        startActivity(intent);
    }

    public void outSig(View view) {
        Intent intent = new Intent();
        intent.setClass(ChafenSetting.this, NetcfSetting.class);
        startActivity(intent);
    }
}
