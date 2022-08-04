package com.notbestlord.core.rpg.abilites;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.notbestlord.core.utils.Utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AbilityManager {
	private static final HashMap<String, ActiveAbility> activeAbilities = new HashMap<>();
	private static final HashMap<String, PassiveAbility> passiveAbilities = new HashMap<>();
	public static void init(){
		/*activeAbilities.put("mana_boost", new ActiveAbility("mana_boost","Mana Boost", "stat.mana_recovery * 2", 10f,
				"stat.mana_recovery / 2", 5f, "Double mana recovery rate.", "gui_abilities_mana_boost"));
		activeAbilities.put("fireball", new ActiveAbility("fireball", "Fireball", "stat.strength * 2", 10f,
				"stat.strength / 2", 5f, "Double strength.", "gui_abilities_fireball"));
		passiveAbilities.put("copper_heart", new PassiveAbility("copper_heart", "Copper Heart",
				"stat.endurance + 20", "stat.endurance - 20", "Increase Endurance by 20."));*/
		loadFromJson();
	}

	public static void saveToJson(){
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		for(String key : activeAbilities.keySet()){
			try{
				File file = new File("save/abilities/active/"+key+".json");
				file.createNewFile();
				FileWriter writer = new FileWriter(file);
				writer.write(gson.toJson(activeAbilities.get(key)));
				writer.flush();
				writer.close();
			}
			catch (Exception ignored) {}
		}
		for(String key : passiveAbilities.keySet()){
			try{
				File file = new File("save/abilities/passive/"+key+".json");
				file.createNewFile();
				FileWriter writer = new FileWriter(file);
				writer.write(gson.toJson(passiveAbilities.get(key)));
				writer.flush();
				writer.close();
			}
			catch (Exception ignored) {}
		}
	}

	public static void loadFromJson(){
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		//
		List<String> actives = new ArrayList<>();
		Utils.addFilesFromDir(actives, new File("save/abilities/active"));
		//
		List<String> passives = new ArrayList<>();
		Utils.addFilesFromDir(passives, new File("save/abilities/passive"));
		//
		for(String path : actives){
			try{
				FileReader reader = new FileReader(path);
				ActiveAbility ability = gson.fromJson(reader, ActiveAbility.class);
				activeAbilities.put(ability.getId(), ability);
				reader.close();
			}
			catch (Exception ignored) {}
		}
		//
		for(String path : passives){
			try{
				FileReader reader = new FileReader(path);
				PassiveAbility ability = gson.fromJson(reader, PassiveAbility.class);
				passiveAbilities.put(ability.getId(), ability);
				reader.close();
			}
			catch (Exception ignored) {}
		}
	}

	public static ActiveAbility getActiveAbility(String key){
		return activeAbilities.get(key);
	}

	public static PassiveAbility getPassiveAbility(String key){
		return passiveAbilities.get(key);
	}
}
