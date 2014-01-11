package com.bidaround.youtui.outer.helper;

import java.util.List;

import cn.bidaround.youtui.social.activity.YoutuiConstants;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Description: created by qyj on January 7, 2014
 */
public class AppHelper {
	/**
	 * 通过package name检查APP是否已经安装
	 */
	private static boolean checkApp(Context context, String packageName) {
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> packs = pm.getInstalledPackages(0);
		for (PackageInfo pi : packs) {
			if (pi.applicationInfo.packageName.equals(packageName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 检查新浪微博是否已经安装
	 */
	public static boolean isSinaWeiboExisted(Context context) {
		if (checkApp(context, YoutuiConstants.PACKAGE_NAME_SINA_WEIBO)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 检查腾讯QQ是否已经安装
	 */
	public static boolean isTencentQQExisted(Context context) {
		if (checkApp(context, YoutuiConstants.PACKAGE_NAME_TENCENT_QQ)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 检查人人客户端是否已经安装
	 */
	public static boolean isRenrenExisted(Context context) {
		if (checkApp(context, YoutuiConstants.PACKAGE_NAME_RENREN)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 检查微信是否已经安装
	 */
	public static boolean isWeixinExisted(Context context) {
		if (checkApp(context, YoutuiConstants.PACKAGE_NAME_WEIXIN)) {
			return true;
		} else {
			return false;
		}
	}
}
