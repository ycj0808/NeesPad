package com.neusoft.neespad.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import com.neusoft.neespad.R;
import com.neusoft.neespad.common.Const;
import com.neusoft.neespad.common.MyApplication;
import com.neusoft.neespad.common.Util;
import com.neusoft.neespad.view.Preview;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

public class SmallSurfaceActivity extends Activity {

	private static Camera camera;
	private SurfaceHolder mHolder;
	private static Context mContext;
	private static Preview preview;
	private FrameLayout frameLayout;
	private SurfaceView surfaceView1;
	private SurfaceView surfaceView2;
	private Button btn_camera;
	private static final String TAG = "AutoJustActivity";
	private static String fileName;
	private MyApplication app;
	private static Map<String, Object> dataMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.layout_camera);
		surfaceView1 = (SurfaceView) findViewById(R.id.surfaceView);
		surfaceView2 = (SurfaceView) findViewById(R.id.surfaceView1);
		btn_camera = (Button) findViewById(R.id.btn_camera);
		app = (MyApplication) getApplication();
		dataMap = app.getMap();
		mContext = this;
		surfaceView1.setVisibility(View.VISIBLE);
		surfaceView2.setVisibility(View.GONE);
		preview = new Preview(this, surfaceView1);
		preview.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		/*Intent intent = getIntent();
		if (intent != null) {
			if (intent.getExtras() == null) {
				surfaceView1.setVisibility(View.VISIBLE);
				surfaceView2.setVisibility(View.GONE);
				preview = new Preview(this, surfaceView1);
				preview.setLayoutParams(new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			} else {
				surfaceView2.setVisibility(View.VISIBLE);
				surfaceView1.setVisibility(View.GONE);
				preview = new Preview(this, surfaceView2);
				preview.setLayoutParams(new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			}
		}*/
		frameLayout = (FrameLayout) findViewById(R.id.preview);
		frameLayout.addView(preview);
		preview.setKeepScreenOn(true);
		btn_camera.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				camera.autoFocus(new AutoFocusCallback() {
					@Override
					public void onAutoFocus(boolean flag, Camera camera) {
						camera.takePicture(shutterCallback, rawCallback,
								jpegCallback);
					}
				});
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(camera==null){
			camera = Camera.open();
		}
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

	private static void resetCam() {
		camera.startPreview();
		preview.setCamera(camera);
	}

	// 自动对焦
	public static AutoFocusCallback autoCallBack = new AutoFocusCallback() {
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			camera.takePicture(shutterCallback, rawCallback, jpegCallback);
		}
	};
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
					Toast.makeText(mContext, "拍照成功", Toast.LENGTH_SHORT).show();
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
