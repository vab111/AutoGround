package com.example.autoground;

import androidx.appcompat.widget.Toolbar;

import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kongqw.serialportlibrary.Device;
import com.kongqw.serialportlibrary.SerialPortFinder;
import com.kongqw.serialportlibrary.SerialPortManager;
import com.kongqw.serialportlibrary.listener.OnOpenSerialPortListener;
import com.kongqw.serialportlibrary.listener.OnSerialPortDataListener;

import java.io.File;
import java.util.ArrayList;

public class Signal extends BaseActivity {

    private Toolbar toolbar;
    private SerialPortManager mSerialPortManager;
    private File serialDevice;
    private String buffer = "";
    private TextView jingduText;
    private TextView weiduText;
    private TextView derectionText;
    private TextView fuyangText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signal);
        toolbar = findViewById(R.id.signalbar);
        setToolbar();
        jingduText = findViewById(R.id.textView127);
        weiduText = findViewById(R.id.textView129);
        derectionText = findViewById(R.id.textView143);
        fuyangText = findViewById(R.id.textView145);
        initSerialPort();
    }
    private void setToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);//设计隐藏标题
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//设置显示返回键
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // finish();
                mSerialPortManager.closeSerialPort();
                finish();
            }
        });
    }

    private void initSerialPort(){
        mSerialPortManager = new SerialPortManager();
        SerialPortFinder serialPortFinder = new SerialPortFinder();
        ArrayList<Device> devices = serialPortFinder.getDevices();
        for (int i=0;i<devices.size();i++)
        {
            Device item = devices.get(i);
            if (item.getName().equals("ttyS4")) {
                serialDevice = item.getFile();
                mSerialPortManager.openSerialPort(item.getFile(), 115200);
            }
        }
        heartData.heart[0] = 0x05;

        mSerialPortManager.setOnOpenSerialPortListener(new OnOpenSerialPortListener() {
            @Override
            public void onSuccess(File device) {

            }

            @Override
            public void onFail(File device, Status status) {

            }
        });
        mSerialPortManager.setOnSerialPortDataListener(new OnSerialPortDataListener() {
            @Override
            public void onDataReceived(byte[] bytes) {
                String s = new String(bytes);
                String[] subStringArr = s.split(",");
                if (subStringArr[0].equals("$KSXT")||subStringArr[0].equals("$GPYBM"))
                {


                    final String[] strArr = buffer.split(",");
                    if(strArr.length == 14) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                jingduText.setText(strArr[2]);
                                weiduText.setText(strArr[1]);
                                fuyangText.setText(strArr[8]);
                                derectionText.setText(strArr[7]);
                            }
                        });

                    }
                    buffer = s;
                }
                else {
                    buffer+=s;

                }



            }

            @Override
            public void onDataSent(byte[] bytes) {

            }
        });
    }
}
