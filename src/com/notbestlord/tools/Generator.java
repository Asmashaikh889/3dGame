package com.notbestlord.tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.notbestlord.core.event.ConsumeEvent;
import com.notbestlord.core.event.RPGEvent;
import com.notbestlord.core.inventory.*;
import com.notbestlord.core.inventory.equipment.EquipmentItem;
import com.notbestlord.core.inventory.equipment.EquipmentWeapon;
import com.notbestlord.core.inventory.equipment.WeaponType;
import com.notbestlord.core.inventory.recipe.CraftingRecipe;
import com.notbestlord.core.rpg.abilites.ActiveAbility;
import com.notbestlord.core.rpg.abilites.IAbility;
import com.notbestlord.core.rpg.abilites.PassiveAbility;

import javax.swing.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Generator extends JFrame{
	private JTextField itemId;
	private JPanel panel;
	private JTextField displayName;
	private JTextField stackSize;
	private JTextField consumableEvent;
	private JButton createConsumable;
	private JButton createEquipment;
	private JButton createWeapon;
	private JTextField damage;
	private JTextField Stat;
	private JTextField Increase;
	private JButton addStat;
	private JTextField description;
	private JButton addStatMul;
	private JButton clearStats;
	private JTextField equipmentSlot;
	private JTextField weaponTypeField;
	private JTabbedPane tabbedPane1;
	private JTextField AbilityID;
	private JTextField AbilityDisplayName;
	private JTextField AbilityStart;
	private JTextField AbilityEnd;
	private JTextField AbilityDescription;
	private JTextField duration;
	private JButton createPassiveAbility;
	private JButton createActiveAbility;
	private JTextField cooldown;
	private JTextField textureId;
	private JTextField EventType;
	private JTextField EventItemType;
	private JTextField Event;
	private JButton createEventButton;
	private JButton createItem;
	private JTextField itemUseType;
	private JLabel statList;
	private JTextField recipeResultItemType;
	private JTextField recipeResultAmount;
	private JTextField recipeIngredientItemType;
	private JTextField recipeIngredientAmount;
	private JLabel recipeIngredientsLabel;
	private JButton createCraftingRecipe;
	private JButton addIngredientButton;
	private JButton clearIngredientsButton;
	private JTextField recipeUnlocked;
	private JTextField recipeCraftingStation;
	private final HashMap<String, Float> stats = new HashMap<>();
	private final HashMap<String, Float> statMuls = new HashMap<>();
	private final List<ItemRegistry> recipeIngredients = new ArrayList<>();

	public Generator() {
		super("Server");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setContentPane(panel);
		this.pack();
		this.setSize(720,480);
		this.setVisible(true);
		addStat.addActionListener(e -> {
			try{
				float i = Float.parseFloat(Increase.getText());
				stats.put(Stat.getText(), i);
				statList.setText(statList.getText().substring(0, statList.getText().length() - 11) + "stat." + Stat.getText() + ": " + i + "<br/></p></html>");
			}
			catch(Exception ignored) {
				System.out.println("Failed to add stat.");
			}
		});
		addStatMul.addActionListener(e -> {
			try{
				float i = Float.parseFloat(Increase.getText());
				statMuls.put(Stat.getText(), i);
				statList.setText(statList.getText().substring(0, statList.getText().length() - 11) + "statMul." + Stat.getText() + ": " + i + "<br/></p></html>");
			}
			catch(Exception ignored) {
				System.out.println("Failed to add stat mul.");
			}
		});
		createConsumable.addActionListener(e -> {
			try{
				ConsumableItem item = new ConsumableItem(displayName.getText(), itemId.getText(), 0, Integer.parseInt(stackSize.getText()), consumableEvent.getText());
				new ItemInformation(displayName.getText(), itemId.getText(), description.getText(), ItemUseType.consumable);
				saveItem("consumable", item);
			}
			catch(Exception ignored) {
				System.out.println("Failed to save item.");
			}
		});
		clearStats.addActionListener(e -> {
			stats.clear();
			statMuls.clear();
			statList.setText("<html><p></p></html>");
		});
		createEquipment.addActionListener(e -> {
			try {
				EquipmentItem item = new EquipmentItem(displayName.getText(), itemId.getText(), 0, Integer.parseInt(stackSize.getText()));
				for (String stat : stats.keySet()) {
					item.addStat(com.notbestlord.core.rpg.stats.Stat.valueOf(stat), stats.get(stat));
				}
				for (String stat : statMuls.keySet()) {
					item.addStatMul(com.notbestlord.core.rpg.stats.Stat.valueOf(stat), statMuls.get(stat));
				}
				new EquipmentInformation(displayName.getText(), itemId.getText(), description.getText(), ItemUseType.valueOf("equipment_" + equipmentSlot.getText()), item);
				saveItem("equipment", item);
			}
			catch(Exception ignored) {
				System.out.println("Failed to save item.");
			}
		});
		createWeapon.addActionListener(e -> {
			try {
				EquipmentWeapon item = new EquipmentWeapon(displayName.getText(), itemId.getText(), 0, Integer.parseInt(stackSize.getText()), Float.parseFloat(damage.getText()));
				String[] types = weaponTypeField.getText().split(",");
				for(String type : types) {
					item.getWeaponTypes().add(WeaponType.valueOf(type));
				}
				for (String stat : stats.keySet()) {
					item.addStat(com.notbestlord.core.rpg.stats.Stat.valueOf(stat), stats.get(stat));
				}
				for (String stat : statMuls.keySet()) {
					item.addStatMul(com.notbestlord.core.rpg.stats.Stat.valueOf(stat), statMuls.get(stat));
				}
				new EquipmentInformation(displayName.getText(), itemId.getText(), description.getText(), ItemUseType.valueOf("equipment_" + equipmentSlot.getText()), item);
				saveItem("equipment", item);
			}
			catch(Exception ignored) {
				System.out.println("Failed to save item.");
			}
		});
		createActiveAbility.addActionListener(e -> {
			try {
				saveAbility(new ActiveAbility(AbilityID.getText(), AbilityDisplayName.getText(),
						AbilityStart.getText(), Float.parseFloat(duration.getText()), AbilityEnd.getText(),
						Float.parseFloat(cooldown.getText()), AbilityDescription.getText(), textureId.getText()), "save/abilities/active/" + AbilityID.getText() + ".json");
			}
			catch (Exception ignored) {
				System.out.println("Failed to save ability.");
			}
		});
		createPassiveAbility.addActionListener(e -> saveAbility(new PassiveAbility(AbilityID.getText(), AbilityDisplayName.getText(),
				AbilityStart.getText(), AbilityEnd.getText(), AbilityDescription.getText()), "save/abilities/passive/"+AbilityID.getText()+".json"));
		createEventButton.addActionListener(e -> {
			try{
				switch (EventType.getText()){
					case "consumable" -> saveEvent(new ConsumeEvent(Event.getText(), EventItemType.getText()), "save/events/consumable_"+EventItemType.getText()+".json");
				}
			}
			catch (Exception ignored){
				System.out.println("Failed to save event.");
			}

		});
		createItem.addActionListener(e -> {
			try{
				Item item = new Item(displayName.getText(), itemId.getText(), 0, Integer.parseInt(stackSize.getText()));
				new ItemInformation(displayName.getText(), itemId.getText(), description.getText(), ItemUseType.valueOf(itemUseType.getText()));
				saveItem("item", item);
			}
			catch (Exception ignored) {
				System.out.println("Failed to save item.");
			}
		});
		createCraftingRecipe.addActionListener(e -> {
			try{
				CraftingRecipe recipe = new CraftingRecipe(new ItemRegistry(recipeResultItemType.getText(), Integer.parseInt(recipeResultAmount.getText())), recipeIngredients,
						recipeUnlocked.getText(), "");
				saveRecipe(recipe, recipeCraftingStation.getText(),"save/recipes/" + recipeResultItemType.getText() + ".json");
			}
			catch (Exception ignored){
				System.out.println("Failed to save recipe.");
			}
		});
		addIngredientButton.addActionListener(e -> {
			try{
				ItemRegistry registry = new ItemRegistry(recipeIngredientItemType.getText(), Integer.parseInt(recipeIngredientAmount.getText()));
				recipeIngredients.add(registry);
				recipeIngredientsLabel.setText(recipeIngredientsLabel.getText().substring(0, recipeIngredientsLabel.getText().length() - 11) + registry + "<br/></p></html>");
			}
			catch (Exception ignored){
				System.out.println("Failed to add ingredient.");
			}
		});
		clearIngredientsButton.addActionListener(e -> {
			recipeIngredients.clear();
			recipeIngredientsLabel.setText("<html><p></p></html>");
		});
	}


	public static void main(String[] args) {
		ItemInformation.loadFromJson();
		new Generator();
	}


	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	public void saveItem(String type, Item item){
		try {
			Map<String, String> itemTypeMap;
			itemTypeMap = new HashMap<String, String>(gson.fromJson(new FileReader("save/items/item_list.json"), Map.class));
			//
			if(itemTypeMap == null) itemTypeMap = new HashMap<>();
			itemTypeMap.put(item.getType(), type);
			//
			File file = new File("save/items/item_list.json");
			file.createNewFile();
			FileWriter writer = new FileWriter(file);
			writer.write(gson.toJson(itemTypeMap));
			writer.flush();
			writer.close();
		}
		catch (Exception ignored) {}
		try{
			File file = new File("save/items/" + item.getType() + ".json");
			file.createNewFile();
			FileWriter writer = new FileWriter(file);
			writer.write(gson.toJson(item));
			writer.flush();
			writer.close();
		}
		catch (Exception ignored) {}
		ItemInformation.saveToJson();
	}

	public void saveAbility(IAbility ability, String path){
		try{
			File file = new File(path);
			file.createNewFile();
			FileWriter writer = new FileWriter(file);
			writer.write(gson.toJson(ability));
			writer.flush();
			writer.close();
		}
		catch (Exception ignored) {}
	}

	public void saveEvent(RPGEvent event, String path){
		try{
			File file = new File(path);
			file.createNewFile();
			FileWriter writer = new FileWriter(file);
			writer.write(gson.toJson(event));
			writer.flush();
			writer.close();
		}
		catch (Exception ignored) {}
	}

	public void saveRecipe(CraftingRecipe recipe, String craftingStation, String path){
		try{
			File file = new File(path);
			file.createNewFile();
			FileWriter writer = new FileWriter(file);
			writer.write(gson.toJson(recipe.asRegistry(craftingStation)));
			writer.flush();
			writer.close();
		}
		catch (Exception ignored) {}

	}
}
