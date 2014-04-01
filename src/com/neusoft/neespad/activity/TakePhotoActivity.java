package com.neusoft.neespad.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import com.neusoft.neespad.common.Util;
import com.neusoft.neespad.common.Const;
import com.neusoft.neespad.common.MyApplication;
import com.neusoft.neespad.view.Preview;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

public class TakePhotoActivity extends Activity {
	private static final String TAG = "TakePhotoActivity";
	private static Preview preview;
	private static Camera camera;
	private static Context mContext;
	private Activity act;
	private MyApplication app;
	private static Map<String, Object> dataMap;
	private static String fileName;
	private LinearLayout layout;
	private Button btn_camera;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.layout_take_photo);
		init();
	}

	private void init() {
		act = this;
		mContext = this;
		app = (MyApplication) getApplication();
		dataMap = app.getMap();
		Log.i("TAG", dataMap.toString());
		preview = new Preview(this,
				(SurfaceView) findViewById(R.id.surfaceView));
//		preview.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
//				LayoutParams.MATCH_PARENT));
		((FrameLayout) findViewById(R.id.preview)).addView(preview);
		preview.setKeepScreenOn(true);
		btn_camera = (Button) findViewById(R.id.btn_camera);
		btn_camera.setOnClickListener(clickListener);
	}

	// 照相按钮监听事件
	private OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			camera.autoFocus(autoCallBack);
			//setZoomIn();
		}
	};

	// 自动对焦
	public static AutoFocusCallback autoCallBack = new AutoFocusCallback() {
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			camera.takePicture(shutterCallback, rawCallback, jpegCallback);
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		camera = Camera.open();
		camera.startPreview();
		preview.setCamera(camera);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (camera != null) {
			camera.stopPreview();
			preview.setCamera(null);
			camera.release();
			camera = null;
		}
	}

	public static void setZoomIn() {
		if (isSupportZoom()) {
			try {
				Parameters params = camera.getParameters();
				final int MAX = params.getMaxZoom();
				if(MAX!=0){
					int zoomValue = params.getZoom();
					zoomValue += 5;
					params.setZoom(zoomValue);
					camera.setParameters(params);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean isSupportZoom() {
		boolean isSupport = true;
		if (camera.getParameters().isSmoothZoomSupported()) {
			isSupport = false;
		}
		return isSupport;
	}

	private static void resetCam() {
		camera.startPreview();
		preview.setCamera(camera);
	}

	public static ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			Log.d(TAG, "onShutter'd");
		}
	};

	public static PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.d(TAG, "onPictureTaken - raw");
		}
	};

	public static PictureCallback jpegCallback = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			FileOutputStream outStream = null;
			String str = "";
			try {
				// Write to SD Card
				File fileDir = new File(Const.tempPath);
				if (!fileDir.exists()) {
					fileDir.mkdirs();
				}
				fileName = String.format(Const.tempPath + "/%d.jpg",
						System.currentTimeMillis());
				outStream = new FileOutputStream(fileName);
				outStream.write(data);
				outStream.close();
				Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);
				resetCam();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				File file = new File(fileName);
				if (file.exists()) {
					try {
						str = sendPhoto(file);
					} catch (Exception e) {
						e.printStackTrace();
					}
					String mess[] = str.split("~");
					String flag = mess[0];
					String message = mess[1];
					String[] md5Str = { mess[2] };
					String filePath = mess[3].substring(1, mess[3].length());
					String[] pathStr = { filePath };
					dataMap.put("MD5Str", md5Str);
					dataMap.put("FilePath", pathStr);
					dataMap.put("Data", "");
					dataMap.put("flag", "send");
					dataMap.put("BoardcastType", "nees.autoTakePhoto");
					Toast.makeText(mContext, message, Toast.LENGTH_SHORT)
							.show();
				}
			}
			Log.d(TAG, "onPictureTaken - jpeg");
		}
	};

	public static String sendPhoto(File file) throws Exception {
		String md5Str = "";
		String strPathDetail = "";
		String flag = "";
		String message = "";
		String retStr = "";
		if (file.exists()) {
			strPathDetail = file.getPath();
			try {
				md5Str = Util.getMd5Value(strPathDetail);
				flag = "ok";
				message = "受理成功！";
			} catch (IOException e) {
				flag = "no";
				message = "受理失败！";
				e.printStackTrace();
			}
		} else {
			flag = "no";
			message = "受理失败！";
		}
		retStr = flag + "~" + message + "~" + md5Str + "~" + strPathDetail;
		return retStr;
	}

	public static Camera getCamera() {
		return camera;
	}
}
