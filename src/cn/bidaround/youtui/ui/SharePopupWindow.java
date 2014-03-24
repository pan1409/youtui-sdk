package cn.bidaround.youtui.ui;

import java.util.ArrayList;

import com.tencent.weibo.sdk.android.api.util.Util;
import com.viewpagerindicator.CirclePageIndicator;
import cn.bidaround.youtui.R;
import cn.bidaround.youtui.helper.AccessTokenKeeper;
import cn.bidaround.youtui.helper.AppHelper;
import cn.bidaround.youtui.helper.DownloadImage;
import cn.bidaround.youtui.social.OtherShare;
import cn.bidaround.youtui.social.ShareData;
import cn.bidaround.youtui.social.YoutuiConstants;
import cn.bidaround.youtui.util.DensityUtil;
import cn.bidaround.youtui.util.ShareList;
import cn.bidaround.youtui.util.TitleAndLogo;
import cn.bidaround.youtui.wxapi.WXEntryActivity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.Toast;
/**
 * author:gaopan
 */
public class SharePopupWindow extends PopupWindow implements OnClickListener,
		OnItemClickListener {
	
	private String message;
	private Activity act;
	private GridView pagerOne_gridView;
	private GridView pagerTwo_gridView;
	private Handler mHandler = new Handler();
	private ShareData shareData;
	
	public SharePopupWindow(Activity act,ShareData shareData) {
		super(act);
		this.act = act;
		this.shareData = shareData;
	}

	/*
	 * 显示分享主界面
	 */
	public void show() {

		View view = LayoutInflater.from(act)
				.inflate(R.layout.share_popup, null);
		// 消失按钮点击事件
		Button cancelBt = (Button) view.findViewById(R.id.cancel_bt);
		cancelBt.setOnClickListener(this);

		initViewPager(view);
		setFocusable(true);
		setOutsideTouchable(true);
		setContentView(view);
		setWidth(act.getWindowManager().getDefaultDisplay().getWidth());
		setHeight(DensityUtil.dip2px(act, 350));
		showAtLocation(act.findViewById(R.id.popup_bt), Gravity.BOTTOM, 0, 0);
	}
	

	void initViewPager(View view) {
		ViewPager viewPager = (ViewPager) view
				.findViewById(R.id.share_viewpager);
		ArrayList<View> pagerList = new ArrayList<View>();
		// 初始化第一页
		View pagerOne = LayoutInflater.from(act).inflate(
				R.layout.share_fragment, null);
		pagerOne_gridView = (GridView) pagerOne
				.findViewById(R.id.sharepager_grid);
		ArrayList<TitleAndLogo> logoList_pagerOne = new ArrayList<TitleAndLogo>();
		logoList_pagerOne.add(ShareList.sinaWB);
		logoList_pagerOne.add(ShareList.tencentQQ);
		logoList_pagerOne.add(ShareList.qqKongJian);
		logoList_pagerOne.add(ShareList.weiXin);
		logoList_pagerOne.add(ShareList.renRen);
		logoList_pagerOne.add(ShareList.tencentWB);
		ShareGridAdapter pagerOne_gridAdapter = new ShareGridAdapter(act,
				logoList_pagerOne);
		pagerOne_gridView.setAdapter(pagerOne_gridAdapter);
		pagerOne_gridView.setOnItemClickListener(this);
		// 初始化第二页
		ArrayList<TitleAndLogo> logoList_pagerTwo = new ArrayList<TitleAndLogo>();
		logoList_pagerTwo.add(ShareList.wxPYQ);
		logoList_pagerTwo.add(ShareList.sms);
		logoList_pagerTwo.add(ShareList.email);
		logoList_pagerTwo.add(ShareList.erWeiMa);
		logoList_pagerTwo.add(ShareList.copyLink);
		
		View pagerTwo = LayoutInflater.from(act).inflate(
				R.layout.share_fragment, null);
		pagerTwo_gridView = (GridView) pagerTwo
				.findViewById(R.id.sharepager_grid);
		ShareGridAdapter pagerTwo_gridAdapter = new ShareGridAdapter(act,
				logoList_pagerTwo);
		pagerTwo_gridView.setAdapter(pagerTwo_gridAdapter);
		pagerTwo_gridView.setOnItemClickListener(this);

		pagerList.add(pagerOne);
		pagerList.add(pagerTwo);
		SharePagerAdapter PagerAdapter = new SharePagerAdapter(pagerList);
		viewPager.setAdapter(PagerAdapter);
		CirclePageIndicator indicator = (CirclePageIndicator) view.findViewById(R.id.indicator);
		indicator.setViewPager(viewPager);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.cancel_bt:
			dismiss();
			break;

		default:
			break;
		}

	}
	/*
	 * 复制链接
	 * API 11之前用android.text.ClipboardManager;
	 * API 11之后用android.content.ClipboardManager
	 */
	void copyLink(){
		mHandler.post(new Runnable() {
			@SuppressLint("NewApi")
			public void run() {
				if (android.os.Build.VERSION.SDK_INT >= 11) {
					android.content.ClipboardManager clip = (android.content.ClipboardManager) act.getSystemService(Context.CLIPBOARD_SERVICE);
					clip.setPrimaryClip(android.content.ClipData
							.newPlainText("link", message));
					if (clip.hasPrimaryClip()) {
						Toast.makeText(act, "复制成功", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(act, "复制失败，请手动复制", Toast.LENGTH_SHORT).show();
					}
				} else {
					android.text.ClipboardManager clip = (android.text.ClipboardManager) act.getSystemService(Context.CLIPBOARD_SERVICE);
					clip.setText(message);
					if (clip.hasText()) {
						Toast.makeText(act, "复制成功", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(act, "复制失败，请手动复制", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View arg1,
			int position, long arg3) {
		if (adapterView == pagerOne_gridView) {
			switch (position) {
			//新浪微博
			case ShareList.XINGLANGWEIBO:
				if (!AccessTokenKeeper.readAccessToken(act).isSessionValid()) {
					Toast.makeText(act, "请先授权登录", Toast.LENGTH_SHORT).show();
					Intent shareAuthIt = new Intent(act,
							ShareAuthActivity.class);
					shareAuthIt.putExtra("from", "sina");
					act.startActivity(shareAuthIt);
				}else{
					Intent shareIt = new Intent(act, ShareActivity.class);
					shareIt.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
					shareIt.putExtra("shareData",shareData);
					shareIt.putExtra("from", "sina");
					act.startActivity(shareIt);				
				}

				break;
				//微信
			case ShareList.WEIXIN:
				if (AppHelper.isWeixinExisted(act)) {
					Intent wxIt = new Intent(act, WXEntryActivity.class);
					wxIt.putExtra("wx", true);
					wxIt.putExtra("fromshare", true);
					wxIt.putExtra("shareData", shareData);
					act.startActivity(wxIt);
				} else {
					Toast.makeText(act, "未安装微信", Toast.LENGTH_SHORT).show();
				}

				break;
				//QQ
			case ShareList.QQ:
				Intent QQIt = new Intent(act, QQSkipActivity.class);
				act.startActivity(QQIt);
				break;
				//QQ空间
			case ShareList.QQKONGJIAN:
				break;
				//人人
			case ShareList.RENREN:
				Intent renrenIt = new Intent(act, ShareActivity.class);
				renrenIt.putExtra("from", "renren");
				renrenIt.putExtra("shareData", shareData);
				act.startActivity(renrenIt);
				break;
				//腾讯微博
			case ShareList.TENGXUNWEIBO:
				if(Util.getSharePersistent(act, "ACCESS_TOKEN")==null||"".equals(Util.getSharePersistent(act, "ACCESS_TOKEN"))){
					Intent tencentWbIt = new Intent(act, ShareAuthActivity.class);
					tencentWbIt.putExtra("from", "TencentWB");
					act.startActivity(tencentWbIt);
				}else{
					Intent tencentWbIt = new Intent(act, ShareActivity.class);
					tencentWbIt.putExtra("from", "TencentWB");
					act.startActivity(tencentWbIt);
				}
				break;
			default:
				break;
			}

		} else if (adapterView == pagerTwo_gridView) {
			switch (position) {
			//朋友圈
			case ShareList.WXPYQ%6:
				if (AppHelper.isWeixinExisted(act)) {
					Intent wxIt = new Intent(act, WXEntryActivity.class);
					wxIt.putExtra("pyq", true);
					wxIt.putExtra("fromshare", true);
					wxIt.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
					wxIt.putExtra("shareData", shareData);
					act.startActivity(wxIt);
				} else {
					Toast.makeText(act, "未安装微信", Toast.LENGTH_SHORT).show();
				}

				break;
				//短信
			case ShareList.MESSAGE%6:
				new OtherShare(act).sendSMS(shareData.getText());
				break;
				//邮件
			case ShareList.EMAIL%6:
				new OtherShare(act).sendMail(shareData.getText());
				break;
				//二维码
			case ShareList.ERWEIMA%6:

				break;
				//复制链接
			case ShareList.COPYLINK%6:
				copyLink();
				break;

			default:
				break;
			}
		}

	}

}
