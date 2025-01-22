package it.unipr.iotlab.iot2024.gatti.client;

public class Client {
	public static void main(String[] args) {
		String[] poolAddresses = {"coap://localhost:5683", "coap://localhost:5684", "coap://localhost:5685"}; //contains all the pool addresses
        Thread[] threadList = new Thread[2*poolAddresses.length]; //threads contains both heating and chlorine
        
        int j; //index for threadList array
		
		for(int i = 0; i < poolAddresses.length; i++) {
			HeatingPumpClient hp = new HeatingPumpClient(poolAddresses[i]);
			if(i == 1) { hp.setTemperatureLevel(30.0); } //pool number 1 is the relaxing lagoon
			
			j=2*i; //j is the index of the thread in the threadList array that contains both heating and chlorine threads 
			threadList[j] = new Thread(hp);
			threadList[j].start();
			System.out.println("Heating pump started for pool "+i+"with address: "+poolAddresses[i]);
			
			ChlorineMixerClient cm = new ChlorineMixerClient(poolAddresses[i]);
			if(i == 1) { cm.setChlorineLevel(3.0); } //pool number 1 is the relaxing lagoon (needs more chlorine)
			
			j++; //chlorine is uneven index
			threadList[j] = new Thread(cm);
			threadList[j].start();
			System.out.println("Chlorine mixer started for pool "+i+"with address: "+poolAddresses[i]);
			}
		
		try {
			for(Thread t : threadList) { t.join(); } //wait for all threads to finish
		} catch (InterruptedException e) {
				e.printStackTrace();
		}

		System.out.println("All pool clients have proceded.");
		
		try {
			Thread.sleep(600000); // 600 seconds
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
}