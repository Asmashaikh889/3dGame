package com.notbestlord.core;

import com.notbestlord.core.gui.IGui;
import imgui.ImGui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GuiManager {
	private List<IGui> interactGuis;
	private List<IGui> guis;

	public GuiManager(){
		guis = new ArrayList<>();
		interactGuis = new ArrayList<>();
	}

	public int addGui(IGui gui){
		guis.add(gui);
		return guis.indexOf(gui);
	}
	public boolean hasGui(IGui gui){return guis.contains(gui);}
	public void removeGui(IGui gui){guis.remove(gui);}
	public void removeInteractGui(IGui gui){if(interactGuis.contains(gui)) interactGuis.remove(gui);}
	public void toggleGui(IGui gui){
		if(guis.contains(gui)){
			guis.remove(gui);
		}
		else{
			guis.add(gui);
		}
	}
	public void toggleInteractGui(IGui gui){
		if(interactGuis.contains(gui)){
			interactGuis.remove(gui);
		}
		else{
			interactGuis.add(gui);
		}
	}
	public boolean isGuiClosed(){ return guis.isEmpty(); }
	public boolean isInteractGuiClosed(){ return interactGuis.isEmpty(); }

	public void renderGuis(){
		for(int i=0; i< guis.size();i++){
			int count = guis.get(i).updateStyle();
			guis.get(i).renderGui(this);
			ImGui.popStyleColor(count);
		}
	}

	public boolean renderInteractiveGuis(){
		boolean out = false;
		for(int i=0; i<interactGuis.size();i++){
			int count = interactGuis.get(i).updateStyle();
			if(interactGuis.get(i).renderGui(this)) out = true;
			ImGui.popStyleColor(count);
		}
		return out;
	}

	public void Text(String txt){ImGui.text(txt);}
	public void Text(String txt, Color color){ImGui.textColored(color.getRed(),color.getGreen(),color.getBlue(), color.getAlpha(), txt);}
}
