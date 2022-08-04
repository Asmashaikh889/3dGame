package com.notbestlord.network.packet.player.inventory;

import com.notbestlord.core.inventory.recipe.IRecipe;

public class AddRecipePacket {
	private IRecipe recipe;

	public AddRecipePacket() {}

	public AddRecipePacket(IRecipe recipe) {
		this.recipe = recipe.getData();
	}

	public IRecipe getRecipe() {
		return recipe;
	}
}
