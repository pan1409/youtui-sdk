package cn.bidaround.youtui.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.bidaround.youtui.R;
import cn.bidaround.youtui.YouTuiAcceptor;
import cn.bidaround.youtui.helper.Util;
import cn.bidaround.youtui.net.NetUtil;
import cn.bidaround.youtui.net.YTShare;
import cn.bidaround.youtui.point.YtPoint;
import cn.bidaround.youtui.social.KeyInfo;
import cn.bidaround.youtui.social.ShareData;
import cn.bidaround.youtui.social.YoutuiConstants;
import cn.bidaround.youtui.util.DensityUtil;

/**
 * author:gaopan time:2014/3/25
 */
public class SharePopupWindow extends YTPopupWindow implements OnClickListener, OnItemClickListener, OnPageChangeListener {

	private GridView pagerOne_gridView, pagerTwo_gridView;
	private ShareGridAdapter pagerOne_gridAdapter, pagerTwo_gridAdapter;
	private View sharepopup_indicator_linelay;
	private ImageView zeroIamge, oneIamge;
	private ArrayList<String> enList;
	private ShareViewPager viewPager;
	private int[] pointArr = new int[YoutuiConstants.SHARE_SIZE];

	public SharePopupWindow(Activity act, ShareData shareData, int showStyle, YtPoint point) {
		super(act,shareData);
		this.shareData = shareData;
		this.showStyle = showStyle;
		this.point = point;
		instance = this;
	}

	/**
	 * 显示分享主界面
	 */
	@SuppressWarnings("deprecation")
	public void show() {
		View view = LayoutInflater.from(act).inflate(YouTuiAcceptor.res.getIdentifier("share_popup", "layout", YouTuiAcceptor.packName), null);
		initButton(view);
		initViewPager(view);
		// 设置popupwindow的属性
		setFocusable(true);
		setOutsideTouchable(true);
		setContentView(view);
		setWidth(act.getWindowManager().getDefaultDisplay().getWidth());
		setHeight(DensityUtil.dip2px(act, 350));
		setAnimationStyle(R.style.SharePopupAnim);
		showAtLocation(getContentView(), Gravity.BOTTOM, 0, 0);
	}
	
	/**
	 * 初始化取消，查看积分，了解积分按钮
	 * @param view
	 */

	private void initButton(View view) {
		zeroIamge = (ImageView) view.findViewById(YouTuiAcceptor.res.getIdentifier("sharepopup_zero_iv", "id", YouTuiAcceptor.packName));
		oneIamge = (ImageView) view.findViewById(YouTuiAcceptor.res.getIdentifier("sharepopup_one_iv", "id", YouTuiAcceptor.packName));
		TextView know = (TextView) view.findViewById(YouTuiAcceptor.res.getIdentifier("share_popup_knowtv", "id", YouTuiAcceptor.packName));
		TextView check = (TextView) view.findViewById(YouTuiAcceptor.res.getIdentifier("share_popup_checktv", "id", YouTuiAcceptor.packName));
		// 在style变化时改变背景和文字颜色
		if (showStyle == 1) {
			view.setBackgroundColor(0xffffffff);
			know.setTextColor(0xff6c7471);
			check.setTextColor(0xff6c7471);
		}
		know.setOnClickListener(this);
		check.setOnClickListener(this);
		// 消失按钮点击事件
		Button cancelBt = (Button) view.findViewById(YouTuiAcceptor.res.getIdentifier("cancel_bt", "id", YouTuiAcceptor.packName));
		cancelBt.setOnClickListener(this);
	}

	/**
	 * 初始化viewpager
	 */
	private void initViewPager(View view) {
		viewPager = (ShareViewPager) view.findViewById(YouTuiAcceptor.res.getIdentifier("share_viewpager", "id", YouTuiAcceptor.packName));
		ArrayList<View> pagerList = new ArrayList<View>();
		Util.addArr(point.getPoint(), pointArr);
		enList = KeyInfo.enList;
		// Log.i("----", enList.size() + "");

		// 如果分享的数量<=6，只放置一页
		if (enList.size() <= 6) {
			View pagerOne = LayoutInflater.from(act).inflate(YouTuiAcceptor.res.getIdentifier("share_pager", "layout", YouTuiAcceptor.packName), null);
			pagerOne_gridView = (GridView) pagerOne.findViewById(YouTuiAcceptor.res.getIdentifier("sharepager_grid", "id", YouTuiAcceptor.packName));
			pagerOne_gridAdapter = new ShareGridAdapter(act, enList, showStyle, pointArr);
			pagerOne_gridView.setAdapter(pagerOne_gridAdapter);
			pagerOne_gridView.setOnItemClickListener(this);
			pagerList.add(pagerOne);
		} else if (enList.size() > 6 && enList.size() <= 12) {
			// 分享数量在7~12之间,放置两页
			ArrayList<String> pagerOneList = new ArrayList<String>();
			for (int i = 0; i < 6; i++) {
				pagerOneList.add(enList.get(i));
			}
			// 初始化第一页
			View pagerOne = LayoutInflater.from(act).inflate(YouTuiAcceptor.res.getIdentifier("share_pager", "layout", YouTuiAcceptor.packName), null);
			pagerOne_gridView = (GridView) pagerOne.findViewById(YouTuiAcceptor.res.getIdentifier("sharepager_grid", "id", YouTuiAcceptor.packName));
			pagerOne_gridAdapter = new ShareGridAdapter(act, pagerOneList, showStyle, pointArr);
			pagerOne_gridView.setAdapter(pagerOne_gridAdapter);
			pagerOne_gridView.setOnItemClickListener(this);
			pagerList.add(pagerOne);

			ArrayList<String> pagerTwoList = new ArrayList<String>();
			for (int i = 6; i < enList.size(); i++) {
				pagerTwoList.add(enList.get(i));
			}
			// 初始化第二页
			View pagerTwo = LayoutInflater.from(act).inflate(YouTuiAcceptor.res.getIdentifier("share_pager", "layout", YouTuiAcceptor.packName), null);
			pagerTwo_gridView = (GridView) pagerTwo.findViewById(YouTuiAcceptor.res.getIdentifier("sharepager_grid", "id", YouTuiAcceptor.packName));
			pagerTwo_gridAdapter = new ShareGridAdapter(act, pagerTwoList, showStyle, pointArr);
			pagerTwo_gridView.setAdapter(pagerTwo_gridAdapter);
			pagerTwo_gridView.setOnItemClickListener(this);
			pagerList.add(pagerTwo);
		}

		SharePagerAdapter pagerAdapter = new SharePagerAdapter(pagerList);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setOffscreenPageLimit(2);
		// 设置滑动下标
		if (enList.size() > 6 && enList.size() <= 12) {
			viewPager.setOnPageChangeListener(this);
		} else if (enList.size() <= 6) {
			sharepopup_indicator_linelay.setVisibility(View.INVISIBLE);
		}
	}


	/**
	 * 消失和查看积分，了解积分按钮事件
	 */
	@Override
	public void onClick(View v) {

		if (v.getId() == YouTuiAcceptor.res.getIdentifier("cancel_bt", "id", YouTuiAcceptor.packName)) {
			dismiss();
		} else if (v.getId() == YouTuiAcceptor.res.getIdentifier("share_popup_knowtv", "id", YouTuiAcceptor.packName)) {
			Intent knowIt = new Intent(act, ShareActivity.class);
			knowIt.putExtra("from", "know");
			act.startActivity(knowIt);

		} else if (v.getId() == YouTuiAcceptor.res.getIdentifier("share_popup_checktv", "id", YouTuiAcceptor.packName)) {
			Intent checkIt = new Intent(act, ShareActivity.class);
			checkIt.putExtra("from", "check");
			act.startActivity(checkIt);
		}

	}
	/**
	 * 分享按钮点击事件
	 */
	@Override
	public void onItemClick(AdapterView<?> adapterView, View arg1, int position, long arg3) {
		//先判断网络连接
		if(NetUtil.isNetworkConnected(act)){
			// 如果传的是url而没有本地图片，先将网络图片存入sd卡，并将地址赋给shareData的ImagePath
			if (shareData.getImagePath() == null && shareData.getImageUrl() != null) {
				String url = shareData.getImageUrl();
				String fileName = url.substring(url.lastIndexOf("/"));
				shareData.setImagePath(Environment.getExternalStorageDirectory() + YoutuiConstants.FILE_SAVE_PATH + fileName);
			}
			if (adapterView == pagerOne_gridView) {
				new YTShare().doShare(act, position, 0, point, shareData);
				
			} else if (adapterView == pagerTwo_gridView) {
				new YTShare().doShare(act, position, 1, point, shareData);
			}
		}else{
			Toast.makeText(act, "无网络连接，请查看您的网络情况", Toast.LENGTH_SHORT).show();
		}

	}


	/**
	 * 刷新显示积分
	 */
	@Override
	public void refresh(int[] arr) {
		//将传入的积分数组赋值给pointArr
		for(int i=0;i<pointArr.length;i++){
			pointArr[i] = arr[i];
		}
		if(pagerOne_gridAdapter!=null){
			pagerOne_gridAdapter.notifyDataSetChanged();
		}
		if(pagerTwo_gridAdapter!=null){
			pagerTwo_gridAdapter.notifyDataSetChanged();
		}	
	}
	/**
	 * viewpager状态变化监听
	 */
	@Override
	public void onPageScrollStateChanged(int arg0) {

	}
	/**
	 * viewpager滑动监听
	 */
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}
	/**
	 * 页面选择监听，这里用来显示viewpager下标
	 */
	@Override
	public void onPageSelected(int index) {
		// viewpager下标
		switch (index) {
		case 0:
			zeroIamge.setImageDrawable(act.getResources().getDrawable(YouTuiAcceptor.res.getIdentifier("guide_dot_white", "drawable", YouTuiAcceptor.packName)));
			oneIamge.setImageDrawable(act.getResources().getDrawable(YouTuiAcceptor.res.getIdentifier("guide_dot_black", "drawable", YouTuiAcceptor.packName)));
			break;
		case 1:
			zeroIamge.setImageDrawable(act.getResources().getDrawable(YouTuiAcceptor.res.getIdentifier("guide_dot_black", "drawable", YouTuiAcceptor.packName)));
			oneIamge.setImageDrawable(act.getResources().getDrawable(YouTuiAcceptor.res.getIdentifier("guide_dot_white", "drawable", YouTuiAcceptor.packName)));
			break;

		default:
			break;
		}

	}

}
