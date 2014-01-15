package cn.bidaround.youtui.social;

import cn.bidaround.youtui.helper.AppHelper;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

/**
 * Description: created by qyj on January 7, 2014
 */
public class SMSActivity extends Activity {
	private String content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sendSMS();
	}

	/**
	 * 填写短信内容并跳转到短信发送界面
	 */
	private void sendSMS() {// smscontent为短信的内容
		getData();
		Uri smsToUri = Uri.parse("smsto://");// 可以填写确定发送的电话号码，如"smsto://15889937371"
		Intent intent = new Intent(android.content.Intent.ACTION_SENDTO,
				smsToUri);
		intent.putExtra("sms_body", content);
		if (AppHelper.isIntentAvailable(this, intent)) {
			// 显示跳转成功
			result(YoutuiConstants.RESULT_SUCCESSFUL, null);
			startActivity(intent);
		} else {
			// 显示跳转取消
			result(YoutuiConstants.RESULT_CANCEL, null);
		}
		finish();
	}

	/**
	 * 获取所需参数
	 */
	private void getData() {
		Intent intent = getIntent();
		content = intent.getStringExtra("content");
	}

	/**
	 * 返回分享状况信息。
	 */
	private void result(int i, Intent intent) {
		this.setResult(i, intent);
		this.finish();
	}
}