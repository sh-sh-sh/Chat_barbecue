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
		System.out.println("사용할 닉네임을 입력하세요.");
		System.out.println("(*특수문자 입력 불가능하며, 10글자까지 입력 가능합니다.)");
		sc = new Scanner(System.in);
		clientName = sc.nextLine();
		if (clientName.matches("[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힝]*") && clientName.length() <= 10) {
			return clientName;
		} else {
			System.out.println("잘못된 입력입니다.");
			return nameCheck();
		}
	}

	public Client() {
		properties = new Properties();
		try {
			properties.load(new FileInputStream("./src/common/port.prop"));
		} catch (FileNotFoundException e) {
			// TODO 자동 생성된 catch 블록
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 자동 생성된 catch 블록
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
			System.out.println("서버에 연결되었습니다.");

			Thread sender = new Thread(new ClientSender(socket, FileSocket, clientName));
			Thread receiver = new Thread(new ClientReceiver(socket, FileSocket));

			sender.start();
			receiver.start();

		} catch (IOException e) {
			// TODO 자동 생성된 catch 블록
			e.printStackTrace();
		}
	}
} // class
