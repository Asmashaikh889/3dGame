package com.notbestlord.core.rpg.abilites;

import com.notbestlord.core.rpg.IRPGEntity;
import com.notbestlord.core.rpg.RPGUtils;

import java.util.Objects;

public class ActiveAbility implements IAbility {
	private String id;
	private String displayName;
	private String start;
	private float duration;
	private String end;
	private float cooldown;
	private String description;
	private String textureID;

	public ActiveAbility() {}

	public ActiveAbility(String id, String displayName, String start, float duration,
	                     String end, float cooldown, String description, String textureID) {
		this.id = id;
		this.displayName = displayName;
		this.start = start;
		this.duration = duration;
		this.end = end;
		this.cooldown = cooldown;
		this.description = description;
		this.textureID = textureID;
	}

	@Override
	public boolean start(IRPGEntity entity) {
		if (start != null) return RPGUtils.triggerEvent(entity, start);
		return false;
	}

	@Override
	public void end(IRPGEntity entity) {
		if (start != null) RPGUtils.applyEventEffect(entity, end);
	}

	public float getDuration() {
		return duration;
	}

	public float duration() {
		return duration;
	}

	public float cooldown() {
		return cooldown;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getId() {
		return id;
	}

	public String description() {
		return description;
	}

	public String textureID() {
		return textureID;
	}
	@Override
	public int hashCode() {
		return Objects.hash(start, duration, end, cooldown, description, textureID);
	}

	@Override
	public String toString() {
		return "ActiveAbilityRecord[" +
				"start=" + start + ", " +
				"duration=" + duration + ", " +
				"end=" + end + ", " +
				"cooldown=" + cooldown + ", " +
				"description=" + description + ", " +
				"textureID=" + textureID + ']';
	}

}
