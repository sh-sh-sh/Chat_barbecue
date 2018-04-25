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
			// TODO �ڵ� ������ catch ���
			e.printStackTrace();
		}

	}

	private void send(String msg) throws IOException {// ����ڿ��� �����ϴ� �޼����� ���� �޼ҵ� ���ΰ�
		user.getChatOut().writeUTF(msg);
	}

	public void run() {
		try {
			checkName(in.readUTF(), "__waitting__");

			chatRooms.get("__waitting__").addMember(user);
			System.out.println("[" + user.getName() + "]�� [" + user.getCurrentRoom().getName() + "]�� ��");
			System.out.println("[" + user.getCurrentRoom().getName() + "]�� ������ ��� : "
					+ user.getCurrentRoom().getUsers().keySet().toString());
			send("	* SYSTEM : " + user.getName() + "��, �ݰ����ϴ�.");
			send("	* SYSTEM : ��ɾ Ȯ���Ϸ��� /?�� �Է��� �ּ���.");
			System.out.println();
			String msg;
			while (in != null) {
				msg = in.readUTF();
				if (msg.startsWith("/")) {// command
					String cmsg = processCmd(msg.substring(1));
					if (cmsg != null) {
						send(cmsg);// Ŀ��带 ó���� ��� �޼��� ����
					}
				} else {// massage
					// ���� �� �����鿡�� �޼��� ����
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
				sendToAll("#" + user.getName() + "���� [" + a.getName() + "]��(��) �����̽��ϴ�.");
				System.out.println(" [" + a.getName() + "]���� [" + user.getName() + "]���� ������ �����Ͽ����ϴ�.");
				System.out.println("���� [" + a.getName() + "] �� ������ ���� " + a.getUsers().size() + "�Դϴ�.");
				System.out.println("[" + a.getName() + "]�� ������ ���:" + a.getUsers().keySet().toString());
			} catch (IOException e) {
				// TODO �ڵ� ������ catch ���
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
				send("	* SYSTEM : �濡 �ߺ��� �̸��� �ֽ��ϴ�. �̸��� �ٽ� �Է��� �ּ���.");
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
			send("	/create ���̸� ��й�ȣ(����) - �� ����");
			send("	/list - ����Ʈ �ҷ�����");
			send("	/exit - �� ������(���濡���� �Ұ���)");
			send("	/join - �� ����");
			send("	/users - �ش� �� ���� ��� ����");
			send("	/set ��й�ȣ - ���� �� ��й�ȣ ����(���常 ����)");
			send("	/kick �����̸� - ���� �� ���� ����(���常 ����)");
			send("	/headchange �����̸� - �ش� �������� ���� �ѱ��(���常 ����)");
			send("	/destroy - ���� �� ����(���常 ����)");
			return "	* �� ��ɾ�� �ܿ��� ���� ó���˴ϴ�.";
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
			return "	* ����:��ȿ���� ���� ��ɾ�";
		}
	}

	private String headChange(String[] tokens) {
		// ���� ��ū ������ ��������, ���� ��ū�� ������ �ش� ������ �ִ��� Ȯ���ϰ� ������ �����޼���, ������ ���� ����
		// TODO �ڵ� ������ �޼ҵ� ����
		if (tokens.length == 2) {// ������ ������ ���
			if (user.currentRoom.getUsers().containsKey(tokens[1])) {// ������ ������ �����ϴ� ���

			} else {// ������ ������ �������� �ʴ� ���

			}
		} else {// ������ �������� ���� ���

		}
		return null;
	}

	private String roomList(String[] tokens) {// �� ����Ʈ ������
		// TODO �ڵ� ������ �޼ҵ� ����
		return null;
	}

	private String roomDestroy(String[] tokens) {// �� ����(�������)
		if (user.getName().equals(user.getCurrentRoom().getOwner())) {// ������ ������
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

	private String roomDestroy2(String roomName) throws IOException {// room�� �Ű������� �޴� �� ����
		chatRooms.remove(roomName);
		return "[" + roomName + "]���� �����Ǿ����ϴ�.";
	}

	private String kick(String[] tokens) {
		// TODO �ڵ� ������ �޼ҵ� ����
		return null;
	}

	private String set(String[] tokens) {
		// TODO �ڵ� ������ �޼ҵ� ����
		return null;
	}

	private String users(String[] tokens) {
		// TODO �ڵ� ������ �޼ҵ� ����
		return null;
	}

	private String roomJoin(String[] tokens) {
		// TODO �ڵ� ������ �޼ҵ� ����
		return null;
	}

	private String roomExit(String[] tokens) {
		ChatRoom exRoom = null;
		if (user.getCurrentRoom().getUsers().size() == 1) {// �濡 ȥ�� �־����� üũ
			exRoom = user.getCurrentRoom();
		}
		if (user.getName().equals(user.getCurrentRoom().getOwner())) {
			tokens = new String[] { "" };
			headChange(tokens);
		}
		user.getCurrentRoom().getUsers().remove(user.getName());
		chatRooms.get("__waitting__").getUsers().put(user.getName(), user);
		user.setCurrentRoom(chatRooms.get("__waitting__"));
		if (exRoom != null) {// �濡 ȥ�� �־�����
			try {
				send(roomDestroy2(exRoom.getName()));// ���� �ִ� ���� �μ���
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "	* �������� �̵��Ǿ����ϴ�.";
	}

	private String create(String[] tokens) throws IOException {
		if (user.getCurrentRoom().getName().equals(chatRooms.get("__waitting__").getName())) {
			ChatRoom room = null;
			if (tokens.length == 2) {
				room = new ChatRoom(tokens[1], user.getIp_port());
			} else if (tokens.length == 3) {
				room = new ChatRoom(tokens[1], user.getIp_port(), tokens[2]);
			} else {
				return "	* ����:��ɾ� ������ �������� ����";
			}
			chatRooms.put(room.getName(), room);
			String exRoomName = user.getCurrentRoom().getName();
			user.getCurrentRoom().getUsers().remove(user.getName());
			System.out.println("[" + user.getName() + "]���� [" + exRoomName + "]���� �����Ͽ����ϴ�.");
			room.getUsers().put(user.getName(), user);
			System.out.println("[" + user.getName() + "]���� [" + room.getName() + "]�� ���� �� �����Ͽ����ϴ�.");
			user.setCurrentRoom(room);

			try {
				send("	* [" + user.currentRoom.getName() + "]�� ����Ǿ����ϴ�.");
				send("	* [" + user.getName() + "]���� [" + user.currentRoom.getName() + "]�� �����Դϴ�.");
				send("	* ����� ���ÿ� ���� ������ �Ұ� �Ǹ�, �� ���� �� �����ϰ� ������ �ο��˴ϴ�.");
			} catch (IOException e) {
				// TODO �ڵ� ������ catch ���
				e.printStackTrace();
			}
			return "	* ����:�� ���� �Ϸ�";
		} else {// ������ �ƴ� �濡�� ȣ������ ���
			return "	* ����:������ �ƴ� �濡�� �� ���� �Ұ�";
		}
	}
}
