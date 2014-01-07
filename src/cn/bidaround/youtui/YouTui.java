package cn.bidaround.youtui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/** 
 * @author steven 
 * http://www.youtui.mobi
 * 技术支持QQ: 1030311324  
 */  
public class YouTui extends Activity {
	
	private MenuItem exit;
	private WebView webView;
	private String appId;
	private Handler mHandler = new Handler(); 

	@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview_activity);
		/* 初始化内嵌浏览器 */
		initWebView();
		/* 装载数据内容*/
		loadContent();
	}
	
	/* 初始化内嵌浏览器 */
	@SuppressWarnings("deprecation")
	@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
	private void initWebView(){
		webView = (WebView) findViewById(R.id.webview);
		webView.setWebViewClient(new WebViewClient());
		//WebSettings 几乎浏览器的所有设置都在该类中进行  
        WebSettings webSettings = webView.getSettings();
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setBuiltInZoomControls(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSavePassword(true);
        webSettings.setSaveFormData(false);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		webSettings.setDatabaseEnabled(true);
		//重新弹出框
		webView.setWebChromeClient(new MyWebChromeClient());
		//接收web端 js 方法
		webView.addJavascriptInterface(new YouTuiJavaScriptInterface(), "android"); 
	}
	

	
	/* 装载数据内容*/
	private void loadContent(){
		ApplicationInfo info;
        try{
            info = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            int msg = info.metaData.getInt("YOUTUI_APPKEY");
            msg = msg + 0;
            appId = String.valueOf(msg);
        }catch(Exception e){
        	e.printStackTrace();
		}
        //String urlString = "http://yt.bidaround.cn";
        String urlString = "http://192.168.2.109";
		String urlStr = "";
		if (appId==null || appId.length() == 0) {
			urlStr = urlString + "/activity/noappId/";
		}else{
			urlStr = urlString + "/activity/shared/get?appId=" + appId;
		}
		webView.loadUrl(urlStr);
	}
	
	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/* 添加退出菜单 */
		exit = menu.add("Exit");
		/* 设置退出菜单图片 */
		exit.setIcon(R.drawable.close_btn);
		//添加菜单项  
        MenuItem add=menu.add(0,0,0,"add");  
        MenuItem del=menu.add(0,0,0,"del");  
        MenuItem save=menu.add(0,0,0,"save");  
        //绑定到ActionBar    
        add.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);  
        del.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);  
        save.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);  
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/* 结束Activity */
		finish();
		return super.onOptionsItemSelected(item);
	}

	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		view.loadUrl(url);
		return true;
	}
	
	public boolean onKeyDown(int keyCoder,KeyEvent event){
        if(webView.canGoBack() && keyCoder == KeyEvent.KEYCODE_BACK){
        	//goBack()表示返回webView的上一页面  
        	webView.goBack();   
            return true;
        }
        if(keyCoder == KeyEvent.KEYCODE_F5){
        	//f5刷新页面  
        	webView.reload();   
            return true;
        }
        return super.onKeyDown(keyCoder, event);
   }
	
	/** 
     * 继承WebChromeClient类 
     * 对js弹出框时间进行处理 
     *  
     */  
    final class MyWebChromeClient extends WebChromeClient {  
    	@Override  
        public void onProgressChanged(WebView view, int progress){  
    		YouTui.this.setTitle("Loading...");
    		YouTui.this.getWindow().setFeatureInt(Window.FEATURE_PROGRESS, progress * 100);
    		YouTui.this.setProgress(progress);  
              
            if(progress >= 80) {  
            	YouTui.this.setTitle("");  
            }  
        }  
        /** 
         * 处理alert弹出框 
         */  
        @Override
		public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
			AlertDialog.Builder b2 = new AlertDialog.Builder(YouTui.this);
			b2.setMessage(message);
			b2.setPositiveButton("ok",new AlertDialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					result.confirm();
				}
			});
			b2.setCancelable(false);
			b2.create();
			b2.show();
			return true;
		}
  
        /** 
         * 处理confirm弹出框 
         */  
        @Override  
        public boolean onJsConfirm(WebView view, String url, String message,  
                JsResult result) {  
            result.confirm();  
            return super.onJsConfirm(view, url, message, result);  
        }  
  
        /** 
         * 处理prompt弹出框 
         */  
        @Override  
        public boolean onJsPrompt(WebView view, String url, String message,  
                String defaultValue, JsPromptResult result) { 
            result.confirm();  
            return super.onJsPrompt(view, url, message, message, result);  
        }  
    }
    
    /** 
     * android 与 浏览器端交互 
     */  
    final class YouTuiJavaScriptInterface {  
    	YouTuiJavaScriptInterface() {}  
  
        /** 
         * 该方法被浏览器端调用 
         */  
        public void clickOnAndroidCopy(final String message) {  
            mHandler.post(new Runnable() {
				@SuppressLint("NewApi")
				public void run() {
					Log.d(message,"JS");
                	ClipboardManager clip = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                	clip.setPrimaryClip(ClipData.newPlainText("link", "m" +message)); // 复制 
                	AlertDialog.Builder b2 = new AlertDialog.Builder(YouTui.this);
                	
                	if(clip.hasPrimaryClip()){
                		b2.setMessage("复制成功");
                		//clip.getPrimaryClip().getItemAt(0).getText();  
                	}else{
                		b2.setMessage("复制失败，请手动复制");
                	} 
                	
        			b2.setPositiveButton("ok",new AlertDialog.OnClickListener() {
        				@Override
        				public void onClick(DialogInterface dialog, int which) {
        					
        				}
        			});
        			b2.setCancelable(false);
        			b2.create();
        			b2.show();
                }  
            });
        }
        
        /** 
         * 该方法被浏览器端调用,微博授权 
         */  
        public void clickOnAndroidWeibo() {  
            mHandler.post(new Runnable() {  
                @SuppressLint("NewApi")
				public void run() {
             
                }  
            });
        }
        
        /** 
         * 该方法被浏览器端调用,腾讯微博授权 
         */  
        public void clickOnAndroidTencentWeibo() {  
            mHandler.post(new Runnable() {  
                @SuppressLint("NewApi")
				public void run() {
             
                }  
            });
        }
        
        /** 
         * 该方法被浏览器端调用,QQ空间授权 
         */  
        public void clickOnAndroidQZone() {  
            mHandler.post(new Runnable() {  
                @SuppressLint("NewApi")
				public void run() {
             
                }  
            });
        }
        
        /** 
         * 该方法被浏览器端调用,人人网授权 
         */  
        public void clickOnAndroidRenRen() {  
            mHandler.post(new Runnable() {  
                @SuppressLint("NewApi")
				public void run() {
             
                }  
            });
        }
        
        /** 
         * 该方法被浏览器端调用,QQ好友单独邀请 
         */  
        public void clickOnAndroidQQ() {  
            mHandler.post(new Runnable() {  
                @SuppressLint("NewApi")
				public void run() {
             
                }  
            });
        }
        
        /** 
         * 该方法被浏览器端调用,微信好友单独邀请 
         */  
        public void clickOnAndroidWeiXin() {  
            mHandler.post(new Runnable() {  
                @SuppressLint("NewApi")
				public void run() {
             
                }  
            });
        }
        
        /** 
         * 该方法被浏览器端调用,短信单独邀请 
         */  
        public void clickOnAndroidMessage() {  
            mHandler.post(new Runnable() {  
                @SuppressLint("NewApi")
				public void run() {
             
                }  
            });
        }
        
        /** 
         * 该方法被浏览器端调用,邮件单独邀请 
         */  
        public void clickOnAndroidEmail() {  
            mHandler.post(new Runnable() {  
                @SuppressLint("NewApi")
				public void run() {
             
                }  
            });
        }
        
    }  
}