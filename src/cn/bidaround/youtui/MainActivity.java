package cn.bidaround.youtui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import cn.bidaround.youtui.social.ShareData;

public class MainActivity extends Activity implements OnClickListener {
	private Button popupBt;
	private ShareData shareData = new ShareData();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		YouTui.init(this);
		initView();
	}

	void initView() {
		//模拟开发者传递数据
		shareData.setText("default share http://www.baidu.com/");
		shareData.setTitle("标题");
		shareData.setTarget_url("http://www.baidu.com");
		//shareData.setImageUrl("http://b.hiphotos.baidu.com/image/w%3D2048/sign=88209a66544e9258a63481eea8bad158/4610b912c8fcc3ce42febb319045d688d43f20f1.jpg");
		//shareData.setImagePath(Environment.getExternalStorageDirectory()+YoutuiConstants.FILE_SAVE_PATH+"326.jpg");
		popupBt = (Button) findViewById(R.id.popup_bt);
		popupBt.setOnClickListener(this);
	}

@Override
protected void onDestroy() {
	// TODO Auto-generated method stub
	YouTui.destroy();
	super.onDestroy();
}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.you_tui, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.popup_bt){
			YouTui.show(this, shareData);
		}
	}

}
