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
import com.neusoft.neespad.common.MyApplication.PaintNode;
import com.neusoft.neespad.common.Util;
import com.neusoft.neespad.fragment.CustomFragment;
import com.neusoft.neespad.fragment.CustomFragment.IFragmentCallBack;
import com.neusoft.neespad.view.CustomPaintView;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * ���ƻ�ǩ��Ч��
 * 
 * @author Administrator
 */
public class SignCustomActivity extends FragmentActivity implements
		IFragmentCallBack {

	public LinearLayout layout_tab;// Tab����
	public RadioGroup rb_group;// �˵�������
	public RadioButton rb_report;// ҵ������
	public RadioButton rb_treaty1;// 5sЭ��
	public RadioButton rb_treaty2;// ����Э��

	public RelativeLayout layout_menu;// �˵�����
	public Button image_btn_sign;// ǩ����ť
	public ImageButton image_btn_minus;// ��С����
	public ImageButton image_btn_add;// �Ŵ�����
	public ImageButton btn_magnifer;// �Ŵ�
	public ImageButton btn_next_page;// ��һҳ
	public ImageButton btn_last_page;// ��һҳ

	private PopupWindow popWindow;// ��������
	private LinearLayout layout;
	private RelativeLayout layout_top_tip;
	private CustomPaintView mPaintView;
	public String url = "file:///android_asset/report.html";
	private String sdCardPath = "file:///mnt/sdcard/neesPad/temp/";
	private Context mContext;
	private LinearLayout layout_main_container;//�ܵ�����
	
	private Button btn_save;
	private Button btn_clear;
	private Button btn_cancel;
	private Button btn_preview;
	
	private MyApplication app;
	private static Map<String, Object> dataMap;
	private FragmentManager fm = getSupportFragmentManager();
	static CustomFragment reportFragment;
	static CustomFragment treatyFragment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_customer_sign);
		initView();
		setListener();
	}

	/**
	 * ��ʼ��Ԫ�ؿؼ�
	 */
	private void initView() {
		mContext=this;
		app = (MyApplication) getApplication();
		dataMap = app.getMap();
		layout_tab = (LinearLayout) findViewById(R.id.layout_tab);
		rb_group = (RadioGroup) findViewById(R.id.rb_group);
		layout_menu = (RelativeLayout) findViewById(R.id.layout_menu);
		rb_report = (RadioButton) findViewById(R.id.rb_report);
		rb_report.setChecked(true);
		rb_treaty1 = (RadioButton) findViewById(R.id.rb_treaty1);
		rb_treaty2 = (RadioButton) findViewById(R.id.rb_treaty2);
		layout_menu = (RelativeLayout) findViewById(R.id.layout_menu);
		image_btn_sign = (Button) findViewById(R.id.image_btn_sign);
		image_btn_minus = (ImageButton) findViewById(R.id.image_btn_minus);
		image_btn_add = (ImageButton) findViewById(R.id.image_btn_add);
		btn_magnifer = (ImageButton) findViewById(R.id.btn_magnifer);
		btn_next_page = (ImageButton) findViewById(R.id.btn_next_page);
		btn_last_page = (ImageButton) findViewById(R.id.btn_last_page);
		setFragment();
		setListener();
	}

	/**
	 * ���ü����¼�
	 */
	private void setFragment() {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		reportFragment = new CustomFragment();
		ft.add(R.id.fragment_main, reportFragment);
		ft.commit();
	}
	
	/**
	 * �����µ�Fragment
	 * @param url
	 */
	public static void createFragment(String url,FragmentManager fm){
		FragmentTransaction ft = fm.beginTransaction();
		treatyFragment = new CustomFragment();
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.replace(R.id.fragment_main, treatyFragment);
		ft.commit();
	}
	/**
	 * �滻
	 * @param url
	 * @param fm
	 * @param frament
	 */
	public static void changeFragment(String url,FragmentManager fm, CustomFragment frament){
		FragmentTransaction ft = fm.beginTransaction();
		if(frament==null){
			frament = new CustomFragment();
		}
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE); 
		ft.replace(R.id.fragment_main, frament);
		ft.commit();
	}
	/**
	 * ���ü����¼�
	 */
	private void setListener() {
		image_btn_sign.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				LayoutInflater layoutInflater = (LayoutInflater) (SignCustomActivity.this)
						.getSystemService(LAYOUT_INFLATER_SERVICE);
				// ��ȡ�Զ��岼���ļ�poplayout.xml����ͼ
				View popview = layoutInflater.inflate(R.layout.poplayout, null);
				int size[] = Util.getScreenSize(SignCustomActivity.this);
				popWindow = new PopupWindow(popview, size[0],size[1]);
				layout = (LinearLayout) popview.findViewById(R.id.layout_draw);
				layout_top_tip=(RelativeLayout) popview.findViewById(R.id.layout_top_tip);
				layout_main_container=(LinearLayout) popview.findViewById(R.id.layout_main_container);
				if(size[0]<900){
					layout_main_container.setPadding(50, 40, 50, 40);
				}
				
				/**
				 * ��ʾ��
				 */
				layout_top_tip.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
//						layout_top_tip.setVisibility(View.GONE);
//						layout.setVisibility(View.VISIBLE);
					}
				});
				layout_top_tip.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						layout_top_tip.setVisibility(View.GONE);
						layout.setVisibility(View.VISIBLE);
						return false;
					}
				});
				mPaintView = new CustomPaintView(mContext);
				//���ü����¼�
				mPaintView.setOnPaintListener(new OnPaintListener() {
					@Override
					public void paint(float x, float y, int action) {
						PaintNode node=app.new PaintNode();
						node.x=x;
						node.y=y;
						node.action=action;
						node.time=System.currentTimeMillis();
						app.addNodeToPaintPath(node);
					}
				});
				layout.removeAllViews();
				layout.addView(mPaintView);
				initCallBack();
				// �涨������λ��
				popWindow.showAtLocation(findViewById(R.id.layout_main),
						Gravity.CENTER, 0, 0);
				btn_save = (Button) popview.findViewById(R.id.btn_save);
				btn_clear = (Button) popview.findViewById(R.id.btn_clear);
				btn_cancel = (Button) popview.findViewById(R.id.btn_cancel);
				btn_preview=(Button) popview.findViewById(R.id.btn_preview);
				btn_save.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
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
						Toast.makeText(mContext, "ǩ�����", Toast.LENGTH_LONG).show();
						app.getPaintPath();
						app.clearPaintNode();
						popWindow.dismiss();
					}
				});
				//�������
				btn_clear.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						app.clearPaintNode();
						mPaintView.clearAll();
						mPaintView.resetState();
						setButtonEnabled(false);
					}
				});
				//�˳�
				btn_cancel.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						app.clearPaintNode();
						popWindow.dismiss();
					}
				});
				btn_preview.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						mPaintView.preview(app.getPaintPath());
					}
				});
				
			}
		});
		
		/**
		 * �仯
		 */
		rb_group.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				//��ȡ������ѡ�����ID
				int radioButtonId = group.getCheckedRadioButtonId();
				switch (radioButtonId) {
				case R.id.rb_report:
					url="file:///android_asset/report.html";
					rb_report.setChecked(true);
					changeFragment(url,fm,reportFragment);
					break;
				case R.id.rb_treaty1:
					url="file:///android_asset/treaty.html";
					createFragment(url,fm);
					rb_treaty1.setChecked(true);
					break;
				case R.id.rb_treaty2:
					url=sdCardPath+"report.html";
					createFragment(url,fm);
					rb_treaty2.setChecked(true);
					break;
				}
			}
		});
	}

	/**
	 * @throws Exception
	 *             ����ͼƬ savePhoto(������һ�仰�����������������)
	 * @Title: savePhoto
	 * @Description: TODO
	 * @param @param fileName �趨�ļ�
	 * @return void ��������
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
	
	/**
	 * ��ʼ��paintView�Ļص�����
	 */
	private void initCallBack() {
		mPaintView.setCallBack(new PaintViewCallBack() {
			// ������֮���Button���и���
			public void onHasDraw() {
				setButtonEnabled(true);
			}

			// �����֮���ø��������Ĵ��ڶ���ʧ
			public void onTouchDown() {
			}
		});
	}
	/**
	 * ���ð�ť�Ƿ����
	 * @param flag
	 */
	private void setButtonEnabled(boolean flag){
		btn_save.setEnabled(flag);
		btn_clear.setEnabled(flag);
	}
	@Override
	public SignCustomActivity getSignCustomActivity() {
		return SignCustomActivity.this;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (popWindow != null && popWindow.isShowing()) {
			popWindow.dismiss();
		}
	}

	public FragmentManager getFm() {
		return fm;
	}
	
}
