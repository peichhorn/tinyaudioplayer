package de.fips.plugin.tinyaudioplayer.audio;

import static de.fips.plugin.tinyaudioplayer.audio.PlaylistItemTag.playlistItemTag;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.net.URI;

import org.junit.Test;

public class PlaylistItemTest {
	@Test
	public void test_getBitRate() {
		// setup
		final PlaylistItem item1 = new PlaylistItem("Track 1", uriOfTrack(), 220);
		// run + assert
		assertEquals(-1, item1.getBitRate());
	}

	@Test
	public void test_getBitRate_withInfoTag() {
		// setup
		final PlaylistItem item1 = spy(new PlaylistItem("Track 1", uriOfTrack(), 220));
		final PlaylistItemTag tag1 = playlistItemTag().bitRate(192000).build();
		doReturn(tag1).when(item1).getInfoTag();
		// run + assert
		assertEquals(192000, item1.getBitRate());
	}

	@Test
	public void test_getSampleRate() {
		// setup
		final PlaylistItem item1 = new PlaylistItem("Track 1", uriOfTrack(), 220);
		// run + assert
		assertEquals(-1, item1.getSampleRate());
	}

	@Test
	public void test_getSampleRate_withInfoTag() {
		// setup
		final PlaylistItem item1 = spy(new PlaylistItem("Track 1", uriOfTrack(), 220));
		final PlaylistItemTag tag1 = playlistItemTag().samplingRate(44100).build();
		doReturn(tag1).when(item1).getInfoTag();
		// run + assert
		assertEquals(44100, item1.getSampleRate());
	}

	@Test
	public void test_getChannels() {
		// setup
		final PlaylistItem item1 = new PlaylistItem("Track 1", uriOfTrack(), 220);
		// run + assert
		assertEquals(-1, item1.getChannels());
	}

	@Test
	public void test_getChannels_withInfoTag() {
		// setup
		final PlaylistItem item1 = spy(new PlaylistItem("Track 1", uriOfTrack(), 220));
		final PlaylistItemTag tag1 = playlistItemTag().channels(2).build();
		doReturn(tag1).when(item1).getInfoTag();
		// run + assert
		assertEquals(2, item1.getChannels());
	}

	@Test
	public void test_getDisplayableName() {
		// setup
		final PlaylistItem item1 = new PlaylistItem("Track 1", uriOfTrack(), 220);
		final PlaylistItem item2 = new PlaylistItem("Track 2", uriOfTrack(), 0);
		// run + assert
		assertEquals("Track 1 (03:40)", item1.getDisplayableName());
		assertEquals("Track 2", item2.getDisplayableName());
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
		assertEquals("Artist - Title (03:43)", item1.getDisplayableName());
		assertEquals("Title without Artist (04:22)", item2.getDisplayableName());
		assertEquals("Track 3 (03:56)", item3.getDisplayableName());
	}

	@Test
	public void test_getDisplayableLength() {
		// setup
		final PlaylistItem item1 = new PlaylistItem("", uriOfTrack(), 220);
		final PlaylistItem item2 = new PlaylistItem("", uriOfTrack(), 5000);
		final PlaylistItem item3 = new PlaylistItem("", uriOfTrack(), -1);
		// run + assert
		assertEquals("03:40", item1.getDisplayableLength());
		assertEquals("1:23:20", item2.getDisplayableLength());
		assertEquals("unknown", item3.getDisplayableLength());
	}

	private URI uriOfTrack() {
		return new File("track.mp3").toURI();
	}
}
