package com.notbestlord.core.inventory.recipe.registries;

import com.notbestlord.core.inventory.ItemRegistry;
import com.notbestlord.core.inventory.recipe.CraftingRecipe;
import com.notbestlord.core.inventory.recipe.IRecipe;

import java.util.ArrayList;
import java.util.List;

public class CraftingRecipeRegistry extends RecipeRegistry{
	private List<ItemRegistry> ingredients;
	public CraftingRecipeRegistry(CraftingRecipe recipe, String station) {
		super(recipe, station);
		ingredients = recipe.getIngredients();
	}
	@Override
	public IRecipe asRecipe() {
		return new CraftingRecipe(this.uuid, this.result, new ArrayList<>(ingredients), this.unlocked, this.RequirementsDescription, craftingStation);
	}
}
