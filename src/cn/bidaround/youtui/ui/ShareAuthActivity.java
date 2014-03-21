package cn.bidaround.youtui.ui;
import cn.bidaround.youtui.social.SinaShare;
import cn.bidaround.youtui.social.TencentWBShare;
import android.app.Activity;
import android.os.Bundle;
public class ShareAuthActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		String from = getIntent().getExtras().getString("from");
		if("TencentWB".equals(from)){
			new TencentWBShare(this).tencentWBAuth();
		}else if("sina".equals(from)){
			new SinaShare(this).sinaAuth();	
		}
	
	}	
}
