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

	FileReceiver(String filename, int size, String roomname, BufferedInputStream in, DataOutputStream out) {// ������
		this.filename = filename;
		this.in = in;
		this.src = "D://barbecue//" + roomname + "//" + filename;
		this.out = out;
		this.size = size;
	}

	public FileReceiver(String filename, int size, BufferedInputStream in, DataOutputStream out) {// ����ڿ�
		this.filename = filename;
		this.in = in;
		this.src = "D://barbecue//" + filename;
		this.out = out;
		this.size = size;
	}

	public void run() {
		File file = new File(src);
		if (!file.getParentFile().exists()) {// ������ ������ ������ �������� ������
			if (file.getParentFile().mkdir()) {// ������ �����Ѵ�
				System.out.println("���� ���� ���� 1");
			} else {// ���� ������ ����������
				if (!file.getParentFile().getParentFile().exists()) {// �� ������ ���� ������ �������� ������
					if (file.getParentFile().getParentFile().mkdir()) {// ���� ������ �����ϰ�
						System.out.println("���� ���� ���� 2");
						if (file.getParentFile().mkdir()) {// ������ �����Ѵ�
							System.out.println("���� ���� ���� 3");
						} else {
							System.out.println("��� ���� ���� 02 - ���� ���丮�� ���������� ���丮 ���� ����");
						}
					} else {
						System.out.println("��� ���� ���� 03 - ���� ���丮 ���� �Ұ�");
					}
				} else {// �ش� ������ ���� ������ �����ϴµ��� �ȵ�����
					System.out.println("��� ���� ���� 01 - ���� ���丮 �̹� ������");
				}
			}
		} else {
			System.out.println("��� �̹� ������");
		}
		byte[] content = new byte[1024];
		System.out.println("üũ1");
		try {
			bos = new BufferedOutputStream(new FileOutputStream(src));
			System.out.println("üũ2");
		} catch (FileNotFoundException e1) {
			// TODO �ڵ� ������ catch ���
			e1.printStackTrace();
		}
		System.out.println("üũ3");
		int len = 0;
		int total = 0;
		try {
			while (total != size) {
				len = in.read(content, 0, content.length);
				System.out.println("üũ4");
				bos.write(content, 0, len);
				total += len;
				System.out.println(filename + " �ٿ�ε� ������: " + total + "bytes �ٿ�ε� ��...");
			}
			System.out.println("üũ5");
			bos.flush();
			bos.close();
			System.out.println(filename + "(" + total + "bytes) �ٿ�ε� �Ϸ�");
			out.writeUTF("��");// ���� �����ڿ��� ���� �ٿ� �Ϸ������� �˸�
			System.out.println("üũ6");
		} catch (IOException e) {
			e.printStackTrace();
		}
		interrupt();
	} // run
}
