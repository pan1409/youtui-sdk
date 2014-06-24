package cn.bidaround.youtui.wxapi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Toast;
import cn.bidaround.youtui.YouTuiAcceptor;
import cn.bidaround.youtui.helper.Util;
import cn.bidaround.youtui.point.ChannelId;
import cn.bidaround.youtui.point.YtPoint;
import cn.bidaround.youtui.social.KeyInfo;
import cn.bidaround.youtui.social.ShareData;
import cn.bidaround.youtui.social.YoutuiConstants;
import cn.bidaround.youtui.ui.YTBaseShareActivity;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;


/**
 * @author gaopan
 * @since 14/5/4 微信分享activity
 */
public class WXEntryActivity extends YTBaseShareActivity implements IWXAPIEventHandler, OnClickListener {
	private IWXAPI mIWXAPI;
	private Bitmap bitmap;
	private Bitmap bmpThum;
	private boolean isPyq, isAppShare;
	private String uniqueCode;
	private String realUrl ;
	private boolean fromShare;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		
		 //initView("微信");
		// 判断是否为朋友圈

		isPyq = getIntent().getExtras().getBoolean("pyq");
		fromShare = getIntent().getExtras().getBoolean("fromShare");
		uniqueCode = getIntent().getExtras().getString("uniqueCode");
		realUrl = getIntent().getExtras().getString("realUrl");
		// 传入的pointArr不为null时
		if (isPyq) {
			mIWXAPI = WXAPIFactory.createWXAPI(WXEntryActivity.this, KeyInfo.wechatMoments_AppId, false);
			mIWXAPI.registerApp(KeyInfo.wechatMoments_AppId);
		} else {
			mIWXAPI = WXAPIFactory.createWXAPI(WXEntryActivity.this, KeyInfo.wechat_AppId, false);
			mIWXAPI.registerApp(KeyInfo.wechat_AppId);
		}
		mIWXAPI.handleIntent(getIntent(), WXEntryActivity.this);
		Util.showProgressDialog(this, "加载中...");
		shareToWx();
	}

	/**
	 * 分享到微信或朋友圈 当微信没有登陆时，分享会先进入登陆界面，登录后再次启动该activity，
	 * 导致通过Intent传入的ShareData.shareData和pointArr读取都为null 此时在shareToWx不需要做操作
	 */
	protected void shareToWx() {
		if (ShareData.shareData != null) {
			isAppShare = ShareData.shareData.isAppShare;

			WXMediaMessage msg = new WXMediaMessage();

			if (ShareData.shareData.getImagePath() != null) {
				bitmap = BitmapFactory.decodeFile(ShareData.shareData.getImagePath());
			}
			msg.title = ShareData.shareData.getTitle();
			
			msg.description = ShareData.shareData.getText();			
			
			// bitmap为空时微信分享会没有响应，所以要设置一个默认图片让用户知道
			if (bitmap != null) {
				bmpThum = Bitmap.createScaledBitmap(bitmap, 150, 150, true);
			} else {
				bmpThum = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), YouTuiAcceptor.res.getIdentifier("loadfail", "drawable", YouTuiAcceptor.packName)), 150, 150, true);
			}
			msg.setThumbImage(bmpThum);
			WXWebpageObject pageObject = new WXWebpageObject();
			if (isAppShare) {
				if (isPyq) {
					pageObject.webpageUrl = YtPoint.setDownloadUrl(ChannelId.WECHAT);
				} else {
					pageObject.webpageUrl = YtPoint.setDownloadUrl(ChannelId.WECHATFRIEND);
				}
			} else {
				pageObject.webpageUrl = YoutuiConstants.YOUTUI_LINK_URL + uniqueCode;
			}
			msg.mediaObject = pageObject;
			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = buildTransaction("测试");
			req.message = msg;
			if (fromShare) {
				if (isPyq) {
					req.scene = SendMessageToWX.Req.WXSceneTimeline;
				} else if (!isPyq) {
					req.scene = SendMessageToWX.Req.WXSceneSession;
				}
				mIWXAPI.sendReq(req);
			}
		}else{
		}
	
	}

	// 微信在这里监听分享结果
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		handIntent(intent);
	}

	public void handIntent(Intent intent) {
		setIntent(intent);
		// 监听分享后的返回结果
		mIWXAPI.handleIntent(intent, this);
	}

	// 创建唯一标示
	protected String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}

	/**
	 * 监听请求
	 */
	@Override
	public void onReq(BaseReq req) {
		
	}

	/**
	 * @see com.tencent.mm.sdk.openapi.IWXAPIEventHandler#onResp(com.tencent.mm.sdk.openapi.BaseResp)
	 */
	@Override
	public void onResp(BaseResp response) {
		switch (response.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			//Toast.makeText(this, "分享成功", Toast.LENGTH_SHORT).show();
			if (isPyq) {
				YtPoint.sharePoint(this, KeyInfo.youTui_AppKey, ChannelId.WECHATFRIEND,realUrl, !ShareData.shareData.isAppShare, uniqueCode);
			} else {
				YtPoint.sharePoint(this, KeyInfo.youTui_AppKey, ChannelId.WECHAT, realUrl, !ShareData.shareData.isAppShare, uniqueCode);
			}
			break;
		case BaseResp.ErrCode.ERR_SENT_FAILED:
			Toast.makeText(this, "分享失败，请检查网络情况。。。", Toast.LENGTH_SHORT).show();
			break;
		case BaseResp.ErrCode.ERR_COMM:
			Toast.makeText(this, "分享失败，请检查网络情况。。。", Toast.LENGTH_SHORT).show();
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			Toast.makeText(this, "取消分享", Toast.LENGTH_SHORT).show();
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
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
		if (v.getId() == YouTuiAcceptor.res.getIdentifier("shareedit_back_linelay", "id", YouTuiAcceptor.packName)) {
			this.finish();
		} else if (v.getId() == YouTuiAcceptor.res.getIdentifier("shareedit_share_bt", "id", YouTuiAcceptor.packName)) {
			shareToWx();
		}
	}
}
