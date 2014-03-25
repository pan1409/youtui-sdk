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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ShareGridAdapter extends BaseAdapter {
	private Activity act;
	private ArrayList<TitleAndLogo> list;
	private int showStyle;
	public ShareGridAdapter(Activity act,ArrayList<TitleAndLogo> list,int showStyle) {
		this.act = act;
		this.list = list;
		this.showStyle = showStyle;
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
			//处理积分显示
			switch (list.get(position).getId()) {
			case ShareList.XINGLANGWEIBO:	
				break;
			case ShareList.QQ:	
				break;
			case ShareList.QQKONGJIAN:	
				break;
			case ShareList.WEIXIN:	
				break;
			case ShareList.RENREN:	
				break;
			case ShareList.TENGXUNWEIBO:	
				break;
			case ShareList.WXPYQ:	
				break;
			case ShareList.MESSAGE:	
				break;
			case ShareList.EMAIL:	
				break;
			case ShareList.ERWEIMA:	
				break;
			case ShareList.COPYLINK:	
				break;
			default:
				break;
			}
			convertView = view;
		}
		return convertView;
	}

}
