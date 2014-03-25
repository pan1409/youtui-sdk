package cn.bidaround.youtui;
import cn.bidaround.youtui.service.AlertWindowService;
import cn.bidaround.youtui.social.ShareData;
import cn.bidaround.youtui.social.YoutuiConstants;
import cn.bidaround.youtui.ui.SharePopupWindow;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


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
			new SharePopupWindow(this,shareData,YoutuiConstants.WHITE_STYLE).show();
			break;
		case R.id.main_alert_bt:
			alertIt = new Intent(this, AlertWindowService.class);
			break;
		default:
			break;
		}
	}
	

}
