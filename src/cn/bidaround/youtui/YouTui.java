package cn.bidaround.youtui;

import java.io.File;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import cn.bidaround.point.YtPoint;
import cn.bidaround.youtui.helper.DownloadImage;
import cn.bidaround.youtui.social.ShareData;
import cn.bidaround.youtui.social.YoutuiConstants;
import cn.bidaround.youtui.ui.SharePopupWindow;

public class YouTui {

	/**
	 * 开发者应该在程序开始调用该方法初始化友推sdk，友推sdk的其他操作都依赖于此
	 */
	public static void init(Activity act) {
		YtPoint.getInstance(act);
	}
	/**
	 * 调用该方法调出友推sdk的分享界面
	 *act:调用界面实例
	 *shareData:需要分享的数据
	 */
	public static void show(Activity act, ShareData shareData) {
		new SharePopupWindow(act, shareData, YouTuiViewType.BLANK_FULL, YtPoint.getInstance(act)).show();
	}

	/**
	 * 该方法需要在分享页面弹出时调用，用来将网络图片保存到sdk卡， 这样有些不支持分享网络图片的平台页可以用下载好的本地图片分享
	 */
	public void autoDownImage(final ShareData shareData) {
		new Thread() {
			public void run() {
				downImage(shareData);
			}

		}.start();
	}

	/**
	 * 下载并保存图片
	 */
	private void downImage(ShareData shareData) {
		try {
			String url = shareData.getImageUrl();
			String picPath = url.substring(url.lastIndexOf("/") + 1, url.length());
			File file = new File(Environment.getExternalStorageDirectory() + YoutuiConstants.FILE_SAVE_PATH + picPath);
			DownloadImage.downloadImage(shareData.getImageUrl(), YoutuiConstants.FILE_SAVE_PATH, picPath);
			// 如果下载完成并且getImagePath没有被设置，则调用下载完的本地图片分享
			if (file.exists()) {
				Log.i("--file--", "exists");
				if (shareData.getImagePath() == null) {
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
