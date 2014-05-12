package com.neusoft.neespad.activity;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.example.drawdemo.interfaces.PaintViewCallBack;
import com.example.drawdemo.utils.BitMapUtils;
import com.example.drawdemo.view.PaintView.OnPaintListener;
import com.neusoft.neespad.R;
import com.neusoft.neespad.common.Const;
import com.neusoft.neespad.common.MyApplication;
import com.neusoft.neespad.common.Util;
import com.neusoft.neespad.common.MyApplication.PaintNode;
import com.neusoft.neespad.view.CustomPaintView;
import com.neusoft.neespad.view.MyWebView;
import com.neusoft.neespad.view.MyWebView.ScrollChangedCallback;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebView.PictureListener;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

public class HotelReportActivity extends Activity implements
		ScrollChangedCallback {

	private MyWebView hotel_webView;
	private FrameLayout layout_middle_container;
	private LinearLayout layout_hotel_draw;
	private LinearLayout layout_foot_container;
	private Button btn_add;
	private Button btn_minus;
	private Button btn_next_page;
	private Button btn_last_page;
	private Button btn_save;
	private Button btn_clear;
	private Button btn_preview;
	private WebSettings settings;
	public String url = "file:///android_asset/report.html";
	private Context mContext;
	private int fontSize = 1;// WebView字体
	private CustomPaintView mPaintView;
	private MyApplication app;
	private boolean isVisible = false;
	private static Map<String, Object> dataMap;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_hotel_report);
		initView();
		setListener();
	}

	/**
	 * 元素控件初始化
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled")
	private void initView() {
		mContext = this;
		app = (MyApplication) getApplication();
		dataMap = app.getMap();
		hotel_webView = (MyWebView) findViewById(R.id.hotel_webView);
		hotel_webView.registerScrollChangedCallback(this);
		settings = hotel_webView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setBuiltInZoomControls(false);
		settings.setTextSize(WebSettings.TextSize.LARGER);
		hotel_webView.loadUrl(url);
		layout_middle_container = (FrameLayout) findViewById(R.id.layout_middle_container);
		layout_hotel_draw = (LinearLayout) findViewById(R.id.layout_hotel_draw);
		layout_foot_container = (LinearLayout) findViewById(R.id.layout_foot_container);
		btn_add = (Button) findViewById(R.id.btn_add);
		btn_minus = (Button) findViewById(R.id.btn_minus);
		btn_next_page = (Button) findViewById(R.id.btn_next_page);
		btn_last_page = (Button) findViewById(R.id.btn_last_page);
		btn_save = (Button) findViewById(R.id.btn_save);
		btn_clear = (Button) findViewById(R.id.btn_clear);
		btn_preview = (Button) findViewById(R.id.btn_preview);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	/**
	 * 设置监听事件
	 */
	@SuppressWarnings("deprecation")
	private void setListener() {
		hotel_webView.setPictureListener(new PictureListener() {
			@Override
			@Deprecated
			public void onNewPicture(WebView view, Picture picture) {
				int h = view.getContentHeight();
				if (h <= 980) {
					layout_hotel_draw.setVisibility(View.VISIBLE);
					// layout_hotel_draw.setVisibility(View.VISIBLE);
					btn_next_page.setEnabled(false);
					btn_last_page.setEnabled(false);
					showDrawPaintView();
				} else {
					if (!isVisible) {
						if (mPaintView != null) {
							app.clearPaintNode();
							mPaintView.clearAll();
							mPaintView.resetState();
						}
						layout_hotel_draw.setVisibility(View.GONE);
						btn_save.setEnabled(false);
						btn_preview.setEnabled(false);
						btn_clear.setEnabled(false);
					}else{
						btn_save.setEnabled(true);
						btn_preview.setEnabled(true);
						btn_clear.setEnabled(true);
					}
				}
			}
		});

		// 下一页
		btn_next_page.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final int currY = hotel_webView.getScrollY();
				final int height = hotel_webView.getContentHeight();
				int nextY = currY + 980;
				if (nextY < height) {
					if (nextY + 980 < height) {
						hotel_webView.setScrollY(nextY);
					} else {
						hotel_webView.setScrollY(height - 680);
						layout_hotel_draw.setVisibility(View.VISIBLE);
						showDrawPaintView();
						Toast.makeText(mContext, "已经达到底部", Toast.LENGTH_SHORT).show();
						isVisible=true;
					}
				} else {
					isVisible=true;
					hotel_webView.setScrollY(height - 680);
					layout_hotel_draw.setVisibility(View.VISIBLE);
					showDrawPaintView();
					Toast.makeText(mContext, "已经达到底部", Toast.LENGTH_SHORT).show();
				}
			}
		});
		// 上一页
		btn_last_page.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int currY = hotel_webView.getScrollY();
				int lastY = currY - 980;
				if (lastY > 0) {
					hotel_webView.setScrollY(lastY);
				} else {
					hotel_webView.setScrollY(0);
					Toast.makeText(mContext, "已经达到顶部", Toast.LENGTH_SHORT).show();
				}
				if (mPaintView != null) {
					app.clearPaintNode();
					mPaintView.clearAll();
					mPaintView.resetState();
				}
			}
		});

		btn_add.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				fontSize++;
				if (fontSize > 5) {
					fontSize = 5;
					Toast.makeText(mContext, "字体已经最大了", Toast.LENGTH_SHORT)
							.show();
				}
				switch (fontSize) {
				case 1:
					settings.setTextSize(WebSettings.TextSize.SMALLEST);
					break;
				case 2:
					settings.setTextSize(WebSettings.TextSize.SMALLER);
					break;
				case 3:
					settings.setTextSize(WebSettings.TextSize.NORMAL);
					break;
				case 4:
					settings.setTextSize(WebSettings.TextSize.LARGER);
					break;
				case 5:
					settings.setTextSize(WebSettings.TextSize.LARGEST);
					break;
				}
			}
		});
		btn_minus.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				fontSize--;
				if (fontSize < 0) {
					fontSize = 1;
					Toast.makeText(mContext, "字体已经最小了", Toast.LENGTH_SHORT).show();
				}
				switch (fontSize) {
				case 1:
					settings.setTextSize(WebSettings.TextSize.SMALLEST);
					break;
				case 2:
					settings.setTextSize(WebSettings.TextSize.SMALLER);
					break;
				case 3:
					settings.setTextSize(WebSettings.TextSize.NORMAL);
					break;
				case 4:
					settings.setTextSize(WebSettings.TextSize.LARGER);
					break;
				case 5:
					settings.setTextSize(WebSettings.TextSize.LARGEST);
					break;
				}
			}
		});
		// 清除按钮
		btn_clear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mPaintView != null) {
					app.clearPaintNode();
					mPaintView.clearAll();
					mPaintView.resetState();
				}
			}
		});
		// 保存按钮
		btn_save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(mContext, "保存图片", Toast.LENGTH_SHORT).show();
				String fileName = Const.tempPath + "/"
						+ System.currentTimeMillis() + ".png";
				String str = "";
				try {
					str = savePhoto(fileName);
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
				dataMap.put("BoardcastType", "nees.signName");
				app.getPaintPath();
				app.clearPaintNode();
			}
		});
		// 预览
		btn_preview.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPaintView.preview(app.getPaintPath());
			}
		});
	}

	@Override
	public void scrollChanged(int curY) {
		int height = hotel_webView.getContentHeight();

		Log.i("TAG", "WebView的高度:" + height);
		Log.i("TAG", "WebView当前的高度:" + curY);
		if (curY + 980 > height) {
			isVisible = true;
			layout_hotel_draw.setVisibility(View.VISIBLE);

			int footHeight = layout_hotel_draw.getHeight();
			Log.i("TAG", "底部菜单的高度:" + footHeight);
			Toast.makeText(mContext, "底部菜单的高度:" + footHeight,
					Toast.LENGTH_SHORT).show();
			int h = hotel_webView.getHeight();
			Toast.makeText(mContext, "高度:" + h, Toast.LENGTH_SHORT).show();
			showDrawPaintView();
		} else {
			isVisible = false;
			layout_hotel_draw.setVisibility(View.GONE);
		}
	}

	/**
	 * 初始化paintView的回调函数
	 */
	private void initCallBack() {
		mPaintView.setCallBack(new PaintViewCallBack() {
			// 当画了之后对Button进行更新
			public void onHasDraw() {
			}

			// 当点击之后让各个弹出的窗口都消失
			public void onTouchDown() {
			}
		});
	}

	/**
	 * 显示画图
	 */
	private void showDrawPaintView() {
		mPaintView = new CustomPaintView(mContext);
		// 设置监听事件
		mPaintView.setOnPaintListener(new OnPaintListener() {
			@Override
			public void paint(float x, float y, int action) {
				PaintNode node = app.new PaintNode();
				node.x = x;
				node.y = y;
				node.action = action;
				node.time = System.currentTimeMillis();
				app.addNodeToPaintPath(node);
			}
		});
		layout_hotel_draw.removeAllViews();
		layout_hotel_draw.addView(mPaintView);
		initCallBack();
	}
	
	/**
	 * @throws Exception
	 *             保存图片 savePhoto(这里用一句话描述这个方法的作用)
	 * @Title: savePhoto
	 * @Description: TODO
	 * @param @param fileName 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private String savePhoto(String fileName) throws Exception {
		String md5Str = "";
		String strPathDetail = "";
		String flag = "";
		String message = "";
		String retStr = "";
		Bitmap bitmap = mPaintView.getSnapShoot();
		BitMapUtils.saveToSdCard(fileName, bitmap);
		File file = new File(fileName);
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
}
