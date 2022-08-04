package com.notbestlord;

public class ServerLauncher {
	private static GameServer server;
	public static void main(String[] args) {

		try {
			server = new GameServer();
			//
			server.run();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	public static GameServer getGameServer() {
		return server;
	}

}
