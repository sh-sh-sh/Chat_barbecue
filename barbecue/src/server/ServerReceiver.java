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
			// TODO 자동 생성된 catch 블록
			e.printStackTrace();
		}

	}

	private void send(String msg) throws IOException {// 사용자에게 전달하는 메세지에 쓰는 메소드 줄인거
		user.getChatOut().writeUTF(msg);
	}

	public void run() {
		try {
			user.setName(user.getIp_port());
			if (!rooms.checkName("__waitting__", user)) {
				System.out.println("에러 00 : checkName 실패 - " + user.getName());
			}

			if (!rooms.addMember("__waitting__", user)) {
				System.out.println("에러 01 : addmember 실패 - " + user.getName());
			}
			System.out.println("[" + user.getName() + "]이 [" + user.getCurrentRoom().getName() + "]에 들어감");
			System.out.println("[" + user.getCurrentRoom().getName() + "]의 접속자 목록 : "
					+ user.getCurrentRoom().getUsers().keySet().toString());
			send("	* SYSTEM : " + user.getName() + "님, 반갑습니다.");
			send("	* SYSTEM : 명령어를 확인하려면 /?를 입력해 주세요.");
			System.out.println();
			String msg;
			while (in != null) {
				msg = in.readUTF();
				if (msg.startsWith("/")) {// command
					String cmsg = rooms.processCmd(msg.substring(1), user);
					if (cmsg != null) {
						send(cmsg);// 커멘드를 처리한 결과 메세지 전송
					}
				} else if (msg.startsWith("ㅨ")) {// 파일 수신됨
					System.out.println("파일 수신됨2");
					String[] cmd = msg.split("ㅨ");
					System.out.println(Arrays.toString(cmd));
					FileReceiver fr = new FileReceiver(cmd[1], Integer.parseInt(cmd[2]), user.getCurrentRoom().getName(), filein,
							user.getChatOut());
					fr.start();
					sleep(1000);
					user.getCurrentRoom().getFiles().put(cmd[1], new File("D://" + user.getCurrentRoom() + "//" + cmd[1]));
					rooms.sendToAll("	* " + user.getName() + "님이 " + cmd[1] + "파일을 보냈습니다. 파일을 받으려면 /filedown 파일이름 을 입력하세요.",
							user);
				} else if (msg.equals("ㅱ")) {// 상대방이 파일 다운 완료함
					if (user.fs != null) {
						user.fs.interrupt();
						while (user.fs.isAlive()) {
						}
						// System.out.println("파일 전송 완료. 파일 전송 스레드 마침");
						user.fs = null;
						System.out.println("파일샌더 닫음2");
					} else {
						System.out.println("파일 전송 닫힘 에러");
					}
				} else {// massage
					if (msg.contains("ㅱ") || msg.contains("ㅨ") || msg.contains("ㅹ")) {
						send("	* : 사용할 수 없는 특수문자를 입력하셨습니다.");
					} else {
						rooms.sendToAll(msg, user);
					}
				}
			}
		} catch (IOException e) {

		} catch (InterruptedException e) {
			// TODO 자동 생성된 catch 블록
			e.printStackTrace();
		} finally {
			try {
				in.close();
				filein.close();
				user.getChatSocket().close();
				user.getFileSocket().close();
				ChatRoom a = user.getCurrentRoom();
				if (!rooms.removeMember(user.getCurrentRoom().getName(), user.getName())) {
					System.out.println("에러 02 : removeMember 실패 - " + user.getName());
				}
				rooms.sendToAll("	* [" + user.getName() + "]님이 프로그램을 종료하였습니다.", user);
				System.out.println(" [" + a.getName() + "]에서 [" + user.getName() + "]님이 접속을 종료하였습니다.");
				System.out.println("현재 [" + a.getName() + "] 방 접속자 수는 " + a.getUsers().size() + "입니다.");
				System.out.println("[" + a.getName() + "]의 접속자 목록:" + a.getUsers().keySet().toString());
			} catch (IOException e) {
				// TODO 자동 생성된 catch 블록
				e.printStackTrace();
			}
		}
	}
}
