package com.herbstreuth.i2cclient.dao;

import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.UUID;

import com.herbstreuth.i2cclient.dto.BME280dto;
import com.herbstreuth.i2cclient.dto.SensorDtoInterface;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

public class BME280dao implements I2CsensorInterface {

	I2CBus bus = null;
	I2CDevice device = null;

	double temperatureCelsius = 0;
	double temperatureFahrenheit = 0;
	double pressure = 0;
	double humidity = 0;

	public BME280dao(int hexAddress) {
		int intAddress = Integer.parseInt(""+hexAddress,16);
		try {
			this.bus = I2CFactory.getInstance(I2CBus.BUS_1);
			this.device = bus.getDevice(intAddress); 

		} catch (UnsupportedBusNumberException e) {
			System.err.println("Trying to access address [" + intAddress + "] caused the following exception: ["
					+ e.getLocalizedMessage() + "]");
		} catch (IOException e) {
			System.err.println("The following exception was thrown: [" + e.getLocalizedMessage() + "]");
		}

	}

	/* (non-Javadoc)
	 * @see com.herbstreuth.i2cclient.dao.i2cSensorInterface#getMeasurements()
	 */
	@Override
	public SensorDtoInterface getMeasurements() {
		BME280dto returnDto = new BME280dto();

		this.snapshot();

		returnDto.setHumidity(humidity);
		returnDto.setPressure(pressure);
		returnDto.setTemperatureCelsius(temperatureCelsius);
		returnDto.setTemperatureFahrenheit(temperatureFahrenheit);
		returnDto.setTimestamp(new GregorianCalendar().getTimeInMillis());
		returnDto.setUid(UUID.randomUUID().toString());

		return returnDto;

	}

	private void snapshot() {
		// Read 24 bytes of data from address 0x88(136)
		byte[] b1 = new byte[24];
		try {
			this.device.read(0x88, b1, 0, 24);

			// Convert the data
			// temp coefficients
			int dig_T1 = (b1[0] & 0xFF) + ((b1[1] & 0xFF) * 256);
			int dig_T2 = (b1[2] & 0xFF) + ((b1[3] & 0xFF) * 256);
			if (dig_T2 > 32767) {
				dig_T2 -= 65536;
			}
			int dig_T3 = (b1[4] & 0xFF) + ((b1[5] & 0xFF) * 256);
			if (dig_T3 > 32767) {
				dig_T3 -= 65536;
			}

			// pressure coefficients
			int dig_P1 = (b1[6] & 0xFF) + ((b1[7] & 0xFF) * 256);
			int dig_P2 = (b1[8] & 0xFF) + ((b1[9] & 0xFF) * 256);
			if (dig_P2 > 32767) {
				dig_P2 -= 65536;
			}
			int dig_P3 = (b1[10] & 0xFF) + ((b1[11] & 0xFF) * 256);
			if (dig_P3 > 32767) {
				dig_P3 -= 65536;
			}
			int dig_P4 = (b1[12] & 0xFF) + ((b1[13] & 0xFF) * 256);
			if (dig_P4 > 32767) {
				dig_P4 -= 65536;
			}
			int dig_P5 = (b1[14] & 0xFF) + ((b1[15] & 0xFF) * 256);
			if (dig_P5 > 32767) {
				dig_P5 -= 65536;
			}
			int dig_P6 = (b1[16] & 0xFF) + ((b1[17] & 0xFF) * 256);
			if (dig_P6 > 32767) {
				dig_P6 -= 65536;
			}
			int dig_P7 = (b1[18] & 0xFF) + ((b1[19] & 0xFF) * 256);
			if (dig_P7 > 32767) {
				dig_P7 -= 65536;
			}
			int dig_P8 = (b1[20] & 0xFF) + ((b1[21] & 0xFF) * 256);
			if (dig_P8 > 32767) {
				dig_P8 -= 65536;
			}
			int dig_P9 = (b1[22] & 0xFF) + ((b1[23] & 0xFF) * 256);
			if (dig_P9 > 32767) {
				dig_P9 -= 65536;
			}

			// Read 1 byte of data from address 0xA1(161)
			int dig_H1 = ((byte) this.device.read(0xA1) & 0xFF);

			// Read 7 bytes of data from address 0xE1(225)
			this.device.read(0xE1, b1, 0, 7);

			// Convert the data
			// humidity coefficients
			int dig_H2 = (b1[0] & 0xFF) + (b1[1] * 256);
			if (dig_H2 > 32767) {
				dig_H2 -= 65536;
			}
			int dig_H3 = b1[2] & 0xFF;
			int dig_H4 = ((b1[3] & 0xFF) * 16) + (b1[4] & 0xF);
			if (dig_H4 > 32767) {
				dig_H4 -= 65536;
			}
			int dig_H5 = ((b1[4] & 0xFF) / 16) + ((b1[5] & 0xFF) * 16);
			if (dig_H5 > 32767) {
				dig_H5 -= 65536;
			}
			int dig_H6 = b1[6] & 0xFF;
			if (dig_H6 > 127) {
				dig_H6 -= 256;
			}

			// Select control humidity register
			// Humidity over sampling rate = 1
			this.device.write(0xF2, (byte) 0x01);
			// Select control measurement register
			// Normal mode, temp and pressure over sampling rate = 1
			this.device.write(0xF4, (byte) 0x27);
			// Select config register
			// Stand_by time = 1000 ms
			this.device.write(0xF5, (byte) 0xA0);

			// Read 8 bytes of data from address 0xF7(247)
			// pressure msb1, pressure msb, pressure lsb, temp msb1, temp msb, temp lsb,
			// humidity lsb, humidity msb
			byte[] data = new byte[8];
			this.device.read(0xF7, data, 0, 8);

			// Convert pressure and temperature data to 19-bits
			long adc_p = (((long) (data[0] & 0xFF) * 65536) + ((long) (data[1] & 0xFF) * 256) + (long) (data[2] & 0xF0))
					/ 16;
			long adc_t = (((long) (data[3] & 0xFF) * 65536) + ((long) (data[4] & 0xFF) * 256) + (long) (data[5] & 0xF0))
					/ 16;
			// Convert the humidity data
			long adc_h = ((long) (data[6] & 0xFF) * 256 + (long) (data[7] & 0xFF));

			// Temperature offset calculations
			double var1 = (((double) adc_t) / 16384.0 - ((double) dig_T1) / 1024.0) * ((double) dig_T2);
			double var2 = ((((double) adc_t) / 131072.0 - ((double) dig_T1) / 8192.0)
					* (((double) adc_t) / 131072.0 - ((double) dig_T1) / 8192.0)) * ((double) dig_T3);
			double t_fine = (long) (var1 + var2);
			this.temperatureCelsius = (var1 + var2) / 5120.0;
			this.temperatureFahrenheit = this.temperatureCelsius * 1.8 + 32;

			// Pressure offset calculations
			var1 = ((double) t_fine / 2.0) - 64000.0;
			var2 = var1 * var1 * ((double) dig_P6) / 32768.0;
			var2 = var2 + var1 * ((double) dig_P5) * 2.0;
			var2 = (var2 / 4.0) + (((double) dig_P4) * 65536.0);
			var1 = (((double) dig_P3) * var1 * var1 / 524288.0 + ((double) dig_P2) * var1) / 524288.0;
			var1 = (1.0 + var1 / 32768.0) * ((double) dig_P1);
			double p = 1048576.0 - (double) adc_p;
			p = (p - (var2 / 4096.0)) * 6250.0 / var1;
			var1 = ((double) dig_P9) * p * p / 2147483648.0;
			var2 = p * ((double) dig_P8) / 32768.0;
			this.pressure = (p + (var1 + var2 + ((double) dig_P7)) / 16.0) / 100;

			// Humidity offset calculations
			double var_H = (((double) t_fine) - 76800.0);
			var_H = (adc_h - (dig_H4 * 64.0 + dig_H5 / 16384.0 * var_H))
					* (dig_H2 / 65536.0 * (1.0 + dig_H6 / 67108864.0 * var_H * (1.0 + dig_H3 / 67108864.0 * var_H)));
			this.humidity = var_H * (1.0 - dig_H1 * var_H / 524288.0);
			if (this.humidity > 100.0) {
				this.humidity = 100.0;
			} else if (this.humidity < 0.0) {
				this.humidity = 0.0;
			}

		} catch (IOException e) {
			System.err.println("When trying to read values from the sensor, the following exception was thrown: ["
					+ e.getLocalizedMessage() + "]");
		}
	}

}
