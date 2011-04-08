package de.fips.plugin.tinyaudioplayer.view;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.eclipse.jface.viewers.Viewer;
import org.junit.Test;
import org.mockito.InOrder;

import de.fips.plugin.tinyaudioplayer.audio.IPlaylistListener;
import de.fips.plugin.tinyaudioplayer.audio.Playlist;
import de.fips.plugin.tinyaudioplayer.audio.PlaylistItem;

public class PlaylistContentProviderTest {

	@Test
	public void whenInvoked_inputChanged_shouldUpdatePlaylistListener() throws Exception {
		// setup
		final PlaylistContentProvider contentProvider = new PlaylistContentProvider();
		final Playlist oldPlaylist = mock(Playlist.class);
		final Playlist newPlaylist = mock(Playlist.class);
		final InOrder inorder = inOrder(oldPlaylist, newPlaylist);
		// run
		contentProvider.inputChanged(mock(Viewer.class), oldPlaylist, newPlaylist);
		// assert
		inorder.verify(oldPlaylist).removePlaylistListener(any(IPlaylistListener.class));
		inorder.verify(newPlaylist).addPlaylistListener(any(IPlaylistListener.class));
	}

	@Test
	public void whenInvoked_getElements_shouldCallToArrayOnPlaylist() throws Exception {
		// setup
		final PlaylistContentProvider contentProvider = new PlaylistContentProvider();
		final Playlist playlist = mock(Playlist.class);
		// run
		contentProvider.getElements(playlist);
		// assert
		verify(playlist).toArray();
	}

	@Test
	public void whenInvoked_dispose_shouldRemovePlaylistListener() throws Exception {
		// setup
		final PlaylistContentProvider contentProvider = new PlaylistContentProvider();
		final Playlist playlist = mock(Playlist.class);
		contentProvider.inputChanged(mock(Viewer.class), mock(Playlist.class), playlist);
		verify(playlist, never()).removePlaylistListener(any(IPlaylistListener.class));
		// run
		contentProvider.dispose();
		// assert
		verify(playlist).removePlaylistListener(eq(contentProvider));
	}

	@Test
	public void whenInvoked_trackEnqueued_shouldRefreshViewer() throws Exception {
		// setup
		final PlaylistContentProvider contentProvider = new PlaylistContentProvider();
		final Viewer viewer = mock(Viewer.class);
		contentProvider.inputChanged(viewer, mock(Playlist.class), mock(Playlist.class));
		// run
		contentProvider.trackEnqueued(mock(PlaylistItem.class));
		// assert
		verify(viewer).refresh();
	}

	@Test
	public void whenInvoked_trackChanged_shouldRefreshViewer() throws Exception {
		// setup
		final PlaylistContentProvider contentProvider = new PlaylistContentProvider();
		final Viewer viewer = mock(Viewer.class);
		contentProvider.inputChanged(viewer, mock(Playlist.class), mock(Playlist.class));
		// run
		contentProvider.trackChanged(mock(PlaylistItem.class));
		// assert
		verify(viewer).refresh();
	}

	@Test
	public void whenInvoked_trackRemoved_shouldRefreshViewer() throws Exception {
		// setup
		final PlaylistContentProvider contentProvider = new PlaylistContentProvider();
		final Viewer viewer = mock(Viewer.class);
		contentProvider.inputChanged(viewer, mock(Playlist.class), mock(Playlist.class));
		// run
		contentProvider.trackRemoved(mock(PlaylistItem.class));
		// assert
		verify(viewer).refresh();
	}

	@Test
	public void whenInvoked_playlistCleared_shouldRefreshViewer() throws Exception {
		// setup
		final PlaylistContentProvider contentProvider = new PlaylistContentProvider();
		final Viewer viewer = mock(Viewer.class);
		contentProvider.inputChanged(viewer, mock(Playlist.class), mock(Playlist.class));
		// run
		contentProvider.playlistCleared();
		// assert
		verify(viewer).refresh();
	}
}
