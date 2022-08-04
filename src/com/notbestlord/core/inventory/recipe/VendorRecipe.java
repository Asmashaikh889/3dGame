package com.notbestlord.core.inventory.recipe;

import com.notbestlord.core.GuiManager;
import com.notbestlord.core.ObjectLoader;
import com.notbestlord.core.inventory.IInventory;
import com.notbestlord.core.inventory.Item;
import com.notbestlord.core.inventory.ItemRegistry;
import com.notbestlord.core.inventory.recipe.registries.RecipeRegistry;
import com.notbestlord.core.inventory.recipe.registries.VendorRecipeRegistry;
import com.notbestlord.core.rpg.RPGUtils;
import com.notbestlord.network.client.ClientPlayerInventory;
import com.notbestlord.network.server.entity.ServerPlayer;
import com.notbestlord.network.server.player.ServerPlayerInventory;
import imgui.ImGui;

import java.awt.*;
import java.util.UUID;

public class VendorRecipe implements IRecipe{
	private String uuid;
	private int coinCost;
	private ItemRegistry result;
	private String unlocked;
	private String station;

	public VendorRecipe() {}

	public VendorRecipe(int recipeCoinCost, ItemRegistry result) {
		this.coinCost = recipeCoinCost;
		this.result = result;
		unlocked = "";
		this.uuid = UUID.randomUUID().toString();
	}

	public VendorRecipe(String uuid, int recipeCoinCost, ItemRegistry result) {
		this.coinCost = recipeCoinCost;
		this.result = result;
		this.uuid = uuid;
		unlocked = "";
	}

	public VendorRecipe(int recipeCoinCost, ItemRegistry result, String unlocked) {
		this.coinCost = recipeCoinCost;
		this.result = result;
		this.uuid = UUID.randomUUID().toString();
		this.unlocked = unlocked;
	}

	public VendorRecipe(String uuid, int recipeCoinCost, ItemRegistry result, String unlocked, String station) {
		this.coinCost = recipeCoinCost;
		this.result = result;
		this.uuid = UUID.randomUUID().toString();
		this.unlocked = unlocked;
		this.station = station;
	}

	@Override
	public ItemRegistry getResult() {
		return result;
	}

	@Override
	public void Craft(IInventory inventory) {
		if(inventory instanceof ServerPlayerInventory inv && inv.getCoins() >= coinCost && inv.availableItemAmount(result.getItemType(), getResult().getAmount())){
			inv.incCoins(-coinCost);
			inv.addItem(result.getAsItem());
		}
	}

	@Override
	public boolean canCraft(IInventory inventory) {
		return ((inventory instanceof ServerPlayerInventory && ((ServerPlayerInventory) inventory).getCoins() >= coinCost) ||
				(inventory instanceof ClientPlayerInventory && ((ClientPlayerInventory) inventory).getCoins() >= coinCost));
	}

	@Override
	public void ImGuiDisplayRecipe(GuiManager gm, IInventory inv) {
		gm.Text("Result:", new Color(189, 252, 255));
		ImGui.image(ObjectLoader.getTexture("items_" + result.getItemType()), 32,32);
		ImGui.sameLine();
		gm.Text(result.getAmount() + " " + result.getAsItem().getDisplayName() + "\n");
		gm.Text("Cost: " + coinCost);
	}

	@Override
	public boolean isUnlocked(ServerPlayer player) {
		return unlocked.equals("") || RPGUtils.doesMeetRequirements(player,unlocked);
	}

	@Override
	public String getRequirements() {
		return "";
	}

	@Override
	public String getUuid() {
		return uuid;
	}

	@Override
	public IRecipe getData() {
		return new VendorRecipe(uuid, coinCost, result);
	}

	public int getCoins() {return coinCost;}

	@Override
	public RecipeRegistry asRegistry(String station) {
		return new VendorRecipeRegistry(this, station);
	}

	@Override
	public String getStation() {
		return station;
	}

	public String getUnlocked() {
		return unlocked;
	}
}
