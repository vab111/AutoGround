package com.example.autoground;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SystemSetting extends BaseActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_setting);
        toolbar = findViewById(R.id.settingbar);
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

    public void AZJZ(View view) {
        Intent intent = new Intent();
        intent.setClass(SystemSetting.this, Azjz.class);
        startActivity(intent);

    }

    public void NJsetting(View view) {
        Intent intent = new Intent();
        intent.setClass(SystemSetting.this, NJNJsetting.class);
        startActivity(intent);
    }

    public void aboutself(View view) {
        Intent intent = new Intent();
        intent.setClass(SystemSetting.this, AboutSelf.class);
        startActivity(intent);
    }

    public void cfsetting(View view) {
        Intent intent = new Intent();
        intent.setClass(SystemSetting.this, ChafenSetting.class);
        startActivity(intent);
    }

    public void terminalsetting(View view) {
        Intent intent = new Intent();
        intent.setClass(SystemSetting.this, TerminalSetting.class);
        startActivity(intent);
    }
}
