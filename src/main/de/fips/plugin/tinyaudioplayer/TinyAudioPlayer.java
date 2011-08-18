/*
 * Copyright © 2011 Philipp Eichhorn.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de.fips.plugin.tinyaudioplayer;

import lombok.Delegate;

import de.fips.plugin.tinyaudioplayer.audio.IPlaybackListener;
import de.fips.plugin.tinyaudioplayer.audio.PlaybackEvent;
import de.fips.plugin.tinyaudioplayer.audio.Playlist;
import de.fips.plugin.tinyaudioplayer.audio.PlaylistAudioPlayer;
import de.fips.plugin.tinyaudioplayer.audio.PlaylistItem;
import de.fips.plugin.tinyaudioplayer.notifier.NotifierDialog;

/**
 *
 * @author Philipp Eichhorn
 */
public class TinyAudioPlayer {
	@Delegate
	private final PlaylistAudioPlayer player;
	private final PlaylistIOHandler playlistIOHandler;

	public TinyAudioPlayer() {
		this(new PlaylistAudioPlayer(), new PlaylistIOHandler());
	}

	public TinyAudioPlayer(final PlaylistAudioPlayer player, final PlaylistIOHandler playlistIOHandler) {
		super();
		this.player = player;
		this.player.setPlaybackHandler(new PlaybackHandler());
		this.playlistIOHandler = playlistIOHandler;
	}

	public void enqueue() {
		final Playlist newPlaylist = playlistIOHandler.loadNewPlaylist();
		if (newPlaylist != null) {
			player.getPlaylist().add(newPlaylist);
		}
	}

	public void eject() {
		final Playlist newPlaylist = playlistIOHandler.loadNewPlaylist();
		if (newPlaylist != null) {
			player.getPlaylist().clear();
			player.getPlaylist().add(newPlaylist);
			player.play();
		}
	}

	public void export() {
		playlistIOHandler.savePlaylist(player.getPlaylist());
	}

	public void removeSelected() {
		player.getPlaylist().removeSelected();
	}

	public void removeDuplicates() {
		player.getPlaylist().removeDuplicates();
	}

	private class PlaybackHandler implements IPlaybackListener {

		@Override
		public void handlePlaybackEvent(PlaybackEvent event) {
			switch (event.getType()) {
			case Finished:
				player.next();
				break;
			case Started:
				final PlaylistItem track = player.getPlaylist().getCurrentTrack();
				NotifierDialog.notifyAsync("Now playing:", track.getDisplayableName(), track.getLocation());
				break;
			default:
				break;
			}
		}
	}
}
