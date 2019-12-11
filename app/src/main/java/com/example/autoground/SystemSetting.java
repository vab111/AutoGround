package com.example.autoground;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

    public void chuchang(View view) {
        final Dialog bottomDialog = new Dialog(this, R.style.BottomDialog);
        final View contentView = LayoutInflater.from(this).inflate(R.layout.passwordcheck, null);
        bottomDialog.setContentView(contentView);

        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        bottomDialog.setCanceledOnTouchOutside(false);
        bottomDialog.show();
        Button cancelBtn = contentView.findViewById(R.id.button42);
        final Button confirmBtn = contentView.findViewById(R.id.button43);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomDialog.dismiss();
            }
        });
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 添加新的任务

                EditText name = contentView.findViewById(R.id.editText40);
                String ps = name.getText().toString();
                if (ps.equals("lsjg2019"))
                {
                    Intent intent = new Intent();
                    intent.setClass(SystemSetting.this, Senser.class);
                    startActivity(intent);
                    bottomDialog.dismiss();
                }
                else
                {
                    name.setText("");
                    Toast.makeText(getBaseContext(), "密码错误!", Toast.LENGTH_LONG).show();
                }

            }


        });
    }

    public void canshujiaozhun(View view) {
        final Dialog bottomDialog = new Dialog(this, R.style.BottomDialog);
        final View contentView = LayoutInflater.from(this).inflate(R.layout.passwordcheck, null);
        bottomDialog.setContentView(contentView);

        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        bottomDialog.setCanceledOnTouchOutside(false);
        bottomDialog.show();
        Button cancelBtn = contentView.findViewById(R.id.button42);
        final Button confirmBtn = contentView.findViewById(R.id.button43);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomDialog.dismiss();
            }
        });
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 添加新的任务

                EditText name = contentView.findViewById(R.id.editText40);
                String ps = name.getText().toString();
                if (ps.equals("lsjg2019"))
                {
                    Intent intent = new Intent();
                    intent.setClass(SystemSetting.this, CanshuJZ.class);
                    startActivity(intent);
                    bottomDialog.dismiss();
                }
                else
                {
                    name.setText("");
                    Toast.makeText(getBaseContext(), "密码错误!", Toast.LENGTH_LONG).show();
                }

            }


        });
    }
}
