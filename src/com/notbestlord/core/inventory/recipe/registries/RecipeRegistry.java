package com.notbestlord.core.inventory.recipe.registries;

import com.notbestlord.core.inventory.ItemRegistry;
import com.notbestlord.core.inventory.recipe.CraftingRecipe;
import com.notbestlord.core.inventory.recipe.IRecipe;
import com.notbestlord.core.inventory.recipe.VendorRecipe;

public class RecipeRegistry {
	public final ItemRegistry result;
	public final String recipeType;
	public final String RequirementsDescription;
	public final String craftingStation;
	public final String unlocked;
	public String uuid;

	public RecipeRegistry(CraftingRecipe recipe, String station) {
		result = recipe.getResult();
		recipeType = "crafting_recipe";
		RequirementsDescription = recipe.getRequirements();
		craftingStation = station;
		unlocked = recipe.getUnlocked();
		uuid = recipe.getUuid();
	}
	public RecipeRegistry(VendorRecipe recipe, String station) {
		result = recipe.getResult();
		recipeType = "vendor_recipe";
		RequirementsDescription = recipe.getRequirements();
		craftingStation = station;
		unlocked = recipe.getUnlocked();
		uuid = recipe.getUuid();
	}


	public IRecipe asRecipe(){return null;}

}
