package server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import client.FileSender;

public class Rooms {
	Map<String, ChatRoom> chatRooms;
	final String bar = "-------------------------------------------------------------------------";

	public Rooms() {
		chatRooms = Collections.synchronizedMap(new HashMap<String, ChatRoom>());
		ChatRoom chatroom = new ChatRoom("__waitting__", "__admin__");// 초기생성시 대기방 생성해 추가
		chatRooms.put(chatroom.getName(), chatroom);
	}

	public synchronized boolean addMember(String roomname, User user) {
		return chatRooms.get(roomname).addMember(user);
	}

	public synchronized boolean removeMember(String roomname, String username) {
		return chatRooms.get(roomname).removeMember(username);
	}

	private void send(String msg, User user) throws IOException {// 사용자에게 전달하는 메세지에 쓰는 메소드 줄인거
		user.getChatOut().writeUTF(msg);
	}

	public synchronized void setCurrentRoom(String roomname, User user) {
		user.setCurrentRoom(chatRooms.get(roomname));
	}

	boolean checkName(String roomName, User user) throws IOException {// 닉네임을 받아서 제약에 걸리는지 체크
		send("	* 사용할 닉네임을 입력하세요.", user);
		send("	* 특수문자 입력 불가능하며, 10글자까지 입력 가능합니다.", user);
		String clientName = user.getChatIn().readUTF();
		if (clientName.matches("[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힝]*") && clientName.length() <= 10) {// 닉네임 제약에 걸리지 않을 경우
			return overlapCheck(clientName, roomName, user);// 중복검사
		} else {// 특수문자가 들어갔을 경우
			send("	* SYSTEM : 잘못된 입력입니다.", user);
			return checkName(roomName, user);
		}
	}

	synchronized boolean overlapCheck(String name, String roomName, User user) throws IOException {// 닉네임 중복검사
		if (chatRooms.get(roomName).getUsers().size() == 0) {// 해당 방 유저가 0인경우 중복검사 안함(ex:대기방 최초 접속자)
			user.setName(name);
			return true;
		}
		Set<String> set = chatRooms.get(roomName).getUsers().keySet();
		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			if (name.equals(it.next())) {// 방에 중복 닉네임이 있을 경우
				send("	* SYSTEM : 방에 중복된 이름이 있습니다. ", user);
				return checkName(roomName, user);
			}
		} // 방에 중복된 이름이 없었을 경우
		user.setName(name);
		return true;
	}

	synchronized void sendToAll(String msg, User user) throws IOException {// user가 있는 방의 모든 유저들에게 메세지 보냄
		if (msg.equals("ㅹ") || msg.startsWith("ㅨ")) {
			send("해당 특수문자는 사용이 불가능합니다.", user);
		}
		Iterator<String> it = user.getCurrentRoom().getUsers().keySet().iterator();

		while (it.hasNext()) {
			try {
				DataOutputStream out = user.getCurrentRoom().getUsers().get(it.next()).getChatOut();
				if (msg.startsWith("	*")) {
					out.writeUTF(msg);
				} else {
					out.writeUTF("[" + user.getName() + "]" + msg);
				}
			} catch (IOException e) {
			}
		} // while
	}

	String processCmd(String cmd, User user) throws IOException {// 커맨드 처리
		String[] tokens = cmd.split("[ ]+");
		if (tokens[0].equals("?")) {
			send(bar, user);
			send("	*** 명령어 목록 ***", user);
			send("/create 방이름 비밀번호(선택) - 방 생성 *주의:방 이름은 중복이 아닌 공백, 특수문자 제외 10자만 입력 가능", user);
			send("/list - 방 리스트 불러오기", user);
			send("/exit - 방 나가기(대기방에서는 불가능)", user);
			send("/join 들어갈방이름 비밀번호 - 방 들어가기", user);
			send("/users - 해당 방 유저 목록 보기", user);
			send("/owner - 해당 방 방장 보기", user);
			send("/sysexit - 프로그램 종료", user);
			send("/filelist - 접속한 방에 업로드된 파일 보기", user);
			send("/fileup 파일이름 - 접속한 방에 파일 보내기", user);
			send("/filedown 파일이름 - 접속한 방에 전송된 파일 받기 (받은 파일은 D://barbecue 폴더에 저장됩니다.)", user);
			send("/waitusers -대기방 유저 목록 보기", user);
			send("/invite 유저명 -유저 초대(대기방에 있는 유저만 초대 가능)", user);
			send("/y 초대가 있으면 가장 최근의 초대가 승락됨", user);
			send("/n 초대를 거절함", user);
			send("/setpw 비밀번호 - 현재 방 비밀번호 설정(방장만 가능)", user);
			send("/kick 유저이름 - 현재 방 유저 강퇴(방장만 가능)", user);
			send("/headchange 유저이름 - 해당 유저에게 방장 넘기기(방장만 가능)", user);
			send("/destroy - 현재 방 삭제(방장만 가능)", user);
			send(bar, user);
			return null;
		} else if (tokens[0].equals("create")) {
			return create(tokens, user);
		} else if (tokens[0].equals("list")) {
			return roomList(tokens, user);
		} else if (tokens[0].equals("exit")) {
			return roomExit(tokens, user);
		} else if (tokens[0].equals("join")) {
			return roomJoin(tokens, user);
		} else if (tokens[0].equals("sysexit")) {
			return sysExit(user);
		} else if (tokens[0].equals("users")) {
			return users(tokens, user);
		} else if (tokens[0].equals("waitusers")) {
			return waitusers(tokens, user);
		} else if (tokens[0].equals("owner")) {
			return owner(tokens, user);
		} else if (tokens[0].equals("invite")) {
			return invite(tokens, user);
		} else if (tokens[0].equals("y")) {
			return y(tokens, user);
		} else if (tokens[0].equals("n")) {
			return n(tokens, user);
		} else if (tokens[0].equals("filelist")) {
			return FileList(tokens, user);
		} else if (tokens[0].equals("filedown")) {
			return FileReceive(tokens, user);
		} else if (tokens[0].equals("setpw")) {
			return setpw(tokens, user);
		} else if (tokens[0].equals("kick")) {
			return kick(tokens, user);
		} else if (tokens[0].equals("destroy")) {
			return roomDestroy(tokens, user);
		} else if (tokens[0].equals("headchange")) {
			return headChange(tokens, user);
		} else {
			return "	* 오류:유효하지 않은 명령어";
		}
	}

	private String FileList(String[] tokens, User user) throws IOException {
		if (user.getCurrentRoom().getFiles().size() == 0) {
			return "	* " + user.getCurrentRoom().getName() + "에 업로드된 파일이 없습니다.";
		}
		Set<String> set = user.getCurrentRoom().getFiles().keySet();
		Iterator<String> it = set.iterator();
		send(bar, user);
		send("	*** " + user.getCurrentRoom().getName() + "의  파일 목록 ***", user);
		while (it.hasNext()) {
			send(it.next(), user);
		}
		send(bar, user);
		return null;
	}

	synchronized private String waitusers(String[] tokens, User user) throws IOException {
		Set<String> set = chatRooms.get("__waitting__").getUsers().keySet();
		Iterator<String> it = set.iterator();
		send(bar, user);
		send("	*** " + chatRooms.get("__waitting__").getName() + "의 유저 목록 ***", user);
		while (it.hasNext()) {
			send(it.next(), user);
		}
		send(bar, user);
		return null;
	}

	private String FileReceive(String[] tokens, User user) throws IOException {
		if (user.getCurrentRoom().getFiles().containsKey(tokens[1])) {// 해당 이름의 파일이 존재하면
			user.fs = new FileSender(tokens[1], user.getCurrentRoom().getName(), user.getChatOut(), user.getFileOut());
			user.fs.start();
		} else {
			return "	* 오류 : 해당 이름을 가진 파일이 존재하지 않습니다.";
		}
		return null;
	}

	synchronized private String invite(String[] tokens, User user) throws IOException {// 대기실에 있는 유저를 초대하는 메서드
		if (chatRooms.get("__waitting__").getUsers().containsKey(tokens[1])) {// 대기실에 해당 유저가 있으면
			User us = chatRooms.get("__waitting__").getUsers().get(tokens[1]);
			us.setInvite(user.getCurrentRoom().getName());// 해당 유저의 intvite에 초대한 유저의 룸네임 설정
			us.setInvitepw(user.getCurrentRoom().getPassword());// 비밀번호도 설정
			us.getChatOut().writeUTF(user.getName() + "님이 " + user.getCurrentRoom().getName() + "에서 당신을 초대하였습니다.");
			us.getChatOut().writeUTF("초대를 수락하려면 /y 거절하려면 /n을 입력하십시오.");
			return "	* " + us.getName() + "님을 초대하였습니다..";
		} else {
			return "	* 오류 : 대기실에 해당 유저가 존재하지 않습니다.";
		}
	}

	private String n(String[] tokens, User user) {// 가장 최근 초대를 거절하는 메서드
		user.setInvite(null);
		user.setInvitepw(null);
		return "	* 초대를 거절하였습니다.";
	}

	synchronized private String y(String[] tokens, User user) throws IOException {// 가장 최근 초대를 수락하는 메서드
		if (user.getInvite() == null) {
			return "	* 오류 : 초대를 이미 거절하였거나 초대받은 적이 없습니다.";
		}
		if (chatRooms.containsKey(user.getInvite())) {// 초대받은 방이 존재하면
			tokens = new String[] { "", user.getInvite(), user.getInvitepw() };
			return roomJoin(tokens, user);// 조인시킨다
		} else {
			return "	* 오류 : 초대받은 방이 현재 존재하지 않습니다.";
		}
	}

	private boolean isOwner(User user) {
		return user.getName().equals(user.getCurrentRoom().getOwner());
	}

	private String owner(String[] tokens, User user) {
		return "	* " + user.getCurrentRoom().getName() + "의 방장 : " + user.getCurrentRoom().getOwner();
	}

	synchronized private String headChange(String[] tokens, User user) throws IOException {
		// 다음 토큰 없으면 랜덤으로, 다음 토큰이 있으면 해당 유저가 있는지 확인하고 없으면 오류메세지, 있으면 방장 변경
		// TODO 자동 생성된 메소드 스텁
		if (isOwner(user)) {// 명령어를 호출한 유저가 방장인지 확인
			if (tokens.length == 2) {// 유저를 지정한 경우
				if (user.getCurrentRoom().getUsers().containsKey(tokens[1])) {// 지정한 유저가 존재하는 경우
					user.getCurrentRoom().setOwner(tokens[1]);
					sendToAll("	* " + tokens[1] + "님으로 방장이 변경되었습니다.", user);
					System.out.println(user.getCurrentRoom() + "의 방장이" + tokens[1] + "로 변경됨");
					return "	* 성공 : " + tokens[1] + "로 방장 변경됨";
				} else {// 지정한 유저가 존재하지 않는 경우
					return "	* 오류 : 지정한 유저가 해당 방에 존재하지 않습니다.";
				}
			} else {// 유저를 지정하지 않은 경우
				Set<String> set = user.getCurrentRoom().getUsers().keySet();
				Iterator<String> it = set.iterator();
				if (it.hasNext()) {
					user.getCurrentRoom().setOwner(it.next());
					sendToAll("	* " + user.getCurrentRoom().getOwner() + "님으로 방장이 변경되었습니다.", user);
					return "	* 성공 : 랜덤으로 방장이 변경되었습니다.";
				} else {
					return "	* 오류 : 권한을 넘길 유저가 없습니다.";
				}
			}
		} else {// 방장이 아닐 경우
			return "	* 오류 : 방장이 아니기 때문에 방장을 변경할 권한이 없습니다.";
		}
	}

	synchronized private String roomList(String[] tokens, User user) throws IOException {// 방 리스트 보내기
		Set<String> set = chatRooms.keySet();
		Iterator<String> it = set.iterator();
		send(bar, user);
		while (it.hasNext()) {
			String rn = it.next();
			send(" * " + rn + " : " + chatRooms.get(rn).getUsers().size() + "명", user);
		}
		send(bar, user);
		return null;
	}

	private String roomDestroy(String[] tokens, User user) {// 방 삭제(방장권한)
		if (isOwner(user)) {// 방장이 맞으면
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

	synchronized private String roomDestroy2(String roomName) throws IOException {// room을 매개변수로 받는 방 삭제
		chatRooms.remove(roomName);
		return "[" + roomName + "]방이 삭제되었습니다.";
	}

	synchronized private String kick(String[] tokens, User user) {
		if (isOwner(user)) {// 방장이고
			if (user.getCurrentRoom().getUsers().containsKey(tokens[1])) {// 입력한 유저가 존재하면
				User kickUser = user.getCurrentRoom().getUsers().get(tokens[1]);
				if (!user.getCurrentRoom().removeMember(kickUser.getName())) {
					return "	* 오류 : 해당 유저를 퇴장시키지 못했습니다.";
				}
				chatRooms.get("__waitting__").getUsers().put(kickUser.getName(), kickUser);
				kickUser.setCurrentRoom(chatRooms.get("__waitting__"));
				try {
					kickUser.getChatOut().writeUTF("방장에 의해 " + user.getCurrentRoom().getName() + "에서 강제 퇴장되었습니다.");
				} catch (IOException e) {
					e.printStackTrace();
				}
				return "	* 성공 : 해당 유저를 강제 퇴장시켰습니다.";
			} else {
				return "	* 오류 : 지정한 유저가 해당 방에 존재하지 않습니다.";
			}
		} else {
			return "	* 오류 : 방장이 아니므로 추방할 권한이 없습니다.";
		}
	}

	private String setpw(String[] tokens, User user) {// 비밀번호 설정하는 메서드
		if (isOwner(user)) {
			user.getCurrentRoom().setPassword(tokens[1]);
			return "	* 성공 : 패스워드를 설정했습니다.";
		} else {
			return "	* 오류 : 방장이 아니므로 패스워드를 설정할 권한이 없습니다.";
		}
	}

	synchronized private String users(String[] tokens, User user) throws IOException {// 유저 목록 보여줌
		Set<String> set = user.getCurrentRoom().getUsers().keySet();
		Iterator<String> it = set.iterator();
		send(bar, user);
		send("	*** " + user.getCurrentRoom().getName() + "의 유저 목록 ***", user);
		while (it.hasNext()) {
			send(it.next(), user);
		}
		send(bar, user);
		return null;
	}

	synchronized private String roomJoin(String[] tokens, User user) throws IOException {// 방에 들어가는 메소드
		if (chatRooms.containsKey(tokens[1])) {// 입력한 방 이름이 존재할 경우
			if (user.getCurrentRoom().getName().equals(tokens[1])) {// 입력한 방 이름이 자기가 있는 방일 경우
				return "	* 오류 : 이미 해당 방에 입장해 있습니다.";
			} else {
				if (chatRooms.get(tokens[1]).getPassword() != null) {// 해당 방에 비밀번호가 존재할 경우
					if (tokens.length > 1 && tokens[2].equals(chatRooms.get(tokens[1]).getPassword())) {// 비밀번호가 일치할 경우
						roomex(user, false);// 기존 방에서 퇴장하고
						chatRooms.get(tokens[1]).addMember(user);
						user.setCurrentRoom(chatRooms.get(tokens[1]));
						sendToAll("	* " + user.getName() + "님이 입장하셨습니다.", user);
						System.out.println(user.getName() + "님이 " + user.getCurrentRoom().getName() + "에 입장하였습니다.");
						System.out.println("[" + user.getCurrentRoom().getName() + "]의 접속자 목록 : "
								+ user.getCurrentRoom().getUsers().keySet().toString());
						return "	* " + user.getCurrentRoom().getName() + "에 입장되었습니다.";
					} else {
						return "	* 오류 : 패스워드가 일치하지 않습니다.";
					}
				} else {// 비밀번호가 존재하지 않을 경우
					roomex(user, false);// 기존 방에서 퇴장하고
					chatRooms.get(tokens[1]).addMember(user);
					user.setCurrentRoom(chatRooms.get(tokens[1]));
					sendToAll("	* " + user.getName() + "님이 입장하셨습니다.", user);
					System.out.println(user.getName() + "님이 " + user.getCurrentRoom().getName() + "에 입장하였습니다.");
					System.out.println("[" + user.getCurrentRoom().getName() + "]의 접속자 목록 : "
							+ user.getCurrentRoom().getUsers().keySet().toString());
					return "	* " + user.getCurrentRoom().getName() + "에 입장되었습니다.";
				}
			}
		} else {
			return "	* 오류 : 존재하지 않는 방 이름입니다.";
		}
	}

	private String sysExit(User user) throws IOException {// 방에서 퇴장시키고 커맨드 부른 유저 시스템 종료시킴
		roomex(user, false);
		return "ㅹ";
	}

	private String roomExit(String[] tokens, User user) throws IOException {// 방에서 퇴장하기 전 대기방이었는지 체크
		if (user.getCurrentRoom().getName().equals("__waitting__")) {
			return "	* 오류 : 대기방에서는 퇴장할 수 없습니다.";
		}
		return roomex(user, true);
	}

	synchronized private String roomex(User user, boolean gowait) throws IOException {// 방에서 퇴장
		ChatRoom exRoom = user.getCurrentRoom();
		if (!exRoom.getName().equals("__waitting__")) {
			sendToAll("	* [" + user.getName() + "]님이 [" + user.getCurrentRoom().getName() + "]에서 퇴장하였습니다.", user);
		}
		boolean alone = user.getCurrentRoom().getUsers().size() == 1;// 방에 혼자 있었는지 체크

		if (isOwner(user) && !alone) {// 오너였고 혼자가 아니었으면
			String[] tokens = new String[] { "" };
			headChange(tokens, user);// 방장을 바꾼다
		}
		if (!user.getCurrentRoom().removeMember(user.getName())) {
			System.out.println("에러! roomex의 removeMember 실패-" + user.getName());
			return "	* 오류 : 해당 유저를 퇴장시키지 못했습니다.";
		}

		System.out.println("[" + user.getName() + "]님이 [" + exRoom.getName() + "]에서 퇴장하였습니다.");
		System.out.println("[" + exRoom.getName() + "]의 접속자 목록 : " + exRoom.getUsers().keySet().toString());

		if (gowait) {
			chatRooms.get("__waitting__").getUsers().put(user.getName(), user);
			user.setCurrentRoom(chatRooms.get("__waitting__"));
			if (alone && isOwner(user)) {// 방에 혼자 있고 오너였으면(대기방은 혼자있었어도 없애면 안되니까)
				try {
					send(roomDestroy2(exRoom.getName()), user);// 원래 있던 방을 부순다
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return "	* 대기방으로 이동되었습니다.";
		} else {
			return "	* 기존 방에서 퇴장되었습니다.";
		}

	}

	synchronized private String create(String[] tokens, User user) throws IOException {
		if (user.getCurrentRoom().getName().equals(chatRooms.get("__waitting__").getName())) {
			ChatRoom room = null;
			if (tokens.length == 2 || tokens.length == 3) {
				if (tokens[1].matches("[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힝]*") && tokens[1].length() <= 10) {// 방 이름이 조건에 맞는지 검사
					if (chatRooms.containsKey(tokens[1])) {// 방이름 중복검사
						return "	* 오류 : 중복된 방 이름이 있습니다.";
					} else {
						if (tokens.length == 2) {
							room = new ChatRoom(tokens[1], user.getName());
						} else {
							room = new ChatRoom(tokens[1], user.getName(), tokens[2]);
						}
					}
				} else {
					return "	* 오류 : 방 제목이 조건에 맞지 않음";
				}
			} else {
				return "	* 오류 : 명령어 구성이 적절하지 않음";
			}
			chatRooms.put(room.getName(), room);
			String exRoomName = user.getCurrentRoom().getName();
			user.getCurrentRoom().getUsers().remove(user.getName());
			System.out.println("[" + user.getName() + "]님이 [" + exRoomName + "]에서 퇴장하였습니다.");
			room.getUsers().put(user.getName(), user);
			System.out.println("[" + user.getName() + "]님이 [" + room.getName() + "]을 생성 후 입장하였습니다.");
			user.setCurrentRoom(room);

			try {
				send("	* [" + user.getCurrentRoom().getName() + "]에 입장되었습니다.", user);
				send("	* [" + user.getName() + "]님은 [" + user.getCurrentRoom().getName() + "]의 방장입니다.", user);
				send("	* 퇴장과 동시에 방장 권한을 잃게 되며, 방 유저 중 랜덤하게 권한이 부여됩니다.", user);
			} catch (IOException e) {
				// TODO 자동 생성된 catch 블록
				e.printStackTrace();
			}
			return "	* 성공 : 방 생성 완료";
		} else {// 대기방이 아닌 방에서 호출했을 경우
			return "	* 오류 : 대기방이 아닌 방에서 방 생성 불가";
		}
	}

}
