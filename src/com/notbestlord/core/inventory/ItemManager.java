package com.notbestlord.core.inventory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.notbestlord.core.inventory.equipment.EquipmentItem;
import com.notbestlord.core.inventory.equipment.EquipmentWeapon;
import com.notbestlord.core.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemManager {
	public static Map<String, Item> Items = new HashMap<>();
	public static Map<String, String> itemTypes = new HashMap<>();

	public static void init(){
		loadFromJson();
		ItemInformation.loadFromJson();
	}

	public static Item getItem(String type){
		return Item.New(Items.get(type.toLowerCase()));
	}

	public static Item getItem(String type, int amount){
		Item item = Item.New(Items.get(type));
		item.setAmount(amount);
		return item;
	}

	public static String getItemType(String type){
		return itemTypes.getOrDefault(type, "item");
	}

	public static void saveToJson(){
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Map<String, String> itemTypeMap = new HashMap<>();
		for(Item item : Items.values()){
			try{
				File file = new File("save/items/" + item.getType() + ".json");
				file.createNewFile();
				FileWriter writer = new FileWriter(file);

				if(item instanceof ConsumableItem){
					itemTypeMap.put(item.getType(), "consumable");
				}
				else if(item instanceof EquipmentWeapon){
					itemTypeMap.put(item.getType(), "equipment_weapon");
				}
				else if(item instanceof EquipmentItem){
					itemTypeMap.put(item.getType(), "equipment");
				}
				else{
					itemTypeMap.put(item.getType(), "item");
				}

				writer.write(gson.toJson(item));
				writer.flush();
				writer.close();
			}
			catch(Exception ignored) {}
		}
		try{
			File file = new File("save/items/item_list.json");
			file.createNewFile();
			FileWriter writer = new FileWriter(file);
			writer.write(gson.toJson(itemTypeMap));
			writer.flush();
			writer.close();
		}
		catch (Exception ignored) {}
	}

	public static void loadFromJson() {
		Gson gson = new Gson();
		List<String> files = new ArrayList<>();
		Utils.addFilesFromDir(files, "template,png", new File("save/items"));
		files.remove("save\\items\\item_list.json");
		//
		Map<String, String> itemTypeMap = null;
		try {
			itemTypeMap = new HashMap<String, String>(gson.fromJson(new FileReader("save/items/item_list.json"), Map.class));
		}
		catch (Exception ignored) {}
		if(itemTypeMap == null) itemTypeMap = new HashMap<>();
		//
		for(String pathFile : files){
			try {
				Item item = gson.fromJson(new FileReader(pathFile), Item.class);
				if(itemTypeMap.containsKey(item.getType()) && !(itemTypeMap.get(item.getType())).equals("item")){
					String type = itemTypeMap.get(item.getType());
					if(type.equals("consumable")){
						item = gson.fromJson(new FileReader(pathFile), ConsumableItem.class);
					}
					else if(type.equals("equipment_weapon")){
						item = gson.fromJson(new FileReader(pathFile), EquipmentWeapon.class);
					}
					else if(type.equals("equipment")){
						item = gson.fromJson(new FileReader(pathFile), EquipmentItem.class);
					}
				}
				Items.put(item.getType(), item);
			}
			catch (FileNotFoundException ignored) {}
		}
		itemTypes = itemTypeMap;
	}
}
