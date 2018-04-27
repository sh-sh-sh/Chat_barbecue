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
		ChatRoom chatroom = new ChatRoom("__waitting__", "__admin__");// �ʱ������ ���� ������ �߰�
		chatRooms.put(chatroom.getName(), chatroom);
	}

	public synchronized boolean addMember(String roomname, User user) {
		return chatRooms.get(roomname).addMember(user);
	}

	public synchronized boolean removeMember(String roomname, String username) {
		return chatRooms.get(roomname).removeMember(username);
	}

	private void send(String msg, User user) throws IOException {// ����ڿ��� �����ϴ� �޼����� ���� �޼ҵ� ���ΰ�
		user.getChatOut().writeUTF(msg);
	}

	public synchronized void setCurrentRoom(String roomname, User user) {
		user.setCurrentRoom(chatRooms.get(roomname));
	}

	boolean checkName(String roomName, User user) throws IOException {// �г����� �޾Ƽ� ���࿡ �ɸ����� üũ
		send("	* ����� �г����� �Է��ϼ���.", user);
		send("	* Ư������ �Է� �Ұ����ϸ�, 10���ڱ��� �Է� �����մϴ�.", user);
		String clientName = user.getChatIn().readUTF();
		if (clientName.matches("[0-9|a-z|A-Z|��-��|��-��|��-��]*") && clientName.length() <= 10) {// �г��� ���࿡ �ɸ��� ���� ���
			return overlapCheck(clientName, roomName, user);// �ߺ��˻�
		} else {// Ư�����ڰ� ���� ���
			send("	* SYSTEM : �߸��� �Է��Դϴ�.", user);
			return checkName(roomName, user);
		}
	}

	synchronized boolean overlapCheck(String name, String roomName, User user) throws IOException {// �г��� �ߺ��˻�
		if (chatRooms.get(roomName).getUsers().size() == 0) {// �ش� �� ������ 0�ΰ�� �ߺ��˻� ����(ex:���� ���� ������)
			user.setName(name);
			return true;
		}
		Set<String> set = chatRooms.get(roomName).getUsers().keySet();
		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			if (name.equals(it.next())) {// �濡 �ߺ� �г����� ���� ���
				send("	* SYSTEM : �濡 �ߺ��� �̸��� �ֽ��ϴ�. ", user);
				return checkName(roomName, user);
			}
		} // �濡 �ߺ��� �̸��� ������ ���
		user.setName(name);
		return true;
	}

	synchronized void sendToAll(String msg, User user) throws IOException {// user�� �ִ� ���� ��� �����鿡�� �޼��� ����
		if (msg.equals("��") || msg.startsWith("��")) {
			send("�ش� Ư�����ڴ� ����� �Ұ����մϴ�.", user);
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

	String processCmd(String cmd, User user) throws IOException {// Ŀ�ǵ� ó��
		String[] tokens = cmd.split("[ ]+");
		if (tokens[0].equals("?")) {
			send(bar, user);
			send("	*** ��ɾ� ��� ***", user);
			send("/create ���̸� ��й�ȣ(����) - �� ���� *����:�� �̸��� �ߺ��� �ƴ� ����, Ư������ ���� 10�ڸ� �Է� ����", user);
			send("/list - �� ����Ʈ �ҷ�����", user);
			send("/exit - �� ������(���濡���� �Ұ���)", user);
			send("/join �����̸� ��й�ȣ - �� ����", user);
			send("/users - �ش� �� ���� ��� ����", user);
			send("/owner - �ش� �� ���� ����", user);
			send("/sysexit - ���α׷� ����", user);
			send("/filelist - ������ �濡 ���ε�� ���� ����", user);
			send("/fileup �����̸� - ������ �濡 ���� ������", user);
			send("/filedown �����̸� - ������ �濡 ���۵� ���� �ޱ� (���� ������ D://barbecue ������ ����˴ϴ�.)", user);
			send("/waitusers -���� ���� ��� ����", user);
			send("/invite ������ -���� �ʴ�(���濡 �ִ� ������ �ʴ� ����)", user);
			send("/y �ʴ밡 ������ ���� �ֱ��� �ʴ밡 �¶���", user);
			send("/n �ʴ븦 ������", user);
			send("/setpw ��й�ȣ - ���� �� ��й�ȣ ����(���常 ����)", user);
			send("/kick �����̸� - ���� �� ���� ����(���常 ����)", user);
			send("/headchange �����̸� - �ش� �������� ���� �ѱ��(���常 ����)", user);
			send("/destroy - ���� �� ����(���常 ����)", user);
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
			return "	* ����:��ȿ���� ���� ��ɾ�";
		}
	}

	private String FileList(String[] tokens, User user) throws IOException {
		if (user.getCurrentRoom().getFiles().size() == 0) {
			return "	* " + user.getCurrentRoom().getName() + "�� ���ε�� ������ �����ϴ�.";
		}
		Set<String> set = user.getCurrentRoom().getFiles().keySet();
		Iterator<String> it = set.iterator();
		send(bar, user);
		send("	*** " + user.getCurrentRoom().getName() + "��  ���� ��� ***", user);
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
		send("	*** " + chatRooms.get("__waitting__").getName() + "�� ���� ��� ***", user);
		while (it.hasNext()) {
			send(it.next(), user);
		}
		send(bar, user);
		return null;
	}

	private String FileReceive(String[] tokens, User user) throws IOException {
		if (user.getCurrentRoom().getFiles().containsKey(tokens[1])) {// �ش� �̸��� ������ �����ϸ�
			user.fs = new FileSender(tokens[1], user.getCurrentRoom().getName(), user.getChatOut(), user.getFileOut());
			user.fs.start();
		} else {
			return "	* ���� : �ش� �̸��� ���� ������ �������� �ʽ��ϴ�.";
		}
		return null;
	}

	synchronized private String invite(String[] tokens, User user) throws IOException {// ���ǿ� �ִ� ������ �ʴ��ϴ� �޼���
		if (chatRooms.get("__waitting__").getUsers().containsKey(tokens[1])) {// ���ǿ� �ش� ������ ������
			User us = chatRooms.get("__waitting__").getUsers().get(tokens[1]);
			us.setInvite(user.getCurrentRoom().getName());// �ش� ������ intvite�� �ʴ��� ������ ����� ����
			us.setInvitepw(user.getCurrentRoom().getPassword());// ��й�ȣ�� ����
			us.getChatOut().writeUTF(user.getName() + "���� " + user.getCurrentRoom().getName() + "���� ����� �ʴ��Ͽ����ϴ�.");
			us.getChatOut().writeUTF("�ʴ븦 �����Ϸ��� /y �����Ϸ��� /n�� �Է��Ͻʽÿ�.");
			return "	* " + us.getName() + "���� �ʴ��Ͽ����ϴ�..";
		} else {
			return "	* ���� : ���ǿ� �ش� ������ �������� �ʽ��ϴ�.";
		}
	}

	private String n(String[] tokens, User user) {// ���� �ֱ� �ʴ븦 �����ϴ� �޼���
		user.setInvite(null);
		user.setInvitepw(null);
		return "	* �ʴ븦 �����Ͽ����ϴ�.";
	}

	synchronized private String y(String[] tokens, User user) throws IOException {// ���� �ֱ� �ʴ븦 �����ϴ� �޼���
		if (user.getInvite() == null) {
			return "	* ���� : �ʴ븦 �̹� �����Ͽ��ų� �ʴ���� ���� �����ϴ�.";
		}
		if (chatRooms.containsKey(user.getInvite())) {// �ʴ���� ���� �����ϸ�
			tokens = new String[] { "", user.getInvite(), user.getInvitepw() };
			return roomJoin(tokens, user);// ���ν�Ų��
		} else {
			return "	* ���� : �ʴ���� ���� ���� �������� �ʽ��ϴ�.";
		}
	}

	private boolean isOwner(User user) {
		return user.getName().equals(user.getCurrentRoom().getOwner());
	}

	private String owner(String[] tokens, User user) {
		return "	* " + user.getCurrentRoom().getName() + "�� ���� : " + user.getCurrentRoom().getOwner();
	}

	synchronized private String headChange(String[] tokens, User user) throws IOException {
		// ���� ��ū ������ ��������, ���� ��ū�� ������ �ش� ������ �ִ��� Ȯ���ϰ� ������ �����޼���, ������ ���� ����
		// TODO �ڵ� ������ �޼ҵ� ����
		if (isOwner(user)) {// ��ɾ ȣ���� ������ �������� Ȯ��
			if (tokens.length == 2) {// ������ ������ ���
				if (user.getCurrentRoom().getUsers().containsKey(tokens[1])) {// ������ ������ �����ϴ� ���
					user.getCurrentRoom().setOwner(tokens[1]);
					sendToAll("	* " + tokens[1] + "������ ������ ����Ǿ����ϴ�.", user);
					System.out.println(user.getCurrentRoom() + "�� ������" + tokens[1] + "�� �����");
					return "	* ���� : " + tokens[1] + "�� ���� �����";
				} else {// ������ ������ �������� �ʴ� ���
					return "	* ���� : ������ ������ �ش� �濡 �������� �ʽ��ϴ�.";
				}
			} else {// ������ �������� ���� ���
				Set<String> set = user.getCurrentRoom().getUsers().keySet();
				Iterator<String> it = set.iterator();
				if (it.hasNext()) {
					user.getCurrentRoom().setOwner(it.next());
					sendToAll("	* " + user.getCurrentRoom().getOwner() + "������ ������ ����Ǿ����ϴ�.", user);
					return "	* ���� : �������� ������ ����Ǿ����ϴ�.";
				} else {
					return "	* ���� : ������ �ѱ� ������ �����ϴ�.";
				}
			}
		} else {// ������ �ƴ� ���
			return "	* ���� : ������ �ƴϱ� ������ ������ ������ ������ �����ϴ�.";
		}
	}

	synchronized private String roomList(String[] tokens, User user) throws IOException {// �� ����Ʈ ������
		Set<String> set = chatRooms.keySet();
		Iterator<String> it = set.iterator();
		send(bar, user);
		while (it.hasNext()) {
			String rn = it.next();
			send(" * " + rn + " : " + chatRooms.get(rn).getUsers().size() + "��", user);
		}
		send(bar, user);
		return null;
	}

	private String roomDestroy(String[] tokens, User user) {// �� ����(�������)
		if (isOwner(user)) {// ������ ������
			try {
				return roomDestroy2(user.getCurrentRoom().getName());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			return "	* ���� : �� ������ ������ ������ �����ϴ�.";
		}
		// TODO �ڵ� ������ �޼ҵ� ����
		return null;
	}

	synchronized private String roomDestroy2(String roomName) throws IOException {// room�� �Ű������� �޴� �� ����
		chatRooms.remove(roomName);
		return "[" + roomName + "]���� �����Ǿ����ϴ�.";
	}

	synchronized private String kick(String[] tokens, User user) {
		if (isOwner(user)) {// �����̰�
			if (user.getCurrentRoom().getUsers().containsKey(tokens[1])) {// �Է��� ������ �����ϸ�
				User kickUser = user.getCurrentRoom().getUsers().get(tokens[1]);
				if (!user.getCurrentRoom().removeMember(kickUser.getName())) {
					return "	* ���� : �ش� ������ �����Ű�� ���߽��ϴ�.";
				}
				chatRooms.get("__waitting__").getUsers().put(kickUser.getName(), kickUser);
				kickUser.setCurrentRoom(chatRooms.get("__waitting__"));
				try {
					kickUser.getChatOut().writeUTF("���忡 ���� " + user.getCurrentRoom().getName() + "���� ���� ����Ǿ����ϴ�.");
				} catch (IOException e) {
					e.printStackTrace();
				}
				return "	* ���� : �ش� ������ ���� ������׽��ϴ�.";
			} else {
				return "	* ���� : ������ ������ �ش� �濡 �������� �ʽ��ϴ�.";
			}
		} else {
			return "	* ���� : ������ �ƴϹǷ� �߹��� ������ �����ϴ�.";
		}
	}

	private String setpw(String[] tokens, User user) {// ��й�ȣ �����ϴ� �޼���
		if (isOwner(user)) {
			user.getCurrentRoom().setPassword(tokens[1]);
			return "	* ���� : �н����带 �����߽��ϴ�.";
		} else {
			return "	* ���� : ������ �ƴϹǷ� �н����带 ������ ������ �����ϴ�.";
		}
	}

	synchronized private String users(String[] tokens, User user) throws IOException {// ���� ��� ������
		Set<String> set = user.getCurrentRoom().getUsers().keySet();
		Iterator<String> it = set.iterator();
		send(bar, user);
		send("	*** " + user.getCurrentRoom().getName() + "�� ���� ��� ***", user);
		while (it.hasNext()) {
			send(it.next(), user);
		}
		send(bar, user);
		return null;
	}

	synchronized private String roomJoin(String[] tokens, User user) throws IOException {// �濡 ���� �޼ҵ�
		if (chatRooms.containsKey(tokens[1])) {// �Է��� �� �̸��� ������ ���
			if (user.getCurrentRoom().getName().equals(tokens[1])) {// �Է��� �� �̸��� �ڱⰡ �ִ� ���� ���
				return "	* ���� : �̹� �ش� �濡 ������ �ֽ��ϴ�.";
			} else {
				if (chatRooms.get(tokens[1]).getPassword() != null) {// �ش� �濡 ��й�ȣ�� ������ ���
					if (tokens.length > 1 && tokens[2].equals(chatRooms.get(tokens[1]).getPassword())) {// ��й�ȣ�� ��ġ�� ���
						roomex(user, false);// ���� �濡�� �����ϰ�
						chatRooms.get(tokens[1]).addMember(user);
						user.setCurrentRoom(chatRooms.get(tokens[1]));
						sendToAll("	* " + user.getName() + "���� �����ϼ̽��ϴ�.", user);
						System.out.println(user.getName() + "���� " + user.getCurrentRoom().getName() + "�� �����Ͽ����ϴ�.");
						System.out.println("[" + user.getCurrentRoom().getName() + "]�� ������ ��� : "
								+ user.getCurrentRoom().getUsers().keySet().toString());
						return "	* " + user.getCurrentRoom().getName() + "�� ����Ǿ����ϴ�.";
					} else {
						return "	* ���� : �н����尡 ��ġ���� �ʽ��ϴ�.";
					}
				} else {// ��й�ȣ�� �������� ���� ���
					roomex(user, false);// ���� �濡�� �����ϰ�
					chatRooms.get(tokens[1]).addMember(user);
					user.setCurrentRoom(chatRooms.get(tokens[1]));
					sendToAll("	* " + user.getName() + "���� �����ϼ̽��ϴ�.", user);
					System.out.println(user.getName() + "���� " + user.getCurrentRoom().getName() + "�� �����Ͽ����ϴ�.");
					System.out.println("[" + user.getCurrentRoom().getName() + "]�� ������ ��� : "
							+ user.getCurrentRoom().getUsers().keySet().toString());
					return "	* " + user.getCurrentRoom().getName() + "�� ����Ǿ����ϴ�.";
				}
			}
		} else {
			return "	* ���� : �������� �ʴ� �� �̸��Դϴ�.";
		}
	}

	private String sysExit(User user) throws IOException {// �濡�� �����Ű�� Ŀ�ǵ� �θ� ���� �ý��� �����Ŵ
		roomex(user, false);
		return "��";
	}

	private String roomExit(String[] tokens, User user) throws IOException {// �濡�� �����ϱ� �� �����̾����� üũ
		if (user.getCurrentRoom().getName().equals("__waitting__")) {
			return "	* ���� : ���濡���� ������ �� �����ϴ�.";
		}
		return roomex(user, true);
	}

	synchronized private String roomex(User user, boolean gowait) throws IOException {// �濡�� ����
		ChatRoom exRoom = user.getCurrentRoom();
		if (!exRoom.getName().equals("__waitting__")) {
			sendToAll("	* [" + user.getName() + "]���� [" + user.getCurrentRoom().getName() + "]���� �����Ͽ����ϴ�.", user);
		}
		boolean alone = user.getCurrentRoom().getUsers().size() == 1;// �濡 ȥ�� �־����� üũ

		if (isOwner(user) && !alone) {// ���ʿ��� ȥ�ڰ� �ƴϾ�����
			String[] tokens = new String[] { "" };
			headChange(tokens, user);// ������ �ٲ۴�
		}
		if (!user.getCurrentRoom().removeMember(user.getName())) {
			System.out.println("����! roomex�� removeMember ����-" + user.getName());
			return "	* ���� : �ش� ������ �����Ű�� ���߽��ϴ�.";
		}

		System.out.println("[" + user.getName() + "]���� [" + exRoom.getName() + "]���� �����Ͽ����ϴ�.");
		System.out.println("[" + exRoom.getName() + "]�� ������ ��� : " + exRoom.getUsers().keySet().toString());

		if (gowait) {
			chatRooms.get("__waitting__").getUsers().put(user.getName(), user);
			user.setCurrentRoom(chatRooms.get("__waitting__"));
			if (alone && isOwner(user)) {// �濡 ȥ�� �ְ� ���ʿ�����(������ ȥ���־�� ���ָ� �ȵǴϱ�)
				try {
					send(roomDestroy2(exRoom.getName()), user);// ���� �ִ� ���� �μ���
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return "	* �������� �̵��Ǿ����ϴ�.";
		} else {
			return "	* ���� �濡�� ����Ǿ����ϴ�.";
		}

	}

	synchronized private String create(String[] tokens, User user) throws IOException {
		if (user.getCurrentRoom().getName().equals(chatRooms.get("__waitting__").getName())) {
			ChatRoom room = null;
			if (tokens.length == 2 || tokens.length == 3) {
				if (tokens[1].matches("[0-9|a-z|A-Z|��-��|��-��|��-��]*") && tokens[1].length() <= 10) {// �� �̸��� ���ǿ� �´��� �˻�
					if (chatRooms.containsKey(tokens[1])) {// ���̸� �ߺ��˻�
						return "	* ���� : �ߺ��� �� �̸��� �ֽ��ϴ�.";
					} else {
						if (tokens.length == 2) {
							room = new ChatRoom(tokens[1], user.getName());
						} else {
							room = new ChatRoom(tokens[1], user.getName(), tokens[2]);
						}
					}
				} else {
					return "	* ���� : �� ������ ���ǿ� ���� ����";
				}
			} else {
				return "	* ���� : ��ɾ� ������ �������� ����";
			}
			chatRooms.put(room.getName(), room);
			String exRoomName = user.getCurrentRoom().getName();
			user.getCurrentRoom().getUsers().remove(user.getName());
			System.out.println("[" + user.getName() + "]���� [" + exRoomName + "]���� �����Ͽ����ϴ�.");
			room.getUsers().put(user.getName(), user);
			System.out.println("[" + user.getName() + "]���� [" + room.getName() + "]�� ���� �� �����Ͽ����ϴ�.");
			user.setCurrentRoom(room);

			try {
				send("	* [" + user.getCurrentRoom().getName() + "]�� ����Ǿ����ϴ�.", user);
				send("	* [" + user.getName() + "]���� [" + user.getCurrentRoom().getName() + "]�� �����Դϴ�.", user);
				send("	* ����� ���ÿ� ���� ������ �Ұ� �Ǹ�, �� ���� �� �����ϰ� ������ �ο��˴ϴ�.", user);
			} catch (IOException e) {
				// TODO �ڵ� ������ catch ���
				e.printStackTrace();
			}
			return "	* ���� : �� ���� �Ϸ�";
		} else {// ������ �ƴ� �濡�� ȣ������ ���
			return "	* ���� : ������ �ƴ� �濡�� �� ���� �Ұ�";
		}
	}

}
