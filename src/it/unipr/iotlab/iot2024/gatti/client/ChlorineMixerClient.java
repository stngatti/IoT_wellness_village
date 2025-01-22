package it.unipr.iotlab.iot2024.gatti.client;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;

public class ChlorineMixerClient extends CoapClient implements Runnable {
	private String serverUri;
	private double CHLORINE_THRESHOLD = 1.0;
	
	public ChlorineMixerClient(String serverUri) {
		super(serverUri + "/chlorine");
		this.serverUri = serverUri;
	}
	
	public void setChlorineLevel(double chlorine) {
        //set the chlorine level of the chlorine mixer to the desired value
        this.CHLORINE_THRESHOLD = chlorine;
	}
	
	@Override
	public void run() {
		
		while(true) {
			CoapResponse chlorineResponse = get(); //return the chlorine level from sensor
			
			if (chlorineResponse != null) {
				double chlorine = Double.parseDouble(chlorineResponse.getResponseText()); //retrieve the chlorine level from the response
				System.out.println(serverUri + " - Chlorine: " + chlorine + " ppm"); 
				if (chlorine < CHLORINE_THRESHOLD) {
					System.out.println(serverUri + " - Warning: Chlorine level too low. Sending POST message.");
					Double newChlorine = chlorine;
					while(newChlorine <= CHLORINE_THRESHOLD) {
						newChlorine += 1.0; //increase the chlorine level by 1.
						CoapResponse postResponse = post(String.valueOf(newChlorine), 0); //the actuators will handle the chlorine																			
						if (postResponse != null) {
							System.out.println(serverUri + " - POST Response: " + postResponse.getResponseText());
						} else {
							System.err.println(serverUri + " - POST Response Failed (Chlorine)"); //error message if one of the POST response is null
						}
					}
				}
			} else {
				System.err.println(serverUri + " - Failed to get chlorine response."); //error message if the response is null
			} 
			
			// wait 3 seconds before checking again
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}