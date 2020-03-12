package com.ls.demo.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.ls.ble.adapter.BaseListAdapter;
import com.ls.ble.adapter.ViewHolder;
import com.ls.ble.constant.SimpleDevice;
import com.ls.ble.db.DeviceTable;
import com.ls.ble.tools.BleDeviceTools;
import com.ls.ble.tools.BleWrapper;
import com.ls.ble.tools.BleWrapperUiCallbacks.Null;
import com.ls.ble.utils.Utils;
import com.ls.ble.view.HeaderLayout;
import com.wutl.ble.tools.R;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class ScanningActivity extends Activity {
	private static String device_name;
	private static String device_code;

	private ListView device_listview;
	private List<BluetoothDevice> device_datas = new ArrayList<BluetoothDevice>();
	private List<BluetoothDevice> show_device_datas = new ArrayList<BluetoothDevice>();
	private DeviceAdapter mDeviceAdapter = null;

	private BleWrapper mBleWrapper = null;
	private Handler mHandler = new Handler();
	private boolean mScanning = false;
	protected HeaderLayout headerLayout;

	private List<String> has_connect_device_datas = new ArrayList<String>();
	private Activity instance;

	protected void onCreate(Bundle var1) {
		super.onCreate(var1);
		setContentView(R.layout.activity_devicelist_layout);
		instance=this;
	
		initView();
		initData();
		initAction();
		
		this.mBleWrapper = new BleWrapper(this, new Null() {
			public void uiDeviceFound(BluetoothDevice var1, int var2,
					byte[] var3) {
				ScanningActivity.this.handleFoundDevice(var1, var2, var3);
			}
		});
		
		if (!this.mBleWrapper.checkBleHardwareAvailable()) {
			Utils.toast(instance,"本设备蓝牙没有开启");
			this.finish();
		}

		device_listview = (ListView) findViewById(R.id.device_listview);
		mDeviceAdapter = new DeviceAdapter(instance, show_device_datas,
				R.layout.item_find_device);
		device_listview.setAdapter(mDeviceAdapter);

		//从数据库获取已经绑定的设备，通过设备的地址值进行比对
		List<SimpleDevice> temp_data = BleDeviceTools.getInstance(instance)
				.selectBindDeviceWithCode(instance,device_code);

		//遍历加载已经绑定的设备地址
		for (SimpleDevice device : temp_data) {
			has_connect_device_datas.add(device.getAddress());
		}

	}

	protected void onResume() {
		super.onResume();
		BluetoothManager bManager = (BluetoothManager) instance
				.getSystemService(Context.BLUETOOTH_SERVICE);
		BluetoothAdapter bAdapter = bManager.getAdapter();
		if (!bAdapter.isEnabled()) {
			bAdapter.enable();
		}

		this.mBleWrapper.initialize();
		this.mScanning = true;
		this.addScanningTimeout();

		// 开始扫描蓝牙设备
		this.mBleWrapper.startScanning();
		Utils.toast(instance,"设备扫描中...");
	}

	private void addScanningTimeout() {
		Runnable var1 = new Runnable() {
			public void run() {
				if (ScanningActivity.this.mBleWrapper != null) {
					ScanningActivity.this.mScanning = false;
					ScanningActivity.this.mBleWrapper.stopScanning();
				}
			}
		};
		this.mHandler.postDelayed(var1, 5000L);
	}



	private void btDisabled() {
		Utils.toast(instance,"Sorry, BT has to be turned ON for us to work!");
		this.finish();
	}

	/**
	 * find ble device
	 * 
	 * @param var1
	 * @param var2
	 * @param var3
	 */
	private void handleFoundDevice(final BluetoothDevice var1, final int var2,
			final byte[] var3) {
		this.runOnUiThread(new Runnable() {
			public void run() {
				ScanningActivity.this.mDeviceAdapter
						.addDevice(var1, var2, var3);
				ScanningActivity.this.mDeviceAdapter.notifyDataSetChanged();
			}
		});
	}

	protected void onActivityResult(int var1, int var2, Intent var3) {
		if (var1 == 1 && var2 == 0) {
			this.btDisabled();
		} else {
			super.onActivityResult(var1, var2, var3);
		}
	}



	protected void onPause() {
		super.onPause();
		this.mScanning = false;
		this.mBleWrapper.stopScanning();
		this.mDeviceAdapter.clearList();
	}



	//第二步：获取控件以及上个界面传值
	public void initView() {
		headerLayout = (HeaderLayout) findViewById(R.id.ls_headerLayout);
		device_name = getIntent().getStringExtra("param_name");
		device_code = getIntent().getStringExtra("param_code");
		
		headerLayout.showTitle(device_name + "设备列表");

		device_listview = (ListView) findViewById(R.id.device_listview);
	}

	//生命周期第三部，准备初始数据
	public void initData() {
	}

	//生命周期第四部：添加点击事件
	public void initAction() {
		headerLayout.showLeftBackButton(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				instance.finish();
			}
		});
		headerLayout.showRightTextButton("已绑定设备", new OnClickListener() {

			@Override
			public void onClick(View v) {
				List<String> online_device_data = new ArrayList<String>();
				for (BluetoothDevice device : device_datas) {
					online_device_data.add(device.getAddress());
				}
				Intent it = new Intent(instance, DeviceConnectedActivity.class);
				it.putExtra("param_name", device_name);
				it.putExtra("param_code", device_code);
				it.putStringArrayListExtra("online_device",
						(ArrayList<String>) online_device_data);
				instance.startActivity(it);
			}
		});
		
		device_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				BluetoothDevice device = (BluetoothDevice) parent.getAdapter()
						.getItem(position);
				String name=device
						.getName();

				SimpleDevice s_device = new SimpleDevice(device_code, name, device.getAddress(), 3);
				DeviceTable.getInstance(instance).insertDevice(s_device);

				if (device != null) {
					Intent it = new Intent(ScanningActivity.this,
							MeasureActivity.class);
					it.putExtra("param_name", device_name);
					it.putExtra("param_code", device_code);
					it.putExtra("select_device", s_device);
					instance.startActivity(it);
					if (ScanningActivity.this.mScanning) {
						ScanningActivity.this.mScanning = false;
						ScanningActivity.this.mBleWrapper.stopScanning();
					}
					instance.finish();
				}

			}
		});
	}

	
	class DeviceAdapter extends BaseListAdapter<BluetoothDevice> {


		public DeviceAdapter(Context ctx, List<BluetoothDevice> datas,
				int layoutId) {
			super(ctx, datas, layoutId);
		}

		@Override
		public void conver(ViewHolder holder, int position, BluetoothDevice t) {
			String var8 = t.getName().replace(device_code, device_name);
			if (var8 == null || var8.length() <= 0) {
				var8 = "";
			}
			holder.setText(R.id.tv_device_name, var8);

		}

		public void addDevice(BluetoothDevice var1, int var2, byte[] var3) {
			// 过滤其他设备
			String name = var1.getName();
			if (name != null && name.startsWith(device_code)) {
				// 过滤相同设备
				if (!device_datas.contains(var1)) {
					device_datas.add(var1);
					if (!has_connect_device_datas.contains(var1.getAddress())) {
						show_device_datas.add(var1);
					}

				}
			}
		}

		public void clearList() {
			device_datas.clear();
			show_device_datas.clear();
		}
	}
}
