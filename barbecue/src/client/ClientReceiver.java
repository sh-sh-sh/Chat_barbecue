package client;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

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
				System.out.println(in.readUTF());
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