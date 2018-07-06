package server;

import java.io.FileInputStream;
import java.io.FileNotFoundException; 
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

/*
 * 1.client로부터 접속을 받음
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
			// TODO 자동 생성된 catch 블록
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 자동 생성된 catch 블록
			e.printStackTrace();
		}
		this.chatPortNum = Integer.parseInt(properties.getProperty("chat"));
		this.filePortNum = Integer.parseInt(properties.getProperty("file"));
		rooms = new Rooms();
		// chatRooms = Collections.synchronizedMap(new HashMap<String, ChatRoom>());

		// 대기방 생성

	}

	// 각 포트별로 serversocket을 생성하고,
	// accept를 기다려야 함.
	public void start() {
		ServerSocket chatSS, FileSS;
		Socket chatSocket, fileSocket;
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

				System.out.println("[" + chatSocket.getInetAddress() + ":" + chatSocket.getPort() + "]" + "에서 접속하였습니다.");

				// 각 클라이언트 별로 소켓을 가지고 처리하는 무엇

				// 접속 유저를 대기방에 조인시킴\
				// chatRooms.get(0).addMember(user);

				ServerReceiver receiver = new ServerReceiver(user, rooms);

				receiver.start();
			}

		} catch (IOException e) {
			// TODO 자동 생성된 catch 블록
			e.printStackTrace();
		}

	}
}
