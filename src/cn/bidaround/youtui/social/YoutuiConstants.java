package cn.bidaround.youtui.social;

/**
 * @author gaopan
 * @since 14/3/25
 * 友推sdk所需要的部分常量
 */
public class YoutuiConstants {



	public static final String SINA_WEIBO_SCOPE = "email,direct_messages_read,direct_messages_write,"
			+ "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
			+ "follow_app_official_microblog," + "invitation_write";
	
	public static final String TENCENT_SCOPE = "all";
	// 微信4.2以上支持分享至朋友圈
	public static final int WEIXIN_TIMELINE_SUPPORTED_VERSION = 0x21020001;
	
	public static final String RENREN_SCOPE = "read_user_blog read_user_photo read_user_status read_user_album read_user_comment read_user_share publish_blog publish_share send_notification photo_upload status_update create_album publish_comment publish_feed";

	// package name
	public static final String PACKAGE_NAME_SINA_WEIBO = "com.sina.weibo";
	public static final String PACKAGE_NAME_TENCENT_QQ = "com.tencent.mobileqq";
	public static final String PACKAGE_NAME_RENREN = "com.renren.mobile.android";
	public static final String PACKAGE_NAME_WEIXIN = "com.tencent.mm";

	// request code
	public static final int SINA_WEIBO_REQUEST_CODE = 100;
	public static final int TENCENT_WEIBO_REQUEST_CODE = 101;
	public static final int TENCENT_QZONE_REQUEST_CODE = 102;
	public static final int TENCENT_DIRECTIONAL_SHARE_REQUEST_CODE = 103;
	public static final int WEIXIN_SHARE_REQUEST_CODE = 104;
	public static final int RENREN_REQUEST_CODE = 105;
	public static final int SMS_REQUEST_CODE = 106;
	public static final int EMAIL_REQUEST_CODE = 107;
	// result
	public static final int RESULT_SUCCESSFUL = -1;
	public static final int RESULT_CANCEL = 0;
	public static final int RESULT_ERROR = 1;
	public static final int RESULT_SIGNATURE_ERROR = 2;
	public static final int APP_NOT_EXIST = 3;
	public static final int APP_ID_MISSING = 4;
	
	public static final String YT_URL = "http://youtui.mobi";
	//public static final String YT_URL = "http://192.168.2.109";
	//public static final String YT_URL = "http://192.168.2.107";
	// 文件保存路径
	public static final String FILE_SAVE_PATH = "/youtui/";
	//链接跳转网页
	public static final String YOUTUI_LINK_URL = "http://youtui.mobi/link/";
	//友推支持的平台个数
	public static final int SHARE_SIZE = 11;
}
