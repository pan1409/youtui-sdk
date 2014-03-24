package cn.bidaround.youtui;

import cn.bidaround.youtui.service.AlertWindowService;
import cn.bidaround.youtui.social.ShareData;
import cn.bidaround.youtui.ui.SharePopupWindow;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.AlteredCharSequence;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener{
	private Button popupBt;
	private Button main_alert_bt;
	private ShareData shareData = new ShareData();
	Intent alertIt;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
	}
	void initView(){
		shareData.setImageUrl("http://img0.bdstatic.com/img/image/shouye/quanzhixian.jpg");
		popupBt = (Button) findViewById(R.id.popup_bt);
		popupBt.setOnClickListener(this);
		main_alert_bt = (Button) findViewById(R.id.main_alert_bt);
		main_alert_bt.setOnClickListener(this);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.you_tui, menu);
		return true;
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.popup_bt:
			new SharePopupWindow(this,shareData).show();
			break;
		case R.id.main_alert_bt:
			alertIt = new Intent(this, AlertWindowService.class);
			new Thread(){
				public void run() {
					startService(alertIt);
				};
			}.start();
			
			Log.i("-----", "main");
			Toast.makeText(this, "alert be clicked", Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
	}
	

}
