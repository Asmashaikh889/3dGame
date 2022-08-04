package com.notbestlord.core.rpg.npcs;

import com.notbestlord.core.rpg.quests.IQuest;

import java.util.List;

public class NpcDialogue {
	private List<String> dialogue;
	private IQuest quest;

	public NpcDialogue(List<String> dialogue, IQuest quest) {
		this.dialogue = dialogue;
		this.quest = quest;
	}

	public NpcDialogue(List<String> dialogue) {
		this.dialogue = dialogue;
	}

	public List<String> getDialogue() {
		return dialogue;
	}

	public IQuest getQuest() {
		return quest;
	}
}
