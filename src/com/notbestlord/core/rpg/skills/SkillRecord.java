package com.notbestlord.core.rpg.skills;

import java.util.Map;
import java.util.Objects;

public class SkillRecord {
	private Map<SkillType, Skill> skills;
	private Map<SubSkillType, SubSkill> subSkills;

	public SkillRecord() {}
	public SkillRecord(SkillHandler skillHandler) {
		this.skills = skillHandler.skillList();
		this.subSkills = skillHandler.subSkillList();
	}
	public SkillRecord(Map<SkillType, Skill> skills, Map<SubSkillType, SubSkill> subSkills) {
		this.skills = skills;
		this.subSkills = subSkills;
	}

	public Map<SkillType, Skill> skills() {
		return skills;
	}

	public Map<SubSkillType, SubSkill> subSkills() {
		return subSkills;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (SkillRecord) obj;
		return Objects.equals(this.skills, that.skills) &&
				Objects.equals(this.subSkills, that.subSkills);
	}

	@Override
	public int hashCode() {
		return Objects.hash(skills, subSkills);
	}

	@Override
	public String toString() {
		return "SkillRecord[" +
				"skills=" + skills + ", " +
				"subSkills=" + subSkills + ']';
	}
}
