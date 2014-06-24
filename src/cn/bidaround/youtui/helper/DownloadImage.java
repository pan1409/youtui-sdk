package cn.bidaround.youtui.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class DownloadImage {
	private static final String TAG = "--DownloadImage--";
	/**
	 * 加载系统本地图片
	 */
	@SuppressWarnings("unused")
	public static Bitmap loadImage(final String url, final String filename) {
		try {
			FileInputStream fis = new FileInputStream(url + filename);
			if (fis != null) {
				return BitmapFactory.decodeStream(fis);
			} else {
				return null;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 下载文件功能
	 * @throws IOException 
	 */
	public static void down_file(String url, String path, String filename) throws IOException  {
		//Log.i(TAG, "BEGIN DOWN IMAGE");
		URL myURL = new URL(url);
		URLConnection conn = myURL.openConnection();
		conn.connect();
		InputStream is = conn.getInputStream();
		int fileSize = conn.getContentLength();// 根据响应获取文件大小
		if (fileSize <= 0)
			throw new RuntimeException("无法获知文件大小 ");
		if (is == null)
			throw new RuntimeException("stream is null");
		FileUtils util = new FileUtils();
/*		//如果文件已经存在
		if (util.isFileExist(path + filename)) {
			File file = new File(FileUtils.SDPATH+path + filename);
			InputStream fileIs = new FileInputStream(file);
			//判断下载的文件和存在的文件长度是否相同
			int isLen = is.available();
			int fileIsLen = fileIs.available();
			if(isLen==fileIsLen){
				Log.i(TAG, "IMAGE LENGTH IS SAME");
				//如果长度相同再判断内容是否相同，如果都相同则不需要下载，直接return
				byte[] isByte = new byte[isLen];
				byte[] fileIsByte = new byte[fileIsLen];
				is.read(isByte);
				fileIs.read(fileIsByte);
				if(FileUtils.isSame(isByte, fileIsByte)){
					Log.i(TAG, "IMAGE IS SAME");
					return ;
				}

			}
		}*/
		util.creatSDDir(path);
		File file = util.creatSDFile(path + filename);// 保存的文件名
		@SuppressWarnings("resource")
		FileOutputStream fos = new FileOutputStream(file);

		// 把数据存入路径+文件名
		byte buf[] = new byte[1024];
		do {
			// 循环读取
			int numread = is.read(buf);
			if (numread == -1) {
				break;
			}
			fos.write(buf, 0, numread);
		} while (true);		
			is.close();
			//Log.i(TAG, "END DOWN IMAGE");
	}
	
	
}
