package cn.bidaround.youtui;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.webkit.JsPromptResult;
import cn.bidaround.youtui.social.ShareData;
import cn.bidaround.youtui.social.UserId;
import cn.bidaround.youtui.ui.SharePopupWindow;

public class YouTui {
	private Activity act;
	private ShareData shareData;
	private int showStyle;

	public YouTui(Activity act, ShareData shareData, int showStyle) {
		this.act = act;
		this.shareData = shareData;
		this.showStyle = showStyle;
	}
	// 初始化窗口显示
	public void init() {
		new Thread() {
			public void run() {
				try {
					
					int[] pointArr = parse();
					Looper.prepare();
					new SharePopupWindow(act, shareData, showStyle,pointArr).show();
					Looper.loop();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			};
		}.start();	
	}

	/**
	 *  获取积分情况
	 */
	public String getPoints() {
		TelephonyManager teleManager = (TelephonyManager) act
				.getSystemService(Context.TELEPHONY_SERVICE);
		UserId user = new UserId(teleManager);
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost("http://192.168.2.108/activity/sharePointRule");
		String jsonStr;
		try {
	        List<NameValuePair> params = new ArrayList<NameValuePair>(); 
	        params.add(new BasicNameValuePair("cardNum", user.getSimSerialNumber())); 
	        params.add(new BasicNameValuePair("imei", user.getDeviceId()));
	        params.add(new BasicNameValuePair("appId", "10023")); 
			post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			
			HttpResponse response = client.execute(post);
			HttpEntity entity = response.getEntity();
			jsonStr = EntityUtils.toString(entity);
			
			Log.i("------------", jsonStr);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			Log.i("-----", "ClientProtocolException");
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			Log.i("-----", "IOException");
			return null;
		}
		return  jsonStr;
	}
	/**
	 * 解析获得的积分Json文件
	 * @throws JSONException 
	 */
	public int[] parse() throws JSONException{
		int[] pointArr = new int[11];
		String str = getPoints();
		if(str!=null){
			JSONObject objectJson = new JSONObject(str).getJSONObject("object");
			for(int i=0;i<pointArr.length;i++){
				pointArr[i] = objectJson.getInt("channel"+i);
			}
		}
		return pointArr;
	}
}
