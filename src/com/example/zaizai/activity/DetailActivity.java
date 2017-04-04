package com.example.zaizai.activity;

import com.example.zaizai.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailActivity extends Activity {
	
	private ImageView imageDetail;
	private TextView spot,time,reward,username,ps;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.detail_layout);
		
		imageDetail = (ImageView)findViewById(R.id.image_detail);
		spot = (TextView)findViewById(R.id.spot_detail);
		time = (TextView)findViewById(R.id.time_detail);
		reward = (TextView)findViewById(R.id.reward_detail);
		username = (TextView)findViewById(R.id.username_detail);
		ps = (TextView)findViewById(R.id.ps_detail);
		
		imageDetail.setBackgroundResource(getIntent().getIntExtra("headId", 0));
		spot.setText(getIntent().getStringExtra("spot"));
		time.setText(getIntent().getStringExtra("time"));
		reward.setText(getIntent().getStringExtra("reward"));
		username.setText(getIntent().getStringExtra("username"));
		ps.setText(getIntent().getStringExtra("ps"));
	}
}
