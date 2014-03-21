package com.neusoft.neespad.activity;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.example.drawdemo.interfaces.PaintViewCallBack;
import com.example.drawdemo.utils.BitMapUtils;
import com.example.drawdemo.view.PaintView;
import com.neusoft.neespad.common.Const;
import com.neusoft.neespad.common.MyApplication;
import com.neusoft.neespad.common.Util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

public class SignActivity extends Activity {

	private WebView webView;
	private Button btn_sign;
	private PopupWindow popWindow;
	private Context mContext;
	private Button btn_save;
	private Button btn_cancel;
	private LinearLayout layout;
	private PaintView mPaintView;
	private MyApplication app;
	private static Map<String, Object> dataMap;
	private String url="file:///android_asset/report.html";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_sign);
		initView();
	}
	
	
	@SuppressLint("SetJavaScriptEnabled")
	private void initView(){
		mContext=this;
		app=(MyApplication) getApplication();
		dataMap=app.getMap();
		webView=(WebView) findViewById(R.id.webView);
		btn_sign=(Button) findViewById(R.id.btn_sign);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setSupportZoom(true);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.loadUrl(url);
		btn_sign.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LayoutInflater layoutInflater = (LayoutInflater)(SignActivity.this).getSystemService(LAYOUT_INFLATER_SERVICE); 
				 // ��ȡ�Զ��岼���ļ�poplayout.xml����ͼ  
	            View popview = layoutInflater.inflate(R.layout.poplayout, null);  
	            popWindow = new PopupWindow(popview,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);  
	            layout=(LinearLayout) popview.findViewById(R.id.layout_draw);
	            mPaintView = new PaintView(mContext);
	            layout.removeAllViews();
	            layout.addView(mPaintView);
	            initCallBack();
	            //�涨������λ��  
	            popWindow.showAtLocation(findViewById(R.id.main), Gravity.CENTER,  0, 0);  
	            btn_save=(Button) popview.findViewById(R.id.btn_save);
	            btn_cancel=(Button) popview.findViewById(R.id.btn_cancel);
	            btn_save.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						String fileName=Const.tempPath+"/"+System.currentTimeMillis()+".png";
						 String str="";
					    try {
					    	str=savePhoto(fileName);
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
						Toast.makeText(mContext, "ǩ�����", Toast.LENGTH_LONG).show();
						popWindow.dismiss();
					}
				});
	            btn_cancel.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						mPaintView.clearAll();
						mPaintView.resetState();
					}
				});
			}
		});
	}
	
	/**
	 * ��ʼ��paintView�Ļص�����
	 */
	private void initCallBack() {
		mPaintView.setCallBack(new PaintViewCallBack() {
			// ������֮���Button���и���
			public void onHasDraw() {
			}
			// �����֮���ø��������Ĵ��ڶ���ʧ
			public void onTouchDown() {
			}
		});
	}
	/**
	 * @throws Exception 
	  * ����ͼƬ
	  * savePhoto(������һ�仰�����������������)
	  * @Title: savePhoto
	  * @Description: TODO
	  * @param @param fileName    �趨�ļ�
	  * @return void    ��������
	  * @throws
	 */
	private String savePhoto(String fileName) throws Exception{
		String md5Str = "";
		String strPathDetail = "";
		String flag = "";
		String message = "";
		String retStr = "";
		Bitmap bitmap = mPaintView.getSnapShoot();
		BitMapUtils.saveToSdCard(fileName, bitmap);
		File file=new File(fileName);
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
