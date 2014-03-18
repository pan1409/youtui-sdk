package cn.bidaround.youtui.ui;

import com.tencent.mm.sdk.plugin.MMPluginProviderConstants.OAuth;
import com.tencent.weibo.sdk.android.api.util.Util;
import cn.bidaround.youtui.R;
import cn.bidaround.youtui.social.OtherShare;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


public class TencentWBSkipActivity extends Activity implements OnClickListener {
	private Button qqwbskip_auth_bt;
	private Button qqwbskip_share_bt;
	private Button qqwbskip_cancel_bt;
	private static long appid = 801443192;
	private static String app_secket = "45d65f2d2650637c96ece74f4a67b686";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tencentwbskip_activity);
		initView();
		
	}

	void initView() {
		qqwbskip_auth_bt = (Button) findViewById(R.id.qqwbskip_auth_bt);
		qqwbskip_share_bt = (Button) findViewById(R.id.qqwbskip_share_bt);
		qqwbskip_cancel_bt = (Button) findViewById(R.id.qqwbskip_cancel_bt);
		qqwbskip_auth_bt.setOnClickListener(this);
		qqwbskip_share_bt.setOnClickListener(this);
		qqwbskip_cancel_bt.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.qqwbskip_auth_bt:

			break;
		case R.id.qqwbskip_share_bt:

			break;
		case R.id.qqwbskip_cancel_bt:

			break;

		default:
			break;
		}

	}

}
