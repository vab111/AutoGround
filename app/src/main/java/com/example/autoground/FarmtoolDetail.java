package com.example.autoground;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class FarmtoolDetail extends AppCompatActivity {

    private Toolbar toolbar;
    private List FarmtoolList;
    private int id;
    private FarmTool item;
    private EditText ftType;
    private EditText ftBrand;
    private EditText ftSerial;
    private EditText ftYear;
    private EditText ftWidth;
    private EditText ftBackdis;
    private EditText ftMid;
    private RadioButton leftBtn;
    private RadioButton rightBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmtool_detail);
        toolbar = findViewById(R.id.farmtooldetailbar);

        ftType = findViewById(R.id.editText34);
        ftBrand = findViewById(R.id.editText35);
        ftSerial = findViewById(R.id.editText42);
        ftYear = findViewById(R.id.editText43);
        ftWidth = findViewById(R.id.editText44);
        ftBackdis = findViewById(R.id.editText45);
        ftMid = findViewById(R.id.editText46);
        leftBtn = findViewById(R.id.radioButton11);
        rightBtn = findViewById(R.id.radioButton12);
        id = Integer.parseInt(getIntent().getStringExtra("farmtoolid"));
        setToolbar();
        getList();
        if (id<FarmtoolList.size()) {
            ftType.setEnabled(false);
            initView();
        }
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
                Intent intent = new Intent();
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }
    private void initView()
    {
        FarmTool item = (FarmTool) FarmtoolList.get(id);
        ftType.setText(item.NJType);
        ftBrand.setText(item.NJBrand);
        ftSerial.setText(item.NJXinhao);
        ftYear.setText(item.NJyear);
        ftWidth.setText(String.format("%d", item.NJWidth));
        ftBackdis.setText(String.format("%d", item.NJBackdis));
        ftMid.setText(String.format("%d",item.pianyi));
        if (item.leftright)
            leftBtn.setChecked(true);
        else
            rightBtn.setChecked(true);
    }
    private void getList()
    {
        FarmtoolList = new ArrayList();

        File fs = new File(Environment.getExternalStorageDirectory()+"/AutoGround/Farmtool.json");

            String result = "";
            try {
                FileInputStream f = new FileInputStream(fs);
                BufferedReader bis = new BufferedReader(new InputStreamReader(f));
                String line = "";
                while ((line = bis.readLine()) != null) {
                    result += line;
                }
                bis.close();
                f.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (result.length()>0) {
                Gson gson = new Gson();
                FarmtoolList = gson.fromJson(result, new TypeToken<List<FarmTool>>() {
                }.getType());
            }

    }
    private void saveList()
    {
        File fs = new File(Environment.getExternalStorageDirectory()+"/AutoGround/Farmtool.json");
        try {
            FileOutputStream outputStream =new FileOutputStream(fs);
            OutputStreamWriter outStream = new OutputStreamWriter(outputStream);


            Gson gson = new Gson();
            String jsonString = gson.toJson(FarmtoolList);
            outStream.write(jsonString);

            outputStream.flush();
            outStream.flush();
            outputStream.close();
            outputStream.close();



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save(View view) {

        if (id<FarmtoolList.size()) {
            item = (FarmTool) FarmtoolList.get(id);
            item.NJBrand = ftBrand.getText().toString();
            item.NJXinhao = ftSerial.getText().toString();
            item.NJyear = ftYear.getText().toString();
            String content = ftMid.getText().toString();
            if (content.length()>0)
                item.pianyi = Integer.parseInt(content);
            content = ftBackdis.getText().toString();
            if (content.length()>0)
                item.NJBackdis = Integer.parseInt(content);
            content = ftWidth.getText().toString();
            if (content.length()>0)
                item.NJWidth = Integer.parseInt(content);
            if (leftBtn.isChecked())
                item.leftright = true;
            else
                item.leftright = false;
            saveList();
        }
        else
        {
            item = new FarmTool();
            item.NJType = ftType.getText().toString();
            if (item.NJType.length()==0||checkName(item.NJType))
            {
                ftType.setText(null);
                Toast.makeText(getBaseContext(), "请重新输入农具作业类型！", Toast.LENGTH_LONG).show();
                return;
            }
            item.NJBrand = ftBrand.getText().toString();
            item.NJXinhao = ftSerial.getText().toString();
            item.NJyear = ftYear.getText().toString();
            String content = ftMid.getText().toString();
            if (content.length()>0)
                item.pianyi = Integer.parseInt(content);
            content = ftBackdis.getText().toString();
            if (content.length()>0)
                item.NJBackdis = Integer.parseInt(content);
            content = ftWidth.getText().toString();
            if (content.length()>0)
            item.NJWidth = Integer.parseInt(content);
            if (leftBtn.isChecked())
                item.leftright = true;
            else
                item.leftright = false;
            FarmtoolList.add(item);
            saveList();
        }
    }
    private boolean checkName(String name)
    {
        for (int i=0;i<FarmtoolList.size();i++)
        {
            FarmTool item = (FarmTool) FarmtoolList.get(i);
            if (item.NJType.equals(name))
                return true;
        }
        return false;
    }

    public void rightClicked(View view) {
        rightBtn.setChecked(true);
        leftBtn.setChecked(false);
    }

    public void leftClicked(View view) {
        rightBtn.setChecked(false);
        leftBtn.setChecked(true);
    }
}
