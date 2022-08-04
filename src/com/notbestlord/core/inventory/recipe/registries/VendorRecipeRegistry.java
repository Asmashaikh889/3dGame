package com.notbestlord.core.inventory.recipe.registries;

import com.notbestlord.core.inventory.recipe.IRecipe;
import com.notbestlord.core.inventory.recipe.VendorRecipe;

public class VendorRecipeRegistry extends RecipeRegistry{
	private int coins;
	public VendorRecipeRegistry(VendorRecipe recipe, String station) {
		super(recipe, station);
		coins = recipe.getCoins();
	}

	@Override
	public IRecipe asRecipe() {
		return new VendorRecipe(this.uuid, coins, this.result, this.unlocked, craftingStation);
	}
}
