package it.unipr.iotlab.iot2024.gatti.server.resources;

import java.util.Random;
import java.util.Timer;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class ChlorineResource extends CoapResource {
	private double chlorine;

	public ChlorineResource(String name) {
		super(name);
		setObservable(true);
		chlorine = 1.0;
		
		Timer timer = new Timer();
		timer.schedule(new UpdateTask(), 0, 1000);
	}
	
	@Override
	public void handleGET(CoapExchange exchange) {
		// respond to the client
		exchange.respond(String.valueOf(chlorine));
	}
	
	@Override
	public void handlePOST(CoapExchange exchange) {
		try {
			chlorine = Double.parseDouble(exchange.getRequestText());
			//System.out.println("Updated chlorine level to " + chlorine);
	        exchange.respond(ResponseCode.CHANGED, "Chlorine level updated to " + chlorine + " ppm");
	        //System.out.println(exchange.getRequestText());
	        changed(); // notify all the observers
		} catch (NumberFormatException e) {
			exchange.respond(ResponseCode.BAD_REQUEST, "Invalid  chlorine value");
			//System.out.println("Responded to POST request with error message.");
		}
	}
	
	private class UpdateTask extends java.util.TimerTask {
		@Override
		public void run() {
			chlorine -= new Random().nextDouble(0.3); //change the chlorine level randomly
			chlorine = Math.round(chlorine * 1000.0) / 1000.0; //round the chlorine level to 3 decimal places
			changed(); // notify all the observers
		}
	}

}
