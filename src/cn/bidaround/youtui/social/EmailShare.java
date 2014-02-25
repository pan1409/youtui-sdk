package cn.bidaround.youtui.social;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bidaround.youtui.helper.AppHelper;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Description: created by qyj on January 7, 2014
 * revise by hcy on February 19, 2014
 */
public class EmailShare {
	private String subject;
	private String content;
	private Activity mActivity;

	/**
	 * 填写邮件内容并跳转到邮件发送界面
	 */
	public void sendEmail(JSONObject jsonCome, Activity activity) {
		mActivity = activity;
		getData(jsonCome);
		Uri uri = Uri.parse("mailto:");// 可以直接填写确定的收件人，如"mailto:1353015987@qq.com"
		Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
		// it.putExtra(Intent.EXTRA_EMAIL, email_address);// 收件人（一定要为String[]）
		intent.putExtra(Intent.EXTRA_SUBJECT, subject); // 邮件的主题
		intent.putExtra(Intent.EXTRA_TEXT, content);// 邮件的内容
		if (AppHelper.isIntentAvailable(activity, intent)) {
			// 显示跳转成功(自己会显示）
			activity.startActivity(intent);
		} else {
			// 显示没有客户端
			Toast.makeText(mActivity, "系统没有安装电子邮件客户端", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 获取所需参数
	 */
	private void getData(JSONObject jsonCome) {
		try {
			subject = jsonCome.getString("subject");
			content = jsonCome.getString("content");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
}