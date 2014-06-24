package cn.bidaround.youtui;
import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Context;
import cn.bidaround.youtui.helper.DownloadImage;
import cn.bidaround.youtui.point.YtPoint;
import cn.bidaround.youtui.social.KeyInfo;
import cn.bidaround.youtui.social.ShareData;
import cn.bidaround.youtui.social.YoutuiConstants;
import cn.bidaround.youtui.ui.ListPopup;
import cn.bidaround.youtui.ui.ViewPagerPopup;
import cn.bidaround.youtui.ui.YTBasePopupWindow;
/**
 * @author Administrator
 *	该类进行sdk的初始化
 */
public class YouTui {
	private static final String TAG = "--YouTui--";
	public static Context appContext;
	/**
	 * 开发者应该在程序开始调用该方法初始化友推sdk，友推sdk的其他操作都依赖于此
	 */
	public static void init(final Activity act) {
		/**获取应用Context,appContext生命周期长，使用appContext能避免Context泄露*/
		appContext = act.getApplicationContext();
		ShareData.init();
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
	public static void show(final Activity act, int style,boolean hasAct) {	
		if (style == YouTuiViewType.BLACK_POPUP) {
			new ViewPagerPopup(act, style,hasAct).show();
		} else if (style == YouTuiViewType.WHITE_LIST) {
			new ListPopup(act,style,hasAct).show();
		}
	}
	/**
	 * 下载并保存图片
	 * 该方法需要在分享页面弹出时调用，用来将网络图片保存到sdk卡， 这样有些不支持分享网络图片的平台页可以用下载好的本地图片分享
	 * @throws IOException 
	 */
	public void autoDownImage() throws IOException {	
			String url = ShareData.shareData.getImageUrl();
			if(url!=null){
				String picPath = url.substring(url.lastIndexOf("/") + 1, url.length());
				DownloadImage.down_file(ShareData.shareData.getImageUrl(), YoutuiConstants.FILE_SAVE_PATH, picPath);
			}
	}
	/**判断是否有积分,如果没有积分活动或者今天的积分已经获得则返回false*/
	public static boolean hasPoint(){
		return YtPoint.hasPoint();
	}
	/**统计用户使用情况,释放内存*/
	public static void release(final Context context){
		
		ShareData.shareData = null;	
		YTBasePopupWindow.mHandler = null;
		new Thread(){
			public void run() {
				YouTuiAcceptor.close(context);
			};
		}.start();
	}
}
