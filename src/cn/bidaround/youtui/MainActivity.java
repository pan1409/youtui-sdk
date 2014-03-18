package cn.bidaround.youtui;

import cn.bidaround.youtui.social.ShareData;
import cn.bidaround.youtui.ui.SharePopupWindow;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener{
	private Button popupBt;
	private ShareData shareData = new ShareData();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
	}
	void initView(){
		popupBt = (Button) findViewById(R.id.popup_bt);
		popupBt.setOnClickListener(this);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.you_tui, menu);
		return true;
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.popup_bt:
			new SharePopupWindow(this,shareData).show();
			break;

		default:
			break;
		}
	}
	

}
