package com.neusoft.neespad.activity;

import java.util.Map;
import com.neusoft.neespad.common.MyApplication;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.webkit.WebView;

public class BrowseProtocalActivity extends Activity {

	private WebView webView;
	private Context mContext;
	private MyApplication app;
	private static Map<String, Object> dataMap;
	private String url="file:///android_asset/report.html";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_protocal);
		initView();
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	private void initView(){
		mContext=this;
		app=(MyApplication) getApplication();
		dataMap=app.getMap();
		webView=(WebView) findViewById(R.id.webView);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setSupportZoom(true);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.loadUrl(url);
	}
}
