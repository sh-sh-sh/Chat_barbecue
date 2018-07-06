package test;

import server.GrillServer;

public class ServerApp {

	public static void main(String[] args) {
		GrillServer server = new GrillServer(); 
		server.start();
	}

}
