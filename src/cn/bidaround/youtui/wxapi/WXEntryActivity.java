package cn.bidaround.youtui.wxapi;

import cn.bidaround.youtui.social.YoutuiConstants;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Description: created by qyj on January 7, 2014
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
	// IWXAPI是第三方app和微信通信的openapi接口
	private IWXAPI api;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		api = WXAPIFactory.createWXAPI(this, YoutuiConstants.WEIXIN_APP_ID,
				false);
		api.registerApp(YoutuiConstants.WEIXIN_APP_ID);
		api.handleIntent(getIntent(), this);
	}

	/**
	 * 微信发送请求到第三方应用时，会回调到该方法
	 */
	@Override
	public void onReq(BaseReq req) {

	}

	/**
	 * 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
	 */
	@Override
	public void onResp(BaseResp resp) {
		String result = "";
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			result = "分享成功";
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			result = "取消分享";
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			result = "认证失败";
			break;
		default:
			result = "其他错误";
			break;
		}
		Toast.makeText(this, result, Toast.LENGTH_LONG).show();
		finish();
	}

	/**
	 * 打开本地浏览器至指定网页
	 */
	private void openBrouser() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		Uri content_url = Uri.parse("http://yt.bidaround.cn");// 浏览器打开的网址
		intent.setData(content_url);
		intent.setClassName("com.android.browser",
				"com.android.browser.BrowserActivity");
		startActivity(intent);
	}
}
