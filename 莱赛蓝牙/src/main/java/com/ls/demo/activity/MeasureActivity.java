package com.ls.demo.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.ls.ble.adapter.BaseListAdapter;
import com.ls.ble.adapter.ViewHolder;
import com.ls.ble.constant.ReceiveData;
import com.ls.ble.constant.SimpleDevice;
import com.ls.ble.db.DeviceTable;
import com.ls.ble.db.PreferenceMap;
import com.ls.ble.tools.BleWrapper;
import com.ls.ble.tools.BleWrapperUiCallbacks;
import com.ls.ble.utils.ResultDeciphering;
import com.ls.ble.utils.Utils;
import com.ls.ble.view.HeaderLayout;
import com.wutl.ble.tools.R;

/**
 * @author xiaobian
 * @version 创建时间：2017年9月19日 下午4:11:07
 */

public class MeasureActivity extends Activity implements BleWrapperUiCallbacks {
	private static String device_name;
	private static String device_code;

	private ListView data_listview;
	private List<ReceiveData> receive_datas = new ArrayList<ReceiveData>();
	private ReceiveDataAdapter mDataAdapter;

	private TextView tv_connect_statue;
	private TextView tv_refresh;

	private List<BluetoothGattService> m_services = new ArrayList<BluetoothGattService>();
	private List<BluetoothGattCharacteristic> m_characters = new ArrayList<BluetoothGattCharacteristic>();
	private List<SimpleDevice> bind_devices = new ArrayList<SimpleDevice>();

	private BluetoothGattCharacteristic curreCharacteristic = null;
	public static final String EXTRAS_DEVICE_ADDRESS = "BLE_DEVICE_ADDRESS";
	private BleWrapper mBleWrapper;

	private SimpleDevice current_connect_device = null;
	protected HeaderLayout headerLayout;
	private Activity instance;
	// 声明一个声音对象
	private static SoundPool soundPool;
	// 申明一个振动器对象
	private Vibrator mVibrator;

	private boolean is_connecting = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_measuer_layout);
		instance = this;
		initView();
		initData();
		initAction();
	}

	// 第二步：初始化控件
	public void initView() {
		headerLayout = (HeaderLayout) findViewById(R.id.ls_headerLayout);
		device_name = getIntent().getStringExtra("param_name");
		device_code = getIntent().getStringExtra("param_code");
		headerLayout.showTitle(device_name + "测量数据");
		tv_connect_statue = (TextView) findViewById(R.id.tv_connect_statue);
		tv_refresh = (TextView) findViewById(R.id.tv_refresh);
		data_listview = (ListView) findViewById(R.id.data_listview);
		mDataAdapter = new ReceiveDataAdapter(instance, receive_datas,
				R.layout.item_receive_data);
		data_listview.setAdapter(mDataAdapter);

	}

	// 第三步，数据初始化
	public void initData() {
		soundPool = new SoundPool(10, AudioManager.STREAM_RING, 10);
		soundPool.load(instance, R.raw.notice, 1);
		mVibrator = (Vibrator) getApplication().getSystemService(
				Service.VIBRATOR_SERVICE);
	}

	// 第四步：初始化操作
	public void initAction() {
		headerLayout.showLeftBackButton(new OnClickListener() {

			@Override
			public void onClick(View v) {
				instance.finish();
			}
		});

		headerLayout.showRightTextButton("设备管理", new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent it = new Intent(instance, ScanningActivity.class);
				it.putExtra("param_name", device_name);
				it.putExtra("param_code", device_code);
				instance.startActivity(it);
			}
		});

		tv_refresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!is_connecting) {
					Utils.toast(instance, "正在刷新...");
					refresh_connectDevice();
				} else {
					Utils.toast(instance, "设备正在连接，无需刷新...");
				}

			}

		});
	}

	// 点击刷新按键，进行设备连接
	protected void refresh_connectDevice() {
		bind_devices = DeviceTable.getInstance(instance).selectDevices(
				device_code);
		if (bind_devices.size() == 0) {
			this.tv_connect_statue.setText("软件没有连接过设备，前往设备管理进行连接");
			return;
		}
		clearBle();
		initBle();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	// 清理蓝牙，释放资源
	private void clearBle() {
		if (this.mBleWrapper != null) {
			mBleWrapper.stopScanning();
			this.mBleWrapper.diconnect();
			this.mBleWrapper.close();
			this.mBleWrapper = null;
		}
		m_services.clear();
		m_characters.clear();
		curreCharacteristic = null;
		connect_num = 0;
	}

	// 初始化蓝牙wrapper
	private void initBle() {
		if (this.mBleWrapper == null) {
			this.mBleWrapper = new BleWrapper(this, this);
		}
		if (!this.mBleWrapper.initialize()) {
			this.finish();
		}
		// 是否开启自动连接
		boolean tag = new PreferenceMap(instance).getIsAutoConnect();
		if (tag) {
			mBleWrapper.startScanning();
			tv_connect_statue.setText("正在连接设备...");
			cdt.start();
		} else {
			tv_connect_statue.setText("没有开启设备自动连接");
		}
	}

	// 别的activity跳转过来时先调用的此方法
	@Override
	protected void onNewIntent(Intent intent) {

		super.onNewIntent(intent);
		setIntent(intent);
		// 此处的顺序不要弄错
		// 1、先判断新接入设备跟已连接设备是否同一个设备
		SimpleDevice current_device = (SimpleDevice) getIntent()
				.getSerializableExtra("select_device");
		device_name = getIntent().getStringExtra("param_name");
		device_code = getIntent().getStringExtra("param_code");
		current_connect_device = current_device;

		if (this.mBleWrapper == null) {
			this.mBleWrapper = new BleWrapper(this, this);
		}
		if (!this.mBleWrapper.initialize()) {
			this.finish();
		}

		String mDeviceAddress = "";
		if (current_connect_device != null
				&& !Utils.isEmpty(current_connect_device.getAddress())) {
			mDeviceAddress = current_connect_device.getAddress();
			this.mBleWrapper.connect(mDeviceAddress);
			
		} else {
			mDeviceAddress = "";
			this.tv_connect_statue.setText("没有连接设备");
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	@Override
	protected void onResume() {
		super.onResume();
		bind_devices = DeviceTable.getInstance(instance).selectDevices(
				device_code);
		if (bind_devices.size() == 0) {
			this.tv_connect_statue.setText("软件没有连接过设备，前往设备管理进行连接");
			return;
		}
		// 判断蓝牙是否关闭
		BluetoothManager bManager = (BluetoothManager) instance
				.getSystemService(Context.BLUETOOTH_SERVICE);
		BluetoothAdapter bAdapter = bManager.getAdapter();
		if (!bAdapter.isEnabled()) {
			bAdapter.enable();
		}
		initBle();
	}

	@Override
	protected void onPause() {
		super.onPause();
		clearBle();
		cdt.cancel();
	}

	class ReceiveDataAdapter extends BaseListAdapter<ReceiveData> {

		public ReceiveDataAdapter(Context ctx, List<ReceiveData> datas,
				int layoutId) {
			super(ctx, datas, layoutId);
		}

		@Override
		public void conver(ViewHolder holder, int position, ReceiveData t) {
			holder.setText(
					R.id.tv_data_tag,
					"第"
							+ (position + 1)
							+ "个数据："
							+ t.getResultStr().trim().replace("+", "")
									.replace("$", "").replace("Di:", "")
									.replace("Ji:", "").replace("Vi:", ""));
			holder.setText(R.id.tv_data_hex, t.getHexStr());

			if (t.getResultStr().startsWith("Er")) {
				holder.setText(R.id.tv_data_statue, "异常");
			} else {
				holder.setText(R.id.tv_data_statue, "合格");
			}
		}

	}

	@Override
	public void uiAvailableServices(BluetoothGatt var1, BluetoothDevice var2,
			List<BluetoothGattService> var3) {

		this.runOnUiThread(new Runnable() {
			public void run() {
				Iterator var1 = MeasureActivity.this.mBleWrapper
						.getCachedServices().iterator();
				m_services.clear();
				while (var1.hasNext()) {
					BluetoothGattService var2 = (BluetoothGattService) var1
							.next();
					m_services.add(var2);
				}
				if (m_services.size() >= 4) {
					BluetoothGattService var8 = m_services.get(3);
					MeasureActivity.this.mBleWrapper
							.getCharacteristicsForService(var8);
				}
			}
		});
	}

	@Override
	public void uiCharacteristicForService(BluetoothGatt var1,
			BluetoothDevice var2, final BluetoothGattService var3,
			final List<BluetoothGattCharacteristic> var4) {
		this.runOnUiThread(new Runnable() {
			public void run() {

				m_characters.clear();
				Iterator var1 = var4.iterator();
				while (var1.hasNext()) {
					BluetoothGattCharacteristic var2 = (BluetoothGattCharacteristic) var1
							.next();
					m_characters.add(var2);
				}
				BluetoothGattCharacteristic var7 = m_characters.get(1);
				MeasureActivity.this.uiCharacteristicsDetails(
						MeasureActivity.this.mBleWrapper.getGatt(),
						MeasureActivity.this.mBleWrapper.getDevice(),
						MeasureActivity.this.mBleWrapper.getCachedService(),
						var7);
			}
		});
	}

	@Override
	public void uiCharacteristicsDetails(BluetoothGatt var1,
			BluetoothDevice var2, BluetoothGattService var3,
			final BluetoothGattCharacteristic var4) {

		this.runOnUiThread(new Runnable() {
			public void run() {
				curreCharacteristic = var4;
				MeasureActivity.this.mBleWrapper
						.setNotificationForCharacteristic(curreCharacteristic,
								true);
			}
		});
	}

	@Override
	public void uiDeviceConnected(BluetoothGatt var1, final BluetoothDevice var2) {
		this.runOnUiThread(new Runnable() {
			public void run() {
				cdt.cancel();
				connect_num++;
				is_connecting = true;
				if (var2 == null || Utils.isEmpty(var2.getAddress())) {
					MeasureActivity.this.tv_connect_statue.setText("设备信息为空");
					return;
				}
				String address = var2.getAddress();
				if (Utils.isEmpty(var2.getName())) {
					MeasureActivity.this.tv_connect_statue.setText("已经连接设备："
							+ "未知设备");
				} else {
					MeasureActivity.this.tv_connect_statue.setText("已经连接设备："
							+ var2.getName().replace(device_code, device_name));
				}
				SimpleDevice device = new SimpleDevice(device_code,
						device_name, address, 3);
				DeviceTable.getInstance(instance).clearDeviceConnectStatue();
				DeviceTable.getInstance(instance).updateDeviceConnectStatue(
						device);
			}
		});
	}

	/**
	 * 处理设备断开连接后的逻辑
	 * 
	 * @param var1
	 * @param var2
	 */
	@Override
	public void uiDeviceDisconnected(BluetoothGatt var1,
			final BluetoothDevice var2) {
		this.runOnUiThread(new Runnable() {
			public void run() {
				if (var2 == null || Utils.isEmpty(var2.getAddress())) {
					return;
				}
				String address = var2.getAddress();

				SimpleDevice device = new SimpleDevice(device_code,
						device_name, address, 2);
				DeviceTable.getInstance(instance).updateDeviceConnectStatue(
						device);
				tv_connect_statue.setText("当前设备断开连接");
				MeasureActivity.this.m_services.clear();
				MeasureActivity.this.m_characters.clear();
				current_connect_device = null;
				connect_num = 0;
				is_connecting = false;
			}
		});
	}

	private int connect_num = 0;

	/**
	 * 处理扫描发现设备后的逻辑
	 * 
	 * @param var1
	 * @param var2
	 * @param var3
	 */
	@Override
	public void uiDeviceFound(BluetoothDevice var1, int var2, byte[] var3) {
		// 发现的设备为空，则返回
		if (var1 == null || Utils.isEmpty(var1.getAddress())) {
			return;
		}
		if (mBleWrapper.isConnected()) {
			return;
		}
		if (connect_num >= 1) {
			return;
		}
		// 根据发现的设备与已经绑定的设备进行匹配
		String address = var1.getAddress();

		for (SimpleDevice device : bind_devices) {
			if (device.getAddress().equals(address)) {
				current_connect_device = device;
				// 连接设备
				MeasureActivity.this.mBleWrapper.connect(current_connect_device
						.getAddress());
				connect_num++;
				break;
			}
		}

	}

	/**
	 * 处理接收到数据后的逻辑
	 * 
	 * @param var1
	 * @param var2
	 * @param var3
	 * @param characteristic
	 * @param datas十六位的十六进制字节数组
	 * @param value
	 * @param bytes
	 * @param time
	 */
	int last_data_length = 0;
	int current_data_length = 0;
	byte[] temp_datas = new byte[16];

	@Override
	public void uiNewValueForCharacteristic(BluetoothGatt var1,
			BluetoothDevice var2, BluetoothGattService var3,
			final BluetoothGattCharacteristic characteristic,
			final byte[] datas, final int value, final byte[] bytes,
			final String time) {

		this.runOnUiThread(new Runnable() {
			public void run() {
				current_data_length = datas.length;

				System.arraycopy(datas, 0, temp_datas, last_data_length,
						current_data_length);

				if (last_data_length + current_data_length == 16) {

					// 第一步：解密
					String new_content = ResultDeciphering
							.getDecipheringResult(temp_datas);

					if (Utils.isEmpty(new_content)) {
						new_content = "加密数据";
					}

					String content = new_content.trim().replace("+", "")
							.replace("$", "").replace("Di:", "")
							.replace("Ji:", "").replace("Vi:", "");
					// if (content.equals("Er:01")) {
					// return;
					// }
					// 此处加你们的数据是否合理的逻辑
					soundPool.play(1, 1, 1, 0, 0, 1);
					// 停止1秒，开启震动1秒，然后又停止1秒，又开启震动1秒，-1不重复，非-1为从pattern的指定下标开始重复。
					mVibrator.vibrate(new long[] { 0, 100, 0, 0 }, -1);

					receive_datas.add(new ReceiveData(bytesToHexString(temp_datas),
							content));
					mDataAdapter.notifyDataSetChanged();
					data_listview.smoothScrollToPosition(receive_datas.size() - 1);
					// 第二步：置0
					current_data_length = 0;
					last_data_length = 0;
				} else {
					last_data_length = datas.length;
				}

			}

		});

	}

	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	CountDownTimer cdt = new CountDownTimer(10000, 1000) {
		@Override
		public void onTick(long millisUntilFinished) {
			tv_connect_statue.setText("正在连接设备..." + millisUntilFinished / 1000);
		}

		@Override
		public void onFinish() {
			tv_connect_statue.setText("未连接到设备");
		}
	};

	@Override
	public void uiSuccessfulWrite(BluetoothGatt var1, BluetoothDevice var2,
			BluetoothGattService var3, BluetoothGattCharacteristic var4,
			final String var5) {
	}

	@Override
	public void uiFailedWrite(BluetoothGatt var1, BluetoothDevice var2,
			BluetoothGattService var3, BluetoothGattCharacteristic var4,
			final String var5) {
	}

	@Override
	public void uiGotNotification(BluetoothGatt var1, BluetoothDevice var2,
			BluetoothGattService var3, final BluetoothGattCharacteristic var4) {
	}

	@Override
	public void uiNewRssiAvailable(BluetoothGatt var1, BluetoothDevice var2,
			final int var3) {
	}
}
