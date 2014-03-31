package cn.bidaround.youtui.ui;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import cn.bidaround.youtui.social.YoutuiConstants;

import com.tencent.open.HttpStatusException;
import com.tencent.open.NetworkUnavailableException;
import com.tencent.tauth.IRequestListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;

public class QQSkipActivity extends Activity {
	private Tencent mTencent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mTencent = Tencent.createInstance("1101255276", getApplicationContext());	
		//mTencent.setOpenId(arg0)
		//mTencent.setAccessToken(arg0, arg1)
		
		mTencent.login(this, YoutuiConstants.TENCENT_SCOPE, new QQUiListener());
	}
	
	
	
	
	
	
	class QQUiListener implements IUiListener{

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onComplete(JSONObject json) {
			// TODO Auto-generated method stub
			SharedPreferences sp = getSharedPreferences("tencent_open_access", 0);
			Editor edit = sp.edit();
			try {
				edit.putString("openid",json.getString("openid"));
				edit.putString("access_token",json.getString("access_token"));
				edit.putLong("expires_in", System.currentTimeMillis()+Long.parseLong(json.getString("expires_in")));
				edit.commit();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void onError(UiError arg0) {
			// TODO Auto-generated method stub
			
		}
		
		class QQReqListener implements IRequestListener{

			@Override
			public void onComplete(JSONObject arg0, Object arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onConnectTimeoutException(ConnectTimeoutException arg0,
					Object arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onHttpStatusException(HttpStatusException arg0,
					Object arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onIOException(IOException arg0, Object arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onJSONException(JSONException arg0, Object arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onMalformedURLException(MalformedURLException arg0,
					Object arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onNetworkUnavailableException(
					NetworkUnavailableException arg0, Object arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSocketTimeoutException(SocketTimeoutException arg0,
					Object arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onUnknowException(Exception arg0, Object arg1) {
				// TODO Auto-generated method stub
				
			}
			
		}
		
	}
}
