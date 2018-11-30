package com.herbstreuth.i2cclient.output;

import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.herbstreuth.i2cclient.kafka.KafkaProducerFactory;
import com.herbstreuth.i2cclient.main.Configuration;

public class OutputFacetKafka implements OutputFacetInterface {
	public int channel;
	public String json;
	String lastError = "";

	public OutputFacetKafka(String json) {
		this.json = json;
	}

	public boolean startOutput() {
		KafkaProducerFactory producerFactory = new KafkaProducerFactory();
		Producer<Long, String> kafkaProducer = producerFactory.getProducerInstance();
		

		long time = System.currentTimeMillis();

		try {

			final ProducerRecord<Long, String> record = new ProducerRecord<>(
					Configuration.getInstance().getKafka_topic(), time, this.json);

			kafkaProducer.send(record).get();

		} catch (InterruptedException e) {
			this.lastError += e.getMessage();
			
		} catch (ExecutionException e) {
			this.lastError += e.getMessage();
			
		} finally {
			kafkaProducer.flush();
			kafkaProducer.close();
		}

		if (this.lastError.length() != 0) {
			return false;
		} else {
			return true;
		}

	}

	public String getLastError() {
		return this.lastError;
	}

}