package com.herbstreuth.i2cclient.main;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Configuration {

	static Configuration instance = null;

	/**
	 * Temperature sensor based on BOSCH BME280
	 */
	public static final int SENSOR_BME280 = 1;
	/**
	 * Acceleration sensor based on LIS3DH
	 */
	public static final int SENSOR_LIS3DH = 2;

	/**
	 * Local memory on the machine that executes the jar
	 */
	public static final int SENSOR_LOCALMEMORY = 3;

	/**
	 * Output resulting JSON to console
	 */

	public static final int OUTPUT_CONSOLE = 1;

	/**
	 * Output resulting JSON to a Kafka topic
	 */
	public static final int OUTPUT_KAFKA = 2;
	/**
	 * Output resulting JSON to an MQTT topic
	 */
	public static final int OUTPUT_MQTT = 3;

	// Member vars
	/**
	 * Address for i2c sensor (No default)
	 */
	private int i2caddress = 0;
	/**
	 * Sensortype as String, see suffixes of constants SENSOR_*, i.e. BME_280
	 * triggers use of SENSOR_BME280 (No default)
	 */
	private int sensortype = 0;
	/**
	 * Write details of application's activity to console (Defaults to false)
	 */
	private boolean verbose = false;
	/**
	 * Delay in milliseconds between samples (Defaults to 500)
	 */
	private int sampledelay = 0;
	/**
	 * Automatic termination of client after n seconds (Defaults to 10)
	 */
	private int terminate_after = 0;

	/**
	 * List of ids for the outputchannels to be used. Values from OUTPUT_* constants
	 */
	private ArrayList<Integer> outputchannels = null;

	/**
	 * String with comma separated host:port identifiers for kafka brokeres to be
	 * used as destination for producer defaults to localhost:9092
	 */
	private String kafka_brokers = "localhost:9092";
	
	/**
	 * Identifying string for this client. Defaults to {hostname}_i2cclient
	 */
	private String kafka_client_id; // Default will be set dynamically in constructor
	/**
	 * Destination topic for Kafka messages. Defaults to "i2ctopic"
	 */
	private String kafka_topic = "i2ctopic";

	private Configuration() {
		// private instantiation, as this is a Singleton class
		String hostname;
		try {
			hostname = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			hostname = "UNKNOWN_HOST";
		}

		this.kafka_client_id = hostname + "_i2cclient";
	}

	public synchronized static Configuration getInstance() {
		if (instance == null) {
			instance = new Configuration();
		}

		return instance;
	}

	public synchronized int getI2caddress() {
		return i2caddress;
	}

	public synchronized void setI2caddress(int i2caddress) {
		this.i2caddress = i2caddress;
	}

	public synchronized int getSensortype() {
		return sensortype;
	}

	public synchronized void setSensortype(int sensortype) {
		this.sensortype = sensortype;
	}

	public synchronized boolean isVerbose() {
		return verbose;
	}

	public synchronized void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public synchronized int getSampledelay() {
		return sampledelay;
	}

	public synchronized void setSampledelay(int sampledelay) {
		this.sampledelay = sampledelay;
	}

	public synchronized int getTerminate_after() {
		return terminate_after;
	}

	public synchronized void setTerminate_after(int terminate_after) {
		this.terminate_after = terminate_after;
	}

	public synchronized ArrayList<Integer> getOutputchannels() {
		return outputchannels;
	}

	public synchronized void setOutputchannels(ArrayList<Integer> outputchannels) {
		this.outputchannels = outputchannels;
	}

	public synchronized String getKafka_brokers() {
		return kafka_brokers;
	}

	public synchronized void setKafka_brokers(String kafka_brokers) {
		this.kafka_brokers = kafka_brokers;
	}

	public synchronized String getKafka_client_id() {
		return kafka_client_id;
	}

	public synchronized void setKafka_client_id(String kafka_client_id) {
		this.kafka_client_id = kafka_client_id;
	}

	public synchronized String getKafka_topic() {
		return kafka_topic;
	}

	public synchronized void setKafka_topic(String kafka_topic) {
		this.kafka_topic = kafka_topic;
	}
	
	

}
