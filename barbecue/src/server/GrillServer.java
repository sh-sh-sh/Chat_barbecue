package server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/*
 * 1.client�κ��� ������ ����
 * -2prots(chatting/file)
 */
public class GrillServer {

	int chatPortNum;
	int filePortNum;

	Map<String, ChatRoom> chatRooms;
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

		chatRooms = Collections.synchronizedMap(new HashMap<String, ChatRoom>());

		// ���� ����
		ChatRoom chatroom = new ChatRoom("__waitting__", "__admin__");
		chatRooms.put(chatroom.name, chatroom);
	}

	// �� ��Ʈ���� serversocket�� �����ϰ�,
	// accept�� ��ٷ��� ��.
	public void start() {
		ServerSocket chatSS, FileSS;
		Socket chatSocket, fileSocket;
		int count = 0;
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

				count++;
				System.out.println("[" + chatSocket.getInetAddress() + ":" + chatSocket.getPort() + "]" + "���� �����Ͽ����ϴ�.");
				System.out.println("[" + fileSocket.getInetAddress() + ":" + fileSocket.getPort() + "]" + "���� �����Ͽ����ϴ�.");
				System.out.println(count + "���� ������...");
				// �� Ŭ���̾�Ʈ ���� ������ ������ ó���ϴ� ����

				// ���� ������ ���濡 ���ν�Ŵ
				chatRooms.get("__waiting__").addMember(user);

				ServerReceiver receiver = new ServerReceiver(user, chatRooms);

				receiver.start();
			}

		} catch (IOException e) {
			// TODO �ڵ� ������ catch ���
			e.printStackTrace();
		}

	}
}
