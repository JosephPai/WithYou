package com.example.zaizai;

public class Task {
	
	private String spot, time, reward, username, ps;
	private int imageId;
	
	
	
	public Task(String username,String spot, String time, String reward, 
			String ps, int imageId) {
		super();
		this.spot = spot;
		this.time = time;
		this.reward = reward;
		this.username = username;
		this.ps = ps;
		this.imageId = imageId;
	}



	public String getUsername() {
		return username;
	}



	public String getPs() {
		return ps;
	}

	
	public String getSpot() {
		return spot;
	}


	public String getTime() {
		return time;
	}

	public String getReward() {
		return reward;
	}

	public int getImageId() {
		return imageId;
	}
	
}
