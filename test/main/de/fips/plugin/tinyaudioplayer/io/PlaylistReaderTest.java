package de.fips.plugin.tinyaudioplayer.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import de.fips.plugin.tinyaudioplayer.audio.Playlist;
import de.fips.plugin.tinyaudioplayer.audio.PlaylistItem;

public class PlaylistReaderTest {
	@Test
	public void readPLS() throws Exception {
		final File testFile = new File(getClass().getResource("playlist.pls").toURI());
		final PlaylistReader reader = new PlaylistReader();
		assertTrue(reader.canHandle(testFile));
		final Playlist playlist = reader.read(testFile);
		assertEquals(2, playlist.size());
		final PlaylistItem track1 = playlist.getCurrentTrack();
		assertEquals("Artist - Track 01", track1.getName());
		assertEquals(new File("01 - Track 01.mp3").getCanonicalFile().getAbsolutePath(), track1.getLocation());
		assertEquals(220, track1.getLength());
		final PlaylistItem track2 = playlist.getNextTrack();
		assertEquals("Author - Book - Chapter 03 - Title", track2.getName());
		assertEquals(new File("Chapter 03 - Title.mp3").getCanonicalFile().getAbsolutePath(), track2.getLocation());
		assertEquals(1167, track2.getLength());
	}
	
	@Test
	public void readM3U() throws Exception {
		final File testFile = new File(getClass().getResource("playlist.m3u").toURI());
		final PlaylistReader reader = new PlaylistReader();
		assertTrue(reader.canHandle(testFile));
		final Playlist playlist = reader.read(testFile);
		assertEquals(2, playlist.size());
		final PlaylistItem track1 = playlist.getCurrentTrack();
		assertEquals("Artist - Track 01", track1.getName());
		assertEquals(new File("01 - Track 01.mp3").getCanonicalFile().getAbsolutePath(), track1.getLocation());
		assertEquals(220, track1.getLength());
		final PlaylistItem track2 = playlist.getNextTrack();
		assertEquals("Author - Book - Chapter 03 - Title", track2.getName());
		assertEquals(new File("Chapter 03 - Title.mp3").getCanonicalFile().getAbsolutePath(), track2.getLocation());
		assertEquals(1167, track2.getLength());
	}
}
