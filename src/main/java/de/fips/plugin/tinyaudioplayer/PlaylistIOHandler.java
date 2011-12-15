package de.fips.plugin.tinyaudioplayer;

import java.io.File;

import lombok.RequiredArgsConstructor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.fips.plugin.tinyaudioplayer.audio.Playlist;
import de.fips.plugin.tinyaudioplayer.io.AudioFileReader;
import de.fips.plugin.tinyaudioplayer.io.PlaylistReader;
import de.fips.plugin.tinyaudioplayer.io.PlaylistWriter;

@RequiredArgsConstructor
public class PlaylistIOHandler {
	private final Display display;

	public PlaylistIOHandler() {
		this(Display.getDefault());
	}

	public Playlist loadPlaylist() {
		Playlist newPlaylist = null;
		final Shell shell = display.getActiveShell();
		final FileChooser dialog = new FileChooser(shell, SWT.OPEN);
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

	public void savePlaylist(final Playlist playlist) {
		if (!playlist.isEmpty()) {
			final Shell shell = display.getActiveShell();
			final FileChooser dialog = new FileChooser(shell, SWT.SAVE);
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
}
