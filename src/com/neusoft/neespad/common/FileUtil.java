package com.neusoft.neespad.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class FileUtil {
	// private static final String
	// path=Environment.getExternalStorageDirectory().getAbsolutePath();
	public static void newFolder(String folderPath) {
		try {
			String filePath = Const.commPath + "/" + folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			if (!myFilePath.exists()) {
				myFilePath.mkdirs();
			}
		} catch (Exception e) {
			System.out.println("新建目录操作出错");
			e.printStackTrace();
		}
	}

	static char[] hex = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'A', 'B', 'C', 'D', 'E', 'F' };

	public static String getbyte2str(byte[] bytes) {
		int len = bytes.length;
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < len; i++) {
			byte byte0 = bytes[i];
			result.append(hex[byte0 >>> 4 & 0xf]);
			result.append(hex[byte0 & 0xf]);
		}
		return result.toString();
	}

	public static String getMd5(byte[] bytes) throws NoSuchAlgorithmException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");// 申明使用MD5算法
		md5.update(bytes);
		return getbyte2str(md5.digest());
	}

	//
	public static byte[] getPdfByte(String path) throws IOException {

		File file = new File(path);
		FileInputStream fis = new FileInputStream(file);
		int fileLength = (int) file.length();
		byte[] fileBytes = new byte[fileLength];
		fis.read(fileBytes);
		fis.close();
		return fileBytes;
	}

	// md5 校验返回值
	public static String getMd5Value(String path) throws Exception, IOException {

		return getMd5(getPdfByte(path));
	}

	public static String[] getData(String path) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(path));
		StringBuffer str = new StringBuffer("");
		String temp = "";
		while ((temp = reader.readLine()) != null) {
			str.append(temp).append(",");
		}
		if (!"".equals(temp)) {
			temp = str.toString();
			return temp.split(",");
		} else {
			return null;
		}
	}

	/**
	 * 判断是否插入内存卡
	 * 
	 * @return
	 */
	public static boolean isHasSdcard() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 读取文件内容 ReadTxtFile
	 * 
	 * @Title: ReadTxtFile
	 * @Description: TODO
	 * @param @param strFilePath
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	public static String ReadTxtFile(String strFilePath) {
		String path = strFilePath;
		String content = ""; // 文件内容字符串
		// 打开文件
		File file = new File(path);
		// 如果path是传递过来的参数，可以做一个非目录的判断
		if (file.isDirectory()) {
			Log.d("TestFile", "The File doesn't not exist.");
		} else {
			try {
				InputStream instream = new FileInputStream(file);
				if (instream != null) {
					InputStreamReader inputreader = new InputStreamReader(
							instream);
					BufferedReader buffreader = new BufferedReader(inputreader);
					String line;
					// 分行读取
					while ((line = buffreader.readLine()) != null) {
						content += line + "\n";
					}
					instream.close();
				}
			} catch (java.io.FileNotFoundException e) {
				Log.d("TestFile", "The File doesn't not exist.");
			} catch (IOException e) {
				Log.d("TestFile", e.getMessage());
			}
		}
		return content;
	}

	/**
	 * 写文件 writeFileData
	 * 
	 * @Title: writeFileData
	 * @Description: TODO
	 * @param @param fileName
	 * @param @param message 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public static void writeFileData(String fileName, String message) {
		try {
			FileOutputStream fout = new FileOutputStream(fileName, false);
			byte[] bytes = message.getBytes();
			fout.write(bytes);
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 复制中的文件到sdcard copyFileToAsset
	 * 
	 * @Title: copyFileToAsset
	 * @Description: TODO
	 * @param @param filePath 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public static void copyFileToAsset(Context context, String fileName,
			String to_fileDir) {
		File outFile = new File(to_fileDir, fileName);
		InputStream in = null;
		OutputStream out = null;
		if (!outFile.exists()) {
			try {
				in = context.getAssets().open(fileName);
				out = new FileOutputStream(outFile);
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					in.close();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
