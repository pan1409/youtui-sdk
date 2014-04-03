package cn.bidaround.youtui.wxapi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;
import cn.bidaround.youtui.R;
import cn.bidaround.youtui.point.ChannelId;
import cn.bidaround.youtui.point.YtPoint;
import cn.bidaround.youtui.social.ShareData;
import cn.bidaround.youtui.social.YoutuiConstants;

import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.ConstantsAPI;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
	private ShareData shareData;
	private IWXAPI mIWXAPI;
	private Bitmap bitmap;
	private ProgressDialog loadingDialog;
	private Bitmap bmpThum;
	private boolean isPyq;
	private int wxPoint;
	private int pyqPoint;
	private int[] pointArr;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0) {
				loadingDialog.dismiss();
				return;
			}
			super.handleMessage(msg);
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		// SendAuth.Resp
		// 判断是否为朋友圈
		isPyq = getIntent().getExtras().getBoolean("pyq");
		pointArr = getIntent().getExtras().getIntArray("pointArr");
		mIWXAPI = WXAPIFactory.createWXAPI(WXEntryActivity.this, YoutuiConstants.WEIXIN_APP_ID, false);
		loadingBar();
		new Thread() {
			@Override
			public void run() {
				if (getIntent().getExtras().getBoolean("fromshare")) {
					shareData = (ShareData) getIntent().getExtras().getSerializable("shareData");
					mIWXAPI.handleIntent(getIntent(), WXEntryActivity.this);
					mIWXAPI.registerApp(YoutuiConstants.WEIXIN_APP_ID);
					WXMediaMessage msg = new WXMediaMessage();
					try {
						if (shareData.getImagePath() != null) {
							bitmap = BitmapFactory.decodeFile(shareData.getImagePath());
						} else {
							// 一般情况下不会执行该方法，如果用户网络较差，进行分享时网络图片没有下载完时调用
							if (shareData.getImageUrl() != null) {
								bitmap = BitmapFactory.decodeStream(new URL(shareData.getImageUrl()).openStream());
							}
						}

						// if(bitmap==null){
						// Log.i("--bitmap--", "null");
						// }else{
						// Log.i("--bitmap--", "notnull");
						// }
						mHandler.removeMessages(0);
						mHandler.sendEmptyMessage(0);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					msg.title = shareData.getTitle();
					msg.description = shareData.getDescription();
					// bitmap为空时微信分享会没有响应，所以要设置一个默认图片让用户知道
					if (bitmap != null) {
						bmpThum = Bitmap.createScaledBitmap(bitmap, 150, 150, true);
					} else {
						bmpThum = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.erweimaact), 150, 150, true);
					}
					msg.setThumbImage(bmpThum);
					WXWebpageObject pageObject = new WXWebpageObject();
					pageObject.webpageUrl = shareData.getTarget_url();
					msg.mediaObject = pageObject;
					SendMessageToWX.Req req = new SendMessageToWX.Req();
					req.transaction = buildTransaction("测试");
					req.message = msg;
					if (isPyq) {
						pyqPoint = getIntent().getExtras().getIntArray("pointArr")[ChannelId.WECHATFRIEND];
						req.scene = SendMessageToWX.Req.WXSceneTimeline;
					} else {
						wxPoint = getIntent().getExtras().getIntArray("pointArr")[ChannelId.WECHAT];
						req.scene = SendMessageToWX.Req.WXSceneSession;
					}
					mIWXAPI.sendReq(req);
				} else {
					WXEntryActivity.this.finish();
				}
			}
		}.start();
	}
	

	@Override
	protected void onNewIntent(Intent intent) {
		Log.i("--onNewIntent--", "--onNewIntent--");
		super.onNewIntent(intent);
		setIntent(intent);
		// 监听分享后的返回结果
		mIWXAPI.handleIntent(intent, this);
	}

	private void loadingBar() {
		// 进度条
		loadingDialog = new ProgressDialog(this);
		// 设置进度条风格，风格为圆形，旋转的
		loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// 设置ProgressDialog 提示信息
		loadingDialog.setMessage("加载中…");
		// 设置ProgressDialog 的进度条是否不明确
		loadingDialog.setIndeterminate(false);
		// 设置ProgressDialog 是否可以按退回按键取消
		loadingDialog.setCancelable(true);
		loadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				WXEntryActivity.this.finish();
			}
		});

		@SuppressWarnings("deprecation")
		int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
		@SuppressWarnings("deprecation")
		int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
		loadingDialog.getWindow().setLayout(screenWidth * 2 / 3, screenHeight / 5);
		loadingDialog.show();
	}

	// 创建唯一标示
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		finish();
		super.onRestart();
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
				Toast.makeText(this, "取消分享", Toast.LENGTH_SHORT).show();
				finish();
				break;
			}

			break;
		case ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX:
			switch (response.errCode) {
			case BaseResp.ErrCode.ERR_OK:
				Toast.makeText(this, "分享成功", Toast.LENGTH_SHORT).show();
				// 在有积分的情况下分享成功后会向服务器发送通知获得积分
				if ((!isPyq && wxPoint != 0) || (isPyq && pyqPoint != 0)) {
					if (isPyq) {
						Log.i("--wxshare--", "share to wx friend");
						YtPoint.sharePoint(this, "10023", ChannelId.WECHATFRIEND, pointArr);
					} else {
						Log.i("--wxshare--", "share to wx");
						YtPoint.sharePoint(this, "10023", ChannelId.WECHAT, pointArr);
					}
					YtPoint.getInstance(this).refresh(this);
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

}
