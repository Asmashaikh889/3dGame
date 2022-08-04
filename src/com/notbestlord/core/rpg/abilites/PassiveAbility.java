package com.notbestlord.core.rpg.abilites;

import com.notbestlord.core.rpg.IRPGEntity;
import com.notbestlord.core.rpg.RPGUtils;

import java.util.Objects;

public class PassiveAbility implements IAbility {
	private String id;
	private String displayName;
	private String start;
	private String end;
	private String description;

	public PassiveAbility() {}

	public PassiveAbility(String id, String displayName, String start, String end, String description) {
		this.id = id;
		this.displayName = displayName;
		this.start = start;
		this.end = end;
		this.description = description;
	}

	@Override
	public boolean start(IRPGEntity entity) {
		if (start != null) RPGUtils.applyEventEffect(entity, start);
		return true;
	}

	@Override
	public void end(IRPGEntity entity) {
		if (end != null) RPGUtils.applyEventEffect(entity, end);
	}


	@Override
	public int hashCode() {
		return Objects.hash(start, end);
	}

	@Override
	public String toString() {
		return "PassiveAbility{" +
				"id='" + id + '\'' +
				", displayName='" + displayName + '\'' +
				", start='" + start + '\'' +
				", end='" + end + '\'' +
				", description='" + description + '\'' +
				'}';
	}

	public String description() {
		return description;
	}

	public String getId() {
		return id;
	}

	public String getDisplayName() {
		return displayName;
	}
}
