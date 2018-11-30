package com.herbstreuth.i2cclient.dto;

import java.beans.Transient;

import com.google.gson.Gson;

public class BME280dto implements SensorDtoInterface {

	
	transient Gson gson = new Gson();

	double temperatureCelsius = 0;
	double temperatureFahrenheit = 0;
	double pressure = 0;
	double humidity = 0;

	String uid;
	Long timestamp;

	public double getTemperatureCelsius() {
		return temperatureCelsius;
	}

	public void setTemperatureCelsius(double temperatureCelsius) {
		this.temperatureCelsius = temperatureCelsius;
	}

	public double getTemperatureFahrenheit() {
		return temperatureFahrenheit;
	}

	public void setTemperatureFahrenheit(double temperatureFahrenheit) {
		this.temperatureFahrenheit = temperatureFahrenheit;
	}

	public double getPressure() {
		return pressure;
	}

	public void setPressure(double pressure) {
		this.pressure = pressure;
	}

	public double getHumidity() {
		return humidity;
	}

	public void setHumidity(double humidity) {
		this.humidity = humidity;
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

	/* (non-Javadoc)
	 * @see com.herbstreuth.i2cclient.dto.SensorDtoInterface#getJson()
	 */
	@Override
	public String getJson() {
		return gson.toJson(this);
	}

}
