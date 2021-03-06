package com.example.autoground;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.android.guard.CommunicationService;
import com.android.guard.DataType;
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

public class CanshuJZ extends BaseActivity {

    private Toolbar toolbar;
    private zhuanjiaohuan zjhFragment;
    private hangxianghuan hxhFragment;
    private julihuan jlhFragment;
    private suduhuan sdhFragment;
    private CanshuInfo canshuInfo;
    private CommunicationService mService;
    private boolean aniu = false;

    private Button zjhBtn;
    private Button hxhBtn;
    private Button jlhBtn;
    private Button sdhBtn;
    private boolean isEdit = false;
    private SoftKeyBoardListener softKeyBoardListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canshu_jz);
        toolbar = findViewById(R.id.canshubar);
        zjhBtn= findViewById(R.id.button47);
        hxhBtn= findViewById(R.id.button48);
        jlhBtn= findViewById(R.id.button49);
        sdhBtn = findViewById(R.id.button51);
        setToolbar();
        getRecord();
        initFragment5();
        initFragment6();
        initFragment8();
        initFragment4();
        btnGray();
        zjhBtn.setBackgroundColor(Color.GREEN);
        mService = CommunicationService.getInstance(this);
        mService.getData(new CommunicationService.IProcessData() {
            @Override
            public void process(byte[] bytes, DataType dataType) {
                Log.e("CanshuJZ", "收到数据！");

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
        softKeyBoardListener = new SoftKeyBoardListener(this);        //软键盘状态监听
        softKeyBoardListener.setListener(new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                //软键盘已经显示，做逻辑
                isEdit = true;
                 }
                 @Override
                 public void keyBoardHide(int height) {                //软键盘已经隐藏,做逻辑
                     isEdit = false;
                 }
        });

    }
    public void onStart()
    {
        super.onStart();
        zjhFragment.maxlimit.setText(String.valueOf(canshuInfo.zjh_maxlimit).toCharArray(), 0, String.valueOf(canshuInfo.zjh_maxlimit).length());
        zjhFragment.maxwucha.setText(String.valueOf(canshuInfo.zjh_maxwucha).toCharArray(), 0, String.valueOf(canshuInfo.zjh_maxwucha).length());
        zjhFragment.maxoutput.setText(String.valueOf(canshuInfo.zjh_max_output).toCharArray(), 0, String.valueOf(canshuInfo.zjh_max_output).length());
        zjhFragment.percentP.setText(String.valueOf(canshuInfo.zjh_biliP).toCharArray(), 0, String.valueOf(canshuInfo.zjh_biliP).length());
        //zjhFragment.amiDianliu.setText(String.valueOf(canshuInfo.zjh_ami).toCharArray(), 0, String.valueOf(canshuInfo.zjh_ami).length());
        hxhFragment.maxlimit.setText(String.valueOf(canshuInfo.hxh_maxlimit).toCharArray(), 0, String.valueOf(canshuInfo.hxh_maxlimit).length());
        hxhFragment.maxwucha.setText(String.valueOf(canshuInfo.hxh_maxwucha).toCharArray(), 0, String.valueOf(canshuInfo.hxh_maxwucha).length());
        hxhFragment.maxoutput.setText(String.valueOf(canshuInfo.hxh_max_output).toCharArray(), 0, String.valueOf(canshuInfo.hxh_max_output).length());
        hxhFragment.percentP.setText(String.valueOf(canshuInfo.hxh_biliP).toCharArray(), 0, String.valueOf(canshuInfo.hxh_biliP).length());
       // hxhFragment.amiDianliu.setText(String.valueOf(canshuInfo.hxh_ami).toCharArray(), 0, String.valueOf(canshuInfo.hxh_ami).length());
        jlhFragment.maxlimit.setText(String.valueOf(canshuInfo.jlh_maxlimit).toCharArray(), 0, String.valueOf(canshuInfo.jlh_maxlimit).length());
        jlhFragment.maxwucha.setText(String.valueOf(canshuInfo.jlh_maxwucha).toCharArray(), 0, String.valueOf(canshuInfo.jlh_maxwucha).length());
        jlhFragment.maxoutput.setText(String.valueOf(canshuInfo.jlh_max_output).toCharArray(), 0, String.valueOf(canshuInfo.jlh_max_output).length());
        jlhFragment.percentP.setText(String.valueOf(canshuInfo.jlh_biliP).toCharArray(), 0, String.valueOf(canshuInfo.jlh_biliP).length());
       // jlhFragment.dianliuWucha.setText(String.valueOf(canshuInfo.jlh_ami).toCharArray(), 0, String.valueOf(canshuInfo.jlh_ami).length());
        sdhFragment.maxlimit.setText(String.valueOf(canshuInfo.sdh_mid).toCharArray(), 0, String.valueOf(canshuInfo.sdh_mid).length());

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
        switch (id) {
            case 1793:
                //0x701

                final int txxiuzheng = ((data[5] << 8) | (0xff&data[6]));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        zjhFragment.actDianliu.setText(String.format("%.2f°", (float)txxiuzheng/100));
                        if (!isEdit) {
                            int x = (int) (100 * (Float.parseFloat(zjhFragment.amiDianliu.getText().toString())));
                            zjhFragment.dianliuWucha.setText(String.format("%.2f°", (float) (txxiuzheng - x) / 100));
                        }

                    }
                });

                final int hxCheck = ((data[1] << 8) | (0xff&data[2]));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hxhFragment.actDianliu.setText(String.format("%.2f°", (float)hxCheck/100));
                        if (!isEdit) {
                            int x = (int) (100 * (Float.parseFloat(hxhFragment.amiDianliu.getText().toString())));
                            hxhFragment.dianliuWucha.setText(String.format("%.2f°", (float) (hxCheck - x) / 100));
                        }
                    }
                });
                break;
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

                //TODO 发送车辆信息601
                byte[] id = new byte[4];
                id[0] = -64;
                id[1] = 32;
                id[2] = (byte) 0xfe;
                id[3] = 0x00;
                byte[] order = new byte[8];
                order[0] = 0x23;
                order[1] = 0x07;
                order[2] = 0x20;
                order[3] = 0x02;
                order[4] = 0x00;
                order[5] = 0x00;
                order[6] = 0x00;
                order[7] = 0x00;
                mService.sendCan(id, order);
                order[1] = 0x08;
                mService.sendCan(id, order);
                order[1] = 0x09;
                mService.sendCan(id,order);
                finish();
            }
        });
    }

    private void initFragment4(){

        //开启事务，fragment的控制是由事务来实现的

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();



        //第一种方式（add），初始化fragment并添加到事务中，如果为null就new一个

        if(zjhFragment == null){

            zjhFragment = new zhuanjiaohuan();
            transaction.add(R.id.canshu_frame, zjhFragment);
        }
        hideFragment(transaction);
        transaction.show(zjhFragment);
        transaction.commit();

    }
    private void initFragment5(){

        //开启事务，fragment的控制是由事务来实现的

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();



        //第一种方式（add），初始化fragment并添加到事务中，如果为null就new一个

        if(hxhFragment == null){

            hxhFragment = new hangxianghuan();
            transaction.add(R.id.canshu_frame, hxhFragment);
        }
        hideFragment(transaction);
        transaction.show(hxhFragment);
        transaction.commit();

    }
    private void initFragment6(){

        //开启事务，fragment的控制是由事务来实现的

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();



        //第一种方式（add），初始化fragment并添加到事务中，如果为null就new一个

        if(jlhFragment == null){

            jlhFragment = new julihuan();
            transaction.add(R.id.canshu_frame, jlhFragment);
        }
        hideFragment(transaction);
        transaction.show(jlhFragment);
        transaction.commit();

    }
    private void initFragment8(){

        //开启事务，fragment的控制是由事务来实现的

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();



        //第一种方式（add），初始化fragment并添加到事务中，如果为null就new一个

        if(sdhFragment == null){

            sdhFragment = new suduhuan();
            transaction.add(R.id.canshu_frame, sdhFragment);
        }
        hideFragment(transaction);
        transaction.show(sdhFragment);
        transaction.commit();

    }
    private void hideFragment(FragmentTransaction transaction){

        if(zjhFragment != null){

            transaction.hide(zjhFragment);

        }
        if(hxhFragment != null){

            transaction.hide(hxhFragment);

        }
        if(jlhFragment != null){

            transaction.hide(jlhFragment);

        }
        if(sdhFragment != null){

            transaction.hide(sdhFragment);

        }



    }


    public void zjh(View view) {
        initFragment4();
        btnGray();
        zjhBtn.setBackgroundColor(Color.GREEN);
    }

    public void hxh(View view) {
        initFragment5();
        btnGray();
        hxhBtn.setBackgroundColor(Color.GREEN);
    }

    public void jlh(View view) {
        initFragment6();
        btnGray();
        jlhBtn.setBackgroundColor(Color.GREEN);
    }

    public void sdh(View view) {
        initFragment8();
        btnGray();
        sdhBtn.setBackgroundColor(Color.GREEN);
    }

    public void btnGray()
    {
        zjhBtn.setBackgroundColor(Color.GRAY);
        hxhBtn.setBackgroundColor(Color.GRAY);
        jlhBtn.setBackgroundColor(Color.GRAY);
        sdhBtn .setBackgroundColor(Color.GRAY);

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

        File fs = new File(Environment.getExternalStorageDirectory()+"/AutoGround/CarsInfor.json");
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
                fileList = gson.fromJson(result, new TypeToken<List<CanshuInfo>>() {
                }.getType());
            }
        }
        else {
            try {
                FileOutputStream outputStream =new FileOutputStream(fs);
                OutputStreamWriter outStream = new OutputStreamWriter(outputStream);

                CanshuInfo header = new CanshuInfo();

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
        canshuInfo = (CanshuInfo) fileList.get(0);
    }
    private void saveRecord()
    {
        List fileList = new ArrayList();
        File fs = new File(Environment.getExternalStorageDirectory()+"/AutoGround/CarsInfor.json");
        try {
            FileOutputStream outputStream =new FileOutputStream(fs);
            OutputStreamWriter outStream = new OutputStreamWriter(outputStream);

            fileList.add(canshuInfo);
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

    public void zjhTest(View view) {
        //TODO 发送车辆信息601
        byte[] id = new byte[4];
        id[0] = -64;
        id[1] = 32;
        id[2] = (byte) 0xfe;
        id[3] = 0x00;
        byte[] order = new byte[8];
        order[0] = 0x23;
        order[1] = 0x07;
        order[2] = 0x20;
        order[3] = 0x02;
        int mubiao = (int) (100*(Float.parseFloat(zjhFragment.amiDianliu.getText().toString())));

        if (mubiao<0)
            order[4] = (byte) ((mubiao/256-1)&0xff);

        else
            order[4] = (byte) ((mubiao/256)&0xff);

        order[5] = (byte) ((mubiao % 256) & 0xff);

        order[6] = 0x00;
        if (!aniu)
        {
            order[7] = 0x01;
            aniu = true;
            zjhFragment.testBtn.setText("取消");
        }
        else
        {
            order[7] = 0x00;
            aniu =false;

            zjhFragment.testBtn.setText("测试");
        }

        mService.sendCan(id,order);
        Log.e("转角设置:", String.valueOf(mubiao));
    }

    public void hxhTest(View view) {
        //TODO 发送车辆信息601
        byte[] id = new byte[4];
        id[0] = -64;
        id[1] = 32;
        id[2] = 0x00;
        id[3] = 0x00;
        byte[] order = new byte[8];
        order[0] = 0x23;
        order[1] = 0x08;
        order[2] = 0x20;
        order[3] = 0x02;
        int mubiao = (int) (100*(Float.parseFloat(hxhFragment.amiDianliu.getText().toString())));
        if (mubiao<0)
            order[4] = (byte) ((mubiao/256-1)&0xff);

        else
            order[4] = (byte) ((mubiao/256)&0xff);

        order[5] = (byte) ((mubiao % 256) & 0xff);
        order[6] = 0x00;
        if (!aniu)
        {
            order[7] = 0x01;
            aniu = true;
            hxhFragment.testBtn.setText("取消");
        }
        else
        {
            order[7] = 0x00;
            aniu =false;

            hxhFragment.testBtn.setText("测试");
        }

        mService.sendCan(id,order);
        Log.e("航向设置:", String.valueOf(mubiao));
    }

    public void jlhTest(View view) {
        //TODO 发送车辆信息601
        byte[] id = new byte[4];
        id[0] = -64;
        id[1] = 32;
        id[2] = 0x00;
        id[3] = 0x00;
        byte[] order = new byte[8];
        order[0] = 0x23;
        order[1] = 0x09;
        order[2] = 0x20;
        order[3] = 0x02;
        int mubiao = (int) (100*(Float.parseFloat(jlhFragment.dianliuWucha.getText().toString())));
        if (mubiao<0)
            order[4] = (byte) ((mubiao/256-1)&0xff);

        else
            order[4] = (byte) ((mubiao/256)&0xff);

        order[5] = (byte) ((mubiao % 256) & 0xff);
        order[6] = 0x00;
        if (!aniu)
        {
            order[7] = 0x01;
            aniu = true;
            jlhFragment.testBtn.setText("取消");
        }
        else
        {
            order[7] = 0x00;
            aniu =false;

            jlhFragment.testBtn.setText("测试");
        }

        mService.sendCan(id,order);
        Log.e("转角设置:", String.valueOf(mubiao));
    }

    public void zjhSend(View view) {
        byte[] id = new byte[4];
        id[0] = -64;
        id[1] = 32;
        id[2] = (byte) 0xfe;
        id[3] = 0x00;
        byte[] order = new byte[8];
        order[0] = 0x23;
        order[1] = 0x07;
        order[2] = 0x20;
        order[3] = 0x00;
        int mubiao = (int) (100*(Float.parseFloat(zjhFragment.maxlimit.getText().toString())));
        canshuInfo.zjh_maxlimit = Float.parseFloat(zjhFragment.maxlimit.getText().toString());
        order[4] = (byte) ((mubiao/256)&0xff);

        order[5] = (byte) ((mubiao % 256) & 0xff);
        mubiao = (int) (100*(Float.parseFloat(zjhFragment.maxwucha.getText().toString())));
        canshuInfo.zjh_maxwucha = Float.parseFloat(zjhFragment.maxwucha.getText().toString());
        order[6] = (byte) ((mubiao/256)&0xff);

        order[7] = (byte) ((mubiao % 256) & 0xff);


        mService.sendCan(id,order);

        order[3] = 0x01;
        mubiao = (int) (100*(Float.parseFloat(zjhFragment.maxoutput.getText().toString())));
        canshuInfo.zjh_max_output = Float.parseFloat(zjhFragment.maxoutput.getText().toString());
        order[4] = (byte) ((mubiao/256)&0xff);

        order[5] = (byte) ((mubiao % 256) & 0xff);
        mubiao = (int) (100*(Float.parseFloat(zjhFragment.percentP.getText().toString())));
        canshuInfo.zjh_biliP = Float.parseFloat(zjhFragment.percentP.getText().toString());
        order[6] = (byte) ((mubiao/256)&0xff);

        order[7] = (byte) ((mubiao % 256) & 0xff);
        mService.sendCan(id,order);
        saveRecord();
    }

    public void hxhSend(View view) {
        byte[] id = new byte[4];
        id[0] = -64;
        id[1] = 32;
        id[2] = (byte) 0xfe;
        id[3] = 0x00;
        byte[] order = new byte[8];
        order[0] = 0x23;
        order[1] = 0x08;
        order[2] = 0x20;
        order[3] = 0x00;
        int mubiao = (int) (100*(Float.parseFloat(hxhFragment.maxlimit.getText().toString())));
        canshuInfo.hxh_maxlimit = Float.parseFloat(hxhFragment.maxlimit.getText().toString());
        order[4] = (byte) ((mubiao/256)&0xff);

        order[5] = (byte) ((mubiao % 256) & 0xff);
        mubiao = (int) (100*(Float.parseFloat(hxhFragment.maxwucha.getText().toString())));
        canshuInfo.hxh_maxwucha = Float.parseFloat(hxhFragment.maxwucha.getText().toString());
        order[6] = (byte) ((mubiao/256)&0xff);

        order[7] = (byte) ((mubiao % 256) & 0xff);


        mService.sendCan(id,order);

        order[3] = 0x01;
        mubiao = (int) (100*(Float.parseFloat(hxhFragment.maxoutput.getText().toString())));
        canshuInfo.hxh_max_output = Float.parseFloat(hxhFragment.maxoutput.getText().toString());
        order[4] = (byte) ((mubiao/256)&0xff);

        order[5] = (byte) ((mubiao % 256) & 0xff);
        mubiao = (int) (100*(Float.parseFloat(hxhFragment.percentP.getText().toString())));
        canshuInfo.hxh_biliP = Float.parseFloat(hxhFragment.percentP.getText().toString());
        order[6] = (byte) ((mubiao/256)&0xff);

        order[7] = (byte) ((mubiao % 256) & 0xff);
        mService.sendCan(id,order);
        saveRecord();
    }

    public void jlhSend(View view) {
        byte[] id = new byte[4];
        id[0] = -64;
        id[1] = 32;
        id[2] = (byte) 0xfe;
        id[3] = 0x00;
        byte[] order = new byte[8];
        order[0] = 0x23;
        order[1] = 0x09;
        order[2] = 0x20;
        order[3] = 0x00;
        int mubiao = (int) (100*(Float.parseFloat(jlhFragment.maxlimit.getText().toString())));
        canshuInfo.jlh_maxlimit = Float.parseFloat(jlhFragment.maxlimit.getText().toString());
        order[4] = (byte) ((mubiao/256)&0xff);

        order[5] = (byte) ((mubiao % 256) & 0xff);
        mubiao = (int) (100*(Float.parseFloat(jlhFragment.maxwucha.getText().toString())));
        canshuInfo.jlh_maxwucha = Float.parseFloat(jlhFragment.maxwucha.getText().toString());
        order[6] = (byte) ((mubiao/256)&0xff);

        order[7] = (byte) ((mubiao % 256) & 0xff);


        mService.sendCan(id,order);

        order[3] = 0x01;
        mubiao = (int) (100*(Float.parseFloat(jlhFragment.maxoutput.getText().toString())));
        canshuInfo.jlh_max_output = Float.parseFloat(jlhFragment.maxoutput.getText().toString());

        order[4] = (byte) ((mubiao/256)&0xff);

        order[5] = (byte) ((mubiao % 256) & 0xff);
        mubiao = (int) (100*(Float.parseFloat(jlhFragment.percentP.getText().toString())));
        canshuInfo.jlh_biliP = Float.parseFloat(jlhFragment.percentP.getText().toString());
        order[6] = (byte) ((mubiao/256)&0xff);

        order[7] = (byte) ((mubiao % 256) & 0xff);
        mService.sendCan(id,order);
        saveRecord();
    }

    public void sdhmid(View view) {
        float mid = Float.parseFloat(sdhFragment.maxlimit.getText().toString());
        canshuInfo.sdh_mid = mid;
        byte[] id = new byte[4];
        id[0] = -64;
        id[1] = 32;
        id[2] = (byte) 0xfe;
        id[3] = 0x00;
        byte[] order = new byte[8];
        order[0] = 0x23;
        order[1] = 0x0a;
        order[2] = 0x20;
        order[3] = 0x02;
        int num = (int) (mid*10);
        order[4] = (byte) (num/256&0xff);
        order[5] = (byte) (num%256&0xff);
        mService.sendCan(id, order);
        saveRecord();
    }
}
