package com.example.autoground;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import java.util.ArrayList;
import java.util.List;

import cn.qqtheme.framework.picker.OptionPicker;
import cn.qqtheme.framework.picker.SinglePicker;

public class Cfsource extends AppCompatActivity {

    private Toolbar toolbar;
    private RadioButton p900module;
    private RadioButton h800module;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cfsource);
        toolbar = findViewById(R.id.cfsourcebar);
        p900module = findViewById(R.id.radioButton3);
        h800module = findViewById(R.id.radioButton4);
        setToolbar();
        p900click(p900module);
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


    public void h800click(View view) {
        p900module.setChecked(false);
        List data = new ArrayList<>();
        for (int i = 1;i<11;i++)
        {
            data.add(String.valueOf(i));
        }
        SinglePicker picker = new SinglePicker(this, data);
        picker.setCanceledOnTouchOutside(false);
        picker.setSelectedIndex(1);
        picker.setCycleDisable(false);
        picker.setOnItemPickListener(new SinglePicker.OnItemPickListener() {
            @Override
            public void onItemPicked(int i, Object o) {

            }

        });
        picker.show();
    }


    public void p900click(View view) {
        p900module.setChecked(true);
        h800module.setChecked(false);
        List data = new ArrayList<>();
        for (int i = 1;i<11;i++)
        {
            data.add(String.valueOf(i));
        }
        SinglePicker picker = new SinglePicker(this, data);
        picker.setCanceledOnTouchOutside(false);
        picker.setSelectedIndex(1);
        picker.setCycleDisable(false);
        picker.setOnItemPickListener(new SinglePicker.OnItemPickListener() {
            @Override
            public void onItemPicked(int i, Object o) {

            }

        });
        picker.show();
    }
}
