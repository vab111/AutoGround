package com.example.autoground;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class Azjz extends BaseActivity {

    private Toolbar toolbar;
    private EditText zxDerection;
    private EditText avgHang;
    private EditText curSpeed;
    private EditText headDerection;
    private EditText avgHead;
    private EditText txxz;
    private EditText wheelAngle;
    private EditText avgAngle;
    private EditText AngleFix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_azjz);
        toolbar = findViewById(R.id.azjzbar);
        zxDerection = findViewById(R.id.editText);
        avgHang = findViewById(R.id.editText2);
        curSpeed= findViewById(R.id.editText3);
        headDerection= findViewById(R.id.editText4);
        avgHead= findViewById(R.id.editText5);
        txxz= findViewById(R.id.editText6);
        wheelAngle= findViewById(R.id.editText7);
        avgAngle= findViewById(R.id.editText8);
        AngleFix= findViewById(R.id.editText9);
//        zxDerection.setEnabled(false);
//        avgHang.setEnabled(false);
//        curSpeed.setEnabled(false);
//        headDerection.setEnabled(false);
//        avgHead.setEnabled(false);
//        txxz.setEnabled(false);
//        wheelAngle.setEnabled(false);
//        avgAngle.setEnabled(false);
//        AngleFix.setEnabled(false);
        setToolbar();
    }
    private void setToolbar() {
        TextView text = (TextView) toolbar.getChildAt(0);
        text.setGravity(Gravity.CENTER_HORIZONTAL);
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
}
