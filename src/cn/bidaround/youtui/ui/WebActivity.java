package cn.bidaround.youtui.ui;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebActivity extends Activity {
	private WebView webView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initWebView();
		setContentView(webView);
	}

	void initWebView() {
		webView = new WebView(this);
		webView.setWebViewClient(new WebViewClient());
		WebSettings setting = webView.getSettings();
		setting.setJavaScriptEnabled(true);
		setting.setBuiltInZoomControls(true);
		setting.setSupportZoom(true);
		setting.setCacheMode(WebSettings.LOAD_NO_CACHE);
		webView.loadUrl("https://open.t.qq.com/cgi-bin/oauth2/authorize?client_id=801443192&response_type=token&redirect_uri=http://yt.bidaround.cn/");
	}
}
