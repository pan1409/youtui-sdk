package cn.bidaround.youtui.ui;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import cn.bidaround.youtui.R;
import cn.bidaround.youtui.YouTui;
import cn.bidaround.youtui.helper.AppHelper;
import cn.bidaround.youtui.point.YtPoint;
import cn.bidaround.youtui.social.KeyInfo;
import cn.bidaround.youtui.social.OtherShare;
import cn.bidaround.youtui.social.ShareData;
import cn.bidaround.youtui.social.YoutuiConstants;
import cn.bidaround.youtui.util.DensityUtil;
import cn.bidaround.youtui.util.ShareList;
import cn.bidaround.youtui.wxapi.WXEntryActivity;

/**
 * author:gaopan time:2014/3/25
 */
public class SharePopupWindow extends PopupWindow implements OnClickListener, OnItemClickListener, OnPageChangeListener {

	private Activity act;
	private GridView pagerOne_gridView, pagerTwo_gridView;
	private ShareGridAdapter pagerOne_gridAdapter, pagerTwo_gridAdapter;
	private View sharepopup_indicator_linelay;
	private YtPoint point;
	private ShareData shareData;
	private int showStyle = -1;
	private Handler mHandler = new Handler();
	private ImageView zeroIamge, oneIamge;
	private Resources res;
	private String packName, message;
	private ArrayList<String> enList;
	private static int weChatIndex, wechatMomentsIndex, sinaWeiboIndex, qQIndex, qZoneIndex, tencentWeiboIndex, renrenIndex, shortMessageIndex, emailIndex;

	public SharePopupWindow() {
	}

	public SharePopupWindow(Context context) {
	}

	public SharePopupWindow(Activity act, ShareData shareData, int showStyle, YtPoint point) {
		super(act);
		this.act = act;
		this.shareData = shareData;
		this.showStyle = showStyle;
		this.point = point;
	}

	/*
	 * 显示分享主界面
	 */
	@SuppressWarnings("deprecation")
	public void show() {
		YtPoint.getInstance(act).refresh(act);
		new YouTui().autoDownImage(shareData);

		res = act.getResources();
		packName = act.getPackageName();

		View view = LayoutInflater.from(act).inflate(res.getIdentifier("share_popup", "layout", packName), null);
		initButton(view);
		initViewPager(view);
		// 设置popupwindow的属性
		setFocusable(true);
		setOutsideTouchable(true);
		setContentView(view);
		setWidth(act.getWindowManager().getDefaultDisplay().getWidth());
		setHeight(DensityUtil.dip2px(act, 350));

		showAtLocation(getContentView(), Gravity.BOTTOM, 0, 0);
	}

	void initButton(View view) {
		sharepopup_indicator_linelay = view.findViewById(R.id.sharepopup_indicator_linelay);
		zeroIamge = (ImageView) view.findViewById(res.getIdentifier("sharepopup_zero_iv", "id", packName));
		oneIamge = (ImageView) view.findViewById(res.getIdentifier("sharepopup_one_iv", "id", packName));
		TextView know = (TextView) view.findViewById(res.getIdentifier("share_popup_knowtv", "id", packName));
		TextView check = (TextView) view.findViewById(res.getIdentifier("share_popup_checktv", "id", packName));
		// 在style变化时改变背景和文字颜色
		if (showStyle == 1) {
			view.setBackgroundColor(0xffffffff);
			know.setTextColor(0xff6c7471);
			check.setTextColor(0xff6c7471);
		}
		know.setOnClickListener(this);
		check.setOnClickListener(this);
		// 消失按钮点击事件
		Button cancelBt = (Button) view.findViewById(res.getIdentifier("cancel_bt", "id", packName));
		cancelBt.setOnClickListener(this);
	}

	/**
	 * 初始化viewpager
	 */
	void initViewPager(View view) {
		ViewPager viewPager = (ViewPager) view.findViewById(res.getIdentifier("share_viewpager", "id", packName));
		ArrayList<View> pagerList = new ArrayList<View>();

		enList = KeyInfo.enList;
		Log.i("----", enList.size() + "");

		// 如果分享的数量<=6，只放置一页
		if (enList.size() <= 6) {
			View pagerOne = LayoutInflater.from(act).inflate(res.getIdentifier("share_pager", "layout", packName), null);
			pagerOne_gridView = (GridView) pagerOne.findViewById(res.getIdentifier("sharepager_grid", "id", packName));
			pagerOne_gridAdapter = new ShareGridAdapter(act, enList, showStyle, point.getPoint());
			pagerOne_gridView.setAdapter(pagerOne_gridAdapter);
			pagerOne_gridView.setOnItemClickListener(this);
			pagerList.add(pagerOne);
		} else if (enList.size() > 6 && enList.size() <= 12) {
			// 分享数量在7~12之间,放置两页
			ArrayList<String> pagerOneList = new ArrayList<String>();
			for (int i = 0; i < 6; i++) {
				pagerOneList.add(enList.get(i));
			}
			// 第一页
			View pagerOne = LayoutInflater.from(act).inflate(res.getIdentifier("share_pager", "layout", packName), null);
			pagerOne_gridView = (GridView) pagerOne.findViewById(res.getIdentifier("sharepager_grid", "id", packName));
			pagerOne_gridAdapter = new ShareGridAdapter(act, pagerOneList, showStyle, point.getPoint());
			pagerOne_gridView.setAdapter(pagerOne_gridAdapter);
			pagerOne_gridView.setOnItemClickListener(this);
			pagerList.add(pagerOne);

			ArrayList<String> pagerTwoList = new ArrayList<String>();
			for (int i = 6; i < enList.size(); i++) {
				pagerTwoList.add(enList.get(i));
			}
			// 第二页
			View pagerTwo = LayoutInflater.from(act).inflate(res.getIdentifier("share_pager", "layout", packName), null);
			pagerTwo_gridView = (GridView) pagerTwo.findViewById(res.getIdentifier("sharepager_grid", "id", packName));
			pagerTwo_gridAdapter = new ShareGridAdapter(act, pagerTwoList, showStyle, point.getPoint());
			pagerTwo_gridView.setAdapter(pagerTwo_gridAdapter);
			pagerTwo_gridView.setOnItemClickListener(this);
			pagerList.add(pagerTwo);
		}

		SharePagerAdapter pagerAdapter = new SharePagerAdapter(pagerList);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setOffscreenPageLimit(2);
		getIndex();
		if (enList.size() > 6 && enList.size() <= 12) {
			viewPager.setOnPageChangeListener(this);
		} else if (enList.size() <= 6) {
			sharepopup_indicator_linelay.setVisibility(View.INVISIBLE);
		}
	}

	private void getIndex() {
		weChatIndex = enList.indexOf(ShareList.WECHAT);
		wechatMomentsIndex = enList.indexOf(ShareList.WECHATMOMENTS);
		sinaWeiboIndex = enList.indexOf(ShareList.SINAWEIBO);
		qQIndex = enList.indexOf(ShareList.QQ);
		qZoneIndex = enList.indexOf(ShareList.QZONE);
		tencentWeiboIndex = enList.indexOf(ShareList.TENCENTWEIBO);
		renrenIndex = enList.indexOf(ShareList.RENREN);
		shortMessageIndex = enList.indexOf(ShareList.SHORTMESSAGE);
		emailIndex = enList.indexOf(ShareList.EMAIL);
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == res.getIdentifier("cancel_bt", "id", packName)) {
			dismiss();
		} else if (v.getId() == res.getIdentifier("share_popup_knowtv", "id", packName)) {
			Intent knowIt = new Intent(act, ShareActivity.class);
			knowIt.putExtra("from", "know");
			act.startActivity(knowIt);

		} else if (v.getId() == res.getIdentifier("share_popup_checktv", "id", packName)) {
			Intent checkIt = new Intent(act, ShareActivity.class);
			checkIt.putExtra("from", "check");
			act.startActivity(checkIt);
		}

	}

	/*
	 * 复制链接 API 11之前用android.text.ClipboardManager; API
	 * 11之后用android.content.ClipboardManager
	 */
	void copyLink() {
		mHandler.post(new Runnable() {
			@SuppressWarnings("deprecation")
			@SuppressLint("NewApi")
			public void run() {
				if (android.os.Build.VERSION.SDK_INT >= 11) {
					android.content.ClipboardManager clip = (android.content.ClipboardManager) act.getSystemService(Context.CLIPBOARD_SERVICE);
					clip.setPrimaryClip(android.content.ClipData.newPlainText("link", message));
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
	public void onItemClick(AdapterView<?> adapterView, View arg1, int position, long arg3) {
		// 如果传的是url而没有本地图片
		if (shareData.getImagePath() == null && shareData.getImageUrl() != null) {
			String url = shareData.getImageUrl();
			String fileName = url.substring(url.lastIndexOf("/"));
			shareData.setImagePath(Environment.getExternalStorageDirectory() + YoutuiConstants.FILE_SAVE_PATH + fileName);
		}
		if (adapterView == pagerOne_gridView) {
			// 新浪微博
			if (position == sinaWeiboIndex % 6 && sinaWeiboIndex / 6 == 0) {
				if (AppHelper.isSinaWeiboExisted(act)) {
					Intent shareIt = new Intent(act, ShareActivity.class);
					shareIt.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					shareIt.putExtra("shareData", shareData);
					shareIt.putExtra("from", "sina");
					shareIt.putExtra("pointArr", point.getPoint());
					act.startActivityForResult(shareIt, sinaWeiboIndex);
				} else {
					Toast.makeText(act, "未安装新浪微博", Toast.LENGTH_SHORT).show();
				}
				// 微信
			} else if (position == weChatIndex && weChatIndex / 6 == 0) {
				if (AppHelper.isWeixinExisted(act)) {
					Intent wxIt = new Intent(act, WXEntryActivity.class);
					wxIt.putExtra("wx", true);
					wxIt.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					wxIt.putExtra("pointArr", point.getPoint());
					wxIt.putExtra("fromshare", true);
					wxIt.putExtra("shareData", shareData);
					act.startActivityForResult(wxIt, weChatIndex);
				} else {
					Toast.makeText(act, "未安装微信", Toast.LENGTH_SHORT).show();
				}
				// QQ
			} else if (position == qQIndex && qQIndex / 6 == 0) {
				Intent qqIt = new Intent(act, ShareActivity.class);
				qqIt.putExtra("shareData", shareData);
				qqIt.putExtra("from", "QQ");
				qqIt.putExtra("pointArr", point.getPoint());
				act.startActivityForResult(qqIt, qQIndex);
				// QQ空间
			} else if (position == qZoneIndex && qZoneIndex / 6 == 0) {
				Intent qzoneIt = new Intent(act, ShareActivity.class);
				qzoneIt.putExtra("shareData", shareData);
				qzoneIt.putExtra("from", "Qzone");
				qzoneIt.putExtra("pointArr", point.getPoint());
				act.startActivityForResult(qzoneIt, qZoneIndex);
				// 微信朋友圈
			} else if (position == wechatMomentsIndex && wechatMomentsIndex / 6 == 0) {
				if (AppHelper.isWeixinExisted(act)) {
					Intent wxIt = new Intent(act, WXEntryActivity.class);
					wxIt.putExtra("pyq", true);
					wxIt.putExtra("fromshare", true);
					wxIt.putExtra("shareData", shareData);
					wxIt.putExtra("pointArr", point.getPoint());
					act.startActivityForResult(wxIt, wechatMomentsIndex);
				} else {
					Toast.makeText(act, "未安装微信", Toast.LENGTH_SHORT).show();
				}
				// 腾讯微博
			} else if (position == tencentWeiboIndex && tencentWeiboIndex / 6 == 0) {
				Intent qqWBIt = new Intent(act, ShareActivity.class);
				qqWBIt.putExtra("shareData", shareData);
				qqWBIt.putExtra("from", "QQWB");
				qqWBIt.putExtra("pointArr", point.getPoint());
				act.startActivityForResult(qqWBIt, tencentWeiboIndex);
				// 人人网
			} else if (position == renrenIndex % 6 && renrenIndex / 6 == 0) {
				if (AppHelper.isRenrenExisted(act)) {
					Intent renrenIt = new Intent(act, ShareActivity.class);
					renrenIt.putExtra("from", "renren");
					renrenIt.putExtra("shareData", shareData);
					renrenIt.putExtra("pointArr", point.getPoint());
					act.startActivityForResult(renrenIt, renrenIndex);
				} else {
					Toast.makeText(act, "未安装人人网客户端", Toast.LENGTH_SHORT).show();
				}
				// 短信
			} else if (position == shortMessageIndex % 6 && shortMessageIndex / 6 == 0) {
				new OtherShare(act).sendSMS(shareData.getText());
				// 邮件
			} else if (position == emailIndex % 6 && emailIndex / 6 == 0) {
				new OtherShare(act).sendMail(shareData.getText());
			}

		} else if (adapterView == pagerTwo_gridView) {
			// 人人网
			if (position == renrenIndex % 6 && renrenIndex / 6 == 1) {
				if (AppHelper.isRenrenExisted(act)) {
					Intent renrenIt = new Intent(act, ShareActivity.class);
					renrenIt.putExtra("from", "renren");
					renrenIt.putExtra("shareData", shareData);
					renrenIt.putExtra("pointArr", point.getPoint());
					act.startActivityForResult(renrenIt, renrenIndex);
				} else {
					Toast.makeText(act, "未安装人人网客户端", Toast.LENGTH_SHORT).show();
				}
				// 短信
			} else if (position == shortMessageIndex % 6 && shortMessageIndex / 6 == 1) {
				new OtherShare(act).sendSMS(shareData.getText());
				// 邮件
			} else if (position == emailIndex % 6 && emailIndex / 6 == 1) {
				new OtherShare(act).sendMail(shareData.getText());
			}
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int index) {
		// viewpager下标
		switch (index) {
		case 0:
			zeroIamge.setImageDrawable(act.getResources().getDrawable(res.getIdentifier("guide_dot_white", "drawable", packName)));
			oneIamge.setImageDrawable(act.getResources().getDrawable(res.getIdentifier("guide_dot_black", "drawable", packName)));
			break;
		case 1:
			zeroIamge.setImageDrawable(act.getResources().getDrawable(res.getIdentifier("guide_dot_black", "drawable", packName)));
			oneIamge.setImageDrawable(act.getResources().getDrawable(res.getIdentifier("guide_dot_white", "drawable", packName)));
			break;

		default:
			break;
		}

	}

}
