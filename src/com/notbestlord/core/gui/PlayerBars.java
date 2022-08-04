package com.notbestlord.core.gui;

import com.notbestlord.ClientLauncher;
import com.notbestlord.core.GuiManager;
import com.notbestlord.core.entity.Player;
import com.notbestlord.core.gui.IGui;
import com.notbestlord.core.rpg.stats.Stat;
import com.notbestlord.core.utils.Utils;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;

import java.awt.*;
import java.util.UUID;

public class PlayerBars implements IGui {
	private String title = UUID.randomUUID().toString();
	private Player owner;

	public PlayerBars(Player owner) {
		this.owner = owner;
	}

	@Override
	public int updateStyle() {
		ImGui.pushStyleColor(ImGuiCol.WindowBg, Color.TRANSLUCENT);
		ImGui.pushStyleColor(ImGuiCol.TitleBg, Color.TRANSLUCENT);
		ImGui.pushStyleColor(ImGuiCol.TitleBgActive, Color.TRANSLUCENT);
		ImGui.pushStyleColor(ImGuiCol.TitleBgCollapsed, Color.TRANSLUCENT);
		ImGui.pushStyleColor(ImGuiCol.ScrollbarBg, Color.TRANSLUCENT);
		ImGui.pushStyleColor(ImGuiCol.ScrollbarGrab, Color.TRANSLUCENT);
		ImGui.pushStyleColor(ImGuiCol.ScrollbarGrabActive, Color.TRANSLUCENT);
		ImGui.pushStyleColor(ImGuiCol.ScrollbarGrabHovered, Color.TRANSLUCENT);
		ImGui.pushStyleColor(ImGuiCol.ResizeGrip, Color.TRANSLUCENT);
		ImGui.pushStyleColor(ImGuiCol.ResizeGripActive, Color.TRANSLUCENT);
		ImGui.pushStyleColor(ImGuiCol.ResizeGripHovered, Color.TRANSLUCENT);
		ImGui.pushStyleColor(ImGuiCol.Border, Color.TRANSLUCENT);
		ImGui.pushStyleColor(ImGuiCol.BorderShadow, Color.TRANSLUCENT);
		return 13;
	}

	@Override
	public boolean renderGui(GuiManager gm) {
		ImGui.pushStyleColor(ImGuiCol.Text, Color.TRANSLUCENT);
		ImGui.begin(title, ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoCollapse);
		ImGui.setWindowPos(0, ClientLauncher.getWindow().getHeight() - ImGui.getWindowHeight());
		ImGui.popStyleColor();
		//
		ImGui.pushStyleColor(ImGuiCol.Text, Color.black.getRGB());
		// health
		ImGui.pushStyleColor(ImGuiCol.PlotHistogram, Utils.getImColor(Color.red));
		ImGui.pushStyleColor(ImGuiCol.PlotHistogramHovered, Color.red.getRGB());
		ImGui.progressBar(getPercent(owner.getStat(Stat.health), owner.getStat(Stat.health_max)),160, 16);
		ImGui.popStyleColor();
		ImGui.popStyleColor();
		ImGui.sameLine();
		gm.Text("Health");
		// mana
		ImGui.pushStyleColor(ImGuiCol.PlotHistogram, Utils.getImColor(new Color(0, 164, 255)));
		ImGui.pushStyleColor(ImGuiCol.PlotHistogramHovered, Utils.getImColor(new Color(0, 164, 255)));
		ImGui.progressBar(getPercent(owner.getStat(Stat.mana), owner.getStat(Stat.mana_max)),124, 16);
		ImGui.popStyleColor();
		ImGui.popStyleColor();
		ImGui.sameLine();
		gm.Text("Mana");
		// stamina
		ImGui.pushStyleColor(ImGuiCol.PlotHistogram, Utils.getImColor(new Color(158, 255, 150)));
		ImGui.pushStyleColor(ImGuiCol.PlotHistogramHovered, Utils.getImColor(new Color(158, 255, 150)));
		ImGui.progressBar(getPercent(owner.getStat(Stat.stamina), owner.getStat(Stat.stamina_max)), 124, 16);
		ImGui.popStyleColor();
		ImGui.popStyleColor();
		ImGui.sameLine();
		gm.Text("Stamina");
		// qi
		if(owner.getStat(Stat.qi_max) != 0) {
			ImGui.pushStyleColor(ImGuiCol.PlotHistogram, Utils.getImColor(new Color(50,220,250)));
			ImGui.pushStyleColor(ImGuiCol.PlotHistogramHovered, Utils.getImColor(new Color(50,220,250)));
			ImGui.progressBar(getPercent(owner.getStat(Stat.qi), owner.getStat(Stat.qi_max)), 124, 16);
			ImGui.popStyleColor();
			ImGui.popStyleColor();
			ImGui.sameLine();
			gm.Text("Qi");
		}
		//
		ImGui.popStyleColor();
		ImGui.end();
		return false;
	}

	private float getPercent(float min, float max){
		if(min/max < 0){
			return 0;
		}
		if(min/max > 1){
			return 1;
		}
		return min/max;
	}
}
