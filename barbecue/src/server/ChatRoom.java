package server;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ChatRoom {

	private String name;
	private String owner;
	private String password;

	private Map<String, User> users;

	public Map<String, User> getUsers() {
		return users;
	}

	public void setUsers(Map<String, User> users) {
		this.users = users;
	}

	public ChatRoom(String name, String owner) {
		this(name, owner, null);
	}

	public ChatRoom(String name, String owner, String password) {
		this.name = name;
		this.owner = owner;
		this.password = password;

		users = Collections.synchronizedMap(new HashMap<>());
	}

	public boolean addMember(User user) {
		User us = users.put(user.getName(), user);
		if (us == null) {
			return true;
		} else {
			return false;
		}
	}

	public boolean removeMember(String key) {
		User us = users.remove(key);
		if (us == null) {
			return false;
		} else {
			return true;
		}

	}

	synchronized void SendMsg(String name, String msg) {

	}

	public boolean setName(String name) {
		this.name = name;
		return false;
	}

	public String getName() {
		return name;
	}

	public String getOwner() {
		return owner;
	}

	public String getPassword() {
		return password;
	}

	public boolean setPassword(String password) {
		this.password = password;
		return false;
	}
}

/*
 * Set<Map.Entry<String, User>> set = users.entrySet(); Iterator<Entry<String,
 * User>> a = set.iterator(); a.next();
 */
