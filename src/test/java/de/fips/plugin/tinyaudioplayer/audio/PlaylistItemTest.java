package de.fips.plugin.tinyaudioplayer.audio;

import static de.fips.plugin.tinyaudioplayer.audio.PlaylistItemTag.playlistItemTag;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.io.File;
import java.net.URI;

import org.junit.Test;

public class PlaylistItemTest {
	@Test
	public void test_getDisplayableBitRate() {
		// setup
		final PlaylistItem item1 = new PlaylistItem("Track 1", uriOfTrack(), 220);
		// run + assert
		assertThat(item1.getDisplayableBitRate()).isEqualTo("unknown");
	}

	@Test
	public void test_getDisplayableBitRate_withInfoTag() {
		// setup
		final PlaylistItem item1 = spy(new PlaylistItem("Track 1", uriOfTrack(), 220));
		final PlaylistItemTag tag1 = playlistItemTag().bitRate(192000).build();
		doReturn(tag1).when(item1).getInfoTag();
		// run + assert
		assertThat(item1.getDisplayableBitRate()).isEqualTo("192000 bit/s");
	}

	@Test
	public void test_getDisplayableSampleRate() {
		// setup
		final PlaylistItem item1 = new PlaylistItem("Track 1", uriOfTrack(), 220);
		// run + assert
		assertThat(item1.getDisplayableSampleRate()).isEqualTo("unknown");
	}

	@Test
	public void test_getDisplayableSampleRate_withInfoTag() {
		// setup
		final PlaylistItem item1 = spy(new PlaylistItem("Track 1", uriOfTrack(), 220));
		final PlaylistItemTag tag1 = playlistItemTag().samplingRate(44100).build();
		doReturn(tag1).when(item1).getInfoTag();
		// run + assert
		assertThat(item1.getDisplayableSampleRate()).isEqualTo("44100 Hz");
	}

	@Test
	public void test_getDisplayableChannels() {
		// setup
		final PlaylistItem item1 = new PlaylistItem("Track 1", uriOfTrack(), 220);
		// run + assert
		assertThat(item1.getDisplayableChannels()).isEqualTo("unknown");
	}

	@Test
	public void test_getDisplayableChannels_withInfoTag() {
		// setup
		final PlaylistItem item1 = spy(new PlaylistItem("Track 1", uriOfTrack(), 220));
		final PlaylistItemTag tag1 = playlistItemTag().channels(2).build();
		doReturn(tag1).when(item1).getInfoTag();
		// run + assert
		assertThat(item1.getDisplayableChannels()).isEqualTo("stereo");
	}

	@Test
	public void test_getDisplayableName() {
		// setup
		final PlaylistItem item1 = new PlaylistItem("Track 1", uriOfTrack(), 220);
		final PlaylistItem item2 = new PlaylistItem("Track 2", uriOfTrack(), 0);
		// run + assert
		assertThat(item1.getDisplayableName()).isEqualTo("Track 1 (03:40)");
		assertThat(item2.getDisplayableName()).isEqualTo("Track 2");
	}

	@Test
	public void test_getDisplayableName_withInfoTag() {
		// setup
		final PlaylistItem item1 = spy(new PlaylistItem("Track 1", uriOfTrack(), 220));
		final PlaylistItemTag tag1 = playlistItemTag().artist("Artist").title("Title").playTime(223).build();
		final PlaylistItem item2 = spy(new PlaylistItem("Track 2", uriOfTrack(), 262));
		final PlaylistItemTag tag2 = playlistItemTag().title("Title without Artist").build();
		final PlaylistItem item3 = spy(new PlaylistItem("Track 3", uriOfTrack(), 236));
		final PlaylistItemTag tag3 = playlistItemTag().build();
		doReturn(tag1).when(item1).getInfoTag();
		doReturn(tag2).when(item2).getInfoTag();
		doReturn(tag3).when(item3).getInfoTag();
		// run + assert
		assertThat(item1.getDisplayableName()).isEqualTo("Artist - Title (03:43)");
		assertThat(item2.getDisplayableName()).isEqualTo("Title without Artist (04:22)");
		assertThat(item3.getDisplayableName()).isEqualTo("Track 3 (03:56)");
	}

	@Test
	public void test_getDisplayableLength() {
		// setup
		final PlaylistItem item1 = new PlaylistItem("", uriOfTrack(), 220);
		final PlaylistItem item2 = new PlaylistItem("", uriOfTrack(), 5000);
		final PlaylistItem item3 = new PlaylistItem("", uriOfTrack(), -1);
		// run + assert
		assertThat(item1.getDisplayableLength()).isEqualTo("03:40");
		assertThat(item2.getDisplayableLength()).isEqualTo("1:23:20");
		assertThat(item3.getDisplayableLength()).isEqualTo("unknown");
	}

	private URI uriOfTrack() {
		return new File("track.mp3").toURI();
	}
}
