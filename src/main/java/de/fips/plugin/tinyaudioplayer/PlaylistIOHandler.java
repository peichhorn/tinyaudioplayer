package de.fips.plugin.tinyaudioplayer;

import java.io.File;

import lombok.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

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
		val shell = display.getActiveShell();
		val dialog = new FileChooser(shell, SWT.OPEN);
		val audioFileExtensions = new AudioFileReader().formatExtensions();
		val playlistFileExtensions = new PlaylistReader().formatExtensions();
		dialog.setFilterExtensions(new String[] { audioFileExtensions + ";" + playlistFileExtensions, audioFileExtensions, playlistFileExtensions });
		dialog.setFilterNames(new String[] { "All Supported Files", new AudioFileReader().completeFormatName(), new PlaylistReader().completeFormatName() });
		val selectedFileName = dialog.open();
		if (selectedFileName != null) {
			val selectedFile = new File(selectedFileName);
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
			val shell = display.getActiveShell();
			val dialog = new FileChooser(shell, SWT.SAVE);
			dialog.setFilterExtensions(new String[] { new PlaylistWriter().formatExtensions() });
			dialog.setFilterNames(new String[] { new PlaylistWriter().completeFormatName() });
			val selectedFileName = dialog.open();
			if (selectedFileName != null) {
				val selectedFile = new File(selectedFileName);
				if (new PlaylistWriter().canHandle(selectedFile)) {
					new PlaylistWriter().write(selectedFile, playlist);
				}
			}
		}
	}
}
