package de.fips.plugin.tinyaudioplayer.view;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.junit.Test;

import de.fips.plugin.tinyaudioplayer.TinyAudioPlayer;
import de.fips.plugin.tinyaudioplayer.audio.Playlist;
import de.fips.plugin.tinyaudioplayer.audio.PlaylistItem;


public class PlaylistViewSelectionChangedListenerTest {
	@Test
	public void whenInvoked_selectionChanged_shouldSelectTracksInPlaylist() throws Exception {
		// setup
		final Playlist playlist = mock(Playlist.class);
		doReturn(false).when(playlist).isEmpty();
		final TinyAudioPlayer player = mock(TinyAudioPlayer.class);
		doReturn(playlist).when(player).getPlaylist();
		final Viewer viewer = mock(Viewer.class);
		final List<PlaylistItem> selectedTracks = asList(mock(PlaylistItem.class), mock(PlaylistItem.class));
		final SelectionChangedEvent event = new SelectionChangedEvent(viewer, new StructuredSelection(selectedTracks));
		final PlaylistViewSelectionChangedListener listener = new PlaylistViewSelectionChangedListener(player);
		// run
		listener.selectionChanged(event);
		// assert
		verify(playlist).selectTracks(eq(selectedTracks));
	}
}
