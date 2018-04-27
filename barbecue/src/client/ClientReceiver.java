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
				if (msg.equals("ㅹ")) {// 프로그램 종료
					System.exit(0);
				} else if (msg.startsWith("ㅨ")) {// 파일 받기
					String[] cmd = msg.split("ㅨ");
					System.out.println("파일 수신됨1");
					System.out.println("cmd[0]:" + cmd[0] + ",cmd[1]:" + cmd[1]);
					FileReceiver fr = new FileReceiver(cmd[0], Integer.parseInt(cmd[1]), Filein, sender.out);
					fr.start();
				} else if (msg.equals("ㅱ")) {// 상대가 파일 다운 완료
					if (sender.filesender != null) {
						sender.filesender.interrupt();
						while (!sender.filesender.isInterrupted()) {// 인터럽트 대기
							System.out.println("인터럽트 대기중");
						}
						sender.filesender = null;
						System.out.println("파일샌더 초기화 완료");
					} else {
						System.out.println("파일 전송 닫힘 에러");
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
			System.out.println("수신이 종료되었습니다.");
		} catch (IOException e) {
			// TODO 자동 생성된 catch 블록
			e.printStackTrace();
		}
	} // run
} // ClientReceiver