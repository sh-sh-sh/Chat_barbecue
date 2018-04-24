package server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;

public class ServerReceiver extends Thread {

	User user;
	DataInputStream in;
	BufferedInputStream filein;

	ChatRoom currentRoom;
	List<ChatRoom> chatRooms;

	public ServerReceiver(User user, List<ChatRoom> chatRooms) {
		this.user = user;
		this.chatRooms = chatRooms;
		currentRoom = chatRooms.get(0);
		try {
			in = new DataInputStream(user.getChatSocket().getInputStream());
			filein = new BufferedInputStream(user.getFileSocket().getInputStream());
		} catch (IOException e) {
			// TODO 자동 생성된 catch 블록
			e.printStackTrace();
		}

	}

	public void run() {
		try {
			user.setName(in.readUTF());
			String msg;
			while (in != null) {
				msg = in.readUTF();
				System.out.println(msg);

				if (msg.startsWith("/")) {// command
					processCmd(msg.substring(1));
				} else {// massage
					// 현재 방 유저들에게 메세지 전송
				}
			}
		} catch (IOException e) {
			// TODO 자동 생성된 catch 블록
			e.printStackTrace();
		} finally {
			try {
				in.close();
				filein.close();
				user.getChatSocket().close();
				user.getFileSocket().close();
			} catch (IOException e) {
				// TODO 자동 생성된 catch 블록
				e.printStackTrace();
			}

		}
	}

	boolean processCmd(String cmd) {
		String[] tokens = cmd.split("[ ]+");
		if (tokens[0].equals("create")) {
			// 대기방에 있는지 검증
			if (currentRoom.name.equals("__waiting__")) {
				ChatRoom room = null;
				if (tokens.length == 2) {
					room = new ChatRoom(tokens[1], user.getIp_port());
				} else if (tokens.length == 3) {
					room = new ChatRoom(tokens[1], user.getIp_port(), tokens[2]);
				} else {
					return false;
				}
				chatRooms.add(room);

			} else {
				return false;
			}
		}
		return true;
	}
}
