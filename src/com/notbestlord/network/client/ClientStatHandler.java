package com.notbestlord.network.client;

import com.notbestlord.core.rpg.stats.Stat;
import com.notbestlord.core.rpg.stats.StatusEffectType;

import java.util.HashMap;
import java.util.Map;

public class ClientStatHandler {
	public final Map<StatusEffectType, Float> effects = new HashMap<>();
	public final Map<Stat, Float> stats = new HashMap<>();
}
