package it.unipr.iotlab.iot2024.gatti.server.resources;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class TemperatureResource extends CoapResource {
	private double temperature;

	public TemperatureResource(String name) {
		super(name);
		setObservable(true);
		getAttributes().setObservable();
		
		temperature = 28.0;
		
		Timer timer = new Timer();
		timer.schedule(new UpdateTask(), 0, 1000); //update the temperature every second
	}
	
	@Override
	public void handleGET(CoapExchange exchange) {
		exchange.respond(String.valueOf(temperature)); //respond to the client with the current temperature
	}
	
	@Override
	public void handlePOST(CoapExchange exchange) {
		try {
			double newValue = Double.parseDouble(exchange.getRequestText());
			temperature = newValue; //update the temperature
			exchange.respond(ResponseCode.CHANGED, "Temperature updated to " + temperature + "°C");
			changed(); // notify all the observers			
		} catch (NumberFormatException e) {
			exchange.respond(ResponseCode.BAD_REQUEST, "Invalid number");
		}
	}
	
	private class UpdateTask extends TimerTask{
		@Override
		public void run() {
            temperature -= new Random().nextDouble(3.0); //increase or decrease the temperature randomly
            temperature = Math.round(temperature * 100.0) / 100.0; //round the temperature to 2 decimal places
            changed(); // notify all the observers
		}
	}
		
}
