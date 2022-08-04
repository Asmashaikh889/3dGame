package com.notbestlord.core.inventory;

import com.notbestlord.core.GuiManager;
import com.notbestlord.core.inventory.equipment.EquipmentItem;
import com.notbestlord.core.inventory.equipment.EquipmentWeapon;
import com.notbestlord.core.rpg.stats.Stat;
import com.notbestlord.core.utils.ColoredString;
import com.notbestlord.core.utils.Utils;
import imgui.ImGui;

import java.awt.*;

public class EquipmentInformation extends ItemInformation{
	private EquipmentItem item;

	public EquipmentInformation() {}

	public EquipmentInformation(String displayName, String itemType, String description, ItemUseType useType, EquipmentItem item) {
		super(displayName, itemType, description, useType);
		this.item = item;
		itemInformationByItemType.put(itemType,this);
	}
	public ColoredString getAttribute(){
		return new ColoredString(item.getAttribute().getColor(),Utils.UpperCaseStart(item.getAttribute().name()));
	}
	public String getStats(){
		String out = "";
		for(Stat stat : item.getItemStats().keySet()){
			out += "+" + item.getStat(stat) + " " + Utils.UpperCaseStart(stat.name()) + "\n";
		}
		for(Stat stat : item.getItemStatMuls().keySet()){
			out += "+" + ((item.getStatMul(stat) - 1f) * 100f) + "% " + Utils.UpperCaseStart(stat.name()) + "\n";
		}
		return out;
	}

	@Override
	public void RenderInGui(GuiManager gm) {
		gm.Text("\n"+ item.getDisplayName()+":\n" + getDescription());
		if(item instanceof EquipmentWeapon) gm.Text("+" + ((EquipmentWeapon) item).getDamageStat() + " damage");
		gm.Text("Stats:");
		gm.Text(getStats());
		gm.Text("Attribute: ");
		ImGui.sameLine();
		ColoredString clrStr = getAttribute();
		gm.Text(clrStr.getString(),clrStr);
		gm.Text(getUseType().toString(), new Color(255, 236, 178, 255));
	}
}
