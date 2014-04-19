package cn.bidaround.youtui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import cn.bidaround.youtui.social.ShareData;

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
		shareData.setDescription("友推积分组件");
		shareData.setTitle("友推分享");
		shareData.setText("通过友推积分组件，开发者几行代码就可以为应用添加分享送积分功能，并提供详尽的后台统计数据，除了本身具备的分享功能外，开发者也可将积分功能单独集成在已有分享组件的app上，快来试试吧 http://youtui.mobi");
		shareData.setTarget_url("http://youtui.mobi");
		shareData.setImageUrl("http://cdnup.b0.upaiyun.com/media/image/default.png");
		//shareData.setImagePath(Environment.getExternalStorageDirectory()+YoutuiConstants.FILE_SAVE_PATH+"demo.png");
		
		popupBt = (Button) findViewById(R.id.popup_bt);
		popupBt.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.you_tui, menu);
		return true;
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
	}
	

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.popup_bt){
			YouTui.show(this, shareData,YouTuiViewType.BLACK_POPUP);
		}
	}

}
