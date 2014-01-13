package cn.bidaround.youtui;

import android.app.Activity;
import android.content.Intent;

public class YouTui {
	
	public Activity webviewActivity = new YouTuiWebview();
	
	/* 显示友推组件,供外部调用 */
	public static void show(Activity context, int type){
		//setContent(type);
		Intent intent = context.getIntent();
		//调用友推组件，方便用户分享推荐，或举行推荐奖励活动
		intent.setClass(context, YouTuiWebview.class);
		context.startActivity(intent);
	}
}
