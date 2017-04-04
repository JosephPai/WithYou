package com.example.zaizai;

import java.util.ArrayList;
import java.util.List;

import com.example.zaizai.R;
import com.example.zaizai.adapter.MyFragmentPagerAdapter;
import com.example.zaizai.adapter.MyPagerAdapter;
import com.example.zaizai.fragment.MyissueFragment;
import com.example.zaizai.fragment.PersonalFragment;
import com.example.zaizai.fragment.TaskhallFragment;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements OnClickListener {
	
	public int currentLaunch = 0;
	public int isNewpo = 0;
	public String rewardNewpo, spotNewpo, psNewpo;

	public String getRewardNewpo() {
		return rewardNewpo;
	}

	public void setRewardNewpo(String rewardNewpo) {
		this.rewardNewpo = rewardNewpo;
	}

	public String getSpotNewpo() {
		return spotNewpo;
	}

	public void setSpotNewpo(String spotNewpo) {
		this.spotNewpo = spotNewpo;
	}

	public String getPsNewpo() {
		return psNewpo;
	}

	public void setPsNewpo(String psNewpo) {
		this.psNewpo = psNewpo;
	}

	public int getIsNewpo() {
		return isNewpo;
	}

	public void setIsNewpo(int isNewpo) {
		this.isNewpo = isNewpo;
	}

	public int getCurrentLaunch() {
		return currentLaunch;
	}

	public void setCurrentLaunch(int currentLaunch) {
		this.currentLaunch = currentLaunch;
	}


	private TextView titleText;
	//private List<View> views = new ArrayList<View>(); 
	private List<Fragment> fragments = new ArrayList<Fragment>(); 
    private ViewPager viewPager;
    private LinearLayout lytaskhall,lypersonal,lymyissue;
    private ImageView litaskhall,lipersonal,limyissue,liCurrent;
    private TextView lttaskhall,ltpersonal,ltmyissue,ltCurrent;
    
    public FragmentTransaction mFragmentTransaction;
    public FragmentManager fragmentManager;
    public String curFragmentTag = "";
    
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.bottom_layout);
				
		
		initView();  		  
        initData();  
	}


	private void initView() {
		// TODO Auto-generated method stub
		viewPager = (ViewPager)findViewById(R.id.viewPager);
		
		lytaskhall = (LinearLayout)findViewById(R.id.lytaskhall);
		lypersonal = (LinearLayout)findViewById(R.id.lypersonal);
		lymyissue = (LinearLayout)findViewById(R.id.lymyissue);
		
		lytaskhall.setOnClickListener(this);
		lypersonal.setOnClickListener(this);
		lymyissue.setOnClickListener(this);
		
		litaskhall = (ImageView)findViewById(R.id.litaskhall);
		lipersonal = (ImageView)findViewById(R.id.lipersonal);
		limyissue = (ImageView)findViewById(R.id.limyissue);
		
		lttaskhall = (TextView)findViewById(R.id.lttaskhall);
		ltpersonal = (TextView)findViewById(R.id.ltpersonal);
		ltmyissue = (TextView)findViewById(R.id.ltmyissue);
		
		litaskhall.setSelected(true);
		lttaskhall.setSelected(true);
		liCurrent = litaskhall;
		ltCurrent = lttaskhall;
		
		viewPager.setOnPageChangeListener(new OnPageChangeListener(){
			@Override  
            public void onPageSelected(int position) {  
                changeTab(position);  
            }  
  
            @Override  
            public void onPageScrolled(int arg0, float arg1, int arg2) {  
  
            }  
  
            @Override  
            public void onPageScrollStateChanged(int arg0) {  
  
            }  
		});
		
		viewPager.setOffscreenPageLimit(2);
		//设置向左和向右都缓存limit个界面
	}


	private void initData() {
		// TODO Auto-generated method stub
		
		/*LayoutInflater mInflater = LayoutInflater.from(this);  
        View tab01 = mInflater.inflate(R.layout.taskhall_layout, null);  
        View tab02 = mInflater.inflate(R.layout.myissue_layout, null);  
        View tab03 = mInflater.inflate(R.layout.personal_layout, null);   
        views.add(tab01);  
        views.add(tab02);  
        views.add(tab03);   
  
        MyPagerAdapter adapter = new MyPagerAdapter(views);  
        viewPager.setAdapter(adapter);*/
		
		Fragment taskhallFragment = new TaskhallFragment();
		Fragment myissueFragment = new MyissueFragment();
		Fragment personalFragment = new PersonalFragment();
		
		fragments.add(taskhallFragment);
		fragments.add(myissueFragment);
		fragments.add(personalFragment);
		
		MyFragmentPagerAdapter adapter = new 
				MyFragmentPagerAdapter(getSupportFragmentManager(),fragments);
		viewPager.setAdapter(adapter);
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		changeTab(v.getId());
	}


	public void changeTab(int id) {
		// TODO Auto-generated method stub
		liCurrent.setSelected(false);
		ltCurrent.setSelected(false);
		
		switch(id){
		case R.id.lytaskhall:
			viewPager.setCurrentItem(0);
		case 0:
			litaskhall.setSelected(true);
			liCurrent = litaskhall;
			lttaskhall.setSelected(true);
			ltCurrent = lttaskhall;
			break;
			
		case R.id.lymyissue:
			viewPager.setCurrentItem(1);
		case 1:
			limyissue.setSelected(true);
			liCurrent = limyissue;
			ltmyissue.setSelected(true);
			ltCurrent = ltmyissue;
			break;
			
		case R.id.lypersonal:
			viewPager.setCurrentItem(2);
		case 2:
			lipersonal.setSelected(true);
			liCurrent = lipersonal;
			ltpersonal.setSelected(true);
			ltCurrent = ltpersonal;
			break;
		default:
			break;
		}
	}
	
	/*@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		switch(requestCode){
		case 2:
			if (resultCode == RESULT_OK) {
				setCurrentLaunch(1);
			}
			break;
		default:
		}
	}*/
	
}
