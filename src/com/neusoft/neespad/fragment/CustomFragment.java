package com.neusoft.neespad.fragment;

import com.neusoft.neespad.R;
import com.neusoft.neespad.activity.SignCustomActivity;
import com.neusoft.neespad.view.MyWebView;
import com.neusoft.neespad.view.MyWebView.ScrollChangedCallback;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * ���Ƶ�fragment,����ǩ��Э��
 * 
 * @author Administrator
 */
@SuppressLint("NewApi")
public class CustomFragment extends Fragment implements ScrollChangedCallback{

	private IFragmentCallBack fragmentCallBack;
	private WebSettings settings;
	private MyWebView webView;
	private String url = "";
	private int fontSize = 1;// WebView����
	private Bitmap mBitmap;
	private RelativeLayout layout_menu;// �˵�����
	private Button image_btn_sign;// ǩ����ť
	private ImageButton image_btn_minus;// ��С����
	private ImageButton image_btn_add;// �Ŵ�����
	private ImageButton btn_magnifer;// �Ŵ�
	private ImageButton btn_next_page;// ��һҳ
	private ImageButton btn_last_page;// ��һҳ
	private Context mContext;
	private boolean useMagnifier = false;// ʹ�÷Ŵ�
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			fragmentCallBack = (IFragmentCallBack) activity;
			mContext = fragmentCallBack.getSignCustomActivity();
			url = fragmentCallBack.getSignCustomActivity().url;
		} catch (ClassCastException e) {
			throw new ClassCastException("must implements IFragmentCallBack");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.layout_custom_fragment, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initView(view);
		getDefaultFontSize();
	}

	/**
	 * ��ʼ��Ԫ�ؿؼ�
	 * @param view
	 */
	@SuppressLint("SetJavaScriptEnabled")
	@SuppressWarnings("deprecation")
	private void initView(View view) {
		webView = (MyWebView) view.findViewById(R.id.fragment_webView);
		webView.registerScrollChangedCallback(this);
		webView.setBackgroundColor(0);
		settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setBuiltInZoomControls(false);
		settings.setTextSize(WebSettings.TextSize.LARGER);
		webView.loadUrl(url);
	}

	@SuppressWarnings("deprecation")
	private void getDefaultFontSize() {
		if (settings.getTextSize() == WebSettings.TextSize.SMALLEST) {
			fontSize = 1;
		} else if (settings.getTextSize() == WebSettings.TextSize.SMALLER) {
			fontSize = 2;
		} else if (settings.getTextSize() == WebSettings.TextSize.NORMAL) {
			fontSize = 3;
		} else if (settings.getTextSize() == WebSettings.TextSize.LARGER) {
			fontSize = 4;
		} else if (settings.getTextSize() == WebSettings.TextSize.LARGEST) {
			fontSize = 5;
		}
	}

	/**
	 * ���ü����¼�
	 */
	private void setListener() {
		// ���ó����¼���Ч
		webView.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				return true;
			}
		});
		// �Ŵ󾵵ļ����¼�
		btn_magnifer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!useMagnifier) {
					useMagnifier = true;
					webView.setOnTouchListener(webViewToucherListener);
				} else {
					useMagnifier = false;
					webView.setOnTouchListener(null);
					if (mBitmap != null) {
						mBitmap.recycle();
					}
				}
			}
		});

		// ������С
		image_btn_minus.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				fontSize--;
				if (fontSize < 0) {
					fontSize = 1;
					Toast.makeText(mContext, "�����Ѿ���С��", Toast.LENGTH_SHORT).show();
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

		// ����Ŵ�
		image_btn_add.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				fontSize++;
				if (fontSize > 5) {
					fontSize = 5;
					Toast.makeText(mContext, "�����Ѿ������", Toast.LENGTH_SHORT)
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
		//��һҳ�ļ����¼�
		btn_next_page.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				final int currY=webView.getScrollY();
				final int height=webView.getContentHeight();
				int nextY=currY+680;
				if(nextY<height){
					if(nextY+680<height){
						webView.setScrollY(nextY);
					}else{
						webView.setScrollY(height-680);
						Toast.makeText(mContext, "�Ѿ��ﵽ�ײ�", Toast.LENGTH_SHORT).show();
					}
				}else{
					webView.setScrollY(height-680);
					Toast.makeText(mContext, "�Ѿ��ﵽ�ײ�", Toast.LENGTH_SHORT).show();
				}
			}
		});
		//��һҳ�ļ����¼�
		btn_last_page.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				int currY=webView.getScrollY();
				int lastY=currY-680;
				if(lastY>0){
					webView.setScrollY(lastY);
				}else{
					webView.setScrollY(0);
					Toast.makeText(mContext, "�Ѿ��ﵽ����", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	public void onPause() {                                                        
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		layout_menu = (RelativeLayout) fragmentCallBack.getSignCustomActivity().findViewById(R.id.layout_menu);
		image_btn_sign = (Button) fragmentCallBack.getSignCustomActivity().findViewById(R.id.image_btn_sign);
		image_btn_minus = (ImageButton) fragmentCallBack.getSignCustomActivity().findViewById(R.id.image_btn_minus);
		image_btn_add = (ImageButton) fragmentCallBack.getSignCustomActivity().findViewById(R.id.image_btn_add);
		btn_magnifer = (ImageButton) fragmentCallBack.getSignCustomActivity().findViewById(R.id.btn_magnifer);
		btn_next_page = (ImageButton) fragmentCallBack.getSignCustomActivity().findViewById(R.id.btn_next_page);
		btn_last_page = (ImageButton) fragmentCallBack.getSignCustomActivity().findViewById(R.id.btn_last_page);
		setListener();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	/**
	 * webView�Ĵ����¼�
	 */
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
				if (mBitmap != null) {
					mBitmap.recycle();
				}
				return false;
			}
		}
	};

	/**
	 * webView��������Ľ�ͼ captureWebViewVisibleSize
	 * 
	 * @Title: captureWebViewVisibleSize
	 * @Description: TODO
	 * @param @param webView
	 * @param @return �趨�ļ�
	 * @return Bitmap ��������
	 * @throws
	 */
	private Bitmap captureWebViewVisibleSize(WebView webView) {
		Bitmap bmp = webView.getDrawingCache();
		return bmp;
	}

	/**
	 * ����ص��ӿ���
	 * 
	 * @author Administrator
	 */
	public interface IFragmentCallBack {
		/**
		 * ��ȡAttach��Activity
		 * 
		 * @return
		 */
		public SignCustomActivity getSignCustomActivity();
	}

	@Override
	public void scrollChanged(int curY) {
		
	}
}
