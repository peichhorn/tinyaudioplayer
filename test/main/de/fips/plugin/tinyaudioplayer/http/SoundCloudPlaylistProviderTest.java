package de.fips.plugin.tinyaudioplayer.http;

import static de.fips.plugin.tinyaudioplayer.assertions.Assertions.assertThat;
import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.fips.plugin.tinyaudioplayer.audio.Playlist;

public class SoundCloudPlaylistProviderTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void test_getPlaylistFor() throws Exception {
		// setup
		final SoundCloudPlaylistProvider provider = spy(new SoundCloudPlaylistProvider());
		doReturn(fileAsString(file("simplified_requestdump"))).when(provider).getSoundCloudPageFor(any(URI.class));
		// run
		final Playlist playlist = provider.getPlaylistFor("chromeo");
		// assert
		assertThat(playlist).hasSize(4);
		assertThat(playlist.getCurrentTrack()).hasName("Night By Night \"&\"") //
				.hasLocation(new URI("http://media.soundcloud.com/stream/LHBNmmjRYBmq")) //
				.hasLength(225);
		assertThat(playlist.getNextTrack()).hasName("Needy Girl") //
				.hasLocation(new URI("http://media.soundcloud.com/stream/AoIr6ZoEySy4")) //
				.hasLength(257);
		assertThat(playlist.getNextTrack()).hasName("Hot Mess featuring Elly Jacson (Duck Sauce Remix)") //
				.hasLocation(new URI("http://media.soundcloud.com/stream/phq1RrkR47eE")) //
				.hasLength(327);
		assertThat(playlist.getNextTrack()).hasName("Don't Turn The Lights On (Aeroplane Remix)") //
				.hasLocation(new URI("http://media.soundcloud.com/stream/rBI1kUevLhn3")) //
				.hasLength(333);
	}

	@Test
	public void test_getNumberOfPagesFor() throws Exception {
		// setup
		final SoundCloudPlaylistProvider provider = spy(new SoundCloudPlaylistProvider());
		doReturn(fileAsString(file("simplified_requestdump"))).when(provider).getSoundCloudPageFor(any(URI.class));
		// run
		final int numberOfPages = provider.getNumberOfPagesFor("chromeo");
		// assert
		assertThat(numberOfPages).isEqualTo(37);
	}

	@Test
	public void test_getSearchQueryURIFor() throws Exception {
		// setup
		final SoundCloudPlaylistProvider provider = new SoundCloudPlaylistProvider();
		// run
		final URI uri = provider.getSearchQueryURIFor("daft punk", 2);
		// assert
		assertThat(uri).isEqualTo(new URI("http://soundcloud.com/search?page=2&q%5Bfulltext%5D=daft+punk"));
	}

	@Test
	public void test_getSearchQueryURIFor_invalid() throws Exception {
		// setup
		final SoundCloudPlaylistProvider provider = new SoundCloudPlaylistProvider();
		// run + assert
		thrown.expect(IllegalArgumentException.class);
		provider.getSearchQueryURIFor("<(''<)", 0); // Oh noes kirby breaks URIs xD 
	}

	@Test
	public void test_getSearchQueryURIFor_null() throws Exception {
		// setup
		final SoundCloudPlaylistProvider provider = new SoundCloudPlaylistProvider();
		// run + assert
		thrown.expect(IllegalArgumentException.class);
		provider.getSearchQueryURIFor(null, 0);
	}

	@Test
	public void test_getBufferTracksAsJSON() throws Exception {
		// setup
		final SoundCloudPlaylistProvider provider = new SoundCloudPlaylistProvider();
		// run
		final List<String> playlist = provider.getBufferTracksAsJSON(fileAsString(file("simplified_requestdump")));
		// assert
		assertThat(playlist).hasSize(5).containsOnly( //
				"{\"duration\":225312,\"title\":\"Night By Night &quot;&amp;&quot;\",\"streamUrl\":\"http://media.soundcloud.com/stream/LHBNmmjRYBmq?stream_token=kV51k\"}", //
				"{\"duration\":257220,\"title\":\"Needy Girl\",\"streamUrl\":\"http://media.soundcloud.com/stream/AoIr6ZoEySy4?stream_token=NDZJ5\"}", //
				"{\"duration\":225312,\"title\":\"Night By Night &quot;&amp;&quot;\",\"streamUrl\":\"http://media.soundcloud.com/stream/LHBNmmjRYBmq?stream_token=kV51k\"}", //
				"{\"duration\":327673,\"title\":\"Hot Mess featuring Elly Jacson (Duck Sauce Remix)\",\"streamUrl\":\"http://media.soundcloud.com/stream/phq1RrkR47eE?stream_token=hcRwo\"}", //
				"{\"duration\":333527,\"title\":\"Don't Turn The Lights On (Aeroplane Remix)\",\"streamUrl\":\"http://media.soundcloud.com/stream/rBI1kUevLhn3?stream_token=JBkk7\"}");
	}

	@Test
	public void test_asPlaylist() throws Exception {
		// setup
		final SoundCloudPlaylistProvider provider = new SoundCloudPlaylistProvider();
		final String json = "{\"title\":\"Artist - Title\",\"streamUrl\":\"http://media.soundcloud.com/stream/EXAMPLE?stream_token=TOKEN\",\"duration\" : 220000}";
		final List<String> bufferTracksAsJSON = asList(json);
		// run
		final Playlist playlist = provider.asPlaylist(bufferTracksAsJSON);
		// assert
		assertThat(playlist).hasSize(1);
		assertThat(playlist.getCurrentTrack()).hasName("Artist - Title") //
				.hasLocation(new URI("http://media.soundcloud.com/stream/EXAMPLE")) //
				.hasLength(220);
	}

	@Test
	public void test_toSearchString() throws Exception {
		// setup
		final SoundCloudPlaylistProvider provider = new SoundCloudPlaylistProvider();
		// run + assert
		assertThat(provider.toSearchString("daft punk")).isEqualTo("daft+punk");
		assertThat(provider.toSearchString("chromeo")).isEqualTo("chromeo");
	}

	private String fileAsString(File file) throws Exception {
		return new SoundCloudPlaylistProvider().readStreamAsString(new FileInputStream(file));
	}

	private File file(String path) throws Exception {
		return new File(getClass().getResource(path).toURI());
	}
}
