package com.example.autoground;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SecretSetting extends BaseActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secret_setting);
        toolbar = findViewById(R.id.secretbar);
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
    public void terminalsetting(View view) {
        Intent intent = new Intent();
        intent.setClass(SecretSetting.this, TerminalSetting.class);
        startActivity(intent);
    }

    public void controldata(View view) {
        Intent intent = new Intent();
        intent.setClass(SecretSetting.this, CanshuJZ.class);
        startActivity(intent);
    }

    public void outfactory(View view) {
        Intent intent = new Intent();
        intent.setClass(SecretSetting.this, Senser.class);
        startActivity(intent);
    }

    public void installcheck(View view) {
        Intent intent = new Intent();
        intent.setClass(SecretSetting.this, Azjz.class);
        startActivity(intent);
    }
}
