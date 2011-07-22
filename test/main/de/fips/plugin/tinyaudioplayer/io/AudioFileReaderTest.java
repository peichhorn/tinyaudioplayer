package de.fips.plugin.tinyaudioplayer.io;

import static de.fips.plugin.tinyaudioplayer.assertions.Assertions.assertThat;
import static org.fest.assertions.Assertions.assertThat;

import java.io.File;

import org.junit.Test;

import de.fips.plugin.tinyaudioplayer.audio.Playlist;

public class AudioFileReaderTest {
	@Test
	public void formatDefinition() {
		// setup
		final AudioFileReader reader = new AudioFileReader();
		// run + assert
		assertThat(reader.formatName()).isEqualTo("Audio File");
		assertThat(reader.formatExtensions()).isEqualTo("*.mp3;*.ogg;*.wav");
		assertThat(reader.completeFormatName()).isEqualTo("Audio File (*.mp3;*.ogg;*.wav)");
	}

	@Test
	public void whenInvokedWithMP3File_canHandle_shouldReturnTrue() throws Exception {
		// setup
		final AudioFileReader reader = new AudioFileReader();
		// run + assert
		assertThat(reader.canHandle(new File("test.mp3"))).isTrue();
	}

	@Test
	public void whenInvokedWithOGGFile_canHandle_shouldReturnTrue() throws Exception {
		// setup
		final AudioFileReader reader = new AudioFileReader();
		// run + assert
		assertThat(reader.canHandle(new File("test.ogg"))).isTrue();
	}

	@Test
	public void whenInvokedWithWAVFile_canHandle_shouldReturnTrue() throws Exception {
		// setup
		final AudioFileReader reader = new AudioFileReader();
		// run + assert
		assertThat(reader.canHandle(new File("test.wav"))).isTrue();
	}

	@Test
	public void whenInvokedWithAnUnwantedFile_canHandle_shouldReturnFalse() throws Exception {
		// setup
		final AudioFileReader reader = new AudioFileReader();
		// run + assert
		assertThat(reader.canHandle(new File("test.xml"))).isFalse();
	}

	@Test
	public void whenInvokedWithMP3File_read_shouldCreateValidPlaylist() throws Exception {
		// setup
		final File testFile = file("track.mp3");
		final AudioFileReader reader = new AudioFileReader();
		// run
		final Playlist playlist = reader.read(testFile);
		// assert
		assertThat(playlist).hasSize(1);
		assertThat(playlist.getCurrentTrack()).hasName("track") //
				.hasLocation(testFile) //
				.hasLength(0);
	}

	@Test
	public void whenInvokedWithWAVFile_read_shouldCreateValidPlaylist() throws Exception {
		// setup
		final File testFile = file("track.wav");
		final AudioFileReader reader = new AudioFileReader();
		// run
		final Playlist playlist = reader.read(testFile);
		// assert
		assertThat(playlist).hasSize(1);
		assertThat(playlist.getCurrentTrack()).hasName("track") //
				.hasLocation(testFile) //
				.hasLength(0);
	}

	@Test
	public void whenInvokedWithOGGFile_read_shouldCreateValidPlaylist() throws Exception {
		// setup
		final File testFile = file("track.ogg");
		final AudioFileReader reader = new AudioFileReader();
		// run
		final Playlist playlist = reader.read(testFile);
		// assert
		assertThat(playlist).hasSize(1);
		assertThat(playlist.getCurrentTrack()).hasName("track") //
				.hasLocation(testFile) //
				.hasLength(0);
	}
	
	private File file(String path) throws Exception {
		return new File(getClass().getResource(path).toURI());
	}
}
