package com.notbestlord.core.inventory.recipe;

import com.notbestlord.core.GuiManager;
import com.notbestlord.core.inventory.ItemRegistry;
import com.notbestlord.core.inventory.recipe.registries.RecipeRegistry;
import com.notbestlord.core.inventory.IInventory;
import com.notbestlord.core.inventory.Item;
import com.notbestlord.network.server.entity.ServerPlayer;

public interface IRecipe {

	ItemRegistry getResult();

	void Craft(IInventory inventory);

	boolean canCraft(IInventory inventory);

	void ImGuiDisplayRecipe(GuiManager gm, IInventory inv);

	boolean isUnlocked(ServerPlayer player);

	String getRequirements();

	String getUuid();

	IRecipe getData();

	RecipeRegistry asRegistry(String station);

	String getStation();
}
