package com.notbestlord.core.utils;

import java.util.HashMap;
import java.util.Map;

public class Flags {
	private final Map<String, Integer> flags = new HashMap<>();

	public Flags() {}

	public int getFlag(String str){
		return flags.containsKey(str) ? flags.get(str) : -1;
	}

	public void setFlag(String str, int n){
		flags.put(str, n);
	}

	public void initFlag(String str){
		flags.put(str, 0);
	}

}
