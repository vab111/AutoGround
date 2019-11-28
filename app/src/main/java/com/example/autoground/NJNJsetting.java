package com.example.autoground;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

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
    private clFragment clszFragment;
    private njFragment njszFragment;
    private txFragment txszFragment;
    private Toolbar toolbar;
    private CarInfor car;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_njnjsetting);
        toolbar = findViewById(R.id.njnjbar);
        setToolbar();
        getRecord();
        initFragment1();
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

                clszFragment = new clFragment();

                transaction.add(R.id.njsetting_frame, clszFragment);
                clszFragment.zhouju.setText(car.Zhou);
                clszFragment.back.setText(car.Backwheel);
                clszFragment.front.setText(car.Frontwheel);
                clszFragment.nianfen.setText(car.year);
                clszFragment.xinghao.setText(car.Xinhao);
                clszFragment.pinpai.setText(car.brand);
                clszFragment.chegao.setText(car.height);
                clszFragment.chepai.setText(car.lisence);
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



            if(njszFragment == null){

                njszFragment = new njFragment();

                transaction.add(R.id.njsetting_frame,njszFragment);
                njszFragment.pinpai.setText(car.NJBrand);
                njszFragment.leixing.setText(car.NJType);
                njszFragment.xinghao.setText(car.NJXinhao);
                njszFragment.nianfen.setText(car.NJyear);
                njszFragment.width.setText(car.NJWidth);
                njszFragment.back.setText(car.NJBackdis);
                njszFragment.pianyi.setText(car.pianyi);
                if (car.leftright)
                    njszFragment.leftSelected();
                else
                    njszFragment.rightSelected();
            }

            hideFragment(transaction);

            transaction.show(njszFragment);



//        if(f2 == null) {

//            f2 = new MyFragment("联系人");

//        }

//        transaction.replace(R.id.main_frame_layout, f2);



            transaction.commit();

        }



        //显示第三个fragment

        private void initFragment3(){

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();



            if(txszFragment == null){

                txszFragment = new txFragment();

                transaction.add(R.id.njsetting_frame,txszFragment);
                txszFragment.toMid.setText(car.TXMiddis);
                txszFragment.toBack.setText(car.TXBackdis);
                txszFragment.toGround.setText(car.TXHeight);

            }

            hideFragment(transaction);

            transaction.show(txszFragment);



//        if(f3 == null) {

//            f3 = new MyFragment("动态");

//        }

//        transaction.replace(R.id.main_frame_layout, f3);



            transaction.commit();

        }



        //隐藏所有的fragment

        private void hideFragment(FragmentTransaction transaction){

            if(clszFragment != null){

                transaction.hide(clszFragment);

            }

            if(njszFragment != null){

                transaction.hide(njszFragment);

            }

            if(txszFragment != null){

                transaction.hide(txszFragment);
            }

        }


    public void clSetting(View view) {
        initFragment1();
    }

    public void njSetting(View view) {
        initFragment2();
    }

    public void txSetting(View view) {
        initFragment3();
    }

    public void leftSelected(View view) {
        njszFragment.leftSelected();
    }

    public void rightSelected(View view) {
        njszFragment.rightSelected();
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

    public void save(View view) {
        car.brand = clszFragment.pinpai.getText().toString();
        car.Xinhao = clszFragment.xinghao.getText().toString();
        car.year = clszFragment.nianfen.getText().toString();
        car.lisence = clszFragment.chepai.getText().toString();
        car.Frontwheel =  Integer.parseInt(clszFragment.front.getText().toString());
        car.Backwheel = Integer.parseInt(clszFragment.back.getText().toString());
        car.Zhou = Integer.parseInt(clszFragment.zhouju.getText().toString());
        car.height = Integer.parseInt(clszFragment.chegao.getText().toString());
        car.NJType = njszFragment.leixing.getText().toString();
        car.NJBrand = njszFragment.pinpai.getText().toString();
        car.NJXinhao = njszFragment.xinghao.getText().toString();
        car.NJyear = njszFragment.nianfen.getText().toString();
        car.NJWidth = Integer.parseInt(njszFragment.width.getText().toString());
        car.NJBackdis= Integer.parseInt(njszFragment.back.getText().toString());
        car.pianyi= Integer.parseInt(njszFragment.pianyi.getText().toString());
        if (njszFragment.leftBtn.isChecked())
            car.leftright = true;
        else
            car.leftright = false;
        car.TXHeight = Integer.parseInt(txszFragment.toGround.getText().toString());
        car.TXBackdis = Integer.parseInt(txszFragment.toBack.getText().toString());
        car.TXMiddis = Integer.parseInt(txszFragment.toMid.getText().toString());;

        List fileList = new ArrayList();
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
