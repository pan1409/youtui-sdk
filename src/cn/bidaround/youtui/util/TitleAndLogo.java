package cn.bidaround.youtui.util;

public class TitleAndLogo {
		private int id;
		private String title;
		private int LogoSrc;
		public TitleAndLogo(int id,String title,int srcId){
			this.id = id;
			this.title = title;
			this.LogoSrc = srcId;
		}
		public int getId(){
			return id;
		}
		public String getTitle() {
			return title;
		}
		public int getLogoSrc() {
			return LogoSrc;
		}
}
