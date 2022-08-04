package com.notbestlord.network.server.player;

import com.google.gson.annotations.Expose;
import com.notbestlord.ServerLauncher;
import com.notbestlord.core.event.EventManager;
import com.notbestlord.core.event.RPGEvent;
import com.notbestlord.core.inventory.*;
import com.notbestlord.core.inventory.equipment.EquipmentItem;
import com.notbestlord.core.inventory.equipment.EquipmentSlot;
import com.notbestlord.core.inventory.recipe.IRecipe;
import com.notbestlord.core.rpg.RPGUtils;
import com.notbestlord.network.packet.player.inventory.AddRecipePacket;
import com.notbestlord.network.packet.player.inventory.PlayerInventoryUpdatePacket;
import com.notbestlord.network.packet.player.inventory.RemoveRecipePacket;
import com.notbestlord.network.server.entity.ServerPlayer;

import java.util.*;

public class ServerPlayerInventory implements IInventory {

	@Expose(serialize = false)
	private ServerPlayer owner;

	@Expose(serialize = false)
	private Map<String, IRecipe> unlockedRecipes;

	private ServerPlayerEquipment equipment;
	private Item[] itemArr;
	private int coins;
	private List<String> availableCraftingStations;

	public ServerPlayerInventory(ServerPlayer owner, int capacity){
		this.owner = owner;
		itemArr = new Item[capacity];
		coins = 0;
		equipment = new ServerPlayerEquipment(this);
		availableCraftingStations = new ArrayList<>(List.of("player"));
		unlockedRecipes = new HashMap<>();
		for(IRecipe recipe : RecipeManager.recipes.get("player")){
			if(recipe.isUnlocked(owner)){
				unlockedRecipes.put(recipe.getUuid(), recipe);
			}
		}
	}

	public void init(ServerPlayer player){
		this.owner = player;
		this.equipment.init(this);
		unlockedRecipes = new HashMap<>();
		if(availableCraftingStations == null) availableCraftingStations = new ArrayList<>(List.of("player"));
		for(String station : availableCraftingStations) {
			for (IRecipe recipe : RecipeManager.recipes.get(station)) {
				if (recipe.isUnlocked(owner)) {
					unlockedRecipes.put(recipe.getUuid(), recipe);
				}
			}
		}
	}

	public void updateCapacity(int capacity){
		Item[] newItemArr = new Item[capacity];
		if(capacity < itemArr.length){
			for(int i=0;i<itemArr.length;i++){
				if(itemArr[i] != null)
					newItemArr[i] = itemArr[i];
			}
		}
		else{
			for(int i=0;i<itemArr.length;i++){
				newItemArr[i] = itemArr[i];
			}
		}
		itemArr = newItemArr;
	}
	public boolean canUpdateCapacity(int capacity){
		return usedSlots() <= capacity;
	}

	public int getCoins() {
		return coins;
	}

	public void setCoins(int coins) {
		this.coins = coins;
		refreshOnClient();
	}

	public void incCoins(int coins) {
		this.coins += coins;
		refreshOnClient();
	}

	public ServerPlayerEquipment getEquipment() {
		return equipment;
	}

	public Map<String, IRecipe> getUnlockedRecipes() {
		return new HashMap<>(unlockedRecipes);
	}

	public int usedSlots(){
		int n = 0;
		for(int i=0;i< itemArr.length;i++){
			if(itemArr[i] != null) {
				n++;
			}
		}
		return n;
	}

	public void refreshOnClient(){
		refreshUnlockedRecipes();
		ServerLauncher.getGameServer().sendPacketToPlayer(owner.getUuid(), new PlayerInventoryUpdatePacket(this));
	}

	public void addCraftingStation(String craftingStation){
		availableCraftingStations.add(craftingStation);
	}

	public void removeCraftingStation(String craftingStation){
		List<IRecipe> recipes = new ArrayList<>(unlockedRecipes.values());
		for (IRecipe recipe : recipes){
			if(!recipe.getStation().equalsIgnoreCase(craftingStation)){
				ServerLauncher.getGameServer().sendPacketToPlayer(owner.getUuid(), new RemoveRecipePacket(recipe.getUuid()));
				unlockedRecipes.remove(recipe.getUuid());
			}
		}
	}

	public void refreshUnlockedRecipes(){
		for(String station : availableCraftingStations) {
			for (IRecipe recipe : RecipeManager.recipes.get(station)) {
				if (!unlockedRecipes.containsKey(recipe.getUuid()) && recipe.isUnlocked(owner)) {
					unlockedRecipes.put(recipe.getUuid(), recipe);
					ServerLauncher.getGameServer().sendPacketToPlayer(owner.getUuid(), new AddRecipePacket(recipe.getData()));
				}
				if (unlockedRecipes.containsKey(recipe.getUuid()) && !recipe.isUnlocked(owner)) {
					unlockedRecipes.remove(recipe.getUuid());
					ServerLauncher.getGameServer().sendPacketToPlayer(owner.getUuid(), new RemoveRecipePacket(recipe.getUuid()));
				}
			}
		}
	}

	public void interactItem(String itemID){
		for(Item currentItem : itemArr){
			if(currentItem != null && currentItem.getId().equals(itemID)){
				if(currentItem instanceof ConsumableItem item1){
					RPGEvent event = EventManager.getEvent("consumable_"+item1.getConsumableEvent());
					if(event != null && RPGUtils.triggerEvent(owner, event.getEvent())) {
						removeItem(currentItem.getType(), 1);
					}
				}
				return;
			}
		}
	}

	public void equipItem(String itemID, EquipmentSlot targetSlot){
		for(Item currentItem : itemArr) {
			if (currentItem != null && currentItem.getId().equals(itemID)) {
				if (currentItem instanceof EquipmentItem item) {
					EquipmentSlot slot = EquipmentSlot.valueOf(ItemInformation.itemInformationByItemType.get(currentItem.getType()).getUseType().getStr());
					if(EquipmentSlot.match(slot, targetSlot)) {
						Item i = equipment.replaceEquipment(targetSlot, item);
						removeItem(currentItem);
						if (i != null) addItem(i);
					}
					return;
				}
			}
		}
	}

	public int getItemAmount(Item item){
		int amount = 0;
		for(Item item1 : itemArr){
			if(item != null && item.doesMatch(item1)){
				amount += item1.getAmount();
			}
		}
		return amount;
	}

	@Override
	public void openInventory() {}

	@Override
	public void addItem(Item item) {
		if(item == null) return;
		int amount = item.getAmount();
		for(Item item1 : itemArr){
			if(item.doesMatch(item1)){
				if(item1.getAmount() + amount <= item1.getMaxStackSize()){
					item1.setAmount(item1.getAmount() + amount);
					refreshOnClient();
					return;
				}
				amount -= (item1.getMaxStackSize() - item1.getAmount());
				item1.setAmount(item1.getMaxStackSize());
			}
		}
		if(amount != 0 && this.firstEmpty() != -1) {
			int index = this.firstEmpty();
			itemArr[index] = Item.New(item);
			itemArr[index].setAmount(amount);
		}
		refreshOnClient();
	}

	@Override
	public Item getItem(int index) {
		return itemArr[index];
	}

	@Override
	public void removeItem(int index) {
		itemArr[index] = null;
		refreshOnClient();
	}

	@Override
	public void removeItem(Item item) {
		for(int i=0;i< itemArr.length;i++){
			if(itemArr[i] == item) {
				itemArr[i] = null;
				refreshOnClient();
				return;
			}
		}
	}

	@Override
	public void removeItem(String itemType, int amount) {
		for(int i =0;i<itemArr.length;i++){
			if(itemArr[i] != null && itemArr[i].getType().equals(itemType)){
				if(itemArr[i].getAmount() - amount <= 0){
					amount -= itemArr[i].getAmount();
					itemArr[i] = null;
				}
				else{
					itemArr[i].setAmount(itemArr[i].getAmount() - amount);
					refreshOnClient();
					return;
				}
			}
		}
		refreshOnClient();
	}

	public void removeItem(String itemUuid){
		for(int i =0;i<itemArr.length;i++) {
			if (itemArr[i] != null && itemArr[i].getId().equals(itemUuid)) {
				itemArr[i] = null;
				refreshOnClient();
				return;
			}
		}
	}

	@Override
	public void clear() {
		Arrays.fill(itemArr, null);
		refreshOnClient();
	}

	@Override
	public boolean contains(Item item) {
		for(int i =0;i<itemArr.length;i++){
			if(itemArr[i] != null && itemArr[i].getType().equals(item.getType()))
				return true;
		}
		return false;
	}

	@Override
	public boolean containsAtLeast(String itemType, int amount) {
		for(int i =0;i<itemArr.length;i++){
			if(itemArr[i] != null && itemArr[i].getType().equals(itemType))
				amount -= itemArr[i].getAmount();
			if(amount <= 0)
				return true;
		}
		return false;
	}

	@Override
	public int firstEmpty() {
		for(int i =0;i<itemArr.length;i++){
			if(itemArr[i] == null)
				return i;
		}
		return -1;
	}

	@Override
	public int first(String type) {
		for(int i =0;i<itemArr.length;i++){
			if(itemArr[i] != null && itemArr[i].getType().equals(type))
				return i;
		}
		return -1;
	}

	@Override
	public boolean availableItemAmount(String type, int amount) {
		int out = 0;
		if(firstEmpty() != -1) return true;
		for(int i = 0;i<itemArr.length;i++){
			if(itemArr[i] != null && itemArr[i].getType().equals(type)){
				out += itemArr[i].getMaxStackSize() - itemArr[i].getAmount();
			}
		}
		return out >= amount;
	}

	@Override
	public int first(Item item) {
		for(int i =0;i<itemArr.length;i++){
			if(itemArr[i] != null && itemArr[i].getType().equals(item.getType()))
				return i;
		}
		return -1;
	}

	@Override
	public int getSize() {
		return itemArr.length;
	}

	@Override
	public boolean isEmpty() {
		return this.firstEmpty() == 0;
	}

	@Override
	public Item[] getContents() {
		return itemArr;
	}

	public ServerPlayer getOwner() {
		return owner;
	}
}
