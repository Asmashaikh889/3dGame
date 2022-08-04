package com.notbestlord.network.packet.client;

import java.util.Objects;

public class ClientLoginPacket {
	private String userName;
	private String password;
	private boolean exist;

	public ClientLoginPacket(){}

	public ClientLoginPacket(String userName, String password, boolean exist) {
		this.userName = userName;
		this.password = password;
		this.exist = exist;
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}

	public boolean isExist() {
		return exist;
	}
}
