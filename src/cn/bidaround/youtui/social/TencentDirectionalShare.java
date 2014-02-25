package cn.bidaround.youtui.social;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bidaround.youtui.YouTui;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Description: created by qyj on January 7, 2014
 * revise by hcy on February 19, 2014
 */
public class TencentDirectionalShare {
	private Tencent mTencent;
	private String title;
	private String description;
	private String target_url;
	private String image_url;
	private Activity mActivity;

	
	/**
	 * QQ定向分享总的操作函数
	 * @param jsonObject
	 * @return
	 */
	public void shareByTencentDirection(JSONObject jsonCome,Activity activity) {
		mActivity = activity;
		init(activity);
		directionalShare(jsonCome,activity);
	}

	/**
	 * 初始化Tencent
	 */
	private void init(Activity activity) {
		if (mTencent == null) {
			mTencent = Tencent.createInstance(YoutuiConstants.TENCENT_APP_ID,
					activity.getApplicationContext());
		}
	}

	/**
	 * 定向分享的内容
	 */
	private void directionalShare(JSONObject jsonCome,Activity activity) {
		getData(jsonCome);
		Bundle params = new Bundle();
		params.putInt(Tencent.SHARE_TO_QQ_KEY_TYPE,
				Tencent.SHARE_TO_QQ_TYPE_DEFAULT);
		params.putString(Tencent.SHARE_TO_QQ_TITLE, title);// 分享标题
		params.putString(Tencent.SHARE_TO_QQ_SUMMARY, description);// 分享描述
		params.putString(Tencent.SHARE_TO_QQ_TARGET_URL, target_url);// 跳转链接
		params.putString(Tencent.SHARE_TO_QQ_IMAGE_URL, image_url);// 网络图片的URL，请严格控制尺寸，否则不能显示。（65px*65px较佳）
		// params.putString(Tencent.SHARE_TO_QQ_IMAGE_LOCAL_URL,"/storage/sdcard0/Tencent/QQfile_recv/3.gif");//本地图片的URL，不建议采用这种方式
		share(params,activity);
	}

	/**
	 * 定向分享功能(不需要登录或获取token就可以执行)
	 */
	private void share(Bundle params,Activity activity) {
		mTencent.shareToQQ(activity, params,
				new IUiListener() {
					@Override
					public void onComplete(JSONObject response) {
						// 显示分享成功
						Toast.makeText(mActivity, "分享成功", Toast.LENGTH_LONG).show();
					}
					@Override
					public void onError(UiError e) {
						// 显示分享错误
						String error = e.errorMessage;				
						Toast.makeText(mActivity, "分享失败", Toast.LENGTH_LONG).show();
						Toast.makeText(mActivity, error, Toast.LENGTH_LONG).show();
					}
					@Override
					public void onCancel() {
						// 显示分享取消
						Toast.makeText(mActivity, "操作成功", Toast.LENGTH_LONG).show();
					}
				});
	}

	/**
	 * 获取所需参数
	 */
	private void getData(JSONObject jsonObject){
		try {
			title = jsonObject.getString("title");
			description = jsonObject.getString("description");
			target_url = jsonObject.getString("target_url");
			image_url = jsonObject.getString("image_url");
		} catch (JSONException e) {
			e.printStackTrace();
		}	
	}
}
