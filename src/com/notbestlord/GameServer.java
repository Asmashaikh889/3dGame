package com.notbestlord;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.notbestlord.core.PhysicsManager;
import com.notbestlord.core.event.EventManager;
import com.notbestlord.core.inventory.ItemInformation;
import com.notbestlord.core.inventory.ItemManager;
import com.notbestlord.core.inventory.ItemRegistry;
import com.notbestlord.core.inventory.RecipeManager;
import com.notbestlord.core.inventory.recipe.IRecipe;
import com.notbestlord.core.rpg.abilites.AbilityManager;
import com.notbestlord.core.rpg.npcs.NPC_ID;
import com.notbestlord.core.rpg.quests.Quest;
import com.notbestlord.core.rpg.skills.Skill;
import com.notbestlord.core.rpg.skills.SubSkill;
import com.notbestlord.core.rpg.stats.Stat;
import com.notbestlord.core.utils.Consts;
import com.notbestlord.network.client.ItemsRegisterPacket;
import com.notbestlord.network.data.ClientLoginReplyType;
import com.notbestlord.network.packet.client.ItemInfoListPacket;
import com.notbestlord.network.packet.client.*;
import com.notbestlord.network.packet.entity.EntityAddPacket;
import com.notbestlord.network.packet.entity.EntityMovementPacket;
import com.notbestlord.network.packet.entity.EntityRemovePacket;
import com.notbestlord.network.packet.entity.EntityRotatePacket;
import com.notbestlord.network.packet.player.*;
import com.notbestlord.network.packet.player.inventory.*;
import com.notbestlord.network.packet.player.rpg.*;
import com.notbestlord.network.server.EntityBuffer;
import com.notbestlord.network.server.ServerGui;
import com.notbestlord.network.server.UserdataList;
import com.notbestlord.network.server.entity.*;
import com.notbestlord.serialization.PlayerSerializer;
import org.joml.Vector3f;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

import static com.notbestlord.core.EngineManager.NANOSECOND;

public class GameServer extends Listener {
	private final Server serverHandler;
	private static final int tcpPort = 25575, udpPort = 25576;

	private final PhysicsManager physicsManager;

	private final PlayerSerializer playerSerializer = new PlayerSerializer();

	private final UserdataList usersLoginData;

	private final Map<Integer, ServerPlayer> playersByClientId = new HashMap<>();
	private final Map<String, ServerPlayer> playersByUuid = new HashMap<>();
	private final Map<String, Integer> clientIdByUuid = new HashMap<>();

	private final List<ServerEntity> entities = new ArrayList<>();
	private final List<ServerEntitySpawner> spawners = new ArrayList<>();
	private final List<ServerTerrain> terrains = new ArrayList<>();

	private volatile boolean isRunning = true;

	private final ServerGui gui;

	public GameServer() throws Exception {
		serverHandler = new Server();
		physicsManager = new PhysicsManager();
		gui = new ServerGui(this);
		//
		usersLoginData = loadUserPasswords();
		//
		Kryo serializer = serverHandler.getKryo();
		serializer.setRegistrationRequired(false);

		serverHandler.bind(tcpPort,udpPort);

		serverHandler.start();

		serverHandler.addListener(this);

		init();
		
	}

	public PhysicsManager getPhysics() {
		return physicsManager;
	}

	private void init(){
		//
		ItemManager.init();
		RecipeManager.init();
		Skill.initSkillSystem();
		EventManager.init();
		AbilityManager.init();
		//

		ServerTerrain terrain0 = new ServerTerrain(4,new Vector3f(0,0,0),"stone_bricks", 1,1,1);
		terrains.add(terrain0);
		physicsManager.add(terrain0.getCollider(),terrain0.getPosition());

		spawners.add(new ServerEntitySpawner(new Vector3f(0,0,0), new Vector3f(10,2,10), new Slime(new Vector3f()), 5, 0.25f));
		/*for(int i = 0; i < 5; i++) {
			Slime slime = new Slime(new Vector3f((float) (10f * Math.random()), (float) Math.random(), (float) (10f * Math.random())));
			slime.getRotation().set(180f * Math.random(), 180f * Math.random(), 0);
			entities.add(slime);
		}*/
		ServerNPCEntity blacksmithJohn = new ServerNPCEntity(UUID.randomUUID().toString(), new Vector3f(2,1,2), new Vector3f(), 0.3f, "cube", "green", NPC_ID.test_blacksmith_john);
		blacksmithJohn.addDialogue("none", new ArrayList<>(List.of("Nice to meet'cha, the names John.", "Im the blacksmith of the town,", "so come to me if you need a weapon.")));
		blacksmithJohn.addDialogue("none", new ArrayList<>(List.of("I need ya help, please kill 2 slimes for me.")), Quest.blacksmith_john_0.getQuest());
		entities.add(blacksmithJohn);
		entities.add(new ServerHarvestableEntity(new Vector3f(2,1,4), 0.75f, "iron_ore", 12,"",new ItemRegistry("iron_ingot", 1), "mining_1", "mining_1_1"));
		ServerEventEntity anvil = new ServerEventEntity(new Vector3f(4,1,1), 0.5f, "anvil", "anvil", "status_effect.anvil 300");
		physicsManager.addEntity(anvil.getUuid(), anvil.getCollider(),  anvil.getPosition());
		entities.add(anvil);
	}

	public void run() {

		startEntityThread();

		long lastTime = System.nanoTime();
		double unprocessedTime = 0;
		double time2 = 0;
		int tps = 1000;

		while (isRunning) {
			long startTime = System.nanoTime();
			long passedTime = startTime - lastTime;
			lastTime = startTime;

			unprocessedTime += passedTime / (double) NANOSECOND;
			time2 += passedTime / (double) NANOSECOND;
			if (unprocessedTime > 1f / tps) {
				if(time2 > 5d){
					gui.updateServerTPS((float) unprocessedTime);
					time2 = 0;
				}
				Consts.trueDeltaTime = (float) unprocessedTime;
				Consts.deltaTime = Consts.trueDeltaTime * 100;
				tickUpdate();
			}
			while (unprocessedTime > 1f / tps) {
				unprocessedTime -= 1f / tps;
			}
		}
		//
		saveUserPasswords(usersLoginData);
		serverHandler.stop();
	}

	public void handleCommand(String command){
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		String[] args = command.split("\\s+");

		if(args.length >= 2 && args[0].equalsIgnoreCase("/save")){
			switch (args[1].toLowerCase()){
				case "items" -> {
					ItemManager.saveToJson();
					gui.addToLog("Items saved.");
				}
				case "recipes" -> {
					RecipeManager.saveToJson();
					gui.addToLog("Recipes saved.");
				}
				case "item_info" -> {
					ItemInformation.saveToJson();
					gui.addToLog("Item information saved.");
				}
				case "events" -> {
					EventManager.saveToJson();
					gui.addToLog("Events saved.");
				}
				case "abilities" -> {
					AbilityManager.saveToJson();
					gui.addToLog("Abilities saved.");
				}
			}
		}

		if(args.length >= 2 && args[0].equalsIgnoreCase("/load")){
			switch (args[1].toLowerCase()){
				case "items" -> {
					ItemManager.loadFromJson();
					gui.addToLog("Items loaded.");
				}
				case "recipes" -> {
					RecipeManager.loadFromJson();
					gui.addToLog("Recipes loaded.");
				}
				case "item_info" -> {
					ItemInformation.loadFromJson();
					gui.addToLog("Item information loaded.");
				}
				case "events" -> {
					EventManager.loadFromJson();
					gui.addToLog("Events loaded.");
				}
				case "abilities" -> {
					AbilityManager.loadFromJson();
					gui.addToLog("Abilities loaded.");
				}
			}
		}

		if(args.length >= 3 && args[0].equalsIgnoreCase("/display")){
			switch (args[1].toLowerCase()){
				case "item" -> {
					try {
						gui.addToLog(gson.toJson(ItemManager.getItem(args[2].toLowerCase())).replace("\n", "<br/>"));
					}
					catch (Exception e) {
						gui.addToLog("Item does not exist.");
					}
				}
			}
		}

		if(args.length >= 3 && args[0].equalsIgnoreCase("/give")){
			ServerPlayer player = playersByUuid.get(usersLoginData.getUuid(args[1]));
			if(player != null) {
				try {
					player.getInventory().addItem(ItemManager.getItem(args[2], 1));
				} catch (Exception e) {
					if(player.getInventory().firstEmpty() == -1) gui.addToLog("Inventory full.");
					else gui.addToLog("Item does not exist.");
				}
			}
			else gui.addToLog("Player not found.");
		}

		if(args.length >= 1 && args[0].equalsIgnoreCase("/stop")){
			this.close();
			gui.close();
		}
	}

	public void startEntityThread(){
		Thread thread = new Thread(() -> {
			//update entities for players
			long lastTime = System.nanoTime();
			double unprocessedTime = 0;
			double time2 = 0;
			int tps = 200;
			float bufferTimeMs = 0.04f;

			EntityBuffer buffer = new EntityBuffer((int) (tps * bufferTimeMs));

			while (isRunning) {
				long startTime = System.nanoTime();
				long passedTime = startTime - lastTime;
				lastTime = startTime;

				unprocessedTime += passedTime / (double) NANOSECOND;
				time2 += passedTime / (double) NANOSECOND;
				if (unprocessedTime > 1f / tps) {
					if(time2 > 5d){
						gui.updateEntityThreadTPS((float) unprocessedTime);
						time2 = 0;
					}
					//
					for (int i = 0; i < entities.size(); i++) {
						try {
							ServerEntity entity = entities.get(i);
							if(entity instanceof ServerInteractEntity interact && interact.isDead()){
								sendPacketToAllPlayersTCP(new EntityRemovePacket(entity.getUuid()));
								buffer.removeFromBuffer(entity.getUuid());
								entities.remove(i);
								i--;
							}
							else {
								// use buffer to send entity position
								Vector3f position = buffer.pushToPositionBuffer(entity.getUuid(), entity.getPosition());
								if (position != null) {
									sendPacketToAllPlayersUDP(new EntityMovementPacket(entity.getUuid(), position));
								}
								// use buffer to send entity rotation
								Vector3f rotation = buffer.pushToRotationBuffer(entity.getUuid(), entity.getPosition());
								if (rotation != null) {
									sendPacketToAllPlayersUDP(new EntityRotatePacket(entity.getUuid(), rotation));
								}
							}
						}
						catch (IndexOutOfBoundsException ignored) {}
					}
					//
				}
				while (unprocessedTime > 1f / tps) {
					unprocessedTime -= 1f / tps;
				}
			}
		});
		thread.start();
	}

	public void tickUpdate(){
		// entity updating
		for (int i = 0; i < entities.size(); i++) {
			ServerEntity entity = entities.get(i);
			entity.frameUpdate();
		}
		// player movement
		List<ServerPlayer> players = new ArrayList<>(playersByClientId.values());
		for(ServerPlayer player : players){
			player.resetVelocity();
			player.movePosition(physicsManager);
		}

		for(ServerEntitySpawner spawner : spawners){
			spawner.tickUpdate();
		}
	}

	public void addEntity(ServerEntity entity){
		entities.add(entity);
		sendPacketToAllPlayersTCP(new EntityAddPacket(entity.asEntity()));
	}

	@Override
	public void connected(Connection connection) {
		gui.addToLog("Client [" + connection.getID() + "] connected.");
		gui.updateOtherServerInfo();
		connection.sendTCP(new ItemsRegisterPacket(ItemManager.Items, ItemManager.itemTypes));
		connection.sendTCP(new ItemInfoListPacket(ItemInformation.itemInformationByItemType));
	}

	@Override
	public void disconnected(Connection connection) {
		if(playersByClientId.containsKey(connection.getID())) {
			String uuid = playersByClientId.get(connection.getID()).getUuid();
			entities.remove(playersByUuid.get(uuid));
			savePlayer(uuid);
			clientIdByUuid.remove(uuid);
			playersByClientId.remove(connection.getID());
			playersByUuid.get(uuid).removeFromPhysics(physicsManager);
			playersByUuid.remove(uuid);
			sendPacketToAllPlayersTCP(new EntityRemovePacket(uuid));
			gui.addToLog("Client [" + connection.getID() + "] disconnected from server.");
			gui.updateOtherServerInfo();
		}
	}

	@Override
	public void received(Connection connection, Object o) {
		if(o instanceof ClientLoginPacket packet){
			String uuid;
			//
			if(packet.isExist()){
				UserdataList.UserLoginReply reply = usersLoginData.userLogin(packet.getUserName(), packet.getPassword());
				if(reply.replyType != ClientLoginReplyType.logged_in){
					connection.sendTCP(new ClientConnectPacket("", reply.replyType));
					return;
				}
				uuid = reply.uuid;
			}
			else{
				ClientLoginReplyType reply = usersLoginData.registerUsernameReply(packet.getUserName(), packet.getPassword());
				switch (reply){
					case user_exists -> connection.sendTCP(new ClientConnectPacket("", ClientLoginReplyType.user_exists));
					case username_short -> connection.sendTCP(new ClientConnectPacket("", ClientLoginReplyType.username_short));
					case password_long -> connection.sendTCP(new ClientConnectPacket("", ClientLoginReplyType.password_long));
					case password_short -> connection.sendTCP(new ClientConnectPacket("", ClientLoginReplyType.password_short));
				}
				if(reply != ClientLoginReplyType.logged_in){
					return;
				}
				uuid = UUID.randomUUID().toString();
				usersLoginData.usersDataList.add(new UserdataList.Userdata(packet.getUserName().toLowerCase(), packet.getPassword(), uuid));
				saveUserPasswords(usersLoginData);
			}
			//
			connection.sendTCP(new ClientConnectPacket(uuid, ClientLoginReplyType.logged_in));
			//
			clientIdByUuid.put(uuid, connection.getID());
			ServerPlayer player = loadPlayer(uuid);
			if(player == null) {
				player = new ServerPlayer(uuid);
			}
			playersByClientId.put(connection.getID(), player);
			playersByUuid.put(uuid, player);
			entities.add(player);
			player.addToPhysics(physicsManager);
			gui.addToLog("Client [" + connection.getID() + "] connected with username [" + packet.getUserName() + "] and uuid [" + uuid + "].");
			// send player current world data
			TerrainPacket terrainPacket = new TerrainPacket();
			for (ServerTerrain terrain : terrains) {
				terrainPacket.terrains().add(terrain.getData());
			}
			sendPacketToClient(connection.getID(), terrainPacket);
			//
			for(ServerEntity entity : entities){
				sendPacketToClient(connection.getID(), new EntityAddPacket(entity.asEntity()));
			}
			// add player entity to active clients
			sendPacketToAllPlayersTCP(new EntityAddPacket(player.asEntity()));
			sendPacketToAllPlayersTCP(new EntityMovementPacket(player.getUuid(), player.getPosition()));
			// send inventory
			player.getInventory().refreshOnClient();
			// send stats
			for(Stat stat : Stat.values()){
				if(player.getRPGHandler().statHandler.hasStat(stat)){
					connection.sendTCP(new StatUpdatePacket(stat, player.getStat(stat)));
				}
			}
			// send skills
			for(Skill skill : player.getRPGHandler().skillHandler.skillList().values()){
				connection.sendTCP(new SkillUpdatePacket(skill));
			}
			for(SubSkill subSkill : player.getRPGHandler().skillHandler.subSkillList().values()){
				connection.sendTCP(new SubSkillUpdatePacket(subSkill));
			}
			// send abilities
			player.getRPGHandler().abilityHandler.playerLogin();
			// send unlocked recipes
			for(IRecipe recipe : player.getInventory().getUnlockedRecipes().values()){
				connection.sendTCP(new AddRecipePacket(recipe));
			}
			gui.updateOtherServerInfo();
		}
		else if(o instanceof ClientDisconnectPacket){
			String uuid = playersByClientId.get(connection.getID()).getUuid();
			entities.remove(playersByUuid.get(uuid));
			savePlayer(uuid);
			clientIdByUuid.remove(uuid);
			playersByClientId.remove(connection.getID());
			playersByUuid.get(uuid).removeFromPhysics(physicsManager);
			playersByUuid.remove(uuid);
			sendPacketToAllPlayersTCP(new EntityRemovePacket(uuid));
			gui.addToLog("Client [" + connection.getID() + "] disconnected from uuid [" + uuid + "].");
			gui.updateOtherServerInfo();
		}
		else if(o instanceof PlayerCameraRotationPacket packet){
			if(playersByUuid.containsKey(packet.getUuid())) {
				playersByUuid.get(packet.getUuid()).getCameraRotation().set(packet.getCameraRotation());
			}
		}
		else if(o instanceof ClientInputStartPacket packet){
			if(playersByUuid.containsKey(packet.getPlayerUUID()) && !playersByUuid.get(packet.getPlayerUUID()).clientInputs.contains(packet.getInput())){
				playersByUuid.get(packet.getPlayerUUID()).clientInputs.add(packet.getInput());
			}
		}
		else if(o instanceof ClientInputEndPacket packet){
			if(playersByUuid.containsKey(packet.getPlayerUUID())){
				playersByUuid.get(packet.getPlayerUUID()).clientInputs.remove(packet.getInput());
			}
		}
		else if(o instanceof PlayerWorldInteractPacket packet){
			String uuid = packet.getUuid();
			if(playersByUuid.containsKey(uuid)){
				playersByUuid.get(uuid).getCameraRotation().set(packet.getPlayerRotation());
				playersByUuid.get(uuid).worldInteract(packet.getMouseButton(), entities);
			}
		}
		else if(o instanceof PlayerInteractItemPacket packet){
			if(playersByUuid.containsKey(packet.getUuid())){
				playersByUuid.get(packet.getUuid()).getInventory().interactItem(packet.getItemId());
			}
		}
		else if(o instanceof PlayerEquipItemPacket packet){
			if(playersByUuid.containsKey(packet.getUuid())){
				playersByUuid.get(packet.getUuid()).getInventory().equipItem(packet.getItemId(), packet.getTargetSlot());
			}
		}
		else if(o instanceof PlayerUnequipPacket packet){
			if(playersByUuid.containsKey(packet.getUuid())){
				playersByUuid.get(packet.getUuid()).getInventory().addItem(
						playersByUuid.get(packet.getUuid()).getInventory().getEquipment().replaceEquipment(packet.getSlot(), null));
			}
		}
		else if(o instanceof ItemTrashPacket packet){
			if(playersByUuid.containsKey(packet.getUuid())){
				playersByUuid.get(packet.getUuid()).getInventory().removeItem(packet.getItemUuid());
			}
		}
		else if(o instanceof CraftItemPacket packet){
			if(playersByUuid.containsKey(packet.getPlayerUUID()) && playersByUuid.get(packet.getPlayerUUID()).getInventory().getUnlockedRecipes().containsKey(packet.getRecipeUUID()) &&
				playersByUuid.get(packet.getPlayerUUID()).getInventory().getUnlockedRecipes().get(packet.getRecipeUUID()).canCraft(playersByUuid.get(packet.getPlayerUUID()).getInventory())){
				//
				playersByUuid.get(packet.getPlayerUUID()).getInventory().getUnlockedRecipes().get(packet.getRecipeUUID()).Craft(playersByUuid.get(packet.getPlayerUUID()).getInventory());
			}
		}
		else if(o instanceof QuestAcceptPacket packet){
			String uuid = packet.getPlayerUuid();
			if(playersByUuid.containsKey(uuid)){
				playersByUuid.get(uuid).getRPGHandler().questHandler.acceptQuest(packet.isAccept());
			}
		}
		else if(o instanceof QuestRequestCompletePacket packet){
			if(playersByUuid.containsKey(packet.getPlayerUuid())){
				playersByUuid.get(packet.getPlayerUuid()).getRPGHandler().questHandler.completeQuest(packet.getQuestID());
			}
		}
		else if( o instanceof ActiveAbilityActivatePacket packet){
			if(playersByUuid.containsKey(packet.getUuid())){
				playersByUuid.get(packet.getUuid()).getRPGHandler().abilityHandler.invokeActiveAbility(packet.getAbility());
			}
		}
	}

	public void sendPacketToPlayer(String uuid, Object o){
		if(clientIdByUuid.containsKey(uuid)) {
			serverHandler.sendToTCP(clientIdByUuid.get(uuid), o);
		}
	}

	public void sendPacketToAllPlayersUDP(Object o){
		List<Integer> list = new ArrayList<>(playersByClientId.keySet());
		for(int id : list) {
			serverHandler.sendToUDP(id, o);
		}
	}

	public void sendPacketToAllPlayersTCP(Object o){
		List<Integer> list = new ArrayList<>(playersByClientId.keySet());
		for(int id : list) {
			serverHandler.sendToTCP(id, o);
		}
	}

	public void sendPacketToAllPlayersExclude(String uuid, Object o){
		for(String id : clientIdByUuid.keySet()){
			if(!id.equals(uuid))
				sendPacketToPlayer(id,o);
		}
	}

	public void sendPacketToClient(int id, Object o){
		serverHandler.sendToTCP(id, o);
	}

	public ServerPlayer loadPlayer(String uuid){
		return playerSerializer.deserialize("save/players/" + uuid + ".json");
	}

	public void savePlayer(String uuid){
		try{
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			File file = new File("save/players/" + uuid + ".json");
			file.createNewFile();
			FileWriter writer = new FileWriter(file);
			writer.write(gson.toJson(playerSerializer.serialize(playersByUuid.get(uuid))));
			writer.flush();
			writer.close();
		}
		catch (Exception e){
			System.out.println("Failed to save player ["+uuid+"].");
			e.printStackTrace();
		}
	}

	public UserdataList loadUserPasswords(){
		try{
			Gson gson = new Gson();
			UserdataList lst = gson.fromJson(new FileReader("save/userdata.json"), UserdataList.class);
			return lst;
		}
		catch (Exception ignored){
			return new UserdataList();
		}
	}
	public void saveUserPasswords(UserdataList userPasswords){
		try{
			Gson gson = new Gson();
			File file = new File("save/userdata.json");
			file.createNewFile();
			FileWriter writer = new FileWriter(file);
			writer.write(gson.toJson(userPasswords));
			writer.flush();
			writer.close();
		}
		catch (Exception e){
			System.out.println("Failed to save user passwords.");
			e.printStackTrace();
		}
	}

	public void close(){
		isRunning = false;
	}

	public int getTotalPlayers(){
		return playersByUuid.size();
	}
	public int getConnections(){
		return serverHandler.getConnections().length;
	}
	public List<ServerPlayer> getOnlinePlayers() {return new ArrayList<>(playersByUuid.values());}
}
