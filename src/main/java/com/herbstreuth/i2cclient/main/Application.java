/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.herbstreuth.i2cclient.main;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;

import com.google.gson.Gson;
import com.herbstreuth.i2cclient.dao.BME280dao;
import com.herbstreuth.i2cclient.dao.I2CsensorInterface;
import com.herbstreuth.i2cclient.dao.LIS3DHdao;
import com.herbstreuth.i2cclient.dao.LOCALMEMORYdao;
import com.herbstreuth.i2cclient.dto.SensorDtoInterface;
import com.herbstreuth.i2cclient.output.OutputFacet;

/*
 * Pressure, Altitude, Temperature, Humidity Adapted from
 * https://github.com/adafruit/Adafruit_Python_BME280
 */
public class Application {

	static Application app = null;

	LIS3DHdao lis3dh = null;
	Gson gson = new Gson();

	public static void main(String args[]) throws Exception {

		app = new Application(args);
		app.run();
	}

	public Application(String args[]) {

		if (!initConfiguration(args)) {
			System.err.println("Faulty configuration");
			System.exit(1);
		}
	}

	private boolean initConfiguration(String[] args) {
		boolean initSuccess = true;
		Configuration conf = Configuration.getInstance();

		try { // we might either run into an Arrayindex exception here or leave this check
				// block with an exception

			// sensortype
			for (int i = 0; i < args.length; i++) {
				if (args[i].toLowerCase().equals("--sensortype")) {
					if (args[i + 1].toLowerCase().equals("bme280")) {
						conf.setSensortype(Configuration.SENSOR_BME280);
					} else if (args[i + 1].toLowerCase().equals("lis3dh")) {
						conf.setSensortype(Configuration.SENSOR_LIS3DH);
					} else if (args[i + 1].toLowerCase().equals("localmemory")) {
						conf.setSensortype(Configuration.SENSOR_LOCALMEMORY);
					} else {
						throw new Exception("Unknown sensor [" + args[i + 1] + "]");
					}
				}
			}

			// i2caddress
			int i2caddress = 0;
			for (int i = 0; i < args.length; i++) {
				if (args[i].toLowerCase().equals("--i2caddress")) {
					try {
						i2caddress = Integer.parseInt(args[i + 1]);
					} catch (NumberFormatException e) {
						throw new Exception(
								"Invalid address [" + args[i + 1] + "] must be integer with valid i2c address.");
					}
				}
				conf.setI2caddress(i2caddress);
			}
			// output
			ArrayList<Integer> outputchannels = new ArrayList<>();
			for (int i = 0; i < args.length; i++) {

				if (args[i].toLowerCase().equals("--output")) {
					String[] channels = args[i + 1].split(",");
					for (int j = 0; j < channels.length; j++) {
						if (channels[j].toLowerCase().equals("console")) {
							outputchannels.add(Configuration.OUTPUT_CONSOLE);
						}
						if (channels[j].toLowerCase().equals("kafka")) {
							outputchannels.add(Configuration.OUTPUT_KAFKA);
						}
						if (channels[j].toLowerCase().equals("mqtt")) {
							outputchannels.add(Configuration.OUTPUT_MQTT);
						}
					}

				}

			}
			if (outputchannels.size() > 0) {
				conf.setOutputchannels(outputchannels);
			} else {
				throw new Exception("No valid outputchannels have been set");
			}

			// sampledelay
			int sampledelay = 500;
			for (int i = 0; i < args.length; i++) {
				if (args[i].toLowerCase().equals("--sampledelay")) {
					try {
						sampledelay = Integer.parseInt(args[i + 1]);
					} catch (NumberFormatException e) {
						throw new Exception("Invalid sampledelay [" + args[i + 1] + "] must be integer.");
					}
				}
				conf.setSampledelay(sampledelay);
			}

			// terminate_after
			int terminate_after = 10;
			for (int i = 0; i < args.length; i++) {
				if (args[i].toLowerCase().equals("--terminate_after")) {
					try {
						terminate_after = Integer.parseInt(args[i + 1]);
					} catch (NumberFormatException e) {
						throw new Exception("Invalid terminate_after [" + args[i + 1] + "] must be integer.");
					}
				}
				conf.setTerminate_after(terminate_after);
			}

			// kafka_brokers
			String kafka_brokers = conf.getKafka_brokers();
			for (int i = 0; i < args.length; i++) {
				if (args[i].toLowerCase().equals("--kafka_brokers")) {
					kafka_brokers = args[i + 1];
					conf.setKafka_brokers(kafka_brokers);
				}
			}

			// kafka_client_id
			String kafka_client_id = conf.getKafka_client_id();
			for (int i = 0; i < args.length; i++) {
				if (args[i].toLowerCase().equals("--kafka_client_id")) {
					kafka_client_id = args[i + 1];
					conf.setKafka_client_id(kafka_client_id);
				}
			}

			// kafka_topic
			String kafka_topic = conf.getKafka_topic();
			for (int i = 0; i < args.length; i++) {
				if (args[i].toLowerCase().equals("--kafka_topic")) {
					kafka_topic = args[i + 1];
					conf.setKafka_client_id(kafka_topic);
				}
			}

			// verbose
			boolean verbose = conf.isVerbose();
			for (int i = 0; i < args.length; i++) {

				if (args[i].toLowerCase().equals("--verbose")) {
					if (args[i + 1].toLowerCase().equals("true")) {
						verbose = true;
					} else if (args[i + 1].toLowerCase().equals("false")) {
						verbose = false;
					} else {

						throw new Exception("Invalid value for verbose [" + args[i + 1] + "] must be true|false.");
					}
				}
				conf.setVerbose(verbose);
			}
			
			// sanity check output channels
			if (conf.getOutputchannels().size() == 0) {
				throw new Exception("You need to give at least one output channel. See --output");
			}
			
			// sanity check i2c address
			if (conf.getI2caddress() <= 0 | conf.getI2caddress() >= 0x77 ) {
				throw new Exception("Invalid or missing i2c address. See --i2caddress.");
			}
			
			// sanity check i2c address
			if (conf.getSensortype() == 0 ) {
				throw new Exception("Missing sensortype. See --i2caddress. See --sensortype");
			}
			
			
			

			this.printToConsole("Configuration:");
			this.printToConsole("---------------------");
			this.printToConsole("i2caddress: [" + conf.getI2caddress() + "]");
			this.printToConsole("sampledelay: [" + conf.getSampledelay() + "]");
			this.printToConsole("sensortype: [" + conf.getSensortype() + "]");
			this.printToConsole("terminate_after: [" + conf.getTerminate_after() + "]");
			for (Iterator<Integer> iterator = outputchannels.iterator(); iterator.hasNext();) {
				int channeltype = ((Integer) iterator.next()).intValue();
				if (channeltype == Configuration.OUTPUT_CONSOLE) {
					this.printToConsole("output to: [console]");
				}
				if (channeltype == Configuration.OUTPUT_KAFKA) {
					this.printToConsole("output to: [kafka]");
					this.printToConsole("kafka_brokers: [" + conf.getKafka_brokers() + "]");
					this.printToConsole("kafka_client: [" + conf.getKafka_client_id() + "]");
					this.printToConsole("kafka_topic: [" + conf.getKafka_topic() + "]");
				}
				if (channeltype == Configuration.OUTPUT_MQTT) {
					this.printToConsole("output to: [mqtt]");
				}

			}

		} catch (

		Exception e) {
			System.err.println(e.getMessage());
			printUsage(args);
			initSuccess = false;
		}

		return initSuccess;
	}

	public void printToConsole(String msg) {
		if (Configuration.getInstance().isVerbose()) {
			System.out.println(msg);
		}
	}

	private void printUsage(String[] args) {
		StringBuilder message = new StringBuilder();
		message.append("\n");
		message.append("\n");
		message.append("ERR - Invalid arguments" + "\n");
		for (int i = 0; i < args.length; i++) {
			message.append("[" + i + "] = [" + args[i] + "]" + "\n");
		}
		message.append("--------------------------" + "\n");
		message.append("Call syntax is:" + "\n");
		message.append("--sensortype BME280|LIS3DH|LOCALMEMORY" + "\n");
		message.append("--i2caddress <int>" + "\n");
		message.append("--verbose true|false (defaults to false)" + "\n");
		message.append("--sampledelay <int> Delay in millseconds between samples (defaults to 500)" + "\n");
		message.append(
				"--terminate_after <int> Client terminates after this time (in seconds) (defaults to 10)" + "\n");
		message.append(
				"--output comma separated list of at least one from: CONSOLE|KAFKA|MQTT i.e. CONSOLE,KAFKA or MQTT,KAFKA or ..."
						+ "\n");

		message.append("\n");
		message.append("If you chose output to KAFKA" + "\n");
		message.append("--------------------------------------" + "\n");
		message.append(
				"    --kafka_brokers comma separated list of host:ports the messages are to be sent to. Defaults to localhost:9092"
						+ "\n");
		message.append(
				"    --kafka_client_id Identifying string for this client. Defaults to <hostname>_i2cclient" + "\n");
		message.append("    --kafka_topic Topic to publish the message to. Defaults to i2ctopic" + "\n");

		System.out.println(message.toString());
	}

	private void run() {

		long startTime = new GregorianCalendar().getTimeInMillis();
		long endTime = startTime + Configuration.getInstance().getTerminate_after() * 1000;

		I2CsensorInterface sensor = null;

		if (Configuration.getInstance().getSensortype() == Configuration.SENSOR_BME280) {
			sensor = (I2CsensorInterface) new BME280dao(Configuration.getInstance().getI2caddress());
		}

		if (Configuration.getInstance().getSensortype() == Configuration.SENSOR_LIS3DH) {
			sensor = (I2CsensorInterface) new LIS3DHdao(Configuration.getInstance().getI2caddress());
		}

		if (Configuration.getInstance().getSensortype() == Configuration.SENSOR_LOCALMEMORY) {
			sensor = (I2CsensorInterface) new LOCALMEMORYdao();
		}

		boolean keepRunning = true;
		while (keepRunning) {
			SensorDtoInterface dto = sensor.getMeasurements();

			// Run the selected output facets

			// TODO: Make this multithreaded
			for (Iterator<Integer> iterator = Configuration.getInstance().getOutputchannels().iterator(); iterator
					.hasNext();) {
				int channel = (int) iterator.next();
				OutputFacet facet = new OutputFacet(channel, dto.getJson());
				facet.startOutput();

			}

			// Apply delay
			try {
				Thread.sleep(Configuration.getInstance().getSampledelay());
			} catch (InterruptedException e) {
				// Unlikely...
				e.printStackTrace();
			}

			// Check whether it is time to terminate
			if (new GregorianCalendar().getTimeInMillis() > endTime) {
				System.exit(0);
			}

		}

	}
}
