package it.unipr.iotlab.iot2024.gatti.server;

import org.eclipse.californium.core.CoapServer;

import it.unipr.iotlab.iot2024.gatti.server.resources.ChlorineResource;
import it.unipr.iotlab.iot2024.gatti.server.resources.TemperatureResource;

public class PoolServer extends CoapServer {
	public PoolServer(int port) {
		super(port);
        
		add(new TemperatureResource("temperature"));
		add(new ChlorineResource("chlorine"));
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PoolServer pool1 = new PoolServer(5683);
		PoolServer pool2 = new PoolServer(5684);
		PoolServer pool3 = new PoolServer(5685);
		
		pool1.start();
		pool2.start();
		pool3.start();
        
        System.out.println("Pool server started on 127.0.0.1 port 5683, 5684, 5685");
	}
}