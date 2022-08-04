package com.notbestlord.network.packet.player.rpg;

import com.notbestlord.core.rpg.skills.Skill;
import com.notbestlord.core.rpg.skills.SubSkill;

public class SubSkillUpdatePacket {
	private SubSkill subSkill;

	public SubSkillUpdatePacket() {}

	public SubSkillUpdatePacket(SubSkill subSkill) {
		this.subSkill = subSkill;
	}

	public SubSkill getSubSkill() {
		return subSkill;
	}
}
