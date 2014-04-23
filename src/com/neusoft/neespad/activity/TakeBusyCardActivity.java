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
import com.neusoft.neespad.view.Preview;
import com.neusoft.neespad.view.SVDraw;
import com.neusoft.neespad.view.VerticalSeekBar;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class TakeBusyCardActivity extends Activity {

	private FrameLayout frameLayout;
	private SurfaceView surfaceView;
	private static Preview preview;
	private static SVDraw svDraw;
	private static final String TAG = "AutoJustActivity";
	private static Camera camera;
	private static int margin_left = 120;
	private static int margin_right = 120;
	private static int margin_top = 70;
	private static int margin_bottom = 70;
	private Button btn_camera;
	private MyApplication app;
	private static Map<String, Object> dataMap;
	private VerticalSeekBar seekBar;
	private static Context mContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.layout_draw_rect);
		initView();
		setListener();
	}

	/**
	 * ��ʼ��Ԫ�ؿؼ� initView
	 * @Title: initView
	 * @Description: TODO
	 * @param �趨�ļ�
	 * @return void ��������
	 * @throws
	 */
	private void initView() {
		app = (MyApplication) getApplication();
		dataMap = app.getMap();
		mContext=this;
		frameLayout = (FrameLayout) findViewById(R.id.preview);
		surfaceView = (SurfaceView) findViewById(R.id.my_surfaceView);
		svDraw = (SVDraw) findViewById(R.id.svDraw);
		surfaceView.setZOrderOnTop(false);
		preview = new Preview(this, surfaceView);
		preview.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		frameLayout.addView(preview);
		preview.setKeepScreenOn(true);
		svDraw.setVisibility(View.VISIBLE);
		setMarginParams();
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.setMargins(margin_left, margin_top, margin_right, margin_bottom);
		svDraw.setLayoutParams(lp);
		btn_camera=(Button) findViewById(R.id.btn_camera);
		seekBar=(VerticalSeekBar) findViewById(R.id.seekBar);
	}
	
	/**
	  * ���ü����¼�
	  * setListener(������һ�仰�����������������)
	  * @Title: setListener
	  * @Description: TODO
	  * @param          �趨�ļ�
	  * @return void    ��������
	  * @throws
	  */
	private void setListener(){
		//����
		btn_camera.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				camera.autoFocus(new AutoFocusCallback() {
					@Override
					public void onAutoFocus(boolean success, Camera camera) {
						camera.takePicture(shutterCallback, rawCallback, jpegCallback);
					}
				});
			}
		});
		//�������� 
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

	// �Զ��Խ�
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
			String  filePathString="";
			if (data != null) {
				filePathString=saveImage(data);
				try {
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
						Toast.makeText(mContext, "���ճɹ�", Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					Log.e(TAG, e.getMessage());
					
				}
			}
			resetCam();
		}
	};

	/**
	 * ����ͼƬ saveImage(������һ�仰�����������������)
	 * 
	 * @Title: saveImage
	 * @Description: TODO
	 * @param @param data
	 * @param @return �趨�ļ�
	 * @return String ��������
	 * @throws
	 */
	private static String saveImage(byte[] data) {
		String filePath = "";
		Bitmap mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// data���ֽ����ݣ����������λͼ
		Bitmap sizeBitmap = Bitmap.createScaledBitmap(mBitmap,
				preview.getPreviewSize().width,
				preview.getPreviewSize().height, true);

		Bitmap rectBitmap = Bitmap.createBitmap(sizeBitmap, margin_left,
				margin_top, svDraw.getWidth(), svDraw.getHeight());
		if (rectBitmap != null) {
			filePath=saveBitmapToPicture(rectBitmap);
		}
		return filePath;
	}
	
	/**
	 * ��bitmap����ΪͼƬ saveBitmapToPicture(������һ�仰�����������������)
	 * @Title: saveBitmapToPicture
	 * @Description: TODO
	 * @param @param bitmap �趨�ļ�
	 * @return void ��������
	 * @throws
	 */
	private static String saveBitmapToPicture(Bitmap bitmap) {
		String fileName="";
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
			Log.i("TakeBusyCardActivity", "����ͼƬ�ɹ�");
		} catch (Exception e) {
			Log.i("TakeBusyCardActivity", "����ͼƬʧ��");
			fileName="";
			e.printStackTrace();
		}
		return fileName;
	}

	/**
	  * ����margin ����
	  * setMarginParams(������һ�仰�����������������)
	  * @Title: setMarginParams
	  * @Description: TODO
	  * @param     �趨�ļ�
	  * @return void    ��������
	  * @throws
	  */
	private void setMarginParams(){
		int size[]=Util.getScreenSize(TakeBusyCardActivity.this);
		if(size[0]<900){
			margin_left=margin_right=100;
			margin_top=margin_bottom=50;
		}
	}
	
	/**
	  * ��ȡ���
	  * getCamera(������һ�仰�����������������)
	  * @Title: getCamera
	  * @Description: TODO
	  * @param @return    �趨�ļ�
	  * @return Camera    ��������
	  * @throws
	  */
	public static Camera getCamera() {
		return camera;
	}
	
	/**
	  * ��������
	  * setZoomIn
	  * @Title: setZoomIn
	  * @Description: TODO
	  * @param @param zoomValue    �趨�ļ�
	  * @return void    ��������
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
	  * �ж��Ƿ�֧������
	  * isSupportZoom
	  * @Title: isSupportZoom
	  * @Description: TODO
	  * @param @return     �趨�ļ�
	  * @return boolean    ��������
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
	  * ����ͼƬ
	  * sendPhoto
	  * @Title: sendPhoto
	  * @Description: TODO
	  * @param @param file
	  * @param @throws Exception    �趨�ļ�
	  * @return String    ��������
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
				message = "����ɹ���";
			} catch (IOException e) {
				flag = "no";
				message = "����ʧ�ܣ�";
				e.printStackTrace();
			}
		} else {
			flag = "no";
			message = "����ʧ�ܣ�";
		}
		retStr = flag + "~" + message + "~" + md5Str + "~" + strPathDetail;
		return retStr;
	}
}
