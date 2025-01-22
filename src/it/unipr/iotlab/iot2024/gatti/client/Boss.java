package it.unipr.iotlab.iot2024.gatti.client;

import java.util.Scanner;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;

import it.unipr.iotlab.iot2024.gatti.server.resources.TurnstileAction;

public class Boss{
    private String IDENTIFIER = "Boss";

    private String TURNSTILE_URI = "coap://localhost:5686";
    private String[] POOL_URI = {"coap://localhost:5683", "coap://localhost:5684", "coap://localhost:5685"};

    // Variables to store the last measured values
    private double[] lastTemperatures;
    private double[] lastChlorines;

    public Boss() {
    	lastTemperatures = new double[POOL_URI.length];
    	lastChlorines = new double[POOL_URI.length];
    	startObservingPools();
    }

    public void startObservingPools() {        
		for (int i=0; i < POOL_URI.length; i++) {
			observeTemperature(i);
			observeChlorine(i);
		}
    }

    private void observeTemperature(final int poolIndex) {
        CoapClient temperatureClient = new CoapClient(POOL_URI[poolIndex] + "/temperature");

        temperatureClient.observe(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                if (response != null) {
                    double temperature = Double.parseDouble(response.getResponseText());
                    lastTemperatures[poolIndex] = temperature;
                } else {
                    System.err.println("Failed to get temperature response.");
                }
            }

            @Override
            public void onError() {
                System.err.println("Error observing temperature resource.");
            }
        });
    }

    private void observeChlorine(final int poolIndex) {
        CoapClient chlorineClient = new CoapClient(POOL_URI[poolIndex]+ "/chlorine");

        chlorineClient.observe(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                if (response != null) {
                    double chlorine = Double.parseDouble(response.getResponseText());
                    lastChlorines[poolIndex] = chlorine;
                } else {
                    System.err.println("Failed to get chlorine response.");
                }
            }

            @Override
            public void onError() {
                System.err.println("Error observing chlorine resource.");
            }
        });
    }

    private void setValue(String uri, Double value) {
        CoapClient client = new CoapClient(uri); //the uri must contain the resource
        CoapResponse postResponse = client.post(String.valueOf(value), 0);
        if (postResponse != null) {
            System.out.println("POST Response: " + postResponse.getResponseText());
        } else {
            System.err.println("POST Response Failed");
        }
    }

    private void showTemporaryStatus() {
        displayStatus();
        try {
            Thread.sleep(5000); // Show for 5 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private void displayStatus() {
    	System.out.print("\r"); // Return to the beginning of the line
        StringBuilder status = new StringBuilder("\n=== Pool Status ===\n");
        for (int i = 0; i < 3; i++) {
        	status.append("Pool ").append(i + 1).append(" - Temperature: ").append(lastTemperatures[i]).append(" °C, Chlorine: ").append(lastChlorines[i]).append(" ppm\n");
        }
        System.out.print(status.toString());    
    }

    private void showTurnstileEntries() {
        CoapClient turnstileClient = new CoapClient(TURNSTILE_URI + "/entry");
        CoapResponse response = turnstileClient.get();
        if (response != null) {
            System.out.println("Number of people entered: " + response.getResponseText());
        } else {
            System.err.println("Failed to get turnstile entry count.");
        }
    }

    private void openTurnstile(TurnstileAction action) {
    	CoapClient turnstileClient = new CoapClient(TURNSTILE_URI + "/" + action.toString().toLowerCase());
        //CoapClient turnstileClient = new CoapClient(TURNSTILE_URI + "/entry"); without enum
        CoapResponse postResponse = turnstileClient.post(IDENTIFIER, 3000); //timeout of 3 second
        if (postResponse != null) {
            System.out.println("POST Response: " + postResponse.getResponseText());
        } else {
            System.err.println("Failed to send turnstile request.");
        }
    }

    // user interface method for console commands
    private void userInterface() {
        Scanner scanner = new Scanner(System.in); // scanner object to read user input
        String command; // command entered by the user in the console

        while (true) {
            System.out.println("\nCommands: \n1. set_temperature [pool_number] [value]\n2. set_chlorine [pool_number] [value]\n3. show_status\n4. show_turnstile_entries\n5. enter_turnstile\n6. exit_turnstile\n7. exit");
            System.out.print("> ");
            command = scanner.nextLine(); // Read the command entered by the user in the console

            if (command.equalsIgnoreCase("exit")) {
                System.out.println("Exiting..."); 
                break;
            }

            String[] parts = command.split(" ");
            if (parts.length < 1) {
                System.out.println("Invalid command format.");
                continue;
            }

            String action = parts[0];

            switch (action.toLowerCase()) {
                case "set_temperature":
                case "set_chlorine": {
                    if (parts.length < 3) {
                        System.out.println("Invalid command format.");
                        continue;
                    }

                    int poolNumber;
                    double value;

                    try {
                        poolNumber = Integer.parseInt(parts[1]);
                        value = Double.parseDouble(parts[2]);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number format.");
                        continue;
                    }
                    String poolUri;
                    switch (poolNumber) {
                        case 1:
                            poolUri = POOL_URI[0];
                            break;
                        case 2:
                            poolUri = POOL_URI[1];
                            break;
                        case 3:
                            poolUri = POOL_URI[2];
                            break;
                        default:
                            System.out.println("Invalid pool number.");
                            continue;
                    }
                    if (action.equalsIgnoreCase("set_temperature")) {
                        setValue(poolUri + "/temperature", value);
                    } else {
                        setValue(poolUri + "/chlorine", value);
                    }
                    break;
                }
                case "show_status":
                    showTemporaryStatus();
                    break;
                case "show_turnstile_entries":
                    showTurnstileEntries();
                    break;
                case "enter_turnstile":
                case "exit_turnstile": {
                    if (action.equalsIgnoreCase("enter_turnstile")) {
                        openTurnstile(TurnstileAction.ENTRY);
                    } else {
                        openTurnstile(TurnstileAction.EXIT);
                    }
                    break;
                }
                default:
                    System.out.println("Unknown action.");
                    break;
            }
        }
        scanner.close();
    }

    public static void main(String[] args) {
        final Boss boss = new Boss(); // create a new boss object that starts observing

        new Thread(new Runnable() {
            @Override
            public void run() {
                boss.userInterface(); // runs the user interface
            }
        }).start();

        try {
            Thread.sleep(600000); // keep the program running for 600 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}