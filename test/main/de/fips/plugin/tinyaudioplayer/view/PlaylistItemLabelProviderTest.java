package de.fips.plugin.tinyaudioplayer.view;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Color;
import org.junit.Test;

import de.fips.plugin.tinyaudioplayer.TinyAudioPlayer;
import de.fips.plugin.tinyaudioplayer.audio.Playlist;
import de.fips.plugin.tinyaudioplayer.audio.PlaylistItem;

public class PlaylistItemLabelProviderTest {
	@Test
	public void whenInvoked_update_shouldUseFormattedDisplayNameAsText() throws Exception {
		// setup
		final TinyAudioPlayer player = mock(TinyAudioPlayer.class);
		doReturn(mock(Playlist.class)).when(player).getPlaylist();
		final PlaylistItemLabelProvider labelProvider = new PlaylistItemLabelProvider(player);
		final PlaylistItem item = mock(PlaylistItem.class);
		doReturn("Text").when(item).getFormattedDisplayName();
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
		doReturn("Text").when(item).getFormattedDisplayName();
		final Playlist playlist = mock(Playlist.class);
		doReturn(true).when(playlist).hasTracks();
		doReturn(item).when(playlist).getCurrentTrack();
		doReturn(playlist).when(player).getPlaylist();
		final PlaylistItemLabelProvider labelProvider = new PlaylistItemLabelProvider(player);
		final ViewerCell cell = mock(ViewerCell.class);
		doReturn(item).when(cell).getElement();
		// run
		labelProvider.update(cell);
		// assert
		verify(cell).setBackground(eq(new Color(null, 225, 225, 225)));
	}

	@Test
	public void whenInvokedNormalCell_update_shouldUseWhiteBackground() throws Exception {
		// setup
		final TinyAudioPlayer player = mock(TinyAudioPlayer.class);
		final PlaylistItem item = mock(PlaylistItem.class);
		doReturn("Text").when(item).getFormattedDisplayName();
		final Playlist playlist = mock(Playlist.class);
		doReturn(true).when(playlist).hasTracks();
		doReturn(mock(PlaylistItem.class)).when(playlist).getCurrentTrack();
		doReturn(playlist).when(player).getPlaylist();
		final PlaylistItemLabelProvider labelProvider = new PlaylistItemLabelProvider(player);
		final ViewerCell cell = mock(ViewerCell.class);
		doReturn(item).when(cell).getElement();
		// run
		labelProvider.update(cell);
		// assert
		verify(cell).setBackground(eq(new Color(null, 255, 255, 255)));
	}
}
