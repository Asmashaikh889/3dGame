package com.notbestlord.network.packet.player;

import com.notbestlord.core.rpg.npcs.NPC_ID;

import java.util.List;

public class PlayerConverseNpcPacket {
	private List<String> dialogue;
	private NPC_ID npc_id;

	public PlayerConverseNpcPacket() {}

	public PlayerConverseNpcPacket(NPC_ID npc_id, List<String> dialogue) {
		this.dialogue = dialogue;
		this.npc_id = npc_id;
	}

	public List<String> getDialogue() {
		return dialogue;
	}

	public NPC_ID getNpc_id() {
		return npc_id;
	}
}
