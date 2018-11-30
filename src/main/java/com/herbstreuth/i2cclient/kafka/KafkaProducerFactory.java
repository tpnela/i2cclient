package com.herbstreuth.i2cclient.kafka;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import com.herbstreuth.i2cclient.main.Configuration;

public class KafkaProducerFactory {
	public String kafkaBrokers = Configuration.getInstance().getKafka_brokers();
	public String clientId = Configuration.getInstance().getKafka_client_id();
	private KafkaProducer<Long, String> producerInstance = null;

	public KafkaProducerFactory() {


	}

	public Producer<Long, String> getProducerInstance() {
		if (this.producerInstance == null) {
			Properties props = new Properties();
			props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, this.getKafkaBrokers());
			props.put(ProducerConfig.CLIENT_ID_CONFIG, this.getClientId());
			props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
			props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
			this.producerInstance = new KafkaProducer<>(props);
		}

		return this.producerInstance;
	}

	public synchronized String getKafkaBrokers() {
		return kafkaBrokers;
	}

	public synchronized void setKafkaBrokers(String kafkaBrokers) throws IllegalStateException {
		if (this.producerInstance != null) {
			throw new IllegalStateException(
					"Cannot change producer configuration once created. Run resetState() first.");
		}
		this.kafkaBrokers = kafkaBrokers;
	}

	public synchronized String getClientId() {
		return clientId;
	}

	public synchronized void setClientId(String clientId) throws IllegalStateException {
		if (this.producerInstance != null) {
			throw new IllegalStateException(
					"Cannot change producer configuration once created. Run resetState() first.");
		}
		this.clientId = clientId;
	}

	public void resetState() {
		this.producerInstance = null;
	}

}
