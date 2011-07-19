package de.fips.plugin.tinyaudioplayer.view.playlist;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;
import org.junit.Test;

import de.fips.plugin.tinyaudioplayer.TinyAudioPlayer;
import de.fips.plugin.tinyaudioplayer.view.playlist.PlaylistKeyListener;

public class PlaylistKeyListenerTest {
	@Test
	public void whenInvoked_selectionChanged_shouldSelectTracksInPlaylist() throws Exception {
		// setup
		final TinyAudioPlayer player = mock(TinyAudioPlayer.class);
		final Event event = new Event();
		event.widget = mock(Widget.class);
		event.keyCode = SWT.DEL;
		final KeyEvent keyEvent = new KeyEvent(event);
		final PlaylistKeyListener listener = new PlaylistKeyListener(player);
		// run
		listener.keyPressed(keyEvent);
		// assert
		verify(player).removeSelected();
	}
}
