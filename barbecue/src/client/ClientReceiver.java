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
			System.out.println("수신이 종료되었습니다.");
		} catch (IOException e) {
			// TODO 자동 생성된 catch 블록
			e.printStackTrace();
		}
	} // run
} // ClientReceiver