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

public class Senser extends AppCompatActivity {
    private int state = 1;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senser);
        toolbar = findViewById(R.id.senserbar);
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
    public void reset(View view) {
        final Dialog bottomDialog = new Dialog(this, R.style.BottomDialog);
        final View contentView = LayoutInflater.from(this).inflate(R.layout.resetsensor, null);
        bottomDialog.setContentView(contentView);

        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        bottomDialog.setCanceledOnTouchOutside(false);
        bottomDialog.show();
        Button cancelBtn = contentView.findViewById(R.id.button61);
        final Button confirmBtn = contentView.findViewById(R.id.button62);
        final TextView stateText = contentView.findViewById(R.id.textView78);
        final TextView discription = contentView.findViewById(R.id.textView79);
        final EditText input = contentView.findViewById(R.id.editText41);
        input.setVisibility(View.INVISIBLE);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomDialog.dismiss();
            }
        });
         state = 1;
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 添加新的任务
            state++;
            stateText.setText(String.format("%d/4", state));

            switch (state)
            {
                case 1:
                    discription.setText("");
                    break;
                case 2:
                    discription.setText("请输入准确值");
                    input.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    discription.setText("请再次输入准确值");
                    break;
                case 4:
                    discription.setText("校准完成");
                    confirmBtn.setText("完成");
                    input.setVisibility(View.INVISIBLE);
                    break;
                case 5:
                    bottomDialog.dismiss();
                    break;

            }


            }


        });
    }
}
