package com.example.autoground;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
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
import android.widget.Button;
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

    private void actionRecord(final int position)
    {
        final Dialog bottomDialog = new Dialog(this,R.style.BottomDialog);
        View contentView = LayoutInflater.from(this).inflate(R.layout.recordaction, null);
        bottomDialog.setContentView(contentView);

        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        bottomDialog.setCanceledOnTouchOutside(false);
        bottomDialog.show();
        Button del = contentView.findViewById(R.id.button22);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recorder item = (recorder) fileList.get(position);
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
                fileList.remove(position);
                saveRecord();
                listAdapter.notifyDataSetChanged();
                bottomDialog.dismiss();
            }
        });
        Button useHistory = contentView.findViewById(R.id.button21);
        Button viewHistory = contentView.findViewById(R.id.button20);
        useHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recorder item = (recorder) fileList.get(position);
                Intent intent = new Intent();
                intent.putExtra("taskname",item.taskname);
                setResult(RESULT_OK,intent);
                bottomDialog.dismiss();
                finish();


            }
        });
        viewHistory.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                recorder item = (recorder) fileList.get(position);
                Intent intent = new Intent();
                intent.setClass(history.this, TaskHistory.class);
                intent.putExtra("filename",item.taskname);
                startActivity(intent);
                bottomDialog.dismiss();
            }
        });
    }
    public boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
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
        File appDir = new File(Environment.getExternalStorageDirectory()+"/AutoGround");   //自定义的目录
        if (!appDir.exists()) {
            boolean isSuccess = appDir.mkdir();
            Log.d("MsgId:" ,"----------0------------------"+isSuccess);
        }
        else
            Log.d("MsgId:" ,"----------0------------------目录已经存在:"+Environment.getExternalStorageDirectory()+"/AutoGround");

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
                Toast.makeText(getBaseContext(), "File created successfully", Toast.LENGTH_LONG).show();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
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
            fileOut =new FileOutputStream(Environment.getExternalStorageDirectory()+"/AutoGround/Record.json",false);
            outStream =new OutputStreamWriter(fileOut);
            outStream.write(jsonString);
            outStream.flush();
            outStream.close();
            fileOut.close();
            Toast.makeText(history.this,"保存成功！",Toast.LENGTH_SHORT).show();
        }
        catch(Exception e)
        {
            Toast.makeText(history.this,"保存失败！",Toast.LENGTH_SHORT).show();
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




}
