package cn.bidaround.youtui.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;

import cn.bidaround.youtui.social.RennShare;
import cn.bidaround.youtui.social.SinaShare;
import cn.bidaround.youtui.social.UserId;
import cn.bidaround.youtui.social.YoutuiConstants;
import cn.bidaround.youtui.wxapi.WXEntryActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

/**
 * author:gaopan
 */
public class ShareActivity extends Activity implements IWeiboHandler.Response{
	private SinaShare sinaShare;
	private String from;
	private IWeiboShareAPI iWeiboShareAPI; 
	private int point;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		point = getIntent().getExtras().getInt("point");
		iWeiboShareAPI =  WeiboShareSDK.createWeiboAPI(this,
				YoutuiConstants.SINA_WEIBO_APP_ID);
		//判断需要分享的媒体
		from = getIntent().getExtras().getString("from");
		if ("sina".equals(from)) {
			new Thread() {
				public void run() {
					sinaShare = new SinaShare(ShareActivity.this);
					sinaShare.shareToSina();
				};
			}.start();

		} else if ("renren".equals(from)) {
			new RennShare(this).shareToRenn();
		} else if ("TencentWB".equals(from)) {
		}
	}

	/**
	 * 新浪微博分享完会调用该方法
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		Log.i("onNewIntent", "onNewIntent");
		setIntent(intent);
		if ("sina".equals(from)){
			iWeiboShareAPI.handleWeiboResponse(intent, this);
		}
		super.onNewIntent(intent);
	}
	/**
	 * 新浪分享回调
	 */
	@Override
	public void onResponse(BaseResponse baseResp) {
		switch (baseResp.errCode) {
		case WBConstants.ErrorCode.ERR_OK:
			Toast.makeText(this, "分享成功", Toast.LENGTH_SHORT)
					.show();
			//在该分享有积分获得的情况下，分享成功后通知服务器加
			if(point!=0){
				new Thread(){
					public void run() {
						TelephonyManager teleManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
						UserId user = new UserId(teleManager);
						HttpClient client = new DefaultHttpClient();
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						HttpPost post = new HttpPost(
								"http://192.168.2.108/activity/sharePoint");
						params.add(new BasicNameValuePair("cardNum", user
								.getSimSerialNumber()));
						params.add(new BasicNameValuePair("appId", "10023"));
						params.add(new BasicNameValuePair("channelId", "0"));
						params.add(new BasicNameValuePair("point", point+""));

						try {
							post.setEntity(new UrlEncodedFormEntity(params,
									HTTP.UTF_8));
							client.execute(post);
						} catch (ClientProtocolException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					};
				}.start();

			}
			break;
		case WBConstants.ErrorCode.ERR_CANCEL:
			Toast.makeText(this, "分享取消", Toast.LENGTH_SHORT)
					.show();
			break;
		case WBConstants.ErrorCode.ERR_FAIL:
			Toast.makeText(this, "分享失败", Toast.LENGTH_SHORT)
					.show();
			break;
		default:
			break;
		}
		finish();
	}
}
