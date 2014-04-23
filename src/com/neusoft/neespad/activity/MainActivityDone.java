package com.neusoft.neespad.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.neusoft.neespad.R;
import com.neusoft.neespad.common.Const;
import com.neusoft.neespad.service.MainService;
import com.neusoft.neespad.view.JazzyViewPager;
import com.neusoft.neespad.view.JazzyViewPager.TransitionEffect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

public class MainActivityDone extends Activity {

	// Viewpager对象 
//	private ViewPager viewPager;
	private ImageView imageView;
	// 创建一个数组，用来存放每个页面要显示的View 
	private ArrayList<View> pageViews;
	// 装显示图片的viewgroup 
	private ViewGroup viewPictures;
	private Timer mTimer;
	private TimerTask mTask;//计时
	int pageIndex = 0;//
	boolean isTaskRun;//是否在运行
	private JazzyViewPager mJazzy;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		LayoutInflater inflater = getLayoutInflater();
		pageViews = new ArrayList<View>();
		pageViews.add(inflater.inflate(R.layout.viewpagers00, null));
		pageViews.add(inflater.inflate(R.layout.viewpagers01, null));
		// 从指定的XML文件中加载视图
		viewPictures = (ViewGroup) inflater.inflate(R.layout.layout_main, null);
//		viewPager=(ViewPager) viewPictures.findViewById(R.id.guidePagers);
		
		setContentView(viewPictures);
		//setupJazziness(TransitionEffect.FlipVertical);
//		viewPager.setAdapter(new NavigationPageAdapter());
		//viewPager.setOnPageChangeListener(new NavigationPageChangeListener());
//		startTask();
		Intent bootser = new Intent(MainActivityDone.this,MainService.class);
		startService(bootser); 
	}
	
	private void setupJazziness(TransitionEffect effect) {
		mJazzy=(JazzyViewPager) viewPictures.findViewById(R.id.guidePagers);
		mJazzy.setTransitionEffect(effect);
		mJazzy.setAdapter(new NavigationPageAdapter());
//		mJazzy.setOnPageChangeListener(new NavigationPageChangeListener());
		mJazzy.setPageMargin(30);
	}
	public class NavigationPageAdapter extends PagerAdapter{
		@Override
		public int getCount() {
			return pageViews.size();
		}
		@Override
		public boolean isViewFromObject(View view, Object obj) {
			return view==obj;
		}
		@Override
		public Object instantiateItem(View container, int position) {
			((ViewPager) container).addView(pageViews.get(position));
			mJazzy.setObjectForPosition(pageViews.get(position),position);
			return pageViews.get(position);
		}
		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView(mJazzy.findViewFromObject(position));
			//((ViewPager) container).removeView(pageViews.get(position));
		}
	}
	
	public class NavigationPageChangeListener implements OnPageChangeListener{
		@Override
		public void onPageScrollStateChanged(int state) {
			if (state == 0 && !isTaskRun) {
				setCurrentItem();
				startTask();
			} else if (state == 1 && isTaskRun)
				stopTask();
		}
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}
		@Override
		public void onPageSelected(int index) {
			if(index==pageViews.size()-1){
				index=0;
			}
			pageIndex = index;
		}
	}
	
	/**
	 * 开启定时任务
	 */
	private void startTask() {
		// TODO Auto-generated method stub
		isTaskRun = true;
		mTimer = new Timer();
		mTask = new TimerTask() {
			@Override
			public void run() {
				pageIndex++;
				mHandler.sendEmptyMessage(0);
			}
		};
		mTimer.schedule(mTask, 5 * 1000, 5 * 1000);// 这里设置自动切换的时间，单位是毫秒，2*1000表示2秒
	}

	/**
	 * 停止定时任务
	 */
	private void stopTask() {
		// TODO Auto-generated method stub
		isTaskRun = false;
		mTimer.cancel();
	}

	// 处理EmptyMessage(0)
	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			 setCurrentItem();
		}
	};

	/**
	 * 处理Page的切换逻辑
	 */
	private void setCurrentItem() {
	   if (pageIndex == new NavigationPageAdapter().getCount()) {
			pageIndex = 0;
		}
//	   viewPager.setCurrentItem(pageIndex, false);// 取消动画
//	   String[] effects = this.getResources().getStringArray(R.array.jazzy_effects);
//	   int index=(int) (Math.random()*12);
//	   TransitionEffect effect = TransitionEffect.valueOf(effects[index]);
//	   mJazzy.setTransitionEffect(effect);
	   mJazzy.setCurrentItem(pageIndex, true);// 取消动画
	}
	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		File file=new File(Const.tempPath);
		if(file.exists()){
			File files[]=file.listFiles();
			for(File f : files){
				if(f.exists()){
					f.delete();
				}
			}
		}
	}
}





















