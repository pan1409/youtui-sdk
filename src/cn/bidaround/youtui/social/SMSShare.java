package cn.bidaround.youtui.social;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;
import cn.bidaround.youtui.helper.AppHelper;

/**
 * Description: created by qyj on January 7, 2014
 * revise by hcy on February 19, 2014
 */
public class SMSShare {
	private String content;
	private Activity mActivity;

	/**
	 * 填写短信内容并跳转到短信发送界面
	 */
	public void sendSMS(JSONObject jsonCome, Activity activity) {// smscontent为短信的内容
		mActivity = activity;
		getData(jsonCome);
		Uri smsToUri = Uri.parse("smsto://");// 可以填写确定发送的电话号码，如"smsto://15889937371"
		Intent intent = new Intent(android.content.Intent.ACTION_SENDTO,
				smsToUri);
		intent.putExtra("sms_body", content);
		if (AppHelper.isIntentAvailable(activity, intent)) {
			// 显示跳转成功
			mActivity.startActivity(intent);
		} else {
			// 显示没有客户端
			Toast.makeText(mActivity, "系统没有安装短信客户端", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 获取所需参数
	 */
	private void getData(JSONObject jsonCome) {
		try {
			content = jsonCome.getString("content");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}