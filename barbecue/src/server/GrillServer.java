package server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

/*
 * 1.client�κ��� ������ ����
 * -2prots(chatting/file)
 */
public class GrillServer {

	int chatPortNum;
	int filePortNum;

	// Map<String, ChatRoom> chatRooms;
	// Map<String, ChatRoom> chatRooms;

	Rooms rooms;

	Properties properties;

	public GrillServer() {
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
		rooms = new Rooms();
		// chatRooms = Collections.synchronizedMap(new HashMap<String, ChatRoom>());

		// ���� ����

	}

	// �� ��Ʈ���� serversocket�� �����ϰ�,
	// accept�� ��ٷ��� ��.
	public void start() {
		ServerSocket chatSS, FileSS;
		Socket chatSocket, fileSocket;
		try {
			chatSS = new ServerSocket(chatPortNum);
			FileSS = new ServerSocket(filePortNum);
			System.out.println("���� ���� �Ϸ�");
			while (true) {
				chatSocket = chatSS.accept();
				fileSocket = FileSS.accept();

				User user = new User();
				user.setChatSocket(chatSocket);
				user.setFileSocket(fileSocket);
				user.setIp_port(chatSocket.getInetAddress().toString() + ":" + String.valueOf(chatSocket.getPort()));

				System.out.println("[" + chatSocket.getInetAddress() + ":" + chatSocket.getPort() + "]" + "���� �����Ͽ����ϴ�.");

				// �� Ŭ���̾�Ʈ ���� ������ ������ ó���ϴ� ����

				// ���� ������ ���濡 ���ν�Ŵ\
				// chatRooms.get(0).addMember(user);

				ServerReceiver receiver = new ServerReceiver(user, rooms);

				receiver.start();
			}

		} catch (IOException e) {
			// TODO �ڵ� ������ catch ���
			e.printStackTrace();
		}

	}
}
