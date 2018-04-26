package server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import client.FileSender;

public class ServerReceiver extends Thread {

	User user;
	DataInputStream in;
	BufferedInputStream filein;
	final String bar = "-------------------------------------------------------------------------";

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
			user.setName(user.getIp_port());
			checkName("__waitting__");

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
				} else if (msg.startsWith("��")) {// ���� ������
					String[] cmd = msg.split("��");
					FileReceiver fr = new FileReceiver(cmd[1], user.getCurrentRoom().getName(), filein);
					fr.start();
					sleep(1000);
					user.getCurrentRoom().getFiles().put(cmd[1], new File("D://" + user.getCurrentRoom() + "//" + cmd[1]));
					sendToAll("	* " + user.getName() + "���� " + cmd[1] + "������ ���½��ϴ�. ������ �������� /���Ϲޱ� �����̸� �� �Է��ϼ���.");
				} else {// massage
					sendToAll(msg);
				}
			}
		} catch (IOException e) {

		} catch (InterruptedException e) {
			// TODO �ڵ� ������ catch ���
			e.printStackTrace();
		} finally {
			try {
				in.close();
				filein.close();
				user.getChatSocket().close();
				user.getFileSocket().close();
				ChatRoom a = user.getCurrentRoom();
				a.removeMember(user.getName());
				sendToAll("	* [" + user.getName() + "]���� ���α׷��� �����Ͽ����ϴ�.");
				System.out.println(" [" + a.getName() + "]���� [" + user.getName() + "]���� ������ �����Ͽ����ϴ�.");
				System.out.println("���� [" + a.getName() + "] �� ������ ���� " + a.getUsers().size() + "�Դϴ�.");
				System.out.println("[" + a.getName() + "]�� ������ ���:" + a.getUsers().keySet().toString());
			} catch (IOException e) {
				// TODO �ڵ� ������ catch ���
				e.printStackTrace();
			}

		}
	}

	void checkName(String roomName) throws IOException {
		send("	* ����� �г����� �Է��ϼ���.");
		send("	* Ư������ �Է� �Ұ����ϸ�, 10���ڱ��� �Է� �����մϴ�.");
		String clientName = in.readUTF();
		if (clientName.matches("[0-9|a-z|A-Z|��-��|��-��|��-��]*") && clientName.length() <= 10) {// �г��� ���࿡ �ɸ��� ���� ���
			overlapCheck(clientName, roomName);// �ߺ��˻�
		} else {// Ư�����ڰ� ���� ���
			send("	* SYSTEM : �߸��� �Է��Դϴ�.");
			checkName(roomName);
		}
	}

	void overlapCheck(String name, String roomName) throws IOException {// �г��� �ߺ��˻�
		if (chatRooms.get(roomName).getUsers().size() == 0) {// �ش� �� ������ 0�ΰ�� �ߺ��˻� ����(ex:���� ���� ������)
			user.setName(name);
		}
		Set<String> set = chatRooms.get(roomName).getUsers().keySet();
		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			if (name.equals(it.next())) {// �濡 �ߺ� �г����� ���� ���
				send("	* SYSTEM : �濡 �ߺ��� �̸��� �ֽ��ϴ�. ");
				checkName(roomName);
			} else {// �濡 �ߺ� �г����� ���� ���
				user.setName(name);
			}
		}
	}

	void sendToAll(String msg) throws IOException {
		if (msg.equals("��")) {
			send("�ش� Ư�����ڴ� ����� �Ұ����մϴ�.");
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

	String processCmd(String cmd) throws IOException {
		String[] tokens = cmd.split("[ ]+");
		if (tokens[0].equals("?")) {
			send(bar);
			send("	*** ��ɾ� ��� ***");
			send("/create ���̸� ��й�ȣ(����) - �� ���� *����:�� �̸��� �ߺ��� �ƴ� ����, Ư������ ���� 10�ڸ� �Է� ����");
			send("/list - �� ����Ʈ �ҷ�����");
			send("/exit - �� ������(���濡���� �Ұ���)");
			send("/join �����̸� ��й�ȣ - �� ����");
			send("/users - �ش� �� ���� ��� ����");
			send("/owner - �ش� �� ���� ����");
			send("/sysexit - ���α׷� ����");
			send("/�������� �����̸� - ������ �濡 ���� ������");
			send("/���Ϲޱ� �����̸� - ������ �濡 ���۵� ���� �ޱ�");
			send("/invite ������ -���� �ʴ�(���濡 �ִ� ������ �ʴ� ����)");
			send("/y �ʴ밡 ������ ���� �ֱ��� �ʴ밡 �¶���");
			send("/n �ʴ븦 ������");
			send("/setpw ��й�ȣ - ���� �� ��й�ȣ ����(���常 ����)");
			send("/kick �����̸� - ���� �� ���� ����(���常 ����)");
			send("/headchange �����̸� - �ش� �������� ���� �ѱ��(���常 ����)");
			send("/destroy - ���� �� ����(���常 ����)");
			send(bar);
			return null;
		} else if (tokens[0].equals("create")) {
			return create(tokens);
		} else if (tokens[0].equals("list")) {
			return roomList(tokens);
		} else if (tokens[0].equals("exit")) {
			return roomExit(tokens);
		} else if (tokens[0].equals("join")) {
			return roomJoin(tokens);
		} else if (tokens[0].equals("sysexit")) {
			return sysExit();
		} else if (tokens[0].equals("users")) {
			return users(tokens);
		} else if (tokens[0].equals("owner")) {
			return owner(tokens);
		} else if (tokens[0].equals("invite")) {
			return invite(tokens);
		} else if (tokens[0].equals("y")) {
			return y(tokens);
		} else if (tokens[0].equals("n")) {
			return n(tokens);
		} else if (tokens[0].equals("���Ϲޱ�")) {
			return FileReceive(tokens[1]);
		} else if (tokens[0].equals("setpw")) {
			return setpw(tokens);
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

	private String FileReceive(String string) throws IOException {
		if (user.getCurrentRoom().getFiles().containsKey(string)) {// �ش� �̸��� ������ �����ϸ�
			FileSender fs = new FileSender(string, user.getCurrentRoom().getName(), user.getChatOut(), user.getFileOut());
			fs.start();
		} else {
			return "	* ���� : �ش� �̸��� ���� ������ �������� �ʽ��ϴ�.";
		}
		return null;
	}

	private String invite(String[] tokens) throws IOException {// ���ǿ� �ִ� ������ �ʴ��ϴ� �޼���
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

	private String n(String[] tokens) {// ���� �ֱ� �ʴ븦 �����ϴ� �޼���
		user.setInvite(null);
		user.setInvitepw(null);
		return "	* �ʴ븦 �����Ͽ����ϴ�.";
	}

	private String y(String[] tokens) throws IOException {// ���� �ֱ� �ʴ븦 �����ϴ� �޼���
		if (user.getInvite() == null) {
			return "	* ���� : �ʴ븦 �̹� �����Ͽ��ų� �ʴ���� ���� �����ϴ�.";
		}
		if (chatRooms.containsKey(user.getInvite())) {// �ʴ���� ���� �����ϸ�
			tokens = new String[] { "", user.getInvite(), user.getInvitepw() };
			return roomJoin(tokens);// ���ν�Ų��
		} else {
			return "	* ���� : �ʴ���� ���� ���� �������� �ʽ��ϴ�.";
		}
	}

	private boolean isOwner() {
		return user.getName().equals(user.getCurrentRoom().getOwner());
	}

	private String owner(String[] tokens) {
		return "	* " + user.getCurrentRoom().getName() + "�� ���� : " + user.getCurrentRoom().getOwner();
	}

	private String headChange(String[] tokens) throws IOException {
		// ���� ��ū ������ ��������, ���� ��ū�� ������ �ش� ������ �ִ��� Ȯ���ϰ� ������ �����޼���, ������ ���� ����
		// TODO �ڵ� ������ �޼ҵ� ����
		if (isOwner()) {// ��ɾ ȣ���� ������ �������� Ȯ��
			if (tokens.length == 2) {// ������ ������ ���
				if (user.getCurrentRoom().getUsers().containsKey(tokens[1])) {// ������ ������ �����ϴ� ���
					user.getCurrentRoom().setOwner(tokens[1]);
					sendToAll("	* " + tokens[1] + "������ ������ ����Ǿ����ϴ�.");
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
					sendToAll("	* " + user.getCurrentRoom().getOwner() + "������ ������ ����Ǿ����ϴ�.");
					return "	* ���� : �������� ������ ����Ǿ����ϴ�.";
				} else {
					return "	* ���� : ������ �ѱ� ������ �����ϴ�.";
				}
			}
		} else {// ������ �ƴ� ���
			return "	* ���� : ������ �ƴϱ� ������ ������ ������ ������ �����ϴ�.";
		}
	}

	private String roomList(String[] tokens) throws IOException {// �� ����Ʈ ������
		Set<String> set = chatRooms.keySet();
		Iterator<String> it = set.iterator();
		send(bar);
		while (it.hasNext()) {
			String rn = it.next();
			send(" * " + rn + " : " + chatRooms.get(rn).getUsers().size() + "��");
		}
		send(bar);
		return null;
	}

	private String roomDestroy(String[] tokens) {// �� ����(�������)
		if (isOwner()) {// ������ ������
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
		if (isOwner()) {// �����̰�
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

	private String setpw(String[] tokens) {// ��й�ȣ �����ϴ� �޼���
		if (isOwner()) {
			user.getCurrentRoom().setPassword(tokens[1]);
			return "	* ���� : �н����带 �����߽��ϴ�.";
		} else {
			return "	* ���� : ������ �ƴϹǷ� �н����带 ������ ������ �����ϴ�.";
		}
	}

	private String users(String[] tokens) throws IOException {// ���� ��� ������
		Set<String> set = user.getCurrentRoom().getUsers().keySet();
		Iterator<String> it = set.iterator();
		send(bar);
		send("	*** " + user.getCurrentRoom().getName() + "�� ���� ��� ***");
		while (it.hasNext()) {
			send(it.next());
		}
		send(bar);
		return null;
	}

	private String roomJoin(String[] tokens) throws IOException {// �濡 ���� �޼ҵ�
		if (chatRooms.containsKey(tokens[1])) {// �Է��� �� �̸��� ������ ���
			if (chatRooms.get(tokens[1]).getPassword() != null) {// �ش� �濡 ��й�ȣ�� ������ ���
				if (tokens.length > 1 && tokens[2].equals(chatRooms.get(tokens[1]).getPassword())) {// ��й�ȣ�� ��ġ�� ���
					roomex(tokens[1]);// ���� �濡�� �����ϰ�
					chatRooms.get(tokens[1]).addMember(user);
					user.setCurrentRoom(chatRooms.get(tokens[1]));
					sendToAll("	* " + user.getName() + "���� �����ϼ̽��ϴ�.");
					System.out.println(user.getName() + "���� " + user.getCurrentRoom().getName() + "�� �����Ͽ����ϴ�.");
					System.out.println("[" + user.getCurrentRoom().getName() + "]�� ������ ��� : "
							+ user.getCurrentRoom().getUsers().keySet().toString());
					return "	* " + user.getCurrentRoom().getName() + "�� ����Ǿ����ϴ�.";
				} else {
					return "	* ���� : �н����尡 ��ġ���� �ʽ��ϴ�.";
				}
			} else {// ��й�ȣ�� �������� ���� ���
				roomex(tokens[1]);// ���� �濡�� �����ϰ�
				chatRooms.get(tokens[1]).addMember(user);
				user.setCurrentRoom(chatRooms.get(tokens[1]));
				sendToAll("	* " + user.getName() + "���� �����ϼ̽��ϴ�.");
				System.out.println(user.getName() + "���� " + user.getCurrentRoom().getName() + "�� �����Ͽ����ϴ�.");
				System.out.println("[" + user.getCurrentRoom().getName() + "]�� ������ ��� : "
						+ user.getCurrentRoom().getUsers().keySet().toString());
				return "	* " + user.getCurrentRoom().getName() + "�� ����Ǿ����ϴ�.";
			}
		} else {
			return "	* ���� : �������� �ʴ� �� �̸��Դϴ�.";
		}
	}

	private String sysExit() throws IOException {// �濡�� �����Ű�� Ŀ�ǵ� �θ� ���� �ý��� �����Ŵ
		roomex(user.getCurrentRoom().getName());
		return "��";
	}

	private String roomExit(String[] tokens) throws IOException {// �濡�� �����ϱ� �� �����̾����� üũ
		if (user.getCurrentRoom().getName().equals("__waitting__")) {
			return "	* ���� : ���濡���� ������ �� �����ϴ�.";
		}
		return roomex(tokens[1]);
	}

	private String roomex(String roomName) throws IOException {// �濡�� ����
		ChatRoom exRoom = user.getCurrentRoom();
		sendToAll("	* [" + user.getName() + "]���� [" + user.getCurrentRoom().getName() + "]���� �����Ͽ����ϴ�.");
		boolean alone = user.getCurrentRoom().getUsers().size() == 1;// �濡 ȥ�� �־����� üũ

		if (isOwner() && !alone) {// ���ʿ��� ȥ�ڰ� �ƴϾ�����
			String[] tokens = new String[] { "" };
			headChange(tokens);// ������ �ٲ۴�
		}
		if (!user.getCurrentRoom().removeMember(user.getName())) {
			return "	* ���� : �ش� ������ �����Ű�� ���߽��ϴ�.";
		}
		System.out.println("[" + user.getName() + "]���� [" + exRoom.getName() + "]���� �����Ͽ����ϴ�.");
		System.out.println("[" + exRoom.getName() + "]�� ������ ��� : " + exRoom.getUsers().keySet().toString());
		chatRooms.get("__waitting__").getUsers().put(user.getName(), user);
		user.setCurrentRoom(chatRooms.get("__waitting__"));
		if (alone && isOwner()) {// �濡 ȥ�� �ְ� ���ʿ�����(������ ȥ���־�� ���ָ� �ȵǴϱ�)
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
				send("	* [" + user.getCurrentRoom().getName() + "]�� ����Ǿ����ϴ�.");
				send("	* [" + user.getName() + "]���� [" + user.getCurrentRoom().getName() + "]�� �����Դϴ�.");
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
