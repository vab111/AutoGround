package com.example.autoground;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.guard.CommunicationService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kongqw.serialportlibrary.SerialPortManager;
import com.kongqw.serialportlibrary.listener.OnSerialPortDataListener;

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
    private AzjzData azjz;
    private List azjzData;
    private Button linkBtn;
    private SerialPortManager mSerialPortManager;
    private boolean issend = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal_setting);
        toolbar = findViewById(R.id.terminalbar);
        linkBtn = findViewById(R.id.button36);
        setToolbar();
        getRecord();
        getAzjz();
        mSerialPortManager = new SerialPortManager();
        mSerialPortManager.setOnSerialPortDataListener(new OnSerialPortDataListener() {
            @Override
            public void onDataReceived(byte[] bytes) {
                if (issend)
                    linkBtn.setText("断开连接");
            }

            @Override
            public void onDataSent(byte[] bytes) {

            }
        });
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
        if (!issend)
            issend = true;
        else {
            issend = false;
            linkBtn.setText("连接设备");
        }
        mService = CommunicationService.getInstance(this);
        //TODO 发送车辆信息
        byte[] id = new byte[4];
        id[0] = -64;
        id[1] = 32;
        id[2] = 0x00;
        id[3] = 0x00;
        byte[] order = new byte[8];
        order[0] = 0x23;
        order[1] = 0x00;
        order[2] = 0x20;
        order[3] = 0x00;

        order[4] = (byte) ((car.Frontwheel/256)&0xff);
        order[5] = (byte) ((car.Frontwheel%256)&0xff);
        order[6] = (byte) ((car.Backwheel/256)&0xff);
        order[7] = (byte) ((car.Backwheel%256)&0xff);

        mService.sendCan(id,order);

        order[3] = 0x01;
        order[4] = (byte) ((car.Zhou/256)&0xff);
        order[5] = (byte) ((car.Zhou%256)&0xff);
        order[6] = (byte) ((car.height/256)&0xff);
        order[7] = (byte) ((car.height%256)&0xff);
        mService.sendCan(id,order);

        order[1] = 0x01;
        order[3] = 0x00;
        order[4] = (byte) ((car.NJWidth/256)&0xff);
        order[5] = (byte) ((car.NJWidth%256)&0xff);
        order[6] = (byte) ((car.NJBackdis/256)&0xff);
        order[7] = (byte) ((car.NJBackdis%256)&0xff);
        mService.sendCan(id,order);

        order[3] = 0x01;
        order[4] = (byte) ((car.pianyi/256)&0xff);
        order[5] = (byte) ((car.pianyi%256)&0xff);
        order[6] = 0x00;
        if (car.leftright)
            order[7] = 0x00;
        else
            order[7] = 0x01;
        mService.sendCan(id,order);

        order[1] = 0x02;
        order[3] = 0x00;
        order[4] = (byte) ((car.TXHeight/256)&0xff);
        order[5] = (byte) ((car.TXHeight%256)&0xff);
        order[6] = (byte) ((car.TXBackdis/256)&0xff);
        order[7] = (byte) ((car.TXBackdis%256)&0xff);
        mService.sendCan(id,order);

        order[3] = 0x01;
        order[4] = (byte) ((car.TXMiddis/256)&0xff);
        order[5] = (byte) ((car.TXMiddis%256)&0xff);
        order[6] = 0x00;
        order[7] = 0x00;
        mService.sendCan(id,order);

        id[0] = -64;
        id[1] = 32;
        id[2] = 0x00;
        id[3] = 0x00;

        order[0] = 0x23;
        order[1] = 0x03;
        order[2] = 0x20;
        order[3] = 0x00;

        order[4] = (byte) ((azjz.txxz/256)&0xff);
        order[5] = (byte) ((azjz.txxz%256)&0xff);
        order[6] = (byte) ((azjz.AngleFix/256)&0xff);
        order[7] = (byte) ((azjz.AngleFix%256)&0xff);
        mService.sendCan(id,order);

    }
    private void getAzjz(){
        //TODO 获取历史信息

        azjzData = new ArrayList();
        File appDir = new File(Environment.getExternalStorageDirectory()+"/AutoGround");   //自定义的目录
        if (!appDir.exists()) {
            boolean isSuccess = appDir.mkdir();
            Log.d("MsgId:" ,"----------0------------------"+isSuccess);
        }
        else
            Log.d("MsgId:" ,"----------0------------------目录已经存在:"+Environment.getExternalStorageDirectory()+"/AutoGround");

        File fs = new File(Environment.getExternalStorageDirectory()+"/AutoGround/Azjzdata.json");
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
                azjzData = gson.fromJson(result, new TypeToken<List<AzjzData>>() {
                }.getType());
            }
        }
        else {
            try {
                FileOutputStream outputStream =new FileOutputStream(fs);
                OutputStreamWriter outStream = new OutputStreamWriter(outputStream);

                AzjzData header = new AzjzData();

                azjzData.add(header);
                Gson gson = new Gson();
                String jsonString = gson.toJson(azjzData);
                outStream.write(jsonString);

                outputStream.flush();
                outStream.flush();
                outputStream.close();
                outputStream.close();
                Toast.makeText(getBaseContext(), "File created successfully", Toast.LENGTH_LONG).show();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        //TODO 发送安装校准数据
        azjz = (AzjzData) azjzData.get(0);




    }
}
