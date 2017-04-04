package com.example.zaizai.fragment;

import java.util.ArrayList;
import java.util.List;

import com.example.zaizai.MainActivity;
import com.example.zaizai.R;
import com.example.zaizai.Task;
import com.example.zaizai.adapter.TaskAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.view.View.OnClickListener;

public class MyissueFragment extends Fragment {
	
	private Button newPo;
	private TextView loginText;
	private List<Task> taskList = new ArrayList<Task>();
	 
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
          
    }  
           
    @Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState) {  
        View view = inflater.inflate(R.layout.myissue_layout, container, false);  
        initTasks();//加载个人发布数据
        
        
        
        newPo = (Button)view.findViewById(R.id.newpo);
        newPo.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick(View v){
        		
        		//判断是否登录
                if(((MainActivity)getActivity()).getCurrentLaunch() == 0){
                	//未登录
                	Toast.makeText(getContext(), "请先登录！", Toast.LENGTH_SHORT).show();
                }else{
                	Intent intent = new Intent(getActivity(),com.example.zaizai.activity.NewpoActivity.class);
            		startActivityForResult(intent,3);
                }        		
        	}
        });
        
        
        return view;  
    }  
    
    private void initTasks(){
    	Task bai = new Task("小明","清华大学","12:55","10.00元","我真帅",R.drawable.bai);
    	taskList.add(bai);
    }
    
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
    	super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
		case 3:
			if (resultCode == Activity.RESULT_OK) {
				//获得回传intent之后，添加新的task
				Task hei = new Task("小明",data.getStringExtra("spotNewpo"),"13:40",
						data.getStringExtra("rewardNewpo").toString()+"元",
						data.getStringExtra("psNewpo"),R.drawable.bai);
		    	taskList.add(hei);
		    	//通过MainActivity传递数据
		    	((MainActivity)getActivity()).setSpotNewpo(data.getStringExtra("spotNewpo"));
		    	((MainActivity)getActivity()).setRewardNewpo(data.getStringExtra("rewardNewpo"));
		    	((MainActivity)getActivity()).setPsNewpo(data.getStringExtra("psNewpo"));
		    	//setNewpo(hei);
			}
			break;
		default:
		}
	}
    
    /*//传输task给taskhall的listview
    public Task taskTrans;
    public void setNewpo(Task task){
    	this.taskTrans = task;
    }
    public Task getNewpo(){
    	return taskTrans;
    }*/
    
    OnDataTransmissionListener mListener;
    public interface OnDataTransmissionListener {
    	  public void dataTransmission(String data);
    	}
    public void setOnDataTransmissionListener(OnDataTransmissionListener mListener) {
    	  this.mListener = mListener;
    	}
    
    
    @Override
    public void onResume(){
    	//登录后，状态改变，在该函数中刷新我发布的listview
    	super.onResume();
    	
    	if(((MainActivity)getActivity()).getCurrentLaunch() == 1){
        	//我发布的task的listview
        	
    		TaskAdapter taskAdapter = new TaskAdapter(getActivity(),R.layout.task_item,taskList);
            ListView listView = (ListView)getView().findViewById(R.id.issue_list);
	        listView.setAdapter(taskAdapter);
	        ((MainActivity)getActivity()).setIsNewpo(1);
	        
	      //ListView点击事件
	        listView.setOnItemClickListener(new OnItemClickListener(){
	        	@Override
	        	public void onItemClick(AdapterView<?>parent, View view, 
	        			int position, long id){
	        		Task task = taskList.get(position);
	        		Intent intent = new Intent(getActivity(),com.example.zaizai.activity.DetailActivity.class);
	        		intent.putExtra("headId",task.getImageId());
	        		intent.putExtra("spot", task.getSpot());
	        		intent.putExtra("time", task.getTime());
	        		intent.putExtra("reward", task.getReward());
	        		intent.putExtra("username", task.getUsername());
	        		intent.putExtra("ps", task.getPs());
	        		startActivity(intent);
	        	}
	        });
        }
    	
    }
    
    @Override  
    public void onActivityCreated(Bundle savedInstanceState) {  
        super.onActivityCreated(savedInstanceState); 
        //loginText = (TextView)getActivity().findViewById(R.id.login_text);
        //从其他fragment中获取控件
    }  
      
    @Override  
    public void onPause() {  
        super.onPause();  
    }  
}
