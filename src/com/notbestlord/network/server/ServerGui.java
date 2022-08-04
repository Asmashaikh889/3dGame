package com.notbestlord.network.server;

import com.notbestlord.GameServer;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class ServerGui extends JFrame{
	private final GameServer gameServer;
	private JPanel Server;
	private JTextField commandTextField;
	private JScrollPane Log;
	private JLabel logText;
	private JLabel ServerThreadTPS;
	private JLabel EntityThreadTPS;
	private JLabel OtherInfo;

	private final List<String> commandBacklog = new ArrayList<>();
	private Integer commandP = -1;

	public ServerGui(GameServer gameServer) {
		super("Server");
		this.gameServer = gameServer;
		commandTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == '\n') {
					if(!commandTextField.getText().equalsIgnoreCase("\n")) {
						addToLog(commandTextField.getText());
						commandBacklog.add(0, commandTextField.getText());
						gameServer.handleCommand(commandTextField.getText());
					}
					commandTextField.setText("");
					commandP = -1;
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
				int temp = commandP;
				switch (e.getKeyCode()) {
					case KeyEvent.VK_DOWN -> {
						commandP = temp - (temp == -1 ? 0 : 1);
						updateCurrentCommand();
					}
					case KeyEvent.VK_UP -> {
						commandP = temp + (temp == commandBacklog.size() - 1 ? 0 : 1);
						updateCurrentCommand();
					}
				}
			}
		});
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setContentPane(Server);
		this.pack();
		this.setSize(720,480);
		this.setVisible(true);
	}

	public void updateServerTPS(float deltaTime) {
		ServerThreadTPS.setText("Server Thread TPS: " + tps(deltaTime));
	}

	public void updateEntityThreadTPS(float deltaTime){
		EntityThreadTPS.setText("Entity Thread TPS: " + tps(deltaTime));
	}

	private String tps(float deltaTime){
		String tps = String.format("%.2f",1f / deltaTime);
		return tps.length() > 6 ? tps.substring(0,6) : tps;
	}

	public void addToLog(String txt){
		logText.setText(logText.getText().substring(0, logText.getText().length() - 11) + "<br/>" + txt.replace("\n", "<br/>") + "</p></html>");
	}

	public void close() {
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	public void updateOtherServerInfo(){
		OtherInfo.setText("<html><p>" +
				"Total Connections: " + gameServer.getConnections() +
				"<br/>Total Players: " + gameServer.getTotalPlayers() + "</p></html>");
		Log.getVerticalScrollBar().setValue(Log.getVerticalScrollBar().getMaximumSize().height);
	}

	public void updateCurrentCommand(){
		if(commandP > -1 && commandP < commandBacklog.size()){
			commandTextField.setText(commandBacklog.get(commandP));
		}
		else {
			commandTextField.setText("");
		}
	}
}
