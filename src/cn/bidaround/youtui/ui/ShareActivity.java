package cn.bidaround.youtui.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import cn.bidaround.youtui.YouTuiAcceptor;
import cn.bidaround.youtui.helper.AccessTokenKeeper;
import cn.bidaround.youtui.helper.Util;
import cn.bidaround.youtui.point.ChannelId;
import cn.bidaround.youtui.point.YtPoint;
import cn.bidaround.youtui.social.KeyInfo;
import cn.bidaround.youtui.social.QQOpenShare;
import cn.bidaround.youtui.social.RennShare;
import cn.bidaround.youtui.social.ShareData;
import cn.bidaround.youtui.social.SinaShare;
import cn.bidaround.youtui.social.TencentWbShare;
import cn.bidaround.youtui.social.YoutuiConstants;

import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;

/**
 * @author gaopan
 * @since 2014/4/21 微信分享之外的其他分享平台分享都在该activity完成，这样避免用户集成时注册过多的activity
 */
public class ShareActivity extends YTBaseShareActivity implements IWeiboHandler.Response, OnClickListener {
	private SinaShare sinaShare;
	private String from;
	private IWeiboShareAPI iWeiboShareAPI;
	private int[] pointArr;
	private WebView webView;
	private View pointweb_back_linelay;
	private static final int GET_APP_INFO = 0;
	public static final int RENN_SHARE_ERROR = 1;
	public static final int RENN_HTTP_ERROR= 2;
	private String uniqueCode;
	// 如果是分享应用并且应用信息还没有获取到，在分享时获取应用信息后再进行分享
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case GET_APP_INFO:
				doShare();
				break;
				/**处理人人分享完成*/
			case RENN_SHARE_ERROR:
				Toast.makeText(ShareActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
				ShareActivity.this.finish();
				break;
				/**处理人人分享网络错误*/
			case RENN_HTTP_ERROR:
				Toast.makeText(ShareActivity.this, "连接到服务器错误...", Toast.LENGTH_SHORT).show();
				ShareActivity.this.finish();
				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		pointArr = getIntent().getExtras().getIntArray("pointArr");
		shareData = (ShareData) getIntent().getExtras().getSerializable("shareData");
		iWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, KeyInfo.SinaWeibo_AppKey);
		// 判断分享的媒体
		from = getIntent().getExtras().getString("from");
		// 我的积分和查看积分页面

		// 如果是分享应用并且应用信息还没有获取到，在分享时获取应用信息
		if (shareData.isAppShare) {
			if (YtPoint.appText == null || YtPoint.appDownUrl == null || YtPoint.appLogoUrl == null || YtPoint.appTitlet == null) {
				new Thread() {
					public void run() {
						getAppInfo(shareData);
						mHandler.sendEmptyMessage(GET_APP_INFO);
					};
				}.start();

			} else {
				doShare();
			}
		} else {
			doShare();
		}
	}

	private void doShare() {
		if ("sina".equals(from)) {
			initView("新浪微博");
			if (AccessTokenKeeper.readAccessToken(this).isSessionValid()) {
				// 如果已经授权直接分享
				// Log.i("--shareactivity--", "isSessionValid");
				sinaShare = new SinaShare(ShareActivity.this);
				uniqueCode = sinaShare.shareToSina(shareData);
			} else {
				// 如果没有授权，先授权
				sinaShare = new SinaShare(this);
				sinaShare.sinaAuth();
			}
		} else if ("renren".equals(from)) {
			initView("人人");
		} else if ("QQWB".equals(from)) {
			initView("腾讯微博");
		} else if ("QQ".equals(from)) {
			Util.showProgressDialog(this, "分享中...");
			initView("QQ");
			new QQOpenShare(this, pointArr, "QQ").shareToQQ();
		} else if ("Qzone".equals(from)) {
			Util.showProgressDialog(this, "分享中...");
			initView("QQ空间");
			new QQOpenShare(this, pointArr, "Qzone").shareToQzone();
		} else if ("know".equals(from) || "check".equals(from)) {
			Util.showProgressDialog(this, "加载中...");
			initPointView();
		}
	}

	/**
	 * 初始化查看和了解积分界面
	 */
	@SuppressLint("SetJavaScriptEnabled")
	private void initPointView() {
		View view = LayoutInflater.from(this).inflate(YouTuiAcceptor.res.getIdentifier("point_webview", "layout", YouTuiAcceptor.packName), null);
		// 返回键R.id.pointweb_back_linelay
		pointweb_back_linelay = view.findViewById(YouTuiAcceptor.res.getIdentifier("pointweb_back_linelay", "id", YouTuiAcceptor.packName));
		pointweb_back_linelay.setOnClickListener(this);
		// webview
		webView = (WebView) view.findViewById(YouTuiAcceptor.res.getIdentifier("pointweb_webview", "id", YouTuiAcceptor.packName));
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
		webView.setWebChromeClient(new MyWebChromeClient());
		webView.setOnKeyListener(new WebViewOnKayListener());
		// 查看和了解积分
		if ("check".equals(from)) {
			webView.loadUrl(YoutuiConstants.YT_URL + "/activity/mypoints" + "?appId=" + KeyInfo.YouTui_AppKey + "&cardNum=" + YouTuiAcceptor.cardNum + "&imei=" + YouTuiAcceptor.imei);
		} else if ("know".equals(from)) {
			webView.loadUrl(YoutuiConstants.YT_URL + "/activity/actIntroduce" + "?appId=" + KeyInfo.YouTui_AppKey + "&cardNum=" + YouTuiAcceptor.cardNum + "&imei=" + YouTuiAcceptor.imei);
		}

		setContentView(view);
	}

	class WebViewOnKayListener implements View.OnKeyListener {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					if (webView.canGoBack()) {
						webView.goBack();
						return true;
					} else {
						ShareActivity.this.finish();
						return true;
					}
				}
			}
			return false;
		}

	}

	/**
	 * 加载到80%结束进度条
	 */
	final class MyWebChromeClient extends WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int progress) {
			ShareActivity.this.setTitle("加载中...");
			ShareActivity.this.getWindow().setFeatureInt(Window.FEATURE_PROGRESS, progress * 100);
			ShareActivity.this.setProgress(progress);
			if (progress >= 80) {
				ShareActivity.this.setTitle("");
				Util.dismissDialog();
			}
		}

	}

	/**
	 * 新浪微博分享完会调用该方法
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		// Log.i("onNewIntent", "onNewIntent");
		setIntent(intent);
		if ("sina".equals(from)) {
			iWeiboShareAPI.handleWeiboResponse(intent, this);
		}
		super.onNewIntent(intent);
	}

	/**
	 * 新浪微博授权的回调
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ("sina".equals(from) && sinaShare != null) {
			sinaShare.sinaResult(requestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 新浪分享回调
	 */
	@Override
	public void onResponse(BaseResponse baseResp) {
		switch (baseResp.errCode) {

		case WBConstants.ErrorCode.ERR_OK:
			YtPoint.sharePoint(this, KeyInfo.YouTui_AppKey, ChannelId.SINAWEIBO, pointArr, shareData.getTarget_url(), !shareData.isAppShare, uniqueCode);
			break;

		case WBConstants.ErrorCode.ERR_CANCEL:
			Toast.makeText(this, "分享取消", Toast.LENGTH_SHORT).show();
			break;

		case WBConstants.ErrorCode.ERR_FAIL:
			// Log.i("--shareactivity--", baseResp.errMsg);
			if ("auth faild!!!!".equals(baseResp.errMsg)) {
				Toast.makeText(this, "新浪微博授权失败，请重新获取授权", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, "分享失败", Toast.LENGTH_SHORT).show();
			}
			break;

		default:
			break;
		}

		finish();

	}

	/**
	 * 分享和返回监听
	 */
	@Override
	public void onClick(View v) {
		// 返回
		if (v.getId() == YouTuiAcceptor.res.getIdentifier("shareedit_back_linelay", "id", YouTuiAcceptor.packName)) {
			this.finish();
		} else if (v.getId() == YouTuiAcceptor.res.getIdentifier("shareedit_share_bt", "id", YouTuiAcceptor.packName)) {
			// 分享,先判断网络连接是否可用
//			if (NetUtil.isNetworkConnected(this)) {
				if ("renren".equals(from)) {
					Util.showProgressDialog(this, "分享中...");
					new RennShare(this, pointArr,mHandler).shareToRenn();
				} else if ("QQWB".equals(from)) {
					Util.showProgressDialog(this, "分享中...");
					new TencentWbShare(this, pointArr).shareToTencentWb();
				} else if ("QQ".equals(from)) {
					new QQOpenShare(this, pointArr, "QQ").shareToQQ();
				} else if ("Qzone".equals(from)) {
					new QQOpenShare(this, pointArr, "Qzone").shareToQzone();
				} else if ("sina".equals(from)) {
					sinaShare = new SinaShare(ShareActivity.this);
					sinaShare.shareToSina(shareData);
				}
//			} else {
//				Toast.makeText(this, "无网络连接，请查看您的网络情况", Toast.LENGTH_SHORT).show();
//			}
		} else if (v.getId() == YouTuiAcceptor.res.getIdentifier("pointweb_back_linelay", "id", YouTuiAcceptor.packName)) {
			finish();
		} else if (v.getId() == YouTuiAcceptor.res.getIdentifier("shareedit_attention_bt", "id", YouTuiAcceptor.packName)) {
			Toast.makeText(this, "关注列表", Toast.LENGTH_SHORT).show();
		}
	}
}
