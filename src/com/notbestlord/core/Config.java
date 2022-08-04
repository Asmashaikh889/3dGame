package com.notbestlord.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.notbestlord.core.utils.Controls;
import org.lwjgl.glfw.GLFW;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class Config {
    public static Map<Integer, String> keyCodeStringMapping = generateKeyMapping();
    private static ConfigSaveData configData = loadConfig();

    private static class ConfigSaveData{
        Controls controls;
        String serverIp;
        public ConfigSaveData(Controls controls, String serverIp) {
            this.controls = controls;
            this.serverIp = serverIp;
        }
    }

    private static Map<Integer, String> generateKeyMapping(){
        Map<Integer, String> out = new HashMap<>();
        out.put(GLFW.GLFW_KEY_ESCAPE, "Escape");
        out.put(GLFW.GLFW_KEY_SPACE, "Space");
        out.put(GLFW.GLFW_KEY_TAB, "Tab");
        out.put(GLFW.GLFW_KEY_LEFT_CONTROL, "L Ctrl");
        out.put(GLFW.GLFW_KEY_RIGHT_CONTROL, "R Ctrl");
        out.put(GLFW.GLFW_KEY_LEFT_SHIFT, "L Shift");
        out.put(GLFW.GLFW_KEY_RIGHT_SHIFT, "R Shift");
        return out;
    }

    public static Controls playerControls = configData.controls;
    public static String serverIp = configData.serverIp;

    private static ConfigSaveData loadConfig(){
        Gson gson = new Gson();
        try{
            FileReader reader = new FileReader("data/config.json");
            ConfigSaveData out = gson.fromJson(reader, ConfigSaveData.class);
            reader.close();
            if(out != null && out.controls != null) {
                return out;
            }
        }
        catch (Exception ignored) {}
        return new ConfigSaveData(new Controls(Controls.defaultControls()),"localhost");
    }


    public static void saveConfig(){
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileWriter fileWriter = new FileWriter("data/config.json");
            fileWriter.write(gson.toJson(new ConfigSaveData(playerControls, serverIp)));
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception ignored) {}
    }
}
