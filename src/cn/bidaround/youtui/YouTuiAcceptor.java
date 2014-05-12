package cn.bidaround.youtui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Environment;
import android.telephony.TelephonyManager;
import cn.bidaround.youtui.point.YtPoint;
import cn.bidaround.youtui.social.KeyInfo;

/**
 * @author gaopan 读取res,packageName,cardNum,imei等信息以便于后续使用
 */
public class YouTuiAcceptor {
	/* 邀请码 */
	private static String inviteNum;
	/* 应用的名称 */
	private static String appName;
	/* 设备信息 */
	public static String imei, sdk, model, sys;
	/* 应用资源 */
	public static Resources res;
	/* 应用包名 */
	public static String packName;
	/* SIM卡号 */
	public static String cardNum;
	/* 用于判断是否已找到所要的apk */
	private static boolean flag;

	/* 获取友推渠道号(格式：appName_yt)，以获得appName */
	private static void getYoutuiChannel(Context context) {
		ApplicationInfo info;
		try {
			PackageManager packageManager = context.getPackageManager();
			info = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			String msg = info.metaData.getString("YOUTUI_CHANNEL");
			appName = msg.substring(0, msg.length() - 3);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* 获取设备信息 */
	@SuppressWarnings("deprecation")
	private static void readPhoneInfo(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (tm != null) {
			imei = tm.getDeviceId(); /* 获取imei号 */
		}
		sdk = android.os.Build.VERSION.SDK; // SDK号
		model = android.os.Build.MODEL; // 手机型号
		sys = android.os.Build.VERSION.RELEASE; // android系统版本号
	}

	/**
	 * 获取应用资源
	 * 
	 * @param context
	 */
	private static void setRes(Context context) {
		res = context.getResources();
	}

	private static void setPackName(Context context) {
		packName = context.getPackageName();
	}

	private static void getCardNum(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (tm != null) {
			cardNum = tm.getSimSerialNumber();
		}
	}

	/* 把相关信息（设备信息、appId、邀请码等）发送到服务器 */
	public static void doPost() {
		String actionUrl = "http://youtui.mobi/activity/checkCode";
		List<NameValuePair> params = new ArrayList<NameValuePair>(6);
		params.add(new BasicNameValuePair("appId", KeyInfo.youTui_AppKey));
		params.add(new BasicNameValuePair("inviteCode", inviteNum));
		params.add(new BasicNameValuePair("imei", imei));
		params.add(new BasicNameValuePair("sdkVersion", sdk));
		params.add(new BasicNameValuePair("phoneType", model));
		params.add(new BasicNameValuePair("sysVersion", sys));
		params.add(new BasicNameValuePair("type", "auto"));// 接收推荐类型 auto
															// 自动,manual 人工
		try {
			// 请求到服务器
			post(actionUrl, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* 关闭应用时发送相关信息（appId和IMEI码） */
	public static void toPost() {
		String actionUrl = "http://youtui.mobi/activity/closeApp";
		List<NameValuePair> params = new ArrayList<NameValuePair>(6);
		params.add(new BasicNameValuePair("appId", KeyInfo.youTui_AppKey));
		params.add(new BasicNameValuePair("imei", imei));
		try {
			post(actionUrl, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* 遍历sd卡中的文件，找到对应的apk，从名字中获得邀请码 */
	private static void getInviteNum() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			// 获得SD卡父目录的路径，以便还可以遍历外置SD卡
			File path = Environment.getExternalStorageDirectory().getParentFile();
			File[] files = path.listFiles();// 读取
			getFileName(files);
		}
	}

	/* 从文件名中读取邀请码 */
	private static String getFileName(File[] files) {
		if (flag != true) {
			// 先判断目录是否为空，否则会报空指针
			if (files == null) {
				return null;
			}
			for (File file : files) {
				if (file.isDirectory()) {
					// 若是文件目录。继续读
					getFileName(file.listFiles());
				} else {
					String fileName = file.getName();
					// 扫描apk 安装包中后缀为yt.apk 且包含appName的文件名
					// 格式为appName_邀请码_yt.apk，如tuoche_100041_yt.apk
					if (fileName.endsWith("yt.apk") && fileName.contains(appName)) {
						@SuppressWarnings("unused")
						int l = appName.length();
						inviteNum = fileName.substring(fileName.indexOf("_") + 1, fileName.lastIndexOf("_")).toString();
						flag = true;
						break;
					}
				}
			}
			return null;
		} else {
			return null;
		}
	}

	/**
	 * 通过拼接的方式构造请求内容，实现参数传输以及文件传输
	 * 
	 * @param actionUrl
	 * @param params
	 * @return
	 */
	private static void post(String actionUrl, List<NameValuePair> params) {
		HttpClient httpclient = new DefaultHttpClient();
		// 你的URL
		HttpPost httppost = new HttpPost(actionUrl);

		try {
			// Your DATA
			httppost.setEntity(new UrlEncodedFormEntity(params));
			@SuppressWarnings("unused")
			HttpResponse response;
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 此方法用于邀请码等的自动上传，必须放在用户应用的初始化函数里
	 * 
	 * @param context
	 */
	public static void init(final Context context) {
		/* 新开一个线程 */
		new Thread() {
			public void run() {
				/* 获得应用包名 */
				setPackName(context);
				/* 获得资源 */
				setRes(context);
				/* 获取SIM卡号 */
				getCardNum(context);
				/* 获取应用的渠道号 */
				getYoutuiChannel(context);
				/* 获取邀请码 */
				getInviteNum();
				/* 获取手机信息 */
				readPhoneInfo(context);
				/* 获取应用信息 */
				YtPoint.getAppInfo();
				/**演示用，打包时要注销*/
//				MainActivity main = (MainActivity) context;
//				YtPoint.init(main, false);
//				main.mHandler.sendEmptyMessage(MainActivity.MAIN_POINT_INIT);
				/* 发送到服务器 */
				doPost();
				// 初始化友推积分
				// YtPoint.getInstance(context);
				// YtPoint.init(context);
			}
		}.start();
	}

	/**
	 * 此方法用于应用关闭时数据的上传（统计使用时间），必须写在应用退出的相关函数里
	 * 
	 * @param context
	 */
	public static void close(final Context context) {
		/* 获取手机信息 */
		readPhoneInfo(context);
		/* 发送到服务器 */
		toPost();
	}
}