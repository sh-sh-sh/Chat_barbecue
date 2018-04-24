package client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientSender extends Thread {
	DataOutputStream out;
	DataOutputStream Fileout;
	String name;
	private Scanner sc;

	ClientSender(Socket socket, Socket FileSocket, String name) {
		try {
			out = new DataOutputStream(socket.getOutputStream());
			Fileout = new DataOutputStream(FileSocket.getOutputStream());
			this.name = name;
		} catch (Exception e) {
		}
	}

	public void run() {
		try {
			out.writeUTF(name);

			sc = new Scanner(System.in);
			while (out != null) {
				out.writeUTF("[" + name + "]" + sc.nextLine());
			}
		} catch (IOException e) {
		} finally {
			try {
				out.close();
				Fileout.close();
				System.out.println("전송이 종료되었습니다.");
			} catch (IOException e) {
				// TODO 자동 생성된 catch 블록
				e.printStackTrace();
			}
		}
	} // run()
} // ClientSender