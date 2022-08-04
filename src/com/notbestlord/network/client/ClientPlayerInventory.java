package com.notbestlord.network.client;

import com.notbestlord.ClientLauncher;
import com.notbestlord.core.gui.PlayerInventoryGui;
import com.notbestlord.core.inventory.IInventory;
import com.notbestlord.core.inventory.Item;
import com.notbestlord.core.inventory.equipment.Equipment;
import com.notbestlord.network.packet.player.inventory.PlayerInventoryUpdatePacket;

import java.util.ArrayList;
import java.util.List;

public class ClientPlayerInventory implements IInventory {
	private int coins;
	private int size = 1;
	private List<Item> itemArr;
	private Equipment equipment;
	public final PlayerInventoryGui gui;

	public ClientPlayerInventory() {
		coins = 0;
		itemArr = new ArrayList<>();
		equipment = new Equipment();
		gui = new PlayerInventoryGui(this);
	}

	public void update(PlayerInventoryUpdatePacket packet){
		coins = packet.getCoins();
		equipment = packet.getEquipment();
		size = packet.getItems().length;
		for (int i = 0; i < itemArr.size(); i++) {
			Item item = itemArr.get(i);
			boolean exist = false;
			for (Item item1 : packet.getItems()) {
				if (item1 != null && item1.getId().equals(item.getId())) {
					exist = true;
					item.setAmount(item1.getAmount());
					break;
				}
			}
			if (!exist) {
				removeItem(item);
			}
		}
		for(Item item : packet.getItems()){
			if(item != null) {
				boolean exist = false;
				for (Item item1 : itemArr) {
					if (item1 != null && item1.getId().equals(item.getId())) {
						exist = true;
						break;
					}
				}
				if (!exist) {
					addItem(item);
				}
			}
		}
	}

	public void setCoins(int coins) {
		this.coins = coins;
	}

	public void setEquipment(Equipment equipment) {
		this.equipment = equipment;
	}

	public int getCoins() {
		return coins;
	}

	public Equipment getEquipment() {
		return equipment;
	}

	public int usedSlots(){
		int n = 0;
		for(int i=0;i< itemArr.size();i++){
			if(itemArr.get(i) != null) {
				n++;
			}
		}
		return n;
	}

	@Override
	public void openInventory() {
		ClientLauncher.getWindow().getGuiManager().toggleInteractGui(gui);
	}

	@Override
	public void addItem(Item item) {
		if(item == null) return;
		int amount = item.getAmount();
		for(Item item1 : itemArr){
			if(item.doesMatch(item1)){
				if(item1.getAmount() + amount <= item1.getMaxStackSize()){
					item1.setAmount(item1.getAmount() + amount);
					return;
				}
				amount -= (item1.getMaxStackSize() - item1.getAmount());
				item1.setAmount(item1.getMaxStackSize());
			}
		}
		if(amount > 0)
			itemArr.add(item);
	}

	@Override
	public Item getItem(int index) {
		return itemArr.get(index);
	}

	@Override
	public void removeItem(int index) {
		itemArr.remove(index);
	}

	@Override
	public void removeItem(Item item) {
		for (int i = 0; i < itemArr.size(); i++) {
			Item item1 = itemArr.get(i);
			if(item1 != null && item1.getId().equals(item.getId())){
				itemArr.remove(item1);
				return;
			}
		}
	}

	@Override
	public void removeItem(String itemType, int amount) {
		for(int i =0;i<itemArr.size();i++){
			if(itemArr.get(i) != null && itemArr.get(i).getType().equals(itemType)){
				if(itemArr.get(i).getAmount() - amount <= 0){
					amount -= itemArr.get(i).getAmount();
					itemArr.remove(i);
				}
				else{
					itemArr.get(i).setAmount(itemArr.get(i).getAmount() - amount);
					return;
				}
			}
		}
	}

	@Override
	public void clear() {
		itemArr.clear();
	}

	@Override
	public boolean contains(Item item) {
		for(int i =0;i<itemArr.size();i++){
			if(itemArr.get(i) != null && itemArr.get(i).getType().equals(item.getType()))
				return true;
		}
		return false;
	}

	@Override
	public boolean containsAtLeast(String itemType, int amount) {
		for(int i =0;i < itemArr.size();i++){
			if(itemArr.get(i) != null && itemArr.get(i).getType().equals(itemType))
				amount -= itemArr.get(i).getAmount();
			if(amount <= 0)
				return true;
		}
		return false;
	}

	@Override
	public int firstEmpty() {
		return -1;
	}

	@Override
	public int first(String type) {
		return -1;
	}

	@Override
	public boolean availableItemAmount(String type, int amount) {
		int out = 0;
		if(size > itemArr.size()) return true;
		for(int i = 0;i<itemArr.size();i++){
			if(itemArr.get(i) != null && itemArr.get(i).getType().equals(type)){
				out += itemArr.get(i).getMaxStackSize() - itemArr.get(i).getAmount();
			}
		}
		return out >= amount;
	}

	@Override
	public int first(Item item) {
		return -1;
	}

	@Override
	public int getSize() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return this.firstEmpty() == -1;
	}

	@Override
	public Item[] getContents() {
		Item[] out = new Item[itemArr.size()];
		for(int i=0; i < out.length; i++){
			out[i] = itemArr.get(i);
		}
		return out;
	}

	public List<Item> getList() {
		return itemArr;
	}

}
