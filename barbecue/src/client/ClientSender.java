package client;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner; 

public class ClientSender extends Thread {
	DataOutputStream out;
	BufferedOutputStream Fileout;
	private Scanner sc;
	FileSender filesender;

	ClientSender(Socket socket, Socket FileSocket) {
		try {
			out = new DataOutputStream(socket.getOutputStream());
			Fileout = new BufferedOutputStream(FileSocket.getOutputStream());
		} catch (Exception e) {
		}
	}

	public void run() {
		try {
			sc = new Scanner(System.in);
			while (out != null) {
				String msg = sc.nextLine();
				if (msg.startsWith("/fileup")) {
					String[] token = msg.split("[ ]+");
					filesender = new FileSender(token[1], out, Fileout);
					filesender.start();
				} else {
					out.writeUTF(msg);
				}
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
