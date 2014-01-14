package cn.bidaround.youtui;

import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
public class YouTuiWebview extends Activity {

	private MenuItem exit;
	private WebView webView;
	private String appId;
	private Handler mHandler = new Handler();
	private ProgressDialog loadingDialog;

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
	}

	/**
	 * 进度条
	 */
	private void loadingBar() {
		// 进度条
		loadingDialog = new ProgressDialog(YouTuiWebview.this);
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
		if (appId == null || appId.length() == 0) {
			jumpToWeb("/activity/noappId/", null);
		} else {
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
		String urlString = "http://yt.bidaround.cn";
		// String urlString = "http://192.168.2.106";
		urlString += url;

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
		webView.loadUrl(urlString);
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/* 添加退出菜单 */
		exit = menu.add("Exit");
		/* 设置退出菜单图片 */
		exit.setIcon(R.drawable.close_btn);
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
			// goBack()表示返回webView的上一页面
			webView.goBack();
			return true;
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
			YouTuiWebview.this.setTitle("加载中...");
			YouTuiWebview.this.getWindow().setFeatureInt(
					Window.FEATURE_PROGRESS, progress * 100);
			YouTuiWebview.this.setProgress(progress);
			if (progress >= 80) {
				YouTuiWebview.this.setTitle("");
				loadingDialog.dismiss();
			}
		}

		/**
		 * 处理alert弹出框
		 */
		@Override
		public boolean onJsAlert(WebView view, String url, String message,
				final JsResult result) {
			AlertDialog.Builder b2 = new AlertDialog.Builder(YouTuiWebview.this);
			b2.setMessage(message);
			b2.setPositiveButton("ok", new AlertDialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					result.confirm();
					// end();
				}
			});
			b2.setCancelable(false);
			b2.create();
			b2.show();
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
					if (!AppHelper.isSinaWeiboExisted(getApplicationContext())) {
						Intent intent = new Intent();
						intent.putExtra("state", state);
						onActivityResult(
								YoutuiConstants.SINA_WEIBO_REQUEST_CODE,
								YoutuiConstants.APP_NOT_EXIST, intent);
					} else {
						Intent intent = new Intent();
						intent.setClass(YouTuiWebview.this,
								SinaWeiboSSOActivity.class);
						intent.putExtra("state", state);
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
						intent.setClass(YouTuiWebview.this,
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
					intent.setClass(YouTuiWebview.this,
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
						intent.setClass(YouTuiWebview.this,
								RenrenSSOActivity.class);
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
						intent.setClass(YouTuiWebview.this,
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
					if (!AppHelper.isWeixinExisted(getApplicationContext())) {
						onActivityResult(
								YoutuiConstants.WEIXIN_SHARE_REQUEST_CODE,
								YoutuiConstants.APP_NOT_EXIST, null);
					} else {
						Intent intent = new Intent();
						intent.setClass(YouTuiWebview.this,
								WeiXinShareActivity.class);
						intent.putExtra("mark", mark);
						intent.putExtra("title", title);
						intent.putExtra("description", description);
						intent.putExtra("target_url", target_url);
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
					intent.setClass(YouTuiWebview.this, SMSActivity.class);
					intent.putExtra("content", content);
					startActivity(intent);
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
					intent.setClass(YouTuiWebview.this, EmailActivity.class);
					startActivity(intent);
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
			showAlert("授权成功");
			jumpToWeb("/weiboBindResponse", json);
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
			showAlert("分享成功");
			break;
		case YoutuiConstants.RESULT_CANCEL:
			showAlert("分享成功");// 这是QQ sdk的一个bug
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
			String Dismiss = data.getStringExtra("Dismiss").toString();
			showAlert("分享失败");
			showAlert(Dismiss);
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
		default:
			break;
		}
	}
}