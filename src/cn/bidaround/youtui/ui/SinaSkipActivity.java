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
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import cn.bidaround.youtui.helper.AccessTokenKeeper;
import cn.bidaround.youtui.helper.AppHelper;
import cn.bidaround.youtui.social.YoutuiConstants;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/*
 * 点击新浪微博的跳转页面,现在被弃用
 */
public class SinaSkipActivity extends Activity implements
		IWeiboHandler.Response {

	private Oauth2AccessToken oauth2AccessToken;
	private WeiboAuth mWeiboAuth;
	private SsoHandler mSsoHandler;
	private IWeiboShareAPI iWeiboShareAPI;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		iWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, "2502314449");
		// 当 Activity 被重新初始化时（该 Activity 处于后台时，可能会由于内存不足被杀掉了），
		// 需要调用 {@link IWeiboShareAPI#handleWeiboResponse} 来接收微博客户端返回的数据。
		// 执行成功，返回 true，并调用 {@link IWeiboHandler.Response#onResponse}；
		// 失败返回 false，不调用上述回调
		if (savedInstanceState != null) {
			iWeiboShareAPI.handleWeiboResponse(getIntent(), this);
		}
		mWeiboAuth = new WeiboAuth(this, "2502314449",
				YoutuiConstants.SINA_WEIBO_REDIRECT_URL,
				YoutuiConstants.SINA_WEIBO_SCOPE);
		mSsoHandler = new SsoHandler(this, mWeiboAuth);
		if (!AppHelper.isSinaWeiboExisted(this)) {
			if (!AccessTokenKeeper.readAccessToken(this).isSessionValid()) {
				mWeiboAuth.anthorize(new AuthListener());
			} else {
			}
		} else {
			if (!AccessTokenKeeper.readAccessToken(this).isSessionValid()) {
				mSsoHandler.authorize(new AuthListener());

			} else {
				Toast.makeText(this, "已取得授权", Toast.LENGTH_SHORT).show();
				iWeiboShareAPI.registerApp();
				sendMsgToSina();
			}

		}

	}

	/*
	 * 低版本只支持单条消息分享
	 */
	boolean sendMsgToSina() {
		if (iWeiboShareAPI.getWeiboAppSupportAPI() >= 10351) {
			return sendMultiMessage(true, false, false, false, false, false);
		} else {
			return sendSingleMessage(true, false, false, false, false);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();	
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
		// TODO Auto-generated method stub
		TextObject text = new TextObject();
		text.text = "my share";
		return text;
	}

	private BaseMediaObject getVoiceObj() {
		// TODO Auto-generated method stub
		return null;
	}

	private BaseMediaObject getVideoObj() {
		// TODO Auto-generated method stub
		return null;
	}

	private BaseMediaObject getMusicObj() {
		// TODO Auto-generated method stub
		return null;
	}

	private BaseMediaObject getWebpageObj() {
		// TODO Auto-generated method stub
		return null;
	}

	private ImageObject getImageObj() {
		// TODO Auto-generated method stub
		return null;
	}

	class AuthListener implements WeiboAuthListener {

		@Override
		public void onCancel() {
			Toast.makeText(SinaSkipActivity.this, "授权取消", Toast.LENGTH_SHORT)
					.show();
			SinaSkipActivity.this.finish();
		}

		@Override
		public void onComplete(Bundle bundle) {
			oauth2AccessToken = Oauth2AccessToken.parseAccessToken(bundle);
			if (oauth2AccessToken.isSessionValid()) {
				AccessTokenKeeper.writeAccessToken(SinaSkipActivity.this,
						oauth2AccessToken);

			}
			Toast.makeText(SinaSkipActivity.this, "授权成功", Toast.LENGTH_SHORT)
					.show();
			iWeiboShareAPI.registerApp();
			sendMsgToSina();
		}

		@Override
		public void onWeiboException(WeiboException arg0) {
			Toast.makeText(SinaSkipActivity.this, "授权错误", Toast.LENGTH_SHORT)
					.show();
			SinaSkipActivity.this.finish();
		}

	}
	
	/*
	 * 处理新浪微博回调，必须重写
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}
	/*
	 * BUG onNewIntent怎么都无法调用
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		Log.i("onNewIntent", "onNewIntent");
		super.onNewIntent(intent);
		  iWeiboShareAPI.handleWeiboResponse(intent, this);
	}
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        
//        // 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
//        // 来接收微博客户端返回的数据；执行成功，返回 true，并调用
//        // {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
//        iWeiboShareAPI.handleWeiboResponse(intent, this);
//    }

    /**
     * 接收微客户端博请求的数据。
     * 当微博客户端唤起当前应用并进行分享时，该方法被调用。
     * @param baseRequest 微博请求数据对象
     * @see {@link IWeiboShareAPI#handleWeiboRequest}
     */
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
            Toast.makeText(this, 
                    "分享失败", 
                    Toast.LENGTH_LONG).show();
            break;
            default:
            	break;	
        }
    }

}
