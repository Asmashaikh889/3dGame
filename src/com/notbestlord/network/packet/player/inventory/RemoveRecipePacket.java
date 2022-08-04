package com.notbestlord.network.packet.player.inventory;

public class RemoveRecipePacket {
	private String recipeUUID;

	public RemoveRecipePacket() {}

	public RemoveRecipePacket(String recipeUUID) {
		this.recipeUUID = recipeUUID;
	}

	public String getRecipeUUID() {
		return recipeUUID;
	}
}
