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
	boolean sys;

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
		File file = new File(src);
		if (file.exists()) {
			try {
				System.out.println("üũ1:" + file.length());
				FileSend();
				System.out.println(filename + "���� �غ� �Ϸ�");
				out.writeUTF("��" + filename + "��" + file.length());
			} catch (IOException e) {
				// TODO �ڵ� ������ catch ���
				e.printStackTrace();
			}
		} else {
			System.out.println("�������� �ʴ� �����Դϴ�.");
		}
		try {
			sleep(300000);
		} catch (InterruptedException e) {
			System.out.println(filename + "���� �Ϸ�. ���� ���� �����带 ��Ĩ�ϴ�.");
		}
		// System.out.println("���� ���ۿ� ������ ��ħ");
		// interrupt();
	} // run()

	public void FileSend() throws UnsupportedEncodingException, IOException {
		bin = new BufferedInputStream(new FileInputStream(src));
		byte[] bytes = new byte[1024];
		int len = 0;
		try {
			System.out.println("üũ2");
			// ���� ����
			int total = 0;
			while ((len = bin.read(bytes, 0, bytes.length)) != -1) {
				System.out.println("üũ3");
				Fileout.write(bytes, 0, len);
				total += len;
			}
			System.out.println("üũ4");
			Fileout.flush();
			System.out.println("����: " + filename + "(" + total + ")bytes ���� �Ϸ��");

		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("üũ5");
	}
}