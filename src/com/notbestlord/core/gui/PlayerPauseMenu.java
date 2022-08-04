package com.notbestlord.core.gui;

import com.notbestlord.ClientLauncher;
import com.notbestlord.core.Config;
import com.notbestlord.core.GuiManager;
import com.notbestlord.core.event.KeyCaptureEvent;
import com.notbestlord.core.utils.Controls;
import com.notbestlord.core.utils.Utils;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import org.lwjgl.glfw.GLFW;

public class PlayerPauseMenu implements IGui {
	private IGui currentMenu = null;
	private ControlsMenu controlsMenu = new ControlsMenu();
	private GraphicSettingsMenu graphicSettingsMenu = new GraphicSettingsMenu();

	private class ControlsMenu implements IGui {
		private KeyCaptureEvent keyCaptureEvent = new KeyCaptureEvent();

		@Override
		public int updateStyle() {
			return 0;
		}

		@Override
		public boolean renderGui(GuiManager gm) {
			ImGui.begin("Controls:", ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoCollapse);
			ImGui.setWindowPos(ClientLauncher.getWindow().getWidth() / 2f - ImGui.getWindowWidth() / 2f,
					ClientLauncher.getWindow().getHeight() / 2f - ImGui.getWindowHeight() / 2f);
			for(String key : Controls.defaultControls().keySet()){
				if(!Config.playerControls.controlKeys.containsKey(key)) continue;
				gm.Text(Utils.UpperCaseStart(key)+": ");
				ImGui.sameLine();
				ImGui.beginChild(key, 60, 20);
				if(key.equalsIgnoreCase(keyCaptureEvent.tag)){
					if(ImGui.button("<" + Controls.getKeyName(Config.playerControls.controlKeys.get(key)) + ">")){
						ClientLauncher.getWindow().removeKeyCaptureEvent(keyCaptureEvent);
						keyCaptureEvent.reset();
					}
				}
				else if(ImGui.button("" + Controls.getKeyName(Config.playerControls.controlKeys.get(key)))){
					keyCaptureEvent.tag = key;
					ClientLauncher.getWindow().addKeyCaptureEvent(keyCaptureEvent);
				}
				ImGui.endChild();
			}
			if(keyCaptureEvent.isCaptured()){
				Config.playerControls.controlKeys.put(keyCaptureEvent.tag, keyCaptureEvent.keyCode);
				ClientLauncher.getWindow().removeKeyCaptureEvent(keyCaptureEvent);
				keyCaptureEvent.reset();
			}
			boolean out = ImGui.button("Back");
			ImGui.end();
			return out;
		}
	}
	private class GraphicSettingsMenu implements IGui{

		@Override
		public int updateStyle() {
			return 0;
		}

		@Override
		public boolean renderGui(GuiManager gm) {
			ImGui.begin("Graphic Settings:", ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoCollapse);
			ImGui.setWindowPos(ClientLauncher.getWindow().getWidth() / 2 - ImGui.getWindowWidth() / 2, ClientLauncher.getWindow().getHeight() / 2 - ImGui.getWindowHeight() / 2);
			boolean out = ImGui.button("Back");
			ImGui.end();
			return out;
		}
	}
	
	@Override
	public int updateStyle() {
		return 0;
	}

	public void toggle(){
		currentMenu = null;
		ClientLauncher.getWindow().getGuiManager().toggleInteractGui(this);
	}
	public void close(){
		currentMenu = null;
		ClientLauncher.getWindow().getGuiManager().removeInteractGui(this);
	}

	@Override
	public boolean renderGui(GuiManager gm) {
		//
		if(currentMenu != null){
			if(currentMenu.renderGui(gm)){
				currentMenu = null;
			}
			return false;
		}
		//
		ImGui.begin("Pause Menu:", ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoCollapse);
		ImGui.setWindowPos(ClientLauncher.getWindow().getWidth() / 2 - ImGui.getWindowWidth() / 2, ClientLauncher.getWindow().getHeight() / 2 - ImGui.getWindowHeight() / 2);

		if(ImGui.button("back")){
			ClientLauncher.getWindow().getGuiManager().toggleInteractGui(this);
		}

		if(ImGui.button("Graphics")){
			currentMenu = graphicSettingsMenu;
		}

		if(ImGui.button("Controls")){
			currentMenu = controlsMenu;
		}

		if(ImGui.button("Disconnect")){
			ClientLauncher.getGame().disconnectFromAccount();
		}

		if(ImGui.button("Exit To Desktop")){
			Config.saveConfig();
			GLFW.glfwSetWindowShouldClose(ClientLauncher.getWindow().getWindowHandler(), true);
		}

		ImGui.end();
		return false;
	}
}
