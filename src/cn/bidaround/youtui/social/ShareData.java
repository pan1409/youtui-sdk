package cn.bidaround.youtui.social;

import java.io.Serializable;
/**
 * author:gaopan
 */

public class ShareData implements Serializable{
	private static final long serialVersionUID = 1L;
	public static int TYPE_TEXT = 0;
	public static int TYPE_IAMGE = 1;
	//待分享文本
	private String text = "default text";
	//待分享的图片路径
	private String imagePath;
	//带分享的文件路径
	private String filePath;
	//待分享的标题
	private String title = "default title";
	//标题超链接
	private String titleUrl;
	//网络图片
	private String imageUrl;
	//对分享内容的评价
	private String comment;
	//发布分享的网站名称
	private String site;
	//发布分享的网站url
	private String siteUrl;
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
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTitleUrl() {
		return titleUrl;
	}
	public void setTitleUrl(String titleUrl) {
		this.titleUrl = titleUrl;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	public String getSiteUrl() {
		return siteUrl;
	}
	public void setSiteUrl(String siteUrl) {
		this.siteUrl = siteUrl;
	}
	
}
