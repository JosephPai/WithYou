package com.example.zaizai.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * ÎªÊµÏÖµ×²¿µ¼º½À¸£¬ËéÆ¬ÊÊÅäÆ÷
 * FragmentPagerÊÊÅäÆ÷
 * @author baizechen
 *
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

	private List<Fragment> fragments;
	
	
	public MyFragmentPagerAdapter(FragmentManager fragmentManager, List<Fragment> fragments) {  
        super(fragmentManager);  
        this.fragments = fragments;  
    }

	@Override
	public Fragment getItem(int position) {
		// TODO Auto-generated method stub
		return fragments.get(position);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return fragments.size();
	}
	

}
