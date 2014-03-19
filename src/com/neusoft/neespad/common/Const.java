package com.neusoft.neespad.common;

import android.os.Environment;

public class Const {
	
	//应用相册目录
	public static String imgPath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/nees";
	//应用相册临时目录
	public static String tempPath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/nees/temp";
	//获取系统目录
	public static String commPath=Environment.getExternalStorageDirectory().getAbsolutePath();
}
