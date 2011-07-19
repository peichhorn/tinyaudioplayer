package de.fips.plugin.tinyaudioplayer.view.playlist;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.junit.Test;
import org.mockito.InOrder;

import de.fips.plugin.tinyaudioplayer.TinyAudioPlayer;
import de.fips.plugin.tinyaudioplayer.audio.Playlist;
import de.fips.plugin.tinyaudioplayer.audio.PlaylistItem;
import de.fips.plugin.tinyaudioplayer.view.playlist.PlaylistDoubleClickListener;

public class PlaylistDoubleClickListenerTest {

	@Test
	public void whenInvoked_doubleClick_shouldPlaySelectedTrack() throws Exception {
		// setup
		final Playlist playlist = mock(Playlist.class);
		doReturn(false).when(playlist).isEmpty();
		final TinyAudioPlayer player = mock(TinyAudioPlayer.class);
		doReturn(playlist).when(player).getPlaylist();
		final Viewer viewer = mock(Viewer.class);
		final PlaylistItem newTrack = mock(PlaylistItem.class);
		final DoubleClickEvent event = new DoubleClickEvent(viewer, new StructuredSelection(newTrack));
		final PlaylistDoubleClickListener listener = new PlaylistDoubleClickListener(player);
		final InOrder inorder = inOrder(playlist, player, viewer);
		// run
		listener.doubleClick(event);
		// assert
		inorder.verify(playlist).setCurrentTrack(eq(newTrack));
		inorder.verify(player).play();
		inorder.verify(viewer).refresh();
	}
}
