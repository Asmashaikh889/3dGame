package com.notbestlord.core.utils;

import com.notbestlord.core.Config;
import org.lwjgl.glfw.GLFW;

import java.util.*;

public class Controls {
    public static final List<String> ServerKeyNames = List.of("forward","backward","left","right","jump");
    public static final List<String> ClientKeyNames = List.of("inventory","pause_menu","quest_menu","ability_menu","ability_1","ability_2","ability_3","ability_4","ability_5");

    public Map<String, Integer> controlKeys;

    public Controls(Map<String, Integer> controlKeys) {
        this.controlKeys = controlKeys;
    }

    public static Map<String, Integer> defaultControls(){
        Map<String, Integer> map = new HashMap<>();
        map.put("forward", GLFW.GLFW_KEY_W);
        map.put("left", GLFW.GLFW_KEY_A);
        map.put("backward", GLFW.GLFW_KEY_S);
        map.put("right", GLFW.GLFW_KEY_D);
        map.put("jump", GLFW.GLFW_KEY_SPACE);
        map.put("inventory", GLFW.GLFW_KEY_I);
        map.put("pause_menu", GLFW.GLFW_KEY_TAB);
        map.put("ability_1", GLFW.GLFW_KEY_1);
        map.put("ability_2", GLFW.GLFW_KEY_2);
        map.put("ability_3", GLFW.GLFW_KEY_3);
        map.put("ability_4", GLFW.GLFW_KEY_4);
        map.put("ability_5", GLFW.GLFW_KEY_5);
        map.put("quest_menu", GLFW.GLFW_KEY_K);
        map.put("ability_menu", GLFW.GLFW_KEY_J);
        return map;
    }

    public static String getKeyName(int keyCode){
        if(GLFW.glfwGetKeyName(keyCode,keyCode) == null){
            return Config.keyCodeStringMapping.getOrDefault(keyCode, "null");
        }
        return GLFW.glfwGetKeyName(keyCode,keyCode);
    }



    public int getKey(String key){
        if(controlKeys.containsKey(key))
            return controlKeys.get(key);
        if(defaultControls().containsKey(key)){
            controlKeys.put(key, defaultControls().get(key));
            return controlKeys.get(key);
        }
        return 0;
    }
}
