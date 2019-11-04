package com.example.autoground;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

public class history extends BaseActivity  {
    private static final String TAG_SERVICE = "history";
    private List fileList;
    private ListView listView;
    private int width;
    private int height;
    private Toolbar toolbar;
    private ArrayAdapter listAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        toolbar = findViewById(R.id.historybar);
        getRecord();
        listView = findViewById(R.id.historylist);
        listAdapter = new ArrayAdapter(this,R.layout.record,fileList){
            @Override
            public int getCount() {
                return fileList.size();
            }
            @Override
            public Object getItem(int position) {
                return fileList.get(position);
            }
            @Override
            public long getItemId(int position) {
                return position;
            }
            @Override
            public View getView(int position, @Nullable View convertView, @Nullable ViewGroup parent) {
                //TODO 添加历史记录条目
                recorder user = (recorder) fileList.get(position);
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.record, null);
                TextView nameText = (TextView) view.findViewById(R.id.textView18);
                TextView ageText = (TextView) view.findViewById(R.id.textView19);
                TextView sexText = (TextView) view.findViewById(R.id.editText10);
                TextView timeText = (TextView) view.findViewById(R.id.editText11);
                nameText.setText(user.taskname);
                ageText.setText(user.type);
                sexText.setText(user.mianji);
                timeText.setText(user.time);
                return view;
            }
        };
        listView.setAdapter(listAdapter);
        //为ListView添加点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG_SERVICE, "xuanzhong !");
                if (position>0)
                    actionRecord(position);
            }
        });

        setToolbar();
    }

    private void actionRecord(int position)
    {
        Dialog bottomDialog = new Dialog(this,R.style.BottomDialog);
        View contentView = LayoutInflater.from(this).inflate(R.layout.recordaction, null);
        bottomDialog.setContentView(contentView);

        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        bottomDialog.setCanceledOnTouchOutside(false);
        bottomDialog.show();
    }
    private void setToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);//设计隐藏标题
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//设置显示返回键
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

        fileList = new ArrayList();

        File fs = new File("Record.json");
        if (!fs.exists())
        {
            try
            {
                recorder header = new recorder();
                header.taskname = "作业名称";
                header.type = "作业类型";
                header.mianji = "任务面积";
                header.time = "作业时间";
                fileList.add(header);
                fileList.add(header);

                Gson gson = new Gson();
                String jsonString = gson.toJson(fileList);
                FileOutputStream fileOut= openFileOutput("Record.json", Context.MODE_PRIVATE);
                OutputStreamWriter outStream = new OutputStreamWriter(fileOut);
                outStream.write(jsonString);
                fileOut.flush();
                outStream.flush();
                outStream.close();
                fileOut.close();
                //Toast.makeText(history.this,"创建记录成功！",Toast.LENGTH_SHORT).show();
                Log.e(TAG_SERVICE, "创建记录成功！");
            }
            catch(Exception e)
            {
            }

        }
        else
        {
            Log.e(TAG_SERVICE, "文件访问成功！");
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

    }
    private void saveRecord()
    {
        Gson gson = new Gson();
        String jsonString = gson.toJson(fileList);

        FileOutputStream fileOut=null;
        OutputStreamWriter outStream =null;
        try
        {
            fileOut =new FileOutputStream("/AutoGround/Record.json",false);
            outStream =new OutputStreamWriter(fileOut);
            outStream.write(jsonString);
            outStream.flush();
            outStream.close();
            fileOut.close();
            Toast.makeText(history.this,"保存成功！",Toast.LENGTH_SHORT).show();
        }
        catch(Exception e)
        {
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



    class recorder
    {
        public String taskname;
        public String type;
        public String mianji;
        public String time;
    }
}
