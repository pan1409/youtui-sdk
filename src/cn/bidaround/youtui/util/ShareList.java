package cn.bidaround.youtui.util;
import cn.bidaround.youtui.R;
/**
 * author:gaopan
 * time:2014/3/25
 */

public interface ShareList {
	//保存着分享媒体的信息
	public static TitleAndLogo weiXin = new TitleAndLogo(0, "微信", R.drawable.wxact);
	public static TitleAndLogo wxPYQ = new TitleAndLogo(1, "朋友圈", R.drawable.pyqact);
	public static TitleAndLogo sinaWB = new TitleAndLogo(2, "新浪微博", R.drawable.xinlangact);
	public static TitleAndLogo tencentQQ = new TitleAndLogo(3, "QQ", R.drawable.qqact);
	public static TitleAndLogo qqKongJian = new TitleAndLogo(4, "QQ空间", R.drawable.qqkjact);
	public static TitleAndLogo tencentWB = new TitleAndLogo(5, "腾讯微博", R.drawable.tengxunact);
	
	public static TitleAndLogo renRen = new TitleAndLogo(6, "人人", R.drawable.renrenact);
	public static TitleAndLogo sms = new TitleAndLogo(7, "短信", R.drawable.messact);
	public static TitleAndLogo email = new TitleAndLogo(8, "邮件", R.drawable.mailact);
	public static TitleAndLogo erWeiMa = new TitleAndLogo(9, "二维码", R.drawable.erweimaact);
	public static TitleAndLogo copyLink = new TitleAndLogo(10, "复制链接", R.drawable.lianjieact);
	//pagerone的选项
	public static int WEIXIN = 0;
	public static int WXPYQ = 1;
	public static int XINGLANGWEIBO = 2;
	public static int QQ = 3;
	public static int QQKONGJIAN = 4;
	public static int TENGXUNWEIBO = 5;	
	//pagertwo的选项
	public static int RENREN = 6;
	public static int MESSAGE = 7;
	public static int EMAIL = 8;
	public static int ERWEIMA = 9;
	public static int COPYLINK = 10;
	
}
