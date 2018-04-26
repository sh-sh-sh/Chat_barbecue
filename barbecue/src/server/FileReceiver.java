package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class FileReceiver extends Thread {
	Socket socket;
	BufferedInputStream in;
	BufferedOutputStream bos;
	String filename;
	String src;

	FileReceiver(String filename, String roomname, BufferedInputStream in) {// 서버용
		this.filename = filename;
		this.in = in;
		this.src = "D://barbecue//" + roomname + "//" + filename;
	}

	public FileReceiver(String filename, BufferedInputStream in) {// 사용자용
		this.filename = filename;
		this.in = in;
		this.src = "D://barbecue//" + filename;
	}

	public void run() {
		File file = new File(src);
		if (!file.getParentFile().exists()) {
			System.out.println(file.getParentFile());
			if (file.getParentFile().mkdir()) {
				System.out.println("경로 생성 성공");
			} else {
				if (!file.getParentFile().getParentFile().exists()) {
					if (file.getParentFile().getParentFile().mkdir()) {
						if (file.getParentFile().mkdir()) {
							System.out.println("경로 생성 성공2");
						} else {
							System.out.println("경로 생성 실패 2");
						}
					} else {
						System.out.println("경로 생성 실패 1");
					}
				}
			}
			;
		}
		byte[] content = new byte[1024];
		try {
			bos = new BufferedOutputStream(new FileOutputStream(src));
		} catch (FileNotFoundException e1) {
			// TODO 자동 생성된 catch 블록
			e1.printStackTrace();
		}
		int len = 0;
		int total = 0;
		try {
			while ((len = in.read(content, 0, content.length)) != -1) {
				bos.write(content, 0, len);
				total += len;
				System.out.println("진행중: " + total + "bytes 다운로드 됨...");
			}
			bos.flush();
			bos.close();
			System.out.println(filename + "(" + total + "bytes) 다운로드 완료");
		} catch (IOException e) {
			e.printStackTrace();
		}
		interrupt();
	} // run
}
