package cn.bidaround.youtui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import cn.bidaround.youtui.social.ShareData;
import cn.bidaround.youtui.social.YoutuiConstants;

public class MainActivity extends Activity implements OnClickListener {
	private Button popupBt;
	private ShareData shareData = new ShareData();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		YouTui.init(this);
		initView();
	}

	void initView() {
		//模拟开发者传递数据
		shareData.setDescription("分享描述");
		shareData.setTitle("友推分享");
		shareData.setText("友推sdk通过奖励机制帮助开发者提升应用流量,并提供详尽的后台统计数据，快来试试吧 http://news.163.com/14/0415/02/9PRE4ETA0001121M.html");
		shareData.setTarget_url("http://www.baidu.com");
		shareData.setImageUrl("http://b.hiphotos.baidu.com/image/w%3D2048/sign=88209a66544e9258a63481eea8bad158/4610b912c8fcc3ce42febb319045d688d43f20f1.jpg");
		shareData.setImagePath(Environment.getExternalStorageDirectory()+YoutuiConstants.FILE_SAVE_PATH+"demo.png");
		
		popupBt = (Button) findViewById(R.id.popup_bt);
		popupBt.setOnClickListener(this);
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
