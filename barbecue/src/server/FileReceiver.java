package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileReceiver extends Thread {
	BufferedInputStream in;
	BufferedOutputStream bos;
	DataOutputStream out;
	String filename;
	String src;
	int size;

	FileReceiver(String filename, int size, String roomname, BufferedInputStream in, DataOutputStream out) {// 서버용
		this.filename = filename;
		this.in = in;
		this.src = "D://barbecue//" + roomname + "//" + filename;
		this.out = out;
		this.size = size;
	}

	public FileReceiver(String filename, int size, BufferedInputStream in, DataOutputStream out) {// 사용자용
		this.filename = filename;
		this.in = in;
		this.src = "D://barbecue//" + filename;
		this.out = out;
		this.size = size;
	}

	public void run() {
		File file = new File(src);
		if (!file.getParentFile().exists()) {// 파일을 생성할 폴더가 존재하지 않으면
			if (file.getParentFile().mkdir()) {// 폴더를 생성한다
				System.out.println("폴더 생성 성공 1");
			} else {// 폴더 생성에 실패했으면
				if (!file.getParentFile().getParentFile().exists()) {// 그 폴더의 상위 폴더가 존재하지 않으면
					if (file.getParentFile().getParentFile().mkdir()) {// 상위 폴더를 생성하고
						System.out.println("폴더 생성 성공 2");
						if (file.getParentFile().mkdir()) {// 폴더를 생성한다
							System.out.println("폴더 생성 성공 3");
						} else {
							System.out.println("경로 생성 실패 02 - 상위 디렉토리를 생성했으나 디렉토리 생성 실패");
						}
					} else {
						System.out.println("경로 생성 실패 03 - 상위 디렉토리 생성 불가");
					}
				} else {// 해당 폴더의 상위 폴더가 존재하는데도 안됐으면
					System.out.println("경로 생성 실패 01 - 상위 디렉토리 이미 존재함");
				}
			}
		} else {
			System.out.println("경로 이미 존재함");
		}
		byte[] content = new byte[1024];
		System.out.println("체크1");
		try {
			bos = new BufferedOutputStream(new FileOutputStream(src));
			System.out.println("체크2");
		} catch (FileNotFoundException e1) {
			// TODO 자동 생성된 catch 블록
			e1.printStackTrace();
		}
		System.out.println("체크3");
		int len = 0;
		int total = 0;
		try {
			while (total != size) {
				len = in.read(content, 0, content.length);
				System.out.println("체크4");
				bos.write(content, 0, len);
				total += len;
				System.out.println(filename + " 다운로드 진행중: " + total + "bytes 다운로드 됨...");
			}
			System.out.println("체크5");
			bos.flush();
			bos.close();
			System.out.println(filename + "(" + total + "bytes) 다운로드 완료");
			out.writeUTF("ㅱ");// 파일 전송자에게 파일 다운 완료했음을 알림
			System.out.println("체크6");
		} catch (IOException e) {
			e.printStackTrace();
		}
		interrupt();
	} // run
}
