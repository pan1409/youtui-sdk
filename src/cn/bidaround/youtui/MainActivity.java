package cn.bidaround.youtui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import cn.bidaround.youtui.login.AuthLogin;
import cn.bidaround.youtui.social.ShareData;

public class MainActivity extends Activity implements OnClickListener {
	private Button popupBt, listBt;
	private View main_sina_imageview, main_qq_imageview, main_tencentwb_imageview;
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

		// 模拟开发者传递数据
		// shareData.setDescription("友推积分组件");
		// shareData.setTitle("友推分享");
		// shareData.setText("通过友推积分组件，开发者几行代码就可以为应用添加分享送积分功能，并提供详尽的后台统计数据，除了本身具备的分享功能外，开发者也可将积分功能单独集成在已有分享组件的app上，快来试试吧 http://youtui.mobi");
		// shareData.setTarget_url("http://youtui.mobi");
		// shareData.setImageUrl("http://cdnup.b0.upaiyun.com/media/image/default.png");
		// shareData.setImagePath(Environment.getExternalStorageDirectory()+YoutuiConstants.FILE_SAVE_PATH+"demo.png");

		popupBt = (Button) findViewById(R.id.popup_bt);
		popupBt.setOnClickListener(this);

		listBt = (Button) findViewById(R.id.list_bt);
		listBt.setOnClickListener(this);

		main_sina_imageview = findViewById(R.id.main_sina_imageview);
		main_sina_imageview.setOnClickListener(this);
		main_qq_imageview = findViewById(R.id.main_qq_imageview);
		main_qq_imageview.setOnClickListener(this);
		main_tencentwb_imageview = findViewById(R.id.main_tencentwb_imageview);
		main_tencentwb_imageview.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.you_tui, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// if(shareData==null){
		// Log.i("--MainActivity shareData--", "null");
		// }
		if (v.getId() == R.id.popup_bt) {
			YouTui.show(this, shareData, YouTuiViewType.BLACK_POPUP);
		} else if (v.getId() == R.id.list_bt) {
			YouTui.show(this, shareData, YouTuiViewType.WHITE_LIST);
		} else if (v.getId() == R.id.main_sina_imageview) {
			AuthLogin sinaLogin = new AuthLogin() {
				@Override
				public void onAuthComplete() {
					Log.i("--MainActivity--", "onAuthComplete");
				};
			};
			sinaLogin.sinaAuth(this);
		} else if (v.getId() == R.id.main_qq_imageview) {
			AuthLogin qqLogin = new AuthLogin() {
				@Override
				public void onAuthComplete() {
					Log.i("--MainActivity qq onAuthComplete--", "onAuthComplete");
				}
			};
			qqLogin.qqAuth(this);

		} else if (v.getId() == R.id.main_tencentwb_imageview) {
			AuthLogin tencentWbLogin = new AuthLogin() {
				@Override
				public void onAuthComplete() {
					Log.i("--MainActivity--", "onAuthComplete");
				};

			};
			tencentWbLogin.tencentWbAuth(this);
		}
	}

}
