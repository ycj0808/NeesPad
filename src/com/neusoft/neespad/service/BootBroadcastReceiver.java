package com.neusoft.neespad.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver {

	private static final String ACTION="android.intent.action.BOOT_COMPLETED";
	private static final String SIGN_ACTION="nees.sign";//签名
	private static final String TAKE_PHOTO="nees.takePhoto";//照相
	private static final String TAKE_PHOTO_COMPLETED="nees.takePhotoCompleted";//照相完成
	private static final String LOOK_PROTOCAL="nees.look_protocal";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		//通知完成
		if(intent.getAction().equals(ACTION)){
			
		}
		//通知签名 
		if(intent.getAction().equals(SIGN_ACTION)){
			
		}
		
		//照相
		if(intent.getAction().equals(TAKE_PHOTO)){
			
		}
		//照相完成
		if(intent.getAction().equals(TAKE_PHOTO_COMPLETED)){
			
		}
		//查看协议
		if(intent.getAction().equals(LOOK_PROTOCAL)){
			
		}
	}

}
