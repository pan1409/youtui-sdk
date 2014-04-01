package cn.bidaround.youtui.ui;

import java.util.ArrayList;
import com.viewpagerindicator.CirclePageIndicator;
import cn.bidaround.point.YtPoint;
import cn.bidaround.youtui.R;
import cn.bidaround.youtui.helper.AccessTokenKeeper;
import cn.bidaround.youtui.helper.AppHelper;
import cn.bidaround.youtui.social.OtherShare;
import cn.bidaround.youtui.social.ShareData;
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
import android.widget.TextView;
import android.widget.Toast;

/**
 * author:gaopan time:2014/3/25
 */
public class SharePopupWindow extends PopupWindow implements OnClickListener, OnItemClickListener {

	private String message;
	private Activity act;
	private GridView pagerOne_gridView;
	private GridView pagerTwo_gridView;
	ShareGridAdapter pagerOne_gridAdapter;
	private YtPoint point;
	private ShareData shareData;
	private int showStyle = -1;
	private Handler mHandler = new Handler();
	

	public SharePopupWindow(Activity act, ShareData shareData, int showStyle,YtPoint point) {
		super(act);
		this.act = act;
		this.shareData = shareData;
		this.showStyle = showStyle;
		this.point = point;
	}

	/*
	 * 显示分享主界面
	 */
	public void show() {
		View view = LayoutInflater.from(act).inflate(R.layout.share_popup, null);
		initButton(view);
		initViewPager(view);
		// 设置popupwindow的属性
		setFocusable(true);
		setOutsideTouchable(true);
		setContentView(view);
		setWidth(act.getWindowManager().getDefaultDisplay().getWidth());
		setHeight(DensityUtil.dip2px(act, 350));
		showAtLocation(act.findViewById(R.id.popup_bt), Gravity.BOTTOM, 0, 0);
	}

	void initButton(View view) {
		TextView know = (TextView) view.findViewById(R.id.share_popup_knowtv);
		TextView check = (TextView) view.findViewById(R.id.share_popup_checktv);
		// 在style变化时改变背景和文字颜色
		if (showStyle == 1) {
			view.setBackgroundColor(0xffffffff);
			know.setTextColor(0xff6c7471);
			check.setTextColor(0xff6c7471);
		}
		know.setOnClickListener(this);
		check.setOnClickListener(this);
		// 消失按钮点击事件
		Button cancelBt = (Button) view.findViewById(R.id.cancel_bt);
		cancelBt.setOnClickListener(this);
	}

	/**
	 * 初始化viewpager
	 */
	void initViewPager(View view) {
		ViewPager viewPager = (ViewPager) view.findViewById(R.id.share_viewpager);
		ArrayList<View> pagerList = new ArrayList<View>();
		// 初始化第一页
		View pagerOne = LayoutInflater.from(act).inflate(R.layout.share_fragment, null);
		pagerOne_gridView = (GridView) pagerOne.findViewById(R.id.sharepager_grid);
		ArrayList<TitleAndLogo> logoList_pagerOne = new ArrayList<TitleAndLogo>();
		logoList_pagerOne.add(ShareList.weiXin);
		logoList_pagerOne.add(ShareList.wxPYQ);
		logoList_pagerOne.add(ShareList.sinaWB);
		logoList_pagerOne.add(ShareList.tencentQQ);
		logoList_pagerOne.add(ShareList.qqKongJian);
		logoList_pagerOne.add(ShareList.tencentWB);

		pagerOne_gridAdapter = new ShareGridAdapter(act, logoList_pagerOne, showStyle, point.getPoint());
		pagerOne_gridView.setAdapter(pagerOne_gridAdapter);
		pagerOne_gridView.setOnItemClickListener(this);
		// 初始化第二页
		ArrayList<TitleAndLogo> logoList_pagerTwo = new ArrayList<TitleAndLogo>();
		logoList_pagerTwo.add(ShareList.renRen);
		logoList_pagerTwo.add(ShareList.sms);
		logoList_pagerTwo.add(ShareList.email);
		logoList_pagerTwo.add(ShareList.erWeiMa);
		logoList_pagerTwo.add(ShareList.copyLink);

		View pagerTwo = LayoutInflater.from(act).inflate(R.layout.share_fragment, null);
		pagerTwo_gridView = (GridView) pagerTwo.findViewById(R.id.sharepager_grid);
		ShareGridAdapter pagerTwo_gridAdapter = new ShareGridAdapter(act, logoList_pagerTwo, showStyle, point.getPoint());
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
		switch (v.getId()) {
		case R.id.cancel_bt:
			dismiss();
			break;
		case R.id.share_popup_knowtv:
			
			break;
		case R.id.share_popup_checktv:
			Intent checkIt = new Intent(act, ShareAuthActivity.class);
			checkIt.putExtra("from", "check");
			act.startActivity(checkIt);
			break;
		default:
			break;
		}

	}

	/*
	 * 复制链接 API 11之前用android.text.ClipboardManager; API
	 * 11之后用android.content.ClipboardManager
	 */
	void copyLink() {
		mHandler.post(new Runnable() {
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
		if (adapterView == pagerOne_gridView) {
			switch (position) {
			// 新浪微博
			case ShareList.XINGLANGWEIBO:
				if (AppHelper.isSinaWeiboExisted(act)) {
					if (!AccessTokenKeeper.readAccessToken(act).isSessionValid()) {
						Toast.makeText(act, "请先授权登录", Toast.LENGTH_SHORT).show();
						Intent shareAuthIt = new Intent(act, ShareAuthActivity.class);
						shareAuthIt.putExtra("from", "sina");
						act.startActivity(shareAuthIt);
					} else {
						Intent shareIt = new Intent(act, ShareActivity.class);
						shareIt.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
						shareIt.putExtra("shareData", shareData);
						shareIt.putExtra("from", "sina");
						shareIt.putExtra("pointArr", point.getPoint());
						act.startActivityForResult(shareIt, ShareList.XINGLANGWEIBO);
					}
				} else {
					Toast.makeText(act, "未安装新浪微博", Toast.LENGTH_SHORT).show();
				}
				break;
			// 微信
			case ShareList.WEIXIN:
				if (AppHelper.isWeixinExisted(act)) {
					Intent wxIt = new Intent(act, WXEntryActivity.class);
					wxIt.putExtra("wx", true);
					wxIt.putExtra("pointArr", point.getPoint());
					wxIt.putExtra("fromshare", true);
					wxIt.putExtra("shareData", shareData);
					
					act.startActivity(wxIt);
				} else {
					Toast.makeText(act, "未安装微信", Toast.LENGTH_SHORT).show();
				}

				break;
			// QQ
			case ShareList.QQ:
				Intent qqIt = new Intent(act, ShareActivity.class);
				qqIt.putExtra("shareData", shareData);
				qqIt.putExtra("from", "QQ");
				act.startActivity(qqIt);
				break;
			// QQ空间
			case ShareList.QQKONGJIAN:
				Intent qzoneIt = new Intent(act, ShareActivity.class);
				qzoneIt.putExtra("shareData", shareData);
				qzoneIt.putExtra("from", "Qzone");
				act.startActivity(qzoneIt);

				break;
			// 人人
			case ShareList.WXPYQ:
				if (AppHelper.isWeixinExisted(act)) {
					Intent wxIt = new Intent(act, WXEntryActivity.class);
					wxIt.putExtra("pyq", true);
					wxIt.putExtra("fromshare", true);
					wxIt.putExtra("shareData", shareData);
					wxIt.putExtra("pointArr", point.getPoint());
					act.startActivity(wxIt);
				} else {
					Toast.makeText(act, "未安装微信", Toast.LENGTH_SHORT).show();
				}

				break;

			// 腾讯微博
			case ShareList.TENGXUNWEIBO:
				Intent qqWBIt = new Intent(act, ShareActivity.class);
				qqWBIt.putExtra("shareData", shareData);
				qqWBIt.putExtra("from", "QQWB");
				act.startActivity(qqWBIt);
				break;
			default:
				break;
			}

		} else if (adapterView == pagerTwo_gridView) {
			switch (position) {
			// 朋友圈
			case ShareList.RENREN % 6:
				Intent renrenIt = new Intent(act, ShareActivity.class);
				renrenIt.putExtra("from", "renren");
				renrenIt.putExtra("shareData", shareData);
				act.startActivity(renrenIt);
				break;

			// 短信
			case ShareList.MESSAGE % 6:
				new OtherShare(act).sendSMS(shareData.getText());
				break;
			// 邮件
			case ShareList.EMAIL % 6:
				new OtherShare(act).sendMail(shareData.getText());
				break;
			// 二维码
			case ShareList.ERWEIMA % 6:

				break;
			// 复制链接
			case ShareList.COPYLINK % 6:
				copyLink();
				break;

			default:
				break;
			}
		}

	}

}
