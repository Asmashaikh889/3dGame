package com.notbestlord.core.inventory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.notbestlord.core.GuiManager;
import com.notbestlord.core.utils.Utils;
import imgui.ImGui;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemInformation {
	public static Map<String, ItemInformation> itemInformationByItemType = new HashMap<>();
	private String itemType;
	private String displayName;
	private String description;
	private ItemUseType useType;

	public ItemInformation() {}

	public ItemInformation(String displayName, String itemType, String description, ItemUseType useType) {
		this.itemType = itemType;
		this.displayName = displayName;
		this.description = description;
		this.useType = useType;
		load();
	}

	public void load(){
		itemInformationByItemType.put(itemType, this);
	}

	public String getItemType() {
		return itemType;
	}

	public String getDescription() {
		return description;
	}

	public ItemUseType getUseType() {
		return useType;
	}

	public void RenderInGui(GuiManager gm){
		ImGui.text(displayName+":\n" + description);
		gm.Text(useType.toString(), new Color(255, 236, 178, 255));
	}



	public static void saveToJson(){
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		for(String itemType : itemInformationByItemType.keySet()){
			try{
				File file = new File("save/item_information/" + itemType + ".json");
				file.createNewFile();
				FileWriter writer = new FileWriter(file);
				writer.write(gson.toJson(itemInformationByItemType.get(itemType)));
				writer.flush();
				writer.close();
			}
			catch(Exception ignored) {}
		}
	}

	public static void loadFromJson() {
		Gson gson = new Gson();
		List<String> files = new ArrayList<>();
		Utils.addFilesFromDir(files, "template", new File("save/item_information"));
		//
		for(String pathFile : files){
			try {
				ItemInformation info = gson.fromJson(new FileReader(pathFile), ItemInformation.class);
				if(info.getUseType().name().contains("equipment")){
					info = gson.fromJson(new FileReader(pathFile), EquipmentInformation.class);
				}
				info.load();
			}
			catch (FileNotFoundException ignored) {}
		}
	}
}
