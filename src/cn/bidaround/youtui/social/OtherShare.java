package cn.bidaround.youtui.social;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

public class OtherShare {
	private Activity act;

	public OtherShare(Activity act) {
		this.act = act;
	}

	public void sendSMS(String sms_body) {
		Uri smsToUri = Uri.parse("smsto:");
		Intent sendIntent = new Intent(Intent.ACTION_VIEW, smsToUri);
		sendIntent.putExtra("sms_body", sms_body);
		sendIntent.setType("vnd.android-dir/mms-sms");
		act.startActivityForResult(sendIntent, 1002);
	}

	// 发邮件
	public void sendMail(String emailBody) {
		Intent email = new Intent(android.content.Intent.ACTION_SEND);
		email.setType("plain/text");
		String emailSubject = "共享软件";
		// 设置邮件默认地址
		// email.putExtra(android.content.Intent.EXTRA_EMAIL, emailReciver);
		// 设置邮件默认标题
		email.putExtra(android.content.Intent.EXTRA_SUBJECT, emailSubject);
		// 设置要默认发送的内容
		email.putExtra(android.content.Intent.EXTRA_TEXT, emailBody);
		// 调用系统的邮件系统
		act.startActivityForResult(Intent.createChooser(email, "请选择邮件发送软件"), 1001);
	}

}
