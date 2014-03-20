package cn.bidaround.youtui.wxapi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import cn.bidaround.youtui.social.ShareData;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.Toast;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
	private ShareData shareData;
	private IWXAPI mIWXAPI;
	private Bitmap bitmap;
	private static String APPID_WX = "wxbc9e6010cf85c3e4";
	private ProgressDialog loadingDialog;
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what==0){
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
		
		loadingBar();
		new Thread() {
			@Override
			public void run() {
				if (getIntent().getExtras().getBoolean("fromshare")) {
					shareData = (ShareData) getIntent().getExtras()
							.getSerializable("shareData");
					mIWXAPI = WXAPIFactory.createWXAPI(WXEntryActivity.this,
							APPID_WX, false);
					mIWXAPI.handleIntent(getIntent(),WXEntryActivity.this);
					mIWXAPI.registerApp(APPID_WX);
					
					WXMediaMessage msg = new WXMediaMessage();
					try {
						bitmap = BitmapFactory
								.decodeStream(new URL(shareData.getImageUrl()).openStream());
						mHandler.removeMessages(0);
						mHandler.sendEmptyMessage(0);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (bitmap != null) {
						Bitmap bmpThum = Bitmap.createScaledBitmap(bitmap, 150,
								150, true);
						msg.setThumbImage(bmpThum);
						msg.mediaObject = getImageForUrl();
					}
					SendMessageToWX.Req req = new SendMessageToWX.Req();
					req.transaction = buildTransaction("测试");
					req.message = msg;
					if (getIntent().getExtras().getBoolean("pyq")) {
						req.scene = SendMessageToWX.Req.WXSceneTimeline;
					} else {
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
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		setIntent(intent);
		mIWXAPI.handleIntent(intent, this);
	}

	/*
	 * 获取分享的内容的类型，暂时支持文字和图片两种
	 */
	private int getShareType() {
		if(shareData.getImageUrl()!=null){
			return ShareData.TYPE_IAMGE;
		}
		if(shareData.getText()!=null){
			return ShareData.TYPE_TEXT;
		}
		return -1;
	}

	private void loadingBar() {
		// 进度条
		loadingDialog = new ProgressDialog(this);
		// 设置进度条风格，风格为圆形，旋转的
		loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// 设置ProgressDialog 提示信息
		loadingDialog.setMessage("加载待分享图片…");
		// 设置ProgressDialog 的进度条是否不明确
		loadingDialog.setIndeterminate(false);
		// 设置ProgressDialog 是否可以按退回按键取消
		loadingDialog.setCancelable(true);
		loadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				WXEntryActivity.this.finish();
			}
		});

		int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
		int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
		loadingDialog.getWindow().setLayout(screenWidth * 2 / 3,
				screenHeight / 5);
		loadingDialog.show();
	}

	/*
	 * 分享一张网络上的图片
	 */
	private WXImageObject getImageForUrl() {
		WXImageObject imageObject = new WXImageObject(bitmap);
		return imageObject;
	}

	/*
	 * 分享
	 */
	private WXTextObject getTextObject() {
		WXTextObject textObject = new WXTextObject();
		textObject.text = shareData.getText();
		return textObject;
	}

	// 创建唯一标示
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis())
				: type + System.currentTimeMillis();
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp respone) {
		switch (respone.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			Toast.makeText(this, "分享成功", Toast.LENGTH_SHORT).show();
			break;
		case BaseResp.ErrCode.ERR_SENT_FAILED:
			Toast.makeText(this, "分享失败，请检查网络情况。。。", Toast.LENGTH_SHORT).show();
			break;
		case BaseResp.ErrCode.ERR_COMM:
			Toast.makeText(this, "分享失败，请检查网络情况。。。", Toast.LENGTH_SHORT).show();
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			Toast.makeText(this, "取消分享", Toast.LENGTH_SHORT).show();
		default:
			break;
		}
		finish();
	}
}
