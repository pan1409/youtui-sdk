package cn.bidaround.youtui.ui;

/**
 * author:gaopan
 * time:2014/3/25
 */

import java.util.ArrayList;
import android.app.Activity;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.bidaround.youtui.YouTuiViewType;
import cn.bidaround.youtui.point.ChannelId;
import cn.bidaround.youtui.util.DensityUtil;
import cn.bidaround.youtui.util.ShareList;

public class ShareGridAdapter extends BaseAdapter {
	private Activity act;
	private ArrayList<String> list;
	private int showStyle;
	private int[] pointArr;
	private Resources res;
	private String packName;

	public ShareGridAdapter(Activity act, ArrayList<String> list, int showStyle, int[] pointArr) {
		this.act = act;
		this.list = list;
		this.showStyle = showStyle;
		this.pointArr = pointArr;
		res = act.getResources();
		packName = act.getPackageName();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {	
		//使用convertView优化listview
		if (convertView == null) {
			View view = null;
			if(showStyle==YouTuiViewType.BLACK_POPUP){
				view = LayoutInflater.from(act).inflate(res.getIdentifier("pagergrid_item", "layout", packName), null);			
			}else if(showStyle==YouTuiViewType.WHITE_LIST){
				view = LayoutInflater.from(act).inflate(res.getIdentifier("sharelist_item", "layout", packName), null);
			}	
			convertView = view;
		}
		
		TextView pointText = null;
		if(showStyle==YouTuiViewType.BLACK_POPUP){
			ImageView imageView = (ImageView) convertView.findViewById(res.getIdentifier("logo_imageview", "id", packName));
			TextView textView = (TextView) convertView.findViewById(res.getIdentifier("logo_textview", "id", packName));
			// 设置社交平台logo 
			imageView.setImageResource(ShareList.getLogo(list.get(position), act));
			// 积分textview
			textView.setText(ShareList.getTitle(list.get(position)));
			pointText = (TextView) convertView.findViewById(res.getIdentifier("griditem_point_tv", "id", packName));
		}else if(showStyle==YouTuiViewType.WHITE_LIST){
			ImageView imageView = (ImageView) convertView.findViewById(res.getIdentifier("sharelistitem_logo_image", "id", packName));
			TextView textView = (TextView) convertView.findViewById(res.getIdentifier("sharelistitem_platform_text", "id", packName));
			// 设置社交平台logo
			imageView.setImageResource(ShareList.getLogo(list.get(position), act));
			// 积分textview
			textView.setText(ShareList.getTitle(list.get(position)));
			pointText = (TextView) convertView.findViewById(res.getIdentifier("sharelistitem_point_text", "id", packName));
		}

		// 显示积分
		if (ShareList.SINAWEIBO.equals(list.get(position))) {
			showPoint(pointText, ChannelId.SINAWEIBO);
		} else if (ShareList.EMAIL.equals(list.get(position))) {
			showPoint(pointText, ChannelId.EMAIL);
		} else if (ShareList.QQ.equals(list.get(position))) {
			showPoint(pointText, ChannelId.QQ);
		} else if (ShareList.QZONE.equals(list.get(position))) {
			showPoint(pointText, ChannelId.QZONE);
		} else if (ShareList.RENREN.equals(list.get(position))) {
			showPoint(pointText, ChannelId.RENN);
		} else if (ShareList.SHORTMESSAGE.equals(list.get(position))) {
			showPoint(pointText, ChannelId.MESSAGE);
		} else if (ShareList.TENCENTWEIBO.equals(list.get(position))) {
			showPoint(pointText, ChannelId.TENCENTWEIBO);
		} else if (ShareList.WECHAT.equals(list.get(position))) {
			showPoint(pointText, ChannelId.WECHAT);
		} else if (ShareList.WECHATMOMENTS.equals(list.get(position))) {
			showPoint(pointText, ChannelId.WECHATFRIEND);
		}
		return convertView;
	}

	/**
	 * 显示积分
	 * 
	 * @param pointText
	 * @param channelId
	 */
	private void showPoint(TextView pointText, int channelId) {
		//黑色樣式下積分大於10時，要將積分TextView加寬才能顯示完全
		if(showStyle==YouTuiViewType.BLACK_POPUP){
			if (pointArr[channelId] >= 10) {
				pointText.getLayoutParams().width = DensityUtil.dip2px(act, 40);
			}
		}
		//積分爲0時不顯示
		if (pointArr[channelId] == 0) {
			pointText.setVisibility(View.INVISIBLE);
		} else {
			pointText.setVisibility(View.VISIBLE);
			pointText.setText("+" + pointArr[channelId]);
		}

	}

}
