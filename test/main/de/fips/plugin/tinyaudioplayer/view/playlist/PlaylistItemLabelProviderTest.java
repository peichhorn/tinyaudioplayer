package de.fips.plugin.tinyaudioplayer.view.playlist;

import static de.fips.plugin.tinyaudioplayer.audio.PlaylistItemTag.playlistItemTag;
import static de.fips.plugin.tinyaudioplayer.view.playlist.PlaylistItemLabelProvider.GREY;
import static de.fips.plugin.tinyaudioplayer.view.playlist.PlaylistItemLabelProvider.WHITE;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;

import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Color;
import org.junit.Test;

import de.fips.plugin.tinyaudioplayer.TinyAudioPlayer;
import de.fips.plugin.tinyaudioplayer.audio.Playlist;
import de.fips.plugin.tinyaudioplayer.audio.PlaylistItem;
import de.fips.plugin.tinyaudioplayer.audio.PlaylistItemTag;
import de.fips.plugin.tinyaudioplayer.view.playlist.PlaylistItemLabelProvider;

public class PlaylistItemLabelProviderTest {
	@Test
	public void whenInvoked_update_shouldUseDisplayNameAsText() throws Exception {
		// setup
		final TinyAudioPlayer player = mock(TinyAudioPlayer.class);
		doReturn(mock(Playlist.class)).when(player).getPlaylist();
		final PlaylistItemLabelProvider labelProvider = new PlaylistItemLabelProvider(player);
		final PlaylistItem item = mock(PlaylistItem.class);
		doReturn("Text").when(item).getDisplayableName();
		final ViewerCell cell = mock(ViewerCell.class);
		doReturn(item).when(cell).getElement();
		// run
		labelProvider.update(cell);
		// assert
		verify(cell).setText("Text");
	}

	@Test
	public void whenInvokedWithCellOfCurrentTrack_update_shouldUseGreyBackground() throws Exception {
		// setup
		final TinyAudioPlayer player = mock(TinyAudioPlayer.class);
		final PlaylistItem item = mock(PlaylistItem.class);
		doReturn("Text").when(item).getDisplayableName();
		final Playlist playlist = mock(Playlist.class);
		doReturn(false).when(playlist).isEmpty();
		doReturn(item).when(playlist).getCurrentTrack();
		doReturn(playlist).when(player).getPlaylist();
		final PlaylistItemLabelProvider labelProvider = new PlaylistItemLabelProvider(player);
		final ViewerCell cell = mock(ViewerCell.class);
		doReturn(item).when(cell).getElement();
		// run
		labelProvider.update(cell);
		// assert
		verify(cell).setBackground(eq(new Color(null, GREY)));
	}

	@Test
	public void whenInvokedNormalCell_update_shouldUseWhiteBackground() throws Exception {
		// setup
		final TinyAudioPlayer player = mock(TinyAudioPlayer.class);
		final PlaylistItem item = mock(PlaylistItem.class);
		doReturn("Text").when(item).getDisplayableName();
		final Playlist playlist = mock(Playlist.class);
		doReturn(false).when(playlist).isEmpty();
		doReturn(mock(PlaylistItem.class)).when(playlist).getCurrentTrack();
		doReturn(playlist).when(player).getPlaylist();
		final PlaylistItemLabelProvider labelProvider = new PlaylistItemLabelProvider(player);
		final ViewerCell cell = mock(ViewerCell.class);
		doReturn(item).when(cell).getElement();
		// run
		labelProvider.update(cell);
		// assert
		verify(cell).setBackground(eq(new Color(null, WHITE)));
	}

	@Test
	public void test_getToolTipText() throws Exception {
		// setup
		final TinyAudioPlayer player = mock(TinyAudioPlayer.class);
		final PlaylistItemTag tag = playlistItemTag() //
				.channels(2) //
				.samplingRate(44100) //
				.bitRate(192000) //
				.album("Album 01").genre("Audiobook").year("2002").build();
		final PlaylistItem item = spy(new PlaylistItem("Track 01", new File("01 - Track 01.mp3").toURI(), 220));
		doReturn(tag).when(item).getInfoTag();
		final PlaylistItemLabelProvider labelProvider = new PlaylistItemLabelProvider(player);
		// run
		final String tooltip = labelProvider.getToolTipText(item);
		// assert
		assertThat(tooltip).isEqualTo("album: Album 01\n" + //
				"genre: Audiobook\n" + //
				"year: 2002\n" + //
				"channels: stereo\n" + //
				"sampling rate: 44100 Hz\n" + //
				"bitrate: 192000 bit/s");
	}

	@Test
	public void test_getToolTipText_unknown() throws Exception {
		// setup
		final TinyAudioPlayer player = mock(TinyAudioPlayer.class);
		final PlaylistItem item = new PlaylistItem("Track 01", new File("01 - Track 01.mp3").toURI(), -1);
		final PlaylistItemLabelProvider labelProvider = new PlaylistItemLabelProvider(player);
		// run
		final String tooltip = labelProvider.getToolTipText(item);
		// assert
		assertThat(tooltip).isEqualTo("channels: unknown\n" + //
				"sampling rate: unknown\n" + //
				"bitrate: unknown");
	}
}
