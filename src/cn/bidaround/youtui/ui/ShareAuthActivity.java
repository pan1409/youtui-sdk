package cn.bidaround.youtui.ui;
import cn.bidaround.youtui.component.MyProgressDialog;
import cn.bidaround.youtui.social.ShareData;
import cn.bidaround.youtui.social.SinaShare;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
public class ShareAuthActivity extends Activity{
	private SinaShare sinaShare;
	private ShareData shareData;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		MyProgressDialog.loadingBar(this);
		shareData = (ShareData) getIntent().getExtras().getSerializable("shareData");
		String from = getIntent().getExtras().getString("from");
		
		
		if("TencentWB".equals(from)){
		}else if("sina".equals(from)){
			sinaShare = new SinaShare(this,shareData);
			sinaShare.sinaAuth();	
		}else if("check".equals("from")){
			
		}
	}	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		MyProgressDialog.dismiss();
		if(sinaShare!=null){
			sinaShare.sinaResult(requestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
