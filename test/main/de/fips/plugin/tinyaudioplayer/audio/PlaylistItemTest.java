package de.fips.plugin.tinyaudioplayer.audio;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URI;

import org.junit.Test;


public class PlaylistItemTest {

	@Test
	public void testGetDisplayableName() {
		// setup
		final PlaylistItem item1 = new PlaylistItem("Track", uriOfTrack(), 220);
		// run + assert
		assertEquals("Track", item1.getDisplayableName());
	}

	@Test
	public void testGetDisplayableLength() {
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
