package de.fips.plugin.tinyaudioplayer;

import static org.mockito.Mockito.*;

import org.junit.Test;
import org.mockito.InOrder;

import de.fips.plugin.tinyaudioplayer.audio.Playlist;
import de.fips.plugin.tinyaudioplayer.audio.PlaylistAudioPlayer;

public class TinyAudioPlayerTest {
	@Test
	public void whenInvoked_enqueue_shouldAddTracksToPlaylist() throws Exception {
		// setup
		final Playlist newPlaylist = mock(Playlist.class);
		final Playlist internalPlayersPlaylist = mock(Playlist.class);
		final PlaylistAudioPlayer internalPlayer = mock(PlaylistAudioPlayer.class);
		final PlaylistIOHandler playlistIOHandler = mock(PlaylistIOHandler.class);
		final TinyAudioPlayer player = new TinyAudioPlayer(internalPlayer, playlistIOHandler);
		doReturn(internalPlayersPlaylist).when(internalPlayer).getPlaylist();
		doReturn(newPlaylist).when(playlistIOHandler).loadNewPlaylist();
		// run
		player.enqueue();
		// assert
		verify(internalPlayersPlaylist).add(newPlaylist);
	}

	@Test
	public void whenInvoked_eject_shouldLoadAndPlayNewPlaylist() throws Exception {
		// setup
		final Playlist internalPlayersPlaylist = mock(Playlist.class);
		final Playlist newPlaylist = mock(Playlist.class);
		final PlaylistAudioPlayer internalPlayer = mock(PlaylistAudioPlayer.class);
		final PlaylistIOHandler playlistIOHandler = mock(PlaylistIOHandler.class);
		final TinyAudioPlayer player = new TinyAudioPlayer(internalPlayer, playlistIOHandler);
		doReturn(internalPlayersPlaylist).when(internalPlayer).getPlaylist();
		doReturn(newPlaylist).when(playlistIOHandler).loadNewPlaylist();
		// run
		player.eject();
		// assert
		InOrder inOrder = inOrder(internalPlayersPlaylist, internalPlayer);
		inOrder.verify(internalPlayersPlaylist).clear();
		inOrder.verify(internalPlayersPlaylist).add(newPlaylist);
		inOrder.verify(internalPlayer).play();
	}

	@Test
	public void whenInvoked_export_shouldSavePlaylist() throws Exception {
		// setup
		final Playlist internalPlayersPlaylist = mock(Playlist.class);
		final PlaylistAudioPlayer internalPlayer = mock(PlaylistAudioPlayer.class);
		final PlaylistIOHandler playlistIOHandler = mock(PlaylistIOHandler.class);
		final TinyAudioPlayer player = new TinyAudioPlayer(internalPlayer, playlistIOHandler);
		doReturn(internalPlayersPlaylist).when(internalPlayer).getPlaylist();
		doNothing().when(playlistIOHandler).savePlaylist(any(Playlist.class));
		// run
		player.export();
		// assert
		verify(playlistIOHandler).savePlaylist(internalPlayersPlaylist);
	}

	@Test
	public void whenInvoked_removeSelected_shouldCallPlaylistRemoveSelected() throws Exception {
		// setup
		final Playlist internalPlayersPlaylist = mock(Playlist.class);
		final Playlist newPlaylist = mock(Playlist.class);
		final PlaylistAudioPlayer internalPlayer = mock(PlaylistAudioPlayer.class);
		final PlaylistIOHandler playlistIOHandler = mock(PlaylistIOHandler.class);
		final TinyAudioPlayer player = new TinyAudioPlayer(internalPlayer, playlistIOHandler);
		doReturn(internalPlayersPlaylist).when(internalPlayer).getPlaylist();
		doReturn(newPlaylist).when(playlistIOHandler).loadNewPlaylist();
		// run
		player.removeSelected();
		// assert
		verify(internalPlayersPlaylist).removeSelected();
	}

	@Test
	public void whenInvoked_removeDuplicates_shouldCallPlaylistRemoveDuplicates() throws Exception {
		// setup
		final Playlist internalPlayersPlaylist = mock(Playlist.class);
		final PlaylistAudioPlayer internalPlayer = mock(PlaylistAudioPlayer.class);
		final PlaylistIOHandler playlistIOHandler = mock(PlaylistIOHandler.class);
		final TinyAudioPlayer player = new TinyAudioPlayer(internalPlayer, playlistIOHandler);
		doReturn(internalPlayersPlaylist).when(internalPlayer).getPlaylist();
		doNothing().when(playlistIOHandler).savePlaylist(any(Playlist.class));
		// run
		player.removeDuplicates();
		// assert
		verify(internalPlayersPlaylist).removeDuplicates();
	}
}
