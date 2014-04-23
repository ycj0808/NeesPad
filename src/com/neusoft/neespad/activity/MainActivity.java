package com.neusoft.neespad.activity;

import java.util.ArrayList;
import java.util.List;

import com.neusoft.neespad.R;
import com.neusoft.neespad.service.MainService;
import com.neusoft.neespad.view.AutoScrollViewPager;
import com.neusoft.neespad.view.ImagePagerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Window;
import android.widget.TextView;

public class MainActivity extends Activity {

	private AutoScrollViewPager viewPager;
	private TextView indexText;
	private List<Integer> imageIdList;
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_auto_viewpager);
		mContext=this;
		viewPager = (AutoScrollViewPager) findViewById(R.id.view_pager);
		imageIdList = new ArrayList<Integer>();
		imageIdList.add(R.drawable.ads00);
		imageIdList.add(R.drawable.ads01);
		viewPager.setAdapter(new ImagePagerAdapter(mContext, imageIdList));
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		viewPager.setInterval(5000);
		viewPager.setCycle(true);
		viewPager.startAutoScroll();
		Intent bootser = new Intent(MainActivity.this,MainService.class);
		startService(bootser); 
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int position) {
//			indexText.setText(new StringBuilder().append(position + 1)
//					.append("/").append(imageIdList.size()));
		}

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		// stop auto scroll when onPause
		viewPager.stopAutoScroll();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// start auto scroll when onResume
		viewPager.startAutoScroll();
	}
}
