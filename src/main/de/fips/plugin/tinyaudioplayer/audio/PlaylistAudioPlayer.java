/*
Copyright © 2011 Philipp Eichhorn.

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

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author Philipp Eichhorn
 */
@RequiredArgsConstructor
public class PlaylistAudioPlayer implements IMultiTrackAudioPlayer {
	@Getter
	private final Playlist playlist;
	private IPlaybackListener playbackListener;
	@Getter
	private volatile boolean mute;
	private volatile float volume = 1.0f;
	private SingleTrackAudioPlayer player;

	public PlaylistAudioPlayer() {
		this(new Playlist());
	}

	public void setPlaybackHandler(IPlaybackListener handler) {
		playbackListener = handler;
	}

	@Override
	public void stop() {
		if (player != null) {
			player.stop();
			if (playbackListener != null) {
				player.removePlaybackListener(playbackListener);
			}
			player = null;
		}
	}

	private void play(final String filename) {
		stop();
		player = new SingleTrackAudioPlayer(filename, volume, mute);
		if (playbackListener != null) {
			player.addPlaybackListener(playbackListener);
		}
		player.play();
	}

	@Override
	public void play() {
		if ((player != null) && (player.isPaused())) {
			player.play();
		} else {
			if (playlist.hasTracks()) {
				play(playlist.getCurrentTrack().getLocation());
			}
		}
	}

	@Override
	public void pause() {
		if (player != null) {
			if (player.isPaused()) {
				player.play();
			} else {
				player.pause();
			}
		}
	}

	@Override
	public void previous() {
		boolean wasPlaying = player != null;
		stop();
		playlist.previous();
		if (wasPlaying) {
			play();
		}
	}

	@Override
	public void next() {
		boolean wasPlaying = player != null;
		stop();
		playlist.next();
		if (wasPlaying) {
			play();
		}
	}

	public void toggleShuffle() {
		playlist.toggleShuffle();
	}

	public void toggleRepeat() {
		playlist.toggleRepeat();
	}

	@Override
	public void setMute(final boolean mute) {
		this.mute = mute;
		if (player != null) {
			player.setMute(this.mute);
		}
	}

	@Override
	public void setVolume(final float volume) {
		this.volume = volume;
		if (player != null) {
			player.setVolume(volume);
		}
	}
}
