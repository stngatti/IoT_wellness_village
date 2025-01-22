package it.unipr.iotlab.iot2024.gatti.server.resources;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

import it.unipr.iotlab.iot2024.gatti.server.Repository;

public class TurnstileResource extends CoapResource {
	private TurnstileAction action; // entry or exit
	
	private Repository register;
	private String bossId = "Boss";
	
	public TurnstileResource(TurnstileAction action) {
		super(action.toString().toLowerCase());
		this.action = action;
		setObservable(true);
		getAttributes().setObservable();

		register = Repository.getInstance();
	}
	
	@Override
	public void handlePOST(CoapExchange exchange) {
		String visitorId = exchange.getRequestText();
		if (visitorId == null || visitorId.isEmpty()) {
			exchange.respond(ResponseCode.BAD_REQUEST, "Invalid visitor id");
			System.err.println("Invalid visitor id");
			return;
		}
		if (action == TurnstileAction.ENTRY) {
			if (register.addVisitor(visitorId) || visitorId.equals(bossId)) {
				exchange.respond("Visitor " + visitorId + " has entered.");
				System.out.println("Visitor " + visitorId + " has entered.");
				register.addVisitor(visitorId);
				changed(); // notify all the observers
			} else {
				exchange.respond(ResponseCode.BAD_REQUEST, "Visitor already entered.");
				System.err.println("Visitor already entered.");
			}
		} else {
			if (register.removeVisitor(visitorId) || visitorId.equals(bossId)) {
				exchange.respond("Visitor " + visitorId + " has exited.");
				System.out.println("Visitor " + visitorId + " has exited.");
				register.removeVisitor(visitorId);
				changed(); // notify all the observers
			} else {
				exchange.respond(ResponseCode.BAD_REQUEST, "Visitor is not registered.");
				System.err.println("Visitor is not registered.");
			}
		}
	}
	
	@Override
	public void handleGET(CoapExchange exchange) {
		exchange.respond(String.valueOf(register.getVisitorsCount())); // return the number of visitors in the register
	}

}
