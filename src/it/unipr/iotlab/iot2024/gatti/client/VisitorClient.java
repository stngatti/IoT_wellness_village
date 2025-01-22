package it.unipr.iotlab.iot2024.gatti.client;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;

import java.util.Scanner;

public class VisitorClient {
    private static String address = "coap://localhost:5686";
    private String id;

    // Constructor to assign visitor ID
    public VisitorClient(String id) {
        this.id = id;
    }
 
    public void enter() {
        CoapClient entranceClient = new CoapClient(address + "/entry");
        CoapResponse response = entranceClient.post(id, 0);
		if (response.getCode() == ResponseCode.BAD_REQUEST || response == null) {
			System.err.println("Failed to enter visitor " + id);
		} else {
			System.out.println("Visitor " + id + " entered.");
		}
        entranceClient.shutdown();
    }

    public void exit() {
        CoapClient exitClient = new CoapClient(address + "/exit");
        CoapResponse response = exitClient.post(id, 0);
        if (response.getCode() == ResponseCode.BAD_REQUEST || response == null) {
        	System.err.println("Failed to exit visitor " + id);
		} else {
			System.out.println("Visitor " + id + " exited.");
		}
        exitClient.shutdown();
    }

    public int getEntries() {
        CoapClient visitorClient = new CoapClient(address + "/entry");
        CoapResponse response = visitorClient.get(); // GET request from turnstile server
        return Integer.parseInt(response.getResponseText());
    }

    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        while (true) {


            System.out.println("Choose action (enter/exit/read):");
            System.out.println("Type 'q' to quit.");
            System.out.println("> ");
            String action = scanner.nextLine();

            if (action.equalsIgnoreCase("enter")) {
            	System.out.println("\nEnter your ID (or type 'exit' to quit):");
                String id = scanner.nextLine();
                VisitorClient visitor = new VisitorClient(id);
                visitor.enter();
            } else if (action.equalsIgnoreCase("exit")) {
            	System.out.println("\nEnter your ID (or type 'exit' to quit):");
                String id = scanner.nextLine();
                VisitorClient visitor = new VisitorClient(id);
                visitor.exit();
			} else if (action.equalsIgnoreCase("read")) {
				VisitorClient visitor = new VisitorClient(""); //No need to assign ID to read entries
				System.out.println("Total entries: " + visitor.getEntries());
			} else if (action.equalsIgnoreCase("q")){
				System.out.println("Exiting...");
				break;
			} else {
                System.out.println("Invalid action. Please enter 'enter','exit' or 'read'.");
            }

            Thread.sleep(2000); // Adding a short delay between actions
        }
        scanner.close();
    }
}