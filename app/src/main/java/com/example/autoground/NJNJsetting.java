package com.example.autoground;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

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

public class NJNJsetting extends BaseActivity {
    private CarSetFragment clszFragment;
    private Farmingtool farmFragment;
    private Toolbar toolbar;
    private CarInfor car;
    private CommunicationService mService;
    private Button clszBtn;
    private Button njszBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_njnjsetting);
        toolbar = findViewById(R.id.njnjbar);
        clszBtn = findViewById(R.id.button14);
        njszBtn = findViewById(R.id.button15);
        setToolbar();
        initFragment2();
        initFragment1();
        btnGray();
        clszBtn.setBackgroundColor(Color.GREEN);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        mService = CommunicationService.getInstance(this);
        mService.setShutdownCountTime(12);//setting shutdownCountTime
        try {
            mService.bind();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void btnGray()
    {
        clszBtn.setBackgroundColor(Color.GRAY);
        njszBtn.setBackgroundColor(Color.GRAY);
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




        //显示第一个fragment

    private void initFragment1(){

            //开启事务，fragment的控制是由事务来实现的

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();



            //第一种方式（add），初始化fragment并添加到事务中，如果为null就new一个

            if(clszFragment == null){

                clszFragment = new CarSetFragment();

                transaction.add(R.id.njsetting_frame, clszFragment);


            }

            //隐藏所有fragment

            hideFragment(transaction);

            //显示需要显示的fragment

            transaction.show(clszFragment);



            //第二种方式(replace)，初始化fragment

//        if(f1 == null){

//            f1 = new MyFragment("消息");

//        }

//        transaction.replace(R.id.main_frame_layout, f1);



            //提交事务

            transaction.commit();

        }



        //显示第二个fragment

    private void initFragment2(){

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();



            if(farmFragment == null){

                farmFragment = new Farmingtool();

                transaction.add(R.id.njsetting_frame,farmFragment);



            }

            hideFragment(transaction);

            transaction.show(farmFragment);



//        if(f2 == null) {

//            f2 = new MyFragment("联系人");

//        }

//        transaction.replace(R.id.main_frame_layout, f2);



            transaction.commit();

        }







        //隐藏所有的fragment

    private void hideFragment(FragmentTransaction transaction){

            if(clszFragment != null){

                transaction.hide(clszFragment);

            }

            if(farmFragment != null){

                transaction.hide(farmFragment);

            }



        }


    public void clSetting(View view) {
        initFragment1();
        btnGray();
        clszBtn.setBackgroundColor(Color.GREEN);
    }

    public void njSetting(View view) {
        initFragment2();
        btnGray();
        njszBtn.setBackgroundColor(Color.GREEN);
    }





    public void sendCarInfor(){
        //TODO 添加下位机同步车辆信息
        car = clszFragment.car;
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
        order[6] = (byte) ((car.TXHeight/256)&0xff);
        order[7] = (byte) ((car.TXHeight%256)&0xff);
        mService.sendCan(id,order);



        order[1] = 0x02;
        order[3] = 0x00;
        order[4] = (byte) ((car.TXHeight/256)&0xff);
        order[5] = (byte) ((car.TXHeight%256)&0xff);
        order[6] = (byte) ((car.Backwheel/256)&0xff);
        order[7] = (byte) ((car.Backwheel%256)&0xff);
        mService.sendCan(id,order);

        order[3] = 0x01;
        order[4] = (byte) ((car.TXMiddis/256)&0xff);
        order[5] = (byte) ((car.TXMiddis%256)&0xff);
        order[6] = 0x00;
        order[7] = 0x00;
        mService.sendCan(id,order);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        farmFragment.update();
    }

    public void exClicked(View view) {
        clszFragment.saveSetting();
        if (clszFragment.state>1) {
            clszFragment.state--;
            clszFragment.updateView();
        }

    }

    public void saveCar(View view) {
        clszFragment.savaData();
        sendCarInfor();
    }

    public void nextClicked(View view) {
        clszFragment.saveSetting();
        if (clszFragment.state<6){
            clszFragment.state++;
        clszFragment.updateView();}
    }
}

