package cn.bidaround.youtui.ui;
import cn.bidaround.youtui.social.RennShare;
import cn.bidaround.youtui.social.SinaShare;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
/**
 * author:gaopan
 */
public class ShareActivity extends Activity {
	private SinaShare sinaShare;
	private String from;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		from = getIntent().getExtras().getString("from");
		if("sina".equals(from)){
			sinaShare = new SinaShare(this);
			sinaShare.shareToSina();
		}else if("renren".equals(from)){
			new RennShare(this).shareToRenn();
		}else if("TencentWB".equals(from)){
			
		}
	}
	/*
	 * ?? 下面两个方法为官方使用的处理分享后结果的函数，但是无法被调用
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		Log.i("onNewIntent", "onNewIntent");
		super.onNewIntent(intent);
		setIntent(intent);
		sinaShare.handResp(intent);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("onActivityResult", "onActivityResult");
		super.onActivityResult(requestCode, resultCode, data);
	}
}
