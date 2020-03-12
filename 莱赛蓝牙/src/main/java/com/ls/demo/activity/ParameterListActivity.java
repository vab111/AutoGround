package com.ls.demo.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ls.ble.constant.DeviceParam;
import com.ls.ble.utils.Utils;
import com.ls.ble.view.HeaderLayout;
import com.wutl.ble.tools.R;

public class ParameterListActivity extends Activity implements OnClickListener {
	private LinearLayout ll_param_four, ll_param_three, ll_param_two,
			ll_param_one;

	protected HeaderLayout headerLayout;
	private Activity instance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_parameterlist_layout);
		instance = this;
		if (!instance.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			Utils.toast(instance, "BLE不支持此设备!");
			instance.finish();
		}
		initView();
		initData();
		initAction();
	}

	public void initView() {
		headerLayout = (HeaderLayout) findViewById(R.id.ls_headerLayout);
		headerLayout.showTitle("测量参数列表");
		ll_param_one = (LinearLayout) findViewById(R.id.ll_param_one);
		ll_param_two = (LinearLayout) findViewById(R.id.ll_param_two);
		ll_param_three = (LinearLayout) findViewById(R.id.ll_param_three);
		ll_param_four = (LinearLayout) findViewById(R.id.ll_param_four);
		ll_param_one.setOnClickListener(this);
		ll_param_two.setOnClickListener(this);
		ll_param_three.setOnClickListener(this);
		ll_param_four.setOnClickListener(this);

		if (!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, "本设备不支持蓝牙4.0", Toast.LENGTH_SHORT).show();
			finish();
		}

	}

	public void initData() {
	}

	public void initAction() {
		headerLayout.showRightTextButton("设置", new OnClickListener() {

			@Override
			public void onClick(View v) {
				Utils.goActivity(instance, SettingActivity.class);
			}
		});
	}

	@Override
	public void onClick(View v) {
		Intent it = new Intent(instance, MeasureActivity.class);
		switch (v.getId()) {

		case R.id.ll_param_one:
			it.putExtra("param_name", DeviceParam.Device_One_Name);
			it.putExtra("param_code", DeviceParam.Device_One_Code);
			break;
		case R.id.ll_param_two:
			it.putExtra("param_name", DeviceParam.Device_Two_Name);
			it.putExtra("param_code", DeviceParam.Device_Two_Code);
			break;
		case R.id.ll_param_three:
			it.putExtra("param_name", DeviceParam.Device_Three_Name);
			it.putExtra("param_code", DeviceParam.Device_Three_Code);
			break;
		case R.id.ll_param_four:
			it.putExtra("param_name", DeviceParam.Device_Four_Name);
			it.putExtra("param_code", DeviceParam.Device_Four_Code);
			break;
		default:
			break;
		}
		instance.startActivity(it);
	}

}
