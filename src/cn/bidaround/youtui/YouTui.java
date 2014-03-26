package cn.bidaround.youtui;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;

import com.renn.rennsdk.param.GetShareParam;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import cn.bidaround.youtui.social.ShareData;
import cn.bidaround.youtui.social.UserId;
import cn.bidaround.youtui.ui.SharePopupWindow;

public class YouTui {
	private Activity act;
	private ShareData shareData;
	private int showStyle;
	public YouTui(Activity act,ShareData shareData,int showStyle){
		this.act = act;
		this.shareData = shareData;
		this.showStyle = showStyle;
	}
	public void init(){
//		new Thread(){
//			public void run() {
//				getPoints();
//			};
//		}.start();
		//初始化窗口显示
		new SharePopupWindow(act, shareData, showStyle).show();
	}
	//获取积分情况
	public void getPoints(){
		TelephonyManager teleManager = (TelephonyManager) act.getSystemService(Context.TELEPHONY_SERVICE);
		UserId user = new UserId(teleManager);
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost("http://192.168.2.108");
		HttpParams param = client.getParams();
		param.setParameter("appId", "10000");
		param.setParameter("cardNumStr",user.getSimSerialNumber());
		param.setParameter("imei", user.getDeviceId());
		HttpResponse response = null;
		try {
			response = client.execute(post);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		HttpEntity entity = response.getEntity();
		InputStream is=null;
		try {
			is = entity.getContent();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		SharedPreferences prefer = act.getSharedPreferences("jifen", 0);
		SharedPreferences.Editor editor = prefer.edit();
		editor.putString("json", "");
		editor.commit();
	}
}
