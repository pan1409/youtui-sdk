package cn.bidaround.youtui;

import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;
import android.app.Activity;
import cn.bidaround.youtui.helper.DownloadImage;
import cn.bidaround.youtui.point.YtPoint;
import cn.bidaround.youtui.social.KeyInfo;
import cn.bidaround.youtui.social.ShareData;
import cn.bidaround.youtui.social.YoutuiConstants;
import cn.bidaround.youtui.ui.ShareListPopup;
import cn.bidaround.youtui.ui.SharePopupWindow;

public class YouTui {
	/**
	 * 开发者应该在程序开始调用该方法初始化友推sdk，友推sdk的其他操作都依赖于此
	 */
	public static void init(final Activity act) {

		try {
			KeyInfo.parseXML(act);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		YouTuiAcceptor.init(act);
	}

	/**
	 * 调用该方法调出友推sdk的分享界面 act:调用界面实例 shareData:需要分享的数据
	 */
	public static void show(Activity act, ShareData shareData, int style) {
		if (style == YouTuiViewType.BLACK_POPUP) {
			new SharePopupWindow(act, shareData, style, YtPoint.getInstance(act)).show();
		} else if (style == YouTuiViewType.WHITE_LIST) {
			new ShareListPopup(act, shareData, style, YtPoint.getInstance(act)).show();
		}
	}

	/**
	 * 该方法需要在分享页面弹出时调用，用来将网络图片保存到sdk卡， 这样有些不支持分享网络图片的平台页可以用下载好的本地图片分享
	 */
	public void autoDownImage(ShareData shareData) {
		//Log.i("--YouTui--", "DownImage...");
		downImage(shareData);
	}

	/**
	 * 下载并保存图片
	 */
	private void downImage(ShareData shareData) {
		try {
			String url = shareData.getImageUrl();
			String picPath = url.substring(url.lastIndexOf("/") + 1, url.length());
			DownloadImage.downloadImage(shareData.getImageUrl(), YoutuiConstants.FILE_SAVE_PATH, picPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
