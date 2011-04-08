package de.fips.plugin.tinyaudioplayer.io;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import lombok.Cleanup;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.fips.plugin.tinyaudioplayer.audio.Playlist;
import de.fips.plugin.tinyaudioplayer.audio.PlaylistItem;

public class PlaylistWriterTest {
	private File testDir;

	@Before
	public void createTestDir() {
		testDir = new File(new File(System.getProperty("java.io.tmpdir")), PlaylistWriterTest.class.getSimpleName());
		testDir.mkdir();
	}

	@After
	public void cleanUpTestDir() {
		assertTrue(FileUtils.removeDirectory(testDir));
	}

	@Test
	public void formatDefinition() {
		// setup
		final PlaylistWriter writer = new PlaylistWriter();
		// run + assert
		assertEquals("Playlist File", writer.formatName());
		assertEquals("*.m3u;*.pls", writer.formatExtensions());
		assertEquals("Playlist File (*.m3u;*.pls)", writer.completeFormatName());
	}

	@Test
	public void whenInvokedWithPLSFile_canHandle_shouldReturnTrue() throws Exception {
		// setup
		final PlaylistWriter writer = new PlaylistWriter();
		// run + assert
		assertTrue(writer.canHandle(new File("test.pls")));
	}

	@Test
	public void whenInvokedWithM3UFile_canHandle_shouldReturnTrue() throws Exception {
		// setup
		final PlaylistWriter writer = new PlaylistWriter();
		// run + assert
		assertTrue(writer.canHandle(new File("test.m3u")));
	}

	@Test
	public void whenInvokedWithAnUnwantedFile_canHandle_shouldReturnFalse() throws Exception {
		// setup
		final PlaylistWriter writer = new PlaylistWriter();
		// run + assert
		assertFalse(writer.canHandle(new File("test.xml")));
	}

	@Test
	public void writePLS() throws Exception {
		// setup
		final File testFile = new File(testDir, "playlist.pls");
		final File track1 = new File(testDir, "01 - Track 01.mp3");
		track1.createNewFile();
		final File track2 = new File(testDir, "Chapter 03 - Title.mp3");
		track2.createNewFile();
		final Playlist playlist = new Playlist();
		playlist.add(new PlaylistItem("Artist - Track 01", track1.getAbsolutePath(), 220));
		playlist.add(new PlaylistItem("Author - Book - Chapter 03 - Title", track2.getAbsolutePath(), 1167));
		final PlaylistWriter writer = new PlaylistWriter();
		final List<String> allExpected = asList( //
				"[playlist]", //
				"", //
				"File1=01 - Track 01.mp3", //
				"Title1=Artist - Track 01", //
				"Length1=220", //
				"", //
				"File2=Chapter 03 - Title.mp3", //
				"Title2=Author - Book - Chapter 03 - Title", //
				"Length2=1167", //
				"", //
				"NumberOfEntries=2", //
				"Version=2");
		// run
		writer.write(testFile, playlist);
		@Cleanup TextLines lines = TextLines.textLinesIn(testFile);
		// assert
		for (String expected : allExpected) {
			assertTrue(lines.hasNext());
			assertEquals(expected, lines.next());
		}
		assertFalse(lines.hasNext());
	}

	@Test
	public void writeM3U() throws Exception {
		// setup
		final File testFile = new File(testDir, "playlist.m3u");
		final File track1 = new File(testDir, "01 - Track 01.mp3");
		final File track2 = new File(testDir, "Chapter 03 - Title.mp3");
		final Playlist playlist = new Playlist();
		playlist.add(new PlaylistItem("Artist - Track 01", track1.getAbsolutePath(), 220));
		playlist.add(new PlaylistItem("Author - Book - Chapter 03 - Title", track2.getAbsolutePath(), 1167));
		final PlaylistWriter writer = new PlaylistWriter();
		final List<String> allExpected = asList( //
				"#EXTM3U", //
				"#EXTINF:220,Artist - Track 01", //
				"01 - Track 01.mp3", //
				"#EXTINF:1167,Author - Book - Chapter 03 - Title", //
				"Chapter 03 - Title.mp3");
		// run
		writer.write(testFile, playlist);
		@Cleanup TextLines lines = TextLines.textLinesIn(testFile);
		// assert
		for (String expected : allExpected) {
			assertTrue(lines.hasNext());
			assertEquals(expected, lines.next());
		}
		assertFalse(lines.hasNext());
	}
}
