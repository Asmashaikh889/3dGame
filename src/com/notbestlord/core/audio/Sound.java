package com.notbestlord.core.audio;

import com.notbestlord.core.Camera;
import com.notbestlord.core.utils.Transformation;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_filename;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.libc.LibCStdlib.free;

public class Sound {

	private int bufferId;
	private int sourceId;
	private String filepath;

	private boolean isPlaying = false;

	public Sound(String filepath, boolean loops) {
		this.filepath = filepath;

		// Allocate space to store the return information from stb
		stackPush();
		IntBuffer channelsBuffer = stackMallocInt(1);
		stackPush();
		IntBuffer sampleRateBuffer = stackMallocInt(1);

		ShortBuffer rawAudioBuffer = stb_vorbis_decode_filename(filepath, channelsBuffer, sampleRateBuffer);
		if (rawAudioBuffer == null) {
			System.err.println("Could not load sound '" + filepath + "'");
			stackPop();
			stackPop();
			return;
		}

		// Retrieve the extra information that was stored in the buffers by stb
		int channels = channelsBuffer.get();
		int sampleRate = sampleRateBuffer.get();
		// Free
		stackPop();
		stackPop();

		// Find the correct openAL format
		int format = -1;
		if (channels == 1) {
			format = AL_FORMAT_MONO16;
		} else if (channels == 2) {
			format = AL_FORMAT_STEREO16;
		}

		bufferId = alGenBuffers();
		alBufferData(bufferId, format, rawAudioBuffer, sampleRate);

		// Generate the source
		sourceId = alGenSources();

		alSourcei(sourceId, AL_BUFFER, bufferId);
		alSourcei(sourceId, AL_LOOPING, loops ? 1 : 0);
		alSourcei(sourceId, AL_POSITION, 0);
		alSourcef(sourceId, AL_GAIN, 0.3f);

		// Free stb raw audio buffer
		free(rawAudioBuffer);
	}

	public void delete() {
		alDeleteSources(sourceId);
		alDeleteBuffers(bufferId);
	}

	public void play() {
		int state = alGetSourcei(sourceId, AL_SOURCE_STATE);
		if (state == AL_STOPPED) {
			isPlaying = false;
			alSourcei(sourceId, AL_POSITION, 0);
		}

		if (!isPlaying) {
			alSourcePlay(sourceId);
			isPlaying = true;
		}
	}

	public static void setListener(Vector3f location, Camera camera){
		alListener3f(AL_POSITION, location.x, location.y, location.z);
		Matrix4f mat = Transformation.getViewMatrix(camera);
		Vector3f at = new Vector3f();
		mat.positiveZ(at).negate();
		Vector3f up = new Vector3f();
		mat.positiveY(up);
		float[] data = new float[6];
		data[0] = at.x;
		data[1] = at.y;
		data[2] = at.z;
		data[3] = up.x;
		data[4] = up.y;
		data[5] = up.z;
		alListenerfv(AL_ORIENTATION, data);
	}

	public void play(Vector3f location) {
		int state = alGetSourcei(sourceId, AL_SOURCE_STATE);
		if (state == AL_STOPPED) {
			isPlaying = false;
			alSourcei(sourceId, AL_POSITION, 0);
		}

		if (!isPlaying) {
			if(location != null) {
				alSource3f(sourceId, AL_POSITION, location.x, location.y, location.z);
			}
			alSourcePlay(sourceId);
			isPlaying = true;
		}
	}

	public void stop() {
		if (isPlaying) {
			alSourceStop(sourceId);
			isPlaying = false;
		}
	}

	public String getFilepath() {
		return this.filepath;
	}

	public boolean isPlaying() {
		int state = alGetSourcei(sourceId, AL_SOURCE_STATE);
		if (state == AL_STOPPED) {
			isPlaying = false;
		}
		return isPlaying;
	}

}
