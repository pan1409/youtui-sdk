package cn.bidaround.youtui;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import cn.bidaround.youtui.helper.AppHelper;
import cn.bidaround.youtui.helper.DownloadImage;
import cn.bidaround.youtui.social.EmailActivity;
import cn.bidaround.youtui.social.RenrenSSOActivity;
import cn.bidaround.youtui.social.SMSActivity;
import cn.bidaround.youtui.social.SinaWeiboSSOActivity;
import cn.bidaround.youtui.social.TencentDirectionalShareActivity;
import cn.bidaround.youtui.social.TencentQZoneShareActivity;
import cn.bidaround.youtui.social.TencentWeiboSSOActivity;
import cn.bidaround.youtui.social.WeiXinShareActivity;
import cn.bidaround.youtui.social.YoutuiConstants;

/**
 * @author steven http://www.youtui.mobi 技术支持QQ: 1030311324
 */
public class YouTui extends Activity {

	private MenuItem exit;
	private WebView webView;
	private String appId;
	private Handler mHandler = new Handler();
	private ProgressDialog loadingDialog;
	private String imei,sdk,model,sys;
	private String homeUrl = "";
	private String backUrl = "";

	@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/* 初始化内嵌浏览器 */
		initWebView();
		/* 进度条 */
		loadingBar();
		/* 装载数据内容 */
		loadContent();
		/* 获取设备信息 */
		readPhoneInfo();
	}

	/* 显示友推组件,供外部调用 */
	public void show(Activity context, int type) {
		Intent intent = new Intent();
		// 调用友推组件，方便用户分享推荐，或举行推荐奖励活动
		intent.setClass(context, YouTui.class);
		context.startActivity(intent);
	}
	
	/* 获取设备信息 */
	private void readPhoneInfo(){
		TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
		if(tm != null){
			imei = tm.getDeviceId(); /* 获取imei号 */
		}
		sdk = android.os.Build.VERSION.SDK;    // SDK号
		model = android.os.Build.MODEL;   // 手机型号
		sys = android.os.Build.VERSION.RELEASE;  // android系统版本号
	}
	/**
	 * 进度条
	 */
	private void loadingBar() {
		// 进度条
		loadingDialog = new ProgressDialog(YouTui.this);
		// 设置进度条风格，风格为圆形，旋转的
		loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// 设置ProgressDialog 提示信息
		loadingDialog.setMessage("加载中…");
		// 设置ProgressDialog 的进度条是否不明确
		loadingDialog.setIndeterminate(false);
		// 设置ProgressDialog 是否可以按退回按键取消
		loadingDialog.setCancelable(true);

		int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
		int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
		loadingDialog.getWindow().setLayout(screenWidth * 2 / 3,
				screenHeight / 5);
		loadingDialog.show();
	}

	/**
	 * 初始化内嵌浏览器
	 */
	@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
	private void initWebView() {
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
		webView.setWebChromeClient(new MyWebChromeClient());
		// 接收web端 js 方法
		webView.addJavascriptInterface(new YouTuiJavaScriptInterface(),
				"android");
		//监听滑动事件
		webView.setOnTouchListener(new OnTouchListener() {  
			float xDown = 0, yDown, xUp, yUp;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
            	
            	if(event.getAction() == MotionEvent.ACTION_DOWN){
            		xDown = event.getX();
            		yDown = event.getY();
            	}
            	else if(event.getAction() == MotionEvent.ACTION_UP){
            		xUp = event.getX();
            		yUp = event.getY();
            		if(xUp - xDown < -30){
            			//向左滑动
            			webView.loadUrl("javascript:shared._switch_tab('left')");
            		}else if(xUp - xDown > 30){
            			//向右滑动
            			webView.loadUrl("javascript:shared._switch_tab('right')"); 
            		}
            	}
               return false;
            }
    });
		setContentView(webView);
	}

	/**
	 * 装载数据内容
	 */
	private void loadContent() {
		ApplicationInfo info;
		try {
			info = getPackageManager().getApplicationInfo(getPackageName(),
					PackageManager.GET_META_DATA);
			int msg = info.metaData.getInt("YOUTUI_APPKEY");
			msg = msg + 0;
			appId = String.valueOf(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (appId == null) {
			jumpToWeb("/activity/noappId/", null);
		} else {
			//homeUrl为第一个页面的地址
			homeUrl = "http://youtui.mobi"+"/activity/shared/get?appId=" + appId;
			jumpToWeb("/activity/shared/get?appId=" + appId, null);
		}
	}

	/**
	 * 结束分享
	 */
	private void end() {
		this.finish();
	}

	/**
	 * webview 跳转到网页
	 */
	private void jumpToWeb(String url, JSONObject json) {
		String urlString = "http://youtui.mobi";
		//String urlString = "http://192.168.2.101";
		urlString += url;
		//如果已配置url则直接使用不用组装
		if(url.contains("http://")){
			urlString = url;
		}

		if (json != null) {
			urlString += "?";
			Iterator keys = json.keys();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				urlString += key + "=";
				try {
					String value = json.getString(key);
					urlString += value + "&";
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		//backUrl为点击返回按钮后要返回的页面（授权后的页面）
		if(urlString.contains(backUrl))
			backUrl = urlString;
		webView.loadUrl(urlString);
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/* 添加退出菜单 */
		exit = menu.add("Exit");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/* 结束Activity */
		finish();
		return super.onOptionsItemSelected(item);
	}

	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		view.loadUrl(url);
		return true;
	}

	public boolean onKeyDown(int keyCoder, KeyEvent event) {
		if (webView.canGoBack() && keyCoder == KeyEvent.KEYCODE_BACK) {
			//获当前页面的URL
			String pageUrl = webView.getUrl();
			//如果此页面是授权后的开始页面，则返回到homeUrl
			if(pageUrl.contains("/activity/join?")&&pageUrl.contains("&recommender=")
					&&pageUrl.contains("&picUrl=")&&pageUrl.contains("&weiboUid=")&&pageUrl.contains("&type=")) {
				webView.loadUrl(homeUrl);
				return true;
			}
			//如果此页面是homeUrl，即第一个页面，则结束该Activity
			else if (pageUrl.contains("/activity/shared/get?appId=")) {
				this.finish();
				return true;
			}
			//如果是其他页面，则返回到backUrl，即授权后的开始页面
			else {
				webView.loadUrl(backUrl);
				return true;
			}
		}
			
		if (keyCoder == KeyEvent.KEYCODE_F5) {
			// f5刷新页面
			webView.reload();
			return true;
		}
		return super.onKeyDown(keyCoder, event);
	}

	/**
	 * 继承WebChromeClient类 对js弹出框时间进行处理
	 * 
	 */
	final class MyWebChromeClient extends WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int progress) {
			YouTui.this.setTitle("加载中...");
			YouTui.this.getWindow().setFeatureInt(Window.FEATURE_PROGRESS,
					progress * 100);
			YouTui.this.setProgress(progress);
			if (progress >= 80) {
				YouTui.this.setTitle("");
				loadingDialog.dismiss();
			}
		}

		/**
		 * 处理alert弹出框
		 */
		@Override
		public boolean onJsAlert(WebView view, String url, String message,
				final JsResult result) {
			result.confirm();
			showAlert(message);
			//end();
			return true;
		}

		/**
		 * 处理confirm弹出框
		 */
		@Override
		public boolean onJsConfirm(WebView view, String url, String message,
				JsResult result) {
			result.confirm();
			return super.onJsConfirm(view, url, message, result);
		}

		/**
		 * 处理prompt弹出框
		 */
		@Override
		public boolean onJsPrompt(WebView view, String url, String message,
				String defaultValue, JsPromptResult result) {
			result.confirm();
			return super.onJsPrompt(view, url, message, message, result);
		}
	}

	/**
	 * android 与 浏览器端交互
	 */
	final class YouTuiJavaScriptInterface {
		YouTuiJavaScriptInterface() {
		}

		/**
		 * 装载图片
		 */
		public void autoLoadImage(final String urlpath) {
			String url = urlpath;
			// 保存在sd卡中的文件夹名称
			String savePath = YoutuiConstants.FILE_SAVE_PATH;
			DownloadImage.downloadImage(url, savePath, appId);
		}
		
		/**
		 * 该方法被浏览器端调用，接收邀请
		 */
		public void clickOnAndroidAccept() {
			mHandler.post(new Runnable() {
				@SuppressLint("NewApi")
				public void run() {
					String phoneInfo = imei + ":" + sdk + ":" + model + ":" + sys;
					webView.loadUrl("javascript:accept_invite_code._accept"+"('"+ phoneInfo +"')"); 
				}
			});
		}

		/**
		 * 该方法被浏览器端调用，复制链接
		 */
		public void clickOnAndroidCopy(final String message) {
			mHandler.post(new Runnable() {
				@SuppressLint("NewApi")
				public void run() {
					// API 11之前用android.text.ClipboardManager;
					// API 11之后用android.content.ClipboardManager
					if (android.os.Build.VERSION.SDK_INT >= 11) {
						android.content.ClipboardManager clip = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
						clip.setPrimaryClip(android.content.ClipData
								.newPlainText("link", message));
						if (clip.hasPrimaryClip()) {
							showAlert("复制成功");
						} else {
							showAlert("复制失败，请手动复制");
						}
					} else {
						android.text.ClipboardManager clip = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
						clip.setText(message);
						if (clip.hasText()) {
							showAlert("复制成功");
						} else {
							showAlert("复制失败，请手动复制");
						}
					}
				}
			});
		}

		/**
		 * 该方法被浏览器端调用,新浪微博授权
		 */
		public void clickOnAndroidSinaWeibo(final String state) {
			mHandler.post(new Runnable() {
				@SuppressLint("NewApi")
				public void run() {
					String appId = getAppIdSina("SINA_WEIBO_APP_ID");
					if (!AppHelper.isSinaWeiboExisted(getApplicationContext())) {
						Intent intent = new Intent();
						intent.putExtra("state", state);
						
						onActivityResult(
								YoutuiConstants.SINA_WEIBO_REQUEST_CODE,
								YoutuiConstants.APP_NOT_EXIST, intent);
					} else if (appId == null) {
						Intent intent = new Intent();
						intent.putExtra("state", state);
						onActivityResult(
								YoutuiConstants.SINA_WEIBO_REQUEST_CODE,
								YoutuiConstants.APP_ID_MISSING, intent);
					} else {
						Intent intent = new Intent();
						intent.setClass(YouTui.this, SinaWeiboSSOActivity.class);
						intent.putExtra("state", state);
						intent.putExtra("appid", appId);
						startActivityForResult(intent,
								YoutuiConstants.SINA_WEIBO_REQUEST_CODE);
					}
				}
			});
		}

		/**
		 * 该方法被浏览器端调用,腾讯微博授权
		 */
		public void clickOnAndroidTencentWeibo(final String state) {
			mHandler.post(new Runnable() {
				@SuppressLint("NewApi")
				public void run() {
					if (!AppHelper.isTencentQQExisted(getApplicationContext())) {
						Intent intent = new Intent();
						intent.putExtra("state", state);
						onActivityResult(
								YoutuiConstants.TENCENT_WEIBO_REQUEST_CODE,
								YoutuiConstants.APP_NOT_EXIST, intent);
					} else {
						Intent intent = new Intent();
						intent.setClass(YouTui.this,
								TencentWeiboSSOActivity.class);
						intent.putExtra("state", state);
						startActivityForResult(intent,
								YoutuiConstants.TENCENT_WEIBO_REQUEST_CODE);
					}
				}
			});
		}

		/**
		 * 该方法被浏览器端调用,QQ空间授权
		 */
		public void clickOnAndroidQZone(final String title,
				final String description, final String target_url,
				final String image_url, final String state) {
			mHandler.post(new Runnable() {
				@SuppressLint("NewApi")
				public void run() {
					Intent intent = new Intent();
					intent.setClass(YouTui.this,
							TencentQZoneShareActivity.class);
					intent.putExtra("title", title);
					intent.putExtra("description", description);
					intent.putExtra("target_url", target_url);
					intent.putExtra("image_url", image_url);
					intent.putExtra("state", state);
					startActivityForResult(intent,
							YoutuiConstants.TENCENT_QZONE_REQUEST_CODE);
				}
			});
		}

		/**
		 * 该方法被浏览器端调用,人人网授权
		 */
		public void clickOnAndroidRenren(final String state) {
			mHandler.post(new Runnable() {
				@SuppressLint("NewApi")
				public void run() {
					if (!AppHelper.isRenrenExisted(getApplicationContext())) {
						Intent intent = new Intent();
						intent.putExtra("state", state);
						onActivityResult(YoutuiConstants.RENREN_REQUEST_CODE,
								YoutuiConstants.APP_NOT_EXIST, intent);
					} else {
						Intent intent = new Intent();
						intent.setClass(YouTui.this, RenrenSSOActivity.class);
						intent.putExtra("state", state);
						startActivityForResult(intent,
								YoutuiConstants.RENREN_REQUEST_CODE);
					}
				}
			});
		}

		/**
		 * 该方法被浏览器端调用,QQ定向分享
		 */
		public void clickOnAndroidQQShare(final String title,
				final String description, final String target_url,
				final String image_url) {
			mHandler.post(new Runnable() {
				@SuppressLint("NewApi")
				public void run() {
					if (!AppHelper.isTencentQQExisted(getApplicationContext())) {
						onActivityResult(
								YoutuiConstants.TENCENT_DIRECTIONAL_SHARE_REQUEST_CODE,
								YoutuiConstants.APP_NOT_EXIST, null);
					} else {
						Intent intent = new Intent();
						intent.setClass(YouTui.this,
								TencentDirectionalShareActivity.class);
						intent.putExtra("title", title);
						intent.putExtra("description", description);
						intent.putExtra("target_url", target_url);
						intent.putExtra("image_url", image_url);
						startActivityForResult(
								intent,
								YoutuiConstants.TENCENT_DIRECTIONAL_SHARE_REQUEST_CODE);
					}
				}
			});
		}

		/**
		 * 该方法被浏览器端调用,微信分享
		 */
		public void clickOnAndroidWeixinShare(final String mark,
				final String title, final String description,
				final String target_url) {
			mHandler.post(new Runnable() {
				@SuppressLint("NewApi")
				public void run() {
					String appId = getAppIdByString("WEIXIN_APP_ID");
					if (!AppHelper.isWeixinExisted(getApplicationContext())) {
						onActivityResult(
								YoutuiConstants.WEIXIN_SHARE_REQUEST_CODE,
								YoutuiConstants.APP_NOT_EXIST, null);
					} else if (appId == null) {
						onActivityResult(
								YoutuiConstants.WEIXIN_SHARE_REQUEST_CODE,
								YoutuiConstants.APP_ID_MISSING, null);
					} else {
						Intent intent = new Intent();
						intent.setClass(YouTui.this, WeiXinShareActivity.class);
						intent.putExtra("mark", mark);
						intent.putExtra("title", title);
						intent.putExtra("description", description);
						intent.putExtra("target_url", target_url);
						intent.putExtra("appid", appId);
						startActivityForResult(intent,
								YoutuiConstants.WEIXIN_SHARE_REQUEST_CODE);
					}
				}
			});
		}

		/**
		 * 该方法被浏览器端调用,短信分享
		 */
		public void clickOnAndroidSMSShare(final String content) {
			mHandler.post(new Runnable() {
				@SuppressLint("NewApi")
				public void run() {
					Intent intent = new Intent();
					intent.setClass(YouTui.this, SMSActivity.class);
					intent.putExtra("content", content);
					startActivityForResult(intent,
							YoutuiConstants.SMS_REQUEST_CODE);
				}
			});
		}

		/**
		 * 该方法被浏览器端调用,邮件分享
		 */
		public void clickOnAndroidEmailShare(final String subject,
				final String content) {
			mHandler.post(new Runnable() {
				@SuppressLint("NewApi")
				public void run() {
					Intent intent = new Intent();
					intent.putExtra("subject", subject);
					intent.putExtra("content", content);
					intent.setClass(YouTui.this, EmailActivity.class);
					startActivityForResult(intent,
							YoutuiConstants.EMAIL_REQUEST_CODE);
				}
			});
		}
	}

	/**
	 * 处理新浪微博授权返回的结果
	 */
	public void DealSinaWeibo(int resultCode, Intent data) {
		switch (resultCode) {
		case YoutuiConstants.RESULT_SUCCESSFUL:
			JSONObject json = new JSONObject();
			try {
				json.put("token", data.getStringExtra("AccessToken").toString());
				json.put("ExpiresTime", data.getStringExtra("ExpiresTime")
						.toString());
				json.put("state", data.getStringExtra("state").toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String uri = "/weiboResponse";
			String redirect_url = getAppIdByString("SINA_WEIBO_REDIRECT_URL");
			//如果配置了新浪授权回调地址 则请求到应用自己的回调地址，并加上友推标志位
			if(redirect_url != null && redirect_url != "" ){
				String stateString = data.getStringExtra("state").toString();
				uri = redirect_url + "?proxy=youtui&act=" + stateString;
			}
			//获取授权页面url的后缀
			backUrl = uri;
			jumpToWeb(uri, json);
			
			break;
		case YoutuiConstants.RESULT_SIGNATURE_ERROR:
			String Code = data.getStringExtra("Code").toString();
			showAlert("授权失败");
			showAlert(Code);
			break;
		case YoutuiConstants.RESULT_CANCEL:
			showAlert("授权取消");
			break;
		case YoutuiConstants.RESULT_ERROR:
			String Auth_Exception = data.getStringExtra("Auth_Exception")
					.toString();
			showAlert("授权失败");
			showAlert(Auth_Exception);
			break;
		case YoutuiConstants.APP_NOT_EXIST:
			JSONObject json2 = new JSONObject();
			try {
				json2.put("state", data.getStringExtra("state").toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			showAlert("新浪微博客户端不存在，跳转中...");
			jumpToWeb("/authorize", json2);
			break;
		case YoutuiConstants.APP_ID_MISSING:
			JSONObject json3 = new JSONObject();
			try {
				json3.put("state", data.getStringExtra("state").toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			showAlert("新浪微博AppId不存在，跳转中...");
			jumpToWeb("/authorize", json3);
			break;
		default:
			break;
		}
	}

	/**
	 * 处理腾讯微博授权返回的结果
	 */
	public void DealTencentWeibo(int resultCode, Intent data) {
		switch (resultCode) {
		case YoutuiConstants.RESULT_SUCCESSFUL:
			JSONObject json = new JSONObject();
			try {
				json.put("token", data.getStringExtra("AccessToken").toString());
				json.put("ExpiresTime", data.getStringExtra("ExpiresTime")
						.toString());
				json.put("openid", data.getStringExtra("OpenId").toString());
				json.put("state", data.getStringExtra("state").toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			showAlert("授权成功");
			jumpToWeb("/testauthorization", json);
			break;
		case YoutuiConstants.RESULT_CANCEL:
			showAlert("授权取消");
			break;
		case YoutuiConstants.RESULT_ERROR:
			String Error = data.getStringExtra("Error").toString();
			showAlert("授权失败");
			showAlert(Error);
			break;
		case YoutuiConstants.APP_NOT_EXIST:
			JSONObject json2 = new JSONObject();
			try {
				json2.put("state", data.getStringExtra("state").toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			showAlert("腾讯QQ客户端不存在，跳转中...");
			jumpToWeb("/tencentauthorize", json2);
			break;
		default:
			break;
		}
	}

	/**
	 * 处理QQ空间分享返回的结果
	 */
	public void DealTencentQzone(int resultCode, Intent data) {
		switch (resultCode) {
		case YoutuiConstants.RESULT_SUCCESSFUL:
			String Response = data.getStringExtra("Response").toString();
			showAlert("授权成功");
			showAlert(Response);
			break;
		case YoutuiConstants.RESULT_CANCEL:
			showAlert("授权取消");
			break;
		case YoutuiConstants.RESULT_ERROR:
			String Error = data.getStringExtra("Error").toString();
			showAlert("授权失败");
			showAlert(Error);
			break;
		default:
			break;
		}
	}

	/**
	 * 处理QQ定向分享返回的结果
	 */
	public void DealTencentDirectionalShare(int resultCode, Intent data) {
		switch (resultCode) {
		case YoutuiConstants.RESULT_SUCCESSFUL:
			showAlert("操作成功");
			break;
		case YoutuiConstants.RESULT_CANCEL:
			showAlert("操作成功");// 这是QQ sdk的一个bug
			// showAlert("分享取消");
			break;
		case YoutuiConstants.RESULT_ERROR:
			String Error = data.getStringExtra("Error").toString();
			showAlert("分享失败");
			showAlert(Error);
			break;
		case YoutuiConstants.APP_NOT_EXIST:
			showAlert("腾讯QQ客户端不存在");
			break;
		default:
			break;
		}
	}

	/**
	 * 处理微信分享返回的结果
	 */
	public void DealWeixinShare(int resultCode, Intent data) {
		switch (resultCode) {
		case YoutuiConstants.RESULT_ERROR:
			String Error = data.getStringExtra("Error").toString();
			showAlert("分享失败");
			showAlert(Error);
			break;
		case YoutuiConstants.APP_NOT_EXIST:
			showAlert("分享失败，没有安装微信客户端");
			break;
		case YoutuiConstants.APP_ID_MISSING:
			showAlert("分享失败，没有AppId");
			break;
		default:
			break;
		}
	}

	/**
	 * 处理人人返回的结果
	 */
	public void DealRenren(int resultCode, Intent data) {
		switch (resultCode) {
		case YoutuiConstants.RESULT_SUCCESSFUL:
			JSONObject json = new JSONObject();
			try {
				json.put("token", data.getStringExtra("AccessToken").toString());
				json.put("state", data.getStringExtra("state").toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			showAlert("授权成功");
			jumpToWeb("/renrenauthorize2", json);
			break;
		case YoutuiConstants.RESULT_CANCEL:
			showAlert("授权取消");
		case YoutuiConstants.APP_NOT_EXIST:
			JSONObject json2 = new JSONObject();
			try {
				json2.put("state", data.getStringExtra("state").toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			showAlert("人人客户端不存在，跳转中...");
			jumpToWeb("/renrenauthorize", json2);
			break;
		default:
			break;
		}
	}

	/**
	 * 处理短信结果
	 */
	public void DealSMS(int resultCode, Intent data) {
		switch (resultCode) {
		case YoutuiConstants.RESULT_SUCCESSFUL:
			break;
		case YoutuiConstants.RESULT_CANCEL:
			showAlert("系统没有安装短信客户端");
			break;
		default:
			break;
		}
	}

	/**
	 * 处理电子邮件结果
	 */
	public void DealEmail(int resultCode, Intent data) {
		switch (resultCode) {
		case YoutuiConstants.RESULT_SUCCESSFUL:
			break;
		case YoutuiConstants.RESULT_CANCEL:
			showAlert("系统没有安装电子邮件客户端");
			break;
		default:
			break;
		}
	}

	/**
	 * 从AndroidManifest中获取appid
	 */
	private String getAppIdByString(String appid) {
		ApplicationInfo info;
		String msg = null;
		try {
			info = getPackageManager().getApplicationInfo(getPackageName(),
					PackageManager.GET_META_DATA);
			msg = info.metaData.getString(appid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (msg == null || msg.length() == 0) {
			return null;
		} else {
			return msg;
		}
	}

	/**
	 * 新浪微博从AndroidManifest中获取appid
	 */
	private String getAppIdSina(String appid) {
		ApplicationInfo info;
		String msg = null;
		try {
			info = getPackageManager().getApplicationInfo(getPackageName(),
					PackageManager.GET_META_DATA);
			msg = info.metaData.getString(appid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (msg == null || msg.length() == 0) {
			return null;
		} else {
			String result = msg.substring(4);
			return result;
		}
	}

	/**
	 * 显示结果
	 */
	private void showAlert(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

	/**
	 * startActivityForResult回调结果处理
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case YoutuiConstants.SINA_WEIBO_REQUEST_CODE:
			DealSinaWeibo(resultCode, data);
			break;
		case YoutuiConstants.TENCENT_WEIBO_REQUEST_CODE:
			DealTencentWeibo(resultCode, data);
			break;
		case YoutuiConstants.TENCENT_QZONE_REQUEST_CODE:
			DealTencentQzone(resultCode, data);
			break;
		case YoutuiConstants.TENCENT_DIRECTIONAL_SHARE_REQUEST_CODE:
			DealTencentDirectionalShare(resultCode, data);
			break;
		case YoutuiConstants.WEIXIN_SHARE_REQUEST_CODE:
			DealWeixinShare(resultCode, data);
			break;
		case YoutuiConstants.RENREN_REQUEST_CODE:
			DealRenren(resultCode, data);
			break;
		case YoutuiConstants.SMS_REQUEST_CODE:
			DealSMS(resultCode, data);
			break;
		case YoutuiConstants.EMAIL_REQUEST_CODE:
			DealEmail(resultCode, data);
			break;
		default:
			break;
		}
	}
}