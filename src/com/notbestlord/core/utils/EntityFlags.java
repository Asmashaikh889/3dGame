package com.notbestlord.core.utils;

import java.util.HashMap;
import java.util.Map;

public class EntityFlags extends Flags{
	private final Map<String, Float> cooldowns = new HashMap<>();

	public EntityFlags() {}

	public void frameUpdate() {
		for(String key : cooldowns.keySet()){
			if(cooldowns.get(key) > 0)
				cooldowns.put(key, cooldowns.get(key)-Consts.deltaTime);
		}
	}

	public float getCooldown(String str){
		return cooldowns.containsKey(str) ? cooldowns.get(str) : -1;
	}

	public void setCooldown(String str, float n){
		cooldowns.put(str, n);
	}

	public void initCooldown(String str){
		cooldowns.put(str, 0f);
	}
}
