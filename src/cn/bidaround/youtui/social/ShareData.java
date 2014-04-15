package cn.bidaround.youtui.social;

import java.io.Serializable;
/**
 * author:gaopan
 * 该类为分享数据类，有些平台有分享限制
 * 友推sdk会过滤掉无法分享的内容，只分享能被平台接受的内容
 * 如果需要分享图片，需要设置imageUrl和imagePath中的一项
 * 如果imageUrl和imagePath都被设置，则优先使用imagePath
 */

public class ShareData implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String text = "分享内容";
	private String imagePath;
	private String description = "分享描述";
	private String title = "分享";
	private String imageUrl;
	private String target_url ;
	
	public String getTarget_url() {
		return target_url;
	}
	/**
	 * 网页链接地址
	 */
	public void setTarget_url(String target_url) {
		this.target_url = target_url;
	}
	public String getDescription() {
		return description;
	}
	/**
	 * 设置分享内容的描述
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	public String getText() {
		return text;
	}
	/**
	 * 设置待分享的文字内容，短信不要超过70字，微博不要超过140字
	 */
	public void setText(String text) {
		this.text = text;
	}
	public String getImagePath() {
		return imagePath;
	}
	/**
	 * 待分享的本地图片路径
	 */
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	public String getTitle() {
		return title;
	}
	/**
	 * 待分享的内容标题
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	/**
	 * 设置分享的网络图片url
	 */
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
}
