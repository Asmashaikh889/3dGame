package com.notbestlord.core.rpg.abilites;

import com.notbestlord.core.rpg.IRPGEntity;

public interface IAbility {

	boolean start(IRPGEntity entity);

	void end(IRPGEntity entity);
}
