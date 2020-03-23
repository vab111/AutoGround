package com.example.autoground;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

public class CarSetFragment extends Fragment {
    private TextView discription;
    private EditText data;
    private ImageView img;
    private Button exBtn;
    private Button nextBtn;
    private Button saveBtn;
    public CarInfor car;
    public int state = 1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.carsetfragment, container, false);
        initData();
        discription = view.findViewById(R.id.textView167);
        img = view.findViewById(R.id.imageView4);
        data = view.findViewById(R.id.editText47);
        exBtn = view.findViewById(R.id.button8);
        nextBtn = view.findViewById(R.id.button46);
        saveBtn = view.findViewById(R.id.button50);
        data.setText(String.format("%d", car.TXHeight));
        return view;
    }
    private void initData()
    {
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
    public void savaData()
    {
        saveSetting();
        List fileList = new ArrayList();
        File appDir = new File(Environment.getExternalStorageDirectory()+"/AutoGround");   //自定义的目录
        if (!appDir.exists()) {
            boolean isSuccess = appDir.mkdir();
            Log.d("MsgId:" ,"----------0------------------"+isSuccess);
        }
        else
            Log.d("MsgId:" ,"----------0------------------目录已经存在:"+Environment.getExternalStorageDirectory()+"/AutoGround");

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
    public void updateView()
    {
        switch (state)
        {
            case 1:
                img.setImageResource(R.drawable.car_txheight);
                discription.setText("主天线高度");
                data.setText(String.format("%d", car.TXHeight));
                break;
            case 2:
                img.setImageResource(R.drawable.car_txmid);
                discription.setText("天线到中轴距离");
                data.setText(String.format("%d", car.TXMiddis));
                break;
            case 3:
                img.setImageResource(R.drawable.car_txdistance);
                discription.setText("主副天线距离");
                data.setText(String.format("%d", car.TXBackdis));
                break;
            case 4:
                img.setImageResource(R.drawable.car_txfdis);
                discription.setText("天线到后轴距离");
                data.setText(String.format("%d", car.Backwheel));
                break;
            case 5:
                img.setImageResource(R.drawable.car_zhou);
                discription.setText("拖拉机轴距");
                data.setText(String.format("%d", car.Zhou));
                break;
            case 6:
                img.setImageResource(R.drawable.car_frontwheel);
                discription.setText("拖拉机前轮轮距");
                data.setText(String.format("%d", car.Frontwheel));
                break;
                default:
                    break;
        }
    }
    public void saveSetting()
    {
        String numstr = data.getText().toString();
        int num = 0;
        if (numstr.length()>0)
            num = Integer.parseInt(numstr);
        switch (state)
        {
            case 1:
                car.TXHeight = num;
                break;
            case 2:
                car.TXMiddis= num;
                break;
            case 3:
                car.TXBackdis= num;
                break;
            case 4:
                car.Backwheel= num;
                break;
            case 5:
                car.Zhou= num;
                break;
            case 6:
                car.Frontwheel= num;
                break;
            default:
                break;
        }
        Toast.makeText(getContext(),"保存成功！",Toast.LENGTH_SHORT).show();
    }
}
