package com.example.autoground;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class SystemSetting extends BaseActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_setting);
        toolbar = findViewById(R.id.settingbar);
        setToolbar();
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



    public void NJsetting(View view) {
        Intent intent = new Intent();
        intent.setClass(SystemSetting.this, NJNJsetting.class);
        startActivity(intent);
    }

    public void aboutself(View view) {
        Intent intent = new Intent();
        intent.setClass(SystemSetting.this, AboutSelf.class);
        startActivity(intent);
    }

    public void cfsetting(View view) {
        Intent intent = new Intent();
        intent.setClass(SystemSetting.this, ChafenSetting.class);
        startActivity(intent);
    }



    public void chuchang(View view) {
        final Dialog bottomDialog = new Dialog(this, R.style.BottomDialog);
        final View contentView = LayoutInflater.from(this).inflate(R.layout.passwordcheck, null);
        bottomDialog.setContentView(contentView);

        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        bottomDialog.setCanceledOnTouchOutside(false);
        bottomDialog.show();
        Button cancelBtn = contentView.findViewById(R.id.button42);
        final Button confirmBtn = contentView.findViewById(R.id.button43);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomDialog.dismiss();
            }
        });
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 添加新的任务

                EditText name = contentView.findViewById(R.id.editText40);
                String ps = name.getText().toString();
                if (ps.equals("lsjg2019"))
                {
                    Intent intent = new Intent();
                    intent.setClass(SystemSetting.this, SecretSetting.class);
                    startActivity(intent);
                    bottomDialog.dismiss();
                }
                else
                {
                    name.setText("");
                    Toast.makeText(getBaseContext(), "密码错误!", Toast.LENGTH_LONG).show();
                }

            }


        });
    }



    public void deleteAll(View view) {
        new AlertDialog.Builder(this)
                .setTitle("提示")//这里是表头的内容
                .setMessage("确定删除所有记录？")//这里是中间显示的具体信息
                .setPositiveButton("取消",//这个string是设置左边按钮的文字
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })//setPositiveButton里面的onClick执行的是左边按钮
                .setNegativeButton("确定",//这个string是设置右边按钮的文字
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                deleteAllRecord();
                            }
                        })//setNegativeButton里面的onClick执行的是右边的按钮的操作
                .show();
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
    public void deleteAllRecord()
    {
        List fileList = new ArrayList();
        File appDir = new File(Environment.getExternalStorageDirectory()+"/AutoGround");   //自定义的目录
        if (!appDir.exists()) {
            boolean isSuccess = appDir.mkdir();
            Log.d("MsgId:" ,"----------0------------------"+isSuccess);
        }
        else
            Log.d("MsgId:" ,"----------0------------------目录已经存在:"+Environment.getExternalStorageDirectory()+"/AutoGround");

        File fs = new File(Environment.getExternalStorageDirectory()+"/AutoGround/Record.json");

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

        while(fileList.size()>0) {
            recorder item = (recorder) fileList.get(0);
            appDir = new File(Environment.getExternalStorageDirectory() + "/AutoGround/" + item.taskname);
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
            String abline = Environment.getExternalStorageDirectory() + "/AutoGround/" + item.taskname + ".json";
            deleteFile(abline);
            fileList.remove(0);
        }
        appDir = new File(Environment.getExternalStorageDirectory() + "/AutoGround/ABLine");
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
        }
        recorder header = new recorder();
        header.taskname = "作业名称";
        header.type = "作业类型";
        header.mianji = "任务面积";
        header.time = "作业时间";
        fileList.add(header);
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
            Toast.makeText(SystemSetting.this,"删除成功！",Toast.LENGTH_SHORT).show();
        }
        catch(Exception e)
        {
            Toast.makeText(SystemSetting.this,"删除失败！",Toast.LENGTH_SHORT).show();
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
