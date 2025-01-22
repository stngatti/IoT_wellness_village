package it.unipr.iotlab.iot2024.gatti.server;

import org.eclipse.californium.core.CoapServer;

import it.unipr.iotlab.iot2024.gatti.server.resources.TurnstileAction;
import it.unipr.iotlab.iot2024.gatti.server.resources.TurnstileResource;

public class TurnstileServer extends CoapServer {
	
	public TurnstileServer(int port) {
		super(port);
		
		add(new TurnstileResource(TurnstileAction.ENTRY));
		add(new TurnstileResource(TurnstileAction.EXIT));
	}

	public static void main(String[] args) {
		int port = 5686;
		TurnstileServer turnstile = new TurnstileServer(port);
		turnstile.start();
		System.out.println("Turnstile server started on port " + port);	
	}
}