package com.notbestlord.core.rpg.npcs;

import com.notbestlord.core.entity.Player;
import com.notbestlord.core.utils.Consts;

import java.util.ArrayList;
import java.util.List;

public class NpcConversingHandler {
	private List<String> currentDialogue = new ArrayList<>();
	private NPC_ID currentNpcID = NPC_ID.none;
	private float dialogueCD = 0;
	private Player owner;

	public NpcConversingHandler(Player owner){
		this.owner = owner;
	}

	public void frameUpdate(){
		if(currentNpcID == NPC_ID.none) return;
		if(dialogueCD <= 0){
			owner.addToLog(currentDialogue.get(0));
			currentDialogue.remove(0);
			if(currentDialogue.size() == 0){
				currentNpcID = NPC_ID.none;
				return;
			}
			dialogueCD = 1.2f;
		}
		dialogueCD -= Consts.deltaTime;
	}

	public void setCurrentDialogue(NPC_ID id, List<String> currentDialogue){
		if(currentNpcID == id) return;
		this.currentDialogue.clear();
		this.currentDialogue.addAll(currentDialogue);
		currentNpcID = id;
	}
}
