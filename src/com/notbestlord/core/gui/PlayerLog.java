package com.notbestlord.core.gui;

import com.notbestlord.ClientLauncher;
import com.notbestlord.core.GuiManager;
import com.notbestlord.core.gui.IGui;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerLog implements IGui {
	private final List<String> logStrings = new ArrayList<>();

	public void addLog(String s){
		logStrings.add(0, s);
		if(logStrings.size() > 100){
			logStrings.remove(logStrings.size()-1);
		}
	}

	public void clearLog(){
		logStrings.clear();
	}

	@Override
	public int updateStyle() {
		int transWhite = new Color(255, 255, 255, 128).getRGB();
		ImGui.pushStyleColor(ImGuiCol.WindowBg, transWhite);
		ImGui.pushStyleColor(ImGuiCol.TitleBg, transWhite);
		ImGui.pushStyleColor(ImGuiCol.TitleBgActive, transWhite);
		ImGui.pushStyleColor(ImGuiCol.TitleBgCollapsed, transWhite);
		ImGui.pushStyleColor(ImGuiCol.Text, Color.black.getRGB());
		ImGui.pushStyleColor(ImGuiCol.ResizeGrip, Color.TRANSLUCENT);
		ImGui.pushStyleColor(ImGuiCol.ResizeGripActive, Color.TRANSLUCENT);
		ImGui.pushStyleColor(ImGuiCol.ResizeGripHovered, Color.TRANSLUCENT);
		return 8;
	}

	@Override
	public boolean renderGui(GuiManager gm) {
		if(ClientLauncher.getWindow().isUiOpen()) {
			ImGui.begin("Log", ImGuiWindowFlags.NoScrollbar);
		}
		else{
			ImGui.begin("Log", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize);
		}
		for(int i=0; i< logStrings.size(); i++) {
			String string = logStrings.get(i);
			ImGui.textWrapped(string);
		}
		ImGui.end();
		return false;
	}
}
