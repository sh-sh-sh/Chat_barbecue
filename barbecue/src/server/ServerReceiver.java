package server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class ServerReceiver extends Thread {

	User user;
	DataInputStream in;
	BufferedInputStream filein;

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
			if (!rooms.checkName("__waitting__", user)) {
				System.out.println("���� 00 : checkName ���� - " + user.getName());
			}

			if (!rooms.addMember("__waitting__", user)) {
				System.out.println("���� 01 : addmember ���� - " + user.getName());
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
					String cmsg = rooms.processCmd(msg.substring(1), user);
					if (cmsg != null) {
						send(cmsg);// Ŀ��带 ó���� ��� �޼��� ����
					}
				} else if (msg.startsWith("��")) {// ���� ���ŵ�
					System.out.println("���� ���ŵ�2");
					String[] cmd = msg.split("��");
					System.out.println(Arrays.toString(cmd));
					FileReceiver fr = new FileReceiver(cmd[1], Integer.parseInt(cmd[2]), user.getCurrentRoom().getName(), filein,
							user.getChatOut());
					fr.start();
					sleep(1000);
					user.getCurrentRoom().getFiles().put(cmd[1], new File("D://" + user.getCurrentRoom() + "//" + cmd[1]));
					rooms.sendToAll("	* " + user.getName() + "���� " + cmd[1] + "������ ���½��ϴ�. ������ �������� /filedown �����̸� �� �Է��ϼ���.",
							user);
				} else if (msg.equals("��")) {// ������ ���� �ٿ� �Ϸ���
					if (user.fs != null) {
						user.fs.interrupt();
						while (user.fs.isAlive()) {
						}
						// System.out.println("���� ���� �Ϸ�. ���� ���� ������ ��ħ");
						user.fs = null;
						System.out.println("���ϻ��� ����2");
					} else {
						System.out.println("���� ���� ���� ����");
					}
				} else {// massage
					if (msg.contains("��") || msg.contains("��") || msg.contains("��")) {
						send("	* : ����� �� ���� Ư�����ڸ� �Է��ϼ̽��ϴ�.");
					} else {
						rooms.sendToAll(msg, user);
					}
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
				if (!rooms.removeMember(user.getCurrentRoom().getName(), user.getName())) {
					System.out.println("���� 02 : removeMember ���� - " + user.getName());
				}
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
}
