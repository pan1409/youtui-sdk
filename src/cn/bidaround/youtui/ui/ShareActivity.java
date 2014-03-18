package cn.bidaround.youtui.ui;

import com.sina.weibo.sdk.api.BaseMediaObject;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

public class ShareActivity extends Activity implements IWeiboHandler.Response {
	private IWeiboShareAPI iWeiboShareAPI;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		iWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, "2502314449");
		;
		Toast.makeText(this, "已取得授权", Toast.LENGTH_SHORT).show();
		iWeiboShareAPI.registerApp();
		sendMsgToSina();
	}

	boolean sendMsgToSina() {
		if (iWeiboShareAPI.getWeiboAppSupportAPI() >= 10351) {
			return sendMultiMessage(true, false, false, false, false, false);
		} else {
			return sendSingleMessage(true, false, false, false, false);
		}
	}

	/**
	 * 第三方应用发送请求消息到微博，唤起微博分享界面。 注意：当
	 * {@link IWeiboShareAPI#getWeiboAppSupportAPI()} >= 10351 时，支持同时分享多条消息，
	 * 同时可以分享文本、图片以及其它媒体资源（网页、音乐、视频、声音中的一种）。
	 * 
	 * @param hasText
	 *            分享的内容是否有文本
	 * @param hasImage
	 *            分享的内容是否有图片
	 * @param hasWebpage
	 *            分享的内容是否有网页
	 * @param hasMusic
	 *            分享的内容是否有音乐
	 * @param hasVideo
	 *            分享的内容是否有视频
	 * @param hasVoice
	 *            分享的内容是否有声音
	 */
	private boolean sendMultiMessage(boolean hasText, boolean hasImage,
			boolean hasWebpage, boolean hasMusic, boolean hasVideo,
			boolean hasVoice) {

		// 1. 初始化微博的分享消息
		WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
		if (hasText) {
			weiboMessage.textObject = getTextObj();
		}

		if (hasImage) {
			weiboMessage.imageObject = getImageObj();
		}

		// 用户可以分享其它媒体资源（网页、音乐、视频、声音中的一种）
		if (hasWebpage) {
			weiboMessage.mediaObject = getWebpageObj();
		}
		if (hasMusic) {
			weiboMessage.mediaObject = getMusicObj();
		}
		if (hasVideo) {
			weiboMessage.mediaObject = getVideoObj();
		}
		if (hasVoice) {
			weiboMessage.mediaObject = getVoiceObj();
		}

		// 2. 初始化从第三方到微博的消息请求
		SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
		// 用transaction唯一标识一个请求
		request.transaction = String.valueOf(System.currentTimeMillis());
		request.multiMessage = weiboMessage;

		// 3. 发送请求消息到微博，唤起微博分享界面
		return iWeiboShareAPI.sendRequest(request);
	}

	/**
	 * 第三方应用发送请求消息到微博，唤起微博分享界面。 当{@link IWeiboShareAPI#getWeiboAppSupportAPI()}
	 * < 10351 时，只支持分享单条消息，即 文本、图片、网页、音乐、视频中的一种，不支持Voice消息。
	 */
	private boolean sendSingleMessage(boolean hasText, boolean hasImage,
			boolean hasWebpage, boolean hasMusic, boolean hasVideo) {

		// 1. 初始化微博的分享消息
		// 用户可以分享文本、图片、网页、音乐、视频中的一种
		WeiboMessage weiboMessage = new WeiboMessage();
		if (hasText) {
			weiboMessage.mediaObject = getTextObj();
		}
		if (hasImage) {
			weiboMessage.mediaObject = getImageObj();
		}
		if (hasWebpage) {
			weiboMessage.mediaObject = getWebpageObj();
		}
		if (hasMusic) {
			weiboMessage.mediaObject = getMusicObj();
		}
		if (hasVideo) {
			weiboMessage.mediaObject = getVideoObj();
		}

		// 2. 初始化从第三方到微博的消息请求
		SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
		// 用transaction唯一标识一个请求
		request.transaction = String.valueOf(System.currentTimeMillis());
		request.message = weiboMessage;

		// 3. 发送请求消息到微博，唤起微博分享界面
		return iWeiboShareAPI.sendRequest(request);
	}

	private TextObject getTextObj() {
		TextObject text = new TextObject();
		text.text = "my share";
		return text;
	}

	private BaseMediaObject getVoiceObj() {
		return null;
	}

	private BaseMediaObject getVideoObj() {
		return null;
	}

	private BaseMediaObject getMusicObj() {
		return null;
	}

	private BaseMediaObject getWebpageObj() {
		return null;
	}

	private ImageObject getImageObj() {
		return null;
	}
	/*
	 * ?? 下面两个方法为官方使用的处理分享后结果的函数，但是无法被调用
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		Log.i("onNewIntent", "onNewIntent");
		super.onNewIntent(intent);
		iWeiboShareAPI.handleWeiboResponse(intent, this);
	}

	@Override
	public void onResponse(BaseResponse baseResp) {
		Toast.makeText(this, "回调", Toast.LENGTH_LONG).show();
		switch (baseResp.errCode) {
		case WBConstants.ErrorCode.ERR_OK:
			Toast.makeText(this, "分享成功", Toast.LENGTH_LONG).show();
			break;
		case WBConstants.ErrorCode.ERR_CANCEL:
			Toast.makeText(this, "分享取消", Toast.LENGTH_LONG).show();
			break;
		case WBConstants.ErrorCode.ERR_FAIL:
			Toast.makeText(this, "分享失败", Toast.LENGTH_LONG).show();
			break;
		default:
			break;
		}

	}
}
