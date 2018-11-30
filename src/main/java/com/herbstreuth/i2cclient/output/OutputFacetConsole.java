package com.herbstreuth.i2cclient.output;

public class OutputFacetConsole implements OutputFacetInterface {
	public int channel;
	public String json;

	public OutputFacetConsole(String json) {
		this.json = json;
	}
	
	public boolean startOutput() {
		System.out.println(this.json);
		return true;
	}
	
	public String getLastError() {
		return"";
	}
	
}