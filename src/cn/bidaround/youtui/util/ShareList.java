package cn.bidaround.youtui.util;

import android.content.Context;
import android.content.res.Resources;

/**
 * 局哦去分享平台的logo和名字
 * @author gaopan 
 * @since 2014/3/25
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
	public static String MORE_SHARE = "More";
	/**
	 * 获取分享平台的lolo
	 * @param name
	 * @param context
	 * @return
	 */
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
		}else if(MORE_SHARE.equals(name)){
			
			return res.getIdentifier("more", "drawable", packName);
		}
		return -1;
	}
	/**
	 * 获取分享平台的名字
	 * @param name
	 * @return
	 */
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
		}else if(MORE_SHARE.equals(name)){
			return "更多";
		}
		return "";
	}
}
