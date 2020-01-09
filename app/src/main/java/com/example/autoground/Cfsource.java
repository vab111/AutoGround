package com.example.autoground;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;

import com.android.guard.CommunicationService;
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

import cn.qqtheme.framework.picker.SinglePicker;

public class Cfsource extends AppCompatActivity {

    private Toolbar toolbar;
    private RadioButton p900module;
    private RadioButton h800module;
    private CommunicationService mService;
    private int pindao=0;
    private CarInfor car;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cfsource);
        toolbar = findViewById(R.id.cfsourcebar);
        p900module = findViewById(R.id.radioButton3);
        h800module = findViewById(R.id.radioButton4);
        setToolbar();
        mService = CommunicationService.getInstance(this);
        getRecord();
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
                byte[] id = new byte[4];
                id[0] = -64;
                id[1] = 32;
                id[2] = 0x00;
                id[3] = 0x00;
                byte[] order = new byte[8];
                order[0] = 0x23;
                order[1] = 0x06;
                order[2] = 0x20;
                order[3] = 0x00;
                order[4] = 0x00;
                if (p900module.isChecked())
                    order[5] = 0x01;
                else
                    order[5] = 0x00;
                order[6] = 0x00;
                order[7] = (byte) (pindao&0xff);
                mService.sendCan(id, order);
                save();
                finish();
            }
        });
    }


    public void h800click(View view) {
        car.signal_type = 0;
        p900module.setChecked(false);
        List data = new ArrayList<>();
        for (int i = 0;i<10;i++)
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
                pindao = i;
                car.signal_fre = i;
            }

        });
        picker.show();
    }


    public void p900click(View view) {
        car.signal_type = 1;
        p900module.setChecked(true);
        h800module.setChecked(false);
        List data = new ArrayList<>();
        for (int i = 0;i<10;i++)
        {
            data.add(String.valueOf(i));
        }
        SinglePicker picker = new SinglePicker(this, data);
        picker.setCanceledOnTouchOutside(false);
        picker.setSelectedIndex(car.signal_fre);
        picker.setCycleDisable(false);
        picker.setOnItemPickListener(new SinglePicker.OnItemPickListener() {
            @Override
            public void onItemPicked(int i, Object o) {
                pindao = i;
                car.signal_fre = i;
            }

        });
        picker.show();
    }
    private void getRecord(){
        //TODO 获取历史信息

        List fileList = new ArrayList();
        File appDir = new File(Environment.getExternalStorageDirectory()+"/AutoGround");   //自定义的目录
        if (!appDir.exists()) {
            boolean isSuccess = appDir.mkdir();
            Log.d("MsgId:" ,"----------0------------------"+isSuccess);
        }
        else
            Log.d("MsgId:" ,"----------0------------------目录已经存在:"+Environment.getExternalStorageDirectory()+"/AutoGround");

        File fs = new File(Environment.getExternalStorageDirectory()+"/AutoGround/CarInfor.json");

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
                fileList = gson.fromJson(result, new TypeToken<List<CarInfor>>() {
                }.getType());
            }


        car = (CarInfor) fileList.get(0);
    }
    public void save()
    {   List fileList = new ArrayList();
        File fs = new File(Environment.getExternalStorageDirectory()+"/AutoGround/CarInfor.json");
        try {
            FileOutputStream outputStream =new FileOutputStream(fs);
            OutputStreamWriter outStream = new OutputStreamWriter(outputStream);



            fileList.add(car);
            Gson gson = new Gson();
            String jsonString = gson.toJson(fileList);
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
}
