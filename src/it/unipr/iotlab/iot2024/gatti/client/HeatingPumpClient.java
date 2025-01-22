package it.unipr.iotlab.iot2024.gatti.client;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;

public class HeatingPumpClient extends CoapClient implements Runnable {
	double TEMP_THRESHOLD = 25.0; //default temperature threshold
	String serverUri;
	
	public HeatingPumpClient(String serverUri) {
		super(serverUri+"/temperature");
		this.serverUri = serverUri; //the address of the server
	}
	
	public void setTemperatureLevel(double temperature) {
		this.TEMP_THRESHOLD = temperature; //set the temperature threshold
	}
	
	public void run() {
		while(true) {
			CoapResponse temperatureResponse = get(); //get the temperature from the sensor
			if (temperatureResponse != null) {
				double temperature = Double.parseDouble(temperatureResponse.getResponseText()); //retrieve the temperature from the response
				System.out.println(serverUri + " - Temperature: " + temperature + "°C");		
				if (temperature < TEMP_THRESHOLD) {
					System.out.println(serverUri + " - Warning: Temperature too low. Sending POST message.");
					Double newTemperature = temperature; //copy the temperature to a new variable to avoid changing the original value in the cycle below
					while (newTemperature <= TEMP_THRESHOLD) {
						newTemperature += 1.0; // increase the temperature by 1.
						CoapResponse postResponse = post(String.valueOf(newTemperature), 0); 
						if (postResponse != null) {
							System.out.println(serverUri + " - POST Response: " + postResponse.getResponseText());
						} else {
							System.err.println(serverUri + " - POST Response Failed (Temperature)"); // error message if it fails																			
						}
					}
				}
			} else {
				System.err.println(serverUri + " - Failed to get temperature response."); //error message if the response is null
			}
			
			// wait 2 seconds before checking again
			try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
		}
	}
}