package com.neusoft.neespad.activity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import com.neusoft.neespad.R;
import com.neusoft.neespad.common.Const;
import com.neusoft.neespad.common.MyApplication;
import com.neusoft.neespad.common.Util;
import com.neusoft.neespad.view.DrawImageView;
import com.neusoft.neespad.view.Preview;
import com.neusoft.neespad.view.VerticalSeekBar;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
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
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class TakeBusyLicActivity extends Activity {

	private static Camera camera;
	private SurfaceHolder mHolder;
	private static Context mContext;
	private static Preview preview;
	private FrameLayout frameLayout;
	private SurfaceView surfaceView;
	private SurfaceHolder mySurfaceHolder = null;
	private Button btn_camera;
	private static final String TAG = "AutoJustActivity";
	private static String fileName;
	private MyApplication app;
	private static Map<String, Object> dataMap;
	private static DrawImageView drawIV;
	private VerticalSeekBar seekBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.layout_id_card_busy_lic);
		initView();
		setListener();
	}

	/**
	 * 初始化元素控件 initView(这里用一句话描述这个方法的作用)
	 * 
	 * @Title: initView
	 * @Description: TODO
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void initView() {
		mContext=this;
		app = (MyApplication) getApplication();
		dataMap = app.getMap();
		surfaceView = (SurfaceView) findViewById(R.id.my_surfaceView);
		drawIV = (DrawImageView) findViewById(R.id.draw_IV);
		surfaceView.setZOrderOnTop(false);
		preview = new Preview(this, surfaceView);
		preview.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		frameLayout = (FrameLayout) findViewById(R.id.preview);
		frameLayout.addView(preview);
		preview.setKeepScreenOn(true);
		setDraw(100,50);
		btn_camera = (Button) findViewById(R.id.btn_camera);
		seekBar=(VerticalSeekBar) findViewById(R.id.seekBar);
	}

	@SuppressLint("WrongCall")
	private void setDraw(int width,int height){
		drawIV.setBeginLeft(width);
		drawIV.setBeginTop(height);
		drawIV.setOtherLeft(100);
		drawIV.onDraw(new Canvas());
	}
	/**
	 * 设置监听事件 setListener(这里用一句话描述这个方法的作用)
	 * @Title: setListener
	 * @Description: TODO
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void setListener() {
		btn_camera.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				camera.autoFocus(new AutoFocusCallback() {
					@Override
					public void onAutoFocus(boolean flag, Camera camera) {
						camera.takePicture(shutterCallback, rawCallback,jpegCallback);
					}
				});

			}
		});
		//SeekBar监听事件
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
				setZoomIn(progress);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (camera == null) {
			camera = Camera.open();
			Parameters params = camera.getParameters();
			final int MAX =params.getMaxZoom();
			Log.i("TAG", MAX+"");
			int zoomValue = params.getZoom();
			seekBar.setMax(MAX);
			seekBar.setProgress(zoomValue);
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
			String str="";
			String filePathString="";
			if (data != null) {
				filePathString=saveIDCard(data,600);
				File file=new File(filePathString);
				if(file!=null&&file.exists()){
					try {
						str=sendPhoto(file);
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
			resetCam();
			Log.d(TAG, "onPictureTaken - jpeg");
		}
	};

	/**
	 * 保存身份证照片 saveIDCard
	 * @Title: saveIDCard
	 * @Description: TODO
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private static String saveIDCard(byte[] data,int scaleWidth) {
		String filePath = null;
		Bitmap mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// data是字节数据，将其解析成位图
		Bitmap sizeBitmap = Bitmap.createScaledBitmap(mBitmap,
				preview.getPreviewSize().width,
				preview.getPreviewSize().height, true);
		int left = drawIV.getBeginLeft();
		int top = drawIV.getBeginTop();
		int right = drawIV.getEndRight();
		int bottom = drawIV.getEndBottom();
		float rate=(float) ((sizeBitmap.getWidth()*1.0)/(drawIV.getScrWidth()*1.0));//比例
		float hRate=(float) ((sizeBitmap.getHeight()*1.0)/(drawIV.getScrHeight()*1.0));//纵向比例
		Bitmap rectBitmap = Bitmap.createBitmap(sizeBitmap, left, top,(int)((right-left)*rate),
				(int)((bottom-top)*hRate));
		if (rectBitmap != null) {
			float scaleRate=(float) ((bottom-top)*1.0/(right-left)*1.0);
			int scaleHeight=(int)(scaleRate*scaleWidth);
			Bitmap scaleBitmap=Bitmap.createScaledBitmap(rectBitmap, scaleWidth,scaleHeight,true);
			filePath=saveBitmapToPicture(scaleBitmap);
		}
		return filePath;
	}
	/**
	 * 把bitmap保存为图片 saveBitmapToPicture(这里用一句话描述这个方法的作用)
	 * @Title: saveBitmapToPicture
	 * @Description: TODO
	 * @param @param bitmap 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private static String saveBitmapToPicture(Bitmap bitmap) {
		FileOutputStream outStream = null;
		try {
			File fileDir = new File(Const.tempPath);
			if (!fileDir.exists()) {
				fileDir.mkdirs();
			}
			fileName = String.format(Const.tempPath + "/%d.jpg",
					System.currentTimeMillis());
			outStream = new FileOutputStream(fileName);
			BufferedOutputStream bos = new BufferedOutputStream(outStream);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
			Log.i("TakeIDCardActivity", "保存图片成功");
		} catch (Exception e) {
			Log.i("TakeIDCardActivity", "保存图片失败");
			fileName="";
			e.printStackTrace();
		}
		return fileName;
	}

	/**
	  * 镜头缩放
	  * setZoomIn(这里用一句话描述这个方法的作用)
	  * @Title: setZoomIn
	  * @Description: TODO
	  * @param @param zoomValue    设定文件
	  * @return void    返回类型
	  * @throws
	  */
	public static void setZoomIn(int zoomValue) {
		if (isSupportZoom()) {
			try {
				Parameters params = camera.getParameters();
				final int MAX = params.getMaxZoom();
				if(MAX!=0){
					params.setZoom(zoomValue);
					camera.setParameters(params);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	  * 判断是否缩放
	  * isSupportZoom(这里用一句话描述这个方法的作用)
	  * @Title: isSupportZoom
	  * @Description: TODO
	  * @param @return    设定文件
	  * @return boolean    返回类型
	  * @throws
	  */
	public static boolean isSupportZoom() {
		boolean isSupport = true;
		if (camera.getParameters().isSmoothZoomSupported()) {
			isSupport = false;
		}
		return isSupport;
	}
	
	/**
	  * 发送图片
	  * sendPhoto
	  * @Title: sendPhoto
	  * @Description: TODO
	  * @param @param file
	  * @param @throws Exception    设定文件
	  * @return String    返回类型
	  * @throws
	  */
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
	
	/**
	  * 获取相机
	  * getCamera(这里用一句话描述这个方法的作用)
	  * @Title: getCamera
	  * @Description: TODO
	  * @param @return    设定文件
	  * @return Camera    返回类型
	  * @throws
	  */
	public static Camera getCamera() {
		return camera;
	}
}
