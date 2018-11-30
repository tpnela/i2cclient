package com.herbstreuth.i2cclient.output;

import com.herbstreuth.i2cclient.main.Configuration;

public class OutputFacet {

	private int channel;
	private String json;

	public OutputFacet(int channel, String json) {
		this.channel = channel;
		this.json = json;

	}

	public void startOutput() {

		OutputFacetInterface facet = null;
		
		// Instantiate the right facet

		if (this.channel == Configuration.OUTPUT_CONSOLE) {
			facet = new OutputFacetConsole(json);
		}
		if (this.channel == Configuration.OUTPUT_KAFKA) {
			facet = new OutputFacetKafka(json);
		}
		if (this.channel == Configuration.OUTPUT_MQTT) {
			System.out.println("{\"sorry\":\"not implemented yet\"}");
		}

		// Execute the facet's output process
		if (!facet.startOutput()) {
			System.err.println(facet.getLastError());
		}

	}

}
