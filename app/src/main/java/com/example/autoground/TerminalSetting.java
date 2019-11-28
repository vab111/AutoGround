package com.example.autoground;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

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

public class TerminalSetting extends BaseActivity {

    private CommunicationService mService;
    private Toolbar toolbar;
    private CarInfor car;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal_setting);
        toolbar = findViewById(R.id.terminalbar);
        setToolbar();
        getRecord();
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
        if (fs.exists()) {
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
        }
        else {
            try {
                FileOutputStream outputStream =new FileOutputStream(fs);
                OutputStreamWriter outStream = new OutputStreamWriter(outputStream);

                CarInfor header = new CarInfor();

                fileList.add(header);
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
        car = (CarInfor) fileList.get(0);
    }

    public void linking(View view) {
        mService = CommunicationService.getInstance(this);
        //TODO 发送车辆信息
    }
}
