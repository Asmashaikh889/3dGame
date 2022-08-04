package com.notbestlord.network.packet.player;

public class LogMessagePacket {
	private String msg;

	public LogMessagePacket() {}

	public LogMessagePacket(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}
}
