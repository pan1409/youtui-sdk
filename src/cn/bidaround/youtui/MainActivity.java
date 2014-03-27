package cn.bidaround.youtui;
import cn.bidaround.youtui.helper.DownloadImage;
import cn.bidaround.youtui.social.ShareData;
import cn.bidaround.youtui.social.YoutuiConstants;
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
		new  Runnable() {
			public void run() {				
				DownloadImage.downloadImage(shareData.getImageUrl(), YoutuiConstants.FILE_SAVE_PATH, "326.jpg");
			}
		}.run();	
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
			//初始化友推分享窗口
			new YouTui(this, shareData, YouTuiViewType.WHITE_FULL).init();
			break;
		case R.id.main_alert_bt:	
			break;
		default:
			break;
		}
	}
	

}
