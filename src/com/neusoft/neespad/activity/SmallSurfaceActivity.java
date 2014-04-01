package com.neusoft.neespad.activity;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.neusoft.neespad.common.Const;
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

public class SmallSurfaceActivity extends Activity {

	private static Camera camera;
	private SurfaceHolder mHolder;
	private Context mContext;
	private static Preview preview;
	private FrameLayout frameLayout;
	private SurfaceView surfaceView1;
	private SurfaceView surfaceView2;
	private Button btn_camera;
	private static final String TAG = "AutoJustActivity";
	private static String fileName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.layout_camera);
		surfaceView1=(SurfaceView) findViewById(R.id.surfaceView);
		surfaceView2=(SurfaceView) findViewById(R.id.surfaceView1);
		btn_camera=(Button) findViewById(R.id.btn_camera);
		mContext=this;
		Intent intent=getIntent();
		if(intent!=null){
			if(intent.getExtras()==null){
				surfaceView1.setVisibility(View.VISIBLE);
				surfaceView2.setVisibility(View.GONE);
				preview=new Preview(this,surfaceView1);
				preview.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
			}else{
				surfaceView2.setVisibility(View.VISIBLE);
				surfaceView1.setVisibility(View.GONE);
				preview=new Preview(this, surfaceView2);
				preview.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
			}
		}
		frameLayout=(FrameLayout) findViewById(R.id.preview);
		frameLayout.addView(preview);
		preview.setKeepScreenOn(true);
		btn_camera.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				camera.autoFocus(new AutoFocusCallback() {
					@Override
					public void onAutoFocus(boolean flag, Camera camera) {
						camera.takePicture(shutterCallback, rawCallback, jpegCallback);
					}
				});
			}
		});
	}
	
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
			} 
			Log.d(TAG, "onPictureTaken - jpeg");
		}
	};

}
