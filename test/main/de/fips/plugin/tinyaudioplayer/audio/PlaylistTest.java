package de.fips.plugin.tinyaudioplayer.audio;

import static de.fips.plugin.tinyaudioplayer.assertions.Assertions.assertThat;
import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

public class PlaylistTest {

	@Test
	public void test_add_nullsafe() {
		// run + assert
		new Playlist().add((PlaylistItem) null);
		new Playlist().add((List<PlaylistItem>) null);
		assertThat(new Playlist().add((Playlist) null)).isEqualTo(0);
		assertThat(new Playlist().add((Playlist) null, false)).isEqualTo(0);
	}
	
	@Test
	public void test_add() {
		// setup
		final Playlist playlist = createPlaylistWithSize(2);
		final Playlist otherPlaylist = createPlaylistWithSize(2);
		final PlaylistItem singleItem = createPlaylistItem(4);
		// run
		playlist.add(singleItem);
		final int itemsFromOtherPlaylist = playlist.add(otherPlaylist);
		// assert
		assertThat(itemsFromOtherPlaylist).isEqualTo(2);
		assertThat(playlist).hasSize(5);
		assertThat(playlist.getCurrentTrack()).hasName("Track 01") //
				.hasLocation(new File("01 - Track 01.mp3")) //
				.hasLength(110);
		assertThat(playlist.getNextTrack()).hasName("Track 02") //
				.hasLocation(new File("02 - Track 02.mp3")) //
				.hasLength(220);
		assertThat(playlist.getNextTrack()).hasName("Track 04") //
				.hasLocation(new File("04 - Track 04.mp3")) //
				.hasLength(440);
		assertThat(playlist.getNextTrack()).hasName("Track 01") //
				.hasLocation(new File("01 - Track 01.mp3")) //
				.hasLength(110);
		assertThat(playlist.getNextTrack()).hasName("Track 02") //
				.hasLocation(new File("02 - Track 02.mp3")) //
				.hasLength(220);
	}

	@Test
	public void test_add_ignoreDuplicates() {
		// setup
		final Playlist playlist = createPlaylistWithSize(2);
		final Playlist otherPlaylist = createPlaylistWithSize(3);
		final List<PlaylistItem> list = asList(createPlaylistItem(4), createPlaylistItem(5));
		// run
		playlist.add(list);
		final int itemsFromOtherPlaylist = playlist.add(otherPlaylist, false);
		// assert
		assertThat(itemsFromOtherPlaylist).isEqualTo(1);
		assertThat(playlist).hasSize(5);
		assertThat(playlist.getCurrentTrack()).hasName("Track 01") //
				.hasLocation(new File("01 - Track 01.mp3")) //
				.hasLength(110);
		assertThat(playlist.getNextTrack()).hasName("Track 02") //
				.hasLocation(new File("02 - Track 02.mp3")) //
				.hasLength(220);
		assertThat(playlist.getNextTrack()).hasName("Track 04") //
				.hasLocation(new File("04 - Track 04.mp3")) //
				.hasLength(440);
		assertThat(playlist.getNextTrack()).hasName("Track 05") //
				.hasLocation(new File("05 - Track 05.mp3")) //
				.hasLength(550);
		assertThat(playlist.getNextTrack()).hasName("Track 03") //
				.hasLocation(new File("03 - Track 03.mp3")) //
				.hasLength(330);
	}

	@Test
	public void test_removeCurrent() {
		// setup
		final Playlist playlist = createPlaylistWithSize(3);
		playlist.next();
		// run
		playlist.removeCurrent();
		// assert
		assertThat(playlist).hasSize(2);
		assertThat(playlist.getCurrentTrack()).hasName("Track 01") //
				.hasLocation(new File("01 - Track 01.mp3")) //
				.hasLength(110);
		assertThat(playlist.getNextTrack()).hasName("Track 03") //
		.hasLocation(new File("03 - Track 03.mp3")) //
		.hasLength(330);
	}

	@Test
	public void test_removeDuplicates() {
		// setup
		final Playlist playlist = createPlaylistWithSize(2);
		playlist.add(createPlaylistWithSize(2));
		playlist.add(createPlaylistWithSize(3));
		// run
		final int duplicatesRemoved = playlist.removeDuplicates();
		// assert
		assertThat(duplicatesRemoved).isEqualTo(4);
		assertThat(playlist).hasSize(3);
		assertThat(playlist.getCurrentTrack()).hasName("Track 01") //
				.hasLocation(new File("01 - Track 01.mp3")) //
				.hasLength(110);
		assertThat(playlist.getNextTrack()).hasName("Track 02") //
				.hasLocation(new File("02 - Track 02.mp3")) //
				.hasLength(220);
		assertThat(playlist.getNextTrack()).hasName("Track 03") //
				.hasLocation(new File("03 - Track 03.mp3")) //
				.hasLength(330);
	}

	@Test
	public void test_removeDuplicates_whenEmpty() {
		// run
		final int duplicatesRemoved = new Playlist().removeDuplicates();
		// assert
		assertThat(duplicatesRemoved).isEqualTo(0);
	}

	@Test
	public void test_clear() {
		// setup
		final Playlist playlist = createPlaylistWithSize(4);
		// run
		playlist.clear();
		// assert
		assertThat(playlist).hasSize(0);
	}

	@Test
	public void test_previous() {
		// setup
		final Playlist playlist = createPlaylistWithSize(2);
		final Iterator<PlaylistItem> iter = playlist.iterator();
		playlist.next();
		// run
		playlist.previous();
		// assert
		assertThat(playlist.getCurrentTrack()).isEqualTo(iter.next());
	}

	@Test
	public void test_next() {
		// setup
		final Playlist playlist = createPlaylistWithSize(2);
		final Iterator<PlaylistItem> iter = playlist.iterator();
		iter.next();
		// run
		playlist.next();
		// assert
		assertThat(playlist.getCurrentTrack()).isEqualTo(iter.next());
	}

	@Test
	public void test_clone() {
		// setup
		final Playlist playlist = createPlaylistWithSize(2);
		// run
		final Playlist clone = playlist.clone();
		// assert
		assertThat(playlist == clone).isFalse();
		assertThat(playlist.toString()).isEqualTo(playlist.toString());
	}

	@Test
	public void test_clone_resetsCurrentTrack() {
		// setup
		final Playlist playlist = createPlaylistWithSize(2);
		playlist.next();
		// run
		final Playlist clone = playlist.clone();
		// assert
		assertThat(playlist.getCurrentTrack()).isNotEqualTo(clone.getCurrentTrack());
		assertThat(clone.getCurrentTrack()).isEqualTo(clone.iterator().next());
	}

	private Playlist createPlaylistWithSize(final int size) {
		final Playlist playlist = new Playlist();
		for (int i = 1; i <= size; i++) {
			playlist.add(createPlaylistItem(i));
		}
		return playlist;
	}

	private PlaylistItem createPlaylistItem(final int i) {
		return new PlaylistItem("Track 0" + i, new File("0" + i + " - Track 0" + i + ".mp3").toURI(), i * 110L);
	}
}
