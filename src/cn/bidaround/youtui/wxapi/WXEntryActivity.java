package cn.bidaround.youtui.wxapi;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler  {
	private IWXAPI mIWXAPI;
	private static String APPID_WX = "wxbc9e6010cf85c3e4";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
				if(getIntent().getExtras().getBoolean("fromshare")){
					mIWXAPI = WXAPIFactory.createWXAPI(WXEntryActivity.this, APPID_WX, false);
					mIWXAPI.registerApp(APPID_WX);	
					WXTextObject textObject = new WXTextObject();
					textObject.text = "微信测试分享";		
					WXMediaMessage msg = new WXMediaMessage();
					msg.mediaObject = textObject;
					msg.description = "微信分享";
					SendMessageToWX.Req req = new SendMessageToWX.Req();
					req.transaction = buildTransaction("测试");
					req.message = msg;
					if(getIntent().getExtras().getBoolean("pyq")){
						req.scene = SendMessageToWX.Req.WXSceneTimeline;
					}else{
						req.scene = SendMessageToWX.Req.WXSceneSession;
					}
					mIWXAPI.sendReq(req);
				}else{
					finish();
				}
	}
	//创建唯一标示
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}


	@Override
	public void onReq(BaseReq arg0) {
	}

	@Override
	public void onResp(BaseResp arg0) {
	}
}
