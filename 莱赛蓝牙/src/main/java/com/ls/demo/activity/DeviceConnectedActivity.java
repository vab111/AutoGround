package com.ls.demo.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.ls.ble.adapter.BaseListAdapter;
import com.ls.ble.adapter.ViewHolder;
import com.ls.ble.constant.SimpleDevice;
import com.ls.ble.tools.BleDeviceTools;
import com.ls.ble.view.HeaderLayout;
import com.wutl.ble.tools.R;

public class DeviceConnectedActivity extends Activity {
	private ListView device_listview;
	private List<SimpleDevice> device_datas = new ArrayList<SimpleDevice>();
	private DeviceAdapter mDeviceAdapter = null;
	private List<String> online_device_data = null;

	private static String device_name;
	private static String device_code;
	protected HeaderLayout headerLayout;
	private Activity instance;

	private boolean isInDeviceManage = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connectdevicelist_layout);
		instance = this;
		initView();
		initData();
		initAction();
	}

	public void initView() {
		headerLayout = (HeaderLayout) findViewById(R.id.ls_headerLayout);
		device_name = getIntent().getStringExtra("param_name");
		device_code = getIntent().getStringExtra("param_code");

		online_device_data = getIntent().getStringArrayListExtra(
				"online_device");

		headerLayout.showTitle(device_name + "已绑定设备列表");

		device_listview = (ListView) findViewById(R.id.device_listview);
		mDeviceAdapter = new DeviceAdapter(instance, device_datas,
				R.layout.item_connect_device);
		device_listview.setAdapter(mDeviceAdapter);
	}

	public void initData() {
		List<SimpleDevice> temp_data = BleDeviceTools.getInstance(instance)
				.selectBindDeviceWithCode(instance, device_code);
		for (SimpleDevice device : temp_data) {
			if (device.getStatue() != 3
					&& !online_device_data.contains(device.getAddress())){
				device.setStatue(1);
			}
		}
		device_datas.clear();
		device_datas.addAll(temp_data);
		mDeviceAdapter.notifyDataSetChanged();
	}

	public void initAction() {
		headerLayout.showLeftBackButton(new OnClickListener() {

			@Override
			public void onClick(View v) {
				instance.finish();
			}
		});
		headerLayout.showRightTextButton("管理", new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				isInDeviceManage = !isInDeviceManage;
				mDeviceAdapter.notifyDataSetInvalidated();
			}
		});

		device_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				SimpleDevice device = (SimpleDevice) parent.getAdapter()
						.getItem(position);
				if (device != null) {
					Intent it = new Intent(DeviceConnectedActivity.this,
							MeasureActivity.class);
					it.putExtra("param_name", device_name);
					it.putExtra("param_code", device_code);
					it.putExtra("select_device", device);
					instance.startActivity(it);
					instance.finish();
				}

			}
		});

	}

	class DeviceAdapter extends BaseListAdapter<SimpleDevice> {

		public DeviceAdapter(Context ctx, List<SimpleDevice> datas, int layoutId) {
			super(ctx, datas, layoutId);
		}

		@Override
		public void conver(ViewHolder holder, int position, final SimpleDevice t) {
			holder.setText(R.id.tv_device_name,
					t.getName().replace(device_code, device_name));
			TextView tv_statue = holder.getView(R.id.tv_device_statue);
			TextView tv_delete_btn = holder.getView(R.id.tv_delete_button);
			if (isInDeviceManage) {
				tv_delete_btn.setVisibility(View.VISIBLE);
				tv_statue.setVisibility(View.GONE);
			} else {
				tv_delete_btn.setVisibility(View.GONE);
				tv_statue.setVisibility(View.VISIBLE);
				int tag = t.getStatue();
				if (tag == 0) {
				} else if (tag == 1) {
					tv_statue.setText("设备离线");
				} else if (tag == 2) {
					tv_statue.setText("设备在线");
				} else if (tag == 3) {
					tv_statue.setText("当前连接设备");
				}
			}
			tv_delete_btn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					boolean isOk = BleDeviceTools.getInstance(instance)
							.deleteBindDevice(instance, t);
					if (isOk) {
						initData();
					}
				}
			});

		}

	}

}
