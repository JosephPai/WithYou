package com.example.zaizai.activity;

import com.example.zaizai.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class NewpoActivity extends Activity implements OnClickListener {
	
	private Button post, cancelPost;
	private EditText rewardNewpo, psNewpo, spotNewpo;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.newpo_layout);
		post = (Button)findViewById(R.id.post);
		cancelPost = (Button)findViewById(R.id.cancle_post);
		rewardNewpo = (EditText)findViewById(R.id.reward_newpo);
		psNewpo = (EditText)findViewById(R.id.ps_newpo);
		spotNewpo = (EditText)findViewById(R.id.spot_newpo);
		post.setOnClickListener(this);
		cancelPost.setOnClickListener(this);
	}
	
	public void onClick(View v){
		switch(v.getId()){
		case R.id.post:
			AlertDialog.Builder dialog = new AlertDialog.Builder
			(NewpoActivity.this);
			dialog.setTitle("确定要发布吗？");
			dialog.setMessage("该操作将消耗"+rewardNewpo.getText().toString()+"元");
			dialog.setCancelable(true);
			dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//确定发布的事件
					Toast.makeText(getBaseContext(), "你居然真的确定了哦！",
							Toast.LENGTH_SHORT).show();
					
					//回传数据，发布成功
					Intent intent = new Intent();
					intent.putExtra("spotNewpo", spotNewpo.getText().toString());
					intent.putExtra("psNewpo", psNewpo.getText().toString());
					intent.putExtra("rewardNewpo", rewardNewpo.getText().toString());
					setResult(RESULT_OK,intent);
					finish();
				}
			});
			dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			});
			dialog.show();
			break;
		case R.id.cancle_post:
			finish();
			break;
		default:
			break;
		}
	}
}
