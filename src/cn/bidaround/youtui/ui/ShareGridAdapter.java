package cn.bidaround.youtui.ui;
/**
 * author:gaopan
 * time:2014/3/25
 */

import java.util.ArrayList;
import cn.bidaround.youtui.R;
import cn.bidaround.youtui.util.ShareList;
import cn.bidaround.youtui.util.TitleAndLogo;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ShareGridAdapter extends BaseAdapter {
	private Activity act;
	private ArrayList<TitleAndLogo> list;
	private int showStyle;
	private int[] pointArr;
	public ShareGridAdapter(Activity act,ArrayList<TitleAndLogo> list,int showStyle,int[] pointArr) {
		this.act = act;
		this.list = list;
		this.showStyle = showStyle;
		this.pointArr = pointArr;
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
		if(convertView==null){
			View view = LayoutInflater.from(act).inflate(R.layout.pagergrid_item,null);
			ImageView imageView = (ImageView)view.findViewById(R.id.logo_imageview);
			imageView.setImageResource(list.get(position).getLogoSrc());
			TextView textView = (TextView)view.findViewById(R.id.logo_textview);
			textView.setText(list.get(position).getTitle());
			if(showStyle==1){
				textView.setTextColor(0xff6c7471);
			}
			TextView pointText = (TextView) view.findViewById(R.id.griditem_point_tv);
			//处理积分显示
			switch (list.get(position).getId()) {
			case ShareList.XINGLANGWEIBO:
				if(pointArr[0]==0){
					pointText.setVisibility(View.GONE);
				}else{
					pointText.setText("+"+pointArr[0]);
				}
				break;
			case ShareList.QQ:
				if(pointArr[5]==0){
					pointText.setVisibility(View.GONE);
				}else{
					pointText.setText("+"+pointArr[5]);
				}
				break;
			case ShareList.QQKONGJIAN:	
				if(pointArr[2]==0){
					pointText.setVisibility(View.GONE);
				}else{
					pointText.setText("+"+pointArr[2]);
				}
				break;
			case ShareList.WEIXIN:	
				if(pointArr[3]==0){
					pointText.setVisibility(View.GONE);
				}else{
					pointText.setText("+"+pointArr[3]);
				}
				break;
			case ShareList.RENREN:	
				if(pointArr[4]==0){
					pointText.setVisibility(View.GONE);
				}else{
					pointText.setText("+"+pointArr[4]);
				}
				break;
			case ShareList.TENGXUNWEIBO:
				if(pointArr[1]==0){
					pointText.setVisibility(View.GONE);
				}else{
					pointText.setText("+"+pointArr[1]);
				}
				break;
			case ShareList.WXPYQ:	
				if(pointArr[10]==0){
					pointText.setVisibility(View.GONE);
				}else{
					pointText.setText("+"+pointArr[10]);
				}
				break;
			case ShareList.MESSAGE:
				if(pointArr[7]==0){
					pointText.setVisibility(View.GONE);
				}else{
					pointText.setText("+"+pointArr[7]);
				}
				break;
			case ShareList.EMAIL:
				if(pointArr[8]==0){
					pointText.setVisibility(View.GONE);
				}else{
					pointText.setText("+"+pointArr[8]);
				}
				break;
			case ShareList.ERWEIMA:
				if(pointArr[6]==0){
					pointText.setVisibility(View.GONE);
				}else{
					pointText.setText("+"+pointArr[6]);
				}
				break;
			case ShareList.COPYLINK:
				if(pointArr[9]==0){
					pointText.setVisibility(View.GONE);
				}else{
					pointText.setText("+"+pointArr[9]);
				}
				break;
			default:
				break;
			}
			convertView = view;
		}
		return convertView;
	}

}
