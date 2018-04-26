package server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;

public class ServerReceiver extends Thread {

	User user;
	DataInputStream in;
	BufferedInputStream filein;
	final String bar = "-------------------------------------------------------------------------";

	// Map<String, ChatRoom> chatRooms;

	Rooms rooms;

	public ServerReceiver(User user, Rooms rooms) {
		this.user = user;
		this.rooms = rooms;
		rooms.setCurrentRoom("__waitting__", user);
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
			rooms.checkName("__waitting__", user);

			if (!rooms.addMember("__waitting__", user)) {
				System.out.println("���� 01:addmember ����");
			}
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
					rooms.sendToAll("	* " + user.getName() + "���� " + cmd[1] + "������ ���½��ϴ�. ������ �������� /���Ϲޱ� �����̸� �� �Է��ϼ���.", user);
				} else {// massage
					rooms.sendToAll(msg, user);
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
				rooms.sendToAll("	* [" + user.getName() + "]���� ���α׷��� �����Ͽ����ϴ�.", user);
				System.out.println(" [" + a.getName() + "]���� [" + user.getName() + "]���� ������ �����Ͽ����ϴ�.");
				System.out.println("���� [" + a.getName() + "] �� ������ ���� " + a.getUsers().size() + "�Դϴ�.");
				System.out.println("[" + a.getName() + "]�� ������ ���:" + a.getUsers().keySet().toString());
			} catch (IOException e) {
				// TODO �ڵ� ������ catch ���
				e.printStackTrace();
			}

		}
	}

	String processCmd(String cmd) throws IOException {
		return rooms.processCmd(cmd, user);
	}

}
