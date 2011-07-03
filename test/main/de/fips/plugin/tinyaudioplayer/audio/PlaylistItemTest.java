package de.fips.plugin.tinyaudioplayer.audio;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class PlaylistItemTest {

	@Test
	public void testGetDisplayableName() {
		// setup
		final PlaylistItem item1 = new PlaylistItem("Track", "track.mp3", 220);
		// run + assert
		assertEquals("Track", item1.getDisplayableName());
	}

	@Test
		public void testGetDisplayableLength() {
			// setup
			final PlaylistItem item1 = new PlaylistItem("", "", 220);
			final PlaylistItem item2 = new PlaylistItem("", "", 5000);
			final PlaylistItem item3 = new PlaylistItem("", "", -1);
			// run + assert
			assertEquals("03:40", item1.getDisplayableLength());
			assertEquals("1:23:20", item2.getDisplayableLength());
			assertEquals("unknown", item3.getDisplayableLength());
		}
}
