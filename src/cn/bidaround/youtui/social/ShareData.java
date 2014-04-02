package cn.bidaround.youtui.social;

import java.io.Serializable;
/**
 * author:gaopan
 * 该类为分享数据类，有些平台有分享限制，比如说只能分享文字
 * 友推sdk会过滤掉无法分享的内容，只分享能被平台接受的内容
 */

public class ShareData implements Serializable{

	private static final long serialVersionUID = 1L;
	//待分享文本(微博不能超过140个字符，短信不能超过70个字符）
	private String text = "分享";
	//待分享的图片路径，本地图片和网络图片只需要选择一个，如果都选择了则分享本地图片
	private String imagePath;
	//待分享的文件描述
	private String description = "";
	//待分享的标题，微信在没有设置标题的时候分享无法实现，所以设置默认标题，开发者应该设置重设该参数
	private String title = "分享";
	//网络图片url，本地图片和网络图片只需要选择一个，如果都选择了则分享本地图片
	private String imageUrl;
	//待分享的网页跳转链接
	private String target_url ;
	public String getTarget_url() {
		return target_url;
	}
	public void setTarget_url(String target_url) {
		this.target_url = target_url;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
}
