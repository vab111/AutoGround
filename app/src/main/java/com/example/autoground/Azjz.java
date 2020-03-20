package com.example.autoground;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.guard.Command;
import com.android.guard.CommunicationService;
import com.android.guard.DataType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kongqw.serialportlibrary.Device;
import com.kongqw.serialportlibrary.SerialPortFinder;
import com.kongqw.serialportlibrary.SerialPortManager;
import com.kongqw.serialportlibrary.listener.OnOpenSerialPortListener;
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
    private CommunicationService mService;
    private List azjzData;
    private SerialPortManager mSerialPortManager;
    private File serialDevice;
    private String buffer="";
    private TextView alertText;

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
        alertText = findViewById(R.id.textView175);
        setToolbar();
        initSerialPort();
        try {
            mService = CommunicationService.getInstance(this);
            mService.setShutdownCountTime(12);//setting shutdownCountTime
            mService.bind();
            mService.getData(new CommunicationService.IProcessData() {
                @Override
                public void process(byte[] bytes, DataType dataType) {
                    Log.e("CanTest", "收到数据！");
                    switch (dataType)
                    {
                        case TDataCan:
                            //we get can data
                            //handle can data

                            handleCanData(bytes);
                            break;

                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        loadData();
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

                mSerialPortManager.closeSerialPort();
                finish();
            }
        });
    }
    private void handleCanData(byte[] bytes) {
        byte[] id = new byte[4];
        System.arraycopy(bytes, 1, id, 0, id.length);//ID
        byte[] data = null;
        int frameFormatType = (id[3] & 0x06);
        int frameFormat = 0;
        int frameType = 0;
        long extendid = 0;
        switch (frameFormatType) {
            case 0://标准数据
                frameFormat = 0;
                frameType = 0;
                extendid = (((((id[0]&0xff)<<24)|((id[1]&0xff)<<16)|((id[2]&0xff)<<8)|((id[3]&0xff)))&0xFFFFFFFFl)>>21);//bit31-bit21: 标准ID
                int dataLength = bytes[5];
                data = new byte[dataLength];
                System.arraycopy(bytes, 6, data, 0, dataLength);
                handle((int) extendid, data);
                break;
            case 2://标准远程
                frameFormat = 0;
                frameType = 1;
                extendid = (((((id[0]&0xff)<<24)|((id[1]&0xff)<<16)|((id[2]&0xff)<<8)|((id[3]&0xff)))&0xFFFFFFFFl)>>21);//bit31-bit21: 标准ID
                break;
            case 4://扩展数据
                frameFormat = 1;
                frameType = 0;
                extendid = (((((id[0]&0xff)<<24)|((id[1]&0xff)<<16)|((id[2]&0xff)<<8)|((id[3]&0xff)))&0xFFFFFFFFl)>>3);//bit31-bit3: 扩展ID
                int dataLengthExtra = bytes[5];
                data = new byte[dataLengthExtra];
                System.arraycopy(bytes, 6, data, 0, dataLengthExtra);
                break;
            case 6://扩展远程
                frameFormat = 1;
                frameType = 1;
                extendid = (((((id[0]&0xff)<<24)|((id[1]&0xff)<<16)|((id[2]&0xff)<<8)|((id[3]&0xff)))&0xFFFFFFFFl)>>3);//bit31-bit3: 扩展ID
                break;
        }

    }
    public void handle(int id,byte[] data)
    {
        final AzjzData item = (AzjzData) azjzData.get(0);
        byte[] order = new byte[4];
        order[0] = -80;
        order[1] = 64;
        order[2] = 0x00;
        order[3] = 0x00;
        byte[] ACK = new byte[8];
        ACK[0] = 0x60;
        ACK[2] = 0x20;
        ACK[3] = 0x00;
        ACK[4] = 0x00;
        ACK[5] = 0x00;
        ACK[6] = 0x00;
        ACK[7] = 0x00;
        switch (id) {
            case 1538:
                //0x602
                switch (data[1])
                {
                    case 0:
                        //TODO 天线修正、转角修正

                        final int txxiuzheng = ((data[4] << 8) | (0xff&data[5]));

                        final int zhuanjiaoxiuzheng = ((data[6] << 8) | (0xff&data[7]));
                        ACK[1] = 0x00;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                float jiaodu1 = (float) txxiuzheng/10;
                                float jiaodu2 = (float)zhuanjiaoxiuzheng/10;
                                txxz.setText(String.format("%.1f°", jiaodu1));
                                AngleFix.setText(String.format("%.1f°", jiaodu2));
                                item.txxz = txxiuzheng;
                                item.AngleFix = zhuanjiaoxiuzheng;
                            }
                        });
                        break;
                    case 1:
                        //TODO 直线方向、平均航向
                        final int txxiuzheng1 = ((data[4] << 8) | (0xff&data[5]));
                        final int zhuanjiaoxiuzheng1 = ((data[6] << 8) | (0xff&data[7]));
                        ACK[1] = 0x01;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                float jiaodu1 = (float) txxiuzheng1/10;
                                float jiaodu2 = (float)zhuanjiaoxiuzheng1/10;
                                zxDerection.setText(String.format("%.1f°", jiaodu1));
                                avgHang.setText(String.format("%.1f°", jiaodu2));
                                item.zxDerection = txxiuzheng1;
                                item.avgHang = zhuanjiaoxiuzheng1;
                            }
                        });
                        break;
                    case 2:
                        //TODO 车头方向、车头平均
                        final int txxiuzheng2 = ((data[4] << 8) | (0xff&data[5]));
                        final int zhuanjiaoxiuzheng2 = ((data[6] << 8) | (0xff&data[7]));
                        ACK[1] = 0x02;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                float jiaodu1 = (float) txxiuzheng2/10;
                                float jiaodu2 = (float)zhuanjiaoxiuzheng2/10;
                                headDerection.setText(String.format("%.1f°", jiaodu1));
                                avgHead.setText(String.format("%.1f°", jiaodu2));
                                item.headDerection = txxiuzheng2;
                                item.avgHead = zhuanjiaoxiuzheng2;
                            }
                        });
                        break;
                    case 3:
                        //TODO 车轮转角、转角平均
                        final int txxiuzheng3 = ((data[4] << 8) | (0xff&data[5]));
                        final int zhuanjiaoxiuzheng3 = ((data[6] << 8) | (0xff&data[7]));
                        ACK[1] = 0x03;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                float jiaodu1 = (float) txxiuzheng3/10;
                                float jiaodu2 = (float)zhuanjiaoxiuzheng3/10;
                                wheelAngle.setText(String.format("%.1f°", jiaodu1));
                                avgAngle.setText(String.format("%.1f°", jiaodu2));
                                item.wheelAngle = txxiuzheng3;
                                item.avgAngle = zhuanjiaoxiuzheng3;
                            }
                        });
                        break;
                        default:
                            break;
                }
                mService.sendCan(order, ACK);
                break;
        }
    }

    public void startJZ(View view) {
        heartData.heart[01]|=0x04;
        byte[] order = new byte[4];
        order[0] = -32;
        order[1] = 64;
        order[2] = 0x00;
        order[3] = 0x00;
        mService.sendCan(order, heartData.heart);
        alertText.setText("正在校准中...");
    }

    public void endJZ(View view) {
        heartData.heart[01]&=0xfb;
        byte[] order = new byte[4];
        order[0] = -32;
        order[1] = 64;
        order[2] = 0x00;
        order[3] = 0x00;
        mService.sendCan(order, heartData.heart);
        alertText.setText(null);
    }

    public void loadData()
    {
        azjzData = new ArrayList();

        File fs = new File(Environment.getExternalStorageDirectory()+"/AutoGround/Azjzdata.json");

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
            AzjzData item = (AzjzData) azjzData.get(0);
        float jiaodu1 = (float) item.txxz/10;
        txxz.setText(String.format("%.1f°", jiaodu1));
        jiaodu1 = (float) item.AngleFix/10;
        AngleFix.setText(String.format("%.1f°", jiaodu1));
        jiaodu1 = (float) item.zxDerection/10;
        zxDerection.setText(String.format("%.1f°", jiaodu1));
        jiaodu1 = (float) item.avgHang/10;
        avgHang.setText(String.format("%.1f°", jiaodu1));
        jiaodu1 = (float) item.wheelAngle/10;
        wheelAngle.setText(String.format("%.1f°", jiaodu1));
        jiaodu1 = (float) item.avgAngle/10;
        avgAngle.setText(String.format("%.1f°", jiaodu1));
        jiaodu1 = (float) item.headDerection/10;
        headDerection.setText(String.format("%.1f°", jiaodu1));
        jiaodu1 = (float) item.avgHead/10;
        avgHead.setText(String.format("%.1f°", jiaodu1));
    }
    private void saveJZ()
    {
        AzjzData item = (AzjzData) azjzData.get(0);
        String data = txxz.getText().toString();
        String[] strArr = data.split("°");
        int num = (int) (10*Float.parseFloat(strArr[0]));
        item.txxz = num;

        data = AngleFix.getText().toString();
        strArr = data.split("°");
        num = (int) (10*Float.parseFloat(strArr[0]));
        item.AngleFix = num;

        data = zxDerection.getText().toString();
        strArr = data.split("°");
        num = (int) (10*Float.parseFloat(strArr[0]));
        item.zxDerection = num;

        data = avgAngle.getText().toString();
        strArr = data.split("°");
        num = (int) (10*Float.parseFloat(strArr[0]));
        item.avgAngle = num;

        data = avgHead.getText().toString();
        strArr = data.split("°");
        num = (int) (10*Float.parseFloat(strArr[0]));
        item.avgHead = num;

        data = avgHang.getText().toString();
        strArr = data.split("°");
        num = (int) (10*Float.parseFloat(strArr[0]));
        item.avgHang = num;

        data = headDerection.getText().toString();
        strArr = data.split("°");
        num = (int) (10*Float.parseFloat(strArr[0]));
        item.headDerection = num;

        data = wheelAngle.getText().toString();
        strArr = data.split("°");
        num = (int) (10*Float.parseFloat(strArr[0]));
        item.wheelAngle = num;
        File fs = new File(Environment.getExternalStorageDirectory()+"/AutoGround/Azjzdata.json");
        try {
            FileOutputStream outputStream =new FileOutputStream(fs);
            OutputStreamWriter outStream = new OutputStreamWriter(outputStream);


            Gson gson = new Gson();
            String jsonString = gson.toJson(azjzData);
            outStream.write(jsonString);

            outputStream.flush();
            outStream.flush();
            outputStream.close();
            outputStream.close();
            Toast.makeText(getBaseContext(), "数据保存成功！", Toast.LENGTH_LONG).show();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void initSerialPort()
    {
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


                    String[] strArr = buffer.split(",");
                    if((strArr.length == 14)&&((strArr[0].equals("$KSXT")||subStringArr[0].equals("$GPYBM")))) {

                        final float disu = (float) Double.parseDouble(strArr[4]);
                        if (!strArr[4].equals("")) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    curSpeed.setText(String.format("%.1fKm/h",disu));
                                }
                            });
                        }


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
    public void sendJZ(View view) {
        AzjzData item = (AzjzData) azjzData.get(0);
        mService = CommunicationService.getInstance(this);
        //TODO 发送车辆信息
        byte[] id = new byte[4];
        id[0] = -64;
        id[1] = 32;
        id[2] = 0x00;
        id[3] = 0x00;

        byte[] order = new byte[8];
        order[0] = 0x23;
        order[1] = 0x03;
        order[2] = 0x20;
        order[3] = 0x00;

        order[4] = (byte) ((item.txxz/256)&0xff);
        order[5] = (byte) ((item.txxz%256)&0xff);
        int num = item.txxz;
        if (num<0)
        {
            order[5] = (byte) ((num % 256) & 0xff);
            num /= 256;
            order[4] = (byte) ((num % 256-1) & 0xff);
        }
        order[6] = (byte) ((item.AngleFix/256)&0xff);
        order[7] = (byte) ((item.AngleFix%256)&0xff);
        mService.sendCan(id,order);
        saveJZ();

    }
}
