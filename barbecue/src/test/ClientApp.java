package test;

import client.Client;

public class ClientApp {

	public static void main(String[] args) {
		Client client = new Client();

		client.setServerIp("127.0.0.1");
		client.start();
	}

}
