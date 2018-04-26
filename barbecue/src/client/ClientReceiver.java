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
				if (msg.equals("ㅹ")) {// 프로그램 종료
					System.exit(0);
				} else if (msg.startsWith("ㅨ")) {// 파일 받기
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
			System.out.println("수신이 종료되었습니다.");
		} catch (IOException e) {
			// TODO 자동 생성된 catch 블록
			e.printStackTrace();
		}
	} // run
} // ClientReceiver