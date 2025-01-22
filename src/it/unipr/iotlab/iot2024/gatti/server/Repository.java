package it.unipr.iotlab.iot2024.gatti.server;

import java.util.HashSet;
import java.util.Set;

public class Repository {
	private static Repository instance = null;
	private Set<String> visitors;
	
	private Repository() {
		visitors = new HashSet<String>();
	}
	
	public static synchronized Repository getInstance() {
		if(instance == null) {
            instance = new Repository();
        }
        return instance;
	}
	
	public boolean addVisitor(String visitor) {
		return visitors.add(visitor);
	}
	
	public boolean removeVisitor(String visitor) {
		return visitors.remove(visitor);
	}
	
	public int getVisitorsCount() {
		return visitors.size();
	}
}
