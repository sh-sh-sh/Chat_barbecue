package test;

import client.Client;

public class ClientApp {

	public static void main(String[] args) {
		Client client = new Client();

		client.setServerIp("192.168.0.17");
		client.start();
	}

}
