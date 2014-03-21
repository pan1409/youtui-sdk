package cn.bidaround.youtui.social;

import java.io.Serializable;
/**
 * author:gaopan
 */

public class ShareData implements Serializable{
	//待分享文本(微博不能超过140个字符，短信不能超过70个字符）
	private String text = "default text";
	//待分享的图片路径
	private String imagePath;
	//带分享的文件描述
	private String description = "default description";
	//待分享的标题
	private String title = "default title";
	//网络图片
	private String imageUrl;
	//待分享的网页链接
	private String target_url = "http://www.baidu.com";
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
