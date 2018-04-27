package client;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import server.FileReceiver;

public class ClientReceiver extends Thread {
	DataInputStream in;
	BufferedInputStream Filein;
	Socket socket;
	Socket FileSocket;
	int tmp = 0;

	ClientSender sender;

	ClientReceiver(Socket socket, Socket FileSocket, ClientSender sender) {
		this.sender = sender;
		try {
			in = new DataInputStream(socket.getInputStream());
			Filein = new BufferedInputStream(FileSocket.getInputStream());
		} catch (IOException e) {
		}
	}

	public void run() {
		while (in != null) {
			try {
				String msg = in.readUTF();
				if (msg.equals("��")) {// ���α׷� ����
					System.exit(0);
				} else if (msg.startsWith("��")) {// ���� �ޱ�
					String[] cmd = msg.split("��");
					FileReceiver fr = new FileReceiver(cmd[1], Integer.parseInt(cmd[2]), Filein, sender.out);
					fr.start();
				} else if (msg.equals("��")) {// ��밡 ���� �ٿ� �Ϸ�
					if (sender.filesender != null) {
						sender.filesender.interrupt();
						while (sender.filesender.isAlive()) {
						}
						sender.filesender = null;
						System.out.println("���ϻ��� �ʱ�ȭ �Ϸ�");
					} else {
						System.out.println("���� ���� ���� ����");
					}
				} else {
					System.out.println(msg);
				}
			} catch (IOException e) {
			}
		}
		try {
			in.close();
			Filein.close();
			socket.close();
			FileSocket.close();
			System.out.println("������ ����Ǿ����ϴ�.");
		} catch (IOException e) {
			// TODO �ڵ� ������ catch ���
			e.printStackTrace();
		}
	} // run
} // ClientReceiver