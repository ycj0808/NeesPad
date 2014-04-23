package com.neusoft.neespad.activity;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import com.example.drawdemo.interfaces.PaintViewCallBack;
import com.example.drawdemo.utils.BitMapUtils;
import com.example.drawdemo.view.PaintView;
import com.neusoft.neespad.R;
import com.neusoft.neespad.common.Const;
import com.neusoft.neespad.common.MyApplication;
import com.neusoft.neespad.common.Util;
import com.neusoft.neespad.view.MyWebView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

public class SignActivity extends Activity {

	private MyWebView webView;
	private Button btn_sign;
	private PopupWindow popWindow;
	private Context mContext;
	private Button btn_save;
	private Button btn_cancel;
	private Button btn_exit;
	private LinearLayout layout;
	private PaintView mPaintView;
	private MyApplication app;
	private static Map<String, Object> dataMap;
	private String url = "file:///android_asset/report.html";
	private Button btn_magnifier;// 放大镜
	private Bitmap mBitmap;
	private boolean useMagnifier = false;// 使用放大镜
	private int fontSize = 1;// WebView字体
	private WebSettings settings;
	private ImageButton fontMinus;//缩小字体
	private ImageButton fontAdd;//放大字体
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_sign);
		initView();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initView() {
		mContext = this;
		app = (MyApplication) getApplication();
		dataMap = app.getMap();
		webView = (MyWebView) findViewById(R.id.webView);
		btn_sign = (Button) findViewById(R.id.btn_sign);
		btn_magnifier = (Button) findViewById(R.id.btn_magnifier);
		settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setSupportZoom(true);
		settings.setBuiltInZoomControls(true);
		webView.loadUrl(url);
		webView.setOnTouchListener(null);
		getDefaultFontSize();
		
		fontMinus=(ImageButton) findViewById(R.id.img_font_minus);
		fontAdd=(ImageButton) findViewById(R.id.img_font_add);
		
		// 签名按钮的监听事件
		btn_sign.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				LayoutInflater layoutInflater = (LayoutInflater) (SignActivity.this)
						.getSystemService(LAYOUT_INFLATER_SERVICE);
				// 获取自定义布局文件poplayout.xml的视图
				View popview = layoutInflater.inflate(R.layout.poplayout, null);
				int size[] = Util.getScreenSize(SignActivity.this);
				popWindow = new PopupWindow(popview, size[0] - 100,
						size[1] - 100);
				layout = (LinearLayout) popview.findViewById(R.id.layout_draw);
				mPaintView = new PaintView(mContext);
				layout.removeAllViews();
				layout.addView(mPaintView);
				initCallBack();
				// 规定弹窗的位置
				popWindow.showAtLocation(findViewById(R.id.main),
						Gravity.CENTER, 0, 0);

				// registerForContextMenu(mPaintView);
				// registerForContextMenu(layout);
				registerForContextMenu(popview);

				btn_save = (Button) popview.findViewById(R.id.btn_save);
				btn_cancel = (Button) popview.findViewById(R.id.btn_cancel);
				btn_exit = (Button) popview.findViewById(R.id.btn_exit);
				btn_save.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
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
						Toast.makeText(mContext, "签名完成", Toast.LENGTH_LONG)
								.show();
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
				btn_exit.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						popWindow.dismiss();
					}
				});
			}
		});

		// 放大镜的监听事件
		btn_magnifier.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!useMagnifier) {
					useMagnifier = true;
					webView.setOnTouchListener(webViewToucherListener);
				} else {
					useMagnifier = false;
					webView.setOnTouchListener(null);
					if(mBitmap!=null){
						mBitmap.recycle(); 
					}
				}
			}
		});
		// 长按键 事件
		webView.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				return false;
			}
		});
		//字体缩小
		fontMinus.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				fontSize--;
				if(fontSize < 0){
					fontSize =1;
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
		//字体放大
		fontAdd.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				fontSize++;
				if(fontSize > 5){
					fontSize =5;
					Toast.makeText(mContext, "字体已经最大了", Toast.LENGTH_SHORT).show();
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
	}

	/**
	 * 获取默认字体大小 getDefaultFontSize
	 * @Title: getDefaultFontSize
	 * @Description: TODO
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	@SuppressWarnings("deprecation")
	private void getDefaultFontSize() {
		if (settings.getTextSize() == WebSettings.TextSize.SMALLEST) {
			fontSize = 1;
		} else if (settings.getTextSize() == WebSettings.TextSize.SMALLER) {
			fontSize = 2;
		} else if (settings.getTextSize() == WebSettings.TextSize.NORMAL) {
			fontSize = 3;
		}else if (settings.getTextSize() == WebSettings.TextSize.LARGER){
			fontSize = 4;
		}else if (settings.getTextSize() == WebSettings.TextSize.LARGEST) {
			fontSize = 5;
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

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		MenuInflater mInflater = getMenuInflater();
		mInflater.inflate(R.menu.menu_item, menu);
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.save:
			Toast.makeText(mContext, "save", Toast.LENGTH_LONG).show();
			break;
		case R.id.clear:
			Toast.makeText(mContext, "clear", Toast.LENGTH_LONG).show();
			break;
		case R.id.cancel:
			Toast.makeText(mContext, "cancel", Toast.LENGTH_LONG).show();
			break;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (popWindow != null && popWindow.isShowing()) {
			popWindow.dismiss();
		}
	}

	// webView的触摸事件
	private OnTouchListener webViewToucherListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int mCurrentX = (int) event.getX();
			int mCurrentY = (int) event.getY();
			webView.setDrawingCacheEnabled(true);
			webView.invalidate();
			mBitmap = captureWebViewVisibleSize(webView);
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (mBitmap != null) {
					webView.setBitmap(mBitmap);
				}
				webView.setPoint(mCurrentX, mCurrentY);
				webView.invalidate();
				return true;
			case MotionEvent.ACTION_MOVE:
				if (mBitmap != null) {
					webView.setBitmap(mBitmap);
				}
				webView.setPoint(mCurrentX, mCurrentY);
				webView.invalidate();
				return true;
			default:
				webView.clearDraw();
				webView.invalidate();
				webView.setDrawingCacheEnabled(false);
				if(mBitmap!=null){
					mBitmap.recycle(); 
				}
				return false;
			}
		}
	};

	/**
	 * 
	 * captureWebView
	 * 
	 * @Title: captureWebView
	 * @Description: TODO
	 * @param @param webView
	 * @param @return 设定文件
	 * @return Bitmap 返回类型
	 * @throws
	 */
	@SuppressWarnings("deprecation")
	private Bitmap captureWebView(WebView webView) {
		Picture snapShot = webView.capturePicture();
		Bitmap bmp = Bitmap.createBitmap(snapShot.getWidth(),
				snapShot.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		snapShot.draw(canvas);
		return bmp;
	}

	/**
	 * webView可视区域的截图 captureWebViewVisibleSize
	 * 
	 * @Title: captureWebViewVisibleSize
	 * @Description: TODO
	 * @param @param webView
	 * @param @return 设定文件
	 * @return Bitmap 返回类型
	 * @throws
	 */
	private Bitmap captureWebViewVisibleSize(WebView webView) {
		Bitmap bmp = webView.getDrawingCache();
		return bmp;
	}
	/**
	  * 屏幕快照
	  * captureScreen(这里用一句话描述这个方法的作用)
	  * @Title: captureScreen
	  * @Description: TODO
	  * @param @param context
	  * @param @return    设定文件
	  * @return Bitmap    返回类型
	  * @throws
	 */
    private Bitmap captureScreen(Activity context){
        View cv = context.getWindow().getDecorView();
        Bitmap bmp = Bitmap.createBitmap(cv.getWidth(), cv.getHeight(),Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        cv.draw(canvas);
        return bmp;
    }
}
