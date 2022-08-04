package com.notbestlord.core.event;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.notbestlord.ServerLauncher;
import com.notbestlord.core.rpg.stats.StatusEffectType;
import com.notbestlord.core.utils.Utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventManager {
	private static final HashMap<String, RPGEvent> events = new HashMap<>();

	public static void init(){
		/*events.put("consumable_fireball_ability_stone", new ConsumeEvent("ability.active.fireball", "fireball_ability_stone"));
		events.put("consumable_mana_boost_ability_stone", new ConsumeEvent("ability.active.mana_boost", "mana_boost_ability_stone"));
		events.put("consumable_regeneration_potion_1", new ConsumeEvent("status_effect.regeneration_1 10", "regeneration_potion_1"));
		events.put("consumable_regeneration_potion_2", new ConsumeEvent("status_effect.regeneration_2 10", "regeneration_potion_2"));
		events.put("consumable_mana_generation_potion_1", new ConsumeEvent("status_effect.mana_generation_1 10", "mana_generation_potion_1"));*/
		loadFromJson();
	}


	public static void saveToJson(){
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		for(String key : events.keySet()){
			RPGEvent event = events.get(key);
			try{
				File file = new File("save/events/" + key + ".json");
				file.createNewFile();
				FileWriter writer = new FileWriter(file);
				writer.write(gson.toJson(event));
				writer.flush();
				writer.close();
			}
			catch (Exception ignored) {}
		}
	}

	public static void loadFromJson(){
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		List<String> files = new ArrayList<>();
		Utils.addFilesFromDir(files, new File("save/events"));
		//
		for(String path : files){
			try{
				FileReader reader = new FileReader(path);
				if(path.contains("consumable")) {
					ConsumeEvent event1 = gson.fromJson(reader, ConsumeEvent.class);
					events.put("consumable_"+event1.getItemType(), event1);
				}
				reader.close();
			}
			catch (Exception ignored) {}
		}

	}

	public static RPGEvent getEvent(String name){
		return events.get(name);
	}
}
