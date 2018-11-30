package com.herbstreuth.i2cclient.dto;

import com.google.gson.Gson;

public class LIS3DHdto  implements SensorDtoInterface {
	
	transient Gson gson = new Gson();
	
	double xAngle = 0.0;
	double yAngle = 0.0;

	int x = 0;
	int y = 0;
	int z = 0;
	
	String uid;
	Long timestamp;
	
	
	public double getxAngle() {
		return xAngle;
	}
	public void setxAngle(double xAngle) {
		this.xAngle = xAngle;
	}
	public double getyAngle() {
		return yAngle;
	}
	public void setyAngle(double yAngle) {
		this.yAngle = yAngle;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getZ() {
		return z;
	}
	public void setZ(int z) {
		this.z = z;
	}
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	
	public String getJson() {
		return gson.toJson(this);
	}
	
	

}
