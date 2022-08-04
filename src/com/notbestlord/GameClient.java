package com.notbestlord;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.notbestlord.core.*;
import com.notbestlord.core.audio.AudioManager;
import com.notbestlord.core.audio.Sound;
import com.notbestlord.core.entity.*;
import com.notbestlord.core.entity.Terrain;
import com.notbestlord.core.gui.PlayerAbilityGui;
import com.notbestlord.core.gui.PlayerLoginMenu;
import com.notbestlord.core.gui.PlayerQuestGui;
import com.notbestlord.core.inventory.ItemInformation;
import com.notbestlord.core.inventory.ItemManager;
import com.notbestlord.core.inventory.recipe.IRecipe;
import com.notbestlord.core.lighting.DirectionalLight;
import com.notbestlord.core.rendering.RenderManager;
import com.notbestlord.core.utils.Consts;
import com.notbestlord.core.utils.Controls;
import com.notbestlord.network.client.ItemsRegisterPacket;
import com.notbestlord.network.data.ClientLoginReplyType;
import com.notbestlord.network.data.MouseButton;
import com.notbestlord.network.data.TerrainData;
import com.notbestlord.network.packet.client.ItemInfoListPacket;
import com.notbestlord.network.packet.client.*;
import com.notbestlord.network.packet.entity.EntityAddPacket;
import com.notbestlord.network.packet.entity.EntityMovementPacket;
import com.notbestlord.network.packet.entity.EntityRemovePacket;
import com.notbestlord.network.packet.entity.EntityRotatePacket;
import com.notbestlord.network.packet.player.LogMessagePacket;
import com.notbestlord.network.packet.player.PlayerCameraRotationPacket;
import com.notbestlord.network.packet.player.PlayerConverseNpcPacket;
import com.notbestlord.network.packet.player.PlayerWorldInteractPacket;
import com.notbestlord.network.packet.player.inventory.AddRecipePacket;
import com.notbestlord.network.packet.player.inventory.PlayerInventoryUpdatePacket;
import com.notbestlord.network.packet.player.inventory.RemoveRecipePacket;
import com.notbestlord.network.packet.player.rpg.*;
import com.notbestlord.network.server.entity.ServerEntity;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameClient extends Listener implements ILogic {
	private static final int tcpPort = 25575, udpPort = 25576;
	private Client clientHandler;
	private final RenderManager renderer;
	private final ObjectLoader loader;
	private final WindowManager window;

	private final PlayerLoginMenu playerLoginMenu = new PlayerLoginMenu();
	public static String uuid = "";
	private boolean connected = false;

	private final Map<String, Entity> entities = new HashMap<>();
	private final List<Terrain> terrains = new ArrayList<>();
	private Player player;
	private PlayerQuestGui questGui;
	private final PlayerAbilityGui abilityGui = new PlayerAbilityGui();


	private final Map<String, Float> inputCooldowns = new HashMap<>();
	private final List<String> pressedKeys = new ArrayList<>();

	private final List<TerrainData> terrainDataToLoad = new ArrayList<>();
	private final List<ServerEntity> entitiesToAdd = new ArrayList<>();

	private DirectionalLight directionalLight;


	public GameClient() {
		//
		renderer = new RenderManager();
		window = ClientLauncher.getWindow();
		loader = new ObjectLoader();
		//
		inputCooldowns.put("inventory", 0f);
		inputCooldowns.put("pause_menu", 0f);
		inputCooldowns.put("quest_menu", 0f);
		inputCooldowns.put("ability_menu", 0f);
		inputCooldowns.put("left_click", 0f);
		inputCooldowns.put("right_click", 0f);
		inputCooldowns.put("ability_1", 0f);
		inputCooldowns.put("ability_2", 0f);
		inputCooldowns.put("ability_3", 0f);
		inputCooldowns.put("ability_4", 0f);
		inputCooldowns.put("ability_5", 0f);
	}

	@Override
	public void init() throws Exception {
		loader.registerTextures();
		renderer.init(loader);
		//
		directionalLight = new DirectionalLight(new Vector3f(0,0,0), new Vector3f(0,-90,0), 1f);
		//
		clientHandler = new Client(8192,8192);

		clientHandler.getKryo().setRegistrationRequired(false);

		clientHandler.addListener(this);
		//
		RawModel cubeBlueModel = loader.getModel("cube", "red");

		player = new Player(new Entity(cubeBlueModel, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 1), new Camera());
		player.getInventory().gui.setAbilityGui(abilityGui);
		entities.put(uuid, player);
		questGui = new PlayerQuestGui();
		//
		clientHandler.start();

		clientHandler.connect(5000, Config.serverIp,tcpPort,udpPort);
	}

	@Override
	public void input() {
		if(!connected) return;
		Controls playerControls = Config.playerControls;
		//handle server-side input
		for(String keyName : Controls.ServerKeyNames){
			if(window.isKeyPressed(playerControls.getKey(keyName))){
				if(!pressedKeys.contains(keyName)) {
					//send packet
					ClientInputStartPacket packet = new ClientInputStartPacket(uuid, keyName);
					sendToServerTcp(packet);
					pressedKeys.add(keyName);
				}
			}
			else if (pressedKeys.contains(keyName)){
				ClientInputEndPacket packet = new ClientInputEndPacket(uuid, keyName);
				sendToServerTcp(packet);
				pressedKeys.remove(keyName);
			}
		}
		//handle client-side input
		for(String keyName : Controls.ClientKeyNames){
			if(window.isGuiKeyPressed(playerControls.getKey(keyName))){
				handleInput(keyName);
			}
		}
	}

	private void handleInput(String keyName){
		if(keyName.equals("inventory") && inputCooldowns.get("inventory") <= 0){
			player.getInventory().openInventory();
			inputCooldowns.put("inventory", 0.25f);
		}
		else if(keyName.equals("pause_menu") && inputCooldowns.get("pause_menu") <= 0){
			player.openPauseMenu();
			inputCooldowns.put("pause_menu", 0.25f);
		}
		else if(keyName.equals("quest_menu") && inputCooldowns.get("quest_menu") <= 0){
			questGui.openQuestMenu();
			inputCooldowns.put("quest_menu", 0.25f);
		}
		else if(keyName.equals("ability_menu") && inputCooldowns.get("ability_menu") <= 0){
			abilityGui.open();
			inputCooldowns.put("ability_menu", 0.25f);
		}
		else if(keyName.contains("ability") && inputCooldowns.containsKey(keyName) && inputCooldowns.get(keyName) <= 0) {
			try {
				abilityGui.activate(Integer.parseInt(String.valueOf(keyName.charAt(keyName.length() - 1))));
				inputCooldowns.put(keyName, 0.5f);
			}
			catch (NumberFormatException ignored) {}
		}
	}

	@Override
	public void update(MouseInput mouseInput) {
		// load terrains
		for(String key : inputCooldowns.keySet()){
			if(inputCooldowns.get(key) > 0) inputCooldowns.put(key, inputCooldowns.get(key) - Consts.deltaTime);
		}
		if(!terrainDataToLoad.isEmpty()){
			for(TerrainData data : terrainDataToLoad){
				Material material = new Material();
				material.setTexture(new Texture(ObjectLoader.getTexture(data.textureID())));
				terrains.add(new Terrain(data.size(), data.position(), loader, material));
			}
			terrainDataToLoad.clear();
		}
		// load non-loaded entities
		if(!entitiesToAdd.isEmpty()){
			List<ServerEntity> entityList = new ArrayList<>(entitiesToAdd);
			for (ServerEntity entity : entityList) {
				if (entity.getUuid().equals(uuid) || entities.containsKey(entity.getUuid())) continue;
				RawModel entityModel = new RawModel(loader.getModel(entity.getModelID(), entity.getTextureID()));
				Entity entity1 = new Entity(entityModel, entity.getPosition(), entity.getRotation(), entity.getScale());
				entities.put(entity.getUuid(), entity1);
				entitiesToAdd.remove(entity);
			}
		}
		//
		if(player != null){
			// update camera location
			abilityGui.frameUpdate();
			player.updatePosition();
			player.frameUpdate();
			// camera rotation
			if(!ClientLauncher.getWindow().isUiOpen()){
				Vector2f rotVec = mouseInput.getDisplayVec();
				player.moveRotation(rotVec.x * Consts.MOUSE_SENSITIVITY, rotVec.y * Consts.MOUSE_SENSITIVITY);
				sendToServerTcp(new PlayerCameraRotationPacket(uuid, player.getCamera().getRotation()));
			}
			Sound.setListener(player.getPos(), player.getCamera());
		}

		if(mouseInput.isLeftButtonPress() && inputCooldowns.get("left_click") <= 0){
			sendToServerTcp(new PlayerWorldInteractPacket(uuid, player.getCamera().getRotation(), MouseButton.left));
			/*renderer.generateParticle(new Particle(ParticleTexture.fire, player.getPos(),
					new Vector3f(), 1,1f,0,1f));*/
			inputCooldowns.put("left_click", 0.25f);
		}

		if(mouseInput.isRightButtonPress() && inputCooldowns.get("right_click") <= 0){
			sendToServerTcp(new PlayerWorldInteractPacket(uuid, player.getCamera().getRotation(), MouseButton.right));
			inputCooldowns.put("right_click", 0.25f);
		}

		// processing terrain models
		for(Terrain terrain : terrains){
			renderer.processTerrain(terrain);
		}

		// processing entity models
		List<Entity> tEntities = new ArrayList<>(entities.values());
		for(Entity entity : tEntities){
			renderer.processEntities(entity);
		}

	}

	@Override
	public void render() {
		if(window.isResize()){
			GL11.glViewport(0,0,window.getWidth(),window.getHeight());
			window.setResize(true);
		}
		renderer.render(player.getCamera(), directionalLight, null, null);
	}

	@Override
	public void cleanup() {
		clientHandler.close();
		renderer.cleanup();
		loader.cleanup();
	}

	public void sendToServerTcp(Object o){
		clientHandler.sendTCP(o);
	}

	public void sendToServerUdp(Object o){
		clientHandler.sendUDP(o);
	}


	@Override
	public void connected(Connection connection) {
		System.out.println("Connected to server");
		ClientLauncher.getWindow().getGuiManager().toggleInteractGui(playerLoginMenu);
	}

	@Override
	public void disconnected(Connection connection) {
		GLFW.glfwSetWindowShouldClose(ClientLauncher.getWindow().getWindowHandler(), true);
	}

	@Override
	public void received(Connection connection, Object o) {
		if(o instanceof ClientConnectPacket packet){
			playerLoginMenu.currentLoginReply = packet.getReply();
			if(packet.getReply() != ClientLoginReplyType.logged_in){
				return;
			}
			uuid = packet.getUuid();
			connected = true;
			ClientLauncher.getWindow().getGuiManager().removeInteractGui(playerLoginMenu);
			player.startGui();
		}
		else if(o instanceof TerrainPacket){
			terrainDataToLoad.addAll(((TerrainPacket) o).terrains());
		}
		else if(o instanceof SoundPacket packet){
			if(AudioManager.getSound(packet.getSoundId()) != null){
				AudioManager.getSound(packet.getSoundId()).play(packet.getLocation());
			}
		}
		else if(o instanceof EntityMovementPacket movementPacket){
			if(movementPacket.uuid().equals(uuid)){
				player.updatePosition(movementPacket.position());
			}
			else if(entities.containsKey(movementPacket.uuid())){
				entities.get(movementPacket.uuid()).setPos(movementPacket.position());
			}
		}
		else if(o instanceof EntityRotatePacket rotatePacket){
			if(entities.containsKey(rotatePacket.getUuid())){
				entities.get(rotatePacket.getUuid()).setPos(rotatePacket.getRotation());
			}
		}
		else if(o instanceof EntityAddPacket){
			entitiesToAdd.add(((EntityAddPacket) o).getEntity());
		}
		else if(o instanceof EntityRemovePacket){
			entities.remove(((EntityRemovePacket) o).uuid());
		}
		else if(o instanceof ItemInfoListPacket packet){
			ItemInformation.itemInformationByItemType = packet.getItemInformationByItemType();
		}
		else if(o instanceof ItemsRegisterPacket packet) {
			ItemManager.Items = packet.getItems();
			ItemManager.itemTypes = packet.getItemTypes();
		}
		else if(o instanceof PlayerInventoryUpdatePacket){
			player.getInventory().update((PlayerInventoryUpdatePacket) o);
		}
		else if(o instanceof StatUpdatePacket){
			player.getInventory().gui.statHandler.stats.put(((StatUpdatePacket) o).getStat(),((StatUpdatePacket) o).getN());
		}
		else if(o instanceof StatusEffectUpdatePacket packet){
			if(packet.getDuration() == 0 && player.getInventory().gui.statHandler.effects.containsKey(packet.getType())){
				player.getInventory().gui.statHandler.effects.remove(packet.getType());
			}
			else if(packet.getDuration() != 0){
				player.getInventory().gui.statHandler.effects.put(packet.getType(), packet.getDuration());
			}
		}
		else if(o instanceof SkillUpdatePacket packet) {
			player.getInventory().gui.skills.skills().put(packet.getSkill().getSkillType(), packet.getSkill());
		}
		else if(o instanceof SubSkillUpdatePacket packet){
			player.getInventory().gui.skills.subSkills().put(packet.getSubSkill().getSkillType(), packet.getSubSkill());
		}
		else if(o instanceof AddRecipePacket){
			IRecipe recipe = ((AddRecipePacket) o).getRecipe();
			player.getInventory().gui.unlockedRecipes.put(recipe.getUuid(), recipe);
		}
		else if(o instanceof RemoveRecipePacket){
			String uuid = ((RemoveRecipePacket) o).getRecipeUUID();
			player.getInventory().gui.unlockedRecipes.remove(uuid);
		}
		else if(o instanceof PlayerConverseNpcPacket packet){
			player.setCurrentNPCDialogue(packet.getNpc_id(), packet.getDialogue());
		}
		else if(o instanceof QuestRequestAcceptPacket packet){
			questGui.setAcceptRequestQuest(packet.getQuestName(), packet.getRawDescription());
		}
		else if(o instanceof QuestStartPacket packet){
			questGui.addQuest(packet.getQuestID(), packet.getDescription(), packet.getProgressPercent());
		}
		else if(o instanceof QuestProgressUpdatePacket packet){
			questGui.updateQuest(packet.getQuestID(), packet.getDescription(), packet.getProgressPercent());
		}
		else if(o instanceof QuestCompletePacket){
			questGui.completeQuest(((QuestCompletePacket) o).getQuestID());
		}
		else if(o instanceof LogMessagePacket){
			player.addToLog(((LogMessagePacket) o).getMsg());
		}
		else if(o instanceof UpdateActiveAbilityStatePacket packet){
			if(packet.isRemoved()){
				abilityGui.activeAbilities.remove(packet.getAbility());
			}
			else if(!abilityGui.activeAbilities.contains(packet.getAbility())){
				abilityGui.activeAbilities.add(packet.getAbility());
			}
		}
		else if(o instanceof UpdatePassiveAbilityStatePacket packet){
			if(packet.isRemoved()){
				abilityGui.passiveAbilities.remove(packet.getAbility());
			}
			else if(!abilityGui.passiveAbilities.contains(packet.getAbility())){
				abilityGui.passiveAbilities.add(packet.getAbility());
			}
		}
		else if(o instanceof ActiveAbilityActivatePacket packet){
			abilityGui.setActive(packet.getAbility());
		}
		else if(o instanceof ActiveAbilityCooldownEndPacket packet){
			abilityGui.cooldownActiveAbilities.remove(packet.getAbility());
			abilityGui.usedAbilities--;
		}
	}

	public void disconnectFromAccount(){
		entities.clear();
		terrains.clear();
		connected = false;
		uuid = "";
		//
		ClientLauncher.getWindow().getGuiManager().toggleInteractGui(playerLoginMenu);
		ClientLauncher.getWindow().getGuiManager().removeInteractGui(player.getInventory().gui);
		ClientLauncher.getWindow().getGuiManager().removeInteractGui(questGui);
		player.getInventory().gui.unlockedRecipes.clear();
		player.getInventory().gui.statHandler.effects.clear();
		player.endGui();
		player.closePauseMenu();
		//
		abilityGui.activatedActiveAbilities.clear();
		abilityGui.cooldownActiveAbilities.clear();
		abilityGui.usedAbilities = 0;
		abilityGui.activeAbilities.clear();
		abilityGui.passiveAbilities.clear();
		//
		questGui.clearQuests();
		//
		sendToServerTcp(new ClientDisconnectPacket());
	}
}
