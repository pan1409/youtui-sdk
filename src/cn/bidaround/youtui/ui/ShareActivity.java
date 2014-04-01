package cn.bidaround.youtui.ui;

import java.io.IOException;
import java.net.MalformedURLException;

import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;
import cn.bidaround.point.ChannelId;
import cn.bidaround.point.YtPoint;
import cn.bidaround.youtui.social.QQOpenShare;
import cn.bidaround.youtui.social.RennShare;
import cn.bidaround.youtui.social.SinaShare;
import cn.bidaround.youtui.social.YoutuiConstants;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

/**
 * author:gaopan
 */
public class ShareActivity extends Activity implements IWeiboHandler.Response {
	private SinaShare sinaShare;
	private String from;
	private IWeiboShareAPI iWeiboShareAPI;
	private int[] pointArr;
	private String reStr = "default";
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			reStr = (String) msg.obj;
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		pointArr = getIntent().getExtras().getIntArray("pointArr");
		iWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, YoutuiConstants.SINA_WEIBO_APP_ID);
		// 判断需要分享的媒体
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
		} else if ("QQWB".equals(from)) {
			new Thread() {
				public void run() {
					Looper.prepare();
					new QQOpenShare(ShareActivity.this).shareToQQWB();
					Looper.loop();
				};
			}.start();

		} else if ("QQ".equals(from)) {
			new QQOpenShare(this).shareToQQ();
		} else if ("Qzone".equals(from)) {
			new QQOpenShare(this).shareToQzone();
		}
	}

	/**
	 * 新浪微博分享完会调用该方法
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		Log.i("onNewIntent", "onNewIntent");
		setIntent(intent);
		if ("sina".equals(from)) {
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
			Toast.makeText(this, "分享成功", Toast.LENGTH_SHORT).show();
			// 在该分享有积分获得的情况下，分享成功后通知服务器加
			// 这里应该是不等于0，调试
			YtPoint.sharePoint(this, "10023", ChannelId.SINACHANNEL, pointArr);
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

	}
}
