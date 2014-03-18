package cn.bidaround.youtui.ui;

import java.util.ArrayList;

import cn.bidaround.youtui.R;
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
	public ShareGridAdapter(Activity act,ArrayList<TitleAndLogo> list) {
		this.act = act;
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(act).inflate(R.layout.pagergrid_item,
				null);
		((ImageView)view.findViewById(R.id.logo_imageview)).setImageResource(list.get(position).getLogoSrc());
		((TextView)view.findViewById(R.id.logo_textview)).setText(list.get(position).getTitle());
		convertView = view;
		return convertView;
	}

}
