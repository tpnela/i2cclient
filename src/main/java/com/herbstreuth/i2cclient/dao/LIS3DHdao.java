package com.herbstreuth.i2cclient.dao;

import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.UUID;

import com.herbstreuth.i2cclient.dto.LIS3DHdto;

/* Controleverything.com - LIS3DH
 
 https://www.controleverything.com/products
 
 Firmware v1.0 - java
 Author: Yadwinder Singh
 
 Wide supply voltage, 1.71 V to 3.6 V
 Independent IOs supply (1.8 V) and supply voltage compatible
 Ultra low-power mode consumption down to 2 microA
 +- (2g/4g/8g/16g) dynamically selectable fullscale
 I2C/SPI digital output interface
 16 bit data output
 2 independent programmable interrupt generators for free-fall and motion detection
 6D/4D orientation detection
 Free-fall detection
 
 Hardware version - Rev A.
 Platform : Raspberry pi
 
 Project uses pi4j library.
 Please follow a detailed tutorial to install pi4j here.
 
 http://pi4j.com/install.html
 
 Compile the java program with command pi4j Filename.java
 Run it with pi4j Filename
 
 */

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

public class LIS3DHdao implements I2CsensorInterface {

	public final static int LIS3DH_ADDRESS = 0x18;

	public final static int LIS3DHTR_CTRL_REG1_A = 0x20;
	public final static int LIS3DHTR_CTRL_REG2_A = 0x21;
	public final static int LIS3DHTR_CTRL_REG3_A = 0x22;
	public final static int LIS3DHTR_CTRL_REG4_A = 0x23;
	public final static int LIS3DHTR_CTRL_REG5_A = 0x24;

	byte[] b = new byte[6];
	int result = 0;

	double xAngle = 0.0;
	double yAngle = 0.0;

	int x = 0;
	int y = 0;
	int z = 0;

	private I2CBus bus;
	private I2CDevice device;

	public LIS3DHdao(int hexAddress) {
		try {
			// Get i2c bus
			bus = I2CFactory.getInstance(I2CBus.BUS_1); // Depends onthe RasPI version
			// Get device itself
			int intAddress = Integer.parseInt(""+hexAddress,16);
			device = bus.getDevice(intAddress);
			this.setConfig();

		} catch (IOException | UnsupportedBusNumberException e) {
			System.err.println("When setting up the connection to the device following exception was thrown: ["
					+ e.getLocalizedMessage() + "]");
		}
	}

	private void setConfig() {

		// initAccel -- Sets up the accelerometer to begin reading.
		// Normal operation mode, all axes enabled.
		// 10 Hz ODR Data Rate

		try

		{
			device.write(LIS3DHTR_CTRL_REG1_A, (byte) 0x27);
			device.write(LIS3DHTR_CTRL_REG2_A, (byte) 0x00);
			device.write(LIS3DHTR_CTRL_REG3_A, (byte) 0x00);
			device.write(LIS3DHTR_CTRL_REG4_A, (byte) 0x00);
			device.write(LIS3DHTR_CTRL_REG5_A, (byte) 0x00);

		} catch (Exception e) {
			System.err.println("When configuring the device the following exception was thrown: ["
					+ e.getLocalizedMessage() + "]");
		}

	}

	public LIS3DHdto getMeasurements() {

		LIS3DHdto returnDto = new LIS3DHdto();

		this.snapshot();

		returnDto.setX(x);
		returnDto.setY(y);
		returnDto.setZ(z);
		returnDto.setxAngle(xAngle);
		returnDto.setyAngle(yAngle);
		returnDto.setTimestamp(new GregorianCalendar().getTimeInMillis());
		returnDto.setUid(UUID.randomUUID().toString());

		return returnDto;

	}

	private void snapshot() {
		try {
			// lis3dh.read(0x28,b,0,6);

			b[0] = (byte) device.read(0x28);
			b[1] = (byte) device.read(0x29);
			b[2] = (byte) device.read(0x2A);
			b[3] = (byte) device.read(0x2B);
			b[4] = (byte) device.read(0x2C);
			b[5] = (byte) device.read(0x2D);

			this.x = (b[1] << 8) | (b[0] & 0xFF);
			this.y = (b[3] << 8) | (b[2] & 0xFF);
			this.z = (b[5] << 8) | (b[4] & 0xFF);
			
		} catch (Exception e) {
			System.err.println("When measuring the following exception was thrown: [" + e.getLocalizedMessage() + "]");
		}

		// Convert Acclelerometer values to degrees

		xAngle = (Math.atan2(y, z) + Math.PI) * 57.3;
		yAngle = (Math.atan2(z, x) + Math.PI) * 57.3;

	}

}
