package cn.bidaround.youtui.social;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;

/**
 * Description: created by qyj on January 7, 2014
 * revise by hcy on February 19, 2014
 */
public class TencentWeiboSSO extends Activity {
	private Tencent mTencent;
	private String state;
	private Activity mActivity;
	private WebView mWebView;
	
	/**
	 * 腾讯微博分享总的操作函数
	 * @param jsonCome
	 * @param activity
	 * @param webView
	 */
	public void shareByTencentWeibo(JSONObject jsonCome,Activity activity,WebView webView) {
		mActivity = activity;
		mWebView = webView;
		init();
		getData(jsonCome);
		login();
	}
	
	/**
	 * 初始化Tencent
	 */
	private void init() {
		if (mTencent == null) {
			mTencent = Tencent.createInstance(YoutuiConstants.TENCENT_APP_ID,
					this.getApplicationContext());
		}
	}

	/**
	 * 登录QQ
	 */
	private void login() {
		init();
		if (mTencent.isSessionValid()) {
			// 显示获取Token正常
			Intent intent = new Intent();
			intent.putExtra("AccessToken", getToken());
			intent.putExtra("ExpiresTime", getExpiresTime());
			intent.putExtra("OpenId", getOpenId());
			result(YoutuiConstants.RESULT_SUCCESSFUL, intent);
		} else {
			mTencent.login(this, YoutuiConstants.TENCENT_SCOPE,
					new IUiListener() {
						@Override
						public void onComplete(JSONObject response) {
							// 显示获取Token正常					
							JSONObject json = new JSONObject();
							try {
								json.put("AccessToken", getToken());
								json.put("ExpiresTime",
										String.valueOf(mTencent.getExpiresIn()));
								json.put("OpenId", getOpenId());
								json.put("state", state);
							} catch (JSONException e) {
								e.printStackTrace();
							}
							Toast.makeText(mActivity, "授权成功", Toast.LENGTH_LONG).show();
							jumpToWeb("/testauthorization", json);
						}

						@Override
						public void onError(UiError e) {
							// 显示授权错误
							String error = e.errorMessage;
							Toast.makeText(mActivity, "失败", Toast.LENGTH_LONG).show();
							Toast.makeText(mActivity, error, Toast.LENGTH_LONG).show();
						}

						@Override
						public void onCancel() {
							// 显示授权取消
							Toast.makeText(mActivity, "授权取消", Toast.LENGTH_LONG).show();
						}
					});
		}
	}

	/**
	 * 获取token(注意：需要登录后才能够获取token。但如果没有登录直接执行mTencent.getAccessToken()不会报错，返回空值)
	 */
	private String getToken() {
		String token = mTencent.getAccessToken();
		return token;
	}

	/**
	 * 获取token(注意：需要登录后才能够获取token。但如果没有登录直接执行mTencent.getAccessToken()不会报错，返回空值)
	 */
	private String getOpenId() {
		String openid = mTencent.getOpenId();
		return openid;
	}

	/**
	 * 获取token有效期(请先登录)
	 */
	private String getExpiresTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, (int) mTencent.getExpiresIn() / 1000);
		String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
				.format(calendar.getTime());
		return date;
	}

	/**
	 * 获取所需参数
	 */
	private void getData(JSONObject jsonCome) {
		try {
			state = jsonCome.getString("state");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 返回授权信息。
	 */
	private void result(int i, Intent intent) {
		intent.putExtra("state", state);
		this.setResult(i, intent);
		this.finish();
	}
	
	/**
	 * webview 跳转到网页
	 */
	private void jumpToWeb(String url, JSONObject json) {
		String urlString = "http://youtui.mobi";
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
		mWebView.loadUrl(urlString);
	}
}
