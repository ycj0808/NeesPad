package com.neusoft.neespad.activity;

import java.util.Map;

import com.neusoft.neespad.R;
import com.neusoft.neespad.common.Const;
import com.neusoft.neespad.common.FileUtil;
import com.neusoft.neespad.common.MyApplication;
import com.neusoft.neespad.view.WebViewCopy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnLongClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class BrowseProtocalActivity extends Activity {

	private WebView webView;
	private WebSettings webSettings;
	private Context mContext;
	private MyApplication app;
	private static Map<String, Object> dataMap;
	private String url = "file:///android_asset/report.html";
	private String sdCardPath = "file:///mnt/sdcard/neesPad/temp/";
	private String fileName = "report.html";
	private String filePathString = Const.tempPath;
	private WebViewCopy copy;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_protocal);
		initView();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initView() {
		mContext = this;
		app = (MyApplication) getApplication();
		dataMap = app.getMap();
		webView = (WebView) findViewById(R.id.webView);
		copy = new WebViewCopy(this, webView);
		webSettings = webView.getSettings();
		// 打开页面时， 自适应屏幕
		// webSettings.setUseWideViewPort(true);
		// webSettings.setLoadWithOverviewMode(true);

		// java代码可调用javascript代码
		webSettings.setJavaScriptEnabled(true);
		// 使页面支持缩放
		webSettings.setSupportZoom(true);
		webSettings.setBuiltInZoomControls(true);
		// webView.loadUrl(url);
		new Thread(new Runnable() {
			@Override
			public void run() {
				boolean flag = FileUtil.copyFileFromAssetToSdCard(mContext,
						fileName, filePathString);
				if (flag) {
					webView.loadUrl(sdCardPath + fileName);
				}
			}
		}).start();
		setListener();
		
	}

	private void setListener() {
		//取消长按事件
		webView.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				return false;
			}
		});
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		copy.onCreateContextMenu(menu, v, menuInfo,0,"复制"); 
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@SuppressWarnings("deprecation")
	private void enterTextSelection() {
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1)
			return;
		try {
			WebView.class.getMethod("selectText").invoke(this);
		} catch (Exception e) {
			try {
				WebView.class.getMethod("emulateShiftHeld").invoke(this);
			} catch (Exception e1) {
				KeyEvent shiftPressEvent = new KeyEvent(0, 0,
						KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SHIFT_LEFT, 0, 0);
				shiftPressEvent.dispatch(webView);
			}
		}
	}
}
