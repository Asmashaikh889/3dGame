package com.notbestlord.network.server.entity;

import com.notbestlord.ServerLauncher;
import com.notbestlord.core.physics.Collider;
import com.notbestlord.core.physics.BoundingBox3f;
import com.notbestlord.core.rpg.npcs.NPC_ID;
import com.notbestlord.core.rpg.npcs.NpcDialogue;
import com.notbestlord.core.rpg.quests.IQuest;
import com.notbestlord.core.utils.Range;
import com.notbestlord.network.data.MouseButton;
import com.notbestlord.network.packet.player.PlayerConverseNpcPacket;
import org.joml.Vector3f;

import java.util.*;

public class ServerNPCEntity extends ServerInteractEntity{
	private final Map<String, List<NpcDialogue>> NPCDialogueByContext = new HashMap<>();
	private final NPC_ID npc_id;
	private final Collider collider;

	public ServerNPCEntity(String uuid, Vector3f position, Vector3f rotation, float scale, String modelID, String textureID, NPC_ID id) {
		super(uuid, position, rotation, scale, modelID, textureID);
		collider = new Collider(new BoundingBox3f(new Vector3f(-scale,-scale,-scale), new Vector3f(scale, scale, scale)),1,1,1);
		npc_id = id;
	}

	public void InteractWContext(ServerPlayer player, String context){
		if(NPCDialogueByContext.containsKey(context)){
			// send context specific dialogue to player
			NpcDialogue dialogue = getNpcDialogue(context);
			ServerLauncher.getGameServer().sendPacketToPlayer(player.getUuid(), new PlayerConverseNpcPacket(npc_id, dialogue.getDialogue()));
			player.getRPGHandler().questHandler.setCurrentAcceptingQuest(dialogue.getQuest());
		}
	}

	@Override
	public void Interact(ServerPlayer player, MouseButton button) {
		// send default npc dialogue to player
		if(button == MouseButton.right && !player.getRPGHandler().questHandler.updateConverseQuests(this)){
			NpcDialogue dialogue = getNpcDialogue("none");
			ServerLauncher.getGameServer().sendPacketToPlayer(player.getUuid(), new PlayerConverseNpcPacket(npc_id, dialogue.getDialogue()));
			player.getRPGHandler().questHandler.setCurrentAcceptingQuest(dialogue.getQuest());
		}
	}

	@Override
	public void frameUpdate() {
		super.frameUpdate();
		//
		ServerLauncher.getGameServer().getPhysics().setPostCollisionPos(this, getPosition(), collider, 0);
	}
	@Override
	public Collider getCollider() {
		return collider;
	}

	@Override
	public boolean isDead() {
		return false;
	}

	public NPC_ID getNpc_id() {
		return npc_id;
	}

	public void addDialogue(String context, List<String> dialogue){
		if(!NPCDialogueByContext.containsKey(context)){
			NPCDialogueByContext.put(context, new ArrayList<>());
		}
		NPCDialogueByContext.get(context).add(new NpcDialogue(dialogue));
	}

	public void addDialogue(String context, List<String> dialogue, IQuest quest){
		if(!NPCDialogueByContext.containsKey(context)){
			NPCDialogueByContext.put(context, new ArrayList<>());
		}
		NPCDialogueByContext.get(context).add(new NpcDialogue(dialogue, quest));
	}

	public NpcDialogue getNpcDialogue(String context){
		return NPCDialogueByContext.get(context).get(Range.randomInt(0,NPCDialogueByContext.get(context).size()));
	}
}
