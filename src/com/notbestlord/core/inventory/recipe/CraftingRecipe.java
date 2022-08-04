package com.notbestlord.core.inventory.recipe;

import com.notbestlord.core.GuiManager;
import com.notbestlord.core.ObjectLoader;
import com.notbestlord.core.inventory.IInventory;
import com.notbestlord.core.inventory.Item;
import com.notbestlord.core.inventory.ItemInformation;
import com.notbestlord.core.inventory.ItemRegistry;
import com.notbestlord.core.inventory.recipe.registries.CraftingRecipeRegistry;
import com.notbestlord.core.inventory.recipe.registries.RecipeRegistry;
import com.notbestlord.core.rpg.RPGUtils;
import com.notbestlord.network.server.entity.ServerPlayer;
import imgui.ImGui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CraftingRecipe implements IRecipe{
	private String uuid;
	private ItemRegistry result;
	private List<ItemRegistry> ingredients;
	private String Unlocked;
	private String Requirements;
	private String station;

	public CraftingRecipe() {}

	public CraftingRecipe(ItemRegistry result, List<ItemRegistry> ingredients) {
		this.uuid = UUID.randomUUID().toString();
		this.result = result;
		this.ingredients = ingredients;
		this.Unlocked = "";
	}

	public CraftingRecipe(ItemRegistry result, List<ItemRegistry> ingredients, String Unlocked, String Requirements) {
		this.uuid = UUID.randomUUID().toString();
		this.result = result;
		this.ingredients = ingredients;
		this.Unlocked = Unlocked;
		this.Requirements = Requirements;
	}
	public CraftingRecipe(String uuid, ItemRegistry result, List<ItemRegistry> ingredients, String Unlocked, String Requirements, String station) {
		this.uuid = uuid;
		this.result = result;
		this.ingredients = ingredients;
		this.Unlocked = Unlocked;
		this.Requirements = Requirements;
		this.station = station;
	}

	@Override
	public String getRequirements() {
		return Requirements;
	}

	@Override
	public ItemRegistry getResult() {
		return result;
	}

	@Override
	public void Craft(IInventory inventory) {
		if (!canCraft(inventory)) return;
		for (ItemRegistry item : ingredients) {
			inventory.removeItem(item.getItemType(), item.getAmount());
		}
		inventory.addItem(result.getAsItem());
	}

	@Override
	public boolean canCraft(IInventory inventory) {
		if (!inventory.availableItemAmount(result.getItemType(), result.getAmount())) return false;
		for (ItemRegistry item : ingredients) {
			if (!inventory.containsAtLeast(item.getItemType(), item.getAmount()))
				return false;
		}
		return true;
	}

	@Override
	public void ImGuiDisplayRecipe(GuiManager gm, IInventory inv) {
		gm.Text("Result:", new Color(189, 252, 255));
		ImGui.image(ObjectLoader.getTexture("items_" + result.getItemType()), 32,32);
		ImGui.sameLine();
		ItemInformation itemInfo = ItemInformation.itemInformationByItemType.get(result.getItemType());
		gm.Text(result.getAmount() + " " + result.getAsItem().getDisplayName() + "\n" + (itemInfo != null ? itemInfo.getDescription() : ""));
		gm.Text( "    " + (itemInfo != null ? itemInfo.getUseType() : "") + "\n", new Color(255, 236, 178, 255));
		gm.Text("Ingredients:",new Color(189, 252, 255));
		for(ItemRegistry item : ingredients){
			ImGui.image(ObjectLoader.getTexture("items_" + item.getItemType()), 32,32);
			ImGui.sameLine();
			gm.Text(item.getAmount() + " " + item.getAsItem().getDisplayName() + "\n");
			ImGui.sameLine();
			gm.Text(inv.containsAtLeast(item.getItemType(), item.getAmount()) ? " V" : " X", inv.containsAtLeast(item.getItemType(), item.getAmount()) ? Color.green : Color.red);
		}
	}

	@Override
	public boolean isUnlocked(ServerPlayer player) {
		return Unlocked.equals("") || RPGUtils.doesMeetRequirements(player, Unlocked);
	}

	@Override
	public String getUuid() {
		return uuid;
	}

	@Override
	public IRecipe getData() {
		return new CraftingRecipe(uuid,result,ingredients,null, Requirements, station);
	}

	public List<ItemRegistry> getIngredients() {return new ArrayList<>(ingredients);}

	@Override
	public RecipeRegistry asRegistry(String station) {
		return new CraftingRecipeRegistry(this, station);
	}

	@Override
	public String getStation() {
		return station;
	}


	public String getUnlocked() {
		return Unlocked;
	}
}
