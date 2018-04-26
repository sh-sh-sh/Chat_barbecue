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

	FileReceiver(String filename, String roomname, BufferedInputStream in) {// ������
		this.filename = filename;
		this.in = in;
		this.src = "D://barbecue//" + roomname + "//" + filename;
	}

	public FileReceiver(String filename, BufferedInputStream in) {// ����ڿ�
		this.filename = filename;
		this.in = in;
		this.src = "D://barbecue//" + filename;
	}

	public void run() {
		File file = new File(src);
		if (!file.getParentFile().exists()) {
			System.out.println(file.getParentFile());
			if (file.getParentFile().mkdir()) {
				System.out.println("��� ���� ����");
			} else {
				if (!file.getParentFile().getParentFile().exists()) {
					if (file.getParentFile().getParentFile().mkdir()) {
						if (file.getParentFile().mkdir()) {
							System.out.println("��� ���� ����2");
						} else {
							System.out.println("��� ���� ���� 2");
						}
					} else {
						System.out.println("��� ���� ���� 1");
					}
				}
			}
			;
		}
		byte[] content = new byte[1024];
		try {
			bos = new BufferedOutputStream(new FileOutputStream(src));
		} catch (FileNotFoundException e1) {
			// TODO �ڵ� ������ catch ���
			e1.printStackTrace();
		}
		int len = 0;
		int total = 0;
		try {
			while ((len = in.read(content, 0, content.length)) != -1) {
				bos.write(content, 0, len);
				total += len;
				System.out.println("������: " + total + "bytes �ٿ�ε� ��...");
			}
			bos.flush();
			bos.close();
			System.out.println(filename + "(" + total + "bytes) �ٿ�ε� �Ϸ�");
		} catch (IOException e) {
			e.printStackTrace();
		}
		interrupt();
	} // run
}
