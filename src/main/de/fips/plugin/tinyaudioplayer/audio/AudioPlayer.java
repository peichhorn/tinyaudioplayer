/*
Copyright � 2011 Philipp Eichhorn.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
package de.fips.plugin.tinyaudioplayer.audio;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import de.fips.plugin.tinyaudioplayer.TinyAudioPlayerConstants;
import de.fips.plugin.tinyaudioplayer.TinyAudioPlayerPlugin;
import de.fips.plugin.tinyaudioplayer.audio.PlaybackEvent.Type;

import lombok.AutoGenMethodStub;
import lombok.Await;
import lombok.Getter;
import lombok.ListenerSupport;
import lombok.Signal;

/**
 * 
 * @author: Philipp Eichhorn
 */
@ListenerSupport(IPlaybackListener.class)
@AutoGenMethodStub
public class AudioPlayer implements IAudioPlayer, Runnable {
	private final transient ILog log = TinyAudioPlayerPlugin.getDefaultLog();
	
	private final int EXTERNAL_BUFFER_SIZE = 0x10000;
	@Getter
	private volatile boolean paused;
	private volatile boolean mute;
	private volatile float volume = 1.0f;
	private final String fileName;
	private SourceDataLine line;

	public AudioPlayer(final String fileName, final float volume, final boolean mute) {
		this.fileName = fileName;
		this.volume = Math.min(2.0f, Math.max(volume, 0.0f));
		this.mute = mute;
	}
	
	public void pause() {
		paused = true;
	}
	
	public void play() {
		if (line == null) {
			final Thread thread = new Thread(this, "AudioPlayer thread playing: " + fileName);
			thread.setDaemon(true);
			thread.start();
		} else {
			runUnpause();
		}
	}

	public void stop() {
		if (line != null) {
			runUnpause();
			line.stop();
			line.close();
			line = null;
		}
	}
	
	public void setMute(final boolean mute) {
		this.mute = mute;
		applyMute();
	}
	
	public void setVolume(final float volume) {
		this.volume = Math.min(2.0f, Math.max(volume, 0.0f));
		applyVolume();
	}
	
	@Override
	public void run() {
		final AudioInputStream encodedAudioInputStream = getEncodeAudioInputStream();
		if (encodedAudioInputStream != null) {
			fireHandlePlaybackEvent(new PlaybackEvent(Type.Started));

			final AudioFormat decodedFormat = decodeAudioFormat(encodedAudioInputStream.getFormat());
			AudioInputStream decodedAudioInputStream = null;
			try {
				decodedAudioInputStream = AudioSystem.getAudioInputStream(decodedFormat, encodedAudioInputStream);
				line = (SourceDataLine) AudioSystem.getLine(new DataLine.Info(SourceDataLine.class, decodedFormat));
				line.open(decodedFormat);
				applyMute();
				applyVolume();
				line.start();
				int nBytesRead = 0;
				byte[] abData = new byte[EXTERNAL_BUFFER_SIZE];
				while (nBytesRead != -1) {
					nBytesRead = decodedAudioInputStream.read(abData, 0, abData.length);
					if (nBytesRead >= 0) {
						if (isPaused()) {
							if (line.isRunning()) {
								line.stop();
							}
							fireHandlePlaybackEvent(new PlaybackEvent(Type.Paused));
							runPause();
							fireHandlePlaybackEvent(new PlaybackEvent(Type.Resumed));
							if (!line.isRunning()) {
								line.start();
							}
						}
						line.write(abData, 0, nBytesRead);
						fireHandlePlaybackEvent(new PlaybackEvent(Type.Progress, line.getMicrosecondPosition()));
					}
				}
				decodedAudioInputStream.close();
				line.drain();
				fireHandlePlaybackEvent(new PlaybackEvent(Type.Finished));
			} catch (Exception e) {
				if (decodedAudioInputStream != null) {
					try {
						decodedAudioInputStream.close();
					} catch(IOException ignore) {
					}
				}
				fireHandlePlaybackEvent(new PlaybackEvent(Type.Canceled));
			} finally {
				
				stop();
			}
		}
	}
	
	private void applyMute() {
		if ((line != null ) && line.isOpen() && line.isControlSupported(BooleanControl.Type.MUTE)) {
			final BooleanControl muteControl = ((BooleanControl)line.getControl(BooleanControl.Type.MUTE));
			muteControl.setValue(mute);
		}
	}
	
	private void applyVolume() {
		if ((line != null ) && line.isOpen() && line.isControlSupported(FloatControl.Type.MASTER_GAIN)) { 
			final FloatControl volumeControl = ((FloatControl)line.getControl(FloatControl.Type.MASTER_GAIN));
			float dB = (float)(Math.log(volume) / Math.log(10.0) * 20.0);
			volumeControl.setValue(dB);
		}
	}

	private AudioInputStream getEncodeAudioInputStream() {
		final File soundFile = new File(fileName);
		AudioInputStream encodedAudioInputStream = null;
		try {
			encodedAudioInputStream = AudioSystem.getAudioInputStream(soundFile);
		} catch (UnsupportedAudioFileException e) {
			log.log(new Status(IStatus.ERROR, TinyAudioPlayerConstants.PLUGIN_ID, "Filetype of '" + fileName + "' not supported!"));
		} catch (IOException e) {
			log.log(new Status(IStatus.ERROR, TinyAudioPlayerConstants.PLUGIN_ID, "Filetype of '" + fileName + "' not supported!"));
		}
		return encodedAudioInputStream;
	}

	private AudioFormat decodeAudioFormat(final AudioFormat endcodedAudioFormat) {
		int sampleSizeInBits = 16;
		if ((endcodedAudioFormat.getEncoding() != AudioFormat.Encoding.ULAW) && (endcodedAudioFormat.getEncoding() != AudioFormat.Encoding.ALAW)) {
			sampleSizeInBits = endcodedAudioFormat.getSampleSizeInBits();
			if (sampleSizeInBits != 8) {
				sampleSizeInBits = 16;
			}
		}

		final int channels = endcodedAudioFormat.getChannels();
		final float sampleRate = endcodedAudioFormat.getSampleRate();
		return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sampleRate, sampleSizeInBits, channels, (channels * sampleSizeInBits) >> 3, sampleRate, false);
	}

	@Await(value = "canResume", conditionMethod = "isPaused")
	private void runPause() {
	}
	
	
	@Signal("canResume")
	private void runUnpause() {
		paused = false; 
	}
	
}