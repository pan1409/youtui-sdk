package cn.bidaround.youtui.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.bidaround.youtui.R;
import cn.bidaround.youtui.component.MyProgressDialog;
import cn.bidaround.youtui.helper.AccessTokenKeeper;
import cn.bidaround.youtui.point.ChannelId;
import cn.bidaround.youtui.point.YtPoint;
import cn.bidaround.youtui.social.KeyInfo;
import cn.bidaround.youtui.social.QQOpenShare;
import cn.bidaround.youtui.social.RennShare;
import cn.bidaround.youtui.social.ShareData;
import cn.bidaround.youtui.social.SinaShare;
import cn.bidaround.youtui.social.YoutuiConstants;

import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;

/**
 * author:gaopan
 */
public class ShareActivity extends Activity implements IWeiboHandler.Response, OnClickListener {
	private SinaShare sinaShare;
	private ShareData shareData;
	private String from;
	private IWeiboShareAPI iWeiboShareAPI;
	private int[] pointArr;
	private View shareedit_back_linelay, shareedit_share_bt;
	private TextView shareedit_title_text, shareedit_sharetitle_text;
	private EditText shareedit_sharetext_edit;
	private ImageView shareeidt_shareimage_image;
	private WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		shareData = (ShareData) getIntent().getExtras().getSerializable("shareData");
		pointArr = getIntent().getExtras().getIntArray("pointArr");

		iWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, KeyInfo.SinaWeibo_AppKey);
		// 判断分享的媒体
		from = getIntent().getExtras().getString("from");
		if ("sina".equals(from)) {
			MyProgressDialog.loadingBar(this);
			initView("新浪微博");
			if (AccessTokenKeeper.readAccessToken(this).isSessionValid()) {
				new Thread() {
					public void run() {
						sinaShare = new SinaShare(ShareActivity.this, shareData);
						sinaShare.shareToSina();
					};
				}.start();
			} else {
				sinaShare = new SinaShare(this, shareData);
				sinaShare.sinaAuth();
			}

		} else if ("renren".equals(from)) {
			initView("人人");
		} else if ("QQWB".equals(from)) {
			initView("腾讯微博");
		} else if ("QQ".equals(from)) {
			MyProgressDialog.loadingBar(this);
			initView("QQ");
			new QQOpenShare(this, pointArr, "QQ").shareToQQ();
		} else if ("Qzone".equals(from)) {
			MyProgressDialog.loadingBar(this);
			initView("QQ空间");
			new QQOpenShare(this, pointArr, "Qzone").shareToQzone();
		}else if("know".equals(from)||"check".equals(from)){
			MyProgressDialog.loadingBar(this);
			initPointView();
		}
	}

	/**
	 * 初始化分享界面
	 * @param title
	 */
	private void initView(String title) {
		View rennView = LayoutInflater.from(this).inflate(R.layout.shareedit_activity, null);
		setContentView(rennView);
		shareedit_back_linelay = rennView.findViewById(R.id.shareedit_back_linelay);
		shareedit_back_linelay.setOnClickListener(this);
		shareedit_share_bt = rennView.findViewById(R.id.shareedit_share_bt);
		shareedit_share_bt.setOnClickListener(this);
		shareedit_title_text = (TextView) rennView.findViewById(R.id.shareedit_title_text);
		shareedit_title_text.setText(title);
		shareedit_sharetitle_text = (TextView) rennView.findViewById(R.id.shareedit_sharetitle_text);
		shareedit_sharetitle_text.setText(shareData.getTitle());
		shareedit_sharetext_edit = (EditText) rennView.findViewById(R.id.shareedit_sharetext_edit);
		shareedit_sharetext_edit.setText(shareData.getText());
		shareeidt_shareimage_image = (ImageView) rennView.findViewById(R.id.shareeidt_shareimage_image);
		Bitmap bitmap = BitmapFactory.decodeFile(shareData.getImagePath());
		shareeidt_shareimage_image.setImageBitmap(bitmap);
	}
	/**
	 * 初始化查看和了解积分界面
	 */
	@SuppressLint("SetJavaScriptEnabled")
	private void initPointView(){
		webView = new WebView(this);
		webView.setWebViewClient(new WebViewClient());
		// WebSettings 几乎浏览器的所有设置都在该类中进行
		WebSettings webSettings = webView.getSettings();
		webSettings.setDefaultTextEncodingName("utf-8");
		webSettings.setBuiltInZoomControls(true);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSavePassword(true);
		webSettings.setSaveFormData(false);
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		webSettings.setDatabaseEnabled(true);
		// 重新弹出框
		TelephonyManager teleMan = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		webView.setWebChromeClient(new MyWebChromeClient());
		//
		if("check".equals(from)){
			webView.loadUrl(YoutuiConstants.YT_URL + "/activity/mypoints" + "?appId=" + KeyInfo.YouTui_AppKey + "&cardNum=" + teleMan.getSimSerialNumber()+"&imei="+teleMan.getDeviceId());
		}else if("know".equals(from)){
			webView.loadUrl(YoutuiConstants.YT_URL + "/activity/actIntroduce" + "?appId=" + KeyInfo.YouTui_AppKey + "&cardNum=" + teleMan.getSimSerialNumber()+"&imei="+teleMan.getDeviceId());
		}
		
		setContentView(webView);
	}
	
	

	final class MyWebChromeClient extends WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int progress) {
			ShareActivity.this.setTitle("加载中...");
			ShareActivity.this.getWindow().setFeatureInt(Window.FEATURE_PROGRESS, progress * 100);
			ShareActivity.this.setProgress(progress);
			if (progress >= 80) {
				ShareActivity.this.setTitle("");
				MyProgressDialog.dismiss();
			}
		}

	}

	/**
	 * 新浪微博分享完会调用该方法
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		MyProgressDialog.dismiss();
		Log.i("onNewIntent", "onNewIntent");
		setIntent(intent);
		if ("sina".equals(from)) {
			iWeiboShareAPI.handleWeiboResponse(intent, this);
		}
		super.onNewIntent(intent);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		MyProgressDialog.dismiss();
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ("sina".equals(from) && sinaShare != null) {
			sinaShare.sinaResult(requestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		MyProgressDialog.dismiss();
		finish();
		super.onRestart();
	}

	/**
	 * 新浪分享回调
	 */
	@Override
	public void onResponse(BaseResponse baseResp) {
		switch (baseResp.errCode) {

		case WBConstants.ErrorCode.ERR_OK:
			Toast.makeText(this, "分享成功", Toast.LENGTH_SHORT).show();
			YtPoint.sharePoint(this, KeyInfo.YouTui_AppKey, ChannelId.SINACHANNEL, pointArr);
			YtPoint.getInstance(this).refresh(this);
			break;

		case WBConstants.ErrorCode.ERR_CANCEL:
			Toast.makeText(this, "分享取消", Toast.LENGTH_SHORT).show();
			break;

		case WBConstants.ErrorCode.ERR_FAIL:
			Toast.makeText(this, "分享失败", Toast.LENGTH_SHORT).show();
			break;

		default:
			break;
		}

		finish();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.shareedit_back_linelay:
			this.finish();
			break;
		// 分享按钮点击事件
		case R.id.shareedit_share_bt:
			if ("renren".equals(from)) {
				new RennShare(this, pointArr).shareToRenn();
			} else if ("QQWB".equals(from)) {
				new QQOpenShare(ShareActivity.this, pointArr, "QQWB").shareToQQWB();
			} else if ("QQ".equals(from)) {
				new QQOpenShare(this, pointArr, "QQ").shareToQQ();
			} else if ("Qzone".equals(from)) {
				new QQOpenShare(this, pointArr, "Qzone").shareToQzone();
			} else if ("sina".equals(from)) {
				new Thread() {
					public void run() {
						sinaShare = new SinaShare(ShareActivity.this, shareData);
						sinaShare.shareToSina();
					};
				}.start();
			}
			break;

		default:
			break;
		}
	}

}
