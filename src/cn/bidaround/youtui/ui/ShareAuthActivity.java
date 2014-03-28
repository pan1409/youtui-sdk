package cn.bidaround.youtui.ui;
import cn.bidaround.youtui.social.SinaShare;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
public class ShareAuthActivity extends Activity{
	SinaShare sinaShare;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		String from = getIntent().getExtras().getString("from");
		if("TencentWB".equals(from)){
		}else if("sina".equals(from)){
			sinaShare = new SinaShare(this);
			sinaShare.sinaAuth();	
		}else if("check".equals("from")){
			
		}
	}	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(sinaShare!=null){
			sinaShare.sinaResult(requestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
