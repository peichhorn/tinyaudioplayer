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
package de.fips.plugin.tinyaudioplayer;

import static de.fips.plugin.tinyaudioplayer.TinyAudioPlayerConstants.COVER_DETECTION_PATTERN;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;

import lombok.Getter;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import de.fips.plugin.tinyaudioplayer.audio.AudioPlayer;
import de.fips.plugin.tinyaudioplayer.audio.IAudioPlayer;
import de.fips.plugin.tinyaudioplayer.audio.IPlaybackListener;
import de.fips.plugin.tinyaudioplayer.audio.IPlaylistListener;
import de.fips.plugin.tinyaudioplayer.audio.PlaybackEvent;
import de.fips.plugin.tinyaudioplayer.audio.Playlist;
import de.fips.plugin.tinyaudioplayer.audio.PlaylistItem;
import de.fips.plugin.tinyaudioplayer.io.AudioFileReader;
import de.fips.plugin.tinyaudioplayer.io.PlaylistReader;

/**
 * 
 * @author Philipp Eichhorn
 */
public class TinyAudioPlayer implements IAudioPlayer {
	private final PlaybackListener playbackListener = new PlaybackListener();
	private final PlaylistListener playlistListener = new PlaylistListener();
	private final Playlist playlist = new Playlist();
	@Getter
	private volatile boolean mute;
	private volatile float volume = 1.0f;
	private AudioPlayer player;

	TinyAudioPlayer() {
		playlist.addPlaylistListener(playlistListener);
	}

	public void stop() {
		if (player != null) {
			player.stop();
			player.removePlaybackListener(playbackListener);
			player = null;
		}
	}

	private void play(final String filename) {
		stop();
		player = new AudioPlayer(filename, volume, mute);
		player.addPlaybackListener(playbackListener);
		player.play();
	}

	public void play() {
		if ((player != null) && (player.isPaused())) {
			player.play();
		} else {
			if (playlist.hasTracks()) {
				play(playlist.getCurrentTrack().getLocation());
			}
		}
	}

	public void pause() {
		if (player != null) {
			if (player.isPaused()) {
				player.play();
			} else {
				player.pause();
			}
		}
	}

	public void previous() {
		boolean wasPlaying = player != null;
		stop();
		playlist.previous();
		if (wasPlaying) {
			play();
		}
	}

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
	
	public void setMute(final boolean mute) {
		this.mute = mute;
		if (player != null) {
			player.setMute(this.mute);
		}
	}

	public void setVolume(final float volume) {
		this.volume = volume;
		if (player != null) {
			player.setVolume(volume);
		}
	}

	public void eject() {
		final Shell shell = new Shell(Display.getDefault());
		final FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		dialog.setFilterExtensions(new String[] { new AudioFileReader().formatExtensions(), new PlaylistReader().formatExtensions() });
		dialog.setFilterNames(new String[] { new AudioFileReader().completeFormatName(), new PlaylistReader().completeFormatName() });
		final String selectedFileName = dialog.open();
		if (selectedFileName != null) {
			final File selectedFile = new File(selectedFileName);
			Playlist newPlaylist = null;
			if (new AudioFileReader().canHandle(selectedFile)) {
				newPlaylist = new AudioFileReader().read(selectedFile);
			} else if (new PlaylistReader().canHandle(selectedFile)) {
				newPlaylist = new PlaylistReader().read(selectedFile);
			}
			if (newPlaylist != null) {
				playlist.clear();
				playlist.add(newPlaylist);
				play();
			}
		}
	}
	
	private Image loadCoverFor(final PlaylistItem track){
		Image thumbnail = null;
		final File file = new File(track.getLocation());
		final File parent = file.getParentFile();
		if (parent != null) {
			String[] coverNames = parent.list(new FilenameFilter() {
				@Override
				public boolean accept(File file, String s) {
					final Matcher matcher = COVER_DETECTION_PATTERN.matcher(s.toLowerCase());
					return matcher.matches();
				}
			});
			if (coverNames.length > 0) {
				try {
					final File coverFile = new File(parent, coverNames[0]).getCanonicalFile().getAbsoluteFile();
					thumbnail = TinyAudioPlayerPlugin.getDefaultImageRegistry().get(coverFile.getPath());
					if (thumbnail == null) {
						final URL imageURL = coverFile.toURI().toURL();
						final ImageDescriptor descriptor = ImageDescriptor.createFromURL(imageURL);
						thumbnail = descriptor.createImage();
						thumbnail = new Image(Display.getDefault(), thumbnail.getImageData().scaledTo(80, 80));
						TinyAudioPlayerPlugin.getDefaultImageRegistry().put(coverFile.getPath(), thumbnail);
					}
				} catch (IOException ignore) {
				}
			}
		}
		return thumbnail;
	}

	private class PlaybackListener implements IPlaybackListener {
		
		@Override
		public void handlePlaybackEvent(PlaybackEvent event) {
			switch (event.getType()) {
			case Finished:
				next();
				break;
			case Started:
				final PlaylistItem track = playlist.getCurrentTrack();
				NotifierDialog.notifyAsync("Now playing:", track.getFormattedDisplayName(), loadCoverFor(track));
				break;
			default:
				break;
			}
		}
	}

	private class PlaylistListener implements IPlaylistListener {

		@Override
		public void trackEnqueued(PlaylistItem item) {
			// TODO Auto-generated method stub

		}

		@Override
		public void trackChanged(PlaylistItem item) {
			// TODO Auto-generated method stub

		}

		@Override
		public void trackRemoved(PlaylistItem item) {
			// TODO Auto-generated method stub

		}

		@Override
		public void playlistCleared() {
			// TODO Auto-generated method stub

		}
	}
}
