package client;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.Properties;

public class Client {

	String serverIp;
	int chatPortNum;
	int filePortNum;
	String clientName;

	Properties properties;

	public Client(String clientName) {
		this.clientName = clientName;
		properties = new Properties();
		try {
			properties.load(new FileInputStream("./src/common/port.prop"));
		} catch (FileNotFoundException e) {
			// TODO �ڵ� ������ catch ����
			e.printStackTrace();
		} catch (IOException e) {
			// TODO �ڵ� ������ catch ����
			e.printStackTrace();
		}
		this.chatPortNum = Integer.parseInt(properties.getProperty("chat"));
		this.filePortNum = Integer.parseInt(properties.getProperty("file"));
	}

	public void setServerIp(String serverIp) {

	}

	public void start() {
		Socket socket = null;
		Socket FileSocket = null;
		try {
			socket = new Socket(serverIp, chatPortNum);
			FileSocket = new Socket(serverIp, filePortNum);
			System.out.println("������ ����Ǿ����ϴ�.");

			Thread sender = new Thread(new ClientSender(socket, FileSocket, clientName));
			Thread receiver = new Thread(new ClientReceiver(socket, FileSocket));

			sender.start();
			receiver.start();

		} catch (IOException e) {
			// TODO �ڵ� ������ catch ����
			e.printStackTrace();
		}
	}
} // class