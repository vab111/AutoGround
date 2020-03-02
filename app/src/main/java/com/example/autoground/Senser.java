package com.example.autoground;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.guard.CommunicationService;
import com.android.guard.DataType;

public class Senser extends AppCompatActivity {
    private int state = 1;
    private Toolbar toolbar;
    private CommunicationService mService;
    private TextView ad_Value;
    private TextView angle_fix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senser);
        toolbar = findViewById(R.id.senserbar);
        ad_Value = findViewById(R.id.textView76);
        angle_fix = findViewById(R.id.textView77);
        setToolbar();
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
            case 1538:
                //0x602
                switch (data[1])
                {
                    case 6:
                        //TODO 天线修正、转角修正

                        final int txxiuzheng = (((0xff&data[4]) << 8) | (0xff&data[5]));

                        final int zhuanjiaoxiuzheng = ((data[6] << 8) | (0xff&data[7]));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //float jiaodu1 = (float) txxiuzheng/10;
                                float jiaodu2 = (float)zhuanjiaoxiuzheng/100;
                                ad_Value.setText(String.format("AD值：%d", txxiuzheng));
                                angle_fix.setText(String.format("%.1f°", jiaodu2));

                            }
                        });
                        break;

                    default:
                        break;
                }
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
                finish();
            }
        });
    }
    public void reset(View view) {
        final Dialog bottomDialog = new Dialog(this, R.style.BottomDialog);
        final View contentView = LayoutInflater.from(this).inflate(R.layout.resetsensor, null);
        bottomDialog.setContentView(contentView);

        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        bottomDialog.setCanceledOnTouchOutside(false);
        bottomDialog.show();
        Button cancelBtn = contentView.findViewById(R.id.button60);
        final Button confirmBtn = contentView.findViewById(R.id.button62);
        final TextView stateText = contentView.findViewById(R.id.textView78);
        final TextView discription = contentView.findViewById(R.id.textView79);
        final EditText input = contentView.findViewById(R.id.editText41);
        input.setVisibility(View.INVISIBLE);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomDialog.dismiss();
            }
        });
         state = 1;
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 添加新的任务
            state++;
            stateText.setText(String.format("%d/4", state));

            switch (state)
            {
                case 1:
                    discription.setText("");
                    break;
                case 2:
                    discription.setText("请输入准确值");
                    input.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    discription.setText("请再次输入准确值");
                    break;
                case 4:
                    discription.setText("校准完成");
                    confirmBtn.setText("完成");
                    input.setVisibility(View.INVISIBLE);
                    break;
                case 5:
                    bottomDialog.dismiss();
                    break;

            }


            }


        });
    }
}
