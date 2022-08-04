package com.notbestlord.core.audio;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;

import static org.lwjgl.openal.ALC10.*;

public class AudioManager {

	private static final Map<String, Sound> buffers = new HashMap<>();

	public void init() {
		registerAudio();
	}

	private void registerAudio(){
		List<String> audioFiles = new ArrayList<>();
		addFilesFromDir(audioFiles, new File("assets/audio"));
		audioFiles.forEach(file -> {
			try {
				loadSound(file);
			} catch (FileNotFoundException e) {}
		});
	}

	private static void loadSound(String file) throws FileNotFoundException {
		buffers.put(file.replace("assets\\audio\\","").replace("\\","_").replace(".ogg",""), new Sound(file, false));
	}

	private static void addFilesFromDir(List<String> lst, File dir){
		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				addFilesFromDir(lst, file);
			} else {
				lst.add(file.getPath());
			}
		}
	}

	public static Sound getSound(String s){
		return buffers.getOrDefault(s, null);
	}

	public void cleanup(){
		for(Sound s : buffers.values()){
			s.delete();
		}
	}

}
