package cn.bidaround.youtui;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
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

import cn.bidaround.point.YtPoint;
import cn.bidaround.youtui.helper.AccessTokenKeeper;
import cn.bidaround.youtui.helper.DownloadImage;
import cn.bidaround.youtui.social.ShareData;
import cn.bidaround.youtui.social.YoutuiConstants;
import cn.bidaround.youtui.ui.SharePopupWindow;
import cn.bidaround.youtui.util.ShareList;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class MainActivity extends Activity implements OnClickListener {
	private Button popupBt;
	private Button main_alert_bt;
	private YtPoint point;
	private ShareData shareData = new ShareData();
	private int[] pointArr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		point = new YtPoint(this);
		point.init();
		pointArr = point.getPoint();
		initView();
	}

	void initView() {
		shareData.setImageUrl("http://img0.bdstatic.com/img/image/shouye/quanzhixian.jpg");
		popupBt = (Button) findViewById(R.id.popup_bt);
		popupBt.setOnClickListener(this);
		main_alert_bt = (Button) findViewById(R.id.main_alert_bt);
		main_alert_bt.setOnClickListener(this);
		new Runnable() {
			public void run() {
				DownloadImage.downloadImage(shareData.getImageUrl(), YoutuiConstants.FILE_SAVE_PATH, "326.jpg");
			}
		}.run();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.you_tui, menu);
		return true;
	}
	
	/**
	 * 接收分享activity回传的数据
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==ShareList.XINGLANGWEIBO){
			Log.i("------onresult", "onActivityResult");
			String str = data.getExtras().getString("point");
			if(str!=null){
				Log.i("--------onresult", str);
				try {
					pointArr = YouTui.parse(str);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				Log.i("--------onresult", "null");
			}
		}

		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		Log.i("----onnewintent", "main on new intent");
		super.onNewIntent(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.popup_bt:
			// 初始化友推分享窗口
			// new YouTui(this, shareData, YouTuiViewType.BLANK_FULL).init();
			new SharePopupWindow(this, shareData, YouTuiViewType.BLANK_FULL,point).show();
			break;
		case R.id.main_alert_bt:
			// 测试http请求新浪接口方式分享
			new Thread() {
				public void run() {
					HttpClient client = new DefaultHttpClient();
					HttpPost post = new HttpPost("https://api.weibo.com/2/statuses/update.json");
					HttpResponse response = null;
					List<NameValuePair> param = new ArrayList<NameValuePair>();
					param.add(new BasicNameValuePair("access_token", AccessTokenKeeper.readAccessToken(MainActivity.this).getToken()));
					param.add(new BasicNameValuePair("status", "webshare"));
					try {
						post.setEntity(new UrlEncodedFormEntity(param, HTTP.UTF_8));
						response = client.execute(post);
						String str = EntityUtils.toString(response.getEntity());
						Log.i("--------", str);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				};
			}.start();
			break;
		default:
			break;
		}
	}

}
