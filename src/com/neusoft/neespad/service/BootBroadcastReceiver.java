package com.neusoft.neespad.service;
import com.neusoft.neespad.activity.BrowseProtocalActivity;
import com.neusoft.neespad.activity.MainActivity;
import com.neusoft.neespad.activity.SignActivity;
import com.neusoft.neespad.activity.SignCustomActivity;
import com.neusoft.neespad.activity.TakeBusyCardActivity;
import com.neusoft.neespad.activity.TakeCardActivity;
import com.neusoft.neespad.activity.TakePhotoActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;

public class BootBroadcastReceiver extends BroadcastReceiver {

	private static final String ACTION = "android.intent.action.BOOT_COMPLETED";
	private static final String SIGN_ACTION = "nees.sign";// 签名
	private static final String TAKE_PHOTO_START = "nees.takePhoto_start";// 照相
	private static final String TAKE_PHOTO_PROCESSING = "nees.takePhoto_processing";// 照相
	private static final String TAKE_PHOTO_COMPLETED = "nees.takePhotoCompleted";// 照相完成
	private static final String LOOK_PROTOCAL = "nees.look_protocal";
    private static final String BACK_HOME="nees.back_home";
    private static final String PHOTO_BIG_SURFACE="nees.big_surface";
    private static final String PHOTO_SMALL_SURFACE="nees.small_surface";
    private static final String PHOTO_BIG_PROCESSING="nees.take_big_photo_processing";//照证件照
    private static final String PHOTO_SMALL_PROCESSING="nees.take_small_photo_processing";//照证件照
    
    private static final String LOOK_REPORT="nees.look_report";
	@Override
	public void onReceive(final Context context, Intent intent) {
		// 通知完成
		if (intent.getAction().equals(ACTION)) {
			Intent bootStart = new Intent(context, MainActivity.class);
			bootStart.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(bootStart);
		}
		// 通知签名
		if (intent.getAction().equals(SIGN_ACTION)) {
			Intent bootStart = new Intent(context, SignCustomActivity.class);
			bootStart.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(bootStart);
		}

		// 照相
		if (intent.getAction().equals(TAKE_PHOTO_START)) {
			Intent bootStart = new Intent(context, TakePhotoActivity.class);
			bootStart.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(bootStart);
		}

		// 照相进行中
		if (intent.getAction().equals(TAKE_PHOTO_PROCESSING)) {
			Camera camera = TakePhotoActivity.getCamera();
			if(camera!=null){
				camera.autoFocus(TakePhotoActivity.autoCallBack);
			}
		}
		
		if(intent.getAction().equals(PHOTO_BIG_PROCESSING)){
			Camera camera =TakeBusyCardActivity.getCamera();
			if(camera!=null){
				camera.autoFocus(TakeBusyCardActivity.autoCallBack);
			}
		}
		if(intent.getAction().equals(PHOTO_SMALL_PROCESSING)){
			Camera camera =TakeCardActivity.getCamera();
			if(camera!=null){
				camera.autoFocus(TakeCardActivity.autoCallBack);
			}
		}
		
		// 照相完成
		if (intent.getAction().equals(TAKE_PHOTO_COMPLETED)) {
			Intent bootStart = new Intent(context, MainActivity.class);
			bootStart.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(bootStart);
		}
		// 查看协议
		if (intent.getAction().equals(LOOK_PROTOCAL)) {
			Intent bootStart = new Intent(context, BrowseProtocalActivity.class);
			bootStart.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(bootStart);
		}
		
		if(intent.getAction().equals(BACK_HOME)){
			Intent bootStart = new Intent(context, MainActivity.class);
			bootStart.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(bootStart);
		}
		
		if(intent.getAction().equals(PHOTO_BIG_SURFACE)){
			Intent bootStart = new Intent(context, TakeBusyCardActivity.class);
			bootStart.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(bootStart);
		}
		
		if(intent.getAction().equals(PHOTO_SMALL_SURFACE)){
			Intent bootStart = new Intent(context, TakeCardActivity.class);
			bootStart.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(bootStart);
		}
		/**
		 * 新主页--受理单,签名
		 */
		if(intent.getAction().equals(LOOK_REPORT)){
			Intent bootStart = new Intent(context,SignCustomActivity.class);
			bootStart.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(bootStart);
		}
	}
}
