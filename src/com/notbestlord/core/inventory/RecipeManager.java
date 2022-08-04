package com.notbestlord.core.inventory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.notbestlord.ServerLauncher;
import com.notbestlord.core.inventory.recipe.registries.CraftingRecipeRegistry;
import com.notbestlord.core.inventory.recipe.registries.RecipeRegistry;
import com.notbestlord.core.inventory.recipe.registries.VendorRecipeRegistry;
import com.notbestlord.core.inventory.recipe.IRecipe;
import com.notbestlord.core.utils.Utils;
import com.notbestlord.network.server.entity.ServerPlayer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public class RecipeManager {
	public static Map<String, List<IRecipe>> recipes = new HashMap<>();
	public static void init(){
		/*addRecipe(CraftingStation.player, new CraftingRecipe(new ItemRegistry("shard_purple_0", 1),
				new ArrayList<>(List.of(new ItemRegistry("shard_red_0", 2), new ItemRegistry("shard_blue_0", 2)))));

		addRecipe(CraftingStation.player, new CraftingRecipe(new ItemRegistry("shard_orange_0", 1),
				new ArrayList<>(List.of(new ItemRegistry("shard_red_0", 2), new ItemRegistry("shard_yellow_0", 2)))));

		addRecipe(CraftingStation.player, new CraftingRecipe(new ItemRegistry("shard_green_0", 1),
				new ArrayList<>(List.of(new ItemRegistry("shard_yellow_0", 2), new ItemRegistry("shard_blue_0", 2)))));

		addRecipe(CraftingStation.player, new CraftingRecipe(new ItemRegistry("equipment_bag_0", 1),
				new ArrayList<>(List.of(new ItemRegistry("shard_yellow_0", 1), new ItemRegistry("shard_blue_0", 1)))));

		addRecipe(CraftingStation.player, new CraftingRecipe(new ItemRegistry("equipment_bag_1", 1),
				new ArrayList<>(List.of(new ItemRegistry("shard_yellow_0", 1), new ItemRegistry("shard_blue_0", 1))),
				"stat.mana_max >= 100",
				"100 Max Mana"));

		addRecipe(CraftingStation.player, new CraftingRecipe(new ItemRegistry("equipment_bag_2", 1),
				new ArrayList<>(List.of(new ItemRegistry("shard_blue_0", 1), new ItemRegistry("shard_yellow_0", 1))),
				"stat.health_max >= 150",
				"150 Max Mana"));

		addRecipe(CraftingStation.player, new VendorRecipe(250, new ItemRegistry("shard_blue_0", 2),
				"inv.coins >= 250"));

		addRecipe(CraftingStation.player, new CraftingRecipe(new ItemRegistry("regeneration_potion_1", 1),
				new ArrayList<>(List.of(new ItemRegistry("shard_yellow_0", 1), new ItemRegistry("shard_blue_0", 1)))));

		addRecipe(CraftingStation.player, new CraftingRecipe(new ItemRegistry("mana_boost_ability_stone", 1),
				new ArrayList<>(List.of(new ItemRegistry("shard_blue_0", 2)))));

		addRecipe(CraftingStation.player, new CraftingRecipe(new ItemRegistry("fireball_ability_stone", 1),
				new ArrayList<>(List.of(new ItemRegistry("shard_yellow_0", 2)))));*/
		loadFromJson();

	}

	private static void addRecipe(String station, IRecipe recipe){
		if(!recipes.containsKey(station)){
			recipes.put(station,new ArrayList<>());
		}
		recipes.get(station).add(recipe);
	}

	public static void saveToJson(){
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		for(String station : recipes.keySet()){
			for(IRecipe recipe : recipes.get(station)){
				try{
					File file = new File("save/recipes/" + recipe.getResult().getItemType() + ".json");
					file.createNewFile();
					FileWriter writer = new FileWriter(file);
					writer.write(gson.toJson(recipe.asRegistry(station)));
					writer.flush();
					writer.close();
				}
				catch(Exception ignored) {}
			}
		}
	}

	public static void loadFromJson() {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		List<String> files = new ArrayList<>();
		Utils.addFilesFromDir(files, "template", new File("save/recipes"));
		//
		for(String pathFile : files){
			try {
				RecipeRegistry recipeRegistry = gson.fromJson(new FileReader(pathFile), RecipeRegistry.class);
				switch (recipeRegistry.recipeType){
					case "crafting_recipe" -> recipeRegistry = gson.fromJson(new FileReader(pathFile), CraftingRecipeRegistry.class);
					case "vendor_recipe" -> recipeRegistry = gson.fromJson(new FileReader(pathFile), VendorRecipeRegistry.class);
				}
				if(recipeRegistry.uuid.equals("")){
					recipeRegistry.uuid = UUID.randomUUID().toString();
					try{
						FileWriter writer = new FileWriter(pathFile);
						writer.write(gson.toJson(recipeRegistry));
						writer.flush();
						writer.close();
					}
					catch(Exception ignored) {}
				}
				addRecipe(recipeRegistry.craftingStation, recipeRegistry.asRecipe());
			}
			catch (Exception ignored) {}
		}
		try {
			for (ServerPlayer player : ServerLauncher.getGameServer().getOnlinePlayers()) {
				player.getInventory().refreshUnlockedRecipes();
			}
		}
		catch (Exception ignored) {}
	}
}
