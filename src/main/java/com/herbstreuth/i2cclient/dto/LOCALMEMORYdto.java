package com.herbstreuth.i2cclient.dto;

import com.google.gson.Gson;

public class LOCALMEMORYdto implements SensorDtoInterface {
	
	long maxMemory = 0;
	long allocatedMemory = 0;
	long freeMemory = 0;
	String hostname = "";
	
	transient Gson gson = new Gson();

	public synchronized long getMaxMemory() {
		return maxMemory;
	}

	public synchronized void setMaxMemory(long maxMemory) {
		this.maxMemory = maxMemory;
	}

	public synchronized long getAllocatedMemory() {
		return allocatedMemory;
	}

	public synchronized void setAllocatedMemory(long allocatedMemory) {
		this.allocatedMemory = allocatedMemory;
	}

	public synchronized long getFreeMemory() {
		return freeMemory;
	}

	public synchronized void setFreeMemory(long freeMemory) {
		this.freeMemory = freeMemory;
	}

	public synchronized String getHostname() {
		return hostname;
	}

	public synchronized void setHostname(String hostname) {
		this.hostname = hostname;
	}

	@Override
	public String getJson() {
		return gson.toJson(this);
	}

}
