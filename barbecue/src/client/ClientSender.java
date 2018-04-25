package client;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientSender extends Thread {
	DataOutputStream out;
	BufferedOutputStream Fileout;
	String name;
	private Scanner sc;

	ClientSender(Socket socket, Socket FileSocket, String name) {
		try {
			out = new DataOutputStream(socket.getOutputStream());
			Fileout = new BufferedOutputStream(FileSocket.getOutputStream());
			this.name = name;
		} catch (Exception e) {
		}
	}

	public void run() {
		try {
			out.writeUTF(name);

			sc = new Scanner(System.in);
			while (out != null) {
				out.writeUTF(sc.nextLine());
			}
		} catch (IOException e) {
		} finally {
			try {
				out.close();
				Fileout.close();
				System.out.println("������ ����Ǿ����ϴ�.");
			} catch (IOException e) {
				// TODO �ڵ� ������ catch ���
				e.printStackTrace();
			}
		}
	} // run()
} // ClientSender