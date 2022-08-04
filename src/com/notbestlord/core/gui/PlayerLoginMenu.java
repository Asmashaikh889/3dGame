package com.notbestlord.core.gui;

import com.notbestlord.ClientLauncher;
import com.notbestlord.core.GuiManager;
import com.notbestlord.network.data.ClientLoginReplyType;
import com.notbestlord.network.packet.client.ClientLoginPacket;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class PlayerLoginMenu implements IGui{
	private final ImString usernameBox = new ImString("", 16);
	private final ImString passwordBox = new ImString("", 16);
	public ClientLoginReplyType currentLoginReply = ClientLoginReplyType.logged_in;
	@Override
	public int updateStyle() {
		return 0;
	}

	@Override
	public boolean renderGui(GuiManager gm) {
		ImGui.begin("Login Menu:", ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoCollapse);
		ImGui.setWindowPos(ClientLauncher.getWindow().getWidth() / 2f - ImGui.getWindowWidth() / 2f,
				ClientLauncher.getWindow().getHeight() / 2f - ImGui.getWindowHeight() / 2f);
		ImGui.text("Enter Username:");
		ImGui.inputText("  ", usernameBox);
		ImGui.text("Enter Password:");
		ImGui.inputText(" ", passwordBox);
		if(ImGui.button("Register")){
			ClientLauncher.getGame().sendToServerTcp(new ClientLoginPacket(usernameBox.get(), passwordBox.get(), false));
		}
		ImGui.sameLine();
		ImGui.text("    ");
		ImGui.sameLine();
		if(ImGui.button("Login")){
			ClientLauncher.getGame().sendToServerTcp(new ClientLoginPacket(usernameBox.get(), passwordBox.get(), true));
		}
		switch (currentLoginReply){
			case user_exists -> gm.Text("Username already exists.", Color.red);
			case password_short -> gm.Text("Password short,\nat least 6 characters.", Color.red);
			case password_long -> gm.Text("Password long,\nat most 16 characters.", Color.red);
			case username_short -> gm.Text("Username short,\nat least 4 characters.", Color.red);
			case incorrect_login -> gm.Text("Incorrect username/password.", Color.red);
			case user_no_exist -> gm.Text("Username does not exist.", Color.red);
		}
		ImGui.sameLine();
		ImGui.text("    ");
		ImGui.sameLine();
		if(ImGui.button("Exit")){
			GLFW.glfwSetWindowShouldClose(ClientLauncher.getWindow().getWindowHandler(), true);
		}
		ImGui.end();
		return false;
	}
}
