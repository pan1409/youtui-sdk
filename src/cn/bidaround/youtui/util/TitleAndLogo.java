package cn.bidaround.youtui.util;

public class TitleAndLogo {
		private String title;
		private int LogoSrc;
		public TitleAndLogo(String title,int srcId){
			this.title = title;
			this.LogoSrc = srcId;
		}
		
		public String getTitle() {
			return title;
		}
		public int getLogoSrc() {
			return LogoSrc;
		}		
}
