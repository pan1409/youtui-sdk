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
		if (convertView == null) {
			View view = LayoutInflater.from(act).inflate(res.getIdentifier("pagergrid_item", "layout", packName), null);
			ImageView imageView = (ImageView) view.findViewById(res.getIdentifier("logo_imageview", "id", packName));
			imageView.setImageResource(ShareList.getLogo(list.get(position),act));
			TextView textView = (TextView) view.findViewById(res.getIdentifier("logo_textview", "id", packName));
			textView.setText(ShareList.getTitle(list.get(position)));
			if (showStyle == 1) {
				textView.setTextColor(0xff6c7471);
			}
			TextView pointText = (TextView) view.findViewById(res.getIdentifier("griditem_point_tv", "id", packName));
			if (ShareList.SINAWEIBO.equals(list.get(position))) {
				 if(pointArr[0]==0){
				 pointText.setVisibility(View.GONE);
				 }else{
				 pointText.setText("+"+pointArr[0]);
				 }
				 
			} else if (ShareList.EMAIL.equals(list.get(position))) {
				 if(pointArr[8]==0){
				 pointText.setVisibility(View.GONE);
				 }else{
				 pointText.setText("+"+pointArr[8]);
				 }

			} else if (ShareList.QQ.equals(list.get(position))) {
				 if(pointArr[5]==0){
				 pointText.setVisibility(View.GONE);
				 }else{
				 pointText.setText("+"+pointArr[5]);
				 }

			} else if (ShareList.QZONE.equals(list.get(position))) {
				 if(pointArr[2]==0){
				 pointText.setVisibility(View.GONE);
				 }else{
				 pointText.setText("+"+pointArr[2]);
				 }

			} else if (ShareList.RENREN.equals(list.get(position))) {
				 if(pointArr[4]==0){
				 pointText.setVisibility(View.GONE);
				 }else{
				 pointText.setText("+"+pointArr[4]);
				 }

			} else if (ShareList.SHORTMESSAGE.equals(list.get(position))) {
				 if(pointArr[7]==0){
				 pointText.setVisibility(View.GONE);
				 }else{
				 pointText.setText("+"+pointArr[7]);
				 }

			} else if (ShareList.TENCENTWEIBO.equals(list.get(position))) {
				 if(pointArr[1]==0){
				 pointText.setVisibility(View.GONE);
				 }else{
				 pointText.setText("+"+pointArr[1]);
				 }

			} else if (ShareList.WECHAT.equals(list.get(position))) {
				 if(pointArr[3]==0){
				 pointText.setVisibility(View.GONE);
				 }else{
				 pointText.setText("+"+pointArr[3]);
				 }

			} else if (ShareList.WECHATMOMENTS.equals(list.get(position))) {
				 if(pointArr[10]==0){
				 pointText.setVisibility(View.GONE);
				 }else{
				 pointText.setText("+"+pointArr[10]);
				 }
				 
			}
			convertView = view;
		}
		return convertView;
	}

}
