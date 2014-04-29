package cn.bidaround.youtui.wxapi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Toast;
import cn.bidaround.youtui.YouTuiAcceptor;
import cn.bidaround.youtui.component.MyProgressDialog;
import cn.bidaround.youtui.point.ChannelId;
import cn.bidaround.youtui.point.YtPoint;
import cn.bidaround.youtui.social.KeyInfo;
import cn.bidaround.youtui.social.ShareData;
import cn.bidaround.youtui.ui.YTShareActivity;

import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.ConstantsAPI;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

public class WXEntryActivity extends YTShareActivity implements IWXAPIEventHandler, OnClickListener {
	private IWXAPI mIWXAPI;
	private Bitmap bitmap;
	private Bitmap bmpThum;
	private boolean isPyq,isAppShare;
	private int[] pointArr = new int[11];

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0) {
				MyProgressDialog.dismiss();
				return;
			}
			super.handleMessage(msg);
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);	
		super.onCreate(savedInstanceState);
		shareData = (ShareData) getIntent().getExtras().getSerializable("shareData");
		
		initView("微信");
		// 判断是否为朋友圈
		isPyq = getIntent().getExtras().getBoolean("pyq");
		
//		for (int i : pointArr) {
//			Log.i("------", i+"");
//		}		
		//传入的pointArr不为null时
		if(getIntent().getExtras().getIntArray("pointArr")!=null){
			pointArr = getIntent().getExtras().getIntArray("pointArr");
		}

		if (isPyq) {
			mIWXAPI = WXAPIFactory.createWXAPI(WXEntryActivity.this, KeyInfo.WechatMoments_AppId, false);
			mIWXAPI.registerApp(KeyInfo.WechatMoments_AppId);
		} else {
			mIWXAPI = WXAPIFactory.createWXAPI(WXEntryActivity.this, KeyInfo.Wechat_AppId, false);
			mIWXAPI.registerApp(KeyInfo.Wechat_AppId);
		}
		mIWXAPI.handleIntent(getIntent(), WXEntryActivity.this);

		MyProgressDialog.loadingBar(this, "加载中...");
		shareToWx();
	}

	/**
	 * 分享到微信或朋友圈
	 * 当微信没有登陆时，分享会先进入登陆界面，登录后再次启动该activity，导致通过Intent传入的shareData和pointArr读取都为null
	 * 此时在shareToWx不需要做操作
	 */
	private void shareToWx() {
		if (shareData != null) {
			isAppShare = shareData.isAppShare;
			new Thread() {
				@Override
				public void run() {
					WXMediaMessage msg = new WXMediaMessage();
					try {
						if (shareData.getImagePath() != null) {
							bitmap = BitmapFactory.decodeFile(shareData.getImagePath());
						} else if (shareData.getImageUrl() != null) {
							// 一般情况下不会执行该方法，如果用户网络较差，进行分享时网络图片没有下载完时调用
							bitmap = BitmapFactory.decodeStream(new URL(shareData.getImageUrl()).openStream());
						}

						mHandler.removeMessages(0);
						mHandler.sendEmptyMessage(0);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					msg.title = shareData.getTitle();
					msg.description = shareData.getText();
					// bitmap为空时微信分享会没有响应，所以要设置一个默认图片让用户知道
					if (bitmap != null) {
						bmpThum = Bitmap.createScaledBitmap(bitmap, 150, 150, true);
					} else {
						bmpThum = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), YouTuiAcceptor.res.getIdentifier("loadfail", "drawable", YouTuiAcceptor.packName)), 150, 150, true);
					}
					msg.setThumbImage(bmpThum);
					WXWebpageObject pageObject = new WXWebpageObject();
					if(isAppShare){
						if(isPyq){
							pageObject.webpageUrl = YtPoint.setDownloadUrl(ChannelId.WECHAT);
						}else{
							pageObject.webpageUrl =  YtPoint.setDownloadUrl(ChannelId.WECHATFRIEND);
						}
					}else{
						pageObject.webpageUrl = shareData.getTarget_url();
					}
					msg.mediaObject = pageObject;
					SendMessageToWX.Req req = new SendMessageToWX.Req();
					req.transaction = buildTransaction("测试");
					req.message = msg;

					if (isPyq) {
						req.scene = SendMessageToWX.Req.WXSceneTimeline;
					} else {
						req.scene = SendMessageToWX.Req.WXSceneSession;
					}
					mIWXAPI.sendReq(req);

				}
			}.start();
		}
	}

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
	private String buildTransaction(final String type) {
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
		switch (response.getType()) {
		case ConstantsAPI.COMMAND_SENDAUTH:
			switch (response.errCode) {
			case BaseResp.ErrCode.ERR_USER_CANCEL:
				Toast.makeText(this, "取消授权", Toast.LENGTH_SHORT).show();
				finish();
				break;
			}

			break;
		case ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX:
			switch (response.errCode) {
			case BaseResp.ErrCode.ERR_OK:
				//Toast.makeText(this, "分享成功", Toast.LENGTH_SHORT).show();
				if (isPyq) {
					YtPoint.sharePoint(this, KeyInfo.YouTui_AppKey, ChannelId.WECHATFRIEND, pointArr);
				} else {
					YtPoint.sharePoint(this, KeyInfo.YouTui_AppKey, ChannelId.WECHAT, pointArr);
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
			break;

		default:
			break;
		}

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
