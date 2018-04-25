package server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ServerReceiver extends Thread {

	User user;
	DataInputStream in;
	BufferedInputStream filein;

	Map<String, ChatRoom> chatRooms;

	public ServerReceiver(User user, Map<String, ChatRoom> chatRooms) {
		this.user = user;
		this.chatRooms = chatRooms;
		user.setCurrentRoom(chatRooms.get("__waitting__"));
		try {
			in = new DataInputStream(user.getChatSocket().getInputStream());
			filein = new BufferedInputStream(user.getFileSocket().getInputStream());
		} catch (IOException e) {
			// TODO 자동 생성된 catch 블록
			e.printStackTrace();
		}

	}

	private void send(String msg) throws IOException {// 사용자에게 전달하는 메세지에 쓰는 메소드 줄인거
		user.getChatOut().writeUTF(msg);
	}

	public void run() {
		try {
			checkName(in.readUTF(), "__waitting__");

			chatRooms.get("__waitting__").addMember(user);
			System.out.println("[" + user.getName() + "]이 [" + user.getCurrentRoom().getName() + "]에 들어감");
			System.out.println("[" + user.getCurrentRoom().getName() + "]의 접속자 목록 : "
					+ user.getCurrentRoom().getUsers().keySet().toString());
			send("	* SYSTEM : " + user.getName() + "님, 반갑습니다.");
			send("	* SYSTEM : 명령어를 확인하려면 /?를 입력해 주세요.");
			System.out.println();
			String msg;
			while (in != null) {
				msg = in.readUTF();
				if (msg.startsWith("/")) {// command
					String cmsg = processCmd(msg.substring(1));
					if (cmsg != null) {
						send(cmsg);// 커멘드를 처리한 결과 메세지 전송
					}
				} else {// massage
					// 현재 방 유저들에게 메세지 전송
				}
			}
		} catch (IOException e) {

		} finally {
			try {
				in.close();
				filein.close();
				user.getChatSocket().close();
				user.getFileSocket().close();
				ChatRoom a = user.getCurrentRoom();
				a.getUsers().remove(user.getName());
				sendToAll("#" + user.getName() + "님이 [" + a.getName() + "]을(를) 나가셨습니다.");
				System.out.println(" [" + a.getName() + "]에서 [" + user.getName() + "]님이 접속을 종료하였습니다.");
				System.out.println("현재 [" + a.getName() + "] 방 접속자 수는 " + a.getUsers().size() + "입니다.");
				System.out.println("[" + a.getName() + "]의 접속자 목록:" + a.getUsers().keySet().toString());
			} catch (IOException e) {
				// TODO 자동 생성된 catch 블록
				e.printStackTrace();
			}

		}
	}

	void checkName(String name, String roomName) throws IOException {
		user.setName(name);
		Set<String> set = chatRooms.get(roomName).getUsers().keySet();
		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			if (user.getName().equals(it.next())) {
				send("	* SYSTEM : 방에 중복된 이름이 있습니다. 이름을 다시 입력해 주세요.");
				checkName(in.readUTF(), roomName);
			}
		}
	}

	void sendToAll(String msg) {
		Iterator<String> it = user.getCurrentRoom().getUsers().keySet().iterator();

		while (it.hasNext()) {
			try {
				DataOutputStream out = user.getCurrentRoom().getUsers().get(it.next()).getChatOut();
				out.writeUTF("[" + user.getName() + "]" + msg);
			} catch (IOException e) {
			}
		} // while
	}

	String processCmd(String cmd) throws IOException {
		String[] tokens = cmd.split("[ ]+");
		if (tokens[0].equals("?")) {
			send("	/create 방이름 비밀번호(선택) - 방 생성");
			send("	/list - 리스트 불러오기");
			send("	/exit - 방 나가기(대기방에서는 불가능)");
			send("	/join - 방 들어가기");
			send("	/users - 해당 방 유저 목록 보기");
			send("	/set 비밀번호 - 현재 방 비밀번호 설정(방장만 가능)");
			send("	/kick 유저이름 - 현재 방 유저 강퇴(방장만 가능)");
			send("	/headchange 유저이름 - 해당 유저에게 방장 넘기기(방장만 가능)");
			send("	/destroy - 현재 방 삭제(방장만 가능)");
			return "	* 위 명령어들 외에는 오류 처리됩니다.";
		} else if (tokens[0].equals("create")) {
			return create(tokens);
		} else if (tokens[0].equals("list")) {
			return roomList(tokens);
		} else if (tokens[0].equals("exit")) {
			return roomExit(tokens);
		} else if (tokens[0].equals("join")) {
			return roomJoin(tokens);
		} else if (tokens[0].equals("users")) {
			return users(tokens);
		} else if (tokens[0].equals("set")) {
			return set(tokens);
		} else if (tokens[0].equals("kick")) {
			return kick(tokens);
		} else if (tokens[0].equals("destroy")) {
			return roomDestroy(tokens);
		} else if (tokens[0].equals("headchange")) {
			return headChange(tokens);
		} else {
			return "	* 오류:유효하지 않은 명령어";
		}
	}

	private String headChange(String[] tokens) {
		// 다음 토큰 없으면 랜덤으로, 다음 토큰이 있으면 해당 유저가 있는지 확인하고 없으면 오류메세지, 있으면 방장 변경
		// TODO 자동 생성된 메소드 스텁
		if (tokens.length == 2) {// 유저를 지정한 경우
			if (user.currentRoom.getUsers().containsKey(tokens[1])) {// 지정한 유저가 존재하는 경우

			} else {// 지정한 유저가 존재하지 않는 경우

			}
		} else {// 유저를 지정하지 않은 경우

		}
		return null;
	}

	private String roomList(String[] tokens) {// 방 리스트 보내기
		// TODO 자동 생성된 메소드 스텁
		return null;
	}

	private String roomDestroy(String[] tokens) {// 방 삭제(방장권한)
		if (user.getName().equals(user.getCurrentRoom().getOwner())) {// 방장이 맞으면
			try {
				return roomDestroy2(user.getCurrentRoom().getName());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			return "	* 오류 : 방 삭제를 수행할 권한이 없습니다.";
		}
		// TODO 자동 생성된 메소드 스텁
		return null;
	}

	private String roomDestroy2(String roomName) throws IOException {// room을 매개변수로 받는 방 삭제
		chatRooms.remove(roomName);
		return "[" + roomName + "]방이 삭제되었습니다.";
	}

	private String kick(String[] tokens) {
		// TODO 자동 생성된 메소드 스텁
		return null;
	}

	private String set(String[] tokens) {
		// TODO 자동 생성된 메소드 스텁
		return null;
	}

	private String users(String[] tokens) {
		// TODO 자동 생성된 메소드 스텁
		return null;
	}

	private String roomJoin(String[] tokens) {
		// TODO 자동 생성된 메소드 스텁
		return null;
	}

	private String roomExit(String[] tokens) {
		ChatRoom exRoom = null;
		if (user.getCurrentRoom().getUsers().size() == 1) {// 방에 혼자 있었는지 체크
			exRoom = user.getCurrentRoom();
		}
		if (user.getName().equals(user.getCurrentRoom().getOwner())) {
			tokens = new String[] { "" };
			headChange(tokens);
		}
		user.getCurrentRoom().getUsers().remove(user.getName());
		chatRooms.get("__waitting__").getUsers().put(user.getName(), user);
		user.setCurrentRoom(chatRooms.get("__waitting__"));
		if (exRoom != null) {// 방에 혼자 있었으면
			try {
				send(roomDestroy2(exRoom.getName()));// 원래 있던 방을 부순다
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "	* 대기방으로 이동되었습니다.";
	}

	private String create(String[] tokens) throws IOException {
		if (user.getCurrentRoom().getName().equals(chatRooms.get("__waitting__").getName())) {
			ChatRoom room = null;
			if (tokens.length == 2) {
				room = new ChatRoom(tokens[1], user.getIp_port());
			} else if (tokens.length == 3) {
				room = new ChatRoom(tokens[1], user.getIp_port(), tokens[2]);
			} else {
				return "	* 오류:명령어 구성이 적절하지 않음";
			}
			chatRooms.put(room.getName(), room);
			String exRoomName = user.getCurrentRoom().getName();
			user.getCurrentRoom().getUsers().remove(user.getName());
			System.out.println("[" + user.getName() + "]님이 [" + exRoomName + "]에서 퇴장하였습니다.");
			room.getUsers().put(user.getName(), user);
			System.out.println("[" + user.getName() + "]님이 [" + room.getName() + "]을 생성 후 입장하였습니다.");
			user.setCurrentRoom(room);

			try {
				send("	* [" + user.currentRoom.getName() + "]에 입장되었습니다.");
				send("	* [" + user.getName() + "]님은 [" + user.currentRoom.getName() + "]의 방장입니다.");
				send("	* 퇴장과 동시에 방장 권한을 잃게 되며, 방 유저 중 랜덤하게 권한이 부여됩니다.");
			} catch (IOException e) {
				// TODO 자동 생성된 catch 블록
				e.printStackTrace();
			}
			return "	* 성공:방 생성 완료";
		} else {// 대기방이 아닌 방에서 호출했을 경우
			return "	* 오류:대기방이 아닌 방에서 방 생성 불가";
		}
	}
}
