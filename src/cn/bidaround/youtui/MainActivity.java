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
import cn.bidaround.point.YtPoint;
import cn.bidaround.youtui.helper.AccessTokenKeeper;
import cn.bidaround.youtui.helper.DownloadImage;
import cn.bidaround.youtui.social.ShareData;
import cn.bidaround.youtui.social.YoutuiConstants;
import cn.bidaround.youtui.ui.SharePopupWindow;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {
	private Button popupBt;
	private Button main_alert_bt;
	private ShareData shareData = new ShareData();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		YtPoint.getInstance(this);
		initView();
	}

	void initView() {
		//模拟开发者传递数据
		shareData.setText("default share");
		shareData.setTitle("标题");
		shareData.setTarget_url("http://www.baidu.com");
		shareData.setImageUrl("http://imgt9.bdstatic.com/it/u=2,843227418&fm=19&gp=0.jpg");
		shareData.setImagePath(Environment.getExternalStorageDirectory()+YoutuiConstants.FILE_SAVE_PATH+"326.jpg");
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.popup_bt:
			// 初始化友推分享窗口
			// new YouTui(this, shareData, YouTuiViewType.BLANK_FULL).init();
			new SharePopupWindow(this, shareData, YouTuiViewType.BLANK_FULL, YtPoint.getInstance(this)).show();
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
