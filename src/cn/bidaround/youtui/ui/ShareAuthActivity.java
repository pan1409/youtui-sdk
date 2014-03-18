package cn.bidaround.youtui.ui;

import cn.bidaround.youtui.helper.AccessTokenKeeper;
import cn.bidaround.youtui.helper.AppHelper;
import cn.bidaround.youtui.social.YoutuiConstants;

import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class ShareAuthActivity extends Activity{
	private Oauth2AccessToken oauth2AccessToken;
	private WeiboAuth mWeiboAuth;
	private SsoHandler mSsoHandler;
	private IWeiboShareAPI iWeiboShareAPI;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		iWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, "2502314449");
		mWeiboAuth = new WeiboAuth(this, "2502314449",
				YoutuiConstants.SINA_WEIBO_REDIRECT_URL,
				YoutuiConstants.SINA_WEIBO_SCOPE);
		mSsoHandler = new SsoHandler(this, mWeiboAuth);
		mWeiboAuth.anthorize(new AuthListener());
			
	
	}
	
	class AuthListener implements WeiboAuthListener {

		@Override
		public void onCancel() {
			Toast.makeText(ShareAuthActivity.this, "授权取消", Toast.LENGTH_SHORT)
					.show();
			ShareAuthActivity.this.finish();
		}

		@Override
		public void onComplete(Bundle bundle) {
			oauth2AccessToken = Oauth2AccessToken.parseAccessToken(bundle);
			if (oauth2AccessToken.isSessionValid()) {
				AccessTokenKeeper.writeAccessToken(ShareAuthActivity.this,
						oauth2AccessToken);

			}
			Toast.makeText(ShareAuthActivity.this, "授权成功", Toast.LENGTH_SHORT)
					.show();
			ShareAuthActivity.this.finish();
		}

		@Override
		public void onWeiboException(WeiboException arg0) {
			Toast.makeText(ShareAuthActivity.this, "授权错误", Toast.LENGTH_SHORT)
					.show();
			ShareAuthActivity.this.finish();
		}

	}
	
	
}
