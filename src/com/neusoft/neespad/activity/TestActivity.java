package com.neusoft.neespad.activity;

import com.neusoft.neespad.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TestActivity extends Activity {

	private Button btnButton;
	private Button btnButton2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_control);
		btnButton=(Button) findViewById(R.id.btn_control);
		btnButton2=(Button) findViewById(R.id.btn_control2);
		btnButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent=new Intent(TestActivity.this,BigSurfaceActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("type", "big");
				intent.putExtra("data", bundle);
				startActivity(intent);
				//finish();
			}
		});
		
		btnButton2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent=new Intent(TestActivity.this,SmallSurfaceActivity.class);
				startActivity(intent);
				//finish();
			}
		});
	}
}
