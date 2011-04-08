package de.fips.plugin.tinyaudioplayer.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import de.fips.plugin.tinyaudioplayer.audio.Playlist;
import de.fips.plugin.tinyaudioplayer.audio.PlaylistItem;

public class PlaylistReaderTest {

	@Test
	public void formatDefinition() {
		// setup
		final PlaylistReader reader = new PlaylistReader();
		// run + assert
		assertEquals("Playlist File", reader.formatName());
		assertEquals("*.m3u;*.pls", reader.formatExtensions());
		assertEquals("Playlist File (*.m3u;*.pls)", reader.completeFormatName());
	}

	@Test
	public void whenInvokedWithPLSFile_canHandle_shouldReturnTrue() throws Exception {
		// setup
		final PlaylistReader reader = new PlaylistReader();
		// run + assert
		assertTrue(reader.canHandle(new File("test.pls")));
	}

	@Test
	public void whenInvokedWithM3UFile_canHandle_shouldReturnTrue() throws Exception {
		// setup
		final PlaylistReader reader = new PlaylistReader();
		// run + assert
		assertTrue(reader.canHandle(new File("test.m3u")));
	}

	@Test
	public void whenInvokedWithAnUnwantedFile_canHandle_shouldReturnFalse() throws Exception {
		// setup
		final PlaylistReader reader = new PlaylistReader();
		// run + assert
		assertFalse(reader.canHandle(new File("test.xml")));
	}

	@Test
	public void readPLS() throws Exception {
		// setup
		final File testFile = new File(getClass().getResource("playlist.pls").toURI());
		final PlaylistReader reader = new PlaylistReader();
		// run
		final Playlist playlist = reader.read(testFile);
		// assert
		assertEquals(3, playlist.size());
		final PlaylistItem track1 = playlist.getCurrentTrack();
		assertEquals("Artist - Track 01", track1.getName());
		assertEquals(new File("01 - Track 01.mp3").getAbsolutePath(), track1.getLocation());
		assertEquals(220, track1.getLength());
		final PlaylistItem track2 = playlist.getNextTrack();
		assertEquals("Author - Book - Chapter 03 - Title", track2.getName());
		assertEquals(new File("Chapter 03 - Title.mp3").getAbsolutePath(), track2.getLocation());
		assertEquals(1167, track2.getLength());
		final PlaylistItem track3 = playlist.getNextTrack();
		assertEquals("Chapter 04 - Title", track3.getName());
		assertEquals(new File("Chapter 04 - Title.mp3").getAbsolutePath(), track3.getLocation());
		assertEquals(0, track3.getLength());
	}

	@Test
	public void readM3U() throws Exception {
		// setup
		final File testFile = new File(getClass().getResource("playlist.m3u").toURI());
		final PlaylistReader reader = new PlaylistReader();
		// run
		final Playlist playlist = reader.read(testFile);
		// assert
		assertEquals(3, playlist.size());
		final PlaylistItem track1 = playlist.getCurrentTrack();
		assertEquals("Artist - Track 01", track1.getName());
		assertEquals(new File("01 - Track 01.mp3").getAbsolutePath(), track1.getLocation());
		assertEquals(220, track1.getLength());
		final PlaylistItem track2 = playlist.getNextTrack();
		assertEquals("Author - Book - Chapter 03 - Title", track2.getName());
		assertEquals(new File("Chapter 03 - Title.mp3").getAbsolutePath(), track2.getLocation());
		assertEquals(1167, track2.getLength());
		final PlaylistItem track3 = playlist.getNextTrack();
		assertEquals("Chapter 04 - Title", track3.getName());
		assertEquals(new File("Chapter 04 - Title.mp3").getAbsolutePath(), track3.getLocation());
		assertEquals(0, track3.getLength());
	}
}
