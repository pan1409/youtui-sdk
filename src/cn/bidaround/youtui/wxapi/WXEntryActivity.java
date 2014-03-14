package cn.bidaround.youtui.wxapi;

import cn.bidaround.youtui.R;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
public class WXEntryActivity extends Activity implements OnClickListener,IWXAPIEventHandler  {
	private IWXAPI mIWXAPI;
	private static String APPID_WX = "wxbc9e6010cf85c3e4";
	private Button wxShareBt;
	private Button wxCancelBt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wxskip_activity);
		initView();
		mIWXAPI = WXAPIFactory.createWXAPI(this, APPID_WX, false);
		mIWXAPI.registerApp(APPID_WX);	
	}

	void initView() {
		wxShareBt = (Button) findViewById(R.id.wxshare_bt);
		wxCancelBt = (Button) findViewById(R.id.wxcancel_bt);
		wxShareBt.setOnClickListener(this);
		wxCancelBt.setOnClickListener(this);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	//创建唯一标示
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.wxshare_bt:			
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
			finish();
			break;
		case R.id.wxcancel_bt:
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void onReq(BaseReq arg0) {
		// TODO Auto-generated method stub
		finish();
	}

	@Override
	public void onResp(BaseResp arg0) {
		// TODO Auto-generated method stub
		finish();
	}
}
