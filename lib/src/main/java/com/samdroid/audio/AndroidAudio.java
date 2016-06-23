package com.samdroid.audio;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

public class AndroidAudio implements Audio {
	AssetManager assets;
	SoundPool soundPool;

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi") 
	public AndroidAudio(Activity activity) {
		activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		this.assets = activity.getAssets();

		if (Build.VERSION.SDK_INT >= 21) {
			SoundPool.Builder builder = new SoundPool.Builder();
			builder.setMaxStreams(20);
			AudioAttributes.Builder audioBuilder = new AudioAttributes.Builder();
			audioBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
			builder.setAudioAttributes(audioBuilder.build());
			this.soundPool = builder.build();
		
		} else {
			this.soundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);
		}
	}

	@Override
	public Music createMusic(String filename) {
		try {
			AssetFileDescriptor assetDescriptor = assets.openFd(filename);
			return new AndroidMusic(assetDescriptor);
		} catch (IOException e) {
			throw new RuntimeException("Couldn't load music '" + filename + "'");
		}
	}

	@Override
	public Sound createSound(String filename) {
		try {
			AssetFileDescriptor assetDescriptor = assets.openFd(filename);
			int soundId = soundPool.load(assetDescriptor, 0);
			return new AndroidSound(soundPool, soundId);
		} catch (IOException e) {
			throw new RuntimeException("Couldn't load sound '" + filename + "'");
		}
	}
}
