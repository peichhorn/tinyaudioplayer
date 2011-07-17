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

import lombok.Delegate;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import de.fips.plugin.tinyaudioplayer.audio.IPlaybackListener;
import de.fips.plugin.tinyaudioplayer.audio.PlaybackEvent;
import de.fips.plugin.tinyaudioplayer.audio.Playlist;
import de.fips.plugin.tinyaudioplayer.audio.PlaylistAudioPlayer;
import de.fips.plugin.tinyaudioplayer.audio.PlaylistItem;
import de.fips.plugin.tinyaudioplayer.io.AudioFileReader;
import de.fips.plugin.tinyaudioplayer.io.PlaylistReader;
import de.fips.plugin.tinyaudioplayer.io.PlaylistWriter;
import de.fips.plugin.tinyaudioplayer.notifier.NotifierDialog;

/**
 *
 * @author Philipp Eichhorn
 */
public class TinyAudioPlayer {
	@Delegate
	private final PlaylistAudioPlayer player;

	public TinyAudioPlayer() {
		this(new PlaylistAudioPlayer());
	}

	public TinyAudioPlayer(final PlaylistAudioPlayer player) {
		super();
		this.player = player;
		this.player.setPlaybackHandler(new PlaybackHandler());
	}

	public void enqueue() {
		final Playlist newPlaylist = loadNewPlaylist();
		if (newPlaylist != null) {
			player.getPlaylist().add(newPlaylist);
		}
	}

	public void eject() {
		final Playlist newPlaylist = loadNewPlaylist();
		if (newPlaylist != null) {
			player.getPlaylist().clear();
			player.getPlaylist().add(newPlaylist);
			player.play();
		}
	}

	Playlist loadNewPlaylist() {
		Playlist newPlaylist = null;
		final Shell shell = new Shell(Display.getDefault());
		final FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		final String audioFileExtensions = new AudioFileReader().formatExtensions();
		final String playlistFileExtensions = new PlaylistReader().formatExtensions();
		dialog.setFilterExtensions(new String[] { audioFileExtensions + ";" + playlistFileExtensions, audioFileExtensions, playlistFileExtensions });
		dialog.setFilterNames(new String[] { "All Supported Files", new AudioFileReader().completeFormatName(), new PlaylistReader().completeFormatName() });
		final String selectedFileName = dialog.open();
		if (selectedFileName != null) {
			final File selectedFile = new File(selectedFileName);
			if (new AudioFileReader().canHandle(selectedFile)) {
				newPlaylist = new AudioFileReader().read(selectedFile);
			} else if (new PlaylistReader().canHandle(selectedFile)) {
				newPlaylist = new PlaylistReader().read(selectedFile);
			}
		}
		return newPlaylist;
	}

	public void export() {
		savePlaylist(player.getPlaylist());
	}
	
	void savePlaylist(final Playlist playlist) {
		if (playlist.hasTracks()) {
			final Shell shell = new Shell(Display.getDefault());
			final FileDialog dialog = new FileDialog(shell, SWT.SAVE);
			dialog.setFilterExtensions(new String[] { new PlaylistWriter().formatExtensions() });
			dialog.setFilterNames(new String[] { new PlaylistWriter().completeFormatName() });
			final String selectedFileName = dialog.open();
			if (selectedFileName != null) {
				final File selectedFile = new File(selectedFileName);
				if (new PlaylistWriter().canHandle(selectedFile)) {
					new PlaylistWriter().write(selectedFile, playlist);
				}
			}
		}
	}

	private Image loadCoverFor(final PlaylistItem track){
		Image thumbnail = null;
		File parent = null;
		try {
			final File file = new File(track.getLocation());
			parent = file.getParentFile();
		} catch(IllegalArgumentException ignore) {
			// File(URI) preconditions did not hold
		}
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

	private class PlaybackHandler implements IPlaybackListener {

		@Override
		public void handlePlaybackEvent(PlaybackEvent event) {
			switch (event.getType()) {
			case Finished:
				player.next();
				break;
			case Started:
				final PlaylistItem track = player.getPlaylist().getCurrentTrack();
				NotifierDialog.notifyAsync("Now playing:", track.getDisplayableName(), loadCoverFor(track));
				break;
			default:
				break;
			}
		}
	}
}
