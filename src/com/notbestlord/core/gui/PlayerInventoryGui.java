package com.notbestlord.core.gui;

import com.notbestlord.ClientLauncher;
import com.notbestlord.GameClient;
import com.notbestlord.core.GuiManager;
import com.notbestlord.core.ObjectLoader;
import com.notbestlord.core.inventory.ConsumableItem;
import com.notbestlord.core.inventory.Item;
import com.notbestlord.core.inventory.ItemInformation;
import com.notbestlord.core.inventory.equipment.Equipment;
import com.notbestlord.core.inventory.equipment.EquipmentSlot;
import com.notbestlord.core.inventory.recipe.IRecipe;
import com.notbestlord.core.rpg.abilites.ActiveAbility;
import com.notbestlord.core.rpg.abilites.PassiveAbility;
import com.notbestlord.core.rpg.skills.*;
import com.notbestlord.core.rpg.stats.Stat;
import com.notbestlord.core.rpg.stats.StatusEffectType;
import com.notbestlord.core.utils.Utils;
import com.notbestlord.network.client.ClientPlayerInventory;
import com.notbestlord.network.client.ClientStatHandler;
import com.notbestlord.network.packet.player.inventory.*;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerInventoryGui implements IGui {
	//
	private ClientPlayerInventory inventory;
	public final ClientStatHandler statHandler;
	//
	private ImVec2 mousePos;
	// items
	private final ImString searchItem;
	private Item currentItem;
	private int currentItemIndex;
	// equipment
	private EquipmentSlot currentEquipmentSlot = EquipmentSlot.none;
	// skills
	public final SkillRecord skills;
	private SkillType currentSkillType = SkillType.none;
	private SubSkillType currentSubSkillType = SubSkillType.none;
	// crafting
	private IRecipe currentRecipe = null;
	public final Map<String, IRecipe> unlockedRecipes = new HashMap<>();
	// abilities
	private PlayerAbilityGui abilityGui;
	private ActiveAbility activeAbility = null;
	private PassiveAbility passiveAbility = null;
	//
	private boolean statsRendering = false, equipmentRendering = false, skillRendering = false, abilityRendering = false, craftingRendering = false;
	public PlayerInventoryGui(ClientPlayerInventory inventory) {
		searchItem = new ImString();
		this.inventory = inventory;
		this.statHandler = new ClientStatHandler();
		this.skills = new SkillRecord(new HashMap<>(), new HashMap<>());
	}

	public void setAbilityGui(PlayerAbilityGui gui){
		this.abilityGui = gui;
	}

	@Override
	public int updateStyle() {
		return 0;
	}

	@Override
	public boolean renderGui(GuiManager gm) {
		ImGui.begin("Inventory: ", ImGuiWindowFlags.NoMove);
		ImGui.setWindowPos(ClientLauncher.getWindow().getWidth() / 2f - ImGui.getWindowWidth() / 2f,
				ClientLauncher.getWindow().getHeight() / 2f - ImGui.getWindowHeight() / 2f);
		//
		gm.Text("Coins: ");
		ImGui.sameLine();
		gm.Text(""+inventory.getCoins(), new Color(255,215,0));
		//
		ImGui.sameLine();
		gm.Text("	");
		//
		ImGui.sameLine();
		if(ImGui.button("Equipment")){
			equipmentRendering = !equipmentRendering;
		}
		//
		ImGui.sameLine();
		gm.Text("	");
		//
		ImGui.sameLine();
		if(ImGui.button("Stats")){
			statsRendering = !statsRendering;
		}
		//
		ImGui.sameLine();
		gm.Text("	");
		ImGui.sameLine();
		if(ImGui.button("Skills")){
			skillRendering = !skillRendering;
		}
		//
		ImGui.sameLine();
		gm.Text("	");
		ImGui.sameLine();
		if(ImGui.button("Abilities")){
			abilityRendering = !abilityRendering;
		}
		//
		ImGui.sameLine();
		gm.Text("	");
		ImGui.sameLine();
		if(ImGui.button("Crafting")) {
			craftingRendering = !craftingRendering;
		}
		//
		gm.Text("Capacity: ");
		ImGui.sameLine();
		gm.Text(inventory.usedSlots() + "/" + inventory.getSize(), new Color(142, 255, 210));
		//
		ImVec2 vec = ImGui.getCursorScreenPos();
		// items
		ImGui.text("Items:");
		ImGui.beginChild("PlayerInventorySearchBar", 200, 20);
		ImGui.inputText(" ", searchItem);
		boolean focused = ImGui.isItemFocused();
		ImGui.endChild();
		//
		ImGui.sameLine();
		ImGui.pushStyleColor(ImGuiCol.Button, Color.TRANSLUCENT);
		ImGui.pushStyleColor(ImGuiCol.ButtonActive, Color.TRANSLUCENT);
		ImGui.pushStyleColor(ImGuiCol.ButtonHovered, Color.TRANSLUCENT);
		ImGui.imageButton(ObjectLoader.getTexture("gui_trash_can"), 16,16);
		ImGui.popStyleColor(3);
		if(ImGui.beginDragDropTarget()){
			Object obj = ImGui.acceptDragDropPayload("item_payload");
			if(obj != null) {
				Item item1 = (Item) obj;
				ClientLauncher.getGame().sendToServerTcp(new ItemTrashPacket(GameClient.uuid, item1.getId()));
			}
			ImGui.endDragDropTarget();
		}
		//
		ImGui.beginChild("inventoryItems", 240, ImGui.getWindowHeight() - 120);
		List<Item> contents = inventory.getList();
		for (int i = 0; i < contents.size();i++) {
			Item item = contents.get(i);
			if ((item != null && searchItem.get().equals("")) || (item != null && !searchItem.get().equals("") && item.getDisplayName().toLowerCase().contains(searchItem.get().toLowerCase()))) {
				ImGui.beginChild(item.getId(),48,48);
				ImGui.setCursorScreenPos(ImGui.getCursorScreenPosX()+8, ImGui.getCursorScreenPosY()+8);
				if(ImGui.imageButton(ObjectLoader.getTexture("items_" + item.getType()), 32, 32,0,0,1,1,0) && currentItem != item) {
					if (ItemInformation.itemInformationByItemType.containsKey(item.getType())) {
						this.currentItem = item;
						this.currentItemIndex = i;
						this.mousePos = ImGui.getMousePos();
					}
				}
				if(ImGui.beginDragDropSource()){
					ImGui.setDragDropPayload("item_payload", contents.get(i));
					ImGui.image(ObjectLoader.getTexture("items_" + item.getType()), 16, 16);
					ImGui.endDragDropSource();
				}
				if(ImGui.beginDragDropTarget()){
					Object obj = ImGui.acceptDragDropPayload("item_payload");
					if(obj != null) {
						Item item1 = (Item) obj;
						int i1 = contents.indexOf(item1);
						contents.set(i,item1);
						contents.set(i1, item);
					}
					ImGui.endDragDropTarget();
				}
				ImGui.endChild();
				ImGui.sameLine();
				gm.Text("\n"+item.getAmount() + "/" + item.getMaxStackSize() + " " + item.getDisplayName());
			}
		}
		ImGui.endChild();
		if(ImGui.beginDragDropTarget()){
			Object obj = ImGui.acceptDragDropPayload("equipment_ret_payload");
			if(obj != null) {
				ClientLauncher.getGame().sendToServerTcp(new PlayerUnequipPacket(GameClient.uuid, (EquipmentSlot) obj));
			}
			ImGui.endDragDropTarget();
		}
		ImGui.setCursorScreenPos(vec.x + 400, vec.y);
		// equipment
		ImGui.sameLine();
		if(equipmentRendering) renderEquipment();
		// stats
		if(statsRendering){
			ImGui.sameLine();
			ImGui.beginChild("PlayerStats", 240,ImGui.getWindowHeight() - 120);
			gm.Text("Stats:");
			// stats
			for(Stat stat : Stat.values()){
				if(statHandler.stats.containsKey(stat) && stat.isDisplayed(statHandler.stats.get(stat)))
					gm.Text(Utils.UpperCaseStart(stat.getStr()) + ": " + String.format("%.2f",statHandler.stats.get(stat)), stat.getColor());
			}

			//status effects
			if(!statHandler.effects.isEmpty()){
				gm.Text("\nStatus Effects:", new Color(72, 253, 200));
				List<StatusEffectType> effects = new ArrayList<>(statHandler.effects.keySet());
				for(StatusEffectType effect : effects){
					ImGui.textWrapped(effect.getDescription() + "("+(statHandler.effects.get(effect) == -1 ? "inf" : String.format("%.2f", statHandler.effects.get(effect)) + "s")+")");
				}
			}
			ImGui.endChild();
		}
		// skills
		if(skillRendering) {
			ImGui.sameLine();
			ImGui.beginChild("Skills:", 240,ImGui.getWindowHeight() - 120);
			if (currentSubSkillType != SubSkillType.none) {
				gm.Text(currentSubSkillType.getStr() + " (" + currentSkillType.getStr() + "):");
				SubSkill currentSkill = skills.subSkills().get(currentSubSkillType);
				gm.Text("Level: " + currentSkill.getLevel() + " (" +
						Utils.getPercent(currentSkill.getCurrentExp(), currentSkill.getRequiredExp(), "%.2f") + "%)");
				// description
				ImGui.textWrapped(currentSubSkillType.getDescription());
				// display rewards per level
				gm.Text("Benefits:");
				ImGui.textWrapped(currentSubSkillType.getBenefits(currentSkill.getLevel()));
				//
				if (ImGui.button("Back")) {
					currentSubSkillType = SubSkillType.none;
				}
			} else if (currentSkillType != SkillType.none) {
				gm.Text(currentSkillType.getStr() + " Skill:");
				Skill currentSkill = skills.skills().get(currentSkillType);
				gm.Text("Level: " + currentSkill.getLevel() + " (" +
						Utils.getPercent(currentSkill.getCurrentExp(), currentSkill.getRequiredExp(), "%.2f") + "%)");
				// description
				ImGui.textWrapped(currentSkillType.getDescription());
				// display rewards per level
				gm.Text("Benefits:");
				ImGui.textWrapped(currentSkillType.getBenefits(currentSkill.getLevel()));
				// display sub skills
				if (currentSkillType.doesHaveSubSkills()) {
					for (SubSkillType subSkillType : currentSkillType.getSubSkills()) {
						if (ImGui.button(subSkillType.getStr() + ": " + skills.subSkills().get(subSkillType).getLevel())) {
							currentSubSkillType = subSkillType;
						}
					}
				}
				//
				if (ImGui.button("Back")) {
					currentSkillType = SkillType.none;
				}
			}
			else {
				gm.Text("Skills:");
				for (Skill skill : skills.skills().values()) {
					if (ImGui.button(skill.getSkillType().getStr() + ": " + skill.getLevel())) {
						currentSkillType = skill.getSkillType();
					}
				}
			}
			ImGui.endChild();
		}
		// abilities
		if(abilityRendering && abilityGui != null){
			ImGui.sameLine();
			ImGui.beginChild("Abilities:", 240,ImGui.getWindowHeight() - 120);
			ImGui.text("Abilities: ");
			if(abilityGui.activeAbilities.size() != 0){
				ImGui.pushStyleColor(ImGuiCol.Button, Color.TRANSLUCENT);
				ImGui.pushStyleColor(ImGuiCol.ButtonActive, Color.TRANSLUCENT);
				ImGui.pushStyleColor(ImGuiCol.ButtonHovered, Color.TRANSLUCENT);
				if (ImGui.treeNode("Active Abilities:")) {
					List<ActiveAbility> activeAbilities = abilityGui.activeAbilities;
					for (int i = 0; i < activeAbilities.size(); i++) {
						ActiveAbility ability = activeAbilities.get(i);
						ImGui.indent();
						if (ImGui.button(ability.getDisplayName()) && activeAbility != ability) {
							mousePos = ImGui.getMousePos();
							activeAbility = ability;
						}
						if (ImGui.beginDragDropSource()) {
							ImGui.setDragDropPayload("active_ability_inv", ability);
							ImGui.text(ability.getId());
							ImGui.endDragDropSource();
						}
						if (ImGui.beginDragDropTarget()) {
							Object object = ImGui.acceptDragDropPayload("active_ability_inv");
							if (object != null) {
								ActiveAbility ability1 = (ActiveAbility) object;
								if (abilityGui.activeAbilities.contains(ability1)) {
									abilityGui.activeAbilities.set(abilityGui.activeAbilities.indexOf(ability1), ability);
									abilityGui.activeAbilities.set(i, ability1);
								}
							}
							ImGui.endDragDropTarget();
						}
						ImGui.unindent();
					}
					ImGui.treePop();
				}
				ImGui.popStyleColor(3);
			}
			if(abilityGui.passiveAbilities.size() != 0){
				if (ImGui.treeNode("Passive Abilities:")) {
					for(PassiveAbility ability : abilityGui.passiveAbilities) {
						ImGui.indent();
						ImGui.text(ability.getDisplayName());
						if(ImGui.isItemClicked() && passiveAbility != ability){
							mousePos = ImGui.getMousePos();
							passiveAbility = ability;
						}
						ImGui.unindent();
					}
					ImGui.treePop();
				}
			}
			ImGui.endChild();
		}
		//
		ImGui.end();
		//
		if(this.currentItem != null){
			ImGui.pushStyleColor(ImGuiCol.TitleBg, Color.TRANSLUCENT);
			ImGui.pushStyleColor(ImGuiCol.TitleBgActive, Color.TRANSLUCENT);
			ImGui.pushStyleColor(ImGuiCol.TitleBgCollapsed, Color.TRANSLUCENT);

			ImGui.pushStyleColor(ImGuiCol.Text, Color.TRANSLUCENT);

			ImGui.setNextWindowPos(mousePos.x, mousePos.y);

			ImGui.begin(currentItem.getId(), ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoCollapse);
			ImGui.popStyleColor();


			ItemInformation ItemInfo = ItemInformation.itemInformationByItemType.get(currentItem.getType());
			ItemInfo.RenderInGui(gm);

			if(this.currentItem instanceof ConsumableItem){
				ImGui.sameLine();
				if(ImGui.button("Use")){
					// send use packet
					ClientLauncher.getGame().sendToServerTcp(new PlayerInteractItemPacket(GameClient.uuid, contents.get(currentItemIndex).getId()));
					//
					currentItem = null;
				}
			}

			if(!ImGui.isWindowFocused()){
				this.currentItem = null;
			}

			ImGui.end();
			ImGui.popStyleColor(3);
		}

		// equipment current item
		if(this.currentEquipmentSlot != EquipmentSlot.none && inventory.getEquipment().getItemInSlot(currentEquipmentSlot) != null){
			ImGui.pushStyleColor(ImGuiCol.TitleBg, Color.TRANSLUCENT);
			ImGui.pushStyleColor(ImGuiCol.TitleBgActive, Color.TRANSLUCENT);
			ImGui.pushStyleColor(ImGuiCol.TitleBgCollapsed, Color.TRANSLUCENT);

			ImGui.pushStyleColor(ImGuiCol.Text, Color.TRANSLUCENT);

			ImGui.setNextWindowPos(mousePos.x, mousePos.y);

			ImGui.begin("EquipmentPopup-" + inventory.getEquipment().getItemInSlot(currentEquipmentSlot).getId(),
					ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoCollapse);
			ImGui.popStyleColor();


			ItemInformation ItemInfo = ItemInformation.itemInformationByItemType.get(inventory.getEquipment().getItemInSlot(currentEquipmentSlot).getType());
			ItemInfo.RenderInGui(gm);

			if(!ImGui.isWindowFocused()){
				this.currentEquipmentSlot = EquipmentSlot.none;
			}
			ImGui.end();
			ImGui.popStyleColor(3);
		}
		//
		if(activeAbility != null){
			ImGui.setNextWindowPos(mousePos.x, mousePos.y);
			ImGui.pushStyleColor(ImGuiCol.Text, Color.TRANSLUCENT);
			ImGui.begin(activeAbility.getDisplayName() + "inv", ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize);
			ImGui.popStyleColor();
			ImGui.setWindowSize(120, Math.max(ImGui.getWindowHeight(), 80));
			ImGui.textWrapped(activeAbility.description());
			if(!ImGui.isWindowFocused()){
				activeAbility = null;
			}
			ImGui.end();
		}
		//
		if(passiveAbility != null){
			ImGui.setNextWindowPos(mousePos.x, mousePos.y);
			ImGui.pushStyleColor(ImGuiCol.Text, Color.TRANSLUCENT);
			ImGui.begin(passiveAbility.getDisplayName() + "inv", ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize);
			ImGui.popStyleColor();
			ImGui.setWindowSize(120, Math.max(ImGui.getWindowHeight(), 80));
			ImGui.textWrapped(passiveAbility.description());
			if(!ImGui.isWindowFocused()){
				passiveAbility = null;
			}
			ImGui.end();
		}
		// crafting menu
		if(craftingRendering){
			//
			ImGui.begin("Crafting:");
			gm.Text("Player Crafting Station:   ", new Color(145, 226, 255));
			ImGui.newLine();
			//
			int c1 = 0;
			int c2 = (int) ImGui.getWindowWidth() / 48;
			List<String> recipeKeys = new ArrayList<>(unlockedRecipes.keySet());
			for(String key : recipeKeys){
				if (ImGui.imageButton(ObjectLoader.getTexture("items_" + unlockedRecipes.get(key).getResult().getItemType()), 32, 32)) {
					currentRecipe = unlockedRecipes.get(key);
				}
				c1++;
				if(c1 < c2) {
					ImGui.sameLine();
				}
				else{
					c1 = 0;
				}
			}
			//
			if(currentRecipe != null && unlockedRecipes.containsValue(currentRecipe)){
				gm.Text("");
				currentRecipe.ImGuiDisplayRecipe(gm, inventory);
				if(currentRecipe.canCraft(inventory)){
					if(ImGui.button("Craft")){
						ClientLauncher.getGame().sendToServerTcp(new CraftItemPacket(GameClient.uuid ,currentRecipe.getUuid()));
					}
				}
				else if(inventory.getSize() == inventory.getList().size()){
					ImGui.button("Inventory Full");
				}
				else{
					ImGui.button("Missing ingredients");
				}
			}
			else if(currentRecipe != null && !unlockedRecipes.containsValue(currentRecipe)){
				currentRecipe = null;
			}
			//
			ImGui.end();
		}
		return focused;
	}

	private void renderEquipment(){
		Equipment equipment = inventory.getEquipment();
		ImGui.beginChild("Equipment:",240, ImGui.getWindowHeight() - 120);
		ImGui.text("Equipment:");
		ImGui.text("");
		ImGui.sameLine();
		if (equipment.getHead() != null) {
			if(ImGui.imageButton(ObjectLoader.getTexture("items_" + equipment.getHead().getType()), 32, 32)){
				toggleCurrentSlot(EquipmentSlot.head);
			}
		}
		else if(ImGui.imageButton(ObjectLoader.getTexture("gui_equipment_head"), 32, 32)){
			toggleCurrentSlot(EquipmentSlot.head);
		}
		if(equipment.getHead() != null) {
			if (ImGui.beginDragDropSource()) {
				ImGui.setDragDropPayload("equipment_ret_payload", EquipmentSlot.head);
				ImGui.image(ObjectLoader.getTexture("items_" + equipment.getHead().getType()), 16, 16);
				ImGui.endDragDropSource();
			}
		}
		if(ImGui.beginDragDropTarget()){
			Object obj = ImGui.acceptDragDropPayload("item_payload");
			if(obj != null) {
				Item item = (Item) obj;
				ClientLauncher.getGame().sendToServerTcp(new PlayerEquipItemPacket(GameClient.uuid, EquipmentSlot.head, item.getId()));
			}
			ImGui.endDragDropTarget();
		}
		//
		ImGui.sameLine();
		if (equipment.getNecklace() != null) {
			if(ImGui.imageButton(ObjectLoader.getTexture("items_" + equipment.getNecklace().getType()), 32, 32)){
				toggleCurrentSlot(EquipmentSlot.necklace);
			}
		}
		else if(ImGui.imageButton(ObjectLoader.getTexture("gui_equipment_necklace"), 32, 32)){
			toggleCurrentSlot(EquipmentSlot.necklace);
		}
		if(equipment.getNecklace() != null) {
			if (ImGui.beginDragDropSource()) {
				ImGui.setDragDropPayload("equipment_ret_payload", EquipmentSlot.necklace);
				ImGui.image(ObjectLoader.getTexture("items_" + equipment.getNecklace().getType()), 16, 16);
				ImGui.endDragDropSource();
			}
		}
		if(ImGui.beginDragDropTarget()){
			Object obj = ImGui.acceptDragDropPayload("item_payload");
			if(obj != null) {
				Item item = (Item) obj;
				ClientLauncher.getGame().sendToServerTcp(new PlayerEquipItemPacket(GameClient.uuid, EquipmentSlot.necklace, item.getId()));
			}
			ImGui.endDragDropTarget();
		}
		//
		ImGui.text("");
		ImGui.sameLine();
		if (equipment.getChest() != null){
			if(ImGui.imageButton(ObjectLoader.getTexture("items_" + equipment.getChest().getType()), 32, 32)){
				toggleCurrentSlot(EquipmentSlot.chest);
			}
		}
		else if(ImGui.imageButton(ObjectLoader.getTexture("gui_equipment_chest"), 32, 32)) {
			toggleCurrentSlot(EquipmentSlot.chest);
		}
		if(equipment.getChest() != null) {
			if (ImGui.beginDragDropSource()) {
				ImGui.setDragDropPayload("equipment_ret_payload", EquipmentSlot.chest);
				ImGui.image(ObjectLoader.getTexture("items_" + equipment.getChest().getType()), 16, 16);
				ImGui.endDragDropSource();
			}
		}
		if(ImGui.beginDragDropTarget()){
			Object obj = ImGui.acceptDragDropPayload("item_payload");
			if(obj != null) {
				Item item = (Item) obj;
				ClientLauncher.getGame().sendToServerTcp(new PlayerEquipItemPacket(GameClient.uuid, EquipmentSlot.chest, item.getId()));
			}
			ImGui.endDragDropTarget();
		}
		//
		ImGui.sameLine();
		ImGui.beginChild("equipment_ring_1",48,48);
		if (equipment.getRing_1() != null) {
			if(ImGui.imageButton(ObjectLoader.getTexture("items_" + equipment.getRing_1().getType()), 32, 32)){
				toggleCurrentSlot(EquipmentSlot.ring_1);
			}
		}
		else if(ImGui.imageButton(ObjectLoader.getTexture("gui_equipment_ring"), 32, 32)){
			toggleCurrentSlot(EquipmentSlot.ring_1);
		}
		if(equipment.getRing_1() != null) {
			if (ImGui.beginDragDropSource()) {
				ImGui.setDragDropPayload("equipment_ret_payload", EquipmentSlot.ring_1);
				ImGui.image(ObjectLoader.getTexture("items_" + equipment.getRing_1().getType()), 16, 16);
				ImGui.endDragDropSource();
			}
		}
		if(ImGui.beginDragDropTarget()){
			Object obj = ImGui.acceptDragDropPayload("item_payload");
			if(obj != null) {
				Item item = (Item) obj;
				ClientLauncher.getGame().sendToServerTcp(new PlayerEquipItemPacket(GameClient.uuid, EquipmentSlot.ring_1, item.getId()));
			}
			ImGui.endDragDropTarget();
		}
		ImGui.endChild();
		//
		ImGui.sameLine();
		ImGui.beginChild("equipment_ring_2",48,48);
		if (equipment.getRing_2() != null) {
			if(ImGui.imageButton(ObjectLoader.getTexture("items_" + equipment.getRing_2().getType()), 32, 32)){
				toggleCurrentSlot(EquipmentSlot.ring_2);
			}
		}
		else if(ImGui.imageButton(ObjectLoader.getTexture("gui_equipment_ring"), 32, 32)){
			toggleCurrentSlot(EquipmentSlot.ring_2);
		}
		if(equipment.getRing_2() != null) {
			if (ImGui.beginDragDropSource()) {
				ImGui.setDragDropPayload("equipment_ret_payload", EquipmentSlot.ring_2);
				ImGui.image(ObjectLoader.getTexture("items_" + equipment.getRing_2().getType()), 16, 16);
				ImGui.endDragDropSource();
			}
		}
		if(ImGui.beginDragDropTarget()){
			Object obj = ImGui.acceptDragDropPayload("item_payload");
			if(obj != null) {
				Item item = (Item) obj;
				ClientLauncher.getGame().sendToServerTcp(new PlayerEquipItemPacket(GameClient.uuid, EquipmentSlot.ring_2, item.getId()));
			}
			ImGui.endDragDropTarget();
		}
		ImGui.endChild();
		//
		ImGui.text("");
		ImGui.sameLine();
		if (equipment.getLegs() != null) {
			if(ImGui.imageButton(ObjectLoader.getTexture("items_" + equipment.getLegs().getType()), 32, 32)){
				toggleCurrentSlot(EquipmentSlot.legs);
			}
		}
		else if(ImGui.imageButton(ObjectLoader.getTexture("gui_equipment_legs"), 32, 32)) {
			toggleCurrentSlot(EquipmentSlot.legs);
		}
		if(equipment.getLegs() != null) {
			if (ImGui.beginDragDropSource()) {
				ImGui.setDragDropPayload("equipment_ret_payload", EquipmentSlot.legs);
				ImGui.image(ObjectLoader.getTexture("items_" + equipment.getLegs().getType()), 16, 16);
				ImGui.endDragDropSource();
			}
		}
		if(ImGui.beginDragDropTarget()){
			Object obj = ImGui.acceptDragDropPayload("item_payload");
			if(obj != null) {
				Item item = (Item) obj;
				ClientLauncher.getGame().sendToServerTcp(new PlayerEquipItemPacket(GameClient.uuid, EquipmentSlot.legs, item.getId()));
			}
			ImGui.endDragDropTarget();
		}
		//
		ImGui.sameLine();
		ImGui.beginChild("equipment_ring_3",48,48);
		if (equipment.getRing_3() != null) {
			if(ImGui.imageButton(ObjectLoader.getTexture("items_" + equipment.getRing_3().getType()), 32, 32)){
				toggleCurrentSlot(EquipmentSlot.ring_3);
			}
		}
		else if(ImGui.imageButton(ObjectLoader.getTexture("gui_equipment_ring"), 32, 32)){
			toggleCurrentSlot(EquipmentSlot.ring_3);
		}
		if(equipment.getRing_3() != null) {
			if (ImGui.beginDragDropSource()) {
				ImGui.setDragDropPayload("equipment_ret_payload", EquipmentSlot.ring_3);
				ImGui.image(ObjectLoader.getTexture("items_" + equipment.getRing_3().getType()), 16, 16);
				ImGui.endDragDropSource();
			}
		}
		if(ImGui.beginDragDropTarget()){
			Object obj = ImGui.acceptDragDropPayload("item_payload");
			if(obj != null) {
				Item item = (Item) obj;
				ClientLauncher.getGame().sendToServerTcp(new PlayerEquipItemPacket(GameClient.uuid, EquipmentSlot.ring_3, item.getId()));
			}
			ImGui.endDragDropTarget();
		}
		ImGui.endChild();
		//
		ImGui.sameLine();
		ImGui.beginChild("equipment_ring_4",48,48);
		if (equipment.getRing_4() != null) {
			if(ImGui.imageButton(ObjectLoader.getTexture("items_" + equipment.getRing_4().getType()), 32, 32)){
				toggleCurrentSlot(EquipmentSlot.ring_4);
			}
		}
		else if(ImGui.imageButton(ObjectLoader.getTexture("gui_equipment_ring"), 32, 32)){
			toggleCurrentSlot(EquipmentSlot.ring_4);
		}
		if(equipment.getRing_4() != null) {
			if (ImGui.beginDragDropSource()) {
				ImGui.setDragDropPayload("equipment_ret_payload", EquipmentSlot.ring_4);
				ImGui.image(ObjectLoader.getTexture("items_" + equipment.getRing_4().getType()), 16, 16);
				ImGui.endDragDropSource();
			}
		}
		if(ImGui.beginDragDropTarget()){
			Object obj = ImGui.acceptDragDropPayload("item_payload");
			if(obj != null) {
				Item item = (Item) obj;
				ClientLauncher.getGame().sendToServerTcp(new PlayerEquipItemPacket(GameClient.uuid, EquipmentSlot.ring_4, item.getId()));
			}
			ImGui.endDragDropTarget();
		}
		ImGui.endChild();
		//
		//
		ImGui.text("");
		ImGui.sameLine();
		if (equipment.getFeet() != null) {
			if(ImGui.imageButton(ObjectLoader.getTexture("items_" + equipment.getFeet().getType()), 32, 32)){
				toggleCurrentSlot(EquipmentSlot.feet);
			}
		}
		else if(ImGui.imageButton(ObjectLoader.getTexture("gui_equipment_feet"), 32, 32)) {
			toggleCurrentSlot(EquipmentSlot.feet);
		}
		if(equipment.getFeet() != null) {
			if (ImGui.beginDragDropSource()) {
				ImGui.setDragDropPayload("equipment_ret_payload", EquipmentSlot.feet);
				ImGui.image(ObjectLoader.getTexture("items_" + equipment.getFeet().getType()), 16, 16);
				ImGui.endDragDropSource();
			}
		}
		if(ImGui.beginDragDropTarget()){
			Object obj = ImGui.acceptDragDropPayload("item_payload");
			if(obj != null) {
				Item item = (Item) obj;
				ClientLauncher.getGame().sendToServerTcp(new PlayerEquipItemPacket(GameClient.uuid, EquipmentSlot.feet, item.getId()));
			}
			ImGui.endDragDropTarget();
		}
		//
		ImGui.text("");
		ImGui.sameLine();
		if (equipment.getBack() != null) {
			if(ImGui.imageButton(ObjectLoader.getTexture("items_" + equipment.getBack().getType()), 32, 32)){
				toggleCurrentSlot(EquipmentSlot.back);
			}
		}
		else if(ImGui.imageButton(ObjectLoader.getTexture("gui_equipment_back"), 32, 32)){
			toggleCurrentSlot(EquipmentSlot.back);
		}
		if(equipment.getBack() != null) {
			if (ImGui.beginDragDropSource()) {
				ImGui.setDragDropPayload("equipment_ret_payload", EquipmentSlot.back);
				ImGui.image(ObjectLoader.getTexture("items_" + equipment.getBack().getType()), 16, 16);
				ImGui.endDragDropSource();
			}
		}
		if(ImGui.beginDragDropTarget()){
			Object obj = ImGui.acceptDragDropPayload("item_payload");
			if(obj != null) {
				Item item = (Item) obj;
				ClientLauncher.getGame().sendToServerTcp(new PlayerEquipItemPacket(GameClient.uuid, EquipmentSlot.back, item.getId()));
			}
			ImGui.endDragDropTarget();
		}
		//
		ImGui.sameLine();
		if (equipment.getOff_hand() != null) {
			if(ImGui.imageButton(ObjectLoader.getTexture("items_" + equipment.getOff_hand().getType()), 32, 32)){
				toggleCurrentSlot(EquipmentSlot.off_hand);
			}
		}
		else if(ImGui.imageButton(ObjectLoader.getTexture("gui_equipment_off_hand"), 32, 32)) {
			toggleCurrentSlot(EquipmentSlot.off_hand);
		}
		if(equipment.getOff_hand() != null) {
			if (ImGui.beginDragDropSource()) {
				ImGui.setDragDropPayload("equipment_ret_payload", EquipmentSlot.off_hand);
				ImGui.image(ObjectLoader.getTexture("items_" + equipment.getOff_hand().getType()), 16, 16);
				ImGui.endDragDropSource();
			}
		}
		if(ImGui.beginDragDropTarget()){
			Object obj = ImGui.acceptDragDropPayload("item_payload");
			if(obj != null) {
				Item item = (Item) obj;
				ClientLauncher.getGame().sendToServerTcp(new PlayerEquipItemPacket(GameClient.uuid, EquipmentSlot.off_hand, item.getId()));
			}
			ImGui.endDragDropTarget();
		}
		//
		ImGui.sameLine();
		if (equipment.getMain_hand() != null) {
			if(ImGui.imageButton(ObjectLoader.getTexture("items_" + equipment.getMain_hand().getType()), 32, 32)){
				toggleCurrentSlot(EquipmentSlot.main_hand);
			}
		}
		else if(ImGui.imageButton(ObjectLoader.getTexture("gui_equipment_main_hand"), 32, 32)) {
			toggleCurrentSlot(EquipmentSlot.main_hand);
		}
		if(equipment.getMain_hand() != null) {
			if (ImGui.beginDragDropSource()) {
				ImGui.setDragDropPayload("equipment_ret_payload", EquipmentSlot.main_hand);
				ImGui.image(ObjectLoader.getTexture("items_" + equipment.getMain_hand().getType()), 16, 16);
				ImGui.endDragDropSource();
			}
		}
		if(ImGui.beginDragDropTarget()){
			Object obj = ImGui.acceptDragDropPayload("item_payload");
			if(obj != null) {
				Item item = (Item) obj;
				ClientLauncher.getGame().sendToServerTcp(new PlayerEquipItemPacket(GameClient.uuid, EquipmentSlot.main_hand, item.getId()));
			}
			ImGui.endDragDropTarget();
		}
		ImGui.endChild();
	}
	private void toggleCurrentSlot(EquipmentSlot slot){
		currentEquipmentSlot = currentEquipmentSlot != EquipmentSlot.none && currentEquipmentSlot == slot ? EquipmentSlot.none : slot;
		if(currentEquipmentSlot != EquipmentSlot.none){
			mousePos = ImGui.getMousePos();
		}
	}
}
