package com.example.zaizai.fragment;


import java.util.ArrayList;
import com.example.zaizai.MainActivity;
import com.example.zaizai.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class PersonalFragment extends Fragment {
	
	/*private String[] personal_item = {"我的发布","我的抢单",
			"个人账户","反馈","关于"};*/
	private ArrayList<String> listP = new ArrayList<String>();
	ImageButton login;
	TextView loginText;
	
	Button logVid,logPic;
	
	@Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
          
    }  
           
    @Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState) {  
        View view = inflater.inflate(R.layout.personal_layout, container, false);

		//loginText = (TextView)view.findViewById(R.id.login_text);
        //登录头像点击事件设置
        login = (ImageButton)view.findViewById(R.id.login_head);
        /*login.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick(View v){
        			//使用该函数回掉loginactivity的数据，判断是否登陆
                	Intent intent = new Intent(getActivity(),com.example.zaizai.activity.VideoVerify.class);
                    startActivityForResult(intent,2);

        	}
        }); */
        
        logVid = (Button)view.findViewById(R.id.log_vid);
        logPic = (Button)view.findViewById(R.id.log_pic);
        logVid.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick(View v){
        		Intent intent = new Intent(getActivity(),com.example.zaizai.activity.VideoVerify.class);
                startActivityForResult(intent,2);
        	}
        });
        
        logPic.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick(View v){
        		Intent intent2 = new Intent(getActivity(),com.example.zaizai.activity.OnlineFace.class);
                startActivityForResult(intent2,2);
        	}
        });
        
        
        //加载ListView
        listP.add("我的发布");
        listP.add("我的抢单");
        listP.add("个人账户");
        listP.add("反馈");
        listP.add("关于");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(),//获取context
        		android.R.layout.simple_list_item_1,listP);
        ListView listPersonal = (ListView)view.findViewById(R.id.list_personal);//注意在fragment中获取控件使用的方法
        listPersonal.setAdapter(adapter);
        
        //设置每个item点击事件
        listPersonal.setOnItemClickListener(new OnItemClickListener(){
        	@Override
        	public void onItemClick(AdapterView<?>parent, View view, 
        			int position, long id){
        		if(listP.get(position).equals("我的发布")){
        			MainActivity parentActivity = (MainActivity)getActivity();
        			parentActivity.changeTab(R.id.lymyissue);
        		}
        		if(listP.get(position).equals("我的抢单")){
        			Intent intent = new Intent(getActivity(),com.example.zaizai.activity.MygrabActivity.class);
                    startActivity(intent);
        		}
        		if(listP.get(position).equals("个人账户")){
        			Intent intent = new Intent(getActivity(),com.example.zaizai.activity.MyaccActivity.class);
                    startActivity(intent);
        		}
        		if(listP.get(position).equals("反馈")){
        			Intent intent = new Intent(getActivity(),com.example.zaizai.activity.FeedbackActivity.class);
                    startActivity(intent);
        		}
        		if(listP.get(position).equals("关于")){
        			Intent intent = new Intent(getActivity(),com.example.zaizai.activity.AboutActivity.class);
                    startActivity(intent);
        		}
        	}
        });
        
        
        return view;  
    }  
    
      
    @Override  
    public void onActivityCreated(Bundle savedInstanceState) {  
        super.onActivityCreated(savedInstanceState);  
      
    }  
      
    @Override  
    public void onPause() {  
        super.onPause();  
    }
    
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
    	super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
		case 2:
			if (resultCode == Activity.RESULT_OK) {
				((MainActivity)getActivity()).setCurrentLaunch(1);
				//loginText.setText("小明");
	        	login.setBackgroundResource(R.drawable.bai);
			}
			break;
		default:
		}
	}
}
