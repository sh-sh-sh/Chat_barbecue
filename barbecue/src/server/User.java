package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import client.FileSender;

public class User {
	private String name;
	private Socket chatSocket;
	private Socket fileSocket;
	private ChatRoom currentRoom;
	private String ip_port;
	private String invite;
	private String invitepw;
	public FileSender fs;
	private boolean suverUser;

	public boolean isSuverUser() {
		return suverUser;
	}

	public void setSuverUser(boolean suverUser) {
		this.suverUser = suverUser;
	}

	public String getInvitepw() {
		return invitepw;
	}

	public void setInvitepw(String invitepw) {
		this.invitepw = invitepw;
	}

	public String getInvite() {
		return invite;
	}

	public void setInvite(String invite) {
		this.invite = invite;
	}

	public ChatRoom getCurrentRoom() {
		return currentRoom;
	}

	public void setCurrentRoom(ChatRoom currentRoom) {
		this.currentRoom = currentRoom;
	}

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

	public String toString() {
		return name;
	}

	public DataInputStream getChatIn() throws IOException {
		DataInputStream in = new DataInputStream(chatSocket.getInputStream());
		return in;
	}

	public BufferedInputStream getFileIn() throws IOException {
		BufferedInputStream in = new BufferedInputStream(fileSocket.getInputStream());
		return in;
	}

	public DataOutputStream getChatOut() throws IOException {
		DataOutputStream out = new DataOutputStream(chatSocket.getOutputStream());
		return out;
	}

	public BufferedOutputStream getFileOut() throws IOException {
		BufferedOutputStream out = new BufferedOutputStream(fileSocket.getOutputStream());
		return out;
	}
}
