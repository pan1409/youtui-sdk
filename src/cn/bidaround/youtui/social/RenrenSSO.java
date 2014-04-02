package cn.bidaround.youtui.social;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.webkit.WebView;
import android.widget.Toast;

import com.renn.rennsdk.RennClient;
import com.renn.rennsdk.RennClient.LoginListener;

/**
 * Description: created by qyj on January 7, 2014 revise by hcy on February 19,
 * 2014
 */
public class RenrenSSO extends Activity {
	private RennClient rennClient;
	private String state;
	private Activity mActivity;
	private WebView mWebView;

	/**
	 * 总的操作函数
	 * 
	 * @param jsonCome
	 * @param activity
	 * @param webView
	 */
	public void shareByRenren(JSONObject jsonCome, Activity activity, WebView webView) {
		mActivity = activity;
		mWebView = webView;
		init();
		getData(jsonCome);
		login();
	}

	/**
	 * 人人初始化
	 */
	private void init() {
		rennClient = RennClient.getInstance(mActivity);
		rennClient.init(YoutuiConstants.RENREN_APP_ID, YoutuiConstants.RENREN_API_KEY, YoutuiConstants.RENREN_SECRET_KEY);
		rennClient.setScope(YoutuiConstants.RENREN_SCOPE);
		rennClient.setTokenType("bearer");
		rennClient.setLoginListener(new LoginListener() {
			@Override
			public void onLoginSuccess() {
				// 显示获取Token正常
				JSONObject json = new JSONObject();
				try {
					json.put("token", getToken());
					json.put("state", state);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				Toast.makeText(mActivity, "授权成功", Toast.LENGTH_LONG).show();
				jumpToWeb("/renrenauthorize2", json);
			}

			@Override
			public void onLoginCanceled() {
				// 显示授权取消
				Toast.makeText(mActivity, "授权取消", Toast.LENGTH_LONG).show();
			}
		});
	}

	/**
	 * 人人登录
	 */
	private void login() {
		rennClient.login(mActivity);
	}

	/**
	 * 人人注销
	 */
	private void logout() {
		rennClient.logout();
	}

	/**
	 * 获取token(请先登录)
	 */
	private String getToken() {
		String token = rennClient.getAccessToken().accessToken;
		return token;
	}

	/**
	 * 获取token有效期(请先登录)
	 */
	@SuppressLint("SimpleDateFormat")
	private String getExpiresTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, (int) rennClient.getAccessToken().expiresIn);
		String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(calendar.getTime());
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
	@SuppressWarnings("unused")
	private void result(int i, Intent intent) {
		intent.putExtra("state", state);
		this.setResult(i, intent);
		logout();
		this.finish();
	}

	/**
	 * webview 跳转到网页
	 */
	private void jumpToWeb(String url, JSONObject json) {
		String urlString = "http://youtui.mobi";
		urlString += url;
		// 如果已配置url则直接使用不用组装
		if (url.contains("http://")) {
			urlString = url;
		}
		if (json != null) {
			urlString += "?";
			@SuppressWarnings("rawtypes")
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