package com.notbestlord.network.packet.client;

import com.notbestlord.network.data.ClientLoginReplyType;

public class ClientConnectPacket {
	private String uuid;
	private ClientLoginReplyType reply;

	public ClientConnectPacket() {}

	public ClientConnectPacket(String uuid, ClientLoginReplyType reply) {
		this.uuid = uuid;
		this.reply = reply;
	}

	public String getUuid() {
		return uuid;
	}

	public ClientLoginReplyType getReply() {
		return reply;
	}
}
