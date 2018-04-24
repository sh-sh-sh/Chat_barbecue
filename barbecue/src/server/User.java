package server;

import java.net.Socket;

public class User {
	String name;
	Socket chatSocket;
	Socket fileSocket;

	String ip_port;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Socket getChatSocket() {
		return chatSocket;
	}

	public void setChatSocket(Socket chatSocket) {
		this.chatSocket = chatSocket;
	}

	public Socket getFileSocket() {
		return fileSocket;
	}

	public void setFileSocket(Socket fileSocket) {
		this.fileSocket = fileSocket;
	}

	public String getIp_port() {
		return ip_port;
	}

	public void setIp_port(String ip_port) {
		this.ip_port = ip_port;
	}

}
