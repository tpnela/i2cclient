package com.herbstreuth.i2cclient.dao;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.herbstreuth.i2cclient.dto.LOCALMEMORYdto;
import com.herbstreuth.i2cclient.dto.SensorDtoInterface;

public class LOCALMEMORYdao implements I2CsensorInterface {

	public LOCALMEMORYdao() {
		// Default constructor
	}

	@Override
	public SensorDtoInterface getMeasurements() {

		Runtime runtime = Runtime.getRuntime();

		LOCALMEMORYdto returnDto = new LOCALMEMORYdto();

		returnDto.setAllocatedMemory(runtime.totalMemory());
		returnDto.setFreeMemory(runtime.freeMemory());
		returnDto.setMaxMemory(runtime.maxMemory());
		try {
			returnDto.setHostname(InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			returnDto.setHostname("Couldn't read hostname");
		}

		return returnDto;
	}

}
