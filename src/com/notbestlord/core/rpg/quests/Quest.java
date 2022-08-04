package com.notbestlord.core.rpg.quests;

import com.notbestlord.core.entity.EntityType;
import com.notbestlord.core.inventory.ItemManager;
import com.notbestlord.core.rpg.skills.SubSkillType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Quest {
	none(null),
	blacksmith_john_1(new FetchQuest(
			QuestID.blacksmith_john_1,
			new ArrayList<>(List.of(ItemManager.getItem("shard_yellow_0", 5))),
			"Get john 5 Yellow Crystals.",
			player -> player.getRPGHandler().skillHandler.incSubSkillExp(SubSkillType.craftsmanship_ore_processing, 100))),
	blacksmith_john_0(new SlayQuest(QuestID.blacksmith_john_0, "Kill 2 slimes.", new HashMap<>(Map.of(EntityType.monster_slime, 2)),
			player -> player.getInventory().incCoins(250), blacksmith_john_1));

	private final IQuest quest;

	Quest(IQuest quest) {
		this.quest = quest;
	}

	public IQuest getQuest() {
		return quest;
	}
}
