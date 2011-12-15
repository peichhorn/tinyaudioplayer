package de.fips.plugin.tinyaudioplayer.io;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import de.fips.plugin.tinyaudioplayer.audio.Playlist;
import de.fips.plugin.tinyaudioplayer.audio.PlaylistItem;

public class PlaylistWriterTest {
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@Test
	public void formatDefinition() {
		// setup
		final PlaylistWriter writer = new PlaylistWriter();
		// run + assert
		assertThat(writer.formatName()).isEqualTo("Playlist File");
		assertThat(writer.formatExtensions()).isEqualTo("*.m3u;*.pls");
		assertThat(writer.completeFormatName()).isEqualTo("Playlist File (*.m3u;*.pls)");
	}

	@Test
	public void whenInvokedWithPLSFile_canHandle_shouldReturnTrue() throws Exception {
		// setup
		final PlaylistWriter writer = new PlaylistWriter();
		// run + assert
		assertThat(writer.canHandle(new File("test.pls"))).isTrue();
	}

	@Test
	public void whenInvokedWithM3UFile_canHandle_shouldReturnTrue() throws Exception {
		// setup
		final PlaylistWriter writer = new PlaylistWriter();
		// run + assert
		assertThat(writer.canHandle(new File("test.m3u"))).isTrue();
	}

	@Test
	public void whenInvokedWithAnUnwantedFile_canHandle_shouldReturnFalse() throws Exception {
		// setup
		final PlaylistWriter writer = new PlaylistWriter();
		// run + assert
		assertThat(writer.canHandle(new File("test.xml"))).isFalse();
	}

	@Test
	public void whenInvokedWithPLSFile_write_shouldCreateValidPLSFile() throws Exception {
		// setup
		final File testDir = tempFolder.newFolder(getClass().getSimpleName());
		final File testFile = new File(testDir, "playlist.pls");
		final Playlist playlist = createPlaylist(testDir);
		final PlaylistWriter writer = new PlaylistWriter();
		// run
		writer.write(testFile, playlist);
		// assert
		assertThat(testFile).hasSameContentAs(file("exported_playlist.pls"));
	}

	@Test
	public void whenInvokedWithM3UFile_write_shouldCreateValidM3UFile() throws Exception {
		// setup
		final File testDir = tempFolder.newFolder(getClass().getSimpleName());
		final File testFile = new File(testDir, "playlist.m3u");
		final Playlist playlist = createPlaylist(testDir);
		final PlaylistWriter writer = new PlaylistWriter();
		// run
		writer.write(testFile, playlist);
		// assert
		assertThat(testFile).hasSameContentAs(file("exported_playlist.m3u"));
	}

	private Playlist createPlaylist(final File testDir) {
		final File track1 = new File(testDir, "01 - Track 01.mp3");
		final File track2 = new File(testDir, "Chapter 03 - Title.mp3");
		final Playlist playlist = new Playlist();
		playlist.add(new PlaylistItem("Artist - Track 01", track1.toURI(), 220));
		playlist.add(new PlaylistItem("Author - Book - Chapter 03 - Title", track2.toURI(), 1167));
		return playlist;
	}

	private File file(String path) throws Exception {
		return new File(getClass().getResource(path).toURI());
	}
}
