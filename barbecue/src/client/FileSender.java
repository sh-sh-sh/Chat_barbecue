package client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class FileSender extends Thread {
	DataOutputStream out;
	BufferedOutputStream Fileout;
	String filename;
	BufferedInputStream bin;
	String src;

	FileSender(String filename, DataOutputStream out, BufferedOutputStream Fileout) {// 유저용
		this.out = out;
		this.Fileout = Fileout;
		this.filename = filename;
		this.src = filename;
	}

	public FileSender(String filename, String roomname, DataOutputStream out, BufferedOutputStream Fileout) {// 서버용
		this.out = out;
		this.Fileout = Fileout;
		this.filename = filename;
		this.src = "D://barbecue//" + roomname + "//" + filename;
	}

	public void run() {
		File file = new File(filename);
		if (file.exists()) {
			try {
				FileSend();
				out.writeUTF("ㅨ" + filename);
			} catch (IOException e) {
				// TODO 자동 생성된 catch 블록
				e.printStackTrace();
			}
		} else {
			System.out.println("존재하지 않는 파일입니다.");
		}
		try {
			sleep(300000);// 5분간 스레드 유지
		} catch (InterruptedException e) {
			// TODO 자동 생성된 catch 블록
			e.printStackTrace();
		}
		System.out.println("파일 전송용 스레드 마침");
		interrupt();
	} // run()

	public void FileSend() throws UnsupportedEncodingException, IOException {
		bin = new BufferedInputStream(new FileInputStream(src));
		byte[] bytes = new byte[1024];
		int len = 0;
		try {
			// 파일 내용
			int total = 0;
			while ((len = bin.read(bytes, 0, bytes.length)) != -1) {
				Fileout.write(bytes, 0, len);
				total += len;
			}
			Fileout.flush();
			Fileout.close();
			System.out.println("전송: " + total + "bytes 전송 완료됨");

		} catch (IOException e) {
		}
	}
}