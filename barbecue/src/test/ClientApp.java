package test;

import client.Client;

public class ClientApp {

	public static void main(String[] args) {
		Client client = new Client(args[0]);

		client.setServerIp("127.0.0.1");
		client.start();
	}

}
