package cn.bidaround.youtui.ui;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bidaround.youtui.helper.AccessTokenKeeper;
import cn.bidaround.youtui.social.ShareData;
import cn.bidaround.youtui.social.YoutuiConstants;
import com.tencent.connect.auth.QQAuth;
import com.tencent.connect.dataprovider.Constants;

import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.Toast;

public class QQSkipActivity extends Activity {
	private Tencent mTencent;
	public static QQAuth qqAuth;
	private ShareData shareData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		shareData = (ShareData) getIntent().getExtras().getSerializable("shareData");
		mTencent = Tencent.createInstance(YoutuiConstants.TENCENT_APP_ID, this);
		mTencent.setOpenId(AccessTokenKeeper.readQQOpenid(this));
		mTencent.setAccessToken(AccessTokenKeeper.readQQAccessToken(this), AccessTokenKeeper.readQQExpires(this));
		// 没有授权的话先登录授权
		if (mTencent.isSessionValid() && mTencent.getAppId() != null) {
			Bundle params = new Bundle();
			params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
			params.putString(QQShare.SHARE_TO_QQ_TITLE, "要分享的标题");
			params.putString(QQShare.SHARE_TO_QQ_SUMMARY, "要分享的摘要");
			params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "http://www.qq.com/news/1.html");
			params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif");
			params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "测试应用222222");
			params.putString(QQShare.SHARE_TO_QQ_EXT_INT, "其他附加功能");
			mTencent.shareToQQ(this, params, new MyQQShareUIListener());
		} else {
			mTencent.login(this, YoutuiConstants.TENCENT_SCOPE, new MyQQAuthUIListener());
		}

	}

	/**
	 * 分享回调
	 */
	class MyQQShareUIListener implements IUiListener {

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onComplete(Object arg0) {
			// TODO Auto-generated method stub
			Toast.makeText(QQSkipActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onError(UiError arg0) {
			// TODO Auto-generated method stub

		}

	}

	/**
	 * 授权回调
	 */
	class MyQQAuthUIListener implements IUiListener {

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onComplete(Object object) {
			// TODO Auto-generated method stub
			Toast.makeText(QQSkipActivity.this, "授权成功", Toast.LENGTH_SHORT).show();
			// 将access_token信息写入SharedPreferences中
			SharedPreferences sp = getSharedPreferences("tencent_open_access", 0);
			Editor edit = sp.edit();
			JSONObject json = (JSONObject) object;

			try {
				edit.putString("openid", json.getString("openid"));
				edit.putString("access_token", json.getString("access_token"));
				edit.putString("expires_in", System.currentTimeMillis() + Long.parseLong(json.getString("expires_in")) + "");
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

	}

	//

}
