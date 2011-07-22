package de.fips.plugin.tinyaudioplayer.io;

import static org.fest.assertions.Assertions.assertThat;
import static de.fips.plugin.tinyaudioplayer.assertions.Assertions.assertThat;

import java.io.File;

import org.junit.Test;

import de.fips.plugin.tinyaudioplayer.audio.Playlist;

public class PlaylistReaderTest {

	@Test
	public void formatDefinition() {
		// setup
		final PlaylistReader reader = new PlaylistReader();
		// run + assert
		assertThat(reader.formatName()).isEqualTo("Playlist File");
		assertThat(reader.formatExtensions()).isEqualTo("*.m3u;*.pls");
		assertThat(reader.completeFormatName()).isEqualTo("Playlist File (*.m3u;*.pls)");
	}

	@Test
	public void whenInvokedWithPLSFile_canHandle_shouldReturnTrue() throws Exception {
		// setup
		final PlaylistReader reader = new PlaylistReader();
		// run + assert
		assertThat(reader.canHandle(new File("test.pls"))).isTrue();
	}

	@Test
	public void whenInvokedWithM3UFile_canHandle_shouldReturnTrue() throws Exception {
		// setup
		final PlaylistReader reader = new PlaylistReader();
		// run + assert
		assertThat(reader.canHandle(new File("test.m3u"))).isTrue();
	}

	@Test
	public void whenInvokedWithAnUnwantedFile_canHandle_shouldReturnFalse() throws Exception {
		// setup
		final PlaylistReader reader = new PlaylistReader();
		// run + assert
		assertThat(reader.canHandle(new File("test.xml"))).isFalse();
	}

	@Test
	public void whenInvokedWithPLSFile_read_shouldCreateValidPlaylist() throws Exception {
		// setup
		final File testFile = file("playlist.pls");
		final PlaylistReader reader = new PlaylistReader();
		// run
		final Playlist playlist = reader.read(testFile);
		// assert
		assertThat(playlist).hasSize(3);
		assertThat(playlist.getCurrentTrack()).hasName("Artist - Track 01") //
				.hasLocation(new File("01 - Track 01.mp3")) //
				.hasLength(220);
		assertThat(playlist.getNextTrack()).hasName("Author - Book - Chapter 03 - Title") //
				.hasLocation(new File("Chapter 03 - Title.mp3")) //
				.hasLength(1167);
		assertThat(playlist.getNextTrack()).hasName("Chapter 04 - Title") //
				.hasLocation(new File("Chapter 04 - Title.mp3")) //
				.hasLength(0);
	}

	@Test
	public void whenInvokedWithM3UFile_read_shouldCreateValidPlaylist() throws Exception {
		// setup
		final File testFile = file("playlist.m3u");
		final PlaylistReader reader = new PlaylistReader();
		// run
		final Playlist playlist = reader.read(testFile);
		// assert
		assertThat(playlist).hasSize(3);
		assertThat(playlist.getCurrentTrack()).hasName("Artist - Track 01") //
				.hasLocation(new File("01 - Track 01.mp3")) //
				.hasLength(220);
		assertThat(playlist.getNextTrack()).hasName("Author - Book - Chapter 03 - Title") //
				.hasLocation(new File("Chapter 03 - Title.mp3")) //
				.hasLength(1167);
		assertThat(playlist.getNextTrack()).hasName("Chapter 04 - Title") //
				.hasLocation(new File("Chapter 04 - Title.mp3")) //
				.hasLength(0);
	}

	private File file(String path) throws Exception {
		return new File(getClass().getResource(path).toURI());
	}
}
