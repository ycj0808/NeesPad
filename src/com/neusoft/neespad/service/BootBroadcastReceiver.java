package com.neusoft.neespad.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver {

	private static final String ACTION="android.intent.action.BOOT_COMPLETED";
	private static final String SIGN_ACTION="nees.sign";//ǩ��
	private static final String TAKE_PHOTO="nees.takePhoto";//����
	private static final String TAKE_PHOTO_COMPLETED="nees.takePhotoCompleted";//�������
	private static final String LOOK_PROTOCAL="nees.look_protocal";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		//֪ͨ���
		if(intent.getAction().equals(ACTION)){
			
		}
		//֪ͨǩ�� 
		if(intent.getAction().equals(SIGN_ACTION)){
			
		}
		
		//����
		if(intent.getAction().equals(TAKE_PHOTO)){
			
		}
		//�������
		if(intent.getAction().equals(TAKE_PHOTO_COMPLETED)){
			
		}
		//�鿴Э��
		if(intent.getAction().equals(LOOK_PROTOCAL)){
			
		}
	}

}
