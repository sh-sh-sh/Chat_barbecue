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
 * 1.client로부터 접속을 받음
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
			// TODO 자동 생성된 catch 블록
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 자동 생성된 catch 블록
			e.printStackTrace();
		}
		this.chatPortNum = Integer.parseInt(properties.getProperty("chat"));
		this.filePortNum = Integer.parseInt(properties.getProperty("file"));

		chatRooms = Collections.synchronizedMap(new HashMap<String, ChatRoom>());

		// 대기방 생성
		ChatRoom chatroom = new ChatRoom("__waitting__", "__admin__");
		chatRooms.put(chatroom.name, chatroom);
	}

	// 각 포트별로 serversocket을 생성하고,
	// accept를 기다려야 함.
	public void start() {
		ServerSocket chatSS, FileSS;
		Socket chatSocket, fileSocket;
		int count = 0;
		try {
			chatSS = new ServerSocket(chatPortNum);
			FileSS = new ServerSocket(filePortNum);
			System.out.println("서버 생성 완료");
			while (true) {
				chatSocket = chatSS.accept();
				fileSocket = FileSS.accept();

				User user = new User();
				user.setChatSocket(chatSocket);
				user.setFileSocket(fileSocket);
				user.setIp_port(chatSocket.getInetAddress().toString() + ":" + String.valueOf(chatSocket.getPort()));

				count++;
				System.out.println("[" + chatSocket.getInetAddress() + ":" + chatSocket.getPort() + "]" + "에서 접속하였습니다.");
				System.out.println("[" + fileSocket.getInetAddress() + ":" + fileSocket.getPort() + "]" + "에서 접속하였습니다.");
				System.out.println(count + "명이 접속중...");
				// 각 클라이언트 별로 소켓을 가지고 처리하는 무엇

				// 접속 유저를 대기방에 조인시킴
				chatRooms.get("__waiting__").addMember(user);

				ServerReceiver receiver = new ServerReceiver(user, chatRooms);

				receiver.start();
			}

		} catch (IOException e) {
			// TODO 자동 생성된 catch 블록
			e.printStackTrace();
		}

	}
}
