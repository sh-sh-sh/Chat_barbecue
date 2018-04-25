package client;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;

public class Client {

	String serverIp;
	int chatPortNum;
	int filePortNum;
	String clientName;

	Properties properties;
	private Scanner sc;

	public String nameCheck() {
		System.out.println("����� �г����� �Է��ϼ���.");
		System.out.println("(*Ư������ �Է� �Ұ����ϸ�, 10���ڱ��� �Է� �����մϴ�.)");
		sc = new Scanner(System.in);
		clientName = sc.nextLine();
		if (clientName.matches("[0-9|a-z|A-Z|��-��|��-��|��-��]*") && clientName.length() <= 10) {
			return clientName;
		} else {
			System.out.println("�߸��� �Է��Դϴ�.");
			return nameCheck();
		}
	}

	public Client() {
		properties = new Properties();
		try {
			properties.load(new FileInputStream("./src/common/port.prop"));
		} catch (FileNotFoundException e) {
			// TODO �ڵ� ������ catch ���
			e.printStackTrace();
		} catch (IOException e) {
			// TODO �ڵ� ������ catch ���
			e.printStackTrace();
		}
		this.chatPortNum = Integer.parseInt(properties.getProperty("chat"));
		this.filePortNum = Integer.parseInt(properties.getProperty("file"));
	}

	public void setServerIp(String serverIp) {

	}

	public void start() {
		this.clientName = nameCheck();
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
			// TODO �ڵ� ������ catch ���
			e.printStackTrace();
		}
	}
} // class
