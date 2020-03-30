package com.example.autoground;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.serialport.SerialPort;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

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
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.List;

import cn.qqtheme.framework.picker.SinglePicker;


public class MainActivity extends BaseActivity {

    
    private static final String TAG_SERVICE ="MainActivity" ;
    private MysurfaceView surfaceView;
    private SerialPortManager mSerialPortManager;
    private String buffer = "";
    private Button task;
    private CommunicationService mService;
    private TextView piancha;
    private boolean isTask = false;
    private boolean isA = false;
    private boolean isB = false;
    private int state=0;
    private Point startUp = new Point(0,0);
    private Point endUp = new Point(0,0);
    private String fileName;
    private RecordInfor recordInfor;
    private boolean isAuto = false;
    private Point curPoint = new Point(0,0);
    private List fileList;
    private ServiceConnection coon;
    private Button pointA;
    private Button pointB;
    private List azjzData;
    private TextView taskName;
    private TextView mianji;
    private TextView length;
    private Button toMid;
    private Button taskBtn;
    private TextView stars;
    private TextView chafen;
    private TextView stateText;
    private ImageView stateImg;
    private ImageView img_1;
    private ImageView img_2;
    private ImageView img_3;
    private ImageView img_4;
    private ImageView img_5;
    private ImageView img_6;
    private TextView JState;
    private TextView Ruler;
    private File serialDevice;
    private Button pyBtn;
    private Button mAbBtn;
    private TextView pindao;
    private int deltaT = -1;
    private List farmtoolList;
    private int farmtoolid;
    private int derectionFix;
    private boolean robbed = false;
    private byte warning_1 = 0x00;
    private byte warning_2 = 0x00;
    private byte warning_3 = 0x00;
    private byte warning_4 = 0x00;
    private TextView timeTotal;
    private double carDerection;
    private Button warningBtn;
    private TextView taskKuan;
    private int Jjpianyi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        surfaceView = findViewById(R.id.surfaceView);
        task = findViewById(R.id.Mode);
        piancha = findViewById(R.id.textView67);
        pointA = findViewById(R.id.piontA);
        pointB = findViewById(R.id.button13);
        taskName = findViewById(R.id.textView71);
        mianji = findViewById(R.id.textView72);
        length = findViewById(R.id.textView73);
        toMid = findViewById(R.id.button37);
        taskBtn = findViewById(R.id.newTask);
        stars = findViewById(R.id.textView66);
        chafen = findViewById(R.id.textView74);
        stateText = findViewById(R.id.textView82);
        stateImg = findViewById(R.id.stateImg);
        pindao = findViewById(R.id.textView152);
        img_1 = findViewById(R.id.stateImg2);
        img_2 = findViewById(R.id.stateImg3);
        img_3 = findViewById(R.id.stateImg4);
        img_4 = findViewById(R.id.stateImg5);
        img_5 = findViewById(R.id.stateImg6);
        img_6 = findViewById(R.id.stateImg7);
        JState = findViewById(R.id.textView65);
        pyBtn = findViewById(R.id.button28);
        mAbBtn = findViewById(R.id.button55);
        pyBtn.setVisibility(View.INVISIBLE);
        mAbBtn.setVisibility(View.INVISIBLE);
        Ruler = findViewById(R.id.textView165);
        timeTotal = findViewById(R.id.textView170);
        warningBtn = findViewById(R.id.button16);
        taskKuan = findViewById(R.id.textView173);
        checkPermission();
        initSerialPort();

        initCanService();
        coon = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
        final Intent intent = new Intent(this,HeartbeatService.class);
        bindService(intent,coon, Service.BIND_AUTO_CREATE);
        getAzjz();
        getRecord();
        getFarmtool();
        getPianyi();
    }
    Handler hmessage=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub

            //Toast.makeText(MainActivity.this,"方向盘被抢夺，退出自动！",Toast.LENGTH_SHORT).show();
            switch (msg.what)
            {
                case 0:
                    showWarning();
                    break;
                case 1:
                    showRTK();
                    break;
            }

        }
    };
    public String getGapTime(long time){

        long hours = time / (60 * 60);
        String diffTime="";
        if (hours>0) {
            diffTime = hours + "小时";
            time = time%(60*60);
            hours = time/60;
            if (hours>0)
            {
                diffTime+=(hours+"分");
                hours = time%60;
                diffTime+=(hours+"秒");
            }
            else
            {
                hours = time%60;
                diffTime+=(hours+"秒");
            }
        }
        else
        {
            time = time%(60*60);
            hours = time/60;
            if (hours>0)
            {
                diffTime+=(hours+"分");

                hours = time%60;
                diffTime+=(hours+"秒");
            }
            else
            {
                time = time%60;
                diffTime+=(time+"秒");
            }
        }
        return diffTime;
    }
    private void showWarning(){
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("警告")
                .setMessage("方向盘被抢夺，退出自动状态！")
                .setPositiveButton("确定", null)
                .show();
        try {
            Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
            mAlert.setAccessible(true);
            Object mAlertController = mAlert.get(dialog);
            Field mMessage = mAlertController.getClass().getDeclaredField("mMessageView");
            mMessage.setAccessible(true);
            TextView mMessageView = (TextView) mMessage.get(mAlertController);
            mMessageView.setTextColor(Color.RED);
            mMessageView.setTextSize(30);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
    private void showRTK(){
            AlertDialog dialog =  new AlertDialog.Builder(this)
                .setTitle("警告")
                .setMessage("退出RTK状态，结束自动驾驶！")
                .setPositiveButton("确定", null)
                .show();
        try {
            Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
            mAlert.setAccessible(true);
            Object mAlertController = mAlert.get(dialog);
            Field mMessage = mAlertController.getClass().getDeclaredField("mMessageView");
            mMessage.setAccessible(true);
            TextView mMessageView = (TextView) mMessage.get(mAlertController);
            mMessageView.setTextColor(Color.RED);
            mMessageView.setTextSize(30);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
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

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Ruler.setText(String.format("%dM", (int)(15/surfaceView.scale)));
                        }
                    });
                    String[] strArr = buffer.split(",");
                    if(strArr.length == 14) {
                        Log.e(TAG_SERVICE, "收到数据！");
                        int x = 0;
                        int y = 0;
                        String time1 = strArr[13].substring(strArr[13].length()-5);
                        int time = (int) ((Double.parseDouble(time1)*100)/100);
                        final float disu = (float) Double.parseDouble(strArr[4]);
                        if (!strArr[4].equals("")) {
                            if (isAuto)
                            {
                                if (disu>0.5f) {
                                    if (deltaT ==-1)
                                        deltaT = time;
                                    else {
                                        int t = (time+10-deltaT)%10;

                                            double juli = (t * disu) / 3.6;
                                            recordInfor.distance += juli;
                                            recordInfor.time += t;
                                            Log.e(TAG_SERVICE, String.format("%f", juli));
                                            recordInfor.square = recordInfor.distance * (recordInfor.Kuan / 100);
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mianji.setText(String.format("%.2f亩", recordInfor.square * 0.0015));
                                                    length.setText(String.format("%.2fm", recordInfor.distance));
                                                    timeTotal.setText(getGapTime(recordInfor.time));
                                                }
                                            });
                                            deltaT = time;

                                    }
                                }
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    chafen.setText(String.format("%.1fKm/h",disu));
                                }
                            });
                        }
                        if (!strArr[5].equals(""))
                            x = (int) (Double.parseDouble(strArr[5]) * 10);
                        if (!strArr[6].equals(""))
                            y = (int) (Double.parseDouble(strArr[6]) * 10);
                        curPoint.x = x;
                        curPoint.y = y;
                        double jiaodu = 0.0f;
                        if (!strArr[7].equals(""))
                            jiaodu = Double.parseDouble(strArr[7]);
                        carDerection = jiaodu;
                        jiaodu+=derectionFix;
                        surfaceView.handleData(new Point(x, y), jiaodu);

                            if (!strArr[11].equals(""))
                                x = (int) (Double.parseDouble(strArr[11]) * 10);
                            if (!strArr[12].equals(""))
                                y = (int) (Double.parseDouble(strArr[12]) * 10);

                            startUp.x = x;
                            startUp.y = y;

                        int star = 0;
                        if (!strArr[10].equals(""))
                            star = (int) Double.parseDouble(strArr[10]);
                        final int finalStar = star;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                stars.setText(String.format("%d", finalStar));
                            }
                        });
                        if (!strArr[9].equals(""))
                            x = (int) Double.parseDouble(strArr[9]);
                        if (subStringArr[0].equals("$KSXT"))
                        {
                            if ((x!=3)&&(isAuto)&&(state == 4))
                            {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        endAuto();
                                        hmessage.sendEmptyMessage(1);
                                        Toast.makeText(MainActivity.this, "等待RTK固定解！",Toast.LENGTH_SHORT);
                                    }
                                });
                            }
                            switch (x)
                            {
                                case 0:
                                    state = 0;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            JState.setText("未定位");
                                            stateImg.setImageResource(R.drawable.state_gray);
                                        }
                                    });
                                    break;
                                case 1:
                                    state = 0;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            JState.setText("单点解");
                                            stateImg.setImageResource(R.drawable.state_yellow);
                                        }
                                    });
                                    break;
                                case 2:
                                    state = 0;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            JState.setText("浮点解");
                                            stateImg.setImageResource(R.drawable.state_yellow);
                                        }
                                    });
                                    break;
                                case 3:
                                    state = 4;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            JState.setText("固定解");
                                            stateImg.setImageResource(R.drawable.state_green);
                                        }
                                    });
                                    break;
                            }
                        }
                        else
                        {
                            if(subStringArr[0].equals("$GPYBM"))
                            {
                                if ((x!=4)&&(isAuto)&&(state == 4))
                                {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            endAuto();
                                            hmessage.sendEmptyMessage(1);
                                            Toast.makeText(MainActivity.this, "等待RTK固定解！",Toast.LENGTH_SHORT);
                                        }
                                    });
                                }
                                switch (x)
                                {
                                    case 0:
                                        state = 0;
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                JState.setText("未定位");
                                                stateImg.setImageResource(R.drawable.state_gray);
                                            }
                                        });
                                        break;
                                    case 1:
                                        state = 0;
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                JState.setText("单点解");
                                                stateImg.setImageResource(R.drawable.state_yellow);
                                            }
                                        });
                                        break;
                                    case 4:
                                        state = 4;
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                JState.setText("固定解");
                                                stateImg.setImageResource(R.drawable.state_green);
                                            }
                                        });
                                        break;
                                    case 5:
                                        state = 0;
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                JState.setText("浮点解");
                                                stateImg.setImageResource(R.drawable.state_yellow);
                                            }
                                        });
                                        break;
                                    case 6:
                                        state = 0;
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                JState.setText("定位解");
                                                stateImg.setImageResource(R.drawable.state_yellow);
                                            }
                                        });
                                        break;

                                }
                            }
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
    public void Setting(View view) {
        if (isTask)
        {
            Toast.makeText(getBaseContext(), "请先结束当前任务！", Toast.LENGTH_LONG).show();
            return;
        }
        mSerialPortManager.closeSerialPort();
        Intent intent = new Intent();
        intent.setClass(MainActivity.this,SystemSetting.class);
        startActivityForResult(intent,RESULT_CANCELED);

    }

    public void history(View view) {
        if (isTask)
        {
            Toast.makeText(getBaseContext(), "请先结束当前任务！", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent();
        intent.setClass(MainActivity.this,history.class);
        mSerialPortManager.closeSerialPort();
        startActivityForResult(intent,RESULT_CANCELED);
    }

    public void newTask(View view) {
        //TODO 新建
        if (!isTask) {
            if (fileList.size()>31)
                delRecord();
            final Dialog bottomDialog = new Dialog(this, R.style.BottomDialog);
            final View contentView = LayoutInflater.from(this).inflate(R.layout.newtask, null);
            bottomDialog.setContentView(contentView);

            bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
            bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
            bottomDialog.setCanceledOnTouchOutside(false);
            bottomDialog.show();
            Button cancelBtn = contentView.findViewById(R.id.button18);
            final TextView type = contentView.findViewById(R.id.editText31);
            final TextView kuan = contentView.findViewById(R.id.editText32);
            final TextView abLine = contentView.findViewById(R.id.textView2);
            final Button confirmBtn = contentView.findViewById(R.id.button19);
            final Point pointA1 = new Point();
            final Point pointB1 = new Point();
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isA = false;
                    isB = false;
                    bottomDialog.dismiss();
                }
            });
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO 添加新的任务

                    TextView name = contentView.findViewById(R.id.editText30);
                    TextView type = contentView.findViewById(R.id.editText31);
                    TextView kuan = contentView.findViewById(R.id.editText32);
                    if (checkName(name.getText().toString())&&name.getText().toString().length()>0) {

                        newImgFolder(name.getText().toString());
                        recordInfor = new RecordInfor();
                        recordInfor.type = type.getText().toString();
                        if (type.getText().toString().length()==0)
                        {
                            Toast.makeText(getBaseContext(), "请选择作业类型！",Toast.LENGTH_SHORT).show();
                            return;
                        }

                        recordInfor.Kuan = Integer.parseInt(kuan.getText().toString());
                        recordInfor.isB = isB;
                        recordInfor.isA = isA;
                        updateRecord(name.getText().toString(), type.getText().toString());
                        if (isB)
                        {
                            heartData.heart[1]|=0x03;
                            recordInfor.pointA.x = pointA1.x;
                            recordInfor.pointA.y = pointA1.y;
                            recordInfor.pointB.x = pointB1.x;
                            recordInfor.pointB.y = pointB1.y;
                            pyBtn.setVisibility(View.VISIBLE);
                            mAbBtn.setVisibility(View.VISIBLE);
                            pointB.setVisibility(View.INVISIBLE);
                            pointA.setVisibility(View.INVISIBLE);
                            double jiaodu = Math.atan2(recordInfor.pointA.x-recordInfor.pointB.x,recordInfor.pointA.y-recordInfor.pointB.y);
                            Log.e("路径角度", String.format("%f",jiaodu));
                            int centerdis = getCenterDelta()/10;

                            Point a = new Point();
                            Point b = new Point();
                            a.x = (int) (recordInfor.pointA.x+(recordInfor.pointA.y-curPoint.y)*Math.tan(jiaodu)+(curPoint.x-recordInfor.pointA.x));
                            a.y = recordInfor.pointA.y;

                            b.x = (int) (recordInfor.pointB.x+(recordInfor.pointA.y-curPoint.y)*Math.tan(jiaodu)+(curPoint.x-recordInfor.pointA.x));
                            b.y = recordInfor.pointB.y;
                            surfaceView.setA(a);
                            surfaceView.setB(b);
                        }

                        initABLine(recordInfor, name.getText().toString());
                        fileName = name.getText().toString();
                        taskName.setText(fileName);
                        isTask = true;
                        surfaceView.CurrentTask = fileName;
                        surfaceView.ChanWidth = recordInfor.Kuan/10;
                        taskKuan.setText(String.format("当前作业宽度：%dCM", recordInfor.Kuan));
                        taskBtn.setBackgroundResource(R.drawable.stop);
                        byte[] id = new byte[4];
                        id[0] = -64;
                        id[1] = 32;
                        id[2] = (byte) 0xfe;
                        id[3] = 0x00;
                        byte[] order = new byte[8];
                        order[0] = 0x23;
                        order[1] = 0x04;
                        order[2] = 0x20;
                        order[3] = 0x00;
                        order[4] = 0x00;
                        if (type.equals("播种"))
                            order[5] = 0x00;

                        if (type.equals("打药"))
                            order[5] = 0x01;
                        if (type.equals("施肥"))
                            order[5] = 0x02;
                        if (type.equals("开沟"))
                            order[5] = 0x03;
                        if (type.equals("犁地"))
                            order[5] = 0x04;
                        order[6] = (byte) ((recordInfor.Kuan/256)&0xff);
                        order[7] = (byte) ((recordInfor.Kuan%256)&0xff);
                        mService.sendCan(id, order);

                        FarmTool item = (FarmTool) farmtoolList.get(farmtoolid);
                        order[1] = 0x01;
                        order[4] = (byte) (item.NJWidth/256&0xff);
                        order[5] = (byte) (item.NJWidth%256&0xff);
                        order[6] = (byte) (item.NJBackdis/256&0xff);
                        order[7] = (byte) (item.NJBackdis%256&0xff);
                        mService.sendCan(id, order);
                        order[3] = 0x01;
                        order[4] = (byte) (Math.abs(Jjpianyi)/256&0xff);
                        order[5] = (byte) (Math.abs(Jjpianyi)%256&0xff);
                        order[6] = 0x00;
                        if (Jjpianyi>0)
                            order[7] = 0x00;
                        else
                            order[7] = 0x01;
                        mService.sendCan(id, order);
                        if (isB) {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            order[1] = 0x05;
                            order[3] = 0x00;
                            int num = recordInfor.pointA.x * 10;
                            if (num < 0) {
                                order[7] = (byte) ((num % 256) & 0xff);
                                num /= 256;
                                order[6] = (byte) ((num % 256 - 1) & 0xff);
                                num /= 256;
                                order[5] = (byte) ((num % 256 - 1) & 0xff);
                                num /= 256;
                                order[4] = (byte) ((num % 256 - 1) & 0xff);
                            } else {
                                order[7] = (byte) ((num % 256) & 0xff);
                                num /= 256;
                                order[6] = (byte) ((num % 256) & 0xff);
                                num /= 256;
                                order[5] = (byte) ((num % 256) & 0xff);
                                num /= 256;
                                order[4] = (byte) ((num % 256) & 0xff);
                            }
                            mService.sendCan(id, order);
                            order[3] = 0x01;
                            num = recordInfor.pointA.y * 10;
                            if (num < 0) {
                                order[7] = (byte) ((num % 256) & 0xff);
                                num /= 256;
                                order[6] = (byte) ((num % 256 - 1) & 0xff);
                                num /= 256;
                                order[5] = (byte) ((num % 256 - 1) & 0xff);
                                num /= 256;
                                order[4] = (byte) ((num % 256 - 1) & 0xff);
                            } else {
                                order[7] = (byte) ((num % 256) & 0xff);
                                num /= 256;
                                order[6] = (byte) ((num % 256) & 0xff);
                                num /= 256;
                                order[5] = (byte) ((num % 256) & 0xff);
                                num /= 256;
                                order[4] = (byte) ((num % 256) & 0xff);
                            }
                            mService.sendCan(id, order);
                            order[3] = 0x02;
                            num = recordInfor.pointB.x * 10;
                            if (num < 0) {
                                order[7] = (byte) ((num % 256) & 0xff);
                                num /= 256;
                                order[6] = (byte) ((num % 256 - 1) & 0xff);
                                num /= 256;
                                order[5] = (byte) ((num % 256 - 1) & 0xff);
                                num /= 256;
                                order[4] = (byte) ((num % 256 - 1) & 0xff);
                            } else {
                                order[7] = (byte) ((num % 256) & 0xff);
                                num /= 256;
                                order[6] = (byte) ((num % 256) & 0xff);
                                num /= 256;
                                order[5] = (byte) ((num % 256) & 0xff);
                                num /= 256;
                                order[4] = (byte) ((num % 256) & 0xff);
                            }
                            mService.sendCan(id, order);
                            order[3] = 0x03;
                            num = recordInfor.pointB.y * 10;
                            if (num < 0) {
                                order[7] = (byte) ((num % 256) & 0xff);
                                num /= 256;
                                order[6] = (byte) ((num % 256 - 1) & 0xff);
                                num /= 256;
                                order[5] = (byte) ((num % 256 - 1) & 0xff);
                                num /= 256;
                                order[4] = (byte) ((num % 256 - 1) & 0xff);
                            } else {
                                order[7] = (byte) ((num % 256) & 0xff);
                                num /= 256;
                                order[6] = (byte) ((num % 256) & 0xff);
                                num /= 256;
                                order[5] = (byte) ((num % 256) & 0xff);
                                num /= 256;
                                order[4] = (byte) ((num % 256) & 0xff);
                            }
                            mService.sendCan(id, order);

                        }
                        if(state == 4&&!isA)
                            pointA.setBackgroundResource(R.drawable.setpointa);
                        if (state == 4 && !isB)
                            pointB.setBackgroundResource(R.drawable.setbp);
                        bottomDialog.dismiss();
                    } else {
                        Toast.makeText(getBaseContext(), "该任务已经存在，请重新命名！", Toast.LENGTH_LONG).show();
                        name.setText("");
                    }
                }


            });
            Button selectType = contentView.findViewById(R.id.button27);
            selectType.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final List data = new ArrayList<>();


                    for (int i=0;i<farmtoolList.size();i++)
                    {
                        FarmTool item = (FarmTool) farmtoolList.get(i);
                        data.add(item.NJType);
                    }

                    SinglePicker picker = new SinglePicker(MainActivity.this, data);
                    picker.setCanceledOnTouchOutside(false);
                    picker.setSelectedIndex(1);
                    picker.setCycleDisable(true);
                    picker.setOnItemPickListener(new SinglePicker.OnItemPickListener() {
                        @Override
                        public void onItemPicked(int i, Object o) {
                            type.setText(data.get(i).toString());
                            farmtoolid = i;
                            FarmTool item = (FarmTool) farmtoolList.get(i);
                            kuan.setText(String.format("%d", item.NJWidth));

                        }

                    });
                    picker.show();
                }
            });
            Button selectABL = contentView.findViewById(R.id.button3);
            selectABL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final List data = new ArrayList<>();
                    File appDir = new File(Environment.getExternalStorageDirectory() + "/AutoGround/ABLine");
                    final File[] files = appDir.listFiles();
                    for (int i=0;i<files.length;i++) {

                            data.add(files[i].getName());
                    }
                    if (data.size()==0)
                    {
                        Toast.makeText(getBaseContext(), "没有可用的AB线！", Toast.LENGTH_LONG).show();
                        return;
                    }
                    SinglePicker picker = new SinglePicker(MainActivity.this, data);
                    picker.setCanceledOnTouchOutside(false);
                    picker.setSelectedIndex(1);
                    picker.setCycleDisable(true);
                    picker.setOnItemPickListener(new SinglePicker.OnItemPickListener() {
                        @Override
                        public void onItemPicked(int i, Object o) {
                            abLine.setText(files[i].getName());
                            List taskList = new ArrayList();
                            ABLine abline = null;
                            File fs = new File(files[i].getAbsolutePath());
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
                                    taskList = gson.fromJson(result, new TypeToken<List<ABLine>>() {
                                    }.getType());
                                    abline = (ABLine) taskList.get(0);
                                }
                            }
                            pointA1.x = abline.pointA.x;
                            pointA1.y = abline.pointA.y;
                            pointB1.x = abline.pointB.x;
                            pointB1.y = abline.pointB.y;
                            isA = true;
                            isB = true;

                        }

                    });
                    picker.show();
                }
            });
        }
        else
        {
            if (isAuto)
            {
                Toast.makeText(getBaseContext(), "请先关闭自动驾驶！", Toast.LENGTH_SHORT);
                return;
            }
            surfaceView.isA = false;
            surfaceView.isB = false;
            surfaceView.isTask = false;

            saveTask();
            if (isB) {

                surfaceView.saveTask();
            }

            isTask = false;
            isA = false;
            isB = false;
            taskName.setText(null);
            mianji.setText(null);
            length.setText(null);
            timeTotal.setText(null);
            taskKuan.setText(null);
            pointA.setBackgroundResource(R.drawable.setpointa);
            pointB.setBackgroundResource(R.drawable.setbp);
            heartData.heart[1] = 0x00;


            taskBtn.setBackgroundResource(R.drawable.newtask);
            pointA.setVisibility(View.VISIBLE);
            pointB.setVisibility(View.VISIBLE);
            mAbBtn.setVisibility(View.INVISIBLE);
            pyBtn.setVisibility(View.INVISIBLE);
        }

    }

    private void initABLine(RecordInfor record,String name) {
        List recordlist = new ArrayList();
        File fs = new File(Environment.getExternalStorageDirectory()+"/AutoGround/"+name+".json");
        try {
            FileOutputStream outputStream =new FileOutputStream(fs);
            OutputStreamWriter outStream = new OutputStreamWriter(outputStream);


            recordlist.add(record);
            Gson gson = new Gson();
            String jsonString = gson.toJson(recordlist);
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
    private void saveTask()
    {

        List recordlist = new ArrayList();
        File fs = new File(Environment.getExternalStorageDirectory()+"/AutoGround/"+fileName+".json");

        try {
            FileOutputStream outputStream =new FileOutputStream(fs);
            OutputStreamWriter outStream = new OutputStreamWriter(outputStream);

            if(!recordInfor.isB)
                recordInfor.isA = false;
            recordlist.add(recordInfor);
            Gson gson = new Gson();
            String jsonString = gson.toJson(recordlist);
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
        for (int i=1;i<fileList.size();i++)
        {
            recorder item = (recorder) fileList.get(i);
            if (item.taskname.equals(fileName)) {

                item.mianji = recordInfor.square*0.0015+"亩";
            }

        }
        fs = new File(Environment.getExternalStorageDirectory()+"/AutoGround/Record.json");

        try {
            FileOutputStream outputStream =new FileOutputStream(fs);
            OutputStreamWriter outStream = new OutputStreamWriter(outputStream);



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
    private void saveABLine(){
        List recordlist = new ArrayList();

        long t = System.currentTimeMillis();
        File fs = new File(Environment.getExternalStorageDirectory()+"/AutoGround/ABLine/"+fileName+String.valueOf(t)+".json");
        try {
            FileOutputStream outputStream =new FileOutputStream(fs);
            OutputStreamWriter outStream = new OutputStreamWriter(outputStream);

            ABLine abLine = new ABLine();
            abLine.pointA.x = recordInfor.pointA.x;
            abLine.pointA.y = recordInfor.pointA.y;
            abLine.pointB.x = recordInfor.pointB.x;
            abLine.pointB.y = recordInfor.pointB.y;
            recordlist.add(abLine);
            Gson gson = new Gson();
            String jsonString = gson.toJson(recordlist);
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
    private void getRecord(){
        //TODO 获取历史信息

        fileList = new ArrayList();
        File appDir = new File(Environment.getExternalStorageDirectory()+"/AutoGround");   //自定义的目录
        if (!appDir.exists()) {
            boolean isSuccess = appDir.mkdir();
            Log.d("MsgId:" ,"----------0------------------"+isSuccess);
        }
        else
            Log.d("MsgId:" ,"----------0------------------目录已经存在:"+Environment.getExternalStorageDirectory()+"/AutoGround");
        appDir = new File(Environment.getExternalStorageDirectory()+"/AutoGround/ABLine");   //自定义的目录
        if (!appDir.exists()) {
            boolean isSuccess = appDir.mkdir();
            Log.d("MsgId:" ,"----------0------------------"+isSuccess);
        }
        else
            Log.d("MsgId:" ,"----------0------------------目录已经存在:"+Environment.getExternalStorageDirectory()+"/AutoGround/ABLine");

        File fs = new File(Environment.getExternalStorageDirectory()+"/AutoGround/Record.json");
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
                fileList = gson.fromJson(result, new TypeToken<List<recorder>>() {
                }.getType());
            }
        }
        else {
            try {
                FileOutputStream outputStream =new FileOutputStream(fs);
                OutputStreamWriter outStream = new OutputStreamWriter(outputStream);

                recorder header = new recorder();
                header.taskname = "作业名称";
                header.type = "作业类型";
                header.mianji = "任务面积";
                header.time = "作业时间";
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
        fs = new File(Environment.getExternalStorageDirectory()+"/AutoGround/CarInfor.json");
        if (fs.exists()) {

        }
        else {
            try {
                FileOutputStream outputStream =new FileOutputStream(fs);
                OutputStreamWriter outStream = new OutputStreamWriter(outputStream);

                CarInfor header = new CarInfor();
                List carlist = new ArrayList();
                carlist.add(header);
                Gson gson = new Gson();
                String jsonString = gson.toJson(carlist);
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
    private void getFarmtool(){
        //TODO 获取历史信息

        farmtoolList = new ArrayList();
        File appDir = new File(Environment.getExternalStorageDirectory()+"/AutoGround");   //自定义的目录
        if (!appDir.exists()) {
            boolean isSuccess = appDir.mkdir();
            Log.d("MsgId:" ,"----------0------------------"+isSuccess);
        }
        else
            Log.d("MsgId:" ,"----------0------------------目录已经存在:"+Environment.getExternalStorageDirectory()+"/AutoGround");

        File fs = new File(Environment.getExternalStorageDirectory()+"/AutoGround/Farmtool.json");
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
                farmtoolList = gson.fromJson(result, new TypeToken<List<FarmTool>>() {
                }.getType());
            }
        }
        else {
            try {
                FileOutputStream outputStream =new FileOutputStream(fs);
                OutputStreamWriter outStream = new OutputStreamWriter(outputStream);

                FarmTool item_1 = new FarmTool();
                item_1.NJType = "播种";
                item_1.NJWidth = 300;
                farmtoolList.add(item_1);
                FarmTool item_2 = new FarmTool();
                item_2.NJType = "打药";
                item_2.NJWidth = 300;
                farmtoolList.add(item_2);
                FarmTool item_3 = new FarmTool();
                item_3.NJType = "施肥";
                item_3.NJWidth = 300;
                farmtoolList.add(item_3);
                FarmTool item_4 = new FarmTool();
                item_4.NJType = "开沟";
                item_4.NJWidth = 300;
                farmtoolList.add(item_4);
                FarmTool item_5 = new FarmTool();
                item_5.NJType = "犁地";
                item_5.NJWidth = 300;
                farmtoolList.add(item_5);
                Gson gson = new Gson();
                String jsonString = gson.toJson(farmtoolList);
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
    public boolean checkName(String name) {
        for (int i=1;i<fileList.size();i++)
        {
            recorder item = (recorder) fileList.get(i);

            if (item.taskname.equals(name))
                return false;
        }
        return true;
    }
    public void updateRecord(String name,String type) {
                File fs = new File(Environment.getExternalStorageDirectory()+"/AutoGround/Record.json");
                Gson gson = new Gson();
                recorder header = new recorder();
                header.taskname = name;
                header.type = type;
                header.mianji = "0";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String current = sdf.format(System.currentTimeMillis());
                header.time = current;

                fileList.add(header);
                String jsonString = gson.toJson(fileList);
                try {
                    FileOutputStream outputStream =new FileOutputStream(fs);
                    OutputStreamWriter outStream = new OutputStreamWriter(outputStream);

                outStream.write(jsonString);

                outputStream.flush();
                outStream.flush();
                outputStream.close();
                outputStream.close();
                Toast.makeText(getBaseContext(), "记录保存成功！", Toast.LENGTH_LONG).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }






    }
    public void newImgFolder(String name) {
        File appDir = new File(Environment.getExternalStorageDirectory()+"/AutoGround/"+name);   //自定义的目录
        if (!appDir.exists()) {
            boolean isSuccess = appDir.mkdir();
            Log.d("MsgId:" ,"----------0------------------"+isSuccess);
        }
        else
            Log.d("MsgId:" ,"----------0------------------目录已经存在:"+Environment.getExternalStorageDirectory()+"/AutoGround/"+name);
    }
    private void checkPermission() {
        //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show();
            }
            //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        } else {
           // Toast.makeText(this, "授权成功！", Toast.LENGTH_SHORT).show();
            Log.e(TAG_SERVICE, "checkPermission: 已经授权！");
        }
    }

    public void setA(View view) {
        if (state!=4) {
            Toast.makeText(getBaseContext(), "等待进入RTK状态!", Toast.LENGTH_LONG).show();
            return;
        }
            if ((!isA)&&(state==4)&&isTask) {
                heartData.heart[1] |= 0x01;

                isA = true;
                recordInfor.isA = true;
                recordInfor.pointA.x = startUp.x;
                recordInfor.pointA.y = startUp.y;
                surfaceView.setA(curPoint);
                pointA.setBackgroundResource(R.drawable.setap_gray);
                byte[] order = new byte[4];
                order[0] = -32;
                order[1] = 64;
                order[2] = 0x00;
                order[3] = 0x00;
                mService.sendCan(order, heartData.heart);
            }
            else
            {
                if ((isA)&&!isB)
                {
                    heartData.heart[1]&=0xfe;
                    surfaceView.isA = false;
                    isA = false;
                    pointA.setBackgroundResource(R.drawable.setpointa);
                    byte[] order = new byte[4];
                    order[0] = -32;
                    order[1] = 64;
                    order[2] = 0x00;
                    order[3] = 0x00;
                    mService.sendCan(order, heartData.heart);
                }
            }
    }

    public void setB(View view) {
        if (state!=4) {
            Toast.makeText(getBaseContext(), "等待进入RTK状态!", Toast.LENGTH_LONG).show();
            return;
        }
        if (!isB&&isA) {
//TODO 添加AB点距离限制
            int x = curPoint.x- surfaceView.pointA.x;
            int y = curPoint.y - surfaceView.pointA.y;
            int distance = (int) Math.sqrt(x*x+y*y);
            if (distance<100)
            {
                Toast.makeText(getBaseContext(), "AB点距离务必在10米以上！", Toast.LENGTH_LONG).show();
                return;
            }
            else {
                heartData.heart[1] |= 0x02;
                isB = true;
                recordInfor.isB = true;
                pointB.setBackgroundResource(R.drawable.setbp);
                //平移AB点到中心位置
                double jiaodu = Math.atan2(recordInfor.pointA.x-startUp.x,recordInfor.pointA.y-startUp.y);

                Point a = new Point();
                Point b = new Point();
                a.x = (int) (recordInfor.pointA.x+(recordInfor.pointA.y-curPoint.y)*Math.tan(jiaodu)+(curPoint.x-recordInfor.pointA.x));
                a.y = recordInfor.pointA.y;

                b.x = (int) (startUp.x+(recordInfor.pointA.y-curPoint.y)*Math.tan(jiaodu)+(curPoint.x-recordInfor.pointA.x));
                b.y = startUp.y;
                surfaceView.setA(a);
                surfaceView.setB(b);
                recordInfor.pointA.x = a.x;
                recordInfor.pointA.y = a.y;
                recordInfor.pointB.x = b.x;
                recordInfor.pointB.y = b.y;
                saveABLine();
                pyBtn.setVisibility(View.VISIBLE);
                mAbBtn.setVisibility(View.VISIBLE);
                pointB.setVisibility(View.INVISIBLE);
                pointA.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void taskChange(View view) {
        if (state!=4){
            Toast.makeText(getBaseContext(), "等待进入RTK状态!", Toast.LENGTH_LONG).show();
            return;
        }
        if (!isB)
        {
            Toast.makeText(getBaseContext(), "请先设置AB线！", Toast.LENGTH_LONG).show();
            return;
        }
        if (!isAuto && isB)
        {//自动驾驶状态
            stateText.setText("自动");
            deltaT = -1;
            isAuto = true;
            task.setBackgroundResource(R.drawable.mt);
            heartData.heart[1] |=0x08;
            surfaceView.setTaskOn();
            byte[] order = new byte[4];
            order[0] = -32;
            order[1] = 64;
            order[2] = 0x00;
            order[3] = 0x00;
            mService.sendCan(order, heartData.heart);
        }
        else {
            if (isAuto) {
                isAuto = false;
                stateText.setText("手动");
                task.setBackgroundResource(R.drawable.at);
                heartData.heart[1] &= 0xf7;
                //添加已有路径到历史记录


                surfaceView.isTask = false;
                byte[] order = new byte[4];
                order[0] = -32;
                order[1] = 64;
                order[2] = 0x00;
                order[3] = 0x00;
                mService.sendCan(order, heartData.heart);
            }
        }

    }

    private void endAuto()
    {
        if (isAuto) {
            isAuto = false;
            stateText.setText("手动");
            task.setBackgroundResource(R.drawable.at);
            heartData.heart[1] &= 0xf7;
            //添加已有路径到历史记录


            surfaceView.isTask = false;
            byte[] order = new byte[4];
            order[0] = -32;
            order[1] = 64;
            order[2] = 0x00;
            order[3] = 0x00;
            mService.sendCan(order, heartData.heart);
        }
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
    public void handle(int id,byte[] data) {
        switch (id) {
            case 1793:
                //0x701

                        final int txxiuzheng = ((data[3] << 8) | (0xff&data[4]));
                        final int pindao1 = data[7];
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pindao.setText(String.format("频道：%d", pindao1));
                                piancha.setText(String.format("%d", txxiuzheng));
                                img_1.setImageResource(R.drawable.pianyi);
                                img_2.setImageResource(R.drawable.pianyi);
                                img_3.setImageResource(R.drawable.pianyi);
                                img_4.setImageResource(R.drawable.pianyi_1);
                                img_5.setImageResource(R.drawable.pianyi_1);
                                img_6.setImageResource(R.drawable.pianyi_1);
                                if (txxiuzheng>0)
                                {
                                    if (txxiuzheng>15)
                                    {
                                        img_6.setImageResource(R.drawable.pianyi_1_h);
                                    }
                                    else
                                    {
                                        if (txxiuzheng>10)
                                        {
                                            img_5.setImageResource(R.drawable.pianyi_1_h);
                                        }
                                        else
                                        {
                                            if (txxiuzheng>3)
                                            {
                                                img_4.setImageResource(R.drawable.pianyi_1_h);
                                            }
                                        }
                                    }
                                }
                                else
                                {
                                    if (txxiuzheng<-15)
                                    {
                                        img_1.setImageResource(R.drawable.pianyi_h);
                                    }
                                    else
                                    {
                                        if (txxiuzheng<-10)
                                        {
                                            img_2.setImageResource(R.drawable.pianyi_h);
                                        }
                                        else
                                        {
                                            if (txxiuzheng<-3)
                                            {
                                                img_3.setImageResource(R.drawable.pianyi_h);
                                            }
                                        }
                                    }
                                }
                            }
                        });
                        break;
            case 1538:
                //0x602
                switch (data[1])
                {
                    case 5:
                        //TODO 天线修正、转角修正
                        warning_1 = data[4];
                        warning_2 = data[5];
                        warning_3 = data[6];
                        warning_4 = data[7];
                        int result = warning_1+warning_2+warning_3+warning_4;
                        if (result>0)
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    warningBtn.setVisibility(View.VISIBLE);
                                }
                            });
                        else
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    warningBtn.setVisibility(View.INVISIBLE);
                                }
                            });
                        final int x = data[5]&0x10;
                        if (x!=0) {
                            if (!robbed) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        taskChange(null);
                                        hmessage.sendEmptyMessage(0);
                                    }
                                });
                                robbed = true;
                            }

                        }
                        else
                            robbed = false;
                        break;

                    default:
                        break;
                }
                break;
        }
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
        AzjzData item = (AzjzData) azjzData.get(0);
        derectionFix = -item.txxz/10;




    }



    public void tomid(View view) {
        //TODO 添加代码，当前位置回到正中间

        surfaceView.setMidShow();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getRecord();
        getFarmtool();
        getAzjz();
        getPianyi();
        initCanService();
        initSerialPort();
        if (data!=null) {
            fileName = data.getStringExtra("taskname");
            isTask = true;
            surfaceView.CurrentTask = fileName;
            taskBtn.setBackgroundResource(R.drawable.stop);
            getTask();
            mianji.setText(String.format("%.2f亩", recordInfor.square*0.0015f));
            length.setText(String.format("%.2fm", recordInfor.distance));
            timeTotal.setText(getGapTime(recordInfor.time));
            byte[] id = new byte[4];
            id[0] = -64;
            id[1] = 32;
            id[2] = (byte) 0xfe;
            id[3] = 0x00;
            byte[] order = new byte[8];
            order[0] = 0x23;
            order[1] = 0x04;
            order[2] = 0x20;
            order[3] = 0x00;
            order[4] = 0x00;
            if (recordInfor.type.equals("播种"))
                order[5] = 0x00;
            if (recordInfor.type.equals("打药"))
                order[5] = 0x01;
            if (recordInfor.type.equals("施肥"))
                order[5] = 0x02;
            if (recordInfor.type.equals("开沟"))
                order[5] = 0x03;
            if (recordInfor.type.equals("犁地"))
                order[5] = 0x04;
            order[6] = (byte) ((recordInfor.Kuan/256)&0xff);
            order[7] = (byte) ((recordInfor.Kuan%256)&0xff);
            taskKuan.setText(String.format("当前作业宽度：%dCM", recordInfor.Kuan));
            mService.sendCan(id, order);
            for (int i=0;i>farmtoolList.size();i++)
            {
                FarmTool item = (FarmTool) farmtoolList.get(i);
                if (item.NJType.equals(recordInfor.type))
                    farmtoolid = i;
            }
            FarmTool item = (FarmTool) farmtoolList.get(farmtoolid);
            order[1] = 0x01;
            order[4] = (byte) (item.NJWidth/256&0xff);
            order[5] = (byte) (item.NJWidth%256&0xff);
            order[6] = (byte) (item.NJBackdis/256&0xff);
            order[7] = (byte) (item.NJBackdis%256&0xff);
            mService.sendCan(id, order);

            order[3] = 0x01;
            order[4] = (byte) (Math.abs(Jjpianyi)/256&0xff);
            order[5] = (byte) (Math.abs(Jjpianyi)%256&0xff);
            order[6] = 0x00;
            if (Jjpianyi>0)
                order[7] = 0x00;
            else
                order[7] = 0x01;
            mService.sendCan(id, order);
            if (recordInfor.isA) {
                surfaceView.setA(recordInfor.pointA);
                heartData.heart[1]|=0x01;
                isA = true;
            }
            if (recordInfor.isB) {
                isB = true;
                heartData.heart[1]|=0x02;
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                double jiaodu = Math.atan2(recordInfor.pointA.x-recordInfor.pointB.x,recordInfor.pointA.y-recordInfor.pointB.y);
                Log.e("路径角度", String.format("%f",jiaodu));
                int centerdis = getCenterDelta()/10;

                Point a = new Point();
                Point b = new Point();
                a.x = (int) (recordInfor.pointA.x+(recordInfor.pointA.y-curPoint.y)*Math.tan(jiaodu)+(curPoint.x-recordInfor.pointA.x));
                a.y = recordInfor.pointA.y;

                b.x = (int) (recordInfor.pointB.x+(recordInfor.pointA.y-curPoint.y)*Math.tan(jiaodu)+(curPoint.x-recordInfor.pointA.x));
                b.y = recordInfor.pointB.y;
                surfaceView.setA(a);
                surfaceView.setB(b);


                pointB.setVisibility(View.INVISIBLE);
                pointA.setVisibility(View.INVISIBLE);
                mAbBtn.setVisibility(View.VISIBLE);
                pyBtn.setVisibility(View.VISIBLE);
                surfaceView.loadBuffer();

                id[0] = -32;
                id[1] = 64;
                id[2] = 0x00;
                id[3] = 0x00;
                mService.sendCan(id, heartData.heart);
                id[0] = -64;
                id[1] = 32;
                id[2] = (byte) 0xfe;
                id[3] = 0x00;

                order[0] = 0x23;
                order[1] = 0x04;
                order[2] = 0x20;
                order[3] = 0x00;
                order[4] = 0x00;
                order[5] = 0x00;
                int num = recordInfor.Kuan;
                order[6] = (byte) ((num/256)&0xff);
                order[7] = (byte) ((num%256)&0xff);
                mService.sendCan(id, order);

                order[1] = 0x05;
                num = recordInfor.pointA.x*10;
                if (num<0)
                {
                    order[7] = (byte) ((num % 256) & 0xff);
                    num /= 256;
                    order[6] = (byte) ((num % 256-1) & 0xff);
                    num /= 256;
                    order[5] = (byte) ((num % 256-1) & 0xff);
                    num /= 256;
                    order[4] = (byte) ((num % 256-1) & 0xff);
                }
                else {
                    order[7] = (byte) ((num % 256) & 0xff);
                    num /= 256;
                    order[6] = (byte) ((num % 256) & 0xff);
                    num /= 256;
                    order[5] = (byte) ((num % 256) & 0xff);
                    num /= 256;
                    order[4] = (byte) ((num % 256) & 0xff);
                }
                mService.sendCan(id, order);
                order[3] = 0x01;
                num = recordInfor.pointA.y*10;
                if (num<0)
                {
                    order[7] = (byte) ((num % 256) & 0xff);
                    num /= 256;
                    order[6] = (byte) ((num % 256-1) & 0xff);
                    num /= 256;
                    order[5] = (byte) ((num % 256-1) & 0xff);
                    num /= 256;
                    order[4] = (byte) ((num % 256-1) & 0xff);
                }
                else {
                    order[7] = (byte) ((num % 256) & 0xff);
                    num /= 256;
                    order[6] = (byte) ((num % 256) & 0xff);
                    num /= 256;
                    order[5] = (byte) ((num % 256) & 0xff);
                    num /= 256;
                    order[4] = (byte) ((num % 256) & 0xff);
                }
                mService.sendCan(id, order);
                order[3] = 0x02;
                num = recordInfor.pointB.x*10;
                if (num<0)
                {
                    order[7] = (byte) ((num % 256) & 0xff);
                    num /= 256;
                    order[6] = (byte) ((num % 256-1) & 0xff);
                    num /= 256;
                    order[5] = (byte) ((num % 256-1) & 0xff);
                    num /= 256;
                    order[4] = (byte) ((num % 256-1) & 0xff);
                }
                else {
                    order[7] = (byte) ((num % 256) & 0xff);
                    num /= 256;
                    order[6] = (byte) ((num % 256) & 0xff);
                    num /= 256;
                    order[5] = (byte) ((num % 256) & 0xff);
                    num /= 256;
                    order[4] = (byte) ((num % 256) & 0xff);
                }
                mService.sendCan(id, order);
                order[3] = 0x03;
                num = recordInfor.pointB.y*10;
                if (num<0)
                {
                    order[7] = (byte) ((num % 256) & 0xff);
                    num /= 256;
                    order[6] = (byte) ((num % 256-1) & 0xff);
                    num /= 256;
                    order[5] = (byte) ((num % 256-1) & 0xff);
                    num /= 256;
                    order[4] = (byte) ((num % 256-1) & 0xff);
                }
                else {
                    order[7] = (byte) ((num % 256) & 0xff);
                    num /= 256;
                    order[6] = (byte) ((num % 256) & 0xff);
                    num /= 256;
                    order[5] = (byte) ((num % 256) & 0xff);
                    num /= 256;
                    order[4] = (byte) ((num % 256) & 0xff);
                }
                mService.sendCan(id, order);

            }



        }
    }

    public void getTask() {
        List taskList = new ArrayList();
        File fs = new File(Environment.getExternalStorageDirectory()+"/AutoGround/"+fileName+".json");
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
                taskList = gson.fromJson(result, new TypeToken<List<RecordInfor>>() {
                }.getType());
                recordInfor = (RecordInfor) taskList.get(0);
            }
        }
        taskName.setText(fileName);
    }

    public void zoomOut(View view) {
        if (surfaceView.scale+1<5.0f)
        {
            surfaceView.scale+=1.0f;
        }
        else
        {
            surfaceView.scale = 5.0f;
        }
    }

    public void zoomIn(View view) {
        if (surfaceView.scale-1>1.0f)
        {
            surfaceView.scale-=1.0f;
        }
        else
            surfaceView.scale = 1.0f;
    }

    public void signalTest(View view) {
        if (isTask)
        {
            Toast.makeText(getBaseContext(), "请先结束当前任务！", Toast.LENGTH_LONG).show();
            return;
        }
        mSerialPortManager.closeSerialPort();
        startActivity(MainActivity.this, Signal.class);
    }

    public void setabpy(View view) {
        //设置AB线偏移
        final Dialog bottomDialog = new Dialog(this, R.style.BottomDialog);
        final View contentView = LayoutInflater.from(this).inflate(R.layout.ablinemove, null);
        bottomDialog.setContentView(contentView);

        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        bottomDialog.setCanceledOnTouchOutside(false);
        bottomDialog.show();
        final Button cancelBtn = contentView.findViewById(R.id.button56);
        final TextView type = contentView.findViewById(R.id.editText33);
        final Button confirmBtn = contentView.findViewById(R.id.button57);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomDialog.dismiss();
            }
        });
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.getText().toString().length()==0) {
                    bottomDialog.dismiss();
                    return;
                }
                int x = Integer.parseInt(type.getText().toString());

                byte[] id = new byte[4];
                id[0] = -64;
                id[1] = 32;
                id[2] = 0x00;
                id[3] = 0x00;
                byte[] order = new byte[8];
                order[0] = 0x23;
                order[1] = 0x04;
                order[2] = 0x20;
                order[3] = 0x01;
                order[6] = 0x00;
                order[7] = 0x00;

                order[4] = (byte) ((x/256)&0xff);
                if(x<0)
                    order[4] = (byte) ((x/256-1)&0xff);
                order[5] = (byte) ((x%256)&0xff);

                double jiaodu = Math.atan2(recordInfor.pointA.x-recordInfor.pointB.x,recordInfor.pointA.y-recordInfor.pointB.y);
                jiaodu = (Math.toDegrees(jiaodu)+360)%360;
                carDerection = (630-carDerection-derectionFix)%360;
                float y = x;
                int result = (int) Math.abs(jiaodu-carDerection);
                if ((result<50)||(result>310))
                    y = -y;

                y/=10;
                jiaodu = Math.toRadians(jiaodu);
                surfaceView.pointA.x-= (Math.sin(jiaodu+Math.PI/2)*y);
                surfaceView.pointA.y-=(y*Math.cos(jiaodu+Math.PI/2));

                surfaceView.pointB.x-=(Math.sin(jiaodu+Math.PI/2)*y);
                surfaceView.pointB.y-=(y*Math.cos(jiaodu+Math.PI/2));
                mService.sendCan(id, order);
                bottomDialog.dismiss();
            }
        });
    }
    public void initCanService()
    {
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
        mAbBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        heartData.heart[1]|=0x10;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        double jiaodu = Math.atan2(recordInfor.pointA.x-recordInfor.pointB.x,recordInfor.pointA.y-recordInfor.pointB.y);
                        Log.e("路径角度", String.format("%f",jiaodu));
                        int centerdis = getCenterDelta()/10;

                        Point a = new Point();
                        Point b = new Point();
                        a.x = (int) (recordInfor.pointA.x+(recordInfor.pointA.y-curPoint.y)*Math.tan(jiaodu)+(curPoint.x-recordInfor.pointA.x));
                        a.y = recordInfor.pointA.y;

                        b.x = (int) (recordInfor.pointB.x+(recordInfor.pointA.y-curPoint.y)*Math.tan(jiaodu)+(curPoint.x-recordInfor.pointA.x));
                        b.y = recordInfor.pointB.y;
                        surfaceView.pointA.x = a.x;
                        surfaceView.pointA.y = a.y;
                        surfaceView.pointB.x = b.x;
                        surfaceView.pointB.y = b.y;
                        break;
                    case MotionEvent.ACTION_UP:
                        heartData.heart[1] &=0xef;
                        break;

                }
                return false;
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {			switch (keyCode) {
        case KeyEvent.KEYCODE_BACK:
            AlertDialog.Builder build = new AlertDialog.Builder(this);
            build.setTitle("系统提示").setMessage("确定要退出吗？");
            build.setPositiveButton("确定",
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    unbindService(coon);

                    mSerialPortManager.closeSerialPort();
                    System.exit(0);
                }
            });
            build.setNegativeButton("取消",
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();				break;
    }
    return super.onKeyDown(keyCode, event);
    }
    private int getCenterDelta()
    {
        List fileList = new ArrayList();
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


        CarInfor car = (CarInfor) fileList.get(0);
        return car.TXMiddis;
    }

    public void showWarning(View view) {
        final Dialog bottomDialog = new Dialog(this, R.style.BottomDialog);
        final View contentView = LayoutInflater.from(this).inflate(R.layout.warningalert, null);
        bottomDialog.setContentView(contentView);

        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        //bottomDialog.setCanceledOnTouchOutside(false);
        bottomDialog.show();
        ImageView img1 = contentView.findViewById(R.id.imageView31);
        ImageView img2 = contentView.findViewById(R.id.imageView22);
        ImageView img3 = contentView.findViewById(R.id.imageView23);
        ImageView img4 = contentView.findViewById(R.id.imageView24);
        ImageView img5 = contentView.findViewById(R.id.imageView25);
        ImageView img6 = contentView.findViewById(R.id.imageView26);
        ImageView img7 = contentView.findViewById(R.id.imageView27);
        ImageView img8 = contentView.findViewById(R.id.imageView28);
        ImageView img9 = contentView.findViewById(R.id.imageView29);
        ImageView img10 = contentView.findViewById(R.id.imageView30);
        ImageView img11 = contentView.findViewById(R.id.imageView21);
        ImageView img12 = contentView.findViewById(R.id.imageView32);
        ImageView img13 = contentView.findViewById(R.id.imageView33);
        ImageView img14 = contentView.findViewById(R.id.imageView34);
        ImageView img15 = contentView.findViewById(R.id.imageView35);
        ImageView img16 = contentView.findViewById(R.id.imageView36);
        ImageView img17 = contentView.findViewById(R.id.imageView37);
        ImageView img18 = contentView.findViewById(R.id.imageView38);
        Button confirm = contentView.findViewById(R.id.button17);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomDialog.dismiss();
            }
        });
        int x = warning_1&0x01;
        if(x==0)
            img1.setBackgroundColor(Color.GREEN);
        else
            img1.setBackgroundColor(Color.RED);
        x = warning_1&0x02;
        if(x==0)
            img2.setBackgroundColor(Color.GREEN);
        else
            img2.setBackgroundColor(Color.RED);
        x = warning_1&0x04;
        if(x==0)
            img3.setBackgroundColor(Color.GREEN);
        else
            img3.setBackgroundColor(Color.RED);
        x = warning_1&0x08;
        if(x==0)
            img4.setBackgroundColor(Color.GREEN);
        else
            img4.setBackgroundColor(Color.RED);
        x = warning_1&0x10;
        if(x==0)
            img5.setBackgroundColor(Color.GREEN);
        else
            img5.setBackgroundColor(Color.RED);
        x = warning_1&0x20;
        if(x==0)
            img6.setBackgroundColor(Color.GREEN);
        else
            img6.setBackgroundColor(Color.RED);
        x = warning_1&0x40;
        if(x==0)
            img7.setBackgroundColor(Color.GREEN);
        else
            img7.setBackgroundColor(Color.RED);
        x = warning_1&0x80;
        if(x==0)
            img8.setBackgroundColor(Color.GREEN);
        else
            img8.setBackgroundColor(Color.RED);
        x = warning_2&0x01;
        if(x==0)
            img9.setBackgroundColor(Color.GREEN);
        else
            img9.setBackgroundColor(Color.RED);
        x = warning_2&0x02;
        if(x==0)
            img10.setBackgroundColor(Color.GREEN);
        else
            img10.setBackgroundColor(Color.RED);
        x = warning_2&0x04;
        if(x==0)
            img11.setBackgroundColor(Color.GREEN);
        else
            img11.setBackgroundColor(Color.RED);
        x = warning_2&0x08;
        if(x==0)
            img12.setBackgroundColor(Color.GREEN);
        else
            img12.setBackgroundColor(Color.RED);
        x = warning_2&0x10;
        if(x==0)
            img13.setBackgroundColor(Color.GREEN);
        else
            img13.setBackgroundColor(Color.RED);
        x = warning_2&0x20;
        if(x==0)
            img14.setBackgroundColor(Color.GREEN);
        else
            img14.setBackgroundColor(Color.RED);
        x = warning_2&0x40;
        if(x==0)
            img15.setBackgroundColor(Color.GREEN);
        else
            img15.setBackgroundColor(Color.RED);
        x = warning_2&0x80;
        if(x==0)
            img16.setBackgroundColor(Color.GREEN);
        else
            img16.setBackgroundColor(Color.RED);
        x = warning_3&0x01;
        if(x==0)
            img17.setBackgroundColor(Color.GREEN);
        else
            img17.setBackgroundColor(Color.RED);
        x = warning_2&0x02;
        if(x==0)
            img18.setBackgroundColor(Color.GREEN);
        else
            img18.setBackgroundColor(Color.RED);
    }
    private void delRecord()
    {
        recorder item = (recorder) fileList.get(1);
        File appDir = new File(Environment.getExternalStorageDirectory() + "/AutoGround/" + item.taskname);
        if (appDir.exists()) {
            File[] files = appDir.listFiles();

            boolean flag = true;
            for (int i = 0; i < files.length; i++) {
                // 删除子文件
                if (files[i].isFile()) {
                    flag = deleteFile(files[i].getAbsolutePath());
                    if (!flag)
                        break;
                }

            }

            appDir.delete();
        }
        String abline = Environment.getExternalStorageDirectory() + "/AutoGround/" + item.taskname+".json";
        deleteFile(abline);
        fileList.remove(1);
        saveRecord();
    }
    private void saveRecord(){
        Gson gson = new Gson();
        String jsonString = gson.toJson(fileList);

        FileOutputStream fileOut=null;
        OutputStreamWriter outStream =null;
        try
        {
            fileOut =new FileOutputStream(Environment.getExternalStorageDirectory()+"/AutoGround/Record.json",false);
            outStream =new OutputStreamWriter(fileOut);
            outStream.write(jsonString);
            outStream.flush();
            outStream.close();
            fileOut.close();
            Toast.makeText(MainActivity.this,"删除成功！",Toast.LENGTH_SHORT).show();
        }
        catch(Exception e)
        {
            Toast.makeText(MainActivity.this,"删除失败！",Toast.LENGTH_SHORT).show();
        }
        finally
        {
            try
            {
                if(null!=outStream)
                    outStream.close();

                if(null!=fileOut)
                    fileOut.close();
            }
            catch(Exception e)
            {
            }
        }
    }
    private void getPianyi(){
        CarAcess carAcess = new CarAcess();
        carAcess.getCar();
        Jjpianyi = carAcess.car.pianyi;

    }
}
