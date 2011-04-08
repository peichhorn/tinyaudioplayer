package de.fips.plugin.tinyaudioplayer.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import de.fips.plugin.tinyaudioplayer.audio.Playlist;
import de.fips.plugin.tinyaudioplayer.audio.PlaylistItem;

public class AudioFileReaderTest {
	@Test
	public void formatDefinition() {
		// setup
		final AudioFileReader reader = new AudioFileReader();
		// run + assert
		assertEquals("Audio File", reader.formatName());
		assertEquals("*.mp3;*.ogg;*.wav", reader.formatExtensions());
		assertEquals("Audio File (*.mp3;*.ogg;*.wav)", reader.completeFormatName());
	}

	@Test
	public void whenInvokedWithMP3File_canHandle_shouldReturnTrue() throws Exception {
		// setup
		final AudioFileReader reader = new AudioFileReader();
		// run + assert
		assertTrue(reader.canHandle(new File("test.mp3")));
	}

	@Test
	public void whenInvokedWithOGGFile_canHandle_shouldReturnTrue() throws Exception {
		// setup
		final AudioFileReader reader = new AudioFileReader();
		// run + assert
		assertTrue(reader.canHandle(new File("test.ogg")));
	}

	@Test
	public void whenInvokedWithWAVFile_canHandle_shouldReturnTrue() throws Exception {
		// setup
		final AudioFileReader reader = new AudioFileReader();
		// run + assert
		assertTrue(reader.canHandle(new File("test.wav")));
	}

	@Test
	public void whenInvokedWithAnUnwantedFile_canHandle_shouldReturnFalse() throws Exception {
		// setup
		final AudioFileReader reader = new AudioFileReader();
		// run + assert
		assertFalse(reader.canHandle(new File("test.xml")));
	}

	@Test
	public void readMP3() throws Exception {
		// setup
		final File testFile = new File(getClass().getResource("track.mp3").toURI());
		final AudioFileReader reader = new AudioFileReader();
		// run
		final Playlist playlist = reader.read(testFile);
		// assert
		assertEquals(1, playlist.size());
		final PlaylistItem track1 = playlist.getNextTrack();
		assertEquals("track", track1.getName());
		assertEquals(testFile.getAbsolutePath(), track1.getLocation());
		assertEquals(0, track1.getLength());
	}

	@Test
	public void readWAV() throws Exception {
		// setup
		final File testFile = new File(getClass().getResource("track.wav").toURI());
		final AudioFileReader reader = new AudioFileReader();
		// run
		final Playlist playlist = reader.read(testFile);
		// assert
		assertEquals(1, playlist.size());
		final PlaylistItem track1 = playlist.getNextTrack();
		assertEquals("track", track1.getName());
		assertEquals(testFile.getAbsolutePath(), track1.getLocation());
		assertEquals(0, track1.getLength());
	}

	@Test
	public void readOGG() throws Exception {
		// setup
		final File testFile = new File(getClass().getResource("track.ogg").toURI());
		final AudioFileReader reader = new AudioFileReader();
		// run
		final Playlist playlist = reader.read(testFile);
		// assert
		assertEquals(1, playlist.size());
		final PlaylistItem track1 = playlist.getNextTrack();
		assertEquals("track", track1.getName());
		assertEquals(testFile.getAbsolutePath(), track1.getLocation());
		assertEquals(0, track1.getLength());
	}
}
