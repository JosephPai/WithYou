package com.example.zaizai.adapter;

import java.util.List;

import com.example.zaizai.R;
import com.example.zaizai.Task;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TaskAdapter extends ArrayAdapter<Task> {
	
	private int resourceId;

	public TaskAdapter(Context context, int textViewResourceId, 
			List<Task> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
		resourceId = textViewResourceId;
	}
	
	@Override 
	public View getView(int position, View convertView, ViewGroup parent) {
		Task task = getItem(position);
		View view=LayoutInflater.from(getContext()).inflate(resourceId,null); 
		
		ImageView taskImage = (ImageView)view.findViewById(R.id.task_image);
		TextView taskSpot = (TextView)view.findViewById(R.id.task_spot);
		TextView taskTime = (TextView)view.findViewById(R.id.task_time);
		TextView taskReward = (TextView)view.findViewById(R.id.task_reward);
		
		taskImage.setImageResource(task.getImageId());
		taskSpot.setText(task.getSpot());
		taskTime.setText(task.getTime());
		taskReward.setText(task.getReward());
		
		return view;
	}

}
