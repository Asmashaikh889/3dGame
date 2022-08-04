package com.notbestlord.network.packet.player.inventory;

public class CraftItemPacket {
	private String playerUUID;
	private String recipeUUID;

	public CraftItemPacket() {}

	public CraftItemPacket(String playerUUID, String recipeUUID) {
		this.playerUUID = playerUUID;
		this.recipeUUID = recipeUUID;
	}

	public String getPlayerUUID() {
		return playerUUID;
	}

	public String getRecipeUUID() {
		return recipeUUID;
	}
}
