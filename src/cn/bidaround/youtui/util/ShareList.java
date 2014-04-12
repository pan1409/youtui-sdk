package cn.bidaround.youtui.util;

import android.content.Context;
import android.content.res.Resources;

/**
 * author:gaopan time:2014/3/25
 */

public class ShareList {
	public static String WECHAT = "Wechat";
	public static String WECHATMOMENTS = "WechatMoments";
	public static String SINAWEIBO = "SinaWeibo";
	public static String QQ = "QQ";
	public static String QZONE = "QZone";
	public static String TENCENTWEIBO = "TencentWeibo";
	public static String RENREN = "Renren";
	public static String SHORTMESSAGE = "ShortMessage";
	public static String EMAIL = "Email";

	public static int getLogo(String name,Context context){
		String packName = context.getPackageName();
		Resources res = context.getResources();
		if(WECHAT.equals(name)){
			
			return res.getIdentifier("wxact", "drawable", packName);
		}else if(WECHATMOMENTS.equals(name)){
			
			return res.getIdentifier("pyqact", "drawable", packName);
		}else if(SINAWEIBO.equals(name)){
			
			return res.getIdentifier("xinlangact", "drawable", packName);
		}else if(QQ.equals(name)){
			
			return res.getIdentifier("qqact", "drawable", packName);
		}else if(QZONE.equals(name)){
			
			return res.getIdentifier("qqkjact", "drawable", packName);
		}else if(TENCENTWEIBO.equals(name)){
			
			return res.getIdentifier("tengxunact", "drawable", packName);
		}else if(RENREN.equals(name)){
			
			return res.getIdentifier("renrenact", "drawable", packName);
		}else if(SHORTMESSAGE.equals(name)){
			
			return res.getIdentifier("messact", "drawable", packName);
		}else if(EMAIL.equals(name)){
			
			return res.getIdentifier("mailact", "drawable", packName);
		}
		return -1;
	}

	public static String getTitle(String name) {
		if(WECHAT.equals(name)){
			return "微信";
		}else if(WECHATMOMENTS.equals(name)){
			return "微信朋友圈";
		}else if(SINAWEIBO.equals(name)){
			return "新浪微博";
		}else if(QQ.equals(name)){
			return "QQ";
		}else if(QZONE.equals(name)){
			return "QQ空间";
		}else if(TENCENTWEIBO.equals(name)){
			return "腾讯微博";
		}else if(RENREN.equals(name)){
			return "人人网";
		}else if(SHORTMESSAGE.equals(name)){
			return "短信";
		}else if(EMAIL.equals(name)){
			return "邮件";
		}
		return "";
	}

	// 保存着分享媒体的信息
	// public static TitleAndLogo weiXin = new TitleAndLogo(0, "微信",
	// R.drawable.wxact);
	// public static TitleAndLogo wxPYQ = new TitleAndLogo(1, "朋友圈",
	// R.drawable.pyqact);
	// public static TitleAndLogo sinaWB = new TitleAndLogo(2, "新浪微博",
	// R.drawable.xinlangact);
	// public static TitleAndLogo tencentQQ = new TitleAndLogo(3, "QQ",
	// R.drawable.qqact);
	// public static TitleAndLogo qqKongJian = new TitleAndLogo(4, "QQ空间",
	// R.drawable.qqkjact);
	// public static TitleAndLogo tencentWB = new TitleAndLogo(5, "腾讯微博",
	// R.drawable.tengxunact);
	//
	// public static TitleAndLogo renRen = new TitleAndLogo(6, "人人",
	// R.drawable.renrenact);
	// public static TitleAndLogo sms = new TitleAndLogo(7, "短信",
	// R.drawable.messact);
	// public static TitleAndLogo email = new TitleAndLogo(8, "邮件",
	// R.drawable.mailact);
	// public static TitleAndLogo erWeiMa = new TitleAndLogo(9, "二维码",
	// R.drawable.erweimaact);
	// public static TitleAndLogo copyLink = new TitleAndLogo(10, "复制链接",
	// R.drawable.lianjieact);
	// pagerone的选项
	// public static int WEIXIN = 0;
	// public static int WXPYQ = 1;
	// public static int XINGLANGWEIBO = 2;
	// public static int QQ = 3;
	// public static int QQKONGJIAN = 4;
	// public static int TENGXUNWEIBO = 5;
	// //pagertwo的选项
	// public static int RENREN = 6;
	// public static int MESSAGE = 7;
	// public static int EMAIL = 8;
	// public static int ERWEIMA = 9;
	// public static int COPYLINK = 10;

}
