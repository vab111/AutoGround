package com.ls.demo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.ls.ble.constant.BaseActivity;
import com.ls.ble.db.PreferenceMap;
import com.ls.ble.view.HeaderLayout;
import com.wutl.ble.tools.R;

/**
 * @author xiaobian
 * @version 创建时间：2017年9月20日 上午9:47:14
 * 
 */
public class SettingActivity extends Activity {
	private ImageView iv_switch;
	protected HeaderLayout headerLayout;
	private Activity instance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		instance = this;
		initView();
		initData();
		initAction();
	}

	public void initView() {
		headerLayout = (HeaderLayout) findViewById(R.id.ls_headerLayout);
		headerLayout.showTitle("设置");
		headerLayout.showLeftBackButton(new OnClickListener() {

			@Override
			public void onClick(View v) {
				instance.finish();
			}
		});
		iv_switch = (ImageView) findViewById(R.id.iv_switch);
		iv_switch.setSelected(new PreferenceMap(instance).getIsAutoConnect());
	}

	public void initData() {
	}

	public void initAction() {
		iv_switch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean tag = new PreferenceMap(instance).getIsAutoConnect();
				new PreferenceMap(instance).setIsAutoConnect(!tag);
				iv_switch.setSelected(new PreferenceMap(instance)
						.getIsAutoConnect());
			}
		});
	}

}
