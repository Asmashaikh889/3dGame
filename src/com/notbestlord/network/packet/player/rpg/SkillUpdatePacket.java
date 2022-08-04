package com.notbestlord.network.packet.player.rpg;

import com.notbestlord.core.rpg.skills.Skill;
import com.notbestlord.core.rpg.skills.SubSkill;

public class SkillUpdatePacket {
	private Skill skill;

	public SkillUpdatePacket() {}

	public SkillUpdatePacket(Skill skill) {
		this.skill = skill;
	}

	public Skill getSkill() {
		return skill;
	}
}
