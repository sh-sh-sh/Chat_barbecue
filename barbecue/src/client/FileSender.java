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

	FileSender(String filename, DataOutputStream out, BufferedOutputStream Fileout) {// ������
		this.out = out;
		this.Fileout = Fileout;
		this.filename = filename;
		this.src = filename;
	}

	public FileSender(String filename, String roomname, DataOutputStream out, BufferedOutputStream Fileout) {// ������
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
				out.writeUTF("��" + filename);
			} catch (IOException e) {
				// TODO �ڵ� ������ catch ���
				e.printStackTrace();
			}
		} else {
			System.out.println("�������� �ʴ� �����Դϴ�.");
		}
		try {
			sleep(300000);// 5�а� ������ ����
		} catch (InterruptedException e) {
			// TODO �ڵ� ������ catch ���
			e.printStackTrace();
		}
		System.out.println("���� ���ۿ� ������ ��ħ");
		interrupt();
	} // run()

	public void FileSend() throws UnsupportedEncodingException, IOException {
		bin = new BufferedInputStream(new FileInputStream(src));
		byte[] bytes = new byte[1024];
		int len = 0;
		try {
			// ���� ����
			int total = 0;
			while ((len = bin.read(bytes, 0, bytes.length)) != -1) {
				Fileout.write(bytes, 0, len);
				total += len;
			}
			Fileout.flush();
			Fileout.close();
			System.out.println("����: " + total + "bytes ���� �Ϸ��");

		} catch (IOException e) {
		}
	}
}