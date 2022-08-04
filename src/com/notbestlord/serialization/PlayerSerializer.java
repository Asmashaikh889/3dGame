package com.notbestlord.serialization;

import com.esotericsoftware.kryonet.JsonSerialization;
import com.google.gson.*;
import com.google.gson.annotations.Expose;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.notbestlord.core.inventory.ConsumableItem;
import com.notbestlord.core.inventory.Item;
import com.notbestlord.core.inventory.ItemManager;
import com.notbestlord.core.inventory.equipment.EquipmentItem;
import com.notbestlord.core.inventory.equipment.EquipmentWeapon;
import com.notbestlord.core.rpg.PlayerRPGHandler;
import com.notbestlord.core.rpg.abilites.AbilityHandler;
import com.notbestlord.core.rpg.quests.QuestHandler;
import com.notbestlord.core.rpg.skills.SkillHandler;
import com.notbestlord.core.rpg.stats.StatHandler;
import com.notbestlord.core.utils.EntityFlags;
import com.notbestlord.network.server.entity.ServerPlayer;
import com.notbestlord.network.server.player.ServerPlayerInventory;
import org.joml.Vector3f;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;

public class PlayerSerializer {
	private Gson gsonDef = new GsonBuilder().setPrettyPrinting().create();
	private Gson gson = new GsonBuilder().setExclusionStrategies(
			new ExclusionStrategy() {
		@Override
		public boolean shouldSkipField(FieldAttributes fieldAttributes) {
			return fieldAttributes.getAnnotation(Expose.class) != null;
		}

		@Override
		public boolean shouldSkipClass(Class<?> aClass) {
			return false;
		}
	}).registerTypeAdapter(Item.class, (JsonDeserializer) (jsonElement, type, context) -> {
		if(jsonElement.isJsonNull()){
			return null;
		}
		String itemType = jsonElement.getAsJsonObject().get("type").getAsString();
		//
		switch (ItemManager.getItemType(itemType)){
			case "equipment_weapon" -> {
				return gsonDef.fromJson(jsonElement, EquipmentWeapon.class);
			}
			case "equipment" -> {
				return gsonDef.fromJson(jsonElement, EquipmentItem.class);
			}
			case "consumable" -> {
				return gsonDef.fromJson(jsonElement, ConsumableItem.class);
			}
			default -> {
				return gsonDef.fromJson(jsonElement, Item.class);
			}
		}
	}).registerTypeAdapter(Item.class, new JsonSerializer<Item>() {
		@Override
		public JsonElement serialize(Item item, Type type, JsonSerializationContext context) {
			if(item == null){
				return context.serialize(null);
			}
			if(item instanceof EquipmentWeapon item2){
				return context.serialize(item2);
			}
			else if(item instanceof EquipmentItem item2){
				return context.serialize(item2);
			}
			else if(item instanceof ConsumableItem item2){
				return context.serialize(item2);
			}
			return gsonDef.toJsonTree(item);
		}
	}).create();

	public JsonElement serialize(ServerPlayer player) {
		JsonObject jsonPlayer = new JsonObject();
		jsonPlayer.addProperty("uuid", player.getUuid());
		jsonPlayer.add("position", gson.toJsonTree(player.getPosition()));
		jsonPlayer.add("statHandler", gson.toJsonTree(player.getStatHandler()));
		jsonPlayer.add("skillHandler", gson.toJsonTree(player.getRPGHandler().skillHandler));
		jsonPlayer.add("questHandler", gson.toJsonTree(player.getRPGHandler().questHandler));
		jsonPlayer.add("abilityHandler", gson.toJsonTree(player.getRPGHandler().abilityHandler));
		jsonPlayer.add("inventory", gson.toJsonTree(player.getInventory()));
		jsonPlayer.add("entityFlags", gson.toJsonTree(player.getFlags()));
		return jsonPlayer;
	}

	public ServerPlayer deserialize(JsonElement jsonElement) {
		ServerPlayer player = new ServerPlayer(
				jsonElement.getAsJsonObject().get("uuid").getAsString(),
				gson.fromJson(jsonElement.getAsJsonObject().get("position"), Vector3f.class),
				new PlayerRPGHandler(
						gson.fromJson(jsonElement.getAsJsonObject().get("statHandler"), StatHandler.class),
						gson.fromJson(jsonElement.getAsJsonObject().get("skillHandler"), SkillHandler.class),
						gson.fromJson(jsonElement.getAsJsonObject().get("questHandler"), QuestHandler.class),
						gson.fromJson(jsonElement.getAsJsonObject().get("abilityHandler"), AbilityHandler.class)
				),
				gson.fromJson(jsonElement.getAsJsonObject().get("inventory"), ServerPlayerInventory.class),
				gson.fromJson(jsonElement.getAsJsonObject().get("entityFlags"), EntityFlags.class)
				);
		return player;
	}

	public ServerPlayer deserialize(String fileName) {
		try {
			return deserialize(gson.fromJson(new FileReader(fileName), JsonElement.class));
		}
		catch (FileNotFoundException e){
			return null;
		}
	}
}
