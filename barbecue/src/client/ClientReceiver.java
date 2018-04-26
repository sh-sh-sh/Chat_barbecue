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

	ClientReceiver(Socket socket, Socket FileSocket) {
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
					FileReceiver fr = new FileReceiver(msg.substring(1), Filein);
					fr.start();
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